package test;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.golemgame.local.Keyword;

public class TestKeyword {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		String input = "$A_Ba$ASDF";
		
		Pattern keywordPattern =  Pattern.compile("\\$[A-Z][A-Z_0-9]*");
		Matcher matcher  = keywordPattern.matcher(input);
		StringBuilder replacement = new StringBuilder();
		int prevend = 0;
		while(matcher.find())
		{
			int start = matcher.start();
			replacement.append(input.subSequence(prevend, start));
			String key = matcher.group();
			System.out.println(key);
			Keyword word = null;// keywords.get(key);
			if(word != null)
				replacement.append(word.get());
			prevend = matcher.end()+1;
		}
		System.out.println("Done");
		replacement.append(input.substring(prevend));
		//System.out.println( replacement.toString());
	}

}
