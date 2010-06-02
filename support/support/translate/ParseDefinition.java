package support.translate;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.Writer;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.zip.GZIPOutputStream;

import support.translate.parser.ASTDefinition;
import support.translate.parser.ASTDefinitionClass;
import support.translate.parser.Node;
import support.translate.parser.ParseException;
import support.translate.parser.Translator;
import support.translate.parser.Node.NodeType;

import com.golemgame.local.Language;

public class ParseDefinition {

	/**
	 * @param args
	 * @throws ParseException 
	 * @throws IOException 
	 * @throws FileNotFoundException 
	 * @throws URISyntaxException 
	 */	
	public static void main(String[] args) throws ParseException, FileNotFoundException, IOException, URISyntaxException {
	
		//need to support Tags in the descriptions somehow... perhaps not at this level? Perhaps at the string constant level?
		
	//	ParseDefinition.class.getResourceAsStream("definitions");
		
	    File langs = new File("support/support/translate/languages");
		
	    for(File f:langs.listFiles())
	    {
	    	if(f.exists()&&f.getName().endsWith(".lng"))
	    	{
	    		Collection<Character> a = constructAlphabet(new BufferedInputStream( new FileInputStream(f)));
	    		parseDefinition(new BufferedInputStream( new FileInputStream(f)),a);
	    		
	    	}
	    }
		
	}

	private static void parseDefinition(InputStream input, Collection<Character> alphabet) throws ParseException, FileNotFoundException, IOException, URISyntaxException
	{
		
		
		try{
		Translator.ReInit(input,"UTF8");
		//t.Start().dump("");
		}catch(java.lang.NullPointerException e)
		{
			Translator t = new Translator(input,"UTF8");
		}
		Node root = Translator.Start();
		String language = null;
		String country = null;
		String variant = null;
		for (int i = 0;i<root.jjtGetNumChildren();i++)
		{
			//search for keys for language and country and variant
			Node element = root.jjtGetChild(i);
			if(element.getNodeType() == NodeType.Definition)
			{
				ASTDefinition def = (ASTDefinition) element;
				if("LANGUAGE".equalsIgnoreCase(def.getKey()))
				{
					language = def.getEntry();
				}else if("COUNTRY".equalsIgnoreCase(def.getKey()))
				{
					country = def.getEntry();
				}else if ("VARIANT".equalsIgnoreCase(def.getKey()))
				{
					variant = def.getEntry();				
				}
				if(variant != null && language != null && country != null)
					break;
			}
		}
		
		
		String fName = "Language";
		if(language != null)
			fName += "_" + language ;
		if(language!=null && country!= null)
			fName += "_" + country ;
		if(language!=null && country!= null && variant != null)
			fName += "_" + variant ;
		String alphaDest = "dataFolder/com/golemgame/local/alphabet/";
		String dctDest = "dataFolder/com/golemgame/local/dct/";
		String classDst = "localization/com/golemgame/local";
		String packageName = "com.golemgame.local";
		File base = new File( ParseDefinition.class.getResource("").toURI());
		File dest = new File(base.getParentFile().getParentFile().getParent() + "/" + dctDest + "/" + fName + Language.DICTIONARY_EXT);
		
		File alpha = new File(base.getParentFile().getParentFile().getParent() + "/" + alphaDest + "/" + fName + Language.ALPHABET_EXT);
		alpha.delete();
		alpha.createNewFile();
		writeAlphabet(alphabet,alpha);
		
		dest.delete();
		dest.createNewFile();
	
		DataOutputStream output =new DataOutputStream(new GZIPOutputStream( new BufferedOutputStream(new FileOutputStream(dest))));
		//Writer writer = new OutputStreamWriter(output,"UTF16");
		construct(root,"",output);
		output.close();
		System.out.println(".DCT File updated at: " + dest);
		if(language != null && language.length()>0) //catch the default language
		{
			
			File classFile = new File(classDst + "/" + fName+".java");
			if(!classFile.exists())
			{//only if the file doesnt exit
				Writer classWriter = new BufferedWriter( new FileWriter(classFile));
				classWriter.write("/*Generated language file*/\n");
				classWriter.write("package " + packageName + ";\n\n");
				classWriter.write("public class " + fName + " extends Language{\n\n}\n");
				/*
				 * public class Language_en extends Language {
	
					}
				 */
				classWriter.close();
			}else{
				System.out.println("Java File already exists: " + classFile + "(No changes needed)");
			}
		}
		System.out.println("Dont forget to refresh the dataFolder for changes to take effect.");
	}
	
	private static void writeAlphabet(Collection<Character> alphabet, File alpha) throws FileNotFoundException, IOException {
		DataOutputStream out = new DataOutputStream( new GZIPOutputStream( new BufferedOutputStream(new FileOutputStream(alpha) )));
		
		for(Character s:alphabet)
			out.writeChar(s);
		out.close();
	}

	public static void construct(Node node,String prefix, DataOutputStream output) throws IOException
	{
		for (int i = 0;i<node.jjtGetNumChildren();i++)
		{
			Node child = node.jjtGetChild(i);
			switch(child.getNodeType())
			{
				case Definition:
				{
					writeDefinition((ASTDefinition)child, prefix, output);
					
					break;
				}
				case DefinitionClass:
				{
					
					construct(child, prefix +  (prefix == ""? "" : ".") + ((ASTDefinitionClass) child).getName(), output);
					break;
				}
				default://do nothing
				
			}
		}
	
	}

	private static void writeDefinition(ASTDefinition child, String prefix, DataOutputStream output) throws IOException {
		String key = prefix +(prefix=="" || prefix == null ? "":  ".") + child.getKey();
		String entry = child.getEntry();
		if (entry == null || entry.length()==0)
			return;//ok
		output.writeUTF(key);
		output.writeUTF(entry);
	
	}
	
	private static Collection<Character> constructAlphabet(InputStream input) throws IOException
	{
		Set<Character> alphabet = new HashSet<Character>();
		
		Reader r = new BufferedReader (new InputStreamReader(input,"UTF8"));
		int c = 0;
		while((c=r.read())>=0)
		{
			char ch = (char)c;
			if(Character.isDefined(ch) && !Character.isLowSurrogate(ch) &&! Character.isHighSurrogate(ch) &&! Character.isISOControl(ch) &&! Character.isSpaceChar(ch))
			{
				alphabet.add(ch);
			}
		}
		
	/*	Set<String> alphabet = new HashSet<String>();
		Scanner r= new Scanner((input));
		
		String cur = "";
		while(r.hasNext())
		{
			cur = r.nextLine();
			for(int i = 0;i<cur.length();i++)//this doesnt solve the surrogate character problem
				alphabet.add(cur.substring(i, i+1));
		}
		*/
		return alphabet;
		
	}
	
}
