/*
 * This file is part of pattern recognition network (prNet), a program to find patterns in data-structures
 * Copyright (C) 2022  Elija Giesbrecht
 * Published under GPLv3-or-later license
 */
package ext;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public abstract class General {
	/*
	 * The print-methods are just some useful but not necessary methods to print more complex structures
	 */
	public static void print(Map<Object, Object> in) {
		in.forEach(General::print);
	}
	
	public static void print(List<Object> in) {
		in.forEach(General::print);
	}
	
	public static void print(Object...in) {
		for(Object o:in) System.out.println(o);
	}
	
	
	public static <KeyType, ValType extends Comparable<ValType>> Map<KeyType, ValType> sortMap(Map<KeyType, ValType> map) {
		List<Map.Entry<KeyType, ValType>> capitalList=new LinkedList<>(map.entrySet());
	    Collections.sort(capitalList, (l1, l2)->l1.getValue().compareTo(l2.getValue()));
	    Map<KeyType, ValType> result=new HashMap<>();
	    for(Map.Entry<KeyType, ValType> entry:capitalList) {
	    	result.put(entry.getKey(), entry.getValue());
	    }
	    return result;
	}
	
	
	public static interface valueTypeAddition <ValueType> {
		public ValueType add(ValueType val1, ValueType val2);
	}
	
	public static <KeyType, ValType> void addToMap(Map<KeyType, ValType> parent, Map<KeyType, ValType> child, valueTypeAddition<ValType> addition) {
		child.forEach((key, val)->{
			if(parent.containsKey(key)) {
				ValType curVal=parent.get(key);
				parent.put(key, addition.add(curVal, val));
			}else {
				parent.put(key, val);
			}
		});
	}
	
	
	public static <KeyType, ValType extends Object & Comparable<? super ValType>> Map<KeyType, ValType> getHighest(Map<KeyType, ValType> map) {
		if(map.values().size()==0) return map;
		ValType max=Collections.max(map.values());
		return map.entrySet().stream()
			.filter(entry->entry.getValue().equals(max))
			.collect(Collectors.toMap(entry->entry.getKey(), entry->entry.getValue()));
	}
	
	
	public static <KeyType, ValType extends Object & Comparable<? super ValType>> List<KeyType> getHighestAsList(Map<KeyType, ValType> map) {
		if(map.values().size()==0) return null;
		ValType max=Collections.max(map.values());
		return map.entrySet().stream()
			.filter(entry->entry.getValue().equals(max))
			.map(entry->entry.getKey())
			.collect(Collectors.toList());
	}
}
