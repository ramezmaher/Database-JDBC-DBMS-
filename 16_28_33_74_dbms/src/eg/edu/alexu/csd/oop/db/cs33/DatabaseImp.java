package eg.edu.alexu.csd.oop.db.cs33;

import java.io.File;
import java.util.ArrayList;
import java.util.Map;
import java.util.regex.Matcher;
import java.sql.SQLException;
import java.util.regex.Pattern;

import eg.edu.alexu.csd.oop.db.Database;

public class DatabaseImp implements Database {

	private ArrayList<MyTable> database = null;
	private String currentDB = null;
	private MyTable currentTable = null;
	// To be sent to JDBC
	private String[] currentQueryColumnNames = null;
	private String currentQueryTableName = null;
	
	private int queriesCounter = 0;
	private XML xml = new XML();
	
	@Override
	public String createDatabase(String databasePath, boolean dropIfExists) {
		if (dropIfExists) {
			try {
				// Drop database first then create it again
				executeStructureQuery("DROP DATABASE " + databasePath);
				executeStructureQuery("CREATE DATABASE " + databasePath);
			} catch (SQLException e) {
				try {
					throw new SQLException();
				} catch (RuntimeException | SQLException err) {
				}
			}
		} else {
			try {
				// Create database or use it if exists
				executeStructureQuery("CREATE DATABASE " + databasePath);
			} catch (SQLException e) {
				try {
					throw new SQLException();
				} catch (RuntimeException | SQLException err) {
				}
			}
		}
		String path = "tests" + System.getProperty("file.separator") + databasePath;
		return path;
	}

	@Override
	public boolean executeStructureQuery(String query) throws SQLException {

		int operation = getOperationStructure(query);

		switch (operation) {

		// *CREATE DATABASE CASE
		case 0:
			//creating the already used database
			if (currentDB != null && currentDB.equalsIgnoreCase(query.split("[\\s]+")[2])) {
				return false;
			}
			//new current database
			currentDB = query.split("[\\s]+")[2];
			File databaseFolder = new File("tests" + System.getProperty("file.separator") + currentDB);
			databaseFolder.mkdirs();
			//checking if there is already saved tables in the database
			database = new ArrayList<MyTable>();
			File[] tables = databaseFolder.listFiles();
			if (tables != null && tables.length != 0) {
				for (File f : tables) {
					if (f.getName().contains(".xml")) {
						//load those tables to the database cache
						database.add(xml.load(databaseFolder.toPath() + System.getProperty("file.separator") + f.getName()));
						currentTable = null;
					}
				}
			}
			return true;

		// *DROP DATABASE CASE
		case 1:
			//if the database wanted is the current database used
			if (currentDB != null && currentDB.equalsIgnoreCase(query.split("[\\s]+")[2])) {
					File filee = new File("tests" + System.getProperty("file.separator") + query.split("[\\s]+")[2]);
					removeFolder(filee);
					currentDB = null;
					database = null;
					currentTable = null;
					return true;
			}
			//if the database wanted isn't the current database then delete the saved database without changing the current database
			String temp = query.split("[\\s]+")[2];
			File filee = new File("tests" + System.getProperty("file.separator") + temp);
			return removeFolder(filee);

		// *CREATE TABLE CASE
		case 2:
			if (currentDB == null) {
				throw new SQLException();
			}
			CreateTableParser parser = new CreateTableParser(query);
			// Return false if table columns is empty (for tests)
			if (parser.isMapEmpty()) {
				throw new SQLException();
			}
			// check if the table is already created
			if (database != null && database.size() > 0) {
				for (MyTable t : database) {
					if (t.getName().equalsIgnoreCase(parser.getName())) {
						return false;
					}
				}
			}

			MyTable table = new MyTable(parser.getColumnsMap());
			table.setName(parser.getName());
			table.setColumnsCasePreserved(parser.getColumnsCasePreserved());
			database.add(table);
			String path = "tests" + System.getProperty("file.separator") + currentDB
					+ System.getProperty("file.separator") + parser.getName();
			xml.create(path, table.getValidColumnsMap());
			return true;

		// *DROP TABLE CASE
		case 3:
			if (currentDB == null) {
				throw new SQLException();
			}

			CreateTableParser parserDrop = new CreateTableParser(query);
			String wantedTable = parserDrop.getName();
			
			for (MyTable t : database) {
				if (t.getName().equalsIgnoreCase(wantedTable)) {
					this.database.remove(t);
					String pathh = "tests" + System.getProperty("file.separator") + currentDB
							+ System.getProperty("file.separator") + t.getName();
					xml.drop(pathh);
					if (currentTable.getName().equalsIgnoreCase(wantedTable)) {
						currentTable = null;
					}
					return true;
				}
			}
		}
		return false;
	}

	@Override
	public Object[][] executeQuery(String query) throws SQLException {

		SelectParser parser = new SelectParser();
		String name = parser.getName(query);
		String[] Columns = parser.getColumns(query);
		String Condition = parser.getCondition(query);

		int tableIndex = -1;

		for (MyTable table : database) {
			Pattern pattern = Pattern.compile(name, Pattern.CASE_INSENSITIVE);
			Matcher m = pattern.matcher(table.getName());
			if (m.find())
				tableIndex = database.indexOf(table);
		}

		if (tableIndex == -1)
			return null;

		MyTable table = database.get(tableIndex);
		ArrayList<Map<String, String>> result = table.select(Columns, Condition);
		if (result == null)
			return null;
		
		// To be sent to JDBC
		if(query.contains("*")) {
			String[] columnsCasePreserved = new String[table.getColumnsCasePreserved().size()];
			columnsCasePreserved = table.getColumnsCasePreserved().toArray(columnsCasePreserved);
			this.currentQueryColumnNames = columnsCasePreserved;
		}
		else {
			this.currentQueryColumnNames = Columns;
		}
		this.currentQueryTableName = name;

		int rows = 0, cols = 0;
		rows = result.size();
		if (rows > 0)
			cols = result.get(0).size();

		Object[][] finalResult = new Object[rows][cols];
		for (int i = 0; i < rows; i++) {
			Map<String, String> element = result.get(i);
			int j = 0;
			for (String s : element.keySet()) {

				// If the column data type is integer then parse string to integer
				if (table.getValidColumnsMap().get(s).equalsIgnoreCase("int")) {
					finalResult[i][j] = Integer.parseInt(element.get(s));
				} else {
					finalResult[i][j] = element.get(s);
				}
				j++;
			}
		}
		for (int i = 0 ; i < finalResult.length ; i++) {
			for (int j = 0 ; j < finalResult[i].length ; j++) {
				System.out.print(finalResult[i][j] + "\t");
			}
			System.out.println();
		}
		return finalResult;
	}

	@Override
	public int executeUpdateQuery(String query) throws SQLException {

		// The updated rows count after any operation
		int updatedRows = 0;
		int operation = getOperationUpdate(query);
		if (operation == -1)
			return 0;
		if (database.isEmpty()) {
			throw new SQLException();
		}
		int i = 0;
		switch (operation) {

		// *INSERT case
		case 0:
			InsertParser parser = new InsertParser(query, "INSERT");
			String name = parser.getName();
			boolean found = false;
			for (i = 0; i < database.size(); i++) {

				String n = database.get(i).getName();
				if (n.equalsIgnoreCase(name)) {

					parser.setColumns(database.get(i).getValidColumnsArray());
					updatedRows = database.get(i).addRow(parser.getMap());

					found = true;
					break;
				}
			}
			if (!found) {
				throw new SQLException();
			}
			break;

		// *UPDATE case
		case 1:
			InsertParser parse = new InsertParser(query, "UPDATE");
			String nameU = parse.getName();
			boolean foundU = false;
			for (i = 0; i < database.size(); i++) {
				String n = database.get(i).getName();
				if (n.equalsIgnoreCase(nameU)) {
					String[] arr = database.get(i).parseCondition(parse.getCondition());
					updatedRows = database.get(i).Update(arr[1], arr[2], Integer.parseInt(arr[0]), parse.getMap());
					foundU = true;
					break;
				}
			}
			if (!foundU) {
				throw new SQLException();
			}
			break;

		// *DELETE case
		case 2:
			InsertParser pars = new InsertParser(query, "DELETE");
			String nameD = pars.getName();
			boolean foundD = false;
			for (i = 0; i < database.size(); i++) {
				String n = database.get(i).getName();
				if (n.equalsIgnoreCase(nameD)) {
					String[] arr = database.get(i).parseCondition(pars.getCondition());
					updatedRows = database.get(i).remove(arr[1], arr[2], Integer.parseInt(arr[0]));
					foundD = true;
					break;
				}
			}
			if (!foundD) {
				throw new SQLException();
			}
			break;
		}
		// if the user decided to choose another table to operate on we save the
		// previous table first
		if (currentTable == null) {
			currentTable = database.get(i);
			if (updatedRows != 0) {
				queriesCounter++;
			}
		} else if (database.get(i) != currentTable) {
			save();
			currentTable = database.get(i);
			queriesCounter = 0;
		}
		// if 5 operations are done on the same table we save the table
		else if (updatedRows != 0) {
			queriesCounter++;
			if (queriesCounter == 5) {
				save();
				queriesCounter = 0;
			}
		}
		System.out.println(updatedRows);
		return updatedRows;
	}

	// Helper function to select which operation to be performed
	// on database structure
	private int getOperationStructure(String query) {
		Pattern pattern = Pattern.compile("create database", Pattern.CASE_INSENSITIVE);
		Matcher m = pattern.matcher(query);
		if (m.find())
			return 0;

		pattern = Pattern.compile("drop database", Pattern.CASE_INSENSITIVE);
		m = pattern.matcher(query);
		if (m.find())
			return 1;

		pattern = Pattern.compile("create table", Pattern.CASE_INSENSITIVE);
		m = pattern.matcher(query);
		if (m.find())
			return 2;

		pattern = Pattern.compile("drop table", Pattern.CASE_INSENSITIVE);
		m = pattern.matcher(query);
		if (m.find())
			return 3;

		return -1;
	}

	// Helper function to select which operation to be performed
	// on database as an update to it
	private int getOperationUpdate(String query) {
		Pattern pattern = Pattern.compile("insert into", Pattern.CASE_INSENSITIVE);
		Matcher m = pattern.matcher(query);
		if (m.find())
			return 0;

		pattern = Pattern.compile("update", Pattern.CASE_INSENSITIVE);
		m = pattern.matcher(query);
		if (m.find())
			return 1;
		pattern = Pattern.compile("delete", Pattern.CASE_INSENSITIVE);
		m = pattern.matcher(query);
		if (m.find())
			return 2;

		return -1;
	}

	// Recursive method to remove all files in a folder
	private boolean removeFolder(File folder) {

		// Check if folder is empty
		File[] files = folder.listFiles();
		if (files != null) {
			for (File f : files) {
				removeFolder(f);
			}
		}
		return folder.delete();
	}

	// Getters methods
	public ArrayList<MyTable> getTables() {
		return (ArrayList<MyTable>) this.database;
	}

	public void save() {
		if (currentDB != null && currentTable != null) {
			String path = "tests" + System.getProperty("file.separator") + currentDB
					+ System.getProperty("file.separator") + currentTable.getName() + ".xml";
			xml.save(path, currentTable);
		}
	}

	public String[] getCurrentColumnNames() {
		return currentQueryColumnNames;
	}

	public String getCurrentQueryTableName() {
		return currentQueryTableName;
	}
	
}
