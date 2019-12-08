package eg.edu.alexu.csd.oop.db.cs33;

import java.io.File;

public class DTDDrop {

	private String path;
	
	public DTDDrop(String path) {
		this.path = path ;
	}

	public void Drop () {
		new File(path).delete();
	}
}
