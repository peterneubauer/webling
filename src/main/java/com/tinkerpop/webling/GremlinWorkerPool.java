package com.tinkerpop.webling;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Stack;

/**
 * 
 * @author Pavel A. Yaskevich
 *
 */
public class GremlinWorkerPool {

    private static int idleWorkersInitial = 1;
    private static Integer currentPort = 2000;
    public  static Stack<Map<String, Object>> idleWorkers;
    public  static Map<String, Map<String, Object>> workers;
    
    static {
    	idleWorkers = new Stack<Map<String, Object>>();
    	workers = new HashMap<String, Map<String, Object>>();
    }
    
    public static void startWorker(final String sessionId) throws Exception {
        Map<String, Object> info = null;

        if ((info = workers.get(sessionId)) == null) {
            // trying to get from idle workers first
            if (!idleWorkers.empty()) {
                workers.put(sessionId, idleWorkers.pop());
                
                // allocating one more worker for future clients
                if (idleWorkers.size() < 2) {
                	new Thread() {
                		public void run() {
                			Utility.logger.info("Running new idle worker on " + currentPort + " port.");
                			try {
                				GremlinWorkerPool.startIdleWorker();
                			} catch(IOException e) {
                				Utility.logger.fatal(e.getMessage());
                			}
                		}
                	}.run();
                }
            } else {
            	info = new HashMap<String, Object>();
            
            	info.put("port", currentPort);
            	info.put("process", startWorkerProcess());
            
            	workers.put(sessionId, info);
            	currentPort++;
            }
        }
    }

    public static void startInitial() throws IOException {
    	for (int i = 0; i < idleWorkersInitial; i++) {
            startIdleWorker();
    	}
        new GarbageCollector();
    }
    
    private static void startIdleWorker() throws IOException {
    	Map<String, Object> worker = new HashMap<String, Object>();
    	
    	worker.put("port", currentPort);
    	worker.put("process", startWorkerProcess());
    	idleWorkers.push(worker);
    	
    	currentPort++;
    }
    
    private static Process startWorkerProcess() throws IOException {
    	String currentDirectory = System.getProperty("user.dir");
    	File workerDirectory = new File(currentDirectory + "/tmp/workers/" + currentPort);
    	
    	if(!workerDirectory.exists()) {
    		workerDirectory.mkdir();
    	} else {
    		Utility.deleteSubContentFor(workerDirectory);
    		workerDirectory.mkdir();
    	}

    	ProcessBuilder worker = new ProcessBuilder("/bin/bash", currentDirectory + "/webling.sh", "start_worker", currentPort.toString());
    	worker.directory(workerDirectory);
    	
    	return worker.start();
    }
    
    public static void stopWorker(final String sessionId) {
    	Map<String, Object> info = workers.get(sessionId);
    	((Process) info.get("process")).destroy();
    }

    public static List<String> evaluate(final String sessionId, final String code) {
        Socket client = null;
        Map<String, Object> info = null;
        
        if ((info = workers.get(sessionId)) == null) {
            try {
            	GremlinWorkerPool.startWorker(sessionId);
            } catch(Exception e) {
            	System.err.println(e);
            }
            info = workers.get(sessionId);
        }
        
        try {
            client = new Socket("127.0.0.1", ((Integer) info.get("port")).intValue());
        } catch(Exception e) {
            System.err.println("Trouble: " + e);
        }
        
        List<String> resultBuffer = new ArrayList<String>();
        String result = null;
        
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
            PrintWriter out = new PrintWriter(client.getOutputStream(), true);
        	
            out.println(code);

            while((result = in.readLine()) != null) {
                resultBuffer.add(result);
            }
            client.close();
        } catch(Exception e) {
            System.err.println("Communication trouble: " + e);
        }

        return resultBuffer;
    }
    
}
