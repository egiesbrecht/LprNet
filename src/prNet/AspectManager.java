/*
 * This file is part of pattern recognition network (prNet), a program to find patterns in data-structures
 * Copyright (C) 2022  Elija Giesbrecht
 * Published under GPLv3-or-later license
 */
package prNet;

import java.nio.channels.AlreadyBoundException;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.function.Function;
import java.util.stream.Collectors;

public abstract class AspectManager {
	
	/**
	 * 
	 * @author Elija GGiesbrecht
	 *
	 */
	public record Aspect<T>(String name, Comparison<T, T> comparison, String table, Function<T, String> keyFunction) {}
	 
	private static LinkedList<Aspect<?>> elements=new LinkedList<>();
	
	
	public static <T> Aspect<T> define(String name, Class<T> classType, Comparison<T, T> comparison, String table, Function<T, String> keyFunction, Statement stat) throws SQLException {
		//check for an aspect with an equals name
		for(Aspect<?> cur:elements) {
			if(cur.name().equals(name)) {
				throw new AlreadyBoundException();
			}
		}
		
		stat.executeUpdate(
				  "create table if not exists \""+table+"\" ("
				+ "	\"key\"	TEXT,"
				+ "	\"author\" TEXT,"
				+ "	\"count\" INTEGER,"
				+ "	\"blob\" BLOB"
				+ ");");
		
		Aspect<T> ret=new Aspect<T>(name, comparison, table, keyFunction);
		elements.add(ret);
		return ret;
	}
	
	
	public static <T> Aspect<T> define(String name, Comparison<T, T> comparison, String table, Function<T, String> keyFunction, Statement stat) throws SQLException {
		//check for an aspect with an equals name
		for(Aspect<?> cur:elements) {
			if(cur.name().equals(name)) {
				throw new AlreadyBoundException();
			}
		}
		
		stat.executeUpdate(
				  "create table if not exists \""+table+"\" ("
				+ "	\"key\"	TEXT,"
				+ "	\"author\" TEXT,"
				+ "	\"count\" INTEGER,"
				+ "	\"blob\" BLOB"
				+ ");");
		
		Aspect<T> ret=new Aspect<T>(name, comparison, table, keyFunction);
		elements.add(ret);
		return ret;
	}
	
	
	@SuppressWarnings("unchecked")
	public static <T> Aspect<T> getAspect(String name) {
		for(Aspect<?> cur:elements) {
			if(cur.name().equals(name)) return (Aspect<T>) cur;
		}
		throw new NoSuchElementException();
	}
	
	@SuppressWarnings("unchecked")
	public static <T> Aspect<T> getAspect(Class<T> classType, String name) {
		for(Aspect<?> cur:elements) {
			if(cur.name().equals(name)) return (Aspect<T>) cur;
		}
		throw new NoSuchElementException();
	}
	
	
	@SuppressWarnings("unchecked")
	public static <T> Aspect<T> getAspect(int position) {
		return (Aspect<T>) elements.get(position);
	}
	
	@SuppressWarnings("unchecked")
	public static <T> Aspect<T> getAspect(Class<T> classType, int position) {
		return (Aspect<T>) elements.get(position);
	}
	
	
	@SuppressWarnings("unchecked")
	public static <T> List<Aspect<T>> getAllAspects() {
		return elements.stream()
				.map(c->(Aspect<T>) c)
				.collect(Collectors.toList());
	}
	
	@SuppressWarnings("unchecked")
	public static <T> List<Aspect<T>> getAllAspects(Class<T> classType) {
		return elements.stream()
				.map(c->(Aspect<T>) c)
				.collect(Collectors.toList());
	}

}
