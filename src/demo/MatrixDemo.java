/*
 * This file is part of pattern recognition network (prNet), a program to find patterns in data-structures
 * Copyright (C) 2022  Elija Giesbrecht
 * Published under GPLv3-or-later license
 */
package demo;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import demo.TwoDimensionalMatrix.Node;
import prNet.Comparison;
import prNet.Pattern;
import prNet.PatternUsage;

/**
 * This class demonstrates how to find patterns in data structures.
 * It creates 6 2-dimensional matrices and then finds all patterns between them.
 * 
 * @author Elija Giesbrecht
 */
public class MatrixDemo {
	private static TwoDimensionalMatrix[] matrices;
	
	public static void main(String[]args) {
		int quantity=6;
		int matrixSize=5;
		
		//---create an array of matrices---
		matrices=new TwoDimensionalMatrix[quantity];
		for(int i=0; i<matrices.length; i++) 
			matrices[i]=new TwoDimensionalMatrix(matrixSize);
		//---------------------------------
		
		//---flips 20 states per matrix----
		// this makes the matrices pretty random
		int flipTime=20;
		for(var mat:matrices) {
			for(int i=0; i<flipTime; i++) {
				int x=randomNumber(matrixSize-1);
				int y=randomNumber(matrixSize-1);
				mat.flipState(x, y);
			}
		}
		//---------------------------------
		
		printMatrices();
		
		//define how two nodes of a matrix should be compared
		Comparison<Node, Node> comp=(a, b)->a.getState()==b.getState();
		
		//Find all patterns between the matrices and print them
		List<Pattern<Node>> foundPatterns=PatternUsage.findPatterns(comp, matrices);
		foundPatterns.forEach(System.out::println);
	}
	
	private static void printMatrices() {
		for(var mat:matrices)
			System.out.println(mat.toString());
	}
	
	private static int randomNumber(int max) {
		return ThreadLocalRandom.current().nextInt(0, max+1);
	}
}
