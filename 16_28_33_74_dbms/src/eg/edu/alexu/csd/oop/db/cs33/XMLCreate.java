package eg.edu.alexu.csd.oop.db.cs33;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentType;
import org.w3c.dom.Element;

public class XMLCreate {
	
	private String path; //XML file path in order to be created into
	private Map<String, String> cols = new LinkedHashMap<String, String>() ;
	
	//Initialization constructor
	public XMLCreate(String path , Map<String, String> cols) {
		this.path = path;
		this.cols = cols ;
	}
	
	//Creates the XML file and writes header with column names
	public void Create() {
		try {
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.newDocument();
			doc.setXmlStandalone(true);
			//root of the tree
			Element root = doc.createElement("table");
			Set<String> columns = cols.keySet();
			for ( String s : columns) {
				root.setAttribute(s, cols.get(s));
			}
			
			doc.appendChild(root);
			TransformerFactory tf = TransformerFactory.newInstance();
			Transformer transformer = tf.newTransformer();
			//indentation
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			//encoding
			transformer.setOutputProperty(OutputKeys.ENCODING, "ISO-8859-1");
			//linking with the dtd schema file
			DOMImplementation domImp  = doc.getImplementation();
			DocumentType docType = domImp.createDocumentType("doctype", "SYSTEM", new File(path).getName().substring(0, new File(path).getName().length() - 4) + ".dtd");
			transformer.setOutputProperty(OutputKeys.DOCTYPE_SYSTEM, docType.getSystemId());
			//transforming from tree to file
			DOMSource domSource = new DOMSource(doc);
			FileOutputStream fos = new FileOutputStream(new File(path));
			transformer.transform(domSource, new StreamResult(fos));
			fos.close();
		} catch (ParserConfigurationException e) {
			try {
				throw new ParserConfigurationException();
			} catch (RuntimeException | ParserConfigurationException err) {
			}
		} catch (TransformerConfigurationException e) {
			try {
				throw new TransformerConfigurationException();
			} catch (RuntimeException | TransformerConfigurationException err) {
			}
		} catch (TransformerException e) {
			try {
				throw new TransformerException(e);
			} catch (RuntimeException | TransformerException err) {
			}
		} catch (FileNotFoundException e) {
			try {
				throw new FileNotFoundException();
			} catch (RuntimeException | FileNotFoundException err) {
			}
		} catch (IOException e) {
			try {
				throw new IOException();
			} catch (RuntimeException | IOException err) {
			}
		}	
	}
}
