package com.tinkerpop.webling;

import java.io.File;


import org.apache.log4j.FileAppender;
import org.apache.log4j.Layout;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;

/**
 * @author Pavel A. Yaskevich
 */
public class Utility {
	
	public static Logger logger;
	private static Layout patternLayout;
	
	static {
		logger = Logger.getLogger(Utility.class);
		patternLayout = new PatternLayout("%d [%t] %p %C{1} - %m%n");
	
		try {
			logger.addAppender(new FileAppender(patternLayout, "log/webling-main.log"));
		} catch(Exception e) {}
	}
	
    public static boolean deleteSubContentFor(File path) {
    	if(path.exists()) {
            File[] files = path.listFiles();

            for(int i = 0; i < files.length; i++) {
                if(files[i].isDirectory())
                	deleteSubContentFor(files[i]);
                else
                    files[i].delete();
            }
    	}
    	return path.delete();
    }
}
