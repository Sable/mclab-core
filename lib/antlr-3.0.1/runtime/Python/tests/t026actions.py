import antlr3
import testbase
import unittest


class t026actions(testbase.ANTLRTest):
    def setUp(self):
        self.compileGrammar()
        

    def testValid1(self):
        cStream = antlr3.StringStream('foobar _Ab98 \n A12sdf')
        lexer = self.getLexer(cStream)
        tStream = antlr3.CommonTokenStream(lexer)
        parser = self.getParser(tStream)
        try:
            events = parser.prog()
            self.fail()
        except antlr3.RecognitionException:
            pass


if __name__ == '__main__':
    unittest.main()
