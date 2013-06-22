// TestRDFFilter.java - test application for RDFFilter.
// Written by David Megginson, david@megginson.com
// NO WARRANTY!  This class is in the public domain.

// $Id: TestRDFFilter.java,v 1.2 2000/03/13 16:44:36 david Exp $

import java.io.FileReader;
import java.io.IOException;

import com.megginson.sax.rdf.RDFReader;
import com.megginson.sax.rdf.RDFHandler;
import com.megginson.sax.rdf.RDFException;

import org.xml.sax.XMLReader;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.InputSource;
import org.xml.sax.helpers.XMLReaderFactory;
import org.xml.sax.helpers.DefaultHandler;


/**
 * Test application for RDF filter.
 *
 * <blockquote>
 * <em>This application, both source code and documentation, is in the
 * Public Domain, and comes with <strong>NO WARRANTY</strong>.</em>
 * </blockquote>
 *
 * <p>This application will display RDF events to standard output, 
 * together with select XML events (start/end element and character-related
 * events).  It is intended for testing the functionality of RDFFilter
 * with different RDF documents.  It will also display the amount
 * of memory used before and after each RDF document processing;
 * that number goes to standard error rather than standard output.</p>
 *
 * <p>This application will use the org.xml.sax.driver property, if
 * set, to select a SAX2 XML reader.  If that property is not set,
 * the application will check the value of the org.xml.sax.parser
 * property and wrap an XMLParserAdapter around it, using
 * AElfred by default if no org.xml.sax.parser property is 
 * specified.</p> 
 *
 * <p>You must have SAX2beta2 and a SAX driver installed to use this
 * test class.</p>
 *
 * @author David Megginson, david@megginson.com
 * @version 1.0alpha
 * @see com.megginson.sax.rdf.RDFFilter
 * @see com.megginson.sax.rdf.RDFHandler
 */
public class TestRDFFilter extends DefaultHandler implements RDFHandler
{

    /**
     * Main entry point.
     *
     * <p>Process all XML documents supplied on the command line
     * (local file names, not URIs), and report the events.</p>
     *
     * @param args The filenames to process.
     */
    public static void main (String args[])
	throws Exception
    {
	TestRDFFilter handler = new TestRDFFilter();
	RDFReader rdf = new RDFReader();
	rdf.setRDFHandler(handler);
	rdf.setContentHandler(handler);
	rdf.setErrorHandler(handler);

	if (args.length != 1) {
	    System.err.println("Usage: java TestRDFFilter <file-or-URL>");
	    System.exit(2);
	}

	try {

	    try {
				// file name?
		FileReader r = new FileReader(args[0]);
		rdf.readRDF(r);
		r.close();
	    } catch (IOException e) {
				// URI?
		rdf.readRDF(args[0]);
	    }
				// Check for embedded exceptions
	} catch (SAXException e) {
	    if (e.getException() != null) {
		throw e.getException();
	    } else {
		throw e;
	    }
	}

	System.exit(errorStatus);
    }



    ////////////////////////////////////////////////////////////////////
    // Implementation of RDFHandler.
    //
    // The RDF events are reported through callbacks.  In serious
    // applications, these callbacks would be implemented in a
    // separate class, but since this is a simple demo, they're
    // included here.  Note that the application has to register
    // itself to receive these events.
    ////////////////////////////////////////////////////////////////////


    /**
     * Report a literal statement.
     *
     * <p>The RDF filter will invoke this method for every statement
     * in the RDF document that has a literal value.  An RDF
     * statement is, essentially, a single NAME=VALUE pair for
     * an object of some sort.</p>
     *
     * <p>Note that the subject may or may not be a proper URI;
     * the subjectType property informs the application how to
     * interpret it.</p>
     *
     * <p>In this demonstration, the statement is simply printed
     * to standard output, but without any information about the
     * subject's type (normally it's not safe to ignore this).  The
     * object is place in quotation marks to show that it is
     * a literal string.</p>
     *
     * @param subjectType The type of subject (from RDFHandler).
     * @param subject The subject of the statement (the identifier
     *        of the object being described).
     * @param predicate The predicate of the statement (the name
     *        of the object's property).
     * @param object The object of the statement (the value of the
     *        object's property), which is a literal string.
     * @param lang The language of the object (the property's value),
     *        as specified by an xml:lang attribute.
     * @see com.megginson.sax.rdf.RDFHandler#literalStatement
     */
    public void literalStatement (int subjectType, String subject,
				 String predicate, String object,
				 String lang)
    {
	System.out.print("{" +
			 predicate +
			 ", " +
			 subject +
			 ", \"");
	char ch[] = object.toCharArray();
	showCharacters(ch, 0, ch.length);
	System.out.println("\" (lang=" +
			   lang +
			   ")}");
    }


    /**
     * Report a resource statement.
     *
     * <p>The RDF filter will invoke this method for every statement
     * in the RDF document that is a link to another resource.  An
     * RDF statement is, essentially, a single NAME=VALUE pair for
     * an object of some sort.</p>
     *
     * <p>Note that the subject may or may not be a proper URI;
     * the subjectType property informs the application how to
     * interpret it.</p>
     *
     * <p>In this demonstration, the statement is simply printed
     * to standard output, but without any information about the
     * subject's type (normally it's not safe to ignore this).</p>
     *
     * @param subjectType The type of subject (from RDFHandler).
     * @param subject The subject of the statement (the identifier
     *        of the object being described).
     * @param predicate The predicate of the statement (the name
     *        of the object's property).
     * @param object The object of the statement (the value of the
     *        object's property), which is the identifier of another
     *        object.
     * @see com.megginson.sax.rdf.RDFHandler#resourceStatement
     */
    public void resourceStatement (int subjectType, String subject,
				  String predicate, String object)
    {
	System.out.println("{" +
			   predicate +
			   ", " +
			   subject +
			   ", " +
			   object +
			   '}');
    }


    /**
     * Report the start of a literal XML statement.
     *
     * <p>The RDF filter will invoke this method for every statement
     * in the RDF document that has a literal XML value (i.e.
     * rdf:parseType="Literal").  The statement is, essentially, a 
     * single NAME=VALUE pair for an object of some sort.</p>
     *
     * <p>Note that the subject may or may not be a proper URI;
     * the subjectType property informs the application how to
     * interpret it.</p>
     *
     * <p>In this demonstration, the start message is simply printed
     * to standard output, but without any information about the
     * subject's type (normally it's not safe to ignore this).
     * The XML markup inside the property is reported through
     * regular SAX2 events, followed by an endXMLStatement
     * event.</p>
     *
     * @param subjectType The type of subject (from RDFHandler).
     * @param subject The subject of the statement (the identifier
     *        of the object being described).
     * @param predicate The predicate of the statement (the name
     *        of the object's property).
     * @param lang The language of the object (the property's value),
     *        as specified by an xml:lang attribute.
     * @see #endXMLStatement
     * @see com.megginson.sax.rdf.RDFHandler#startXMLStatement
     */
    public void startXMLStatement(int subjectType, String subject,
				  String predicate, String lang)
    {
	System.out.println("{" +
			   predicate +
			   ", " +
			   subject +
			   ", [start xml lang=" +
			   lang +
			   "]}");
    }


    /**
     * Report the end of a literal XML statement.
     *
     * <p>This method is invoked after the XML content of a statement
     * has been reported through regular SAX2 callbacks.</p>
     *
     * @see #startXMLStatement
     * @see com.megginson.sax.rdf.RDFHandler#endXMLStatement
     */
    public void endXMLStatement ()
    {
	System.out.println("{[end xml]}");
    }



    ////////////////////////////////////////////////////////////////////
    // Partial implementation of org.xml.sax.ContentHandler.
    //
    // Since this application class is derived from DefaultHandler,
    // there are already default implementations for all of the
    // content handler methods.
    //
    // This section overrides some of the defaults to show the
    // XML markup outside and inside of the RDF blocks.
    //
    // Note that the application has to register itself to receive
    // these events.
    ////////////////////////////////////////////////////////////////////


    /**
     * Show the start of an XML element.
     *
     * <p>For now, show only the local name and no attributes.</p>
     *
     * @param uri The Namespace URI.
     * @param localName The element's local name.
     * @param rawName The element's raw name.
     * @param atts The element's attributes.
     * @see org.xml.sax.ContentHandler#startElement
     */
    public void startElement (String uri, String localName,
			      String rawName, Attributes atts)
    {
	System.out.println("XML start element: " + localName);
    }


    /**
     * Show the end of an XML element.
     *
     * <p>For now, show only the local name.</p>
     *
     * @param uri The Namespace URI.
     * @param localName The element's local name.
     * @param rawName The element's raw name.
     * @param atts The element's attributes.
     * @see org.xml.sax.ContentHandler#endElement
     */
    public void endElement (String uri, String localName, String rawName)
    {
	System.out.println("XML end element: " + localName);
    }


    /**
     * Show a chunk of character data.
     *
     * @param ch The array of characters.
     * @param start The starting position.
     * @param length The number of characters to use.
     * @see org.xml.sax.ContentHandler#characters
     */
    public void characters (char ch[], int start, int length)
    {
	System.out.print("XML characters: ");
	showCharacters(ch, start, length);
	System.out.print("\n");
    }


    /**
     * Show a chunk of whitespace in element content.
     *
     * @param ch The array of characters.
     * @param start The starting position.
     * @param length The number of characters to use.
     * @see org.xml.sax.ContentHandler#ignorableWhitespace
     */
    public void ignorableWhitespace (char ch[], int start, int length)
    {
	System.out.print("XML whitespace: ");
	showCharacters(ch, start, length);
	System.out.print("\n");
    }



    ////////////////////////////////////////////////////////////////////
    // Partial implementation of org.xml.sax.ErrorHandler
    //
    // Make sure that non-fatal errors get reported, because that's
    // how RDF problems arrive.
    //
    // Note that the application has to register itself to receive
    // these events.
    ////////////////////////////////////////////////////////////////////


    /**
     * Handle a warning.
     *
     * @param e The warning information in an exception object.
     * @see org.xml.sax.ErrorHandler#warning
     */
    public void warning (SAXParseException e)
    {
	if (e instanceof RDFException) {
	    System.err.println("*** RDF warning: " + e.getMessage());
	} else {
	    System.err.println("*** XML warning: " + e.getMessage());
	}
	showLocation(e);
	errorStatus = 1;
    }


    /**
     * Handle a non-fatal XML error.
     *
     * @param e The error information in an exception object.
     * @see org.xml.sax.ErrorHandler#error
     */
    public void error (SAXParseException e)
	throws SAXException
    {
	if (e instanceof RDFException) {
	    System.err.println("*** RDF ERROR: " + e.getMessage());
	} else {
	    System.err.println("*** XML ERROR: " + e.getMessage());
	}
	showLocation(e);
	errorStatus = 1;
    }


    /**
     * Handle a fatal XML error.
     *
     * @param e The error information in an exception object.
     * @see org.xml.sax.ErrorHandler#error
     */
    public void fatalError (SAXParseException e)
	throws SAXException
    {
	System.err.println("*** FATAL XML ERROR: " + e.getMessage());
	showLocation(e);
	errorStatus = 1;
    }



    ////////////////////////////////////////////////////////////////////
    // Internal utility methods.
    ////////////////////////////////////////////////////////////////////


    /**
     * Show the location of an error.
     *
     * @param e The SAXParseException with the location information.
     */
    private void showLocation (SAXParseException e)
    {
	String systemId = e.getSystemId();
	int line = e.getLineNumber();
	int col = e.getColumnNumber();
	if (systemId != null || line > -1 || col > -1) {
	    System.out.print("*** (near");
	    if (systemId != null) {
		System.out.print(" " + systemId);
	    }
	    if (line > -1) {
		System.out.print(" line " + line);
	    }
	    if (col > -1) {
		System.out.print(" column " + col);
	    }
	    System.out.print(")\n");
	}
    }


    /**
     * Display characters with some escaping.
     *
     * @param ch The array of characters to escape.
     * @param start The starting position.
     * @param length The number of characters to escape.
     */
    private void showCharacters (char ch[], int start, int length)
    {
	for (int i = start; i < start + length; i++) {
	    switch (ch[i]) {
	    case '\\':
		System.out.print("\\\\");
		break;
	    case '"':
		System.out.print("\\\"");
		break;
	    case '\n':
		System.out.print("\\n");
		break;
	    case '\r':
		System.out.print("\\r");
		break;
	    case '\t':
		System.out.print("\\t");
		break;
	    default:
		System.out.print(ch[i]);
		break;
	    }
	}
    }


    ////////////////////////////////////////////////////////////////////
    // Internal state.
    ////////////////////////////////////////////////////////////////////

    static int errorStatus = 0;
}

// end of TestRDFFilter.java
