package eg.edu.alexu.csd.oop.db.cs33;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
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
import org.w3c.dom.Node;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

public class XMLSave {

	private String path;
	private String tempPath;
	private ArrayList<Map<String,String>> table = new ArrayList<Map<String,String>>();
	private Map<String, String> cols = new LinkedHashMap<String, String>() ;

	public XMLSave (String path, MyTable table) {
		this.path = path;
		this.tempPath = path.substring(0 , path.length() - 4 ) + "temp.xml" ; 
		this.table = table.getTable();
		this.cols = table.getValidColumnsMap();
	}
	
	public void Save() {
		try {
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			//validate the schema
			dbFactory.setValidating(true);
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
			//writing the table
			for (Map<String,String> m : table) {
				Node row = doc.createElement("row");
				root.appendChild(row);
				for (String key : m.keySet()) {
					Node column = doc.createElement(key);
					column.appendChild(doc.createTextNode(m.get(key)));
					row.appendChild(column);
				}
			}
			TransformerFactory tf = TransformerFactory.newInstance();
			Transformer transformer = tf.newTransformer();
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			transformer.setOutputProperty(OutputKeys.ENCODING, "ISO-8859-1");
			DOMImplementation domImp  = doc.getImplementation();
			DocumentType docType = domImp.createDocumentType("doctype", "SYSTEM", new File(path).getName().substring(0, new File(path).getName().length() - 4) + ".dtd");
			transformer.setOutputProperty(OutputKeys.DOCTYPE_SYSTEM, docType.getSystemId());
			DOMSource domSource = new DOMSource(doc);
			//create this data first in a temp file before writing them in order to validate it with the schema file
			FileOutputStream fos = new FileOutputStream(new File(tempPath));
			transformer.transform(domSource, new StreamResult(fos));
			fos.close();
			validate();
			//delete the temp file
			new File(tempPath).delete();
		}
		catch (ParserConfigurationException e) {
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
	
	//validate that the written file match the attached schema file
	private boolean validate() {
		try {
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			//validate the schema
			dbFactory.setValidating(true);
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			//handling errors
			dBuilder.setErrorHandler(new ErrorHandler() {

				@Override
				public void error(SAXParseException arg0) throws SAXException {
					throw new SAXException();
				}

				@Override
				public void fatalError(SAXParseException arg0) throws SAXException {
					throw new SAXException();
				}

				@Override
				public void warning(SAXParseException arg0) throws SAXException {
					throw new SAXException();
				}
				
			});
			File file = new File(tempPath);
			FileInputStream fis = new FileInputStream(file);
			Document doc = dBuilder.parse(fis);
			//if it matches the schema then parse the temp xml file into the original xml file
			doc.setXmlStandalone(true);
			TransformerFactory tf = TransformerFactory.newInstance();
			Transformer transformer = tf.newTransformer();
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			transformer.setOutputProperty(OutputKeys.ENCODING, "ISO-8859-1");
			DOMImplementation domImp  = doc.getImplementation();
			DocumentType docType = domImp.createDocumentType("doctype", "SYSTEM", new File(path).getName().substring(0, new File(path).getName().length() - 4) + ".dtd");
			transformer.setOutputProperty(OutputKeys.DOCTYPE_SYSTEM, docType.getSystemId());
			DOMSource domSource = new DOMSource(doc);
			FileOutputStream fos = new FileOutputStream(new File(path));
			transformer.transform(domSource, new StreamResult(fos));
			fos.close();
			fis.close();
			return true;
		}
		catch (ParserConfigurationException e) {
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
		} catch (SAXException e) {
			try {
				throw new SAXException();
			} catch (RuntimeException | SAXException err) {
			}
		} catch (IOException e) {
			e.printStackTrace();
			try {
				throw new IOException();
			} catch (RuntimeException | IOException err) {
			}
		}
		return false;
	}
}
