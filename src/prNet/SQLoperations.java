/*
 * This file is part of pattern recognition network (prNet), a program to find patterns in data-structures
 * Copyright (C) 2022  Elija Giesbrecht
 * Published under GPLv3-or-later license
 */
package prNet;

import java.io.IOException;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import ext.BlobHandler;
import ext.InferedBlobHandler;
import prNet.AspectManager.Aspect;

public abstract class SQLoperations {
	
	public static <T extends Serializable> List<BasePattern<T>> getAllPatternsByAuthor(String author, Aspect<T> aspect, Connection conn) throws ClassNotFoundException, SQLException, IOException {
		return BlobHandler.get("select blob from "+aspect.table()+" where author='"+author+"'", conn);
	}
	
	
	public static <T extends Serializable> List<BasePattern<T>> getAllPatternsByAuthor(String author, Aspect<T> aspect, InferedBlobHandler<BasePattern<T>> ibh) throws ClassNotFoundException, SQLException, IOException {
		return ibh.get("select blob from "+aspect.table()+" where author='"+author+"'");
	}
	
	
	public static <T extends Serializable> void savePattern(Pattern<T> pattern, String author, Aspect<T> aspect, Connection conn) throws IOException, SQLException, ClassNotFoundException {
		BlobHandler.insert(pattern.getRawType(), "insert into "+aspect.table()+" values('"+PatternUsage.createPatternKey(aspect, pattern)+"', '"+author+"', "+pattern.getDefaultCount()+", ?)", conn);
	}
	
	
	public static <T extends Serializable> void savePatternAsUniques(Pattern<T> pattern, String author, Aspect<T> aspect, Statement stat) throws IOException, SQLException, ClassNotFoundException {
		String
			table=aspect.table(),
			key=PatternUsage.createPatternKey(aspect, pattern),
			baseExp="select count from "+table+" where key='"+key+"' and author='"+author+"'";
		
		if(!key.equals("{}")) {
			if(stat.executeQuery("select exists("+baseExp+")").getBoolean(1)) {
				int count=stat.executeQuery(baseExp).getInt(1)+pattern.getDefaultCount();
				stat.executeUpdate("update "+table+" set count="+count+" where key='"+key+"' and author='"+author+"'");
			}else {
				BlobHandler.insert(pattern.getRawType(), "insert into "+aspect.table()+" values('"+PatternUsage.createPatternKey(aspect, pattern)+"', '"+author+"', "+pattern.getDefaultCount()+", ?)", stat.getConnection());
			}
		}
	}
}
