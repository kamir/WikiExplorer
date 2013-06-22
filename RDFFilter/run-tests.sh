#! /bin/bash
#
#$Id: run-tests.sh,v 1.1 2000/03/13 16:45:42 david Exp $
#
# Unix shell script to run tests in the RDF test suite.
# Usage: sh run-tests.sh
#

echo RDF Tests `date` > tests.OUTPUT
for file in testSuite/bad/*.rdf; do
  echo -n "Running $file..."
  if java -cp aelfred.jar:rdffilter.jar:sax2beta.jar:. TestRDFFilter $file >> tests.OUTPUT 2>&1; then
    echo FAILED
  else
    echo ok
  fi
done

# end of run-tests.sh
