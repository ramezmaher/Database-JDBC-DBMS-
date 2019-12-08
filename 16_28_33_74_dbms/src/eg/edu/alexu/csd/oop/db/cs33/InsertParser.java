package eg.edu.alexu.csd.oop.db.cs33;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

public class InsertParser {

	private String[] querySplit;
	private String operation;
	private int len;
	private String condition;
	private ArrayList<String> columns;

	public InsertParser(String s, String op) {
		this.operation = op;
		if (op.equals("DELETE") || op.equals("UPDATE")) {
			this.condition = getCond(s);
		}
		this.querySplit = s.split("[\\s(,);=]+");
		this.len = querySplit.length;
	}

	public String getName() {

		if (operation.equals("DELETE") || operation.equals("INSERT"))
			return querySplit[2];
		else
			return querySplit[1];
	}

	public Map<String, String> getMap() {
		if (operation == "UPDATE") {

			// iterator that indicates the element after SET in the query (UPDATE table_name
			// SET col1=val1 ,col2=val2,... WHERE condition )
			int i = 3;

			Map<String, String> setMap = new LinkedHashMap<String, String>();
			String[] temp = querySplit;
			for (int x = 0; x < temp.length; x++) {
				temp[x] = temp[x].toLowerCase();
			}
			while (i < len && (!temp[i].toUpperCase().equals("WHERE"))) {
				setMap.put(temp[i], temp[i + 1]);
				i += 2;
			}
			return setMap;
		}

		else if (operation == "INSERT") {

			Map<String, String> map = new LinkedHashMap<String, String>();
			String temp = new String();
			temp = querySplit[3];
			temp = temp.toUpperCase();
			if (temp.equals("VALUES")) {
				if (len - 4 == columns.size()) {
					int i = 4;
					int j = 0;
					int size = columns.size();
					while (i < len && j < size) {
						map.put(columns.get(j).toLowerCase(), querySplit[i]);
						i++;
						j++;
					}
				} else {
					return null;
				}

				return map;
			} else {
				int i = 3;
				ArrayList<String> list = new ArrayList<String>();
				while (!querySplit[i].toUpperCase().equals("VALUES")) {
					list.add(querySplit[i]);
					i++;
				}
				i++;
				int size = list.size();
				int j = 0;
				if (len - i != size) {
					return null;
				}
				while (i < len && j < size) {
					map.put(list.get(j).toLowerCase(), querySplit[i]);
					i++;
					j++;
				}
				Map<String,String> orderedMap = new LinkedHashMap<String,String>();
				for (String s : columns) {
					if (map.keySet().contains(s.toLowerCase())) {
						orderedMap.put(s, map.get(s.toLowerCase()));
					}
				}
				return orderedMap;
			}
		} else
			return null;
	}

	private String getCond(String str) {
		String st = new String();
		String temp = new String();
		temp = str;
		temp = temp.toUpperCase();
		if (temp.contains("WHERE")) {
			st = str.substring(temp.indexOf(" WHERE ") + 6 , str.length());
			return st.trim();
		} else {
			return null;
		}

	}

	public String getCondition() {
		return condition;
	}

	public void setColumns(ArrayList<String> columns) {
		this.columns = columns;
	}

}
