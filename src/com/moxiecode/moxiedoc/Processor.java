package com.moxiecode.moxiedoc;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Vector;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import javax.xml.xpath.XPathExpressionException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.moxiecode.moxiedoc.util.XPathHelper;

public class Processor {
	private int processedFileCount;
	private Vector<File> files;
	private File outDir, msIntelliSenseFile;
	private Document doc;
	private File templateDir;
	private String title, eventClass;

	public Processor() {
		this.files = new Vector<File>();
	}

	public void addFile(File file) {
		this.files.add(file);
	}

	public void setTemplateDir(File template_dir) {
		this.templateDir = template_dir;
	}

	public void setOutDir(File outdir) {
		this.outDir = outdir;
	}

	public void setMsIntelliSenseFile(File intelli_file) {
		this.msIntelliSenseFile = intelli_file;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public void setEventClass(String cls) {
		this.eventClass = cls;
	}

	public void process() throws XPathExpressionException, IOException, ParserConfigurationException, TransformerException, TransformerConfigurationException {
		CommentParser parser = new CommentParser();
		Vector<CommentBlock[]> blockArrays = new Vector<CommentBlock[]>();

		// Setup output DOM Document
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = dbf.newDocumentBuilder();

		// Setup model document
		this.doc = db.newDocument();
		this.doc.appendChild(this.doc.createElement("model"));

		// Parse all added files and add the comment data to XML structure
		this.processedFileCount = 0;
		for (File file : this.files) {
			CommentBlock blocks[] = parser.parse(file);
	
			blockArrays.add(blocks);
			this.addToXml(blocks);

			this.processedFileCount++;
		}

		// Process all member blocks
		for (CommentBlock[] blocks : blockArrays)  {
			for (CommentBlock block : blocks)  {
				if (block.hasTag("member")) {
					Element memberElm = buildMember(block);
					String memberOf = block.getTag("member").getText();

					if (memberElm != null) {
						// Check for specific namespace
						Element nsElm = XPathHelper.findElement("//namespace[@fullname='" + memberOf + "']", this.doc);
						if (nsElm != null && nsElm.getAttribute("fullname").equals(memberOf)) {
							memberElm.setAttribute("fullname", memberOf + "." + memberElm.getAttribute("name"));

							Element membersElm = XPathHelper.findElement("members", nsElm);

							if (membersElm == null)
								membersElm = (Element) nsElm.appendChild(this.doc.createElement("members"));

							membersElm.appendChild(memberElm);
						} else {
							// Check case specific class name
							Element classElm = XPathHelper.findElement("//class[@fullname='" + memberOf + "']", this.doc);
							if (classElm != null && classElm.getAttribute("fullname").equals(memberOf)) {
								Element targetElm = XPathHelper.findElement("members", classElm);
	
								if (targetElm == null)
									targetElm = (Element) classElm.appendChild(this.doc.createElement("members"));
	
								// Force member static if class is static
								if (classElm.hasAttribute("static"))
									memberElm.setAttribute("static", "true");
	
								// Stick member into class
								targetElm.appendChild(memberElm);
							} else {
								Element targetElm = this.doc.getDocumentElement();

								if (memberOf.length() > 0)
									memberElm.setAttribute("fullname", memberOf + "." + memberElm.getAttribute("name"));
								else
									memberElm.setAttribute("fullname", memberElm.getAttribute("name"));

								// Create namespace
								if (memberOf.length() > 0)
									targetElm = makeNameSpace(memberOf, false);
	
								Element membersElm = XPathHelper.findElement("members", targetElm);
	
								if (membersElm == null)
									membersElm = (Element) targetElm.appendChild(this.doc.createElement("members"));
	
								membersElm.appendChild(memberElm);
							}
						}
					}
				}
			}
		}

		// Build inheritage
		for (Element classElm : XPathHelper.findElements("//class[@extends]", this.doc)) {
			Vector<String> superClasses = new Vector<String>();

			if (classElm.hasAttribute("extends")) {
				// Find all super class names
				findSuperClasses(classElm, superClasses);

				// Add super-classes list
				Element superClassesElm = (Element) classElm.appendChild(this.doc.createElement("super-classes"));

				for (String superClass : superClasses) {
					Element superClassElm = (Element) superClassesElm.appendChild(this.doc.createElement("class-ref"));

					superClassElm.setAttribute("class", superClass);
				}

				classElm.appendChild(superClassesElm);

				// Add inherited methods
				for (String superClass : superClasses) {
					Element superClassElm = XPathHelper.findElement("//class[@fullname='" + superClass + "']", doc);

					if (superClassElm != null) {
						Element membersElm = XPathHelper.findElement("members", classElm);
						if (membersElm == null)
							membersElm = (Element) classElm.appendChild(this.doc.createElement("members"));

						for (Element memberElm : XPathHelper.findElements("members/*", superClassElm)) {
							if (!memberElm.hasAttribute("inherited-from") && !memberElm.hasAttribute("constructor")) {
								String memberName = memberElm.getAttribute("name");

								// Check if item exists already
								if (XPathHelper.findElement("members/*[@name='" + memberName + "']", classElm) == null) {
									Element superMemberElm = XPathHelper.findElement("members/*[@name='" + memberName + "']", superClassElm);

									if (!superMemberElm.hasAttribute("inherited-from")) {
										Element memberRefElm = this.doc.createElement(superMemberElm.getNodeName());

										memberRefElm.setAttribute("name", memberName);
										memberRefElm.setAttribute("inherited-from", superClass);

										membersElm.appendChild(memberRefElm);
									}
								}
							}
						}
					}
				}
			}
		}

		// Serialize XML structure
		this.serializeDocument(this.doc, new File(this.outDir, "model.xml"));

		// Generate MS intellisense file
		if (this.msIntelliSenseFile != null) {
			IntelliSenseGenerator generator = new IntelliSenseGenerator(this.doc);

			generator.generateToMsFormat(this.msIntelliSenseFile, this.eventClass);
		}

		// Generate HTML using XSLT
		transform("index.xsl", "index.html", "index");

		// Process classes
		for (Element classElm : XPathHelper.findElements("//class", this.doc)) {
			String className = classElm.getAttribute("fullname");

			transform("class.xsl", "class_" + className + ".html", className);
		}

		// Process members out side classes
		for (Element memberElm : XPathHelper.findElements("//method[@fullname]|//property[@fullname]|//event[@fullname]", this.doc)) {
			String memberFullName = memberElm.getAttribute("fullname");

			transform("member.xsl", "member_" + memberFullName + ".html", memberFullName);
		}

		// Copy resource files
		for (File file : this.templateDir.listFiles()) {
			if (!file.getName().endsWith(".xsl"))
				copy(file, new File(this.outDir, file.getName()));
		}
	}

	public int getProcessedFileCount() {
		return this.processedFileCount;
	}

	// Private methods

	public void copy(File src_path, File dst_path) throws IOException {
		if (!src_path.isHidden()) {
			if (src_path.isDirectory()) {
				if (!dst_path.exists())
					dst_path.mkdir();

				String files[] = src_path.list();

				for (int i = 0; i < files.length; i++)
					copy(new File(src_path, files[i]), new File(dst_path, files[i]));
			} else {
				if (!src_path.exists()) {
					throw new IOException("File or directory does not exist.");
				} else {
					InputStream in = new FileInputStream(src_path);
					OutputStream out = new FileOutputStream(dst_path); 
	
					byte[] buf = new byte[4096];
					int len;
	
					while ((len = in.read(buf)) > 0) {
						out.write(buf, 0, len);
					}
	
					in.close();
					out.close();
				}
			}
		}
	}

	private void transform(String xslt, String output, String target) throws FileNotFoundException, TransformerException {
		TransformerFactory factory = TransformerFactory.newInstance();
		Transformer transformer = factory.newTransformer(new StreamSource(new File(this.templateDir, xslt)));

		transformer.setParameter("target", target);
		
		if (this.title != null)
			transformer.setParameter("title", this.title);
		else
			transformer.setParameter("title", "");

		transformer.transform(new javax.xml.transform.dom.DOMSource(this.doc), new StreamResult(new FileOutputStream(new File(this.outDir, output))));
	}
	
	private void findSuperClasses(Element class_elm, Vector<String> class_names) throws XPathExpressionException{
		String superClassName;

		if (class_elm.hasAttribute("extends")) {
			superClassName = class_elm.getAttribute("extends");
			class_names.add(superClassName);

			// Find next super class
			Element superClassElm = XPathHelper.findElement("//class[@fullname='" + superClassName + "']", this.doc);
			if (superClassElm != null)
				this.findSuperClasses(superClassElm, class_names);
		}
	}

	private Element makeNameSpace(String class_name, boolean skip_last) throws XPathExpressionException {
		Element targetElm = this.doc.getDocumentElement();
		String parts[] = class_name.split("\\.");
		String namespace = "";
	
		// Create namespaces if needed
		for (int i = 0; i < parts.length - (skip_last ? 1 : 0); i++) {
			if (i > 0)
				namespace += ".";

			namespace += parts[i];

			Element nsElm = XPathHelper.findElement("//namespace[@fullname='" + namespace + "']", doc);
			if (nsElm == null) {
				nsElm = this.doc.createElement("namespace");

				nsElm.setAttribute("name", parts[i]);
				nsElm.setAttribute("fullname", namespace);

				targetElm.appendChild(nsElm);
				targetElm = nsElm;
			} else
				targetElm = nsElm;
		}

		return targetElm;
	}

	private String getShortName(String str) {
		return str.replaceAll("^.*\\.", "");
	}

	private Element addClass(CommentBlock block) throws XPathExpressionException {
		String className = block.getTag("class").getText();
		Element targetElm = makeNameSpace(className, true);

		// Add class to namespace
		Element classElm = this.doc.createElement("class");

		classElm.setAttribute("name", getShortName(className));
		classElm.setAttribute("fullname", className);

		targetElm.appendChild(classElm);

		addTags(block, classElm);

		return classElm;
	}

	private void addTags(CommentBlock block, Element elm) {
		Document doc = elm.getOwnerDocument();

		// Add value properties
		String valueProps[] = {"name", "extends", "type", "deprecated", "version", "author"};
		for (String name : valueProps) {
			if (block.hasTag(name))
				elm.setAttribute(name, block.getTag(name).getText());
		}

		// Add boolean properties
		String boolProps[] = {"constructor", "static", "final", "abstract", "private", "protected", "public"};
		for (String name : boolProps) {
			if (block.hasTag(name))
				elm.setAttribute(name, "true");
		}

		// Add description
		Element descriptionElm = doc.createElement("description");
		descriptionElm.appendChild(doc.createTextNode(block.getText()));
		elm.appendChild(descriptionElm);

		// Add summary
		String summary = block.getText().replaceAll("<[^>]+>", "").replaceAll("[\\r\\n]+", " ").trim();
		int dotIdx = summary.indexOf('.');

		if (dotIdx != -1)
			summary = summary.substring(0, dotIdx + 1);

		if (summary.length() > 120)
			summary = summary.substring(0, 120) + "...";

		elm.setAttribute("summary", summary);

		// Add params
		for (ParamTag paramTag : block.getParams()) {
			Element paramElm = doc.createElement("param");

			// Add name/type
			paramElm.setAttribute("name", paramTag.getParameterName());

			// Add types
			String[] types = paramTag.getTypes();
			if (types != null && types.length > 1) {
				for (String type : types) {
					Element typeElm = this.doc.createElement("type");

					typeElm.setAttribute("fullname", type);

					paramElm.appendChild(typeElm);
				}
			} else if (types != null && types.length == 1)
				paramElm.setAttribute("type", types[0]);
			else if (types == null)
				paramElm.setAttribute("type", "Object");

			// Add description
			Element paramDescriptionElm = doc.createElement("description");
			paramDescriptionElm.appendChild(doc.createTextNode(paramTag.getText()));
			paramElm.appendChild(paramDescriptionElm);

			// Append
			elm.appendChild(paramElm);
		}

		// Add return
		if (block.hasTag("return")) {
			ReturnTag returnTag = (ReturnTag) block.getTag("return");

			Element returnElm = doc.createElement("return");
			Element returnDescriptionElm = doc.createElement("description");

			// Add types
			String[] types = returnTag.getTypes();
			if (types != null && types.length > 1) {
				for (String type : types) {
					Element typeElm = this.doc.createElement("type");

					typeElm.setAttribute("fullname", type);

					returnElm.appendChild(typeElm);
				}
			} else if (types != null && types.length == 1)
				returnElm.setAttribute("type", types[0]);
			else if (types == null)
				returnElm.setAttribute("type", "Object");

			returnDescriptionElm.appendChild(doc.createTextNode(returnTag.getText()));
			returnElm.appendChild(returnDescriptionElm);

			elm.appendChild(returnElm);
		}

		// Add examples
		for (Tag exampleTag : block.getExamples()) {
			Element exampleElm = doc.createElement("example");

			// Add description
			Element paramDescriptionElm = doc.createElement("example");
			paramDescriptionElm.appendChild(doc.createTextNode(exampleTag.getText()));
			exampleElm.appendChild(paramDescriptionElm);

			// Append
			elm.appendChild(exampleElm);
		}

		// Add see tags
		for (SeeTag seeTag : block.getSeeTags()) {
			Element seeElm = doc.createElement("see");

			// Add class
			if (seeTag.getClassName().length() > 0)
				seeElm.setAttribute("class", seeTag.getClassName());

			// Add method
			if (seeTag.getMemberName().length() > 0)
				seeElm.setAttribute("member", seeTag.getMemberName());

			// Append
			elm.appendChild(seeElm);
		}
	}

	private Element buildMember(CommentBlock block) {
		Element memberElm = null;

		// Is method
		if (block.hasTag("method")) {
			memberElm = doc.createElement("method");
			memberElm.setAttribute("name", block.getTag("method").getText());
		}

		// Is event
		if (block.hasTag("event")) {
			memberElm = doc.createElement("event");
			memberElm.setAttribute("name", block.getTag("event").getText());
		}

		// Is option
		if (block.hasTag("option")) {
			memberElm = doc.createElement("option");
			memberElm.setAttribute("name", block.getTag("option").getText());
		}

		// Is property
		if (block.hasTag("property")) {
			memberElm = doc.createElement("property");
			memberElm.setAttribute("name", block.getTag("property").getText());

			// If it doesn't have a type default to Object
			if (!block.hasTag("type"))
				memberElm.setAttribute("type", "Object");
		}

		if (memberElm != null)
			addTags(block, memberElm);

		return memberElm;
	}
	
	private void addToXml(CommentBlock[] blocks) throws XPathExpressionException {
		Element classElm = null;
		boolean isStaticClass = false;

		for (CommentBlock block : blocks)  {
			if (block.hasTag("class")) {
				classElm = addClass(block);
				isStaticClass = block.hasTag("static");
			} else {
				Element memberElm;
				Element targetElm;

				if (classElm != null && !block.hasTag("member")) {
					targetElm = XPathHelper.findElement("members", classElm);
					if (targetElm == null)
						targetElm = (Element) classElm.appendChild(this.doc.createElement("members"));

					memberElm = buildMember(block);

					if (memberElm != null) {
						// Whole class is static, force member static
						if (isStaticClass)
							memberElm.setAttribute("static", "true");

						targetElm.appendChild(memberElm);
					}
				}
			}

			// System.err.println(block.toString());
		}
	}

	private void serializeDocument(Document doc, File out_file) throws TransformerException, TransformerConfigurationException, IOException {
		FileOutputStream fos = new FileOutputStream(out_file.getAbsolutePath());

		try {
			DOMSource domSource = new DOMSource(doc);
			StreamResult streamResult = new StreamResult(fos);
			TransformerFactory tf = TransformerFactory.newInstance();
			Transformer serializer = tf.newTransformer();

			serializer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
			//serializer.setOutputProperty(OutputKeys.DOCTYPE_SYSTEM, "users.dtd");
			serializer.setOutputProperty(OutputKeys.INDENT, "yes");

			serializer.transform(domSource, streamResult);
		} finally {
			fos.close();
		}
	}
}
