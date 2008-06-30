import unittest
import imp
import os
import errno
import sys
import glob
import re
from distutils.errors import *


def unlink(path):
    try:
        os.unlink(path)
    except OSError, exc:
        if exc.errno != errno.ENOENT:
            raise


class BrokenTest(unittest.TestCase.failureException):
    def __repr__(self):
        name, reason = self.args
        return '%s: %s: %s works now' % (
            (self.__class__.__name__, name, reason))


def broken(reason, *exceptions):
    '''Indicates a failing (or erroneous) test case fails that should succeed.
    If the test fails with an exception, list the exception type in args'''
    def wrapper(test_method):
        def replacement(*args, **kwargs):
            try:
                test_method(*args, **kwargs)
            except exceptions or unittest.TestCase.failureException:
                pass
            else:
                raise BrokenTest(test_method.__name__, reason)
        replacement.__doc__ = test_method.__doc__
        replacement.__name__ = 'XXX_' + test_method.__name__
        replacement.todo = reason
        return replacement
    return wrapper


dependencyCache = {}
compileErrorCache = {}

# setup java CLASSPATH
if 'CLASSPATH' not in os.environ:
    cp = []

    baseDir = os.path.abspath(os.path.join(os.path.dirname(__file__), '..', '..', '..'))
    libDir = os.path.join(baseDir, 'lib')

    jar = os.path.join(libDir, 'stringtemplate-3.0.jar')
    if not os.path.isfile(jar):
        raise DistutilsFileError(
            "Missing file '%s'. Grap it from a distribution package."
            % jar,
            )
    cp.append(jar)

    jar = os.path.join(libDir, 'antlr-2.7.7.jar')
    if not os.path.isfile(jar):
        raise DistutilsFileError(
            "Missing file '%s'. Grap it from a distribution package."
            % jar,
            )
    cp.append(jar)

    jar = os.path.join(libDir, 'junit-4.2.jar')
    if not os.path.isfile(jar):
        raise DistutilsFileError(
            "Missing file '%s'. Grap it from a distribution package."
            % jar,
            )
    cp.append(jar)

    cp.append(os.path.join(baseDir, 'runtime', 'Python', 'build'))

    classpath = '-cp "' + ':'.join([os.path.abspath(p) for p in cp]) + '"'

else:
    classpath = ''
    

class ANTLRTest(unittest.TestCase):
    def __init__(self, *args, **kwargs):
        unittest.TestCase.__init__(self, *args, **kwargs)

        self.baseName = os.path.splitext(os.path.basename(sys.modules[self.__module__].__file__))[0]
        self.lexerModule = None
        self.parserModule = None
        

    def _invokeantlr(self, dir, file, options):
        fp = os.popen('cd %s; java %s org.antlr.Tool %s %s 2>&1'
                      % (dir, classpath, options, file)
                      )
        output = ''
        failed = False
        for line in fp:
            output += line

            if line.startswith('error('):
                failed = True

        rc = fp.close()
        if rc is not None:
            failed = True

        if failed:
            raise RuntimeError(
                "Failed to compile grammar '%s':\n\n" % file
                + output
                )
        
        
    def compileGrammar(self, grammarName=None, options=''):
        if grammarName is None:
            grammarName = self.baseName + '.g'

        # don't try to rebuild grammar, if it already failed
        if grammarName in compileErrorCache:
            return

        try:
            testDir = os.path.dirname(os.path.abspath(__file__))

            # get dependencies from antlr
            if grammarName in dependencyCache:
                dependencies = dependencyCache[grammarName]

            else:
                dependencies = []
                cmd = ('cd %s; java %s org.antlr.Tool -depend %s 2>&1'
                       % (testDir, classpath, grammarName)
                       )

                output = ""
                failed = False

                fp = os.popen(cmd)
                for line in fp:
                    output += line

                    if line.startswith('error('):
                        failed = True
                    elif ':' in line:
                        a, b = line.strip().split(':', 1)
                        dependencies.append(
                            (os.path.join(testDir, a.strip()),
                             [os.path.join(testDir, b.strip())])
                            )

                rc = fp.close()
                if rc is not None:
                    failed = True

                if failed:
                    raise RuntimeError(
                        "antlr -depend failed with code %s on grammar '%s':\n\n"
                        % (rc, grammarName)
                        + cmd
                        + "\n"
                        + output
                        )

                # add dependencies to my .stg files
                templateDir = os.path.abspath(os.path.join(testDir, '..', '..', '..', 'src', 'org', 'antlr', 'codegen', 'templates', 'Python'))
                templates = glob.glob(os.path.join(templateDir, '*.stg'))
                
                for dst, src in dependencies:
                    src.extend(templates)

                dependencyCache[grammarName] = dependencies


            rebuild = False

            for dest, sources in dependencies:
                if not os.path.isfile(dest):
                    rebuild = True
                    break

                for source in sources:
                    if os.path.getmtime(source) > os.path.getmtime(dest):
                        rebuild = True
                        break
                    

            if rebuild:
                self._invokeantlr(testDir, grammarName, options)

        except:
            # mark grammar as broken
            compileErrorCache[grammarName] = True
            raise
        

    def lexerClass(self, base):
        """Optionally build a subclass of generated lexer class"""
        
        return base


    def parserClass(self, base):
        """Optionally build a subclass of generated parser class"""
        
        return base


    def walkerClass(self, base):
        """Optionally build a subclass of generated walker class"""
        
        return base


    def __load_module(self, name):
        modFile, modPathname, modDescription \
                 = imp.find_module(name, [os.path.dirname(__file__)])

        return imp.load_module(
            name, modFile, modPathname, modDescription
            )
    
        
    def getLexer(self, *args, **kwargs):
        """Build lexer instance. Arguments are passed to lexer.__init__()."""


        self.lexerModule = self.__load_module(self.baseName + 'Lexer')
        cls = getattr(self.lexerModule, self.baseName + 'Lexer')
        cls = self.lexerClass(cls)

        lexer = cls(*args, **kwargs)

        return lexer
    

    def getParser(self, *args, **kwargs):
        """Build parser instance. Arguments are passed to parser.__init__()."""
        
        self.parserModule = self.__load_module(self.baseName + 'Parser')
        cls = getattr(self.parserModule, self.baseName + 'Parser')
        cls = self.parserClass(cls)

        parser = cls(*args, **kwargs)

        return parser
    

    def getWalker(self, *args, **kwargs):
        """Build walker instance. Arguments are passed to walker.__init__()."""
        
        self.walkerModule = self.__load_module(self.baseName + 'Walker')
        cls = getattr(self.walkerModule, self.baseName + 'Walker')
        cls = self.walkerClass(cls)

        walker = cls(*args, **kwargs)

        return walker


    def compileInlineGrammar(self, grammar, options=''):
        testDir = os.path.dirname(os.path.abspath(__file__))
        
        # get type and name from first grammar line
        m = re.match(r'\s*((lexer|parser|tree)\s+|)grammar\s+(\S+);', grammar)
        assert m is not None
        grammarType = m.group(2)
        if grammarType is None:
            grammarType = 'combined'
        grammarName = m.group(3)

        assert grammarType in ('lexer', 'parser', 'tree', 'combined'), grammarType
        
        # dump temp grammar file
        fp = open(os.path.join(testDir, grammarName + '.g'), 'w')
        fp.write(grammar)
        fp.close()

        # compile it
        self._invokeantlr(testDir, grammarName + '.g', options)
        
        if grammarType == 'combined':
            lexerMod = self.__load_module(grammarName + 'Lexer')
            lexerCls = getattr(lexerMod, grammarName + 'Lexer')
            lexerCls = self.lexerClass(lexerCls)

            parserMod = self.__load_module(grammarName + 'Parser')
            parserCls = getattr(parserMod, grammarName + 'Parser')
            parserCls = self.parserClass(parserCls)

            return lexerCls, parserCls
        
        if grammarType == 'lexer':
            lexerMod = self.__load_module(grammarName + 'Lexer')
            lexerCls = getattr(lexerMod, grammarName + 'Lexer')
            lexerCls = self.lexerClass(lexerCls)

            return lexerCls

        if grammarType == 'parser':
            parserMod = self.__load_module(grammarName + 'Parser')
            parserCls = getattr(parserMod, grammarName + 'Parser')
            parserCls = self.parserClass(parserCls)

            return parserCls

        if grammarType == 'tree':
            walkerMod = self.__load_module(grammarName)
            walkerCls = getattr(walkerMod, grammarName)
            walkerCls = self.walkerClass(walkerCls)

            return walkerCls
