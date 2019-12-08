package eg.edu.alexu.csd.oop.jdbc.cs28;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.UIManager;
import javax.swing.text.BadLocationException;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

public class CommandLine {
	
	Driver myDriver = new MyDriver();
	Properties info = new Properties();
	Connection myConnection ;
	Statement myStatment;
	QueryParser qp = new QueryParser();
	File dir = new File("tests");

	public static void main(String[] args) {

		new CommandLine();
	}

	private JFrame frame;
	private JTextPane panel;
	private JTextField input;
	private JScrollPane scrollpane;
	private StyledDocument document;


	public CommandLine() {
		
		info.put("path", dir.getAbsoluteFile());
		try {
			myConnection = myDriver.connect("jdbc:xmldb://localhost", info);
			myStatment = myConnection.createStatement();
		} catch (SQLException e1) {
			e1.printStackTrace();
		}
		
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

		} catch (Exception ex) {
		}

		frame = new JFrame();
		frame.setTitle("Simple JDBC");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		panel = new JTextPane();
		panel.setEditable(false);
		panel.setFont(new Font("Courier New", Font.PLAIN, 12));
		panel.setOpaque(false);

		input = new JTextField();
		input.setEditable(true);
		input.setForeground(Color.WHITE);
		input.setCaretColor(Color.WHITE);
		input.setOpaque(false);
		input.setFont(new Font("Courier New", Font.PLAIN, 12));
		input.setName("Enter SQL Query Here");

		scrollpane = new JScrollPane(panel);
		scrollpane.setOpaque(false);
		scrollpane.getViewport().setOpaque(false);

		document = panel.getStyledDocument();
		Style instructionStyle = panel.addStyle("Instructions", null);
		StyleConstants.setForeground(instructionStyle, new Color(255,255,255));
		try {
			document.insertString(0, "Connected successfully... \n", instructionStyle);
			document.insertString(document.getLength(), "Create your database\n", instructionStyle);
		} catch (BadLocationException e1) {
			e1.printStackTrace();
		}
		
		
		
		frame.add(input, BorderLayout.SOUTH);
		frame.add(scrollpane, BorderLayout.CENTER);

		frame.getContentPane().setBackground(new Color(50, 50, 50));
		frame.setSize(660, 350);
		frame.setLocationRelativeTo(null);
		frame.setResizable(true);
		frame.setVisible(true);

		input.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				
				String text = input.getText();

				if (text.length() > 0) {
					
					if (text.equals("-1")) {
						try {
							myStatment.close();
							myConnection.close();
						} catch (SQLException e1) {
							e1.printStackTrace();
						}
						System.exit(0);
					}
					
					boolean correct;
					try {
						correct = qp.commandChooser(text, myStatment);
					}
					catch (Throwable error) {
						correct = false;
					}
		
					print(text + "\n", correct);
					scrollBottom();
					input.selectAll();
				}
			}

		});

	}

	public void scrollBottom() {
		panel.setCaretPosition(panel.getDocument().getLength());
	}

	public void print(String s, boolean correct) {
		Color green = new Color(127, 255, 0);
		Color red = new Color(255, 50, 0);

		Style styleCorrect = panel.addStyle("Style Correct", null);
		StyleConstants.setForeground(styleCorrect, green);

		Style styleWrong = panel.addStyle("Style Wrong", null);
		StyleConstants.setForeground(styleWrong, red);

		try {
			if (correct) {
				document.insertString(document.getLength(), s, styleCorrect);
			} else {
				document.insertString(document.getLength(), s, styleWrong);
			}
		} catch (Exception exp) {
		}
	}

}
