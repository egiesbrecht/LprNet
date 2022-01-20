/*
 * This file is part of pattern recognition network (prNet), a program to find patterns in data-structures
 * Copyright (C) 2022  Elija Giesbrecht
 * Published under GPLv3-or-later license
 */
package prNet;

import java.io.Serializable;
import java.util.List;

/**
 * A basic pattern that represents a list of elements that was found somewhere multiple times.<p>
 * This kind of pattern doesn't support a counter because it isn't always necessary.
 * For a kind of pattern that does support such mechanics see {@link prNet.Pattern}.
 * 
 * @author Elija Giesbrecht
 *
 * @param <T> The type of elements which represent this pattern
 * @see prNet.Pattern
 */
public class BasePattern<T> extends Assigner<BasePattern<T>, List<T>> implements Serializable {
	
	private static final long serialVersionUID=-3920489365223331348L;

	private List<T> gelements;
	
	private Comparison<T, T> gComparison;
	
	/**
	 * Creates a BasePattern with a list of elements and a {@link Comparison} to compare two elements with each other.
	 * 
	 * @param elements
	 * @param Comparison
	 */
	public BasePattern(List<T> elements, Comparison<T, T> Comparison) {
		super();
		gelements=elements;
		gComparison=Comparison;
	}
	
	public BasePattern(BasePattern<T> base) {
		super();
		gelements=base.gelements;
		gComparison=base.gComparison;
	}
	
	
	/**
	 * @return All elements of this BasePattern
	 */
	public List<T> getElements() {
		return gelements;
	}
	
	public void setElements(List<T> elements) {
		gelements=elements;
	}
	
	
	/**
	 * @return The size of the pattern
	 */
	public int size() {
		return gelements.size();
	}
	
	
	/**
	 * @return The {@link Comparison} of this BasPattern
	 */
	public Comparison<T, T> getComparison() {
		return gComparison;
	}
	
	public String toString() {
		return gelements.toString();
	}
	
	/**
	 * Checks if all elements of this BasePattern and the input are equal.
	 * 
	 * @param in The input BasePattern
	 * @return If all elements are equal
	 */
	public boolean checkElements(List<T> in) {
		if(gelements.size()!=in.size()) return false;
		for(int i=0; i<in.size(); i++) {
			if(in.get(i)==null && gelements.get(i)==null);
			else if(in.get(i)==null || gelements.get(i)==null) return false;
			else if(gComparison.negate().compare(in.get(i), gelements.get(i))) return false;
		}
		return true;
	}
	
	
	/**
	 * No need to say what happens here.
	 */
	public boolean equals(Object obj) {
		if(this==obj)
			return true;
		if(obj instanceof BasePattern) {
			@SuppressWarnings("unchecked")
			BasePattern<T> rp=(BasePattern<T>) obj;
			return
				this.gComparison==rp.getComparison() &&
				this.checkElements(rp.getElements());
		}
		return false;
	}
	
	
	public void addNullAsFirst() {
		//adds null as the first element
		if(gelements.get(0)!=null) {
			int bs=gelements.size();
			gelements.add(null);
			
			for(int i=bs; i>0; i--) {
				gelements.set(i, gelements.get(i-1));
			}
			
			gelements.set(0, null);
		}
	}
	
	public void addNullAsLast() {
		//adds null as the last element
		if(gelements.get(gelements.size()-1)!=null)
			gelements.add(null);
	}
	
	
	public boolean match(List<T> toCompare) {
		return PatternUsage.match(toCompare, this);
	}
	
	public boolean match(BasePattern<T> toCompare) {
		return PatternUsage.match(toCompare, this);
	}
}
