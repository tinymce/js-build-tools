package com.moxiecode.moxiedoc;

import java.io.File;
import java.io.IOException;

import javax.xml.xpath.XPathExpressionException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.moxiecode.moxiedoc.util.XPathHelper;

public class IntelliSenseGenerator {
	private Document doc;

	public IntelliSenseGenerator(Document doc) {
		this.doc = doc;
	}

	public void generate(File out_file) throws IOException, XPathExpressionException {
		java.io.BufferedWriter writer = new java.io.BufferedWriter(new java.io.FileWriter(out_file.getAbsolutePath()));

		try {
			boolean first;

			// Output namespaces
			writer.write("// Namespaces\n");
			for (Element namespaceElm : XPathHelper.findElements("//namespace", this.doc)) {
				writer.write(namespaceElm.getAttribute("fullname") + " = {}\n");
			}

			// Output classes
			writer.write("\n// Classes\n");
			for (Element classElm : XPathHelper.findElements("//class", this.doc)) {
				if (classElm.getAttribute("name").equals("tinymce"))
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
					for (Element paramElm : XPathHelper.findElements("param", constructorElm)) {
						Element paramSummaryElm = XPathHelper.findElement("description", paramElm);
						if (paramSummaryElm != null)
							writer.write("\t/// <param name=\"" + paramElm.getAttribute("name").replaceAll("[.]", "") + "\" type=\"" + paramElm.getAttribute("type") + "\">" + paramSummaryElm.getTextContent().replaceAll("[\r\n]", " ") + "</param>\n");
					}
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
						for (Element paramElm : XPathHelper.findElements("param", methodElm)) {
							Element paramSummaryElm = XPathHelper.findElement("description", paramElm);
							if (paramSummaryElm != null)
								writer.write("\t/// <param name=\"" + paramElm.getAttribute("name").replaceAll("[.]", "") + "\" type=\"" + paramElm.getAttribute("type") + "\">" + paramSummaryElm.getTextContent().replaceAll("[\r\n]", " ") + "</param>\n");
						}

						// Write returns
						Element returnElm = XPathHelper.findElement("return", methodElm);
						if (returnElm != null) {
							writer.write(
									"\t/// <returns type=\"" + returnElm.getAttribute("type") + "\">" + 
									returnElm.getTextContent().replaceAll("[\r\n]", " ") + "</returns>\n"
							);
						}

						writer.write("}\n\n");
					}
				}
			}
		} finally {
			writer.close();
		}
	}
}
