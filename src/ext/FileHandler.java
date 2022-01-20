/*
 * This file is part of pattern recognition network (prNet), a program to find patterns in data-structures
 * Copyright (C) 2022  Elija Giesbrecht
 * Published under GPLv3-or-later license
 */
package ext;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

public abstract class FileHandler {
	public static String read(String fileName) {
		 try {
			 String ret="";
			 File myObj=new File(fileName);
			 Scanner myReader=new Scanner(myObj);
			 while (myReader.hasNextLine()) {
				 String data=myReader.nextLine();
				 ret+=data+"\n";
			 }
			 myReader.close();
			 return ret;
		 }catch(FileNotFoundException e) {
			 e.printStackTrace();
			 return null;
		 }
	}
	
	
	public static void create(String fileName) {
		try {
			File myObj=new File(fileName);
			if(myObj.createNewFile()) System.out.println("File created: "+myObj.getName());
		}catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	public static void write(String fileName, String content) {
		try {
			FileWriter myWriter=new FileWriter(fileName);
			myWriter.write(content);
			myWriter.close();
		}catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	public static String[] getAllFilePathsInDir(String dirPath) {
		File path=new File(dirPath);
		File[] filesList=path.listFiles();
		String[] ret=new String[filesList.length];
		for(int i=0; i<filesList.length; i++) {
			ret[i]=filesList[i].getPath();
		}
		return ret;
	}
	
	
	public static String[] getAllFileNamesInDir(String dirPath) {
		File path=new File(dirPath);
		File[] filesList=path.listFiles();
		String[] ret=new String[filesList.length];
		for(int i=0; i<filesList.length; i++) {
			ret[i]=filesList[i].getName().replace(".", "%").split("%")[0];
		}
		return ret;
	}
}
