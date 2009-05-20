#!/usr/bin/env python3.0

import time
from decimal import Decimal

from jira import JiraDecoder, TracEncoder

if __name__ == "__main__":
    from optparse import OptionParser
    
    usage = "Usage: %prog [options]"
    parser = OptionParser(usage=usage, version="%prog 1.0")
    
    parser.add_option("-i", "--input", dest="input", 
                      help="Read data from FILE", metavar="FILE")
    parser.add_option("-q", "--quiet",
                      action="store_false", dest="verbose", default=True,
                      help="Don't print status messages to stdout")
    parser.add_option("-o", "--output", dest="output",
                      help="Write data to FILE", metavar="FILE")
    (options, args) = parser.parse_args()
    
    if options.input:
        start = time.time()
        
        jira = JiraDecoder(options)
        jira.readBackupFile()
        jira.showResults()
        
        if options.output:
            trac = TracEncoder(options)
            trac.writeDatabase(repr(jira.data))
            trac.showResults()
        
        end = time.time() - start
        print('\nCompleted in %s sec.\n' % (Decimal(str(end)).quantize(Decimal('.0001'))))
        
    else:
        parser.error('Please supply values for the input and output options')
