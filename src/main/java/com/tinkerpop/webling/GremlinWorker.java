package com.tinkerpop.webling;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
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
			Socket incoming = server.accept();
			
			BufferedReader in = new BufferedReader(new InputStreamReader(incoming.getInputStream()));
			PrintWriter out = new PrintWriter(incoming.getOutputStream(), true);
			
			String line = in.readLine();
			try {
				List result = gremlin.evaluate(new ByteArrayInputStream(line.getBytes()));
				out.println(((result.size() == 1) ? result.get(0) : result));
			} catch(Exception e) {
				out.println(e);
			}
			
			incoming.close();
		}
	}

}
