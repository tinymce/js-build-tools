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
				String namespace = namespaceElm.getAttribute("fullname");

				writer.write(namespace + " = {}\n");
				namespaces.add(namespace);
			}

			// Output classes
			writer.write("// Classes\n");
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
					writer.write("\t/// <field name=\"" + propertyElm.getAttribute("name") + "\" type=\"" + propertyElm.getAttribute("type") + "\">" + trim(propertyElm.getTextContent()) + "</field>\n");
				}

				// Write events as fields
				for (Element eventElm : XPathHelper.findElements("members/event", classElm)) {
					writer.write("\t/// <field name=\"" + eventElm.getAttribute("name") + "\" type=\"tinymce.util.Dispatcher\">" + trim(eventElm.getTextContent()) + "</field>\n");
				}

				writer.write("}\n\n");
	
				// Write methods
				for (Element methodElm : XPathHelper.findElements("members/method", classElm))
					writeMethod(classElm.getAttribute("fullname"), methodElm, writer);
			}

			// Output namespace members
			writer.write("// Namespaces\n");
			for (Element namespaceElm : XPathHelper.findElements("//namespace", this.doc)) {
				String namespace = namespaceElm.getAttribute("fullname");

				// Write fields
				for (Element propertyElm : XPathHelper.findElements("members/property|//class[@fullname='" + namespace + "']/members/property", namespaceElm))
					writer.write(namespace + "." + propertyElm.getAttribute("name") + " = new " + propertyElm.getAttribute("type") + "();\n");

				// Write events as fields
				for (Element eventElm : XPathHelper.findElements("members/event|//class[@fullname='" + namespace + "']/members/event", namespaceElm))
					writer.write(namespace + "." + eventElm.getAttribute("name") + " = new tinymce.util.Dispatcher();\n");

				// Write methods
				for (Element methodElm : XPathHelper.findElements("members/method|//class[@fullname='" + namespace + "']/members/method", namespaceElm))
					writeMethod(namespace, methodElm, writer);
			}
		} finally {
			writer.close();
		}
	}

	private void writeMethod(String prefix, Element method_elm, BufferedWriter writer) throws IOException, XPathExpressionException {
		boolean first;

		if (method_elm != null && !method_elm.hasAttribute("constructor")) {
			if (!method_elm.hasAttribute("static"))
				writer.write(prefix + ".prototype." + method_elm.getAttribute("name") + " = function(");
			else
				writer.write(prefix + "." + method_elm.getAttribute("name") + " = function(");

			first = true;
			for (Element paramElm : XPathHelper.findElements("param", method_elm)) {
				if (!first)
					writer.write(", ");

				writer.write(paramElm.getAttribute("name").replaceAll("[.]", ""));
				first = false;
			}

			writer.write(") {\n");

			// Write summary
			writer.write("\t/// <summary>" + method_elm.getAttribute("summary") + "</summary>\n");

			// Write parameters
			for (Element paramElm : XPathHelper.findElements("param", method_elm))
				writeParam(paramElm, writer);

			// Write returns
			writeReturn(XPathHelper.findElement("return", method_elm), writer);

			writer.write("}\n\n");
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
			writer.write(">" + trim(param_elm.getTextContent()) + "</param>\n");
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
			writer.write(">" + trim(return_elm.getTextContent()) + "</returns>\n");
		}
	}

	private String trim(String str) {
		return str.replaceAll("[\r\n]", " ").trim();
	}
}
