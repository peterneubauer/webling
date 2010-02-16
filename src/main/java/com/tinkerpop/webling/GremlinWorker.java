package com.tinkerpop.webling;

import java.io.PrintStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;

import org.apache.log4j.FileAppender;
import org.apache.log4j.Layout;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;

import com.tinkerpop.gremlin.GremlinEvaluator;

/**
 * 
 * @author Pavel A. Yaskevich
 *
 */
public class GremlinWorker {
  
	private static Logger logger;
	private static Layout patternLayout;

	static {
		logger = Logger.getLogger(GremlinWorker.class);
		patternLayout = new PatternLayout("%d [%t] %p %C{1} - %m%n");
	}

	@SuppressWarnings("unchecked")
	public static void main(String[] args) throws IOException {
		ServerSocket server = null;
		String logFilename 	= "../../logs/worker-" + args[0] + ".log";
		
		GremlinEvaluator gremlin = new GremlinEvaluator();
		logger.addAppender(new FileAppender(patternLayout, logFilename));
		
		try {
			server = new ServerSocket(Integer.parseInt(args[0]));
			logger.info("Worker successfully started on " + args[0] + " port.");
		} catch(Exception e) {
			logger.fatal(e);
		}
		
		
		while(true) {
			// Still no native support for this
			gremlin.evaluate("include 'com.tinkerpop.webling.functions.WeblingFunctions'");

			Socket incoming = server.accept();
  
			PrintStream out = new PrintStream(incoming.getOutputStream());
			System.setOut(out);

			BufferedReader in = new BufferedReader(new InputStreamReader(incoming.getInputStream()));

			String line = in.readLine();
			
			try {
				List result = gremlin.evaluate(line);
				out.println(((result.size() == 1) ? result.get(0) : result));
			} catch(NullPointerException e) {
				out.println("gr-statement");
			} catch(Exception e) {
				out.println(e.getMessage());	
				logger.fatal(line + " - " + e.getMessage());
				incoming.close();
				continue;
			}

			if(null != line) logger.info(line + " - evaluated.");
			incoming.close();
		}
	}

}
