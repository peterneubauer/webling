package com.tinkerpop.webling;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
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
    private static Stack<Map<String, Object>> idleWorkers;
    private static Map<String, Map<String, Object>> workers;

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
        		startIdleWorker();
        	} else {
        		// if idle workers will be empty we will be allocating
        		// new assigned worker here, by hand
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
    }
    
    private static void startIdleWorker() throws IOException {
    	Map<String, Object> worker = new HashMap<String, Object>();
    	worker.put("port", currentPort);
    	worker.put("process", startWorkerProcess());
    	idleWorkers.push(worker);
		currentPort++;
    }
    
    private static Process startWorkerProcess() throws IOException {
    	ProcessBuilder worker = new ProcessBuilder("/usr/bin/java", 
    			"-cp", "./target/webling-" + WeblingLauncher.VERSION + "-standalone.jar", 
    			"com.tinkerpop.webling.GremlinWorker", currentPort.toString());
    	
        return worker.start();
    }
    
    public static void stopWorker(final String sessionId) {
    	Map<String, Object> info = workers.get(sessionId);
    	((Process) info.get("process")).destroy();
    }

    public static String evaluate(final String sessionId, final String code) {
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
        	client = new Socket("0.0.0.0", ((Integer) info.get("port")).intValue());
        } catch(Exception e) {
        	System.err.println("Trouble: " + e);
        }
        
        String result = null;
        
        try {
        	BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
        	PrintWriter out = new PrintWriter(client.getOutputStream(), true);
        	
        	out.println(code);
        	result = in.readLine();
        	client.close();
        } catch(Exception e) {
        	System.err.println("Communication trouble: " + e);
        }

        return result;
    }

}
