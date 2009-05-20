#!/usr/bin/env python3.0
# Copyright Thijs Triemstra 2008-2009

import time, logging
from decimal import Decimal

from jira import JiraDecoder, TracEncoder

if __name__ == "__main__":
    from optparse import OptionParser
    
    usage = "Usage: Jira2Trac [options]"
    parser = OptionParser(usage=usage, version="Jira2Trac 1.0")
    
    parser.add_option("-i", "--input", dest="input", 
                      help="Load Jira backup data from XML file", metavar="FILE")
    parser.add_option("-v", "--verbose",
                      action="store_true", dest="verbose", default=False,
                      help="Print debug messages to stdout [default: %default]")
    parser.add_option("-o", "--output", dest="output",
                      help="Save data to Trac SQLite database file", metavar="FILE")
    (options, args) = parser.parse_args()
    
    # create logger
    FORMAT = "%(asctime)-15s - %(message)s"
    LEVEL = logging.INFO

    if options.verbose == True:
        LEVEL = logging.DEBUG
    
    logging.basicConfig(format=FORMAT, level=LEVEL)

    if options.input:
        start = time.time()
        
        jira = JiraDecoder(options.input, logging)
        jira.readBackupFile()
        jira.showResults()
        
        if options.output:
            trac = TracEncoder(options.output, logging)
            trac.writeDatabase(repr(jira.data))
        
        end = time.time() - start

        logging.info('Completed in %s sec.\n' % (Decimal(str(end)).quantize(Decimal('.0001'))))
        
    else:
        parser.error("Please supply values for the 'input' option")
