package com.fredtec;

import java.io.File;
import java.io.IOException;

import java.net.URI;
import java.util.ArrayList;

import org.apache.batik.anim.dom.SVGOMPathElement;
import org.apache.batik.bridge.*;
import org.apache.batik.anim.dom.SAXSVGDocumentFactory;
import org.apache.batik.anim.dom.SVGOMSVGElement;
import org.apache.batik.dom.svg.SVGOMMatrix;
import org.apache.batik.dom.svg.SVGOMPoint;
import org.apache.batik.dom.svg.SVGOMTransform;
import org.apache.batik.gvt.GraphicsNode;
import org.apache.batik.util.XMLResourceDescriptor;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.svg.*;


/**
 * Responsible for converting all SVG path elements into points
 */
public class SVGReader {
	private static final String PATH_ELEMENT_NAME = "path";

	
	int width = 325;
	int height = 450;
	
	private Document svgDocument;

	/**
	 * Creates an SVG Document given a URI.
	 *
	 * @param uri Path to the file.
	 * @throws Exception Something went wrong parsing the SVG file.
	 */
	public SVGReader(String uri ) throws IOException {
		setSVGDocument( createSVGDocument( uri ) );
	}

	/**
	 * Finds all the path nodes and converts them to a list of lists of points.
	 */
	public ArrayList<ArrayList<SVGPoint>> getPoints() {
		int width = 0, height = 0;
		Element svg = svgDocument.getDocumentElement();
		float scale = 1f;
		if (svg.hasAttribute("width") && svg.hasAttribute("height")) {
			width = Integer.parseInt(svg.getAttribute("width"));
			height = Integer.parseInt(svg.getAttribute("height"));
		} else if (svg.hasAttribute("viewBox")) {
			String viewBox = svg.getAttribute("viewBox");
			width = Integer.parseInt(viewBox.split(" ")[2]);
			height = Integer.parseInt(viewBox.split(" ")[3]);
		}
		System.out.println(width + " " + height);
		if (width != 0 && height != 0) {
			if (this.width < width) {
				scale =  (float)this.width / (float)width;
			}
			if (this.height < height) {
				float temp = (float)this.height / (float)height ;
				if (scale == 1f) scale = temp;
				else if (temp < scale) scale = temp;
			}
		}
		System.out.println(scale);
		NodeList pathNodes = getPathElements();
		int pathNodeCount = pathNodes.getLength();
		ArrayList<ArrayList<SVGPoint>> pointsList = new ArrayList<>();
		for( int iPathNode = 0; iPathNode < pathNodeCount; iPathNode++ ) {
			ArrayList<SVGPoint> points = new ArrayList<>();
			SVGOMPathElement node = (SVGOMPathElement)pathNodes.item(iPathNode);
			SVGTransformList transform = node.getTransform().getAnimVal();
			float length = node.getTotalLength();
			SVGMatrix matrix = new SVGOMTransform().getMatrix();
			matrix = matrix.scale(scale);
			if (transform.getNumberOfItems() == 1) {
				matrix = matrix.multiply(transform.getItem(0).getMatrix());
			}
			for (float i = 0; i < length; i++) {
				SVGPoint point = node.getPointAtLength(i);
				point = org.apache.batik.dom.svg.SVGOMPoint.matrixTransform(point, matrix);
				point = new SVGOMPoint(Math.round(point.getX()), Math.round(point.getY()));
				if (!points.contains(point)) {
					points.add(point);	
				}
				
			}
			pointsList.add(points);
		}
		return pointsList;
	}

	/**
	 * Returns a list of elements in the SVG document width names that
	 * match PATH_ELEMENT_NAME.
	 *
	 * @return The list of "path" elements in the SVG document.
	 */
	private NodeList getPathElements() {
		return getSVGDocumentRoot().getElementsByTagName( PATH_ELEMENT_NAME );
	}

	/**
	 * Returns an SVGOMSVGElement that is the document's root element.
	 *
	 * @return The SVG document typecast into an SVGOMSVGElement.
	 */
	private SVGOMSVGElement getSVGDocumentRoot() {
		return (SVGOMSVGElement)getSVGDocument().getDocumentElement();
	}

	/**
	 * This will set the document to parse. This method also initializes
	 * the SVG DOM enhancements, which are necessary to perform SVG and CSS
	 * manipulations. The initialization is also required to extract information
	 * from the SVG path elements.
	 *
	 * @param document The document that contains SVG content.
	 */
	public void setSVGDocument( Document document ) {
		initSVGDOM( document );
		this.svgDocument = document;
	}

	/**
	 * Returns the SVG document parsed upon instantiating this class.
	 *
	 * @return A valid, parsed, non-null SVG document instance.
	 */
	public Document getSVGDocument() {
		return this.svgDocument;
	}

	/**
	 * Enhance the SVG DOM for the given document to provide CSS- and SVG-specific
	 * DOM interfaces.
	 *
	 * @param document The document to enhance.
	 * @link http://wiki.apache.org/xmlgraphics-batik/BootSvgAndCssDom
	 */
	private void initSVGDOM( Document document ) {
		UserAgent userAgent = new UserAgentAdapter();
		DocumentLoader loader = new DocumentLoader( userAgent );
		BridgeContext bridgeContext = new BridgeContext( userAgent, loader );
		bridgeContext.setDynamicState( BridgeContext.DYNAMIC );

		// Enable CSS- and SVG-specific enhancements.
		(new GVTBuilder()).build( bridgeContext, document );
	}

	/**
	 * Use the SAXSVGDocumentFactory to parse the given URI into a DOM.
	 *
	 * @param uri The path to the SVG file to read.
	 * @return A Document instance that represents the SVG file.
	 * @throws Exception The file could not be read.
	 */
	private Document createSVGDocument( String uri ) throws IOException {
		String parser = XMLResourceDescriptor.getXMLParserClassName();
		SAXSVGDocumentFactory factory = new SAXSVGDocumentFactory( parser );
		return factory.createDocument( uri );
	}
}