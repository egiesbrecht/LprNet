/*
 * This file is part of pattern recognition network (prNet), a program to find patterns in data-structures
 * Copyright (C) 2022  Elija Giesbrecht
 * Published under GPLv3-or-later license
 */
package prNet.manipulable;

public interface Transition<T> {
	
	/**
	 * Applies the given lambda-expression to manipulate an element.
	 * 
	 * @param toChange The element that should be manipulated
	 * @param patternPart The reference of a pattern that contains the new information that changes the toChange variable
	 */
	public void apply(T toChange, T patternPart);
	
}
