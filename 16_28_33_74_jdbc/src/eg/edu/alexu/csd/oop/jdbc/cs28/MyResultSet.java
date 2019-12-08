package eg.edu.alexu.csd.oop.jdbc.cs28;

import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Random;

import eg.edu.alexu.csd.oop.jdbc.cs28.superClasses.SuperResultSet;

public class MyResultSet extends SuperResultSet {

	private Object[][] table;
	private String tableName;
	private String[] columnsNames;
	private int currentInd, rowsNum, columnsNum;
	// The statement that generated this result set
	private Statement generator;
	// Boolean to know whether the table is closed or not
	private boolean closed;
	private MyLogger myLogger = MyLogger.getInstance();

	public MyResultSet(Object[][] table, String tableName, Statement generator, String[] columnsNames) {

		this.table = table;
		this.tableName = tableName;
		this.columnsNames = columnsNames;
		this.currentInd = -1;
		this.rowsNum = table.length;
		if(table.length != 0) {
			this.columnsNum = table[0].length;
		}
		this.generator = generator;
		closed = false;
		myLogger.logger.info("Creating resultSet");
	}

	@Override
	public boolean absolute(int row) throws SQLException {

		if (row > rowsNum || row < -rowsNum) {
			return false;
		}

		if (row == 0) {
			currentInd = -1;
		} else if (row > 0) {
			currentInd = row - 1;
		} else {
			currentInd = rowsNum + row;
		}
		myLogger.logger.info("Moving current index to row " + row);
		return true;
	}

	@Override
	public void afterLast() throws SQLException {
		currentInd = rowsNum;
	}

	@Override
	public void beforeFirst() throws SQLException {
		currentInd = -1;
	}

	@Override
	public void close() throws SQLException {

		if(!closed) {
			table = null;
			columnsNames = null;
			currentInd = -1;
			rowsNum = 0;
			columnsNum = 0;
			generator = null;
			closed = true;
			myLogger.logger.warning("ResultSet Closed");
		}
		throw new SQLException();
	}

	@Override
	public int findColumn(String columnLabel) throws SQLException {

		for (int i = 0; i < columnsNames.length; i++) {
			if (columnsNames[i].equalsIgnoreCase(columnLabel)) {
				return i;
			}
		}
		throw new SQLException();
	}

	@Override
	public boolean first() throws SQLException {

		if (rowsNum > 0) {
			currentInd = 0;
			return true;
		}
		return false;
	}

	@Override
	public int getInt(int columnIndex) throws SQLException {

		if (columnIndex > columnsNum || columnIndex <= 0) {
			myLogger.logger.info("Invalid column index is entered");
			throw new SQLException();
		}
		if (table[currentInd][columnIndex - 1] == null) {
			return 0;
		}
		myLogger.logger.info("Getting result at column " + columnIndex);
		return (int) table[currentInd][columnIndex - 1];
	}

	@Override
	public int getInt(String columnLabel) throws SQLException {

		if (table[currentInd][findColumn(columnLabel)] == null) {
			return 0;
		}
		return (int) table[currentInd][findColumn(columnLabel)];
	}

	@Override
	public ResultSetMetaData getMetaData() throws SQLException {

		// Get random row of the table
		Random rand = new Random();
		return new MyResultSetMetaData(this.tableName, this.columnsNames, table[rand.nextInt(rowsNum)]);
	}

	@Override
	public Object getObject(int columnIndex) throws SQLException {

		if (columnIndex > columnsNum || columnIndex <= 0) {
			myLogger.logger.info("Invalid column index is entered");
			throw new SQLException();
		}
		myLogger.logger.info("Getting result at column " + columnIndex);
		return table[currentInd][columnIndex - 1];
	}

	@Override
	public Statement getStatement() throws SQLException {
		return this.generator;
	}

	public String getString(int columnIndex) throws SQLException {

		if (columnIndex > columnsNum || columnIndex <= 0) {
			myLogger.logger.info("Invalid column index is entered");
			throw new SQLException();
		}
		if (table[currentInd][columnIndex - 1] == null) {
			return null;
		}
		myLogger.logger.info("Getting result at column " + columnIndex);
		return (String) table[currentInd][columnIndex - 1];
	}

	@Override
	public String getString(String columnLabel) throws SQLException {

		if (table[currentInd][findColumn(columnLabel)] == null) {
			return null;
		}
		return (String) table[currentInd][findColumn(columnLabel)];
	}

	@Override
	public boolean isAfterLast() throws SQLException {
		return currentInd == rowsNum;
	}

	@Override
	public boolean isBeforeFirst() throws SQLException {
		return currentInd == -1;
	}

	@Override
	public boolean isClosed() throws SQLException {
		return closed;
	}

	@Override
	public boolean isFirst() throws SQLException {
		return currentInd == 0;
	}

	@Override
	public boolean isLast() throws SQLException {
		return currentInd == rowsNum - 1;
	}

	@Override
	public boolean last() throws SQLException {

		if (rowsNum > 0) {
			currentInd = rowsNum - 1;
			myLogger.logger.info("Moving current index to last row");
			return true;
		}
		return false;
	}

	@Override
	public boolean next() throws SQLException {

		currentInd++;
		if (currentInd < rowsNum) {
			myLogger.logger.info("Moving current index to next row");
			return true;
		}
		return false;
	}

	@Override
	public boolean previous() throws SQLException {

		currentInd--;
		if (currentInd < 0) {
			return false;
		}
		myLogger.logger.info("Moving current index to previous row");
		return true;
	}

}
