import sys
import unittest

import antlr3


class TestBaseRecognizer(unittest.TestCase):
    """Tests for BaseRecognizer class"""
    
    def testGetRuleInvocationStack(self):
        """BaseRecognizer._getRuleInvocationStack()"""

        rules = antlr3.BaseRecognizer._getRuleInvocationStack(__name__)
        self.failUnlessEqual(
            rules,
            ['testGetRuleInvocationStack']
            )
        

class TestLexer(unittest.TestCase):

    def testInit(self):
        """Lexer.__init__()"""

        stream = antlr3.StringStream('foo')
        antlr3.Lexer(stream)
        

if __name__ == "__main__":
    unittest.main(testRunner=unittest.TextTestRunner(verbosity=2))
