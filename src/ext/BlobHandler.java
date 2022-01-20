/*
 * This file is part of pattern recognition network (prNet), a program to find patterns in data-structures
 * Copyright (C) 2022  Elija Giesbrecht
 * Published under GPLv3-or-later license
 */
package ext;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public abstract class BlobHandler {
	
	//implements Serializable muss auf alle genutzten Klassen angewendet werden
	
	public static <ObjectType extends Serializable> void insert(ObjectType obj, String sqlcondition/*UNBEDINGT an Sytax halten!*/, Connection conn) throws IOException, SQLException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
	    ObjectOutputStream oos = new ObjectOutputStream(baos);
	    oos.writeObject(obj);
	    
	    byte[] employeeAsBytes = baos.toByteArray();
	    PreparedStatement pstmt = conn.prepareStatement(sqlcondition);
	    ByteArrayInputStream bais = new ByteArrayInputStream(employeeAsBytes);
	    pstmt.setBinaryStream(1, bais, employeeAsBytes.length);
	    pstmt.executeUpdate();
	    pstmt.close();
	}
	
	public static <ObjectType extends Serializable> List<ObjectType> get(String sqlcondition/*MUSS das Blob-Feld zurueckgeben!*/, Connection conn) throws SQLException, ClassNotFoundException, IOException {
		List<ObjectType> ret=new ArrayList<>();
		Statement stat=conn.createStatement();
		boolean exists=stat.executeQuery("select exists("+sqlcondition+")").getBoolean(1);
		if(!exists) return null;
		
		ResultSet rs = stat.executeQuery(sqlcondition);
	    while (rs.next()) {
	    	byte[] st = (byte[]) rs.getObject(1);
	    	ByteArrayInputStream baip = new ByteArrayInputStream(st);
	    	ObjectInputStream ois = new ObjectInputStream(baip);
	    	@SuppressWarnings("unchecked")
			ObjectType emp = (ObjectType) ois.readObject();
	    	ret.add(emp);
	    }
	    stat.close();
	    rs.close();
	    return ret;
	}
}
