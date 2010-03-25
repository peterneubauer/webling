package com.tinkerpop.webling;

import java.io.File;
import java.net.Socket;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.FileFilter;

import java.util.Set;
import java.util.Map;
import java.util.Iterator;


/**
 *
 * @author Pavel A. Yaskevich
 */
public class GarbageCollector extends Thread {
    
    long updateInterval  = 3000000; // 50 mins
    long maxIdleInterval = 1790000; // 29 mins
    
    GarbageCollector() {
        setDaemon(true);
        start();
    }

    @Override
    public void run() {
    	Utility.logger.info("Garbage collector thread initalized.");
    	
        while(true) {
            try {
                Thread.sleep(updateInterval);
            } catch(InterruptedException e) {}

            File workersDir = new File("./tmp/workers");

            FileFilter fileFilter = new FileFilter() {
                public boolean accept(File file) {
                    return file.isDirectory();
                }
            };

            File[] children = workersDir.listFiles(fileFilter);

            for (int i = 0; i < children.length; i++) {
                Integer portNumber = Integer.parseInt(children[i].getName());
                String result = sendRequest(portNumber.intValue(), "LAST_RUN");
                
                try {
                    long runLastTime = Long.parseLong(result);
                    if ((System.currentTimeMillis() - runLastTime) > maxIdleInterval) {
                        Set<String> sessionIds = GremlinWorkerPool.workers.keySet();
                        Iterator<String> sessionIterator = sessionIds.iterator();

                        while(sessionIterator.hasNext()) {
                            String sessionId = sessionIterator.next();
                            
                            Map<String, Object> worker = GremlinWorkerPool.workers.get(sessionId);
                            
                            if (worker.get("port").equals(portNumber)) {
                                GremlinWorkerPool.workers.remove(sessionId);
                                GremlinWorkerPool.idleWorkers.push(worker);
                                
                                sendRequest(portNumber.intValue(), "CLEAR");
                                Utility.logger.info("worker on " + portNumber + " port moved to idle.");
                                break;
                            }
                        }
                    }
                } catch(Exception e) {
                	Utility.logger.error("Something went wrong while working with " + portNumber + " port");
                }
            }
        }
    }

    private static String sendRequest(final int portNumber, final String command) {
        Socket client = null;
        String result = null;
        
        try {
            client = new Socket("127.0.0.1", portNumber);

            PrintWriter out     = new PrintWriter(client.getOutputStream(), true);
            InputStreamReader r = new InputStreamReader(client.getInputStream());
            BufferedReader in   = new BufferedReader(r);

            out.println(command);
            result = in.readLine();
            
            r.close();
            in.close();
            out.close();
        } catch(Exception e) {
        	Utility.logger.fatal("Could not send data to " + portNumber);
            return null;
        }
        
        Utility.logger.info("Sent " + command + " to " + portNumber);
        return result;
    }
    
}
