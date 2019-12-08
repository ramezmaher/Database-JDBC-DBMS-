package eg.edu.alexu.csd.oop.db.cs33;

import java.io.File;

public class XMLDrop {

	private String path;
	
	public XMLDrop (String path) {
		this.path = path;
	}
	
	public void Drop () {
		new File(path).delete();
	}
}
