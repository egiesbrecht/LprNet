/*
 * This file is part of pattern recognition network (prNet), a program to find patterns in data-structures
 * Copyright (C) 2022  Elija Giesbrecht
 * Published under GPLv3-or-later license
 */
package prNet;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Queue;
import java.util.Spliterator;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * This class contains methods to find patterns in multiple or two-dimensional lists.
 * Some methods can be applied on simple two-dimensional lists other on complex data structures like matrices.<p>
 * 
 * This class either can be used like an abstract class or it can be used as an constructed object. It holds all processed
 * calculations in a queue and is accessible through declare, peek and poll methods.
 * 
 * @author Elija Giesbrecht
 * @see prNet.Pattern
 * @see prNet.BasePattern
 * @see prNet.ArrangeableToList
 * @see prNet.Comparison
 */
class PatternSearch<T> {
	
	private Queue<List<Pattern<T>>> occurences;
	
	private Comparison<T, T> comparison;
	
	/**
	 * Creates an object that can be used to perform pattern-finding operations
	 * in a specific order.<p>
	 * This should only be used if it's necessary because it uses more memory than
	 * a call of the methods directly.
	 * 
	 * @param comparisom The {@link Comparison} used to compare 2 elements
	 */
	public PatternSearch(Comparison<T, T> comparisom) {
		this.occurences=new LinkedList<>();
		this.comparison=comparisom;
	}
	
	
	public List<Pattern<T>> peek() {
		return occurences.peek();
	}
	
	
	public List<Pattern<T>> poll() {
		return occurences.poll();
	}
	
	
	public int countOperations() {
		return occurences.size();
	}
	
	
	public void declare(List<List<T>> input) {
		occurences.add(findPatterns(comparison, input));
	}
	
	@SuppressWarnings("unchecked")
	public void declare(List<T>...input) {
		occurences.add(findPatterns(comparison, Arrays.stream(input)
				.map(Objects::requireNonNull)
				.collect(Collectors.toList())));
	}
	
	@SuppressWarnings("unchecked")
	public void declare(ArrangeableToList<T>...input) {
		occurences.add(findPatterns(comparison, input));
	}
	
	
	@SuppressWarnings("unchecked")
	<S extends Spliterator<T>> void declare(S...input) {
		occurences.add(findPatterns(comparison, input));
	}
	
	
	/**
	 * This method finds all patterns in the given array of spliterators.
	 * 
	 * @param <T> The type of elements
	 * @param <S> The type of spliterator-objects
	 * @param comparison The lambda expression that will be used to compare 2 elements
	 * @param input A array of objects which implements the {@link ArrangeableToList}-interface
	 * @return All found patterns
	 * @see java.util.Spliterator
	 */
	@SafeVarargs
	static <T, S extends Spliterator<T>> List<Pattern<T>> findPatterns(Comparison<T, T> comparison, S...input) {
		List<List<T>> finInput=Arrays.stream(input)
				.map(o->
					StreamSupport.stream(o, false)
						.map(Objects::requireNonNull)
						.collect(Collectors.toList()))
				.map(Objects::requireNonNull)
				.collect(Collectors.toList());
		return findPatterns(comparison, finInput);
	}
	
	
	/**
	 * This method finds all patterns in the given array of rearrangeable objects.
	 * 
	 * @param <T> The type of elements
	 * @param comparison The lambda expression that will be used to compare 2 elements
	 * @param input A array of objects which implements the Arrangeable-interface
	 * @return All found patterns
	 * @see prNet.ArrangeableToList
	 */
	@SafeVarargs
	static <T> List<Pattern<T>> findPatterns(Comparison<T, T> comparison, ArrangeableToList<T>...input) {
		List<List<T>> finInput=Arrays.stream(input)
				.map(o->o.arrange())
				.map(Objects::requireNonNull)
				.collect(Collectors.toList());
		return findPatterns(comparison, finInput);
	}
	
	
	/**
	 * This method finds all patterns in the given list with the comparison-function.
	 * 
	 * @param <T> The type of elements
	 * @param input The 2-dimensional list of all analyzeable elements
	 * @param comparison The lambda expression that will be used to compare 2 elements
	 * @return All found patterns
	 */
	static <T> List<Pattern<T>> findPatterns(Comparison<T, T> comparison, List<List<T>> input) {
		List<Pattern<T>> ret=new LinkedList<>();
		for(int i=0; i<input.size(); i++) {
			List<T> currentItem=new LinkedList<>(input.get(i));
			List<List<T>> currentRest=new LinkedList<>(input);
			currentRest.remove(i);
			castToPatterns(findPatternsInOrder(currentItem, currentRest, comparison), comparison).forEach(arg->addPatternToList(ret, arg));
		}
		return ret;
	}
	
	
	/**
	 * Adds one count to the matching Pattern or adds a new one if none of
	 * the Patterns match.
	 * 
	 * @param list The list of Patterns.
	 * @param toAdd The Pattern that will be counted up or added.
	 */
	static <T> void addPatternToList(List<Pattern<T>> list, Pattern<T> toAdd) {
		for(Pattern<T> cur:list) {
			if(cur.equals(toAdd)) {
				cur.addDefaultCount();
				return;
			}
		}
		list.add(toAdd);
	}
	
	
	/**
	 * Checks if the given Pattern is contained in the given list.
	 * This method is only needed if the Pattern isn't able to be identified in a list.
	 * 
	 * @param <T> The parametertype of the Patterns
	 * @param list The list that is checked
	 * @param item The item that will be checked in the list
	 * @return If the item is contained in the list or not
	 */
	static <T> boolean contains(List<Pattern<T>> list, Pattern<T> item, Comparison<Pattern<T>, Pattern<T>> comparison) {
		for(Pattern<T> cur:list) {
			if(comparison.compare(cur, item)) return true;
		}
		return false;
	}
	
	
	/**
	 * Lists all indexes of the first list that appear in the second list in chronological order.
	 * 
	 * @param <T> The type of elements in the lists
	 * @param list1 The first list
	 * @param list2 The second list
	 * @param comparison The {@link Comparison} used to check if 2 elements of the type T are equal
	 * @return All list of all in common indexes
	 */
	static <T> List<Integer> matchingIndexesInOrder(List<T> list1,  List<T> list2, Comparison<T, T> comparison) {
		List<Integer> matches=new ArrayList<>();
		List<T> list2clone=new ArrayList<T>(list2);
		
		for(int i=0; i<list1.size(); i++) {
			T cur=list1.get(i);
			int nextInx=indexOf(list2clone, cur, comparison);
			if(nextInx==-1) {
				if(cur==null)
					matches.add(i);
			}else {
				matches.add(i);
				removeIndicesTillBorder(list2clone, nextInx);
			}
		}
		return matches;
	}
	
	
	static <T> List<List<T>> findPatternsInOrder(List<T> list1, List<List<T>> toCompareLists, Comparison<T, T> comparison) {
		List<List<T>> globret=new LinkedList<>();
		toCompareLists.forEach(list2->{
			List<List<T>> ret=new LinkedList<>();
			List<T> list1clone=new LinkedList<>(list2);
			while(list1clone.size()>0) {
				LinkedList<T> tempret=new LinkedList<>();
				LinkedList<T> list2clone=new LinkedList<>(list1);
				for(T l1:list1clone) {
					int pos=indexOf(list2clone, l1, comparison);
					if(pos==-1) {
						if(tempret.size()>0 && tempret.getLast()!=null) tempret.add(null);
					}else {
						tempret.add(list2clone.get(pos));
						removeIndicesTillBorder(list2clone, pos);
					}
				}
				if(!containsItem(ret, tempret, comparison)) ret.add(tempret);
				list1clone.remove(0);
			}
			ret.forEach(arg->{
				if(!contains(globret, arg, comparison)) globret.add(arg);
			});
		});
		
		return globret;
	}
	
	
	static <T> List<Pattern<T>> castToPatterns(List<List<T>> list, Comparison<T, T> comparison) {
		List<Pattern<T>> ret=new LinkedList<>();
		for(List<T> cur:list) ret.add(new Pattern<T>(cur, comparison));
		return ret;
	}
	
	
	static <T> int indexOf(List<T> list, T item, Comparison<T, T> lambda) {
		for(int i=0; i<list.size(); i++) {
			if(lambda.compare(list.get(i), item)) return i;
		}
		return -1;
	}
	
	
	static <T> List<Integer> allIndicesOf(List<T> list, T toFind, Comparison<T, T> com) {
		List<Integer> ret=new ArrayList<>();
		for(int i=0; i<list.size(); i++) {
			if(list.get(i)==null && toFind==null) ret.add(i);
			else if(list.get(i)==null || toFind==null);
			else {
				if(com.compare(list.get(i), toFind)) ret.add(i);
			}
		}
		return ret;
	}
	
	
	private static <T> boolean contains(List<List<T>> tl, List<T> ol, Comparison<T, T> lambda) {
		for(List<T> arg:tl) {
			if(equals(arg, ol, lambda)) return true;
		}
		return false;
	}
	
	
	private static <T> boolean equals(List<T> it1, List<T> it2, Comparison<T, T> lambda) {
		if(it1.size()!=it2.size()) return false;
		for(int i=0; i<it1.size(); i++) {
			if(it1.get(i)==null && it2.get(i)==null);
			else if(it1.get(i)==null || it2.get(i)==null) return false;
			else if(lambda.negate().compare(it1.get(i), it2.get(i))) return false;
		}
		return true;
	}
	
	
	private static <T> boolean containsItem(List<List<T>> list, List<T> item, Comparison<T, T> lambda) {
		for(List<T> a:list) {if(containsAll(a, item, lambda)) return true;}
		return false;
	}
	
	
	public static <T> boolean containsAll(List<T> list, List<T> toInclude, Comparison<T, T> lambda) {
		for(T con:toInclude) {
			if(!contains(list, con, lambda)) return false;
		}
		return true;
	}
	
	
	static <T> boolean contains(List<T> list, T item, Comparison<T, T> lambda) {
		for(T con:list) {
			if(item==null) {
				if(con==null) return true;
			}
			else if(con!=null){
				if(lambda.compare(con, item)) return true;
			}
		}
		return false;
	}
	
	
	static <T> void removeIndicesTillBorder(List<T> list, int border) {
		for(int i=0; i<=border; i++) list.remove(0);
	}
}
