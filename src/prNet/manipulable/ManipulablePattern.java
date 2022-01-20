/*
 * This file is part of pattern recognition network (prNet), a program to find patterns in data-structures
 * Copyright (C) 2022  Elija Giesbrecht
 * Published under GPLv3-or-later license
 */
package prNet.manipulable;

import java.util.List;

import prNet.BasePattern;
import prNet.Comparison;
import prNet.PatternUsage;

public class ManipulablePattern<T> extends BasePattern<T> {

	private static final long serialVersionUID = 1L;

	private Comparison<T, T> clause;
	
	private Transition<T>  transition;
	
	public ManipulablePattern(List<T> base, Comparison<T, T> clause, Transition<T>  transition) {
		super(base, clause);
		
		/*
		//adds null as the first element
		if(base.get(0)!=null) {
			int bs=base.size();
			base.add(null);
			
			for(int i=bs; i>0; i--) {
				base.set(i, base.get(i-1));
			}
			
			base.set(0, null);
		}
		
		//adds null as the last element
		if(base.get(base.size()-1)!=null)
			base.add(null);
		*/
		
		this.clause=clause;
		this.transition=transition;
	}
	
	public Comparison<T, T> getManipulationClause() {
		return this.clause;
	}
	
	public Transition<T>  getTransition() {
		return this.transition;
	}
	
	public boolean match(List<T> in) {
		return PatternUsage.match(in, this);
	}
}
