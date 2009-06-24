#!/bin/bash

BACKUP=../data/backup.xml
ATTACHMENTS=../data/attachments
HTPASSWD=test/.htpasswd

USERNAME=admin
PASSWORD=admin
HOST=localhost:8080/test

./jira2trac.py -i $BACKUP -a $ATTACHMENTS -u $USERNAME -p $PASSWORD -l $HOST -t $HTPASSWD
