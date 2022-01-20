/*
 * This file is part of pattern recognition network (prNet), a program to find patterns in data-structures
 * Copyright (C) 2022  Elija Giesbrecht
 * Published under GPLv3-or-later license
 */
package prNet;

import java.util.List;

/**
 * This interface makes it possible to analyze complex data structures like multi-dimensional matrices.
 * It arranges the structure as a list which then can be analyzed.<p>
 * The type of nodes in the returned list isn't necessary the same type as the nodes hold in the original structure.
 * 
 * @author Elija Giesbrecht
 *
 * @param <ElementType> The type of nodes hold in the data structure
 */
public interface ArrangeableToList<ElementType> {
	
	/**
	 * A method that arranges the structure of the current object as a list.
	 * @return A list of all nodes/elements of the current structure
	 */
	public List<ElementType> arrange();
	
}
