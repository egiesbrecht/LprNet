package demo.language;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import language.LanguagePatternUsage;
import language.Word;
import prNet.AspectManager;

public class TranslateTexts {
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
		
    	AspectManager.define("word", Word.class, (a, b)->a.getWord().equals(b.getWord()), "word", a->a.getWord(), stat);
    	AspectManager.define("infinitiv", Word.class, (a, b)->a.getInf().equals(b.getInf()), "infinitiv", a->a.getInf(), stat);
    	AspectManager.define("association", Word.class, (a, b)->a.getAssociation().equals(b.getAssociation()), "association", a->a.getAssociation(), stat);
    	AspectManager.define("grammar", Word.class, (a, b)->a.getProp().equals(b.getProp()), "grammar", a->a.getProp().toString(), stat);
    	
    	LanguagePatternUsage.deleteAllLanguageEntries(conn);
    	LanguagePatternUsage.executeFullTranslation(allAuthors, conn);
    	LanguagePatternUsage.executeFullAnalysis(allAuthors, conn);
	}
}
