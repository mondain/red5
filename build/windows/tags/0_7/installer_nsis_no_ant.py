##
## Build Red5 and generate windows installer
##
##
import os, sys
from time import strftime
from distutils.dir_util import remove_tree

# defaults, adjust to your needs
red5_root = r'C:\dev\red5\java\server\trunk'
NSIS_CMD = r'C:\Progra~1\NSIS\makensis.exe'

def error(msg):
    print 'ERROR: %s' % msg
    sys.exit(1)


def log(msg):
    print msg


class Generator:
    
    def __init__(self):
        self.build_root = os.path.abspath(os.path.split(__file__)[0])

    def prepareJava5WrapperConf(self, src, dst):
        fp = file(dst, 'wb')
        jars = [x for x in os.listdir(os.path.join(red5_root, '.', 'dist.java5', 'lib')) if x.lower().endswith('.jar')]
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
            fp.write('wrapper.java.classpath.3=../bin\n')
            fp.write('wrapper.java.classpath.4=../red5.jar\n')
            for idx, filename in enumerate(jars):
                fp.write('wrapper.java.classpath.%d=../lib/%s\n' % (idx+5, filename))
            added = True

    def prepareJava6WrapperConf(self, src, dst):
        fp = file(dst, 'wb')
        jars = [x for x in os.listdir(os.path.join(red5_root, '.', 'dist.java6', 'lib')) if x.lower().endswith('.jar')]
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
            fp.write('wrapper.java.classpath.3=../bin\n')
            fp.write('wrapper.java.classpath.4=../red5.jar\n')
            for idx, filename in enumerate(jars):
                fp.write('wrapper.java.classpath.%d=../lib/%s\n' % (idx+5, filename))
            added = True

    def generate(self, platform='windows'):
        log('Generating from %s' % self.build_root)
                
        # setup .jar files to wrapper.conf
        log('Generating Java 1.6 version...')       
        self.prepareJava6WrapperConf(os.path.join(self.build_root, 'conf', 'wrapper.conf.in'), 
                                os.path.join(self.build_root, 'conf', 'wrapper.conf.java6'))
        log('Generating Java 1.5 version...')
        self.prepareJava5WrapperConf(os.path.join(self.build_root, 'conf', 'wrapper.conf.in'), 
                                os.path.join(self.build_root, 'conf', 'wrapper.conf.java5'))
        
        # build installer
        #script = os.path.join(self.build_root, 'red5.nsi')
        #cmd = NSIS_CMD
        #if ' ' in cmd and not cmd[:1] == '"':
        #    cmd = '"' + cmd + '"'
        #log('Compiling installer, this may take some time...')
        #os.system(cmd, script)
        #log('Installer written')


def main():
    log('Red5 build system')
    log('-----------------')
    generater = Generator()
    generater.generate()
    
    
if __name__ == '__main__':
    main()
