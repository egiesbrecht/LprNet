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

import language.LanguagePatternUsage;
import language.Word;
import prNet.AspectManager;
import prNet.AspectManager.Aspect;

public class AssignAuthors {
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
   
    	/**
    	 * 0: everything
    	 * 1: only the end-result
    	 * 2: nothing
    	 */
    	int printMode=1;
    	
    	/*
    	 * The default
    	 */
    	int calculationMode=2;
    	
    	/**
    	 * Ignores all self-references from the calculation
    	 */
    	boolean ignoreLow=true;
    	
    	for(Aspect<Word> aspect:AspectManager.getAllAspects(Word.class)) {
    		String asName=aspect.name();
    		System.out.println("Aspect \""+asName+"\":");
    		LanguagePatternUsage.matchesOfAllAuthors(allAuthors, asName, conn, ignoreLow, calculationMode, printMode);
    		System.out.println();
    	}
	}
}
