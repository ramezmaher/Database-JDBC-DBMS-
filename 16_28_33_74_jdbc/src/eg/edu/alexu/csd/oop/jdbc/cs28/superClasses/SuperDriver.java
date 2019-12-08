package eg.edu.alexu.csd.oop.jdbc.cs28.superClasses;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverPropertyInfo;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.Properties;
import java.util.logging.Logger;

public class SuperDriver implements Driver {

	@Override
	public boolean acceptsURL(String arg0) throws SQLException {
		throw new UnsupportedOperationException();
		
	}

	@Override
	public Connection connect(String arg0, Properties arg1) throws SQLException {
		throw new UnsupportedOperationException();
		
	}

	@Override
	public int getMajorVersion() {
		throw new UnsupportedOperationException();
		
	}

	@Override
	public int getMinorVersion() {
		throw new UnsupportedOperationException();
		
	}

	@Override
	public Logger getParentLogger() throws SQLFeatureNotSupportedException {
		throw new UnsupportedOperationException();
		
	}

	@Override
	public DriverPropertyInfo[] getPropertyInfo(String arg0, Properties arg1) throws SQLException {
		throw new UnsupportedOperationException();
		
	}

	@Override
	public boolean jdbcCompliant() {
		throw new UnsupportedOperationException();
	}

}
