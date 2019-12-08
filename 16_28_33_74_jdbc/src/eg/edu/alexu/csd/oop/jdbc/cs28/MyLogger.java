package eg.edu.alexu.csd.oop.jdbc.cs28;

import java.io.File;
import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class MyLogger {
	
	// Singleton Design Pattern
	private static MyLogger instance = null;
	private FileHandler fileHandler;
	public Logger logger;
	
	public static MyLogger getInstance() {
		if (instance == null) {
			instance = new MyLogger();
		}
		return instance;
	}
	
	private MyLogger() {
		
		File file = new File("logger.txt");
		try {
			file.createNewFile();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		try {
			fileHandler = new FileHandler("logger.txt", true);
		} catch (SecurityException | IOException e) {
			e.printStackTrace();
		}
		
		logger = Logger.getLogger("Logger");
		logger.addHandler(fileHandler);
		fileHandler.setFormatter(new SimpleFormatter());
		logger.setLevel(Level.INFO);
	}
	
}
