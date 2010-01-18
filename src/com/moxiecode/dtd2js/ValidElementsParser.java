/**
 * 
 * ValidElementsParser.java
 * 
 * Version:0.9
 * 
 * 091020
 * Per Holmström, Moxiecode
 * (c)Copyright notice
 *  
 ** -About the properties-file
 **  The properties-file consists of a translation-table for the final document.
 **  This is used to compress the output. 
 **  Every line starts with a percent(%) sign and a number, followed by an equals(=) sign and then the string to be replaced.
 **  eg. 
 **  %1=a|br|span
 **  %2=i|b|big|small
 **  
 **  With will replace all occurences of "a|br|span" with "%1" and "i|b|big" with "%2"
 **  Since the strings are parsed from start to finish, one can replace the previously used tags to shrink the data even more
 **  eg.
 **  %3=%1|%2|em|strong
 **/

package com.moxiecode.dtd2js;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.Vector;

import com.conradroche.dtd.decl.AttDef;
import com.conradroche.matra.decl.AttList;
import com.conradroche.matra.decl.Attribute;
import com.conradroche.matra.decl.DocType;
import com.conradroche.matra.decl.ElementType;
import com.conradroche.matra.dtdparser.*;
import com.conradroche.matra.exception.DTDException;
import com.conradroche.matra.io.DTDFile;

public class ValidElementsParser {
	private static Hashtable elementList, attributeList, entityList;
	private static DTDParser dtdParser;
	private Vector<String> printedEleList;
	private String[] excludes;
	private FileOutputStream outputStrm = null;
	private OutputStreamWriter osw = new OutputStreamWriter(System.out);
	private OutputStreamWriter osw2;
	private BufferedWriter outputDevice;
	private String dataString = "";
	private Properties parseProperties;
	private boolean includeAttributes;

	/**
	 * validElementsParser
	 *
	 * @param String dtdInputFile (the dtd to be processed)
	 * @param String dtdOutputFile (where the data goes)
	 * @param String dtdPropertiesFile (parsing instructions)
	 * @param String exclude Comma separated list of elements to exclude.
	 */
	public ValidElementsParser(String dtdInputFile, String dtdOutputFile, String dtdPropertiesFile, String exclude, boolean include_attributes) throws FileNotFoundException,
																										 DTDException,IOException {					
		DTDFile testDTD = null;
		testDTD = new DTDFile(dtdInputFile);
		dtdParser = new DTDParser();
		DocType newDoctype;

		// Split excludes
		if (exclude != null) {
			this.excludes = exclude.split(",");
		}

		this.includeAttributes = include_attributes;

		try {
			outputStrm = new FileOutputStream(dtdOutputFile);
			//outputStrm = new FileOutputStream("c:\\temp\\testout.txt");
			osw2 = new OutputStreamWriter(outputStrm);
			outputDevice = new BufferedWriter(osw2);
		} catch (FileNotFoundException e) {
			throw new FileNotFoundException();
		}

		// Setup empty properties object if it wasn't specified
		if (dtdPropertiesFile != null)
			readPropertiesFile(dtdPropertiesFile);
		else
			parseProperties = new Properties();

		try {
			dtdParser.parse(testDTD);
			newDoctype = dtdParser.getDocType();
			attributeList = newDoctype.getAllAttributes();
			elementList = newDoctype.getElementList();
			entityList = newDoctype.getEntityList();
		} catch (DTDException e) {
			throw e;
		}
	} 

	/**
	 * Method that prints out the current elements attributes. 
	 * 
	 * @param element java.lang.String
	 * @return
	 */
	private String printAttributes(String element) {
		AttList attlist = (AttList) attributeList.get(element);

		if (attlist == null)
			return "[]";

		Enumeration attribs = attlist.getAttribs().elements();

		if (!attribs.hasMoreElements())
			return "[]";

		String str = "[";
		while (true) {
			Attribute currAttrib = (Attribute) attribs.nextElement();
			str += currAttrib.getAttributeName();

			if (attribs.hasMoreElements())
				str += "|";
			else
				break;
		}

		str += "]";

		return str;
	}
	
	/**
	 * printElements
	 * @param rootElement
	 * @param elders
	 * @param level
	 * @throws DTDException
	 */
	private void printElements(String rootElement, String elders, int level) throws DTDException {
		boolean exclude = false;

		try {
			// Exclude some elements
			if (this.excludes != null) {
				for (int i = 0; i < this.excludes.length; i++) {
					if (this.excludes[i].equals(rootElement)) {
						exclude = true;
						break;
					}
				}
			}

			if (rootElement.equals("#PCDATA")) {
				return;
			}

			// Create a new 
			if (level == 0) {
				printedEleList = new Vector<String>();
			}

			ElementType root = (ElementType) elementList.get(rootElement);
			if (root == null)
				throw new DTDException("Null value cannot be passed as root("+rootElement+").");

			StringTokenizer st = new StringTokenizer(elders, ">");
			while (st.hasMoreTokens()) {
				String element = st.nextToken();

				if (rootElement.equals(element)) {
					return; //inf loop condition
				}
			}	
			
			elders += ">" + rootElement;

			if (root.isAnyContentModel()) {
				return;
			}

			///make sure all elements gets printed only ONE time.
			if (printedEleList.contains(rootElement)){
				return;
			}

			// Exlude element name and attribs
			if (!exclude) {
				dataString += rootElement;

				if (this.includeAttributes)
					dataString += printAttributes(rootElement);
			}

			printedEleList.addElement(rootElement);

			// the remainder of the method prints any child elements.
			if (root.hasChildren() == false) {
				if (!exclude)
					dataString += "[]\n";

				return;
			}

			String [] childNames = root.getChildrenNames();
			int rep;

			// Exclude children
			if (!exclude) {
				dataString += "[";

				for (rep = 0; rep < childNames.length - 1; rep++) {
					if (!childNames[rep].equals("#PCDATA")) {
						dataString += childNames[rep] + "|";
					} else
						dataString += "#|";
				}

				dataString += childNames[rep];
				dataString += "]\n";
			}

			for (rep=0; rep <= childNames.length - 1; rep++) {
				printElements(childNames[rep], elders, level + 1);	
			}
		} catch (DTDException e){
			throw e; // throw exception to calling method.
		}
	}
	
	
	/**
	 * printValidElements() 
	 *
	 * @throws DTDException
	 * @throws IOException 
	 */	
	public void printValidElements() throws DTDException,IOException {
		Enumeration roots = dtdParser.getDocType().getRootElements();

		printLookup();
		printElements((String) roots.nextElement(), "", 0);

		try {
			compress();
			convertToString();

			outputDevice.write(dataString);
			outputDevice.flush();
		} catch (IOException e){
			throw e;
		}
	}

	public void printLookup() throws IOException {
		int count = 0;

		outputDevice.write("unpack({\n");

		for (Object key : parseProperties.keySet()) {
			if (count++ > 0)
				outputDevice.write(",\n");

			outputDevice.write("\t" + key + " : '" + parseProperties.get(key) + "'");
		}

		outputDevice.write("\n}, ");
	}

	public void convertToString() {
		dataString = dataString.replaceAll("\n$", "");
		dataString = dataString.replaceAll("\n", "' + \n\t'");
		dataString = "'" + dataString + "'\n);";
	}

	/**
	 * readPropertiesFile(String )
	 * read the properties-file using java.util.Properties
	 *
	 * @param propFile String - the propertiesfile
	 * @throws IOException 
	 */
	public void readPropertiesFile(String propFile) throws IOException {	
		parseProperties = new Properties();

		try {
			BufferedReader in = new BufferedReader(new FileReader(propFile));
			parseProperties.load(in);
			in.close();		
		} catch (IOException e){
			throw e;
		};
	}

	/**
	 * compress()
	 * scans dataString for occurances of the Strings found in the properties-file.
	 */
	private void compress(){
		// Sort hashtable by sorting the keys.
		Vector<String> v = new Vector<String>();

		// Fill vector with keys
		for (Object key : parseProperties.keySet())
			v.add((String) key);

		Collections.sort(v);

		java.util.Enumeration<String> keys = v.elements();
	
		while (keys.hasMoreElements()) {
			String aKey = (String) keys.nextElement();
			String aValue = (String) parseProperties.get(aKey);

			// The regexp uses \Q and \E to process a string containing special characters
			dataString = dataString.replaceAll("\\Q" + aValue + "\\E", "\\" + aKey);
		}
	}
}
