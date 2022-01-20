/*
 * This file is part of pattern recognition network (prNet), a program to find patterns in data-structures
 * Copyright (C) 2022  Elija Giesbrecht
 * Published under GPLv3-or-later license
 */
package demo;

import java.util.ArrayList;
import java.util.List;

import demo.TwoDimensionalMatrix.Node;

import prNet.ArrangeableToList;

/**
 * Just a little structure for testing.<p>
 * This is a quadratic two-dimensional-matrix that contains an integer at every position.
 * This integer is meant to be a 0 or a 1 to represent a boolean value. I chose integers over
 * boolean because it is easier to visualize.
 * 
 * @author Elija Giesbrecht
 *
 */

public class TwoDimensionalMatrix implements ArrangeableToList<Node> {
	
	private Node[][] structure;
	
	private static int size;
	
	/**
	 * Creates a quadratic matrix of the given size.
	 * 
	 * @param size The size of the array
	 */
	public TwoDimensionalMatrix(int size) {
		TwoDimensionalMatrix.size=size;
		this.structure=new Node[size][size];
		for(int row=0; row<size; row++) {
			for(int line=0; line<size; line++) {
				structure[row][line]=new Node(row, line, 0);
			}
		}
	}
	
	
	/**
	 * A node of a quadratic matrix that contains a specified value and its position.
	 * 
	 * @author Elija Giesbrecht
	 */
	public static class Node {
		int x, y, state;
		
		/**
		 * Creates a node with a specified value. This value is meant to be
		 * a 1 or a 0 but it can also be set to another integer.
		 * 
		 * @param x The position of the first dimension
		 * @param y The position of the second dimension
		 * @param state
		 */
		public Node(int x, int y, int state) {
			if(x>=size || y>=size) {
				throw new IndexOutOfBoundsException();
			}
			this.x=x;
			this.y=y;
			this.state=state;
		}
		
		public int getXPos() {
			return this.x;
		}
		
		public int getYPos() {
			return this.y;
		}
		
		public int getState() {
			return this.state;
		}
		
		public String toString() {
			return "["+x+", "+y+", "+state+"]";
		}
	}
	
	
	public int getState(int x, int y) {
		return structure[x][y].state;
	}
	
	
	public void setState(int x, int y, int state) {
		structure[x][y].state=state;
	}
	
	
	/**
	 * Flips the content of a Node from a 0 to a 1 or the other way around.
	 * 
	 * @param x The first dimension value
	 * @param y The second dimension value
	 */
	public void flipState(int x, int y) {
		int currentState=structure[x][y].state;
		if(currentState==0)
			structure[x][y].state=1;
		else if(currentState==1) {
			structure[x][y].state=0;
		}
	}
	
	
	public String toString() {
		String ret="";
		for(Node[] row:structure) {
			for(Node line:row) {
				ret+=line.getState()+" ";
			}
			ret+="\n";
		}
		return ret;
	}
	
	
	@Override
	public List<Node> arrange() {
		List<Node> ret=new ArrayList<>();
		for(Node[] row:structure) {
			for(Node line:row) {
				ret.add(line);
			}
		}
		return ret;
	}
	
}
