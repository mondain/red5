##
## Build Red5 and generate windows installer
##
##
import os, sys
from time import strftime

# defaults
ANT_CMD  = r'D:\Develop\osflash\apache-ant-1.6.5\bin\ant'
INNO_CMD = r'C:\Programme\InnoSe~1\ISCC.exe'
VERSION  = strftime('%Y%m%d')

def error(msg):
    print 'ERROR: %s' % msg
    sys.exit(1)

def log(msg):
    print msg


class Builder:
    
    def compile(self, ant, script, *args):
        args = args and ' ' + ' '.join(args) or ''
        assert os.system('%s -quiet -buildfile %s%s' % (ant, script, args)) == 0

    def __init__(self, java_home, ant_cmd):
        self.java_home = java_home
        self.ant_cmd = ant_cmd
        self.build_root = os.path.abspath(os.path.split(__file__)[0])

    def prepareWrapperConf(self, src, dst):
        fp = file(dst, 'wb')
        jars = [x for x in os.listdir(os.path.join(self.build_root, '..', 'dist', 'lib')) if x.lower().endswith('.jar')]
        added = False
        for line in file(src, 'rb').readlines():
            if not line.startswith('wrapper.java.classpath.'):
                fp.write(line)
                continue
            
            if added:
                # skip line, setting will get overwritten
                continue
            
            fp.write('wrapper.java.classpath.1=../lib/wrapper.jar\n')
            fp.write('wrapper.java.classpath.2=../conf\n')
            fp.write('wrapper.java.classpath.3=../lib/red5.jar\n')
            for idx, filename in enumerate(jars):
                fp.write('wrapper.java.classpath.%d=../lib/%s\n' % (idx+4, filename))
            added = True

    def build(self, platform='windows'):
        log('Building from %s' % self.build_root)
        # prepare "dist" directory
        red5_root = os.path.abspath(os.path.join(self.build_root, '..', 'dist'))
        self.compile(self.ant_cmd, os.path.join(red5_root, '..', 'build.xml'), 'installerdist')
        
        # setup .jar files to wrapper.conf
        self.prepareWrapperConf(os.path.join(self.build_root, 'conf', 'wrapper.conf.in'), 
                                os.path.join(self.build_root, 'conf', 'wrapper.conf'))
        
        # build installer
        dest = os.getcwd()
        args = [
            '/q',                                   # quiet
            '/dversion="%s"' % VERSION,             # Red5 version
            '/droot_dir="%s"' % red5_root,          # Red5 root
            '/dbuild_dir="%s"' % self.build_root,   # build root
            '/o"%s"' % dest,                        # output directory
        ]
        script = os.path.join(self.build_root, 'red5-setup.iss')
        cmd = INNO_CMD
        if ' ' in cmd and not cmd[:1] == '"':
            cmd = '"' + cmd + '"'
        log('Compiling installer, this may take some time...')
        assert os.system('%s %s %s' % (cmd, script, ' '.join(args))) == 0
        log('Installer written to %s\setup-red5-%s.exe' % (dest, VERSION))


def main():
    if len(sys.argv) == 2:
        # create installer with specific version number
        global VERSION
        VERSION = sys.argv[1]
    
    log('Red5 build system')
    log('-----------------')
    JAVA_HOME = os.environ.get('JAVA_HOME', r'C:\Programme\Java\jdk1.5.0_05')
    
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
