/*******************************************************************************
 * Copyright 2008, 2009, 2010 Sam Bayless.
 * 
 *     This file is part of Golems.
 * 
 *     Golems is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 * 
 *     Golems is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 * 
 *     You should have received a copy of the GNU General Public License
 *     along with Golems. If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
package support;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import javax.xml.stream.XMLEventFactory;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.Characters;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

/**
 * Note: support package will be excluded from the final build
 * @author Sam
 *
 */
public class ConvertSVGtoPercentage {
	
	public static void main(String[] args) {
		File base = new File("C:/Users/Sam/Convert");
		File output = new File("C:/Users/Sam/Convert/Output");
		base.mkdirs();
		output.mkdirs();
		for(File file:base.listFiles())
		{
			if (file.isDirectory()||file.isHidden() || file.getName().charAt(0)=='.')
				continue;
			File outputFile = new File(output.getAbsolutePath()+ "/" + file.getName());
			if(outputFile.exists())
				outputFile.delete();
			convertSVG(file, outputFile);
		}
		System.out.println("Done set");
	}
	
	private static void convertSVG(File inputFile, File outputFile)
	{

		InputStream input;
		OutputStream output;
		
		String svg= "svg";
		
		try{
			 input = new GZIPInputStream(new BufferedInputStream(new FileInputStream(inputFile)));
			 output = new GZIPOutputStream(new BufferedOutputStream(new FileOutputStream(outputFile)));
	
		} catch(IOException e)
		{
			e.printStackTrace();
			return;
		}
			try{
				XMLInputFactory factory = XMLInputFactory.newInstance();
				XMLEventReader parser = factory.createXMLEventReader(input);
				
				XMLOutputFactory outputFactory = XMLOutputFactory.newInstance();
				XMLEventWriter outputWriter = outputFactory.createXMLEventWriter(output);
			
				while (parser.hasNext()) {
					 XMLEvent event = (XMLEvent)parser.next();
				    if (event.isStartElement()) 
				    {
				    	
				    	StartElement start = event.asStartElement();
				    	
				    	if (start.getName().getLocalPart().equalsIgnoreCase(svg))
				    	{
				    		Iterator<Attribute> attributes = start.getAttributes();
				    		ArrayList<Attribute> newAttributes = new ArrayList<Attribute>();
				    		boolean viewBoxSet = false;
				    		while(attributes.hasNext())
				    		{
				    			Attribute attribute = attributes.next();
				    			String name = attribute.getName().getLocalPart();
				    			if (name.equalsIgnoreCase("width"))
				    			{
						    		String width = attribute.getValue();
						    	
						    			newAttributes.add(m_eventFactory.createAttribute(attribute.getName(), "100%"));
				    			}else if (name.equalsIgnoreCase("height"))
				    			{
				    	    		String height = attribute.getValue();

						    			newAttributes.add(m_eventFactory.createAttribute(attribute.getName()," 100%"));

				    			}else if (name.equalsIgnoreCase("viewBox"))
				    			{
				    				viewBoxSet = true;
				    				newAttributes.add(attribute);
				    			}else
				    				newAttributes.add(attribute);
				    		}
				    		if (!viewBoxSet)
				    		{
				    			newAttributes.add(m_eventFactory.createAttribute("viewBox","0 0 100 100"));
				    		}
					    		StartElement newStart = m_eventFactory.createStartElement(start.getName(), newAttributes.iterator(), start.getNamespaces());
					    		outputWriter.add(newStart);
				    	}else
				    	{
				    		outputWriter.add(event);
				    				    		//parseSVG(parser,outputWriter);
				    	}
				    	
				    }else
				    {
				    	outputWriter.add(event);
				    }

				}
				outputWriter.flush();
				output.flush();
				output.close();
				outputWriter.close();
				parser.close();
			}catch(Exception e)
			{
				e.printStackTrace();
			}
			
			
			System.out.println("Done");
			

	}
	private static  XMLEventFactory m_eventFactory = XMLEventFactory.newInstance();
	private static void parseSVG(XMLEventReader parser,XMLEventWriter outputWriter) throws Exception
	{
		boolean hasViewBox = false;
		while (parser.hasNext()) {
			XMLEvent event= parser.nextEvent();
		    if (event.isEndDocument()) {
		    	outputWriter.add(event);
		       parser.close();
		       break;
		    }else if(event.isAttribute())
		    {
		    	Attribute attribute = (Attribute)event;
		    	String name =  attribute.getName().getLocalPart();
		    	if (name.equalsIgnoreCase("width"))
		    	{
		    		String width = attribute.getValue();
		    		if(width.indexOf('%')>=0)
		    			outputWriter.add(event);
		    		else
		    			outputWriter.add(m_eventFactory.createAttribute(attribute.getName(), width + '%'));
		    	}else if (name.equalsIgnoreCase("height"))
		    	{
		    		String height = attribute.getValue();
		    		if(height.indexOf('%')>=0)
		    			outputWriter.add(event);
		    		else
		    			outputWriter.add(m_eventFactory.createAttribute(attribute.getName(), height + '%'));

		    	}else if (name.equalsIgnoreCase("viewBox"))
		    	{
		    		hasViewBox = true;
		    		outputWriter.add(event);
		    		break;
		    	}
		    }else if(event.isCharacters())
		    {	
		    	
		    	Characters characters = (Characters) event;
		    	System.out.println(characters.getData());
		    	if (characters.isWhiteSpace())
		    		outputWriter.add(characters);

		    	else if(!hasViewBox)
		    	{
		    		//viewbox was never written
		    		outputWriter.add(m_eventFactory.createAttribute("viewBox","0 0 100 100"));
		    		outputWriter.add(event);
		    		break;
		    	}else
		    		outputWriter.add(event);
		    }else
		    {
		    	outputWriter.add(event);
		    }
		    
	 
		}
	}
}
