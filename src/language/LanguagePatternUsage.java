package language;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Map;

import ext.General;
import prNet.*;
import prNet.AspectManager.*;

/**
 * A class that contains many methods used to analyze language under different aspects.This is only a collection
 * of those methods, not a way for a public use-case. This class extends the {@link PatternUsage} class and therefore
 * all methods of it can be called from here.
 * 
 * @author Elija Giesbrecht
 *
 */
public abstract class LanguagePatternUsage extends PatternUsage {
	
	public static int matchesOfAllAuthors(String[] authors, String aspect, Connection conn, boolean ignoreLow, int mode, int print) throws SQLException, ClassNotFoundException, IOException {
		return matchesOfAllAuthors(authors, aspect, conn, ignoreLow, mode, print, -1, -1);
	}
	
	public static int matchesOfAllAuthors(String[] authors, String aspect, Connection conn, boolean ignoreLow, int mode, int print, int min) throws SQLException, ClassNotFoundException, IOException {
		return matchesOfAllAuthors(authors, aspect, conn, ignoreLow, mode, print, min, -1);
	}
	
	public static int matchesOfAllAuthors(String[] authors, String aspect, Connection conn, boolean ignoreLow, int mode, int print, int min, int max) throws SQLException, ClassNotFoundException, IOException {
		int totalMatches=0;
		int totalTextCount=0;
		
		for(String author:authors) {
			if(print==0)
				System.out.println(author+"->");
			
			List<Text> aa=SQLlanguageOperations.loadTextsByAuthor(conn, author);
			totalTextCount+=aa.size();
			totalMatches+=matchesOfAllTextsByAuthor(author, aa, aspect, conn, ignoreLow, mode, print==0, min, max);
		}
		
		if(print==0 || print==1)
			General.print((print==0?"\n":"")+"Total matches:"+totalMatches+" of "+totalTextCount+" texts; "+(double) totalMatches/totalTextCount*100d+"%");
		
		return totalMatches;
	}
	
	public static int matchesOfAllTextsByAuthor(String author, String aspect, Connection conn, boolean ignoreLow, int mode, boolean print, int min, int max) throws SQLException, ClassNotFoundException, IOException {
		return matchesOfAllTextsByAuthor(author, SQLlanguageOperations.loadTextsByAuthor(conn, author), aspect, conn, ignoreLow, mode, print, min, max);
	}
	
	public static int matchesOfAllTextsByAuthor(String author, List<Text> aa, String aspect, Connection conn, boolean ignoreLow, int mode, boolean print, int min, int max) throws SQLException, ClassNotFoundException, IOException {
		int matchCount=0;
    	for(Text c:aa) {
    		Map<String, Double> res=findMatchingAuthors(c, AspectManager.getAspect(aspect), conn, ignoreLow, mode, min, max);
    		List<String> gl=General.getHighestAsList(res);
    		String highest=gl==null?"":gl.get(0);
    		
    		if(highest.equals(author)) 
    			matchCount++;
    		
    		if(print) 
    			General.print("\t"+highest, "\t\t->"+res);
		}
    	
    	if(print)
    		General.print("\n"+author+" matches:"+matchCount+" of "+aa.size()+" texts; "+(double) matchCount/aa.size()*100d+"%\n");
    	
    	return matchCount;
	}
	
	
	public static void deleteAllLanguageEntries(Statement stat) throws SQLException {
		List<Aspect<Word>> aspects=AspectManager.getAllAspects();
		String statm="delete from texte;";
		for(Aspect<Word> o:aspects)
			statm+="delete from "+o.name()+";";
		stat.executeUpdate(statm);
	}
	
	public static void deleteAllLanguageEntries(Connection conn) throws SQLException {
		Statement stat=conn.createStatement();
		deleteAllLanguageEntries(stat);
		stat.close();
	}
	
	
	public static void executeFullTranslation(String[] allAuthors, Connection conn) throws IOException, SQLException, ClassNotFoundException {
		for(String currentAuthor:allAuthors) {
    		translateAndSaveAuthor(currentAuthor, conn);
    	}
	}
	
	
	public static void executeFullAnalysis(String[] allAuthors, Connection conn) throws ClassNotFoundException, SQLException, IOException {
		Statement stat=conn.createStatement();
		for(String currentAuthor:allAuthors) {
			System.out.println("author: "+currentAuthor);
			List<Text> tl=SQLlanguageOperations.loadTextsByAuthor(conn, currentAuthor);
			for(Text o:tl) {
				findAndSavePatterns(o, stat, AspectManager.getAllAspects());
    		}
    	}
		stat.close();
	}
	
	
	private static void findAndSavePatterns(Text text, Statement stat, Aspect<Word> aspect) throws IOException, SQLException, ClassNotFoundException {
		System.out.println("analyze text \""+text.getName()+"\" with aspect \""+aspect.name()+"\"");
		findAndSavePatterns(text.getAllSentences(), text.getAuthor(), stat, aspect);
	}
	
	
	private static void findAndSavePatterns(Text text, Statement stat, List<Aspect<Word>> aspects) throws IOException, SQLException, ClassNotFoundException {
		for(Aspect<Word> cur:aspects) {
			findAndSavePatterns(text, stat, cur);
		}
	}
	
	
	private static Map<String, Double> findMatchingAuthors(Text text, Aspect<Word> aspect, Connection conn, boolean ignoreLow, int mode, int min, int max) throws SQLException {
		return findMatchingAuthors(PatternUsage.findPatterns(aspect.comparison(), text.getAllSentences()), aspect, conn, ignoreLow, mode, min, max);
	}
	
	
	
	public static String createSentenceKey(Aspect<Word> aspect, List<Word> elements) {
		String ret;
		if(elements.size()<=0) return "{}";
		ret="{"+aspect.keyFunction().apply(elements.get(0));
		for(int i=1; i<elements.size(); i++) {
			if(elements.get(i)==null) ret+=", *";
			else ret+=", "+aspect.keyFunction().apply(elements.get(i));
		}
		return ret+"}";
	}
	
	
	private static void translateAndSaveAuthor(String author, Connection conn) throws IOException, SQLException {
		Statement lstat=conn.createStatement();
		List<Text> l=SQLlanguageOperations.translateDir("Autoren/"+author.replace(" ", "")+"/", author, lstat);
		lstat.close();
		
		for(Text cur:l) {
			cur.insertIntoDB(conn);
		}
	}
}
