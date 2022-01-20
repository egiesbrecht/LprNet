package language;

import java.io.IOException;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import ext.BlobHandler;

public class Text implements Serializable {

	private static final long serialVersionUID=-876549565140021371L;
	
	private List<List<Word>> sentences;

	private String filePath, textName, author;
	
	public Text(String pFilePath, String pTextName, String pAuthor, List<List<Word>> in, Statement stat) {
		filePath=pFilePath;
		textName=pTextName;
		author=pAuthor;
		sentences=in;
	}
	
	public String getAuthor() {
		return author;
	}
	
	public String getTextName() {
		return textName;
	}
	
	public String getFilePath() {
		return filePath;
	}
	
	public boolean insertIntoDB(Connection conn) throws IOException, SQLException {
		if(textName==null || filePath==null || author==null || sentences==null) return false;
		BlobHandler.insert(this, "insert into texte(name, author, path, blob) values('"+textName+"', '"+author+"', '"+filePath+"', ?)", conn);
		return true;
	}
	
	public String getName() {
		return textName;
	}
	
	public List<List<Word>> getAllSentences() {
		return sentences;
	}
	
	public List<Word> getSentence(int number) {
		return sentences.get(number);
	}
	
	public String asReadable() {
		String ret="";
		for(List<Word> sentence:sentences) {
			for(Word word:sentence) {
				ret+=word.getWord()+" ";
			}
			ret+=". ";
		}
		return ret;
	}
}
