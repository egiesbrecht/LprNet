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

public class InferedBlobHandler <ObjectType extends Serializable> implements Serializable{
	private static final long serialVersionUID=-1132246475462282839L;
	
	//implements Serializable muss auf alle genutzten Klassen angewendet werden
	private Connection conn;
	public InferedBlobHandler(Connection pConn) {
		conn=pConn;
	}
	
	public void insert(ObjectType obj, String sqlcondition/*UNBEDINGT an Sytax halten!*/) throws IOException, SQLException {
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
	
	public List<ObjectType> get(String sqlcondition/*MUSS das Blob-Feld zur�ckgeben!*/) throws SQLException, ClassNotFoundException, IOException {
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
