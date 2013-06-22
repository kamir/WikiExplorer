// RDFHandler.java - event handler for RDF.
// Written by David Megginson, david@megginson.com
// NO WARRANTY!  This class is in the public domain.

// $Id: RDFHandler.java,v 1.1 2000/03/10 23:17:27 david Exp $

package com.megginson.sax.rdf;

import org.xml.sax.SAXException;


/**
 * SAX event interface for processing RDF.
 *
 * <blockquote>
 * <em>This module, both source code and documentation, is in the
 * Public Domain, and comes with <strong>NO WARRANTY</strong>.</em>
 * </blockquote>
 *
 * <p>This is an extended SAX handler interface for RDF events.  
 * It is meant to be used together with the regular SAX2 handlers, 
 * which will be used to report two kinds of non-RDF events:</p>
 *
 * <ul>
 * <li>Non-RDF XML markup outside of any rdf:RDF containers.</li>
 * <li>XML markup inside a property with parseType=Literal.</li>
 * </ul>
 *
 * <p>Even if the document contains only RDF markup, there will be
 * at least the regular start/endDocument events before and after
 * the RDF.  Note that no regular SAX events (such as start/endElement
 * and characters) will be generated for RDF-specific markup; the
 * events in this handler replace the regular SAX events rather than
 * supplementing them.  rdf:RDF containers may appear at any
 * level of an XML document, and may be mixed in with any amount
 * of non-RDF markup.</p>
 *
 * <p>An RDF handler can be set or queried in SAX2 using the
 * "http://megginson.com/properties/rdf-handler" property,
 * as in the following example:</p>
 *
 * <blockquote><pre>
 * RDFHandler handler = new MyRDFHandler();
 * try {
 *   xmlReader.setProperty("http://megginson.com/properties/rdf-handler",
 *                         handler);
 * } catch (SAXException e) {
 *   System.err.println("XML reader does not support RDF processing.");
 *   System.exit(1);
 * }
 * </pre></blockquote>
 *
 * <p>RDF processing can be enabled or disabled using the 
 * "http://megginson.com/features/rdf" feature.  Both of these
 * are available as constants in this class for convenience.</p>
 *
 * <p>Many SAX2 drivers will not support RDF processing natively, so
 * an RDFFilter is provided together with this handler to interpret
 * RDF events.</p>
 *
 * @author David Megginson, david@megginson.com
 * @version 1.0alpha
 * @since 1.0alpha
 * @see org.xml.sax.XMLReader
 * @see com.megginson.sax.rdf.RDFFilter
 */
public interface RDFHandler
{

    /**
     * Constant: feature name for RDF processing.
     */
    public static final String featureName =
	"http://megginson.com/sax/features/rdf";


    /**
     * Constant: property name for RDF processing.
     */
    public static final String propertyName =
	"http://megginson.com/sax/properties/rdf-handler";


    /**
     * Constant: subject is a proper URI.
     *
     * <p>This is the subject type when the rdf:about or
     * rdf:ID attributes were used.  If rdf:ID was used,
     * the subject itself will have '#' prepended.  Note that
     * the URI may be relative or absolute.</p>
     *
     * <p>This type is also used for rdf:bagID.</p>
     */
    public final static int SUBJECT_URI = 1;


    /**
     * Constant: subject is a URI pattern.
     *
     * <p>This is the subject type when the rdf:aboutEachPrefix
     * attribute was used.  The statement applies to every
     * resource with a URI beginning with the subject string.</p>
     */
    public final static int SUBJECT_URI_PATTERN = 2;


    /**
     * Constant: subject is a distributed identifier.
     *
     * <p>This is the subject type when the rdf:aboutEach
     * attribute was used.  The statement applies to every
     * resource whose value is the value of a property
     * of the subject.</p>
     */
    public final static int SUBJECT_DISTRIBUTED = 3;


    /**
     * Constant: subject is a generated identifier.
     *
     * <p>This is the subject type when no identifier was provided.
     * The generated identifier is deliberately not a proper URI.</p>
     */
    public final static int SUBJECT_GENERATED = 4;


    /**
     * Report a statement with a literal object.
     *
     * @param subjectType A constant indicating the type of subject.
     * @param subject The subject's identifier string.
     * @param predicate The predicate as a fully-resolved URI.
     * @param object The object as a literal string.
     * @param language The language of the literal, or null if none
     *        was specified.
     * @exception org.xml.sax.SAXException The client may throw
     *            an exception.
     */
    public abstract void literalStatement (int subjectType,
					   String subject,
					   String predicate,
					   String object,
					   String language)
	throws SAXException;


    /**
     * Report a statement with a resource object.
     *
     * @param subjectType A constant indicating the type of subject.
     * @param subject The subject's identifier string.
     * @param predicate The predicate as a fully-resolved URI.
     * @param object The object as a fully-resolved URI.
     * @exception org.xml.sax.SAXException The client may throw
     *            an exception.
     */
    public abstract void resourceStatement (int subjectType,
					    String subject,
					    String predicate,
					    String object)
	throws SAXException;


    /**
     * Report the beginning of a statement with an XML object.
     *
     * @param subjectType A constant indicating the type of subject.
     * @param subject The subject's identifier string.
     * @param predicate The predicate as a fully-resolved URI.
     * @param language The language of the literal, or null if none
     *        was specified.
     * @see #endXMLStatement
     * @exception org.xml.sax.SAXException The client may throw
     *            an exception.
     */
    public abstract void startXMLStatement (int subjectType,
					    String subject,
					    String predicate,
					    String language)
	throws SAXException;


    /**
     * Report the end of a statement with an XML object.
     *
     * @see #startXMLStatement
     * @exception org.xml.sax.SAXException The client may throw
     *            an exception.
     */
    public abstract void endXMLStatement ()
	throws SAXException;

}

// end of RDFHandler.java
