package com.pp.arithmo.logging;

public class Logging {
	static {
	      // must set before the Logger
	      // loads logging.properties from the classpath
	      String path = Logging.class
	            .getClassLoader().getResource("logging.properties").getFile();
	      System.setProperty("java.util.logging.config.file", path);

	  }

//	  private static Logger logger = Logger.getLogger(LoadLogPropertiesFile.class.getName());
}
