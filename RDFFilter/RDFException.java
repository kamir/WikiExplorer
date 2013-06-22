// RDFException.java - RDF-specific exception.
// Written by David Megginson, david@megginson.com
// NO WARRANTY!  This class is in the public domain.

// $Id: RDFException.java,v 1.1 2000/03/11 02:18:01 david Exp $

package com.megginson.sax.rdf;

import org.xml.sax.Locator;
import org.xml.sax.SAXParseException;

/**
 * RDF-specific exception.
 *
 * <blockquote>
 * <em>This module, both source code and documentation, is in the
 * Public Domain, and comes with <strong>NO WARRANTY</strong>.</em>
 * </blockquote>
 *
 * <p>This class is a specialized subclass of SAXParseException
 * designed for reporting RDF-specific errors.</p>
 *
 * @author David Megginson, david@megginson.com
 * @version 1.0alpha
 * @since 1.0alpha
 * @see com.megginson.sax.rdf.RDFFilter
 * @see com.megginson.sax.rdf.RDFHandler
 * @see org.xml.sax.SAXParseException
 */
public class RDFException extends SAXParseException
{

    /**
     * Construct a new RDF exception.
     *
     * <p>Public and system IDs will be null, and line and
     * column number will be -1.</p>
     *
     * @param message The error message.
     */
    public RDFException (String message)
    {
	super(message, null, null, -1, -1);
    }


    /**
     * Construct a new RDF exception.
     *
     * @param message The error message.
     * @param locator A SAX2 locator containing the location of the
     *        error.
     */
    public RDFException (String message, Locator locator)
    {
	super(message, locator);
    }
}

// end of RDFException.java
