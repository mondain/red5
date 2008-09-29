##
## Build Red5 and generate windows installer
##
##
import os, sys
from time import strftime
from distutils.dir_util import remove_tree

# defaults, adjust to your needs
red5_root = r'C:\dev\red5\java\server\trunk'
ANT_CMD  = r'C:\dev\ant\bin\ant'
NSIS_CMD = r'C:\Progra~1\NSIS\makensis.exe'
VERSION  = strftime('%Y%m%d')

def error(msg):
    print 'ERROR: %s' % msg
    sys.exit(1)

def log(msg):
    print msg


class Builder:
    
    def compile(self, ant, script, version, *args):
        args = args and ' ' + ' '.join(args) or ''
        assert os.system('%s -quiet -Djava.target_version=%s -buildfile %s%s' % (ant, version, script, args)) == 0

    def __init__(self, java_home, ant_cmd):
        self.java_home = java_home
        self.ant_cmd = ant_cmd
        self.build_root = os.path.abspath(os.path.split(__file__)[0])

    def build(self, platform='windows'):
        log('Building from %s' % self.build_root)
        # prepare "dist" directory
        log('Cleaning old directories...')
        if os.path.isdir(os.path.join(red5_root, 'dist.java5')):
            remove_tree(os.path.join(red5_root, 'dist.java5'))
        if os.path.isdir(os.path.join(red5_root, 'dist.java6')):
            remove_tree(os.path.join(red5_root, 'dist.java6'))
        
        log('Compiling Java 1.5 version...')
        self.compile(self.ant_cmd, os.path.join(red5_root, 'build.xml'), '1.5', 'dist-installer')
        os.renames(os.path.join(red5_root, '.', 'dist'), os.path.join(red5_root, '.', 'dist.java5'))
        
        log('Compiling Java 1.6 version...')
        os.environ['JAVACMD'] = os.path.join(JAVA6_HOME, 'bin', 'java.exe')
        self.compile(self.ant_cmd, os.path.join(red5_root, 'build.xml'), '1.6', 'dist-installer')
        os.renames(os.path.join(red5_root, '.', 'dist'), os.path.join(red5_root, '.', 'dist.java6'))
                
        # build installer
        script = os.path.join(self.build_root, 'red5.nsi')
        cmd = NSIS_CMD
        if ' ' in cmd and not cmd[:1] == '"':
            cmd = '"' + cmd + '"'
        log('Compiling installer, this may take some time...')
        os.system(cmd, script)
        log('Installer written')


def main():
    global JAVA_HOME, JAVA6_HOME
    if len(sys.argv) == 2:
        # create installer with specific version number
        global VERSION
        VERSION = sys.argv[1]
    
    log('Red5 build system')
    log('-----------------')
    JAVA_HOME = os.environ.get('JAVA_HOME', r'C:\dev\java5')
    JAVA6_HOME = os.environ.get('JAVA6_HOME', r'C:\dev\java6')
    
    if not os.path.isfile(os.path.join(JAVA_HOME, 'bin', 'java.exe')):
        error('could not find "java.exe" in "%s"' % JAVA_HOME)
        
    log('using "java.exe" from "%s"' % JAVA_HOME)
    os.environ['JAVACMD'] = os.path.join(JAVA_HOME, 'bin', 'java.exe')
    
    if not os.path.isfile(ANT_CMD):
        error('"%s" does not exist' % ANT_CMD)
        
    log('using "%s" for building' % ANT_CMD)
    
    builder = Builder(JAVA_HOME, ANT_CMD)
    builder.build()
    

if __name__ == '__main__':
    main()
