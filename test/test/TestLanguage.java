package test;

import com.golemgame.local.StringConstants;

public class TestLanguage {
	public static void main(String[] args) throws ClassNotFoundException {
		//Language l = new Language_en();
	//	Language_en_US g;
	//	TestLanguage.class.getClassLoader().loadClass("com.golemgame.local.Language_en_US");
		System.out.println(StringConstants.get("version"));
	}
}
