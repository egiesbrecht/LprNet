/*
 * This file is part of language pattern recognition network (LprNet), a program to find patterns in language like data-structures
 * Copyright (C) 2022  Elija Giesbrecht
 * Published under GPLv3-or-later license
 */
package language;

import java.io.Serializable;

public class Word implements Serializable {
	private static final long serialVersionUID=-2022887738444474352L;
	
	private String
		word,
		inf,
		association,
		wordtype;
	
	private Prop prop;
	
	public Word(String pWord, String pInf, String pAssociation, Prop pProp) {
		word=pWord;
		inf=pInf;
		association=pAssociation;
		prop=pProp;
		wordtype=prop.getType();
	}
	
	public String getWord() {
		return word;
	}
	
	public void setWord(String in) {
		word=in;
	}
		
	public String getInf() {
		return inf;
	}
	
	public void setInf(String in) {
		inf=in;
	}
	
	public String getAssociation() {
		return association;
	}
	
	public void setAssociation(String in) {
		association=in;
	}
	
	public Prop getProp() {
		return prop;
	}
	
	public String getWordType() {
		return wordtype;
	}
	
	public void setProp(Prop in) {
		prop=in;
	}
	
	public String toString() {
		return "["+word+", "+inf+", "+association+", "+prop+"]";
	}
	
	public boolean equals(Word in) {
		return in.getWord().equals(word) && in.getInf().equals(inf) && in.getAssociation().equals(association) && in.getProp().equals(prop);
	}
	
	public boolean equals(Object obj) {
		if(this==obj) return true;
		if(obj instanceof Word) {
			Word w=(Word) obj;
			return w.getWord().equals(word) && w.getInf().equals(inf) && w.getAssociation().equals(association) && w.getProp().equals(prop);
		}
		return false;
	}
	
}

