# Copyright Thijs Triemstra 2008

"""
Convert Jira backups to Trac databases.

@since: 2008-12-20
@author: Thijs Triemstra
"""

from decimal import Decimal
import io, os, stat
import xml.parsers.expat

class JiraDecoder(object):
    """
    1. Decode Jira backup file (XML format)
    """
    def __init__(self, options=None):
        if options:
            self.options = options
            self.lastItem = None
            self.allItems = []
            self.data = dict()
            self.data['versions'] = []
            self.data['resolutions'] = []
            self.data['projects'] = []
            self.data['priorities'] = []
            self.data['users'] = []
            self.data['issues'] = []
            self.data['components'] = []
            self.data['customFieldValues'] = []
            self.data['eventTypes'] = []
            self.data['attachments'] = []
            self.data['issueTypes'] = []
            self.data['groups'] = []
            self.data['memberships'] = []
            self.data['statuses'] = []
            self.data['actions'] = []
            self.size = Decimal(str(os.stat(options.input)[stat.ST_SIZE]/(1024*1024))
                                ).quantize(Decimal('.01'))
            
            print()
            print(self.__doc__.strip())
            print('Reading: %s (%s MB)...\n' % (options.input, self.size))
        else:
            raise BaseException('Please pass in some options')
            
    def readBackupFile(self):
        """
        Read the Jira XML backup dump file.
        """
        self.markup = io.FileIO(self.options.input, 'r').readall()
        
        p = xml.parsers.expat.ParserCreate()
        p.StartElementHandler = self.start_element
        p.EndElementHandler = self.end_element
        p.CharacterDataHandler = self.char_data
        p.Parse(self.markup, 1)
    
    def showResults(self):
        """
        Print the results on the commandline.
        """
        print('Results')
        print(7 * '-')
        
        cat = ['versions', 'resolutions', 'projects', 'priorities',
               'users', 'issues', 'components', 'attachments',
               'issueTypes', 'groups', 'actions']
        
        for x in cat:
            name = x.title()
            if x == 'issueTypes':
                name = 'Issue Types'
            
            print('%25s:  %d' % (name, len(self.data[x])))
            
            if self.options.verbose == True:
                for y in self.data[x]:
                    print('%40s' % y)
            
    
    def addItem(self, category, data):
        self.data[category].append(data)
        self.allItems.append(data)
        self.category = category
    
    def char_data(self, data):
        if self.options.verbose == True:
            print('Character data:', repr(data))
            
        if self.lastItem:
            try:
                # start update
                self.updateItem(data)
            except KeyError:
                pass
    
    def updateItem(self, data):
        if (data != "'\n'") or (data[1:len(data)-1].isspace() == False):
            self.lastItem['body'] += data
        
    def end_element(self, name):
        if self.options.verbose == True:
            print('End element:', name)
    
        if name == 'body':
            # finish update
            self.data[self.category][len(self.data[self.category])-1] = self.lastItem
            self.lastItem = None

    def start_element(self, name, attrs):
        if self.options.verbose == True:
            print('Start element:', name, attrs)
            
        if name == 'Version':
            version = {'number':attrs['name']}
            try:
                version['description'] = attrs['description']
            except KeyError:
                pass
            
            try:
                version['releasedate'] = attrs['releasedate']
            except KeyError:
                pass
                
            self.addItem('versions', version)
            
        elif name == 'Resolution':
            resolution = {'name': attrs['name'], 'description': attrs['description']}
            
            self.addItem('resolutions', resolution)
        
        elif name == 'Status':
            status = {'id': attrs['id'], 'sequence': attrs['sequence'],
                      'name': attrs['name'], 'description': attrs['description']}
            
            self.addItem('statuses', status)
            
        elif name == 'Project':
            project = {'name': attrs['name'], 'owner': attrs['lead'],
                       'description': attrs['description'], 'id': attrs['id']}
            
            self.addItem('projects', project)
        
        elif name == 'Priority':
            priority = {'name': attrs['name'], 'description': attrs['description'],
                        'id': attrs['id']}
            
            self.addItem('priorities', priority)
        
        elif name == 'OSUser':
            user = {'name': attrs['name'], 'id': attrs['id']}
            
            try:
                user['password'] = attrs['passwordHash']
                self.addItem('users', user)
            except KeyError:
                pass
    
        elif name == 'Issue':
            issue = {'project': attrs['project'], 'id': attrs['id'],
                     'reporter': attrs['reporter'], 'assignee': attrs['assignee'],
                     'type': attrs['type'], 'summary': attrs['summary'],
                     'priority': attrs['priority'], 'status': attrs['status'],
                     'created': attrs['created'], 'votes': attrs['votes']}
            
            try:
                issue['resolution'] = attrs['resolution']
            except KeyError:
                pass
                
            self.addItem('issues', issue)
        
        elif name == 'Component':
            component = {'project': attrs['project'], 'id': attrs['id'],
                         'name': attrs['name'], 'description': attrs['description'],
                         'lead': attrs['lead']}

            self.addItem('components', component)
        
        elif name == 'CustomFieldValue' and attrs['customfield'] == '10001':
            customFieldValue = {'issue': attrs['issue'], 'id': attrs['id'],
                                'value': attrs['stringvalue']}

            self.addItem('customFieldValues', customFieldValue)
         
        elif name == 'EventType':
            eventType = {'id': attrs['id'], 'name': attrs['name'],
                         'description': attrs['description']}

            self.addItem('eventTypes', eventType)
        
        elif name == 'IssueType':
            issueType = {'id': attrs['id'], 'sequence': attrs['sequence'],
                         'name': attrs['name'], 'description': attrs['description']}

            self.addItem('issueTypes', issueType)
            
        elif name == 'FileAttachment':
            attachment = {'id': attrs['id'], 'issue': attrs['issue'],
                         'mimetype': attrs['mimetype'], 'created': attrs['created'],
                         'filename': attrs['filename'], 'filesize': attrs['filesize'],
                         'author': attrs['author']}

            self.addItem('attachments', attachment)
        
        elif name == 'OSGroup':
            group = {'id': attrs['id'], 'name': attrs['name']}

            self.addItem('groups', group)
        
        elif name == 'Action':
            action = {'id': attrs['id'], 'issue': attrs['issue'],
                      'author': attrs['author'], 'type': attrs['type'],
                      'created': attrs['created']}

            try:
                action['body'] = attrs['body']
            except KeyError:
                pass
            
            self.addItem('actions', action)
            
        elif name == 'OSMembership':
            membership = {'id': attrs['id'], 'username': attrs['userName'],
                          'groupName': attrs['groupName']}

            self.addItem('memberships', membership)
        
        elif name == 'body':
            # create update object
            self.lastItem = self.allItems[len(self.allItems)-1]
            self.lastItem['body'] = ''
        
        # Unused:
        #  - OSCurrentStep
        #  - OSCurrentStepPrev
        #  - OSHistoryStep
        #  - OSHistoryStepPrev
        #  - OSPropertyEntry
        #  - OSPropertyString
        #  - OSWorkflowEntry
        #  - UserAssociation

class TracEncoder(object):
    """
    2. Write data to Trac database
    """
    
    def __init__(self, options=None):
        if options:
            self.options = options
            print()
            print(self.__doc__.strip())

        else:
            raise BaseException('Please pass in some options')
    
    def writeDatabase(self, jiraData):
        print('Writing: %s...\n' % self.options.output)
        self.outputFile = io.FileIO(self.options.output, 'w')
        self.outputFile.write(jiraData)
        self.outputFile.close()

    def showResults(self):
        """
        Print the results on the commandline.
        """
        print('Results')
        print(7 * '-')
        self.size = Decimal(str(os.stat(self.options.output)[stat.ST_SIZE]/(1024*1024))
                   ).quantize(Decimal('.0001'))

