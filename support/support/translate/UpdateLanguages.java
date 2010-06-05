package support.translate;

import java.awt.datatransfer.StringSelection;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Scanner;

import support.translate.parser.ASTDefinition;
import support.translate.parser.ASTDefinitionClass;
import support.translate.parser.Node;
import support.translate.parser.ParseException;
import support.translate.parser.Translator;
import support.translate.parser.Node.NodeType;

/**
 * Find all language files in the set of languages; add new keys as needed to match the template.
 * @author Sam
 *
 */
public class UpdateLanguages {
	public static void main(String[] args) throws ParseException, FileNotFoundException, IOException {

		//should alter to copy in the defaults for language.lng
		InputStream input = new BufferedInputStream(UpdateLanguages.class.getResourceAsStream("definition_template"));
		
		Translator t = new Translator(input,"UTF8");
		//t.Start().dump("");
		
		Node template = Translator.Start();
		
		//DefinitionClass rootClass = new DefinitionClass("");
		
		//parseDefClass(root,rootClass);
		
		  File langs = new File("support/support/translate/languages");
			
		    for(File f:langs.listFiles())
		    {
	
		    	if(f.getName().endsWith(".lng"))
		    	{
			    	//Translator translate = new Translator(input);
		    		input =new BufferedInputStream(new FileInputStream(f));
		    		try{
		    			Translator.ReInit(input,"UTF8");
		    			//t.Start().dump("");
		    			}catch(java.lang.NullPointerException e)
		    			{
		    				new Translator(input,"UTF8");
		    			}
			    	Node newFile = Translator.Start();
			    	
			    	ArrayList<Addition> additions = new ArrayList<Addition>();
			    	
			    	
			    	
			    	syncKeys(template,newFile,additions);
			    	
			    	ArrayList<String> fileLines = new ArrayList<String>();
			    	
			    	Scanner scanner = new Scanner(new BufferedReader( new FileReader(f)));
			    	while(scanner.hasNextLine())
			    	{
			    		fileLines.add(scanner.nextLine());
			    	}
			    	int offset = 0;
			    	for(Addition a:additions)
			    	{
			    		System.out.println("Key added " + a + " to " + f.getName());
			    		//this doesnt work.. it doesnt handle recursion properly.
			    		if(a.afterLine<0 || a.afterLine>= fileLines.size())
			    		{
			    			fileLines.add( a.getAddition(0));
			    		}else{
			    			fileLines.add(a.afterLine + (offset++) + 1, a.getAddition(0));
			    		}
			    	}
			    	/*for(String s:fileLines)
			    		System.out.println(s);
			    	*/
			    	f.delete();
			    	Writer writer = new BufferedWriter(new FileWriter(f));
			    	for(String s:fileLines)
			    		writer.write(s + "\n");
			    	writer.close();
			    	
			    	System.out.println("Language " + f.getName() + " had " + additions.size() + " additions");
			    	//parseDefinition(new BufferedInputStream( new FileInputStream(f)));
		    	}
		    }
		    
		 File defFile = new File (langs, "language.lng");
		 defFile.delete();
		 defFile.createNewFile();
		 InputStream copyStream = new BufferedInputStream(UpdateLanguages.class.getResourceAsStream("definition_template"));
		 OutputStream out = new BufferedOutputStream(new FileOutputStream(defFile));
		 
		 int c = 0;
		 while((c= copyStream.read())>=0)
			 out.write(c);
		 out.close();
		 System.out.println("Languages updated.");
	}

	private static void syncKeys(Node template, Node compare, ArrayList<Addition> additions) {
		
		for(int i = 0;i<template.jjtGetNumChildren();i++)
		{
			Node child = template.jjtGetChild(i);
			if(child.getNodeType()==NodeType.Definition || child.getNodeType()==NodeType.DefinitionClass)
			{
				boolean matched = false;
				Node match = null;
				for(int j = 0;j<compare.jjtGetNumChildren();j++)
				{
					Node compChild = compare.jjtGetChild(j);
					if(compChild.equals(child))
					{
						matched = true;
						match=compChild;
						break;
					}
				}
				if(!matched)
				{
					int line = 0;
					
					if(compare instanceof ASTDefinitionClass)
					{
						line =(( ASTDefinitionClass)compare).getEndLine();
					}else{
						//add it to the end
						line = -1;
					}
					
					//insert this key or class
					if(child.getNodeType()==NodeType.Definition)
					{
						ASTDefinition def = (ASTDefinition) child;
						
				
						
						additions.add(new KeyAddition(def.getKey(),line));
					}else if (child.getNodeType()==NodeType.DefinitionClass)
					{
						ASTDefinitionClass def = (ASTDefinitionClass) child;
						ClassAddition classAdd = new ClassAddition(def.getName(),line);
					//	
						additions.add(classAdd);
						addClass(classAdd,def,0);
					}
				}else if (child.getNodeType()==NodeType.DefinitionClass)
				{
					//recurse if there WAS a match
					syncKeys(child,match,additions);
				}
			}
		}
		
	}
	
	private static void addClass(ClassAddition parent, ASTDefinitionClass node, int line) {
		
		for(int i = 0;i<node.jjtGetNumChildren();i++)
		{
			Node child = node.jjtGetChild(i);
			if(child instanceof ASTDefinition)
			{
				ASTDefinition def = (ASTDefinition) child;				
				parent.addChild(new KeyAddition(def.getKey(),line));
			}else if( child instanceof ASTDefinitionClass)
			{
				ASTDefinitionClass defClass = (ASTDefinitionClass) child;
				ClassAddition classAdd  = new ClassAddition(defClass.getName(),line);
				parent.addChild(classAdd);
				addClass(classAdd, defClass,line);
			}
		}
	}

/*	private static void parseDefClass(Node root,DefinitionClass rootClass)
	{
		
		for (int i = 0;i<root.jjtGetNumChildren();i++)
		{
			//search for keys for language and country and variant
			Node element = root.jjtGetChild(i);
			if(element.getNodeType()==NodeType.DefinitionClass)
			{
				ASTDefinitionClass defClass = (ASTDefinitionClass) element;
				DefinitionClass childClass = new DefinitionClass(defClass.getName());
				rootClass.addDefClass(childClass);
				parseDefClass(element,childClass);
				
			}else if (element.getNodeType()==NodeType.Definition)
			{
				ASTDefinition def = (ASTDefinition) element;
				rootClass.addKey(def.getKey());
			}
		}
	}*/
	/*
	private static class DefinitionClass 
	{
		private String defClass;
	
		ArrayList<String> supportedKeys = new ArrayList<String>();
		ArrayList<DefinitionClass> defClasses = new ArrayList<DefinitionClass>();
		
		private DefinitionClass(String defClass) {
			super();
			this.defClass = defClass;
		}
		public void addDefClass(DefinitionClass defClass)
		{
			defClasses.add(defClass);
		}
		public void addKey(String key)
		{
			supportedKeys.add(key);
		}
	}*/
	private abstract static class Addition
	{
	
		public int afterLine;

		private Addition(int atLine) {
			super();
			this.afterLine = atLine;
		}

		public abstract String getAddition(int indent);
	
		
	}
	private static class ClassAddition extends Addition
	{
		public String name;
		private ArrayList<Addition> children = new ArrayList<Addition>();
		public ArrayList<Addition> getChildren() {
			return children;
		}
		private ClassAddition(String name, int atLine) {
			super(atLine);
			this.name = name;
		}
		public void addChild(Addition c)
		{
			children.add(c);
		}
		@Override
		public String getAddition(int indent) {
			String ind = "";
			for (int i = 0;i<indent;i++)
				ind +="\t";
			String base = ind +  name + "{\n\n";
		
			for(Addition a:children)
			{
				base += a.getAddition(indent+1) + "\n";
			}
			
			base += ind + "}\n";
			return base;
		}
		@Override
		public String toString() {
			return "ClassAddition " + this.name;
		}
		
	}
	private static class KeyAddition extends Addition
	{
		public String key;
		private KeyAddition(String key, int atLine) {
			super(atLine);
			this.key = key;
		}
		@Override
		public String getAddition(int indent) {
			String ind = "";
			for (int i = 0;i<indent;i++)
				ind +="\t";
			return ind + key + "~\n";
		}
		@Override
		public String toString() {
			return "DefAddition " + this.key;
		}
	}
}
