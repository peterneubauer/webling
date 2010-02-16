package com.tinkerpop.webling;

import org.apache.log4j.PropertyConfigurator;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.util.thread.QueuedThreadPool;

import com.tinkerpop.webling.servlets.EvaluatorServlet;
import com.tinkerpop.webling.servlets.StaticFilesServlet;
import com.tinkerpop.webling.servlets.VisualizationServlet;

/**
 * @author Pavel A. Yaskevich
 */
public class WeblingLauncher {

	public static String VERSION = "0.1";
	
    static {
        PropertyConfigurator.configure("./src/main/resources/log4j.properties");
    }

    /**
     * Initializes server, binds it to specifield port
     * if no command line arguments given host:port will be 127.0.0.1:8080
     * Adds servlets to handle gremlin evaluation and static files
     * 
     * @param port 	Port number
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        int port = 8080;
        
        // port number
        if (args.length == 1)
            port = Integer.parseInt(args[0]);
        
        // start initial workers
        System.out.println("Waiting for workers to start...");
        GremlinWorkerPool.startInitial();
        
        // create web server
        Server webling = new Server(port);
        
        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        context.setContextPath("/");
        webling.setHandler(context);
        
        context.addServlet(new ServletHolder(new StaticFilesServlet()), "/*");
        context.addServlet(new ServletHolder(new EvaluatorServlet()), "/exec");
        context.addServlet(new ServletHolder(new VisualizationServlet()), "/visualize");
        
        webling.setThreadPool(new QueuedThreadPool(20));
        
        webling.start();
        webling.join();
    }
}
