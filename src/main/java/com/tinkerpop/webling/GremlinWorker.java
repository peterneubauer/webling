package com.tinkerpop.webling;

import java.io.PrintStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;

import com.tinkerpop.gremlin.GremlinEvaluator;

/**
 * 
 * @author Pavel A. Yaskevich
 *
 */
public class GremlinWorker {
  
	@SuppressWarnings("unchecked")
	public static void main(String[] args) throws IOException {
		ServerSocket server = null;
		
		GremlinEvaluator gremlin = new GremlinEvaluator();
		
		try {
			  server = new ServerSocket(Integer.parseInt(args[0]));
		} catch(Exception e) {
			  System.err.println(e);
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
			}
			
			incoming.close();
		}
	}

}
