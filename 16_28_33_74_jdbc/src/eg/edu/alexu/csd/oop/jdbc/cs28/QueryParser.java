package eg.edu.alexu.csd.oop.jdbc.cs28;

import java.sql.SQLException;
import java.sql.Statement;

public class QueryParser {
	
	private final int startIndexToCheckTableSchema = 3;
	
	public QueryParser() {}
	
	public boolean commandChooser(String query, Statement statement) {
		
		// Split the query into number of strings that can be manipulated
		String[] querySplit;
		boolean result = false;
		try {
			querySplit = query.split("[\\s(,)=;]+");
		}
		catch (NullPointerException e) {
			return false;
		}
		
		// Learning the command from the strings and send the command to the appropriate class
		// CREATE DATABASE databaseName
		if(querySplit.length == 3 && querySplit[0].equalsIgnoreCase("CREATE") && querySplit[1].equalsIgnoreCase("DATABASE")) {
			
			try {
				statement.execute(query);
			} catch (SQLException e) {
				return false;
			}
			return true;
		}
		// CREATE TABLE tableName (Column1 datatype, Column2 datatype, ..)
		else if(querySplit.length > 3 && querySplit[0].equalsIgnoreCase("CREATE") && querySplit[1].equalsIgnoreCase("TABLE")) {
			if(checkCreateTableSchema(querySplit)) {
				try {
					result = statement.execute(query);

				} catch (SQLException e) {
					return false;
				}
				return result;
			}
			return false;
		}
		// DROP DATABASE databaseName
		else if(querySplit.length == 3 && querySplit[0].equalsIgnoreCase("DROP") && querySplit[1].equalsIgnoreCase("DATABASE")) {
			try {
				result = statement.execute(query);

			} catch (SQLException e) {
				return false;
			}
			return result;
		}
		// DROP TABLE tableName
		else if(querySplit.length == 3 && querySplit[0].equalsIgnoreCase("DROP") && querySplit[1].equalsIgnoreCase("TABLE")) {
			try {
				result = statement.execute(query);

			} catch (SQLException e) {
				return false;
			}
			return result;
		}
		// SELECT column FROM tableName
		else if(querySplit.length >= 4 && querySplit[0].equalsIgnoreCase("SELECT")) {
			for(int i = 0; i < querySplit.length; i++) {
				if(querySplit[i].equalsIgnoreCase("FROM")) {
					try {
						statement.executeQuery(query);
					} catch (SQLException e) {
						return false;
					}
					return true;
				}
			}
			return false;
		}
		// INSERT INTO table_name optional:{(column1, ...)} VALUES (value1, ...)
		else if(querySplit.length >= 5 && querySplit[0].equalsIgnoreCase("INSERT") && querySplit[1].equalsIgnoreCase("INTO")) {
			if(querySplit[3].equalsIgnoreCase("VALUES") || checkInsertTableSchema(querySplit)) {
				try {
					statement.executeUpdate(query);
				} catch (SQLException e) {
					return false;
				}
				return true;
			}
			return false;

		}
		// DELETE FROM tableName optional: WHERE condition
		else if(querySplit.length >= 3 && querySplit[0].equalsIgnoreCase("DELETE") && querySplit[1].equalsIgnoreCase("FROM")) {
			try {
				statement.executeUpdate(query);
			} catch (SQLException e) {
				return false;
			}
			return true;
		}
		// UPDATE tableName SET column1 = value1, ... optional: WHERE condition

		else if(querySplit.length >= 5 && querySplit[0].equalsIgnoreCase("UPDATE") && querySplit[2].equalsIgnoreCase("SET")) {
			if(checkUpdateTableSchema(querySplit)) {
				try {
					statement.executeUpdate(query);
				} catch (SQLException e) {
					return false;
				}
				return true;
			}
		}
		return false;
	}
	
	// Method to check that the create table columns number equal their datatype number
	private boolean checkCreateTableSchema(String[] columns) {
		
		// Check that the columns number plus the datatype number is even
		if ((columns.length - startIndexToCheckTableSchema) % 2 != 0) {
			return false;
		}
		
		// Check each datatype
		for (int i = startIndexToCheckTableSchema + 1; i < columns.length; i += 2) {
			if (!columns[i].equals("varchar") && !columns[i].equals("int")) {
				return false;
			}
		}
		return true;
	}
	
	// Method to check that the insert into table columns number equal their datatype number
	private boolean checkInsertTableSchema(String[] columns) {
		
		// Check that the columns number plus the datatype number is odd
		// because of the additional VALUES keyword
		if ((columns.length - startIndexToCheckTableSchema) % 2 == 0) {
			return false;
		}
		
		// Check each column till VALUES then check datatype
		int i = startIndexToCheckTableSchema, columnsNum = 0, datatypeNum = 0;
		for (; i < columns.length; i++) {
			if (!columns[i].equalsIgnoreCase("VALUES")) {
				columnsNum++;
			}
			else {
				i++;
				break;
			}
		}
		for (; i < columns.length; i++) {
			datatypeNum++;
		}
		
		// Check if the columns number doesn't equal the datatype number
		if(columnsNum != datatypeNum) {
			return false;
		}
		
		return true;
	}
	
	// Method to check that the update table columns number equal their datatype number
	private boolean checkUpdateTableSchema(String[] columns) {
		
		int endIndexToCheckTableSchema = columns.length;
		
		// Loop through the array to check if WHERE is present in the statement
		for (int i = 5; i < columns.length; i++) {
			if(columns[i].equalsIgnoreCase("WHERE")) {
				endIndexToCheckTableSchema = i;
				break;
			}
		}
		
		// Check that the columns number plus the datatype number is even
		if ((endIndexToCheckTableSchema - startIndexToCheckTableSchema) % 2 != 0) {
			return false;
		}
		
		return true;
	}
}


