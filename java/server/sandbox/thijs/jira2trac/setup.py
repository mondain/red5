#!/usr/bin/env python
#
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

import os
from setuptools import setup


def get_version():
    """
    Gets the version number. Pulls it from the source files rather than
    duplicating it.
    """
    # we read the file instead of importing it as root sometimes does not
    # have the cwd as part of the PYTHONPATH

    fn = os.path.join(os.getcwd(), 'jira2trac', '__init__.py')
    lines = open(fn, 'rt').readlines()

    version = None

    for l in lines:
        # include the ' =' as __version__ is a part of __all__
        if l.startswith('__version__ =', ):
            x = compile(l, fn, 'single')
            eval(x)
            version = locals()['__version__']
            break

    if version is None:
        raise RuntimeError('Couldn\'t determine version number')

    return '.'.join([str(x) for x in version])


setup(
    name = 'Jira2Trac',
    version = get_version(),
    author = 'Thijs Triemstra',
    author_email = 'thijs@red5.org',
    url = 'http://trac-hacks.org/wiki/Jira2Trac',
    description = 'Imports a Jira backup into a Trac database using XML-RPC',
    license = 'MIT License',
    zip_safe=True,
    packages=['jira2trac'],
    package_data={'jira2trac': ['templates/*.html',
                                'templates/*.cfg']},

    install_requires = [
        'trac>=0.11',
        'PyAMF>=0.4.2',
    ],

)
