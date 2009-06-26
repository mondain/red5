# Copyright (c) 2008-2009 Thijs Triemstra
#
# Permission is hereby granted, free of charge, to any person obtaining a copy
# of this software and associated documentation files (the "Software"), to
# deal in the Software without restriction, including without limitation the
# rights to use, copy, modify, merge, publish, distribute, sublicense, and/or
# sell copies of the Software, and to permit persons to whom the Software is
# furnished to do so, subject to the following conditions:
#
# The above copyright notice and this permission notice shall be included in
# all copies or substantial portions of the Software. 
#
# THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
# IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
# FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL
# THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
# LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
# FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS
# IN THE SOFTWARE.

"""
The jira2trac Daemon.

@since: 2009-06-25
"""


import logging as log

from time import time
from optparse import OptionParser

from jira2trac import version
from jira2trac import JiraDecoder
from jira2trac import TracEncoder


def run():
    name = "Jira2Trac"
    usage = "Usage: %s [options]" % name
    
    parser = OptionParser(usage=usage, version="%s %s" % (name, version))
    parser.add_option("-i", "--input", dest="input",
                      help="Location of Jira backup XML file", metavar="FILE")
    parser.add_option("-a", "--attachments", dest="attachments", metavar="FILE",
                      help="Location of the Jira attachments folder")
    parser.add_option("-t", "--authentication", dest="authentication", metavar="FILE",
                      help="Location of the .htpasswd file (optional)")
    parser.add_option("-l", "--url", dest="url", help="URL for Trac instance")
    parser.add_option("-u", "--username", dest="username", help="Username for Trac instance")
    parser.add_option("-p", "--password", dest="password", help="Password for Trac instance")
    parser.add_option("-v", "--verbose", default=False, action="store_true",
                      help="Print debug messages to stdout [default: %default]",
                      dest="verbose")

    (options, args) = parser.parse_args()

    FORMAT = "%(message)s"
    LEVEL = log.INFO

    if options.verbose == True:
        FORMAT = "%(asctime)-15s - %(levelname)-3s - " + FORMAT
        LEVEL = log.DEBUG

    log.basicConfig(format=FORMAT, level=LEVEL)

    if options.input:
        start = time()
        jira = JiraDecoder(options.input)

        try:
            jira.parseBackupFile()
            jira.showResults()            
        except KeyboardInterrupt:
            log.warn('Cancelled data parsing!')
            exit()

        if options.username:
            trac = TracEncoder(jira.data, options.username, options.password,
                               options.url, options.attachments,
                               options.authentication)
            try:
                trac.importData()

                if options.authentication is not None:
                    trac.importUsers()

            except KeyboardInterrupt:
                log.warn('Cancelled data import!')
                exit()

        end = time() - start

        log.info('Completed in %s sec.' % (Decimal(str(end)).quantize(Decimal('.0001'))))

    else:
        parser.error("Please specify a value for the 'input' option")


__all__ = ['run']