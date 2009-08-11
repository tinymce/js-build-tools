package com.moxiecode.moxiedoc;

import java.io.File;
import java.io.IOException;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.Vector;

import javax.xml.xpath.XPathExpressionException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.moxiecode.moxiedoc.util.XPathHelper;

public class IntelliSenseGenerator {
	private Document doc;

	public IntelliSenseGenerator(Document doc) {
		this.doc = doc;
	}

	public void generateToMsFormat(File out_file) throws IOException, XPathExpressionException {
		BufferedWriter writer = new BufferedWriter(new FileWriter(out_file.getAbsolutePath()));

		try {
			boolean first;
			Vector<String> namespaces = new Vector<String>();

			// Output namespaces
			writer.write("// Namespaces\n");
			for (Element namespaceElm : XPathHelper.findElements("//namespace", this.doc)) {
				writer.write(namespaceElm.getAttribute("fullname") + " = {}\n");
				namespaces.add(namespaceElm.getAttribute("fullname"));
			}
			
			// Output classes
			writer.write("\n// Classes\n");
			for (Element classElm : XPathHelper.findElements("//class", this.doc)) {
				if (namespaces.contains(classElm.getAttribute("name")))
					continue;

				writer.write(classElm.getAttribute("fullname") + " = function(");

				Element constructorElm = XPathHelper.findElement("members/method[@constructor]", classElm);
				if (constructorElm != null) {
					first = true;
					for (Element paramElm : XPathHelper.findElements("param", constructorElm)) {
						if (!first)
							writer.write(", ");

						writer.write(paramElm.getAttribute("name").replaceAll("[.]", ""));
						first = false;
					}
				}

				writer.write(") {\n");

				// Write summary
				writer.write("\t/// <summary>" + classElm.getAttribute("summary") + "</summary>\n");

				// Write constructor parameters
				if (constructorElm != null) {
					for (Element paramElm : XPathHelper.findElements("param", constructorElm))
						writeParam(paramElm, writer);
				}

				// Write fields
				for (Element propertyElm : XPathHelper.findElements("members/property", classElm)) {
					writer.write("\t/// <field name=\"" + propertyElm.getAttribute("name") + "\" type=\"" + propertyElm.getAttribute("type") + "\">" + propertyElm.getTextContent().replaceAll("[\r\n]", " ") + "</field>\n");
				}

				// Write events as fields
				for (Element eventElm : XPathHelper.findElements("members/event", classElm)) {
					writer.write("\t/// <field name=\"" + eventElm.getAttribute("name") + "\" type=\"tinymce.util.Dispatcher\">" + eventElm.getTextContent().replaceAll("[\r\n]", " ") + "</field>\n");
				}

				writer.write("}\n\n");
				
				// Write methods
				for (Element methodElm : XPathHelper.findElements("members/method", classElm)) {
					if (!methodElm.hasAttribute("constructor")) {
						if (!methodElm.hasAttribute("static"))
							writer.write(classElm.getAttribute("fullname") + ".prototype." + methodElm.getAttribute("name") + " = function(");
						else
							writer.write(classElm.getAttribute("fullname") + "." + methodElm.getAttribute("name") + " = function(");

						first = true;
						for (Element paramElm : XPathHelper.findElements("param", methodElm)) {
							if (!first)
								writer.write(", ");
	
							writer.write(paramElm.getAttribute("name").replaceAll("[.]", ""));
							first = false;
						}
	
						writer.write(") {\n");

						// Write summary
						writer.write("\t/// <summary>" + methodElm.getAttribute("summary") + "</summary>\n");

						// Write parameters
						for (Element paramElm : XPathHelper.findElements("param", methodElm))
							writeParam(paramElm, writer);

						// Write returns
						writeReturn(XPathHelper.findElement("return", methodElm), writer);

						writer.write("}\n\n");
					}
				}
			}
		} finally {
			writer.close();
		}
	}

	private void writeParam(Element param_elm, BufferedWriter writer) throws IOException {
		if (param_elm != null) {
			writer.write("\t/// <param name=\"" + param_elm.getAttribute("name").replaceAll("[.]", "") + "\" type=\"" + param_elm.getAttribute("type") + "\"");

			// Handle element type
			if (param_elm.getAttribute("type").equals("Element"))
				writer.write(" domElement=\"true\"");

			// Handle integer type
			if (param_elm.getAttribute("type").equals("Number"))
				writer.write(" integer=\"true\"");

			// Write summary
			writer.write(">" + param_elm.getTextContent().replaceAll("[\r\n]", " ") + "</param>\n");
		}
	}

	private void writeReturn(Element return_elm, BufferedWriter writer) throws IOException {
		if (return_elm != null) {
			writer.write("\t/// <returns type=\"" + return_elm.getAttribute("type") + "\"");

			// Handle element type
			if (return_elm.getAttribute("type").equals("Element"))
				writer.write(" domElement=\"true\"");

			// Handle integer type
			if (return_elm.getAttribute("type").equals("Number"))
				writer.write(" integer=\"true\"");

			// Write summary
			writer.write(">" + return_elm.getTextContent().replaceAll("[\r\n]", " ") + "</returns>\n");
		}
	}
}
