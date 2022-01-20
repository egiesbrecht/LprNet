package demo.language;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedList;
import java.util.List;

import ext.General;
import language.Prop;
import language.SQLlanguageOperations;
import language.Text;
import language.Word;
import prNet.Comparison;
import prNet.Pattern;
import prNet.PatternSearch;

public class Testing {
	public static void main(String[]args) throws ClassNotFoundException, SQLException, IOException {
		Class.forName("org.sqlite.JDBC");
    	Connection conn=DriverManager.getConnection("jdbc:sqlite:prNet-1.5-uniques.db");
    	Statement stat=conn.createStatement();
    	
    	String[] allAuthors=new String[] {
    			"Anant Agarwala", 
    			"Johanna Adorjan", 
    			"Saskia Aleythe",
    			"Olaleye Akintola"
    		};
    	
    	
	}
	
	static class PatternHost<H, T> {
		private H host;
		private List<Pattern<T>> patterns;
		
		public PatternHost(H host) {
			this.host=host;
			this.patterns=new LinkedList<>();
		}
		
		public PatternHost(H host, List<Pattern<T>> patterns) {
			this.host=host;
			this.patterns=patterns;
		}
		
		public PatternHost(H host, List<T> list, List<List<T>> toBeComparedWith, Comparison<T, T> com) {
			this.host=host;
			this.patterns=PatternSearch
					.castToPatterns(PatternSearch
					.findPatternsInOrder(list, toBeComparedWith, com), com);
		}
		
		public H getHost() {
			return this.host;
		}
		
		public List<Pattern<T>> getPatterns() {
			return this.patterns;
		}
	}
}
