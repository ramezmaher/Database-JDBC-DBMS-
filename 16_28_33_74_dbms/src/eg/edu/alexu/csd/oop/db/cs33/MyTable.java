package eg.edu.alexu.csd.oop.db.cs33;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MyTable {

	// This ArrayList is the table, it stores rows of maps , and the columns will be
	// the keys of the map
	private ArrayList<Map<String, String>> table = new ArrayList<Map<String, String>>();
	// This set is to store all the Keys of the first map in the ArrayList so that
	// no one add maps with different keys
	private Map<String, String> validColumns = new LinkedHashMap<String,String>();
	// Temporary array list to preserve the case of the columns' names
	private ArrayList<String> columnsCasePreserved = new ArrayList<String>();
	private int Size;
	private String name;
	private int changedCounter = 0;

	public MyTable(Map<String, String> Columns) {
		Size = 0;
		this.validColumns = Columns;
	}
	
	public int addRow(Map<String, String> row) {

		if (checkValid(row)) {
			table.add(row);
			Size++;
			changedCounter++;
		}
		else {
		}
		int c = changedCounter;
		changedCounter = 0;
		return c;
	}

	public int remove(String key, String value, int op) {
		if (op == 0) {
			int condVal = Integer.parseInt(value); // the condition's value
			for (int i = 0; i < Size; i++) {
				int val = Integer.parseInt(table.get(i).get(key));// The value form the table to be compared with the
																	// condition
				if (val > condVal) {
					table.remove(i);
					Size--;
					i--;
					changedCounter++;
				}
			}
		} else if (op == 1) {
			int condVal = Integer.parseInt(value); // the condition's value
			for (int i = 0; i < Size; i++) {
				int val = Integer.parseInt(table.get(i).get(key));// The value form the table to be compared with the
																	// condition
				if (val < condVal) {
					table.remove(i);
					Size--;
					i--;
					changedCounter++;
				}
			}
		} else if (op == 2) {
			String condVal = value;// the condition's value
			for (int i = 0; i < Size; i++) {
				String val = table.get(i).get(key.toLowerCase());// The value form the table to be compared with the condition
				if (val.equalsIgnoreCase(condVal)) {
					table.remove(i);
					Size--;
					i--;
					changedCounter++;
				}
			}
		} else {
			table.clear();
			changedCounter = Size;
			Size = 0;
		}
		int c = changedCounter;
		changedCounter = 0;
		return c;
	}

	public int Update(String key, String value, int op, Map<String, String> map) {
		boolean b = checkValid(map);
		if (b) {
			//no condition so update every row
			if (op == -1) {
				Set<String> set = map.keySet();
				for (String str : set) {
					if (!validColumns.containsKey(str)) {
						return 0;
					}
				}
				for (Map<String, String> m : table) {
					for (String str : set) {
						m.put(str, map.get(str));
					}
				}
				return Size;
				//>
			} else if (op == 0) {
				Set<String> set = map.keySet();
				for (String str : set) {
					if (!validColumns.containsKey(str)) {
						return 0;
					}
				}
				for (Map<String, String> m : table) {
					boolean changed = false;
					for (String str : set) {
						int condVal = Integer.parseInt(value); // the condition's value
						int val = Integer.parseInt(m.get(key));// The value form the table to be compared with the condition
						if (val > condVal) {
							m.put(str, map.get(str));
							changed = true;
						}
					}
					if (changed) {
						changedCounter++;
					}
				}
				int c = changedCounter;
				changedCounter = 0;
				return c;
				//<
			} else if (op == 1) {
				Set<String> set = map.keySet();
				for (String str : set) {
					if (!validColumns.containsKey(str)) {
						return 0;
					}
				}
				for (Map<String, String> m : table) {
					boolean changed = false;
					for (String str : set) {
						int condVal = Integer.parseInt(value); // the condition's value
						int val = Integer.parseInt(m.get(key));// The value form the table to be compared with the condition
						if (val < condVal) {
							m.put(str, map.get(str));
							changed = true;
						}
					}
					if (changed) {
						changedCounter++;
					}
				}
				int c = changedCounter;
				changedCounter = 0;
				return c;
			}
			//=
			else if (op == 2) {
				Set<String> set = map.keySet();
				for (String str : set) {
					if (!validColumns.containsKey(str)) {
						return 0;
					}
				}
				for (Map<String, String> m : table) {
					boolean changed = false;
					for (String str : set) {
						String condVal = value.toLowerCase(); // the condition's value
						String val = m.get(key.toLowerCase());// The value form the table to be compared with the condition
						if (val.equals(condVal)) {
							m.put(str, map.get(str));
							changed = true;
						}
					}
					if (changed) {
						changedCounter++;
					}
				}
				int c = changedCounter;
				changedCounter = 0;
				return c;
			} else
				return 0;
		} else
			return 0;
	}

	/* Selects specific part of the table */
	public ArrayList<Map<String, String>> select(String[] Columns, String Condition) {

		ArrayList<Map<String, String>> result = new ArrayList<Map<String, String>>();

		/* First checks if there isn't a condition */
		if (Condition == null) {
			if (Columns.length == 1 && Columns[0].equals("*"))
				return this.table;

			for (int i = 0; i < this.table.size(); i++) {
				Map<String, String> element = new LinkedHashMap<String, String>();
				Map<String, String> current = this.table.get(i);

				for (int j = 0; j < Columns.length; j++) {
					if (current.containsKey(Columns[j])) {
						element.put(Columns[j], current.get(Columns[j]));
					}
				}

				result.add(element);
			}
			return result;
		}

		if (!validCondition(Condition))
			return null;

		String[] strings = parseCondition(Condition);

		String wantedColumn = strings[1].toLowerCase(), wantedValue = strings[2].toLowerCase();
		if (!validColumns.containsKey(wantedColumn))
			return null;

		for (int i = 0; i < this.table.size(); i++) {
			Map<String, String> current = this.table.get(i);

			boolean flag = false;
			switch ((int) Double.parseDouble(strings[0])) {
			/* GREATER THAN case */
			case 0:

				if (Double.parseDouble(current.get(wantedColumn)) > Double.parseDouble(wantedValue))
					flag = true;
				break;

			/* LESS THAN case */
			case 1:
				if (Double.parseDouble(current.get(wantedColumn)) < Double.parseDouble(wantedValue))
					flag = true;
				break;

			/* EQUAL case */
			case 2:
				wantedValue = wantedValue.replace("'", "");
				if (current.get(wantedColumn).equals(wantedValue))
					flag = true;
				break;
			}

			if (flag) {
				if (Columns.length == 1 && Columns[0].equals("*")) {
					result.add(current);
				} else {
					Map<String, String> element = new LinkedHashMap<String, String>();
					for (int j = 0; j < Columns.length; j++) {
						if (current.containsKey(Columns[j])) {
							element.put(Columns[j], current.get(Columns[j]));
						}
					}

					result.add(element);
				}
			}

		}
		if(result.isEmpty()) {
			result = null;
		}
		return result;
	}

	// Method to check validity of row before adding it to table
	private boolean checkValid(Map<String, String> map) {

		if (map == null) {
			return false;
		}
		
		if (map.size() > validColumns.size())
			return false;

		for (String s : map.keySet()) {
			// Checking keys with ignoring case by lowering case all characters
			if (!validColumns.containsKey(s)) {

				return false;
			}

			// If a string check if it between columns or int check if no columns
			String type = validColumns.get(s);
			if (type.equalsIgnoreCase("varchar") && map.get(s).matches("\\d+") ) {
				return false;
			}
			if (type.equalsIgnoreCase("int") && !map.get(s).matches("\\d+") ) {
				return false;
			}
		}
		return true;
	}

	public String[] parseCondition(String condition) {

		String[] results = new String[3];
		String[] split;
		
		if (condition == null) {
			results[0] = "-1";
			return results;
		}
		else if (condition.contains(">")) {
			results[0] = "0";
			split = condition.split(">");
			results[1] = split[0].trim();
			results[2] = split[1].trim();
			return results;
		} else if (condition.contains("<")) {
			results[0] = "1";
			split = condition.split("<");
			results[1] = split[0].trim();
			results[2] = split[1].trim();
			return results;
		} else if (condition.contains("=")) {
			results[0] = "2";
			split = condition.split("=");
			results[1] = split[0].trim();
			results[2] = split[1].trim();
			return results;
		}
		return null;
	}

	private boolean validCondition(String condition) {

		String[] strings = parseCondition(condition);

		String wantedColumn = strings[1];
		String wantedValue = strings[2];

		if (this.validColumns.containsKey(wantedColumn.toLowerCase())) {
			if (wantedValue.contains("'")) {
				Pattern pattern = Pattern.compile("varchar", Pattern.CASE_INSENSITIVE);
				Matcher m = pattern.matcher(this.validColumns.get(wantedColumn.toLowerCase()));
				if (!m.find())
					return false;
			} else {
				Pattern pattern = Pattern.compile("int", Pattern.CASE_INSENSITIVE);
				Matcher m = pattern.matcher(this.validColumns.get(wantedColumn.toLowerCase()));
				if (!m.find())
					return false;
			}
		} else {
			return false;
		}

		return true;

	}

	// Getters and Setters
	public ArrayList<Map<String, String>> getTable() {
		return this.table;
	}
	
	public void setTable (ArrayList<Map<String, String>> table) {
		this.table = table;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setsize (int size) {
		this.Size = size ;
	}
	public String getName() {
		return this.name;
	}

	public ArrayList<String> getValidColumnsArray() {
		ArrayList<String> columns = new ArrayList<String>();
		Set<String> validColumnsArray = validColumns.keySet();
		for (String s : validColumnsArray) {
			columns.add(s.toLowerCase());
		}
		return columns;
	}
	
	public Map<String, String> getValidColumnsMap() {
		return this.validColumns;
	}

	public ArrayList<String> getColumnsCasePreserved() {
		return columnsCasePreserved;
	}

	public void setColumnsCasePreserved(ArrayList<String> columnsCasePreserved) {
		this.columnsCasePreserved = columnsCasePreserved;
	}
}
