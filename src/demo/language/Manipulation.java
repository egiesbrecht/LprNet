/*
 * This file is part of language pattern recognition network (LprNet), a program to find patterns in language-like data-structures
 * Copyright (C) 2022  Elija Giesbrecht
 * Published under GPLv3-or-later license
 */
package demo.language;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.stream.Collectors;

import ext.General;
import language.LanguagePatternUsage;
import language.SQLlanguageOperations;
import language.Text;
import language.Word;
import prNet.AspectManager;
import prNet.BasePattern;
import prNet.Comparison;
import prNet.Pattern;
import prNet.AspectManager.Aspect;
import prNet.manipulable.ManipulablePattern;
import prNet.manipulable.ManipulationUsage;

public class Manipulation {
	public static void main(String[]args) throws SQLException, ClassNotFoundException, IOException {
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
    	
    	Aspect<Word> asp=AspectManager.getAspect(Word.class, "grammar");
    	Comparison<Word, Word> com=asp.comparison();
    	
    	List<BasePattern<Word>> lp=SQLlanguageOperations.getAllPatternsByAuthor(allAuthors[0], asp, conn);
    	/*lp.forEach(o->{
    		o.addNullAsFirst();
    		o.addNullAsLast();
    	});*/
    	
    	List<ManipulablePattern<Word>> lm=lp.stream()
    			.map(o->new ManipulablePattern<Word>(o.getElements(), (a, b)->a.getInf().equals(b.getInf()), (a, b)->{
    				a.setProp(b.getProp());
    				SQLlanguageOperations.rebuildWordBasedOnNewInfinitivAndGrammar(a, stat);
    			})).collect(Collectors.toList());
    	
    	
    	List<Text> la=SQLlanguageOperations.loadTextsByAuthor(conn, allAuthors[3]);
    	Text t=la.get(0);
    	
    	List<Pattern<Word>> pp=LanguagePatternUsage.findPatterns(com, t.getAllSentences());
    	General.print("before manipulation:");
    	LanguagePatternUsage.findMatchingAuthors(pp, asp, conn, false, 2).forEach((k, v)->General.print("\t"+k+" : "+v));
    	
    	try {
	    	for(var sentence:t.getAllSentences()) {
	    		sec:
	    		for(var pattern:lm) {
	    			if(ManipulationUsage.manipulate(sentence, pattern))
	    				break sec;
	    		}
	    	}
    	}catch(Exception e) {
    		e.printStackTrace();
    	}
    	
    	List<Pattern<Word>> pp2=LanguagePatternUsage.findPatterns(com, t.getAllSentences());
    	General.print("", "after manipulation:");
    	LanguagePatternUsage.findMatchingAuthors(pp2, asp, conn, false, 2).forEach((k, v)->General.print("\t"+k+" : "+v));
	}
}
