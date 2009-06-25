Jira to Trac Migration
======================

This document describes how to use the jira.py script that allows
you to import existing Jira data into a Trac database using XML-RPC.
This script requires Python 3.1 or newer. It was tested with
Atlassian Jira Enterprise 3.6.5 (build 161) and Edgewall Trac
0.11.4. The Trac instance needs to have the (patched) XML-RPC 1.0
and AccountManager plugins for Trac installed.

Instructions
============

From a commandline prompt run the following:

./jira2trac.py -i backup.xml -l localhost:8000 -u john -p passw0rd

Run ./jira2trac --help for more info.


-----------------------
Author: Thijs Triemstra
Date: 2009-05-20
