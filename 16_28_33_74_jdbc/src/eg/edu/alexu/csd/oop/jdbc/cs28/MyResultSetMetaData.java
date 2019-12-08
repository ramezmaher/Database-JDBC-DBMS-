package eg.edu.alexu.csd.oop.jdbc.cs28;

import java.sql.JDBCType;
import java.sql.SQLException;

import eg.edu.alexu.csd.oop.jdbc.cs28.superClasses.SuperResultSetMetaData;

public class MyResultSetMetaData extends SuperResultSetMetaData {

	private String tableName;
	private String[] columnsNames, columnsTypes;
	private MyLogger myLogger = MyLogger.getInstance();

	public MyResultSetMetaData(String tableName, String[] columnsNames, Object[] randomRow) {

		myLogger.logger.info("Creating meta data");
		this.tableName = tableName;
		this.columnsNames = columnsNames;

		columnsTypes = new String[randomRow.length];
		for (int i = 0; i < randomRow.length; i++) {
			if (randomRow[i] == null) {
				columnsTypes[i] = null;
			} else if (randomRow[i].toString().matches("\\d+")) {
				columnsTypes[i] = "int";
			} else {
				columnsTypes[i] = "string";
			}
		}
	}

	@Override
	public int getColumnCount() throws SQLException {
		return columnsNames.length;
	}

	@Override
	public String getColumnLabel(int arg0) throws SQLException {

		if (arg0 > getColumnCount() || arg0 <= 0) {
			return null;
		}
		return columnsNames[arg0];
	}

	@Override
	public String getColumnName(int arg0) throws SQLException {

		if (arg0 > getColumnCount() || arg0 <= 0) {
			return null;
		}
		return columnsNames[arg0];
	}

	@Override
	public int getColumnType(int arg0) throws SQLException {

		if (arg0 > getColumnCount() || arg0 <= 0 || columnsTypes[arg0] == null) {
			return -1;
		}
		if (columnsTypes[arg0].equals("int")) {
			return JDBCType.INTEGER.getVendorTypeNumber();
		}
		return JDBCType.VARCHAR.getVendorTypeNumber();
	}

	@Override
	public String getTableName(int arg0) throws SQLException {

		if (arg0 > getColumnCount() || arg0 <= 0) {
			return null;
		}
		return this.tableName;
	}

}
