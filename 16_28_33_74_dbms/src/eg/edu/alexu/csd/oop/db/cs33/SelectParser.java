package eg.edu.alexu.csd.oop.db.cs33;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SelectParser {
	
	public String getName(String query)
	{
		Pattern pattern = Pattern.compile("from",Pattern.CASE_INSENSITIVE);
		Matcher m = pattern.matcher(query);
		if(m.find())
		{
			query = query.substring(m.end(), query.length()-1);
		}
		pattern = Pattern.compile("where",Pattern.CASE_INSENSITIVE);
		m = pattern.matcher(query);
		if(m.find())
		{
			query = query.substring(0, m.start());
		}
		query = query.replace(";", "");
		return query.trim();
	}
	
	public String[] getColumns(String query)
	{
		Pattern pattern = Pattern.compile("select",Pattern.CASE_INSENSITIVE);
		Matcher m = pattern.matcher(query);
		if(m.find())
			query = query.substring(m.end(), query.length()-1);
		pattern = Pattern.compile("from", Pattern.CASE_INSENSITIVE);
		m = pattern.matcher(query);
		if(m.find())
			query = query.substring(0, m.start());
		
		String[] columns = query.split(",");
		for(int i=0;i<columns.length;i++)
			columns[i]=columns[i].trim();
		
		return columns;
	}

	public String getCondition(String query)
	{
		Pattern pattern = Pattern.compile("where",Pattern.CASE_INSENSITIVE);
		Matcher m = pattern.matcher(query);
		if(m.find())
		{
			query = query.substring(m.end(), query.length());
			query = query.replace(";", "");
			return query.trim();
		}
		
		return null;
	}

}
