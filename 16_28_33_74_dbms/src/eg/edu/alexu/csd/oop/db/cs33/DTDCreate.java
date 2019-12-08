package eg.edu.alexu.csd.oop.db.cs33;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;


public class DTDCreate {

	private String path;
	private Map<String, String> cols = new LinkedHashMap<String , String>();
	
	public DTDCreate(String path, Map<String, String> cols) {
		this.path = path;	
		this.cols = cols;
	}

	public void Create() {
		//standard
		String content = "<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>" ;
		content += "\n";
		//root name and its children
		content += "<!ELEMENT table (row*)>";
		content += "\n";
		//attributes of table aka validcolumns names and datatypes
		Set<String> columns = cols.keySet();
		for ( String s : columns ) {
			content += "<!ATTLIST table " + s + " CDATA \"" + cols.get(s) + "\">";
			content += "\n";
		}
		//children of each row aka columns
		content += "<!ELEMENT row (";
		Object[] indexedCols = columns.toArray() ;
		for (int i = 0 ; i < indexedCols.length ; i++) {
			content += indexedCols[i] ;
			if (i != indexedCols.length -1) {
				content+= ", ";
			}
		}
		content += ")>";
		content += "\n";
		//each column can contain text value
		for (int i = 0 ; i < indexedCols.length ; i++) {
			content += "<!ELEMENT " + indexedCols[i] +" (#PCDATA)>";
			if (i != indexedCols.length -1) {
				content+= "\n";
			}
		}
		//write to the file
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(path));
			writer.write(content);
		    writer.close();
		    File f = new File (path);
		    String s = f.getName();
			BufferedWriter w = new BufferedWriter(new FileWriter(s));
			w.write(content);
			w.close();
		} catch (IOException e) {
			try {
				throw new IOException();
			} catch (RuntimeException | IOException err) {
			}
		}
	}
}
