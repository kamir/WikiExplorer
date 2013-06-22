RDF FILTER - EVENT-BASED RDF PROCESSING FOR SAX2
------------------------------------------------

Written by David Megginson, david@megginson.com
NO WARRANTY!  This class is in the public domain.
$Id: README,v 1.2 2000/03/13 16:43:03 david Exp $


************************************************************************
If you want to get going as fast as possible, read QUICKSTART.txt.
************************************************************************

This package contains a simple RDF event-handler interface and an RDF
filter for SAX2 beta2.  The RDF filter uses the feature

  http://megginson.com/sax/features/rdf

to enable or disable RDF processing (enabled by default), and the
property

  http://megginson.com/sax/properties/rdf-handler

to set or query the RDF event handler.  When RDF processing is active, 
all RDF markup is removed from the regular SAX2 event stream (no
elements or attributes) and abstract RDF statements are reported
through the RDFHandler instead, assuming that the application has
registered one.

All RDFHandler events are simple statements, as in the RDF data model,
except that they've been expanded to take into account information
which the model omitted.  An RDF statement is simply a single property
assignment for an entity: in RDF, the entity is called the "subject",
the property name is called the "predicate", and the property value is
called the "object".  Each property generates its own statement, so if
an entity has five properties, it will generate five RDF statements
(or many, many more if reification is enabled with rdf:bagID).

A property in RDF may have one of three different kinds of values:

1. A literal string.
2. A reference to another entity.
3. XML markup.

XML markup is reported through regular SAX2 ContentHandler events
between an invocation of RDFHandler.startXMLStatement and
RDFHandler.endXMLStatement.  The RDFHandler interface itself is
extremely simple.

For a fairly detailed example of using the RDF filter, see
TestRDFFilter.java included in the distribution. 

The beginnings of an RDF test suite are located in the testSuite/
subdirectory; see the README in that directory for more information.


---
2000-03-13

