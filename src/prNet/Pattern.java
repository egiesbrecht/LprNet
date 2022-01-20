/*
 * This file is part of pattern recognition network (prNet), a program to find patterns in data-structures
 * Copyright (C) 2022  Elija Giesbrecht
 * Published under GPLv3-or-later license
 */
package prNet;

import java.io.Serializable;
import java.util.List;

/**
 * An implementation of {@link prNet.BasePattern} which supports a simple counting of how often this pattern occurred.
 * 
 * @author Elija Giesbrecht
 *
 * @param <T> The type of elements
 */
public class Pattern<T> extends BasePattern<T> implements Serializable {
	
	private static final long serialVersionUID=2927826202072615733L;
	
	private int defaultCount;
	
	/**
	 * Creates a pattern.
	 * 
	 * @param elements The elements of the @{link BasePattern} this Pattern is based on
	 * @param aspect The {@link Comparison} that is used to compare two elements
	 */
	public Pattern(List<T> elements, Comparison<T, T> aspect) {
		super(elements, aspect);
	}
	
	/**
	 * Increases the counter by one
	 */
	public void addDefaultCount() {
		defaultCount++;
	}
	
	/**
	 * @return The current state of the counter
	 */
	public int getDefaultCount() {
		return defaultCount;
	}
	
	/**
	 * @return The {@link BasePattern} this object is based on
	 */
	public BasePattern<T> getRawType() {
		return this;
	}
}
