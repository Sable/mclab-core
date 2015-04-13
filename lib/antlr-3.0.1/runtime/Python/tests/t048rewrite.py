import unittest
import textwrap
import antlr3
import testbase

class T(testbase.ANTLRTest):
    def setUp(self):
        self.compileGrammar()

    def _parse(self, input):
        cStream = antlr3.StringStream(input)
        lexer = self.getLexer(cStream)
        tStream = antlr3.TokenRewriteStream(lexer)
        tStream.LT(1) # fill buffer

        return tStream

    
    def testInsertBeforeIndex0(self):
        tokens = self._parse("abc")
        tokens.insertBefore(0, "0")
        
        result = tokens.toString()
        expecting = "0abc"
        self.failUnlessEqual(result, expecting)


    def testInsertAfterLastIndex(self):
        tokens = self._parse("abc")
        tokens.insertAfter(2, "x")
        
        result = tokens.toString()
        expecting = "abcx"
        self.failUnlessEqual(result, expecting)


    def test2InsertBeforeAfterMiddleIndex(self):
        tokens = self._parse("abc")
        tokens.insertBefore(1, "x")
        tokens.insertAfter(1, "x")
        
        result = tokens.toString()
        expecting = "axbxc"
        self.failUnlessEqual(result, expecting)


    def testReplaceIndex0(self):
        tokens = self._parse("abc")
        tokens.replace(0, "x")

        result = tokens.toString()
        expecting = "xbc"
        self.failUnlessEqual(result, expecting)


    def testReplaceLastIndex(self):
        tokens = self._parse("abc")
        tokens.replace(2, "x")

        result = tokens.toString()
        expecting = "abx"
        self.failUnlessEqual(result, expecting)


    def testReplaceMiddleIndex(self):
        tokens = self._parse("abc")
        tokens.replace(1, "x")

        result = tokens.toString()
	expecting = "axc"
        self.failUnlessEqual(result, expecting)


    def test2ReplaceMiddleIndex(self):
        tokens = self._parse("abc")
        tokens.replace(1, "x")
        tokens.replace(1, "y")

        result = tokens.toString()
        expecting = "ayc"
        self.failUnlessEqual(result, expecting)


    def testReplaceThenDeleteMiddleIndex(self):
        tokens = self._parse("abc")
        tokens.replace(1, "x")
        tokens.delete(1)

        result = tokens.toString()
        expecting = "ac"
        self.failUnlessEqual(result, expecting)


    def testReplaceThenInsertSameIndex(self):
        tokens = self._parse("abc")
        tokens.replace(0, "x")
        tokens.insertBefore(0, "0")

        result = tokens.toString()
        expecting = "0xbc"
        self.failUnlessEqual(result, expecting)


    def testReplaceThen2InsertSameIndex(self):
        tokens = self._parse("abc")
        tokens.replace(0, "x")
        tokens.insertBefore(0, "y")
        tokens.insertBefore(0, "z")

        result = tokens.toString()
        expecting = "zyxbc"
        self.failUnlessEqual(result, expecting)


    def testInsertThenReplaceSameIndex(self):
        tokens = self._parse("abc")
        tokens.insertBefore(0, "0")
        tokens.replace(0, "x")

        result = tokens.toString()
        expecting = "0xbc"
        self.failUnlessEqual(result, expecting)


    def test2InsertMiddleIndex(self):
        tokens = self._parse("abc")
        tokens.insertBefore(1, "x")
        tokens.insertBefore(1, "y")

        result = tokens.toString()
        expecting = "ayxbc"
        self.failUnlessEqual(result, expecting)


    def test2InsertThenReplaceIndex0(self):
        tokens = self._parse("abc")
        tokens.insertBefore(0, "x")
        tokens.insertBefore(0, "y")
        tokens.replace(0, "z")

        result = tokens.toString()
        expecting = "yxzbc"
        self.failUnlessEqual(result, expecting)


    def testReplaceThenInsertBeforeLastIndex(self):
        tokens = self._parse("abc")
        tokens.replace(2, "x")
        tokens.insertBefore(2, "y")

        result = tokens.toString()
        expecting = "abyx"
        self.failUnlessEqual(result, expecting)


    def testInsertThenReplaceLastIndex(self):
        tokens = self._parse("abc")
        tokens.insertBefore(2, "y")
        tokens.replace(2, "x")

        result = tokens.toString()
        expecting = "abyx"
        self.failUnlessEqual(result, expecting)


    def testReplaceThenInsertAfterLastIndex(self):
        tokens = self._parse("abc")
        tokens.replace(2, "x")
        tokens.insertAfter(2, "y")
        
        result = tokens.toString()
	expecting = "abxy"
        self.failUnlessEqual(result, expecting)


    def testReplaceRangeThenInsertInMiddle(self):
        tokens = self._parse("abcccba")
        tokens.replace(2, 4, "x")
        # no effect can't insert in middle of replaced region
        tokens.insertBefore(3, "y") 

        result = tokens.toString()
        expecting = "abxba"
        self.failUnlessEqual(result, expecting)


    def testReplaceRangeThenInsertAtLeftEdge(self):
        tokens = self._parse("abcccba")
        tokens.replace(2, 4, "x")
        tokens.insertBefore(2, "y")

        result = tokens.toString()
        expecting = "abyxba"
        self.failUnlessEqual(result, expecting)


    def testReplaceRangeThenInsertAtRightEdge(self):
        tokens = self._parse("abcccba")
        tokens.replace(2, 4, "x")
        tokens.insertBefore(4, "y") # no effect; within range of a replace

        result = tokens.toString()
        expecting = "abxba"
        self.failUnlessEqual(result, expecting)


    def testReplaceRangeThenInsertAfterRightEdge(self):
        tokens = self._parse("abcccba")
        tokens.replace(2, 4, "x")
        tokens.insertAfter(4, "y")

        result = tokens.toString()
        expecting = "abxyba"
        self.failUnlessEqual(result, expecting)


    def testReplaceAll(self):
        tokens = self._parse("abcccba")
        tokens.replace(0, 6, "x")

        result = tokens.toString()
        expecting = "x"
        self.failUnlessEqual(result, expecting)


    def testReplaceSubsetThenFetch(self):
        tokens = self._parse("abcccba")
        tokens.replace(2, 4, "xyz")

        result = tokens.toString(0,6)
        expecting = "abxyzba"
        self.failUnlessEqual(result, expecting)


    def testReplaceThenReplaceSuperset(self):
        tokens = self._parse("abcccba")
        tokens.replace(2, 4, "xyz")
        tokens.replace(2, 5, "foo") # kills previous replace
        
        result = tokens.toString()
        expecting = "abfooa"
        self.failUnlessEqual(result, expecting)


    def testReplaceThenReplaceLowerIndexedSuperset(self):
        tokens = self._parse("abcccba")
        tokens.replace(2, 4, "xyz")
        # executes first since 1<2; then ignores replace@2 as it skips
        # over 1..3
        tokens.replace(1, 3, "foo") 

        result = tokens.toString()
        expecting = "afoocba"
        self.failUnlessEqual(result, expecting)

    def testReplaceSingleMiddleThenOverlappingSuperset(self):
        tokens = self._parse("abcba")
        tokens.replace(2, 2, "xyz")
        tokens.replace(0, 3, "foo")
        
        result = tokens.toString()
        expecting = "fooa"
        self.failUnlessEqual(result, expecting)


if __name__ == '__main__':
    unittest.main()

