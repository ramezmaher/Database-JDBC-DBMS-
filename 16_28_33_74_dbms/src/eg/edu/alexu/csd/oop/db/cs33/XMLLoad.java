package eg.edu.alexu.csd.oop.db.cs33;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

public class XMLLoad {

	private String path;
	private MyTable myTable;
	
	public XMLLoad(String path) {
		this.path = path;
	}
	
	public MyTable Load(){
		try {
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			//handling error if the xml file doesn't match the schema file
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
			Document doc = dBuilder.parse(new File(path));
			//root of the tree
			Node root = doc.getDocumentElement();
			//loading validColumns
			NamedNodeMap validColumns = root.getAttributes();
			Map<String, String> cols = new LinkedHashMap<String,String>();
			for (int i = 0 ; i < validColumns.getLength() ; i++) {
				cols.put(validColumns.item(i).getNodeName(), validColumns.item(i).getNodeValue()) ;
			}
			NodeList rows = root.getChildNodes();
			//loading the table
			ArrayList<Map<String,String>> table = new ArrayList<Map<String,String>>();
			for (int i = 0 ; i < rows.getLength(); i++) {
				if (rows.item(i).getNodeName().equals("row") ) {
					NodeList columns = rows.item(i).getChildNodes();
					Map<String,String> map = new LinkedHashMap<String,String>();
					for (int j = 0 ; j < columns.getLength() ; j++) {
						if (!columns.item(j).getNodeName().equals("#text")) {
							map.put(columns.item(j).getNodeName(), columns.item(j).getTextContent());
							table.add(map);
						}
					}
				}
			}
			myTable = new MyTable(cols);
			myTable.setTable(table);
			myTable.setsize(table.size());
			myTable.setName(new File(path).getName().substring(0 , new File(path).getName().length() - 4));
		}
		catch (ParserConfigurationException e) {
			try {
				throw new ParserConfigurationException();
			} catch (RuntimeException | ParserConfigurationException err) {
			}
		} catch (SAXException e) {
			try {
				throw new SAXException();
			} catch (RuntimeException | SAXException err) {
			}
		} catch (IOException e) {
			try {
				throw new IOException();
			} catch (RuntimeException | IOException err) {
			}
		}
		return myTable;
	}
}


