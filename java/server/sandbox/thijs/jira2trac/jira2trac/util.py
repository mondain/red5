# Copyright (c) 2009 Thijs Triemstra
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
Utilities.
"""

import sys
import logging as log

from hashlib import sha512
from base64 import b64encode

__all__ = ['DisplayProgress', 'ProgressBar', 'create_hash']


def create_hash(self, password):
    """
    Turns strings into base64 encoded SHA-512 hashes.
    
    For example, the word 'sphere' should produce the hash:
    'uQieO/1CGMUIXXftw3ynrsaYLShI+GTcPS4LdUGWbIusFvHPfUzD7CZvms6yMMvA8I7FViHVEqr6Mj4pCLKAFQ=='
    """
    digested = sha512(bytes(password, 'utf-8')).digest()
    hash = b64encode(digested)

    return str(hash, encoding='utf8')


class ProgressBar:
    """
    Creates a text-based progress bar. Call the object with the `print'
    command to see the progress bar, which looks something like this::

        [=======>        22%                  ]

    You may specify the progress bar's width, min and max values on init.
    
    Taken from U{http://code.activestate.com/recipes/168639/}
    """

    def __init__(self, minValue = 0, maxValue = 100, totalWidth=40):
        self.progBar = "[]"   # This holds the progress bar string
        self.min = minValue
        self.max = maxValue
        self.span = maxValue - minValue
        self.width = totalWidth
        self.amount = 0       # When amount == max, we are 100% done
        self.updateAmount(0)  # Build progress bar string

    def updateAmount(self, newAmount = 0):
        """
        Update the progress bar with the new amount (with min and max
        values set at initialization; if it is over or under, it takes the
        min or max value as a default.
        """
        if newAmount < self.min: newAmount = self.min
        if newAmount > self.max: newAmount = self.max
        self.amount = newAmount

        # Figure out the new percent done, round to an integer
        diffFromMin = float(self.amount - self.min)
        percentDone = (diffFromMin / float(self.span)) * 100.0
        percentDone = int(round(percentDone))

        # Figure out how many hash bars the percentage should be
        allFull = self.width - 2
        numHashes = (percentDone / 100.0) * allFull
        numHashes = int(round(numHashes))

        # Build a progress bar with an arrow of equal signs; special cases for
        # empty and full
        if numHashes == 0:
            self.progBar = "[>%s]" % (' '*(allFull-1))
        elif numHashes == allFull:
            self.progBar = "[%s]" % ('='*allFull)
        else:
            self.progBar = "[%s>%s]" % ('='*(numHashes-1),
                                        ' '*(allFull-numHashes))

        # figure out where to put the percentage, roughly centered
        percentPlace = int(len(self.progBar) / 2) - len(str(percentDone))
        percentString = str(percentDone) + "%"
        
        # slice the percentage into the bar
        self.progBar = ' '.join([self.progBar[0:percentPlace], percentString,
                                self.progBar[percentPlace+len(percentString):]
                                ])

    def __str__(self):
        return str(self.progBar)


class DisplayProgress(object):
    """
    Display progress bar.
    """

    def __init__(self, total, title):
        self.progress = 0
        self.title = title
        self.total = total
        self.pb = ProgressBar(self.progress, self.total)
        log.info('  %d %s...' % (self.total, self.title))

    def update(self):
        self.progress += 1
        self.pb.updateAmount(self.progress)
        sys.stdout.write("%s%s" % (str(self.pb), '\r'))
        sys.stdout.flush()
