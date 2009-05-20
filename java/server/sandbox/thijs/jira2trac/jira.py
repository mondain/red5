# Copyright Thijs Triemstra 2008-2009

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
    Parse Jira backup file (XML format)
    """
    def __init__(self, input, logger):
        if input:
            self.logger = logger
            self.input = input
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
            self.size = Decimal(str(os.stat(input)[stat.ST_SIZE]/(1024*1024))
                                ).quantize(Decimal('.01'))
            print()
            logger.info(self.__doc__.strip())
            logger.info('Reading data from %s (%s MB)' % (input, self.size))
            logger.info('Processing...\n')
        else:
            raise BaseException("Please specify the 'input' option")
            
    def readBackupFile(self):
        """
        Read the Jira XML backup dump file.
        """
        self.markup = io.FileIO(self.input, 'r').readall()
        
        p = xml.parsers.expat.ParserCreate()
        p.StartElementHandler = self.start_element
        p.EndElementHandler = self.end_element
        p.CharacterDataHandler = self.char_data
        p.Parse(self.markup, 1)
    
    def showResults(self):
        """
        Print the results on stdout.
        """
        print('Results')
        print(7 * '-')
        
        categories = ['versions', 'resolutions', 'projects', 'priorities',
                      'components', 'issueTypes', 'groups', 'issues',
                      'attachments', 'users', 'actions']
        
        for category in categories:
            name = category.title()
            if category == 'issueTypes':
                name = 'Issue Types'
            
            print('\n%25s:  %d\n' % (name, len(self.data[category])))
            
            for item in self.data[category]:
                if category == 'versions':
                    print('%50s - %s' % (item['number'], item['releasedate']))
                
                elif category == 'resolutions':
                    print('%50s' % (item['name']))
            
                elif category == 'projects':
                    print('%50s (%s - %s)' % (item['name'], item['owner'],
                                              item['id']))

                elif category == 'priorities':
                    print('%50s' % (item['name']))
                
                elif category == 'components':
                    print('%50s (%s - %s)' % (item['name'], item['owner'],
                                              item['project']))

                elif category == 'issueTypes':
                    print('%50s. %s' % (item['sequence'], item['name']))

                elif category == 'groups':
                    print('%50s' % (item['name']))
                    
    def addItem(self, category, data):
        self.data[category].append(data)
        self.allItems.append(data)
        self.category = category
    
    def char_data(self, data):
        self.logger.debug('Character data: %s' % repr(data))
            
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
        self.logger.debug('End element: %s' % name)
    
        if name == 'body':
            # finish update
            self.data[self.category][len(self.data[self.category])-1] = self.lastItem
            self.lastItem = None

    def start_element(self, name, attrs):
        self.logger.debug('Start element: %s %s' % (name, attrs))
            
        if name == 'Version':
            version = {'number':attrs['name']}
            try:
                version['description'] = attrs['description']
            except KeyError:
                version['description'] = None
            
            try:
                version['releasedate'] = attrs['releasedate']
            except KeyError:
                version['releasedate'] = None
                
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
                         'owner': attrs['lead']}

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
    Save data in Trac database
    """
    
    def __init__(self, output, logger):
        if output:
            self.output = output
            self.logger = logger
            print()
            logger.info(self.__doc__.strip())

        else:
            raise BaseException("Please specify the 'output' option")
    
    def writeDatabase(self, jiraData):
        """
        Write the data to the Trac database.
        """
        self.logger.info('Writing data to %s' % self.output)
        self.outputFile = io.FileIO(self.output, 'w')
        self.outputFile.write(jiraData)
        self.outputFile.close()
