/*
 * This file is part of pattern recognition network (prNet), a program to find patterns in data-structures
 * Copyright (C) 2022  Elija Giesbrecht
 * Published under GPLv3-or-later license
 */
package prNet;

import java.util.ArrayList;
import java.util.Arrays;

public class Assigner<Key, Value> {
	
	static class Reference<Key, Value> {
		private Key key;
		private ArrayList<Value> val;
		
		public Reference(Key key, Value val) {
			this.key=key;
			this.val=new ArrayList<>();
			this.val.add(val);
		}
		
		public Reference(Key key, @SuppressWarnings("unchecked") Value...val) {
			this.key=key;
			this.val=new ArrayList<>(Arrays.asList(val));
		}
		
		public Key getKey() {
			return this.key;
		}
		
		public Value getValue() {
			return this.val.get(0);
		}
		
		public ArrayList<Value> getValues() {
			return this.val;
		}
	}
	
	private ArrayList<Reference<Key, Value>> memory;
	
	public Assigner() {
		this.memory=new ArrayList<>();
	}
	
	
	public void assign(Key key, Value val) {
		this.memory.add(new Reference<>(key, val));
	}
	
	void assign(Reference<Key, Value> ref) {
		this.memory.add(ref);
	}
	
	
	public Value read(Key key) {
		for(Reference<Key, Value> c:this.memory) {
			if(c.key.equals(key)) return c.getValue();
		}
		return null;
	}
	
	Reference<Key, Value> get(Key key) {
		for(Reference<Key, Value> c:this.memory) {
			if(c.key.equals(key)) return c;
		}
		return null;
	}
}
