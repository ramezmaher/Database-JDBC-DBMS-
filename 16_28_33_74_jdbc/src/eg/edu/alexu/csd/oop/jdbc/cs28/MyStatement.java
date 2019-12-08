package eg.edu.alexu.csd.oop.jdbc.cs28;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import eg.edu.alexu.csd.oop.db.cs33.DatabaseImp;
import eg.edu.alexu.csd.oop.jdbc.cs28.superClasses.SuperStatement;

public class MyStatement extends SuperStatement {

	private DatabaseImp DB;
	private ArrayList<String> batch = new ArrayList<String>();
	private int TimeOut;
	private boolean closed;
	private Connection connection;
	private String dir;
	private MyLogger myLogger = MyLogger.getInstance();

	public MyStatement(Connection connection, String path, DatabaseImp DB) throws SQLException {
		
		this.connection = connection;
		this.dir = path;
		this.DB = DB;
		closed = false;
		myLogger.logger.info("Statement created successfully");
	}

	@Override
	public void addBatch(String sql) throws SQLException {
		
		if (closed) {
			throw new SQLException("Statement closed");
		}
		batch.add(sql);
		myLogger.logger.info("Adding command to batch");
	}

	@Override
	public void clearBatch() throws SQLException {

		if (closed) {
			throw new SQLException("Statement closed");
		}
		batch.clear();
		myLogger.logger.warning("Batch cleared");
	}

	@Override
	public void close() throws SQLException {
		if (!closed) {
			DB.save();
		}
		closed = true;
		batch = null;
		myLogger.logger.warning("Closing statement");
	}

	@Override
	public boolean execute(String sql) throws SQLException {

		if (closed) {
			throw new SQLException("Statement closed");
		}
		
		String sqlKey = identifySQl(sql);
		boolean result = false;
		
		if(sqlKey.equals("structure")) {
			return DB.executeStructureQuery(sql);
		}
		
		else if(sqlKey.equals("select") && executeQuery(sql) != null) {
			result = true;
		}
		else if(sqlKey.equals("update") && executeUpdate(sql) != 0) {
			result = true;
		}
		
		myLogger.logger.info("Executing command");
		return result;
	}

	@Override
	public int[] executeBatch() throws SQLException {
		int[] updateCounts = new int[batch.size()];
		String command;

		// Looping through commands identifying each statement and calling the
		// appropriate executing function
		for (int i = 0; i < batch.size(); i++) {
			command = batch.get(i);

			switch (identifySQl(command)) {
			case "structure":
				if (execute(command) == true) {
					updateCounts[i] = SUCCESS_NO_INFO;
				} else {
					updateCounts[i] = EXECUTE_FAILED;
				}
				break;

			case "select":
				if (executeQuery(command) != null) {
					updateCounts[i] = SUCCESS_NO_INFO;
				} else {
					updateCounts[i] = EXECUTE_FAILED;
				}
				break;

			case "update":
				updateCounts[i] = executeUpdate(command);
				break;

			case "NonSQL":
				updateCounts[i] = EXECUTE_FAILED;
			}
		}
		myLogger.logger.info("Batch of commands executed");
		return updateCounts;
	}

	@Override
	public ResultSet executeQuery(String sql) throws SQLException {
		if (closed) {
			throw new SQLException("Statement closed");
		}
		myLogger.logger.info("Creating resultSet data");
		Object[][] s = DB.executeQuery(sql);
		if( s != null) {
			return new MyResultSet(s, DB.getCurrentQueryTableName(), this, DB.getCurrentColumnNames());
		}
		return null;
	}

	@Override
	public int executeUpdate(String sql) throws SQLException {
		if (closed) {
			throw new SQLException("Statement closed");
		}
		myLogger.logger.info("Executing update query");
		return DB.executeUpdateQuery(sql);
	}

	@Override
	public Connection getConnection() throws SQLException {
		if (closed) {
			throw new SQLException("Statement closed");
		}
		return this.connection;
	}

	@Override
	public int getQueryTimeout() throws SQLException {
		if (closed) {
			throw new SQLException("Statement closed");
		}
		return TimeOut;
	}

	@Override
	public void setQueryTimeout(int seconds) throws SQLException {
		if (closed) {
			throw new SQLException("Statement closed");
		}
		this.TimeOut = seconds;
	}
	
	// Helper method to identify the sql statement
	private String identifySQl(String sql) {

		// Retrieve first word of the sql statement
		String sqlKey = sql.split("[\\s]+")[0];

		if (sqlKey.equalsIgnoreCase("create") || sqlKey.equalsIgnoreCase("drop")) {
			return "structure";
		}

		else if (sqlKey.equalsIgnoreCase("select")) {
			return "select";
		}

		else if (sqlKey.equalsIgnoreCase("insert") || sqlKey.equalsIgnoreCase("delete")
				|| sqlKey.equalsIgnoreCase("update")) {
			return "update";
		}
		
		return "NonSQL";
	}

}
