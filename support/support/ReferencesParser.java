package support;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

public class ReferencesParser {

	/**
	 * @param args
	 * @throws FileNotFoundException 
	 */
	public static void main(String[] args) throws FileNotFoundException {
		ReferencesParser parser = new ReferencesParser();
		List<Entry> entries = parser.generateEntries(new FileReader(new File("references")));
		
		for (Entry entry:entries)
		{
			if (!entry.isWeak() &! entry.contains("AppClassLoader"))
			{
				System.out.println(entry.getEntries());
			}
		}
		
		System.out.println("Done");
	}
	
	private List<Entry> generateEntries(Reader file)
	{
		List<Entry> entryList = new ArrayList<Entry>();
		String[] acceptablePatterns = new String[]{"Java Local Reference ","Static reference from ", "JNI Global Reference ", "System Class Reference " };
		String lastLine = "";
		try{
			while(true)
			{
				String line;
				if (! hasPattern(lastLine, acceptablePatterns))
				{
					while (!hasPattern((line = readLine(file)), acceptablePatterns))
					{
	
					}
				}
				line = lastLine;
				lastLine = "";
				
				
				
				
				String[] entryPatterns = new String[]{"-->" };
			
				List<String> entries = new ArrayList<String>();
					
						while (hasPattern((line = readLine(file)), entryPatterns))
						{
							entries.add(line);
						}
						entryList.add(new Entry(entries));
									
				lastLine = line;
			}
		}catch(Exception e)
		{
			
		}
		return entryList;
		
		
	}
	
	
	private Entry builtEntry(Reader file) throws IOException {
		Entry entry = null;
		
		
		String[] acceptablePatterns = new String[]{"-->" };
	
		List<String> entries = new ArrayList<String>();
				String line;
				while (hasPattern((line = readLine(file)), acceptablePatterns))
				{
					entries.add(line);
				}

		return entry;
	}

	private boolean hasPattern(String string, String[] acceptablePatterns) {
		if (string == null)
			return false;
		
		for (String pat:acceptablePatterns)
		{
			if (string.contains(pat))
				return true;
		}
		return false;
	}

	private String readLine(Reader file) throws IOException{
		
		String str = "";
		
		while(true)
		{
			int cr =file.read();
			if(cr <0)
				throw new IOException ("EOF");
			
			if (cr == '\n')
				return str;
			
			else str += (char)cr;
		}
	
	}


	private static class Entry
	{
		List<String> entries;

		public Entry(List<String> entries) {
			super();
			this.entries = entries;
		}

		public List<String> getEntries() {
			return entries;
		}
		
		public boolean contains(String string)
		{
			for(String entry:entries)
			{
				if (entry.contains(string) )
				{
					return true;
				}
			}
			return false;
		}
		
		public boolean isWeak()
		{
			return (contains("WeakHashMap") || contains("WeakReference"));
		}
	}
	
}
