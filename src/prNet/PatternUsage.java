/*
 * This file is part of pattern recognition network (prNet), a program to find patterns in data-structures
 * Copyright (C) 2022  Elija Giesbrecht
 * Published under GPLv3-or-later license
 */
package prNet;

import java.io.IOException;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Spliterator;
import java.util.stream.Collectors;

import ext.General;
import prNet.AspectManager.Aspect;

/**
 * A collection of methods to use patterns.
 * 
 * @author Elija Giesbrecht
 *
 */
public abstract class PatternUsage {
	/**
	 * Checks if the first given pattern matches with the second given pattern.
	 * 
	 * @param <T> The type of elements in the list and pattern
	 * @param <P> The type of a pattern that extends the {@link BasePattern}
	 * @param environment The given {@link BasePattern} on which the check will be performed on
	 * @param pattern The {@link BasePattern} that will be checked
	 * @return If the pattern matches on the list
	 */
	public static <T, P extends BasePattern<T>> boolean match(P environment, P pattern) {
		Comparison<T, T> com=pattern.getComparison().and(environment.getComparison());
		List<T>
			patternEnv=new LinkedList<>(pattern.getElements()),
			env=new LinkedList<>(environment.getElements());
		
		for(T t:env) {
			if(t==null) throw new IllegalArgumentException("Argument cannot be null");
		}
		
		return match(env, patternEnv, com);
	}
	
	
	/**
	 * Checks if the given pattern matches with the given list.
	 * 
	 * @param <T> The type of elements in the list and pattern
	 * @param <P> The type of a pattern that extends the {@link BasePattern}
	 * @param environment The given {@link List} on which the check will be performed on
	 * @param pattern The {@link BasePattern} that will be checked
	 * @return If the pattern matches on the list
	 */
	public static <T, P extends BasePattern<T>> boolean match(List<T> environment, P pattern) {
		Comparison<T, T> com=pattern.getComparison();
		List<T>
			patternEnv=new LinkedList<>(pattern.getElements()),
			env=new LinkedList<>(environment);
		
		for(T t:env) {
			if(t==null) throw new IllegalArgumentException("Argument cannot be null");
		}
		
		return match(env, patternEnv, com);
	}
	
	private static <T> boolean match(List<T> env, List<T> patternEnv, Comparison<T, T> com) {
		while(env.size()>0 && patternEnv.size()>0) {
			if(patternEnv.get(0)==null) {
				T next=patternEnv.get(1);
				List<Integer> indices=PatternSearch.allIndicesOf(env, next, com);
				
				for(int c:indices) {
					List<T>
						newEnv=new LinkedList<>(env),
						newPatternEnv=new LinkedList<>(patternEnv);
					newPatternEnv.remove(0);
					PatternSearch.removeIndicesTillBorder(newEnv, c-1);
					
					if(match(newEnv, newPatternEnv, com)) return true;
				}
				
				return false;
			}else {
				if(!com.compare(env.get(0), patternEnv.get(0))) return false;
			}
			
			env.remove(0);
			patternEnv.remove(0);
		}
		
		return env.size()==0 && patternEnv.size()==0;
	}
	
	/**
	 * Finds all patterns in the given list.
	 * 
	 * @param <T> The type of elements in the list
	 * @param analyzableElements The list that contains analyzable content
	 * @param comparison The {@link Comparison} used to check if 2 elements are equal
	 * @return All found patterns
	 */
	public static <T> List<Pattern<T>> findPatterns(Comparison<T, T> comparison, List<List<T>> analyzableElements) {
		return PatternSearch.findPatterns(comparison, analyzableElements);
	}
	
	/**
	 * Finds all patterns in the given list.
	 * 
	 * @param <T> The type of elements in the list
	 * @param analyzableElements The list that contains analyzable content
	 * @param comparison The {@link Comparison} used to check if 2 elements are equal
	 * @return All found patterns
	 */
	@SafeVarargs
	public static <T> List<Pattern<T>> findPatterns(Comparison<T, T> comparison, ArrangeableToList<T>...analyzableElements) {
		return PatternSearch.findPatterns(comparison, analyzableElements);
	}
	
	/**
	 * Finds all patterns in the given list.
	 * 
	 * @param <T> The type of elements in the list
	 * @param analyzableElements The list that contains analyzable content
	 * @param comparison The {@link Comparison} used to check if 2 elements are equal
	 * @return All found patterns
	 */
	@SafeVarargs
	public static <T, S extends Spliterator<T>> List<Pattern<T>> findPatterns(Comparison<T, T> comparison, S...analyzableElements) {
		return PatternSearch.findPatterns(comparison, analyzableElements);
	}
	
	
	/**
	 * Finds and saves all patterns in the given 2-dimensional list in the database.
	 * 
	 * @param <T> The type of elements in the list and later in the patterns
	 * @param analyzeableElements The 2-dimensional list that will be analyzed
	 * @param author The author of the list
	 * @param stat The {@link Statement} that connects with the database
	 * @param aspect The aspect under which the list will be analyzed
	 * @throws SQLException
	 * @throws ClassNotFoundException
	 * @throws IOException
	 */
	public static <T extends Serializable> void findAndSavePatterns(List<List<T>> analyzeableElements, String author, Statement stat, Aspect<T> aspect) throws SQLException, ClassNotFoundException, IOException {
		List<Pattern<T>> patterns=PatternSearch.findPatterns(aspect.comparison(), analyzeableElements);
		
		stat.executeUpdate("begin");
		for(Pattern<T> cur:patterns) {
			//saves everything as multiple entries:
			//SQLs.savePattern(cur, text.getAuthor(), aspect, stat.getConnection());
			
			//saves everything as single entries:
			SQLoperations.savePatternAsUniques(cur, author, aspect, stat);
		}
		stat.executeUpdate("commit");
	}
	
	
	/**
	 * Creates a {@link String} that represents the pattern in the database.
	 * 
	 * @param <T>
	 * @param aspect The aspect of the pattern
	 * @param pattern The pattern to be represented
	 * @return A {@link String} that is used as a key in the database
	 */
	protected static <T> String createPatternKey(Aspect<T> aspect, Pattern<T> pattern) {
		List<T> elements;
		String ret;
		elements=pattern.getElements();
		if(elements.size()<=0) return "{}";
		ret="{"+aspect.keyFunction().apply(elements.get(0));
		for(int i=1; i<elements.size(); i++) {
			if(elements.get(i)==null) ret+=", *";
			else ret+=", "+aspect.keyFunction().apply(elements.get(i));
		}
		return ret+"}";
	}
	
	
	protected static int countAllPatterns(String author, String table, Statement stat) throws SQLException {
		int total=0;
		ResultSet rs=stat.executeQuery("select sum(count) from "+table+" where author='"+author+"'");
		while(rs.next()) {
			total+=rs.getInt(1);
		}
		return total;
	}
	
	
	/**
	 * This method finds all authors of patterns in the database which match with the given list of patterns.
	 * This can be done in 4 different ways which are represented with the "mode"-variable through the integers
	 * 0 to 3. The default is 2 and should always be used if you aren't sure you need another one.<p>
	 * The modes:<p>
	 * 0: counts the total amount of matching patterns in the database.<p>
	 * 1: counts the total amount of matching patterns in the database but only uses the highest of them later.<p>
	 * 2: counts the amount of patterns and then calculates the percentage of the matching pattern to all saved patterns by the
	 * author.<p>
	 * 3: counts the amount of patterns and then calculates the percentage of the matching pattern to all saved patterns by the
	 * author but only uses the highest of them later.<p><p>
	 * Note: the "author" is the creator the given patterns and often defined before. If you don't know what to use here,
	 * you should look up how vou've analyzed what ever you did to get the patterns that are used here in the text-
	 * parameter.
	 * 
	 * @param <T> The type of elements in the patterns
	 * @param text The {@link List} of pattern that will be compared with all authors in the database
	 * @param aspect The {@link Aspect} under which the patterns were analyzed
	 * @param conn The {@link Connection} to the database
	 * @param ignoreLow Decides if every pattern that was found only once should be ignored or not. This should be true if
	 * 	you test a structure that is already saved in the database because it's own patterns won't count in the result and therefore
	 * 	will be ignored afterwards. It's practically deletes all self-references.
	 * @param mode The above explained mode
	 * @param min The minimum count of a pattern needed in the database to be used in the calculation
	 * @return The result of the operation of the given mode
	 * @throws SQLException
	 */
	public static <T> Map<String, Double> findMatchingAuthors(List<Pattern<T>> text, Aspect<T> aspect, Connection conn, boolean ignoreLow, int mode/*Standard=2*/, int min) throws SQLException {
		return findMatchingAuthors(text, aspect, conn, ignoreLow, mode, min, -1);
	}
	
	/**
	 * This method finds all authors of patterns in the database which match with the given list of patterns.
	 * This can be done in 4 different ways which are represented with the "mode"-variable through the integers
	 * 0 to 3. The default is 2 and should always be used if you aren't sure you need another one.<p>
	 * The modes:<p>
	 * 0: counts the total amount of matching patterns in the database.<p>
	 * 1: counts the total amount of matching patterns in the database but only uses the highest of them later.<p>
	 * 2: counts the amount of patterns and then calculates the percentage of the matching pattern to all saved patterns by the
	 * author.<p>
	 * 3: counts the amount of patterns and then calculates the percentage of the matching pattern to all saved patterns by the
	 * author but only uses the highest of them later.<p><p>
	 * Note: the "author" is the creator the given patterns and often defined before. If you don't know what to use here,
	 * you should look up how vou've analyzed what ever you did to get the patterns that are used here in the text-
	 * parameter.
	 * 
	 * @param <T> The type of elements in the patterns
	 * @param text The {@link List} of pattern that will be compared with all authors in the database
	 * @param aspect The {@link Aspect} under which the patterns were analyzed
	 * @param conn The {@link Connection} to the database
	 * @param ignoreLow Decides if every pattern that was found only once should be ignored or not. This should be true if
	 * 	you test a structure that is already saved in the database because it's own patterns won't count in the result and therefore
	 * 	will be ignored afterwards. It's practically deletes all self-references.
	 * @param mode The above explained mode
	 * @return The result of the operation of the given mode
	 * @throws SQLException
	 */
	public static <T> Map<String, Double> findMatchingAuthors(List<Pattern<T>> text, Aspect<T> aspect, Connection conn, boolean ignoreLow, int mode/*Standard=2*/) throws SQLException {
		return findMatchingAuthors(text, aspect, conn, ignoreLow, mode, -1, -1);
	}
	
	/**
	 * This method finds all authors of patterns in the database which match with the given list of patterns.
	 * This can be done in 4 different ways which are represented with the "mode"-variable through the integers
	 * 0 to 3. The default is 2 and should always be used if you aren't sure you need another one.<p>
	 * The modes:<p>
	 * 0: counts the total amount of matching patterns in the database.<p>
	 * 1: counts the total amount of matching patterns in the database but only uses the highest of them later.<p>
	 * 2: counts the amount of patterns and then calculates the percentage of the matching pattern to all saved patterns by the
	 * author.<p>
	 * 3: counts the amount of patterns and then calculates the percentage of the matching pattern to all saved patterns by the
	 * author but only uses the highest of them later.<p><p>
	 * Note: the "author" is the creator the given patterns and often defined before. If you don't know what to use here,
	 * you should look up how vou've analyzed what ever you did to get the patterns that are used here in the text-
	 * parameter.
	 * 
	 * @param <T> The type of elements in the patterns
	 * @param text The {@link List} of pattern that will be compared with all authors in the database
	 * @param aspect The {@link Aspect} under which the patterns were analyzed
	 * @param conn The {@link Connection} to the database
	 * @param ignoreLow Decides if every pattern that was found only once should be ignored or not. This should be true if
	 * 	you test a structure that is already saved in the database because it's own patterns won't count in the result and therefore
	 * 	will be ignored afterwards. It's practically deletes all self-references.
	 * @param mode The above explained mode
	 * @param min The minimum count of a pattern needed in the database to be used in the calculation
	 * @param max The maximum count of a pattern needed in the database to be used in the calculation
	 * @return The result of the operation of the given mode
	 * @throws SQLException
	 */
	private static List<String> gTextKeys;
	public static <T> Map<String, Double> findMatchingAuthors(List<Pattern<T>> text, Aspect<T> aspect, Connection conn, boolean ignoreLow, int mode/*Standard=2*/, int min, int max) throws SQLException {
		String table=aspect.table();
		List<String> textKeys= gTextKeys= text.stream()
				.map(o->createPatternKey(aspect, o))
				.collect(Collectors.toList());
		
		HashMap<String, Double>
			authors=new HashMap<>(),
			authorsByPercent;
		
		Statement stat=conn.createStatement();
		
		//short for "ignore regular expressions", don't use this except you know what your doing!
		boolean ignoreReg=false;
		
		switch(mode) {
			//uses total count of patterns
			case 0:
				for(String key:textKeys) {
					General.addToMap(authors, findAuthors(key, table, stat, ignoreReg, ignoreLow, min, max), (a, b)->a+b);
				}
				
				stat.close();
				return General.sortMap(authors);
				
			//uses the highest of total count of patterns
			//edit: DON'T TOUCH THIS!
			case 1:
				for(String key:textKeys) {
					General.addToMap(authors, General.getHighest(findAuthors(key, table, stat, ignoreReg, ignoreLow, min, max)), (a, b)->a+b);
				}
				
				stat.close();
				return General.sortMap(authors);
				
			//calculates percentages
			case 2:
				for(String key:textKeys) {
					General.addToMap(authors, findAuthors(key, table, stat, ignoreReg, ignoreLow, min, max), (a, b)->a+b);
				}
				
				authorsByPercent=calcualtePercentage(authors, table, stat);
				
				stat.close();
				return authorsByPercent;
				
			//calculates percentages but only uses the highest
			//edit: DON'T TOUCH THIS!
			case 3:
				for(String key:textKeys) {
					General.addToMap(authors, General.getHighest(findAuthors(key, table, stat, ignoreReg, ignoreLow, min, max)), (a, b)->a+b);
				}
				
				authorsByPercent=calcualtePercentage(authors, table, stat);
				
				stat.close();
				return authorsByPercent;
				
			//returns an empty HashMap
			default:
				return authors;
		}
		
	}
	
	
	private static HashMap<String, Double> calcualtePercentage(HashMap<String, Double> authors, String table, Statement stat) throws SQLException {
		HashMap<String, Integer> totalAuthorsCount=new HashMap<>();
		
		for(Map.Entry<String, Double> cEntry:authors.entrySet()) {
			String cAuthor=cEntry.getKey();
			totalAuthorsCount.put(cAuthor, countAllPatterns(cAuthor, table, stat));
		}
		
		HashMap<String, Double> authorsByPercent=new HashMap<>();
		for(Map.Entry<String, Double> c:authors.entrySet()) {
			String cAuthor=c.getKey();
			double
				cTotalCount=totalAuthorsCount.get(cAuthor),
				cCurrentCount=c.getValue(),
				result=cCurrentCount/cTotalCount*100d;
			
			authorsByPercent.put(cAuthor, result);
		}
		
		return authorsByPercent;
	}
	
	
	private static HashMap<String, Double> findAuthors(String key, String table, Statement stat, boolean ignoreReg, boolean ignoreLow, int min, int max) throws SQLException {
		// An experimental option to filter low keys out, don't use this yet.
		boolean delLowVal=false;
		
		HashMap<String, Double> count=new HashMap<>();
		String nKey=ignoreReg? key.replace(", *", "").replace(", ", "%"): key;
		
		String extraCondition="";
		if(max>-1 && min>-1) {
			extraCondition="and count>="+min+" and count<="+max;
		}else if(min>-1) {
			extraCondition="and count>="+min;
		}
		
		ResultSet rs=stat.executeQuery("select author, count from "+table+" where key='"+nKey+"' "+extraCondition);
		while(rs.next()) {
			double pr=rs.getDouble(2);
			if(!ignoreLow && pr==0.0d) {
				pr++;
			}
			else if(pr==0.0d) continue;
			else if(delLowVal){
				if(gTextKeys.contains(key))
					pr--;
			}
			
			count.put(rs.getString(1), pr);
		}
		return count;
	}
}
