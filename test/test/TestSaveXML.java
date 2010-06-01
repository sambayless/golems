package test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import com.golemgame.save.NoSuchKeyException;
import com.golemgame.save.ObjectConstructionException;


public class TestSaveXML {

	/**
	 * @param args
	 * @throws ParserConfigurationException 
	 * @throws ObjectConstructionException 
	 * @throws NoSuchKeyException 
	 * @throws IOException 
	 * @throws SAXException 
	 */
	public static void main(String[] args) throws ParserConfigurationException, NoSuchKeyException, ObjectConstructionException, SAXException, IOException {
		List<Integer> testList = new ArrayList<Integer>();
		testList.add(2);
		testList.add(3);
		
	/*	XMLProperties xmlStore = new XMLProperties();
		xmlStore.putExtended("testList", testList);
		
		XMLProperties xmlRetrieve = new XMLProperties(xmlStore.getRoot());
		
		Object[] o = xmlRetrieve.retrieve("testList");
		System.out.println(o[0]);*/
		
	/*	 XMLSerializer serializer = new XMLSerializer();
		    serializer.setOutputCharStream(
		      new java.io.FileWriter("order.xml"));
		    serializer.serialize(xmlRetrieve.getRoot());*/
	}

}
