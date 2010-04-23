package com.tinkerpop.webling;

import java.io.File;
import java.io.PrintStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.ArrayList;

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
    private static long lastEvaluationTime = System.currentTimeMillis();
    private static List<String> SYS_COMMANDS = new ArrayList<String>();
    private static GremlinEvaluator gremlin = new GremlinEvaluator();
    
    static {
        logger = Logger.getLogger(GremlinWorker.class);
        patternLayout = new PatternLayout("%d [%t] %p %C{1} - %m%n");
        
        // system commands initialization
        SYS_COMMANDS.add("LAST_RUN");
        SYS_COMMANDS.add("CLEAR");
    }

    @SuppressWarnings("unchecked")
    public static void main(String[] args) throws IOException {
        ServerSocket server = null;

        // writting to main log directory
        String logFilename  = System.getProperty("user.dir") + "/../../.." + "/log/worker-" + args[0] + ".log";

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

            // working on system commands
            if(SYS_COMMANDS.contains(line)) {
                logger.info("System command - " + line + " proccessed.");
                evaluateSystemCommand(line, out);
                incoming.close();
                continue;
            }

            // evaluating gremlin statements
            try {
                List result = gremlin.evaluate(line);

                // logging last use time
                lastEvaluationTime = System.currentTimeMillis();

                // return evaluation result to client, maintaining line-breaks
                for( Object resultLine : result) {
                   logger.info(resultLine);
                   out.println(resultLine);
                }
                //out.println(((result.size() == 1) ? result.get(0) : result));

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

    protected static void evaluateSystemCommand(final String cmd, final PrintStream out) {
        if (cmd.equals("LAST_RUN")) {
            out.println(lastEvaluationTime);
        } else if (cmd.equals("CLEAR")) {
            gremlin = new GremlinEvaluator();
            
            // emptying worker directory
            File workerDirectory = new File(System.getProperty("user.dir"));
            Utility.deleteSubContentFor(workerDirectory);
            workerDirectory.mkdir();
        }
    }
}
