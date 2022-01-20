package language;

import java.io.Serializable;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Prop implements Serializable {
	private static final long serialVersionUID=2044486433658850566L;
	
	//im grunde nur ein Array, aber auf "Eigenschaften" zugeschnitten
	private String rProps;
		
	public Prop(String props) {
		//rProps=props;
		String[] ignore=language.Conf.propIgnore;
		for(int i=0; i<ignore.length; i++) {
			if(props.contains(ignore[i]+":")) {props=props.replace(ignore[i]+":", "");}
			else if(props.contains(":"+ignore[i])) {props=props.replace(":"+ignore[i], "");}
		}
		rProps=props;
		//rParent=parent;
	}
	
	public String get(int p) {
		return rProps.split(":")[p];
	}
	
	public String[] get() {
		return rProps.split(":");
	}
	
	public String getType() {
		return rProps.split(":")[0];
	}
	
	public boolean contains(String in) {
		return rProps.contains(in);
	}
	
	public boolean contains(Prop in) {
		return rProps.contains(in.toString());
	}
	
	public String toString() {
		return rProps;
	}
	
	public boolean equals(Prop in) {
		return rProps.equals(in.toString());
	}
	
	public boolean equals(Object obj) {
		if(this==obj) return true;
		if(obj instanceof Prop) {
			Prop p=((Prop) obj);
			return rProps.equals(p.toString());
		}
		return false;
	}
	
	public double similarTo(Prop in) {
		int count=0;
		String[] elements=this.get();
		for(String cur:elements) {
			if(in.contains(cur)) count++;
		}
		double ll=100d/elements.length;
		return ll*count;
	}

	
	public void manipulate(List<String> set) {
		String elem=set.get(0);
		for(int i=1; i<set.size(); i++) {
			elem+=":"+set.get(i);
		}
		rProps=elem;
	}

	
	public void manipulatePosition(String set, int position) {
		String[] elem=rProps.split(":");
		String rep="";
		if(position==0) {
			rep+=set;
		}else {
			rep+=elem[0];
		}
		
		for(int i=1; i<elem.length; i++) {
			if(position==i) {
				rep+=":"+set;
			}else {
				rep+=":"+elem[i];
			}
		}
		
		rProps=rep;
	}
	
	
	public List<String> getManipulateableElements() {
		return new ArrayList<String>(Arrays.asList(this.get()));
	}
	
	/**
	 * Use the first index (0) of the object-array helpObjs as a java.sql.Statement!<p>
	 * Note: Manipulation based on the association cannot be done without a specification of the other
	 * parts like word and grammar! This is the reason why this method only uses grammar-specification.
	 * This problem can be solved by using a sql-query that asks for multiple parameters instead of just one.
	 * This won't be done here because it would need multiple tables and would have a longer query-time. 
	 */
	
	public void rebuildParent(Word parent, Object...helpObjs) {
		if(!(helpObjs[0] instanceof Statement)) {
			System.out.println("helpObjs[0] isn't a Statement!");
			return;
		}
		Statement stat=(Statement) helpObjs[0];
		
		try {
			String inf=parent.getInf();
			Prop prop=parent.getProp();
			boolean exists=stat.executeQuery("select exists(select word from dictionary where infinitiv='"+inf+"' and properties='"+prop.toString()+"')").getBoolean(1);
			if(exists) {
				String r=stat.executeQuery("select word from dictionary where infinitiv='"+inf+"' and properties='"+prop.toString()+"'").getString(1);
				parent.setWord(r);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
