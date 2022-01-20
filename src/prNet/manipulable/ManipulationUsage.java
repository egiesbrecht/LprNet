/*
 * This file is part of pattern recognition network (prNet), a program to find patterns in data-structures
 * Copyright (C) 2022  Elija Giesbrecht
 * Published under GPLv3-or-later license
 */
package prNet.manipulable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import prNet.BasePattern;
import prNet.Comparison;
import prNet.PatternUsage;

public abstract class ManipulationUsage extends PatternUsage {
	
	public static <T, P extends ManipulablePattern<T>> boolean manipulate(List<T> environment, P pattern) {
		return manipulate(environment, pattern, pattern.getManipulationClause(), pattern.getTransition());
	}
	
	public static <T, P extends BasePattern<T>> boolean manipulate(List<T> environment, P pattern, Comparison<T, T> manipulationClause, Transition<T> transition) {
		Comparison<T, T> com=manipulationClause;
		List<T>
			patternEnv=new LinkedList<>(pattern.getElements()),
			env=new LinkedList<>(environment);
		
		for(T t:env) {
			if(t==null) throw new IllegalArgumentException("Argument cannot be null");
		}
		
		return manipulate(env, patternEnv, com, transition);
	}
	
	private static <T> boolean manipulate(List<T> env, List<T> patternEnv, Comparison<T, T> com, Transition<T> transition) {
		HashMap<T, T> soFar=new HashMap<>();
		while(env.size()>0 && patternEnv.size()>0) {
			if(patternEnv.get(0)==null) {
				if(patternEnv.size()==1) return false;
				T next=patternEnv.get(1);
				List<Integer> indices=allIndicesOf(env, next, com);
				
				for(int c:indices) {
					List<T>
						newEnv=new LinkedList<>(env),
						newPatternEnv=new LinkedList<>(patternEnv);
					newPatternEnv.remove(0);
					removeIndicesTillBorder(newEnv, c-1);
					
					if(manipulate(newEnv, newPatternEnv, com, transition)) {
						applyManipulation(soFar, transition);
						return true;
					}
				}
				
				return false;
			}else {
				if(!com.compare(env.get(0), patternEnv.get(0))) return false;
				else soFar.put(env.get(0), patternEnv.get(0));
			}
			
			env.remove(0);
			patternEnv.remove(0);
		}
		
		applyManipulation(soFar, transition);
		return env.size()==0 && patternEnv.size()==0;
	}
	
	private static <T> void applyManipulation(HashMap<T, T> soFar, Transition<T> transition) {
		soFar.forEach(transition::apply);
	}
	
	private static <T> List<Integer> allIndicesOf(List<T> list, T toFind, Comparison<T, T> com) {
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
	
	private static <T> void removeIndicesTillBorder(List<T> list, int border) {
		for(int i=0; i<=border; i++) list.remove(0);
	}
}
