/*
 * This file is part of language pattern recognition network (LprNet), a program to find patterns in language like data-structures
 * Copyright (C) 2022  Elija Giesbrecht
 * Published under GPLv3-or-later license
 */
package language;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import ext.BlobHandler;
import ext.FileHandler;
import prNet.SQLoperations;

/**
 * A class that manages nearly all methods which use SQL.
 * 
 * @author Elija Giesbrecht
 */
public abstract class SQLlanguageOperations extends SQLoperations {

	public static List<Text> loadTextsByAuthor(Connection conn, String author) throws ClassNotFoundException, SQLException, IOException {
		return BlobHandler.get("select blob from texte where author='"+author+"'", conn);
	}
	
	public static List<Text> loadTextsByPath(Connection conn, String path) throws ClassNotFoundException, SQLException, IOException {
		return BlobHandler.get("select blob from texte where path='"+path+"'", conn);
	}
	
	
	public static List<Text> loadText(Connection conn, String name) throws ClassNotFoundException, SQLException, IOException {
		return BlobHandler.get("select blob from texte where name='"+name+"'", conn);
	}
	
	
	public static void rebuildWordBasedOnNewWord(Word parent, Statement stat) {
		try {
			boolean wordExists=stat.executeQuery("select exists(select * from dictionary where word='"+parent.getWord()+"')").getBoolean(1);
			if(wordExists) {
				ResultSet ars=stat.executeQuery("select * from dictionary where word='"+parent.getWord()+"'");
				parent.setInf(ars.getString(2));
				parent.setProp(new Prop(ars.getString(3)));
				
				attachAssoc(parent, stat);
			}else {
				parent.setInf(parent.getWord());
				parent.setAssociation("#undefined");
				parent.setProp(new Prop("#undefined"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	
	public static void rebuildWordBasedOnNewInfinitivAndGrammar(Word parent, Statement stat) {
		try {
			boolean infExists=stat.executeQuery("select exists(select * from dictionary where infinitiv='"+parent.getInf()+"' and properties='"+parent.getProp().toString()+"')").getBoolean(1);
			if(infExists) {
				ResultSet ars=stat.executeQuery("select * from dictionary where infinitiv='"+parent.getInf()+"' and properties='"+parent.getProp().toString()+"'");
				parent.setWord(ars.getString(1));
				
				attachAssoc(parent, stat);
			}else {
				parent.setWord("#undefined-for-infinitiv");
			}
			
		}catch(SQLException e) {
			e.printStackTrace();
		}
	}
	
	private static void attachAssoc(Word parent, Statement stat) throws SQLException {
		String assoc;
		boolean assocExists=stat.executeQuery("select exists(select association from associations where word='"+parent.getInf()+"')").getBoolean(1);
		if(assocExists) {
			assoc=stat.executeQuery("select association from associations where word='"+parent.getInf()+"'").getString(1);
		}else {
			assoc="#undefined";
		}
		parent.setAssociation(assoc);
	}
	
	public static List<Word> makeWords(Statement stat, String...inWord) throws SQLException {
		return makeWords(Arrays.asList(inWord), stat);
	}
	
	public static List<Word> makeWords(List<String> inWords, Statement stat) throws SQLException {
		List<Word> ret=new LinkedList<>();
		for(String aword:inWords) {
			String inf, prop, assoc;
			boolean wordExists=stat.executeQuery("select exists(select * from dictionary where word='"+aword+"')").getBoolean(1);
			if(wordExists) {
				ResultSet ars=stat.executeQuery("select * from dictionary where word='"+aword+"'");
				inf=ars.getString(2);
				prop=ars.getString(3);
				boolean assocExists=stat.executeQuery("select exists(select association from associations where word='"+inf+"')").getBoolean(1);
				if(assocExists) {
					assoc=stat.executeQuery("select association from associations where word='"+inf+"'").getString(1);
				}else {
					assoc="#undefined";
				}
				System.out.println("\""+aword+"\" gefunden");
			}else {
				inf=aword;
				prop="#undefined";
				assoc="#undefined";
				System.out.println("\""+aword+"\" ist nicht in der dictionary-Tabelle vorhanden");
			}
			ret.add(new Word(aword, inf, assoc, new Prop(prop)));
		}
		return ret;
	}
	
	
	public static List<Text> translateDir(String dirPath, String author, Statement stat) {
		List<Text> ret=new LinkedList<>();
		String[] paths=FileHandler.getAllFilePathsInDir(dirPath);
		System.out.println("Datein in \""+dirPath+"\":");
		for(String cp:paths) System.out.println("    "+cp);
		for(String file:paths) {
			file=file.replace("\\", "/");
			String[] namePart=file.split("/");
			String preName=namePart[namePart.length-1].replace(".", "%");
			String name=preName.split("%")[0];
			
			ret.add(translateFile(name, author, file, stat));
		}
		return ret;
	}
	
	
	public static Text translateFile(String name, String author, String path, Statement stat) {
		String content=FileHandler.read(path);
    	
    	content=content.toLowerCase();
		content=content.replace("!", "%");
		content=content.replace("?", "%");
		content=content.replace(".", "%");
		content=content.replace(",", " ");
		content=content.replace(":", " ");
		content=content.replace(";", " ");
		content=content.replace("-", " ");
		content=content.replace("(", " ( ");
		content=content.replace(")", " ) ");
		content=content.replace("\"", " \" ");
		content=content.replace("\n", " ");
				
		while(content.contains("  ")) content=content.replace("  ", " ");
		
		content=content.replace("% ", "%");
		content=content.replace(" %", "%");
		
		List<String> stage1=new LinkedList<>(Arrays.asList(content.split("%")));
		List<List<String>> stage2=new LinkedList<>();
		stage1.forEach(arg->stage2.add(Arrays.asList(arg.split(" "))));
		
		List<List<Word>> stage3=new LinkedList<>();
		stage2.forEach(arg->{
			try {stage3.add(SQLlanguageOperations.makeWords(arg, stat));}
			catch (SQLException e) {e.printStackTrace();}
		});
		
		return new Text(path, name, author, stage3, stat);
	}
}
