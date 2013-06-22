// RDFFilter.java - extract RDF logic from a SAX event stream.
// Written by David Megginson, david@megginson.com
// NO WARRANTY!  This class is in the public domain.

// $Id: RDFFilter.java,v 1.4 2000/03/13 16:43:56 david Exp $


/*
TODO:
- clean up code and clarify logic
- improve efficiency for rdf:li
- consider supporting xml:space
*/

package com.megginson.sax.rdf;

import org.xml.sax.XMLReader;
import org.xml.sax.InputSource;
import org.xml.sax.Locator;
import org.xml.sax.Attributes;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;

import org.xml.sax.helpers.XMLFilterImpl;

import java.io.IOException;

import java.util.Hashtable;


/**
 * Extract RDF logic from a SAX event stream.
 *
 * <blockquote>
 * <em>This module, both source code and documentation, is in the
 * Public Domain, and comes with <strong>NO WARRANTY</strong>.</em>
 * </blockquote>
 *
 * <p>This is a SAX2 filter designed to extract RDF logic from a
 * stream of SAX2 events.  The RDF-related XML events (elements,
 * character data, etc.) are removed from the stream and the RDF
 * itself is reported through the RDFHandler.</p>
 *
 * <p>The SAX2 feature "http://megginson.com/sax/features/rdf" controls
 * RDF processing; if it is true (the default value for this filter),
 * RDF processing will be performed; if it is false, all XML markup
 * will be passed through unmodified.</p>
 *
 * <p>The SAX2 property "http://megginson.com/sax/properties/rdf-handler"
 * allows the handler for RDF events to be set or queried.  See
 * RDFHandler for more information, and note that that class has
 * convenience constants for both the feature and the property.</p>
 *
 * <p>If there are RDF-specific errors or warnings, they are reported
 * through the SAX2 error handler.  The argument will be an
 * RDFException rather than a general SAXParseException, so it is
 * easily distinguishable using instanceof or during a catch.  If 
 * the client application does not set an error handler,
 * RDF errors will go unreported and the results will be 
 * unpredictable.</p>
 *
 * <p>This filter reifies statements only when a bagID is present, or
 * when the property element itself contains an ID.</p>
 *
 * <p>This version of the filter is designed for SAX2beta2.</p>
 *
 * @author David Megginson, david@megginson.com
 * @version 1.0alpha
 * @since 1.0alpha
 * @see com.megginson.sax.rdf.RDFHandler
 * @see com.megginson.sax.rdf.RDFException
 * @see org.xml.sax.SAXFilter
 * @see org.xml.sax.ContentHandler
 * @see org.xml.sax.ErrorHandler
 */
public class RDFFilter extends XMLFilterImpl
{


    ////////////////////////////////////////////////////////////////////
    // Constructors.
    ////////////////////////////////////////////////////////////////////


    /**
     * Construct an RDFFilter with no parent.
     */
    public RDFFilter ()
    {
	init(null);
    }


    /**
     * Construct an RDFFilter with an explicit parent.
     *
     * @param parent The parent XML reader (possibly another filter).
     */
    public RDFFilter (XMLReader parent)
    {
	init(parent);
    }


    /**
     * Internal constructor.
     *
     * @param parent The parent XML reader (possibly another filter).
     */
    private void init (XMLReader parent)
    {
	setParent(parent);
	rdfHandler = null;
	rdfProcessing = true;
    }



    ////////////////////////////////////////////////////////////////////
    // Override some methods from org.xml.sax.XMLReader.
    ////////////////////////////////////////////////////////////////////


    /**
     * Get the value of a SAX2 feature.
     *
     * <p>Intercept requests for http://megginson.com/sax/features/rdf,
     * and pass all others on through.</p>
     *
     * @param name The feature name.
     * @return The current feature status (true or false).
     * @exception org.xml.sax.SAXNotSupportedException If the feature
     *            is recognized but not supported in this context.
     * @exception org.xml.sax.SAXNotRecognizedException If the feature
     *            name is not recognized.
     * @see org.xml.sax.XMLReader#getFeature
     */
    public boolean getFeature (String name)
	throws SAXNotSupportedException, SAXNotRecognizedException
    {
	if (name.equals(RDFHandler.featureName)) {
	    return rdfProcessing;
	} else {
	    return super.getFeature(name);
	}
    }


    /**
     * Set the value of a SAX2 feature.
     *
     * <p>Intercept requests for http://megginson.com/sax/features/rdf,
     * and pass all others on through.</p>
     *
     * @param name The feature name.
     * @param value The new value to set.
     * @exception org.xml.sax.SAXNotSupportedException If the feature
     *            is recognized but not supported in this context.
     * @exception org.xml.sax.SAXNotRecognizedException If the feature
     *            name is not recognized.
     * @see org.xml.sax.XMLReader#setFeature
     */
    public void setFeature (String name, boolean value)
	throws SAXNotSupportedException, SAXNotRecognizedException
    {
	if (name.equals(RDFHandler.featureName)) {
	    rdfProcessing = value;
	} else {
	    super.setFeature(name, value);
	}
    }


    /**
     * Get the value of a SAX2 property.
     *
     * <p>Intercept requests for 
     * http://megginson.com/sax/properties/rdf-handler, and pass all 
     * others on through.</p>
     *
     * @param name The property name.
     * @return The current property value as an Object.
     * @exception org.xml.sax.SAXNotSupportedException If the property
     *            is recognized but not supported in this context.
     * @exception org.xml.sax.SAXNotRecognizedException If the property
     *            name is not recognized.
     * @see org.xml.sax.XMLReader#getProperty
     */
    public Object getProperty (String name)
	throws SAXNotSupportedException, SAXNotRecognizedException
    {
	if (name.equals(RDFHandler.propertyName)) {
	    return rdfHandler;
	} else {
	    return super.getProperty(name);
	}
    }


    /**
     * Set the value of a SAX2 property.
     *
     * <p>Intercept requests for 
     * http://megginson.com/sax/properties/rdf-handler, and pass all 
     * others on through.</p>
     *
     * @param name The property name.
     * @param value The new value to set.
     * @exception org.xml.sax.SAXNotSupportedException If the property
     *            is recognized but not supported in this context.
     * @exception org.xml.sax.SAXNotRecognizedException If the property
     *            name is not recognized.
     * @see org.xml.sax.XMLReader#setProperty.
     */
    public void setProperty (String name, Object value)
	throws SAXNotSupportedException, SAXNotRecognizedException
    {
	if (name.equals(RDFHandler.propertyName)) {
	    rdfHandler = (RDFHandler)value;
	} else {
	    super.setProperty(name, value);
	}
    }


    /**
     * Parse an XML document.
     *
     * <p>The RDF filter overrides this method so that it can
     * (re)initialize the filter's state.</p>
     *
     * @param systemId The URI of the document.
     * @exception java.io.IOException Any I/O related exception.
     * @exception org.xml.sax.SAXException Any XML or client exception.
     * @see #parse(org.xml.sax.InputSource)
     * @see org.xml.sax.XMLReader.parse(java.lang.String)
     */
    public void parse (String systemId)
	throws IOException, SAXException
    {
	parse(new InputSource(systemId));
    }


    /**
     * Parse an XML document.
     *
     * <p>The RDF filter overrides this method so that it can
     * (re)initialize the filter's state.</p>
     *
     * @param input The input source for the XML document.
     * @exception java.io.IOException Any I/O related exception.
     * @exception org.xml.sax.SAXException Any XML or client exception.
     * @see #parse(java.lang.String)
     * @see org.xml.sax.XMLReader.parse(org.xml.sax.InputSource)
     */
    public void parse (InputSource input)
	throws IOException, SAXException
    {
				// Make sure we have a sane state for
				// Namespace processing.
	getParent().setFeature("http://xml.org/sax/features/namespaces", true);
	getParent().setFeature("http://xml.org/sax/features/namespace-prefixes", false);

	genCounter = 0;
	stateMax = 16;
	statePos = 0;
	states = new State[stateMax];
	locator = null;
	idTable = new Hashtable();

	super.parse(input);

	idTable = null;
	states = null;
	locator = null;
    }



    ////////////////////////////////////////////////////////////////////
    // Override methods from org.xml.sax.ContentHandler.
    ////////////////////////////////////////////////////////////////////


    /**
     * Grab the locator as it flies by.
     *
     * <p>If a locator is supplied, the RDF filter will use it for
     * RDF error reporting.</p>
     *
     * @param locator The document locator.
     * @see org.xml.sax.ContentHandler#setDocumentLocator
     */
    public void setDocumentLocator (Locator locator)
    {
	this.locator = locator;
	super.setDocumentLocator(locator);
    }


    /**
     * Start an XML element.
     *
     * <p>If RDF processing is enabled, the RDF filter will pull
     * RDF-related markup out of the SAX event stream and replace
     * it with higher-level, abstract RDF events.</p>
     *
     * @param uri The element's Namespace URI.
     * @param localName The element's local name.
     * @param rawName The elemnet's raw XML 1.0 name.
     * @param atts The element's attribute list.
     * @exception org.xml.sax.SAXException The client may throw
     *            any exception, or the filter may throw an
     *            RDFException.
     * @see org.xml.sax.ContentHandler#startElement
     */
    public void startElement (String uri, String localName,
			      String rawName, Attributes atts)
	throws SAXException
    {
				// Return now if we're not doing RDF
				// processing.
	if (!rdfProcessing) {
	    super.startElement(uri, localName, rawName, atts);
	    return;
	}

				// Create a new state.
	pushState();

				// Note the language, if specified.
	String xml_lang = atts.getValue(XMLNS, "lang");
	if (xml_lang != null) {
	    currentState.lang = xml_lang;
	}

	//
	// Look at our current state and figure out what to do.
	//

	switch (currentState.state) {


				// If we're at the top-level, look for
				// rdf:RDF to start RDF processing.
				// Note that there may be multiple
				// rdf:RDF blocks in the same document.
	case TOP_LEVEL:
	    if (RDFNS.equals(uri)) {
		if ("RDF".equals(localName)) {
		    currentState.state = IN_RDF;
		} else {
		    error("Unknown or out of context RDF element " +
			  localName);
		}
	    } else {
		currentState.state = TOP_LEVEL;
		super.startElement(uri, localName, rawName, atts);
	    }
	    break;

				// If we're immediately inside rdf:RDF,
				// any element is the start of an RDF
				// description.
	case IN_RDF:
	    currentState.state = IN_DESCRIPTION;
	    startResource(uri, localName, atts);
	    break;

				// If we're in an RDF description or
				// in a composite property (a property
				// with parseType="Resource"), any
				// element is the start of an RDF
				// property.
	case IN_DESCRIPTION:
	case IN_COMPOSITE_PROPERTY:
	    currentState.state = IN_PROPERTY;
	    startProperty(uri, localName, atts);
	    break;

				// If we're in a property of unknown
				// type and an element starts, we must
				// have been in a resource property
				// rather than a literal one.
	case IN_PROPERTY:
	    currentState.data.setLength(0);
	    currentState.previousState.state = IN_RESOURCE_PROPERTY;
	    currentState.state = IN_DESCRIPTION;
	    startResource(uri, localName, atts);
	    break;

				// If we're in a regular literal property,
				// no elements are allowed, so report
				// an error.
	case IN_LITERAL:
	    error("No markup allowed in literal property");
	    currentState.state = IN_UNKNOWN;
	    break;


				// If we're in an XML literal property
				// (parseType="Literal"), then even
				// RDF elements are treated regularly.
	case IN_XMLLITERAL:
	case IN_XML:
	    currentState.state = IN_XML;
	    super.startElement(uri, localName, rawName, atts);
	    break;


				// If we're in a property already
				// identified as a resource property,
				// then this is the second child
				// element and is illegal.
	case IN_RESOURCE_PROPERTY:
	    error("Only one element allowed inside a property element");
	    currentState.state = IN_UNKNOWN;
	    break;

				// If we're in an empty resource property,
				// nothing is allowed.
	case IN_EMPTY_RESOURCE_PROPERTY:
	    error("No content allowed in property with rdf:resource, rdf:bagID, or property attributes");
	    currentState.state = IN_UNKNOWN;
	    break;

				// Element with an unknown RDF name
				// or an unknown RDF attribute name,
				// and must ignore it and its
				// descendants.
	case IN_UNKNOWN:
	    // NO-OP;
	    break;

				// No other states expected.
	default:
	    throw new RuntimeException("Unexpected state");
	}
    }


    /**
     * End an XML element.
     *
     * <p>Extract any RDF-specific events, and pass the rest
     * on through.</p>
     *
     * @param uri The element's Namespace URI.
     * @param localName The element's local name.
     * @param rawName The elemnet's raw XML 1.0 name.
     * @exception org.xml.sax.SAXException The client may throw
     *            any exception, or the filter may throw an
     *            RDFException.
     * @see org.xml.sax.ContentHandler#endElement
     */
    public void endElement (String uri, String localName,
			    String rawName)
	throws SAXException
    {
				// Return now if we're not doing RDF
				// processing.
	if (!rdfProcessing) {
	    super.endElement(uri, localName, rawName);
	    return;
	}


	//
	// Look at our current state and figure out what to do.
	//

	switch (currentState.state) {


				// If we're at the top level or in
				// an XML literal (parseType="Literal"),
				// then treat as regular XML markup.
	case TOP_LEVEL:
	case IN_XML:
	    super.endElement(uri, localName, rawName);
	    break;

				// If we're in rdf:RDF, don't do anything
				// special; we'll pop back up to the
				// top level automatically.
	case IN_RDF:
	    // NO-OP
	    break;

				// If we're in a description, signal
				// the end of the current resource
				// description.
	case IN_DESCRIPTION:
	    endResource(uri, localName);
	    break;

				// If we're in a property of unknown
				// type it's a literal property; signal
				// the end in either case.
	case IN_LITERAL:
	case IN_PROPERTY:
	    endLiteralProperty(uri, localName);
	    break;

				// If we're in a resource property,
				// signal its end.
	case IN_RESOURCE_PROPERTY:
	case IN_EMPTY_RESOURCE_PROPERTY:
	    endResourceProperty(uri, localName);
	    break;

				// If we're in a composite property
				// (parseType="Resource"), signal its
				// end.
	case IN_COMPOSITE_PROPERTY:
	    endCompositeProperty(uri, localName);
	    break;

				// If we're in a literal XML property
				// (parseType="Literal"), signal its
				// end.
	case IN_XMLLITERAL:
	    endLiteralXMLProperty(uri, localName);
	    break;

				// Element with an unknown RDF name
				// or an unknown RDF attribute name,
				// and must ignore it and its
				// descendants.
	case IN_UNKNOWN:
	    // NO-OP;
	    break;

				// No other states expected.
	default:
	    throw new RuntimeException("Unexpected state");

	}

				// Pop up to the previous state.
	popState();
    }


    /**
     * Character data.
     *
     * <p>Extract any RDF-specific events, and pass the rest
     * on through.</p>
     *
     * @param ch An array of characters.
     * @param start The starting position in the array.
     * @param length The number of characters to use.
     * @exception org.xml.sax.SAXException The client may throw
     *            any exception, or the filter may throw an
     *            RDFException.
     * @see org.xml.sax.ContentHandler#characters
     */
    public void characters (char ch[], int start, int length)
	throws SAXException
    {
				// Return now if we're not doing RDF
				// processing.
	if (!rdfProcessing) {
	    super.characters(ch, start, length);
	    return;
	}


	//
	// Look at our current state and figure out what to do.
	//

	switch (currentState.state) {

				// If we're at the top level or in
				// an XML literal, it's regular XML
				// character data.
	case TOP_LEVEL:
	case IN_XMLLITERAL:
	case IN_XML:
	    super.characters(ch, start, length);
	    break;

				// If we're in a context where character
				// data is not allowed, report an error
				// for anything but whitespace.
	case IN_RDF:
	case IN_DESCRIPTION:
	case IN_RESOURCE_PROPERTY:
	case IN_COMPOSITE_PROPERTY:
	    if (!isWhitespace(ch, start, length)) {
		error("Out of context character data");
		currentState.state = IN_UNKNOWN;
	    }
	    break;

				// If we're in a property of unknown
				// type, hang on to the character data;
				// if it's non-whitespace, identify
				// the property as a literal.
	case IN_PROPERTY:
	    currentState.data.append(ch, start, length);
	    if (!isWhitespace(ch, start, length)) {
		currentState.state = IN_LITERAL;
	    }
	    break;

				// If we're in a property known to be
				// a literal, hang on to the characters.
	case IN_LITERAL:
	    currentState.data.append(ch, start, length);
	    break;

				// No content allowed in a property
				// with rdf:resource specified.
	case IN_EMPTY_RESOURCE_PROPERTY:
	    error("No content allowed in property with rdf:resource, rdf:bagID, or property attributes");
	    currentState.state = IN_UNKNOWN;
	    break;

				// Element with an unknown RDF name
				// or an unknown RDF attribute name,
				// and must ignore it and its
				// descendants.
	case IN_UNKNOWN:
	    // NO-OP;
	    break;

				// No other states expected.
	default:
	    throw new RuntimeException("Unexpected state");

	}
    }


    /**
     * Whitespace in element content.
     *
     * <p>Extract any RDF-specific events, and pass the rest
     * on through.</p>
     *
     * @param ch An array of characters.
     * @param start The starting position in the array.
     * @param length The number of characters to use.
     * @exception org.xml.sax.SAXException The client may throw
     *            any exception, or the filter may throw an
     *            RDFException.
     * @see org.xml.sax.ContentHandler#ignorableWhitespace
     */
    public void ignorableWhitespace (char ch[], int start, int length)
	throws SAXException
    {
				// Return now if we're not doing RDF
				// processing.
	if (!rdfProcessing) {
	    super.ignorableWhitespace(ch, start, length);
	    return;
	}


	//
	// Look at our current state and figure out what to do.
	//

	switch (currentState.state) {

				// If we're outside of RDF, treat
				// this as regular XML to preserve
				// the distinction (in case anyone
				// cares).
	case TOP_LEVEL:
	case IN_XMLLITERAL:
	case IN_XML:
	    super.ignorableWhitespace(ch, start, length);
	    break;


				// Otherwise, do not distinguish
				// this from other character data.
	default:
	    characters(ch, start, length);
	    break;
	}
    }



    ////////////////////////////////////////////////////////////////////
    // RDF-processing functions.
    ////////////////////////////////////////////////////////////////////


    /**
     * Start an RDF resource description.
     *
     * <p>This method handles the start of a regular RDF resource
     * description, including attribute processing.  The startElement
     * callback invokes this when it determines that an element
     * starts a resource description; the logic of sorting out
     * the RDF is here.</p>
     *
     * @param uri The element's Namespace URI.
     * @param name The element's local name.
     * @param atts The element's attributes.
     * @see #endResource
     * @see #startProperty
     * @exception org.xml.sax.SAXException The client may throw
     *            any exception, or the filter may throw an
     *            RDFException.
     */
    private void startResource (String uri, String name, Attributes atts)
	throws SAXException
    {
				// Start by assuming we're not in
				// a container, and that we haven't
				// found any property attributes.
	currentState.inContainer = false;
	currentState.hasPropertyAtts = false;


				// Now, if we're in anything in the
				// RDF Namespace, make sure we know
				// what we're doing.
	if (RDFNS.equals(uri)) {

				// Is it a container?
	    if(isRDFContainer(name)) {
		currentState.inContainer = true;

				// Is it anything we know about?
	    } else if (!isRDFResource(name)) {
		error("Unknown or out of context RDF description element " +
		      name);
		currentState.state = IN_UNKNOWN;
		return;
	    }
	}

				// RDF-specific identifiers.  We'll
				// scan the attributes for these
				// below, and will keep track of
				// how many we find (since, except
				// for bagID, they're mutually
				// exclusive).
	int identifiersFound = 0;
	String id = null;
	String about = null;
	String aboutEach = null;
	String aboutEachPrefix = null;
	String bagId = null;


				// Now, scan the attributes for the
				// standard RDF identifiers.  Skip
				// any xml:* attributes, and assume
				// that anything else is a property
				// attribute (except rdf:value).
	for (int i = 0; i < atts.getLength(); i++) {

	    String auri = atts.getURI(i);
	    String aname = atts.getLocalName(i);
	    String value = atts.getValue(i);

				// Attributes in the RDF Namespace.
	    if (RDFNS.equals(auri) ||
		("".equals(auri) && RDFNS.equals(uri))) {

		if ("ID".equals(aname)) {
		    id = "#" + checkId(value);
		    identifiersFound++;
		} 

		else if ("about".equals(aname)) {
		    about = value;
		    identifiersFound++;
		} 

		else if ("bagID".equals(aname)) {
		    bagId = "#" + checkId(value);
		} 

		else if ("aboutEach".equals(aname)) {
		    aboutEach = value;
		    identifiersFound++;
		} 

		else if ("aboutEachPrefix".equals(aname)) {
		    aboutEachPrefix = value;
		    identifiersFound++;
		} 

		else if (isRDFProperty(aname) ||
			 (currentState.inContainer &&
			  isItemAttribute(aname))) {
		    currentState.hasPropertyAtts = true;
		} 

		else {
		    error("Unknown or out of context RDF attribute: " +
			  aname);
		    currentState.state = IN_UNKNOWN;
		    return;
		}

	    } 


				// Attributes in the XML Namespace.
	    else if (XMLNS.equals(auri)) {
		// NO-OP
	    } 

				// Any other attributes are property
				// attributes.
	    else {
		currentState.hasPropertyAtts = true;
	    }
	}


				// Set the bag ID (if any); if there is
				// one, make sure to report the type as
				// part of the bag.
	currentState.bagId = bagId;
	if (bagId != null) {
	    reportStatement(bagId, RDF_TYPE, RDF_BAG,
			   RDFHandler.SUBJECT_URI, false, null, null);
	}

				// Now, how many identifiers were found?
				// If there was more than one (not counting
				// bagID), then the RDF is malformed.
				// If there is none, generate one.
	if (identifiersFound == 0) {
	    currentState.subject = genID();
	    currentState.subjectType = RDFHandler.SUBJECT_GENERATED;
	} else if (identifiersFound > 1) {
	    error("Only one of about, ID, aboutEach, and aboutEachPrefix allowed");
	    currentState.state = IN_UNKNOWN;
	    return;
	} else if (id != null) {
	    currentState.subject = id;
	    currentState.subjectType = RDFHandler.SUBJECT_URI;
	} else if (about != null) {
	    currentState.subject = about;
	    currentState.subjectType = RDFHandler.SUBJECT_URI;
	} else if (aboutEach != null) {
	    currentState.subject = aboutEach;
	    currentState.subjectType = RDFHandler.SUBJECT_DISTRIBUTED;
	} else if (aboutEachPrefix != null) {
	    currentState.subject = aboutEachPrefix;
	    currentState.subjectType = RDFHandler.SUBJECT_URI_PATTERN;
	}

				// Now, report the class name if
				// it is not rdf:Description, and
				// check for the special case of
				// containers
	if (RDFNS.equals(uri)) {
	    if (currentState.inContainer &&
		(about != null || aboutEach != null ||
		 aboutEachPrefix != null || bagId != null)) {
		error("RDF containers do not allow about, aboutEach, aboutEachPrefix, or bagID");
	    }
	    if (!"Description".equals(name)) {
	    reportStatement(currentState.subject, RDF_TYPE, uri + name,
			   RDFHandler.SUBJECT_URI, false, null, null);
	    }
	}

				// The current subject becomes the
				// parent resource's object.
	currentState.previousState.object = currentState.subject;


				// Report any property atts, if we think
				// we've seen some.
	if (currentState.hasPropertyAtts) {
	    reportPropAtts(uri, atts);
	}

    }


    /**
     * End a resource.
     *
     * <p>This method handles the end of a regular RDF resource
     * description.  The endElement callback invokes this when it 
     * determines that an element starts a property.  Currently,
     * no special action is required.</p>
     *
     * @param uri The property element's Namespace URI.
     * @param localName The property element's local name.
     * @see #startResource
     * @exception org.xml.sax.SAXException The client may throw
     *            any exception, or the filter may throw an
     *            RDFException.
     */
    private void endResource (String uri, String localName)
	throws SAXException
    {
	// Do nothing for now.
    }


    /**
     * Start a property element.
     *
     * <p>This method handles the start of a regular RDF property,
     * including attribute processing.  The startElement
     * callback invokes this when it determines that an element
     * starts a property; the logic of sorting out
     * the RDF is here.</p>
     *
     * @param uri The property element's Namespace URI.
     * @param localName The property element's local name.
     * @param atts The property element's attributes.
     * @see #endResourceProperty
     * @see #endCompositeProperty
     * @see #endLiteralProperty
     * @see #endLiteralXMLProperty
     * @see #startResource
     * @exception org.xml.sax.SAXException The client may throw
     *            any exception, or the filter may throw an
     *            RDFException.
     */
    private void startProperty (String uri, String localName, Attributes atts)
	throws SAXException
    {
				// If the resource element is in the
				// RDF Namespace, make sure that it's
				// one that's allowed.
	if (RDFNS.equals(uri) &&
	    !isRDFProperty(localName) &&
	    !(currentState.inContainer && "li".equals(localName))) {

	    error("Unknown or out of context RDF property element " +
		  localName);
	    currentState.state = IN_UNKNOWN;
	    return;
	}

				// Check that containers have only rdf:li
	if (currentState.inContainer) {
	    if (currentState.hasPropertyAtts) {
		error("RDF containers may not mix property attributes and property elements");
	    }
	    if (!RDFNS.equals(uri) || !"li".equals(localName)) {
		error("RDF containers allow only rdf:li properties");
	    }
	}

				// Grab the current predicate and
				// clear the cached object.

				// FIXME: inefficient
	currentState.predicate = (uri + localName).intern();
	currentState.object = null;

				// Status to be captured from the
				// attribute list.
	String resource = null;
	String id = null;
	String bagId = null;
	String parseType = null;

				// More status.
	currentState.hasPropertyAtts = false;;

				// Walk through all of the RDF
				// attributes looking for the standard
				// ones.
	for (int i = 0; i < atts.getLength(); i++) {
	    String auri = atts.getURI(i);
	    String aname = atts.getLocalName(i);
	    String value = atts.getValue(i);

				// RDF attributes.
	    if (RDFNS.equals(auri) ||
		("".equals(auri) && RDFNS.equals(uri))) {

		if ("resource".equals(aname)) {
		    resource = value;
		} else if ("ID".equals(aname)) {
		    id = "#" + checkId(value);
		} else if ("bagID".equals(aname)) {
		    bagId = "#" + checkId(value);
		} else if ("parseType".equals(aname)) {
		    if ("Resource".equals(value)) {
			parseType = "Resource";
		    } else {
				// FIXME: add warning
			parseType = "Literal";
		    }
		} else if (isRDFProperty(aname)) {
		    currentState.hasPropertyAtts = true;
		} else {
		    error("Unknown or out of context RDF attribute: " +
			  aname);
		    currentState.state = IN_UNKNOWN;
		    return;
		}

				// XML attributes.
	    } else if (XMLNS.equals(auri)) {
		// NO-OP

				// Other attributes (property atts).
	    } else {
		currentState.hasPropertyAtts = true;
	    }
	}

				// More container checking (then clear
				// the flag).
	if (currentState.inContainer) {
	    if (id != null || bagId != null) {
		error("rdf:li does not allow an ID or bagID attribute");
	    } else if (parseType != null && resource != null) {
		error("rdf:li does not allow both parseType and resource");
	    } else if (currentState.hasPropertyAtts) {
		error("rdf:li does not allow property attributes");
	    }
	    currentState.inContainer = false;
	}


				// Here's the ID for the bag of the
				// statement, if any.
	currentState.statementId = id;


				// Try to figure out what kind of property
				// we're dealing with; we might not know
				// yet, but we can tell if the
				// rdf:parseType attribute or rdf:resource
				// attributes are set, or if there are
				// property attributes.

				// rdf:parseType is set
	if (parseType != null) {

				// Error checks for parseType
	    if (resource != null) {
		error("rdf:resource not allowed with rdf:parseType");
		currentState.state = IN_UNKNOWN;
		return;
	    } else if (bagId != null) {
		error("rdf:bagID not allowed with rdf:parseType");
		currentState.state = IN_UNKNOWN;
		return;
	    } else if (currentState.hasPropertyAtts) {
		error("Property attributes not allowed with rdf:parseType");
		currentState.state = IN_UNKNOWN;
		return;
	    }

	    if (parseType == "Literal") {
		currentState.state = IN_XMLLITERAL;
		if (rdfHandler != null) {
		    rdfHandler.startXMLStatement(currentState.subjectType,
						 currentState.subject,
						 currentState.predicate,
						 currentState.lang);
		}
	    } else {
		pushState();
		currentState.state = IN_COMPOSITE_PROPERTY;
		currentState.bagId = null;
		currentState.subject = genID();
	    }

				// rdf:resource is set
	} else if (resource != null || bagId != null ||
		   currentState.hasPropertyAtts) {
	    if (resource == null) {
		resource = genID();
	    }
	    if (bagId != null) {
	    reportStatement(bagId, RDF_TYPE, RDF_BAG,
			   RDFHandler.SUBJECT_URI, false, null, null);
	    }
	    currentState.state = IN_EMPTY_RESOURCE_PROPERTY;
	    String oldSubject = currentState.subject;
	    currentState.subject = resource;
	    currentState.object = resource;
	    currentState.bagId = bagId;
	    if (currentState.hasPropertyAtts) {
		reportPropAtts(uri, atts);
	    }
	    currentState.subject = oldSubject;
	}
    }


    /**
     * Handle the end of a resource property element.
     *
     * <p>This method reports the property to the RDF handler.</p>
     *
     * @param uri The property element's Namespace URI.
     * @param localName The property element's local name.
     * @exception org.xml.sax.SAXException The client may throw
     *            any exception, or the filter may throw an
     *            RDFException.
     */
    private void endResourceProperty (String uri, String localName)
	throws SAXException
    {
	reportStatement(currentState.subject,
		       currentState.predicate,
		       currentState.object,
		       currentState.subjectType,
		       false,
		       currentState.bagId,
		       currentState.statementId);
    }


    /**
     * Handle the end of a composite property element.
     *
     * <p>This method reports the property to the RDF handler.</p>
     *
     * @param uri The property element's Namespace URI.
     * @param localName The property element's local name.
     * @exception org.xml.sax.SAXException The client may throw
     *            any exception, or the filter may throw an
     *            RDFException.
     */
    private void endCompositeProperty (String uri, String localName)
	throws SAXException
    {
	reportStatement(currentState.previousState.subject,
		       currentState.predicate,
		       currentState.subject,
		       currentState.previousState.subjectType,
		       false,
		       currentState.bagId,
		       currentState.statementId);
	popState();
    }


    /**
     * Handle the end of a literal property element.
     *
     * <p>Report the property to the RDF handler.</p>
     *
     * @param uri The property element's Namespace URI.
     * @param localName The property element's local name.
     * @exception org.xml.sax.SAXException The client may throw
     *            any exception, or the filter may throw an
     *            RDFException.
     */
    private void endLiteralProperty (String uri, String localName)
	throws SAXException
    {
	if (RDFNS.equals(uri) && isRDFResourceProperty(localName)) {
	    warning("RDF property `" + localName +
		    "' expects a resource value, not a literal");
	}

	reportStatement(currentState.subject,
			currentState.predicate,
			currentState.data.toString(),
			currentState.subjectType,
			true,
			currentState.bagId,
			currentState.statementId);
	currentState.data.setLength(0);
    }


    /**
     * Handle the end of an XML literal property.
     *
     * <p>Report the end to the RDF handler.</p>
     *
     * @param uri The property element's Namespace URI.
     * @param localName The property element's local name.
     * @exception org.xml.sax.SAXException The client may throw
     *            any exception, or the filter may throw an
     *            RDFException.
     */
    private void endLiteralXMLProperty (String uri, String localName)
	throws SAXException
    {
	if (RDFNS.equals(uri) && isRDFResourceProperty(localName)) {
	    warning("RDF property " + localName +
		    " expects a resource value, not a literal");
	}

	if (rdfHandler != null) {
	    rdfHandler.endXMLStatement();
	}
    }



    ////////////////////////////////////////////////////////////////////
    // Utility methods.
    ////////////////////////////////////////////////////////////////////


    /**
     * Report all property attributes specified.
     *
     * <p>This method walks through a property list and reports
     * all property attributes in it; that is, all attributes not
     * in the RDF or XML Namespaces, together with rdf:value.</p>
     *
     * @param parentURI The URI of the parent element.
     * @param atts The attribute list containing property atts.
     * @exception org.xml.sax.SAXException The client may throw
     *            any exception, or the filter may throw an
     *            RDFException.
     */
    private void reportPropAtts (String parentURI, Attributes atts)
	throws SAXException
    {
	for (int i = 0; i < atts.getLength(); i++) {
	    String auri = atts.getURI(i);

	    String aname = atts.getLocalName(i);
	    String value = atts.getValue(i);
	    
				// Inherit the Namespace URI of
				// the parent element (RDF SOP).
	    if ("".equals(auri)) {
		auri = parentURI;
	    }

				// Handle RDF attributes properly,
				// and warn if a resource property
				// is going to get a literal value.

				// Check for containers, which may
				// have only itemized attributes
	    if (currentState.inContainer) {
		if (!RDFNS.equals(auri) || !isItemAttribute(aname)) {
		    error("Only rdf:_nnn property attributes allowed for containers");
		}
		reportStatement(currentState.subject,
				auri + aname,
				value,
				currentState.subjectType,
				true,
				currentState.bagId,
				null);
	    } else if (RDFNS.equals(auri)) {
		if (isRDFProperty(aname)) {
		    if (isRDFResourceProperty(aname)) {
			warning("RDF property `" + aname +
				"' expects a resource value, not a literal");
		    }
		    reportStatement(currentState.subject,
				    auri + aname,
				    value,
				    currentState.subjectType,
				    true,
				    currentState.bagId,
				    null);
		}

				// Anything else except xml:
				// attributes make statements.
	    } else if (!XMLNS.equals(aname)) {
		reportStatement(currentState.subject,
				auri + aname,
				value,
				currentState.subjectType,
				true,
				currentState.bagId,
				null);
	    }
	}
    }


    /**
     * Report an atomic statement.
     *
     * <p>This method invokes the literalStatement or resourceStatement
     * methods in the RDF handler.  It also reifies the property if
     * there is a bagID or a statement ID.</p>
     *
     * <p>This method also handles rdf:li property, replacing it
     * with rdf:_n.</p>
     *
     * @param subject The statement's subject.
     * @param predicate The statement's predicate.
     * @param object The statement's object (literal or resource).
     * @param sType The statement's subject type.
     * @param isLiteral true if the statement's object is a literal.
     * @param bagID The bagID of the resource description.
     * @param statementID The ID for the reified statement.
     * @exception org.xml.sax.SAXException The client may throw
     *            any exception, or the filter may throw an
     *            RDFException.
     */
    private void reportStatement (String subject, String predicate,
				 String object, int sType, boolean isLiteral,
				 String bagId, String statementId)
	throws SAXException
    {
				// Handle rdf:li by replacing it
				// with a sequential property.
	if (RDF_LI.equals(predicate)) {
	    StringBuffer buf = new StringBuffer(RDFNS);
	    buf.append('_');
	    buf.append(currentState.previousState.liCounter++);
	    predicate = buf.toString().intern();
	}

				// If the handler is non-null, report
				// the property.
	if (rdfHandler != null) {
	    if (isLiteral) {
		rdfHandler.literalStatement(sType, subject, predicate,
					    object, currentState.lang);
	    } else {
		rdfHandler.resourceStatement(sType, subject, predicate,
					     object);
	    }
	}

				// If we need to reify, make sure
				// we have an ID for the statement.
	if (statementId == null && bagId != null) {
	    statementId = genID();
	}

				// If there's a bagID, generate a
				// list item for the bag.
	if (bagId != null) {
				// FIXME: inefficient (any choice?)
	    StringBuffer liProp = new StringBuffer(RDFNS);
	    liProp.append('_');
	    liProp.append(currentState.previousState.bagLiCounter++);
	    reportStatement(bagId, liProp.toString(), statementId,
			   RDFHandler.SUBJECT_URI, false, null, null);
	}

				// Reify the statement if the
				// reification is reachable (but not
				// otherwise).
	if (statementId != null) {
	    reportStatement(statementId, RDF_TYPE, RDF_STATEMENT,
			   RDFHandler.SUBJECT_URI, false, null, null);
	    reportStatement(statementId, RDF_SUBJECT, subject,
			   RDFHandler.SUBJECT_URI, false, null, null);
	    reportStatement(statementId, RDF_PREDICATE, predicate,
			   RDFHandler.SUBJECT_URI, false, null, null);
	    reportStatement(statementId, RDF_OBJECT, object,
			   RDFHandler.SUBJECT_URI, isLiteral, null, null);
	}
    }


    /**
     * Issue an RDF warning.
     *
     * <p>The warning is reported to the SAX2 error handler, if
     * any, with an RDFException as the argument.</p>
     *
     * @param message The warning message.
     * @exception org.xml.sax.SAXException The client may throw
     *            any exception, or the filter may throw an
     *            RDFException.
     */
    private void warning (String message)
	throws SAXException
    {
	ErrorHandler h = super.getErrorHandler();
	if (h != null) {
	    if (locator != null) {
		h.warning(new RDFException(message, locator));
	    } else {
		h.warning(new RDFException(message));
	    }
	}
    }


    /**
     * Issue an RDF error.
     *
     * <p>The error is reported to the SAX2 error handler, if
     * any, with an RDFException as the argument.</p>
     *
     * @param message The error message.
     * @exception org.xml.sax.SAXException The client may throw
     *            any exception, or the filter may throw an
     *            RDFException.
     */
    private void error (String message)
	throws SAXException
    {
	ErrorHandler h = super.getErrorHandler();
	if (h != null) {
	    if (locator != null) {
		h.error(new RDFException(message, locator));
	    } else {
		h.error(new RDFException(message));
	    }
	}
    }


    /**
     * Push a new state.
     *
     * <p>This method manages creating a new state.  States are kept
     * in an array and reused, so the number of states allocated is
     * never greater than the depth of the elements in the
     * XML document.</p>
     */
    private void pushState ()
    {
	if (statePos >= stateMax) {
	    State newStates[] = new State[statePos * 2];
	    System.arraycopy(states, 0, newStates, 0, statePos);
	    states = newStates;
	}

	State newState = states[statePos];
	if (newState == null) {
	    newState = states[statePos] = new State();
	}

	if (currentState != null) {
	    newState.assign(currentState);
	}
	currentState = newState;
	statePos++;
    }


    /**
     * Revert to a previous state.
     */
    private void popState ()
    {
	statePos--;
	if (statePos > 0) {
	    currentState = states[statePos-1];
	} else {
	    currentState = null;
	}
    }


    /**
     * Generate an ID for an anonymous resource.
     *
     * <p>The generated ID is surrounded by braces so that it's
     * easily distinguishable from a proper URI.</p>
     *
     * @return The generated ID as a string.
     */
    private String genID ()
    {
	return "{__GEN" + genCounter++ + "}";
    }


    /**
     * Test whether an attribute name is a proper container item.
     *
     * @return true if the attribute name is _ followed by a positive
     *         integer.
     */
    private boolean isItemAttribute (String name)
    {
	char ch[] = name.toCharArray();
	if (ch[0] != '_') {
	    return false;
	}

	for (int i = 1; i < ch.length; i++) {
	    switch (ch[i]) {
	    case '0':
	    case '1':
	    case '2':
	    case '3':
	    case '4':
	    case '5':
	    case '6':
	    case '7':
	    case '8':
	    case '9':
		break;
	    default:
		return false;
	    }
	}
	return true;
    }


    /**
     * Test whether a character array is all whitespace.
     *
     * @return true if the array segment contains only whitespace,
     *         false otherwise.
     */
    private boolean isWhitespace (char ch[], int start, int length)
    {
	for (int i = start; i < start + length; i++) {
	    switch (ch[i]) {
	    case ' ':
	    case '\t':
	    case '\n':
	    case '\r':
		break;
	    default:
		return false;
	    }
	}
	return true;
    }


    /**
     * Check for the name of a predefined RDF resource.
     *
     * @param name The name to check.
     */
    private boolean isRDFResource (String name)
    {
	return ("Description".equals(name) ||
		"Statement".equals(name) ||
		isRDFContainer(name));
    }


    /**
     * Check for the name of an RDF container element.
     */
    private boolean isRDFContainer (String name)
    {
	return ("Seq".equals(name) ||
		"Alt".equals(name) ||
		"Bag".equals(name));
    }


    /**
     * Check for the name of a predefined RDF property.
     *
     * <p>This method tests whether the name provided is the name
     * of a predefined RDF property.</p>
     *
     * @param name The name to test.
     */
    private boolean isRDFProperty (String name)
    {
	return (isRDFResourceProperty(name) ||
		"value".equals(name));
    }


    /**
     * Check for the name of a predefined RDF resource property.
     *
     * <p>Some predefined RDF properties explicitly expect
     * resource values; this method checks to see if the
     * name given matches on of those.</p>
     *
     * @param The local name to check (assumed to be in the
     *        RDF Namespace).
     */
    private boolean isRDFResourceProperty (String name)
    {
	return ("type".equals(name) ||
		"subject".equals(name) ||
		"predicate".equals(name) ||
		"object".equals(name));
    }


    /**
     * Check that a local ID hasn't already been used.
     *
     * @param id The ID to check.
     * @return The original ID.
     */
    private String checkId (String id)
	throws SAXException
    {
	if (idTable.containsKey(id)) {
	    error("Duplicate RDF ID or bagID: " + id);
	} else {
	    idTable.put(id, Boolean.TRUE);
	}
	    
	return id;
    }



    ////////////////////////////////////////////////////////////////////
    // Internal Constants.
    ////////////////////////////////////////////////////////////////////


				// Namespaces
    private final static String RDFNS =
	"http://www.w3.org/1999/02/22-rdf-syntax-ns#";
    private final static String XMLNS =
	"http://www.w3.org/XML/1998/namespace";


				// Element names
    private final static String RDF_STATEMENT =
	(RDFNS + "Statement").intern(); 
    private final static String RDF_BAG =
	(RDFNS + "Bag").intern(); 
    private final static String RDF_LI =
	(RDFNS + "li").intern(); 
    private final static String RDF_TYPE =
	(RDFNS + "type").intern();
    private final static String RDF_SUBJECT =
	(RDFNS + "subject").intern();
    private final static String RDF_PREDICATE =
	(RDFNS + "predicate").intern();
    private final static String RDF_OBJECT =
	(RDFNS + "object").intern();


				// State types
    private final static int TOP_LEVEL = 1;
    private final static int IN_RDF = 2;
    private final static int IN_DESCRIPTION = 3;
    private final static int IN_PROPERTY = 4;
    private final static int IN_RESOURCE_PROPERTY = 5;
    private final static int IN_EMPTY_RESOURCE_PROPERTY = 6;
    private final static int IN_LITERAL = 7;
    private final static int IN_XMLLITERAL = 8;
    private final static int IN_COMPOSITE_PROPERTY = 9;
    private final static int IN_XML = 10;
    private final static int IN_UNKNOWN = 11;



    ////////////////////////////////////////////////////////////////////
    // Internal state.
    ////////////////////////////////////////////////////////////////////

				// General status stuff.
    private int genCounter;
    private State currentState;
    private Locator locator;
    private boolean rdfProcessing;
    private RDFHandler rdfHandler;
    private Hashtable idTable;

				// State stack.
    private State states[];
    int statePos;
    int stateMax;



    ////////////////////////////////////////////////////////////////////
    // Snapshot of an RDF state.
    ////////////////////////////////////////////////////////////////////


    /**
     * Internal: a single RDF processing state.
     *
     * <p>These states are designed to be reused.</p>
     */
    class State
    {
	State ()
	{
	    state = TOP_LEVEL;
	    inContainer = false;
	    hasPropertyAtts = false;
	    subject = null;
	    predicate = null;
	    object = null;
	    data = new StringBuffer();
	    bagId = null;
	    statementId = null;
	    subjectType = -1;
	    previousState = null;
	    liCounter = 1;
	    bagLiCounter = 1;
	    lang = null;
	}

	void assign (State inState)
	{
	    state = inState.state;
	    inContainer = inState.inContainer;
	    hasPropertyAtts = inState.hasPropertyAtts;
	    subject = inState.subject;
	    predicate = inState.predicate;
	    object = inState.object;
	    data = inState.data;
	    bagId = inState.bagId;
	    statementId = inState.statementId;
	    subjectType = inState.subjectType;
	    previousState = inState;
	    liCounter = inState.liCounter;
	    bagLiCounter = inState.bagLiCounter;
	    lang = inState.lang;
	}

	int state;

	boolean inContainer;
	boolean hasPropertyAtts;
	
	String subject;
	String predicate;
	String object;
	StringBuffer data;
	String bagId;
	String statementId;
	int subjectType;
	State previousState;
	int liCounter;
	int bagLiCounter;
	String lang;
    }

}

// end of RDFFilter.java
