package com.moxiecode.moxiedoc.util;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import java.util.Vector;

public class XPathHelper {
	public static Vector<Element> findElements(String xpath, Node scope) throws XPathExpressionException {
		XPathFactory xpf = XPathFactory.newInstance();
		XPath xp = xpf.newXPath();
		NodeList nl;
		Vector<Element> elements = new Vector<Element>();

		nl = (NodeList) xp.evaluate(xpath, scope, XPathConstants.NODESET);

		for (int i = 0; i < nl.getLength(); i++)
			elements.add((Element) nl.item(i));

		return elements;
	}

	public static Vector<Node> findNodes(String xpath, Node scope) throws XPathExpressionException {
		XPathFactory xpf = XPathFactory.newInstance();
		XPath xp = xpf.newXPath();
		NodeList nl;
		Vector<Node> elements = new Vector<Node>();

		nl = (NodeList) xp.evaluate(xpath, scope, XPathConstants.NODESET);

		for (int i = 0; i < nl.getLength(); i++)
			elements.add(nl.item(i));

		return elements;
	}

	public static Element findElement(String xpath, Node scope) throws XPathExpressionException {
		return (Element) findNode(xpath, scope);
	}

	public static Node findNode(String xpath, Node scope) throws XPathExpressionException {
		XPathFactory xpf = XPathFactory.newInstance();
		XPath xp = xpf.newXPath();

		return (Node) xp.evaluate(xpath, scope, XPathConstants.NODE);
	}
}
