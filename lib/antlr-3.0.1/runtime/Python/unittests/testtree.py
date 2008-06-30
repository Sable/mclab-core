# -*- coding: utf-8 -*-

import os
import unittest
from StringIO import StringIO

from antlr3.tree import CommonTreeNodeStream, CommonTree
from antlr3 import CommonToken, UP, DOWN, EOF


class TestTreeNodeStream(unittest.TestCase):
    """Test case for the TreeNodeStream class."""

    def newStream(self, t):
        """Build new stream; let's us override to test other streams."""
        return CommonTreeNodeStream(t)


    def testSingleNode(self):
        t = CommonTree(CommonToken(101))

        stream = self.newStream(t)
        expecting = "101"
        found = self.toNodesOnlyString(stream)
        self.failUnlessEqual(expecting, found)

        expecting = "101"
        found = str(stream)
        self.failUnlessEqual(expecting, found)


    def test4Nodes(self):
        # ^(101 ^(102 103) 104)
        t = CommonTree(CommonToken(101))
        t.addChild(CommonTree(CommonToken(102)))
        t.getChild(0).addChild(CommonTree(CommonToken(103)))
        t.addChild(CommonTree(CommonToken(104)))

        stream = self.newStream(t)
        expecting = "101 102 103 104"
        found = self.toNodesOnlyString(stream)
        self.failUnlessEqual(expecting, found)

        expecting = "101 2 102 2 103 3 104 3"
        found = str(stream)
        self.failUnlessEqual(expecting, found)


    def testList(self):
        root = CommonTree(None)

        t = CommonTree(CommonToken(101))
        t.addChild(CommonTree(CommonToken(102)))
        t.getChild(0).addChild(CommonTree(CommonToken(103)))
        t.addChild(CommonTree(CommonToken(104)))

        u = CommonTree(CommonToken(105))

        root.addChild(t)
        root.addChild(u)

        stream = CommonTreeNodeStream(root)
        expecting = "101 102 103 104 105"
        found = self.toNodesOnlyString(stream)
        self.failUnlessEqual(expecting, found)

        expecting = "101 2 102 2 103 3 104 3 105"
        found = str(stream)
        self.failUnlessEqual(expecting, found)


    def testFlatList(self):
        root = CommonTree(None)

        root.addChild(CommonTree(CommonToken(101)))
        root.addChild(CommonTree(CommonToken(102)))
        root.addChild(CommonTree(CommonToken(103)))

        stream = CommonTreeNodeStream(root)
        expecting = "101 102 103"
        found = self.toNodesOnlyString(stream)
        self.failUnlessEqual(expecting, found)

        expecting = "101 102 103"
        found = str(stream)
        self.failUnlessEqual(expecting, found)


    def testListWithOneNode(self):
        root = CommonTree(None)

        root.addChild(CommonTree(CommonToken(101)))

        stream = CommonTreeNodeStream(root)
        expecting = "101"
        found = self.toNodesOnlyString(stream)
        self.failUnlessEqual(expecting, found)

        expecting = "101"
        found = str(stream)
        self.failUnlessEqual(expecting, found)


    def testAoverB(self):
        t = CommonTree(CommonToken(101))
        t.addChild(CommonTree(CommonToken(102)))

        stream = self.newStream(t)
        expecting = "101 102"
        found = self.toNodesOnlyString(stream)
        self.failUnlessEqual(expecting, found)

        expecting = "101 2 102 3"
        found = str(stream)
        self.failUnlessEqual(expecting, found)


    def testLT(self):
        # ^(101 ^(102 103) 104)
        t = CommonTree(CommonToken(101))
        t.addChild(CommonTree(CommonToken(102)))
        t.getChild(0).addChild(CommonTree(CommonToken(103)))
        t.addChild(CommonTree(CommonToken(104)))

        stream = self.newStream(t)
        self.failUnlessEqual(101, stream.LT(1).getType())
        self.failUnlessEqual(DOWN, stream.LT(2).getType())
        self.failUnlessEqual(102, stream.LT(3).getType())
        self.failUnlessEqual(DOWN, stream.LT(4).getType())
        self.failUnlessEqual(103, stream.LT(5).getType())
        self.failUnlessEqual(UP, stream.LT(6).getType())
        self.failUnlessEqual(104, stream.LT(7).getType())
        self.failUnlessEqual(UP, stream.LT(8).getType())
        self.failUnlessEqual(EOF, stream.LT(9).getType())
        # check way ahead
        self.failUnlessEqual(EOF, stream.LT(100).getType())


    def testMarkRewindEntire(self):
        # ^(101 ^(102 103 ^(106 107) ) 104 105)
        # stream has 7 real + 6 nav nodes
        # Sequence of types: 101 DN 102 DN 103 106 DN 107 UP UP 104 105 UP EOF
        r0 = CommonTree(CommonToken(101))
        r1 = CommonTree(CommonToken(102))
        r0.addChild(r1)
        r1.addChild(CommonTree(CommonToken(103)))
        r2 = CommonTree(CommonToken(106))
        r2.addChild(CommonTree(CommonToken(107)))
        r1.addChild(r2)
        r0.addChild(CommonTree(CommonToken(104)))
        r0.addChild(CommonTree(CommonToken(105)))

        stream = CommonTreeNodeStream(r0)
        m = stream.mark() # MARK
        for _ in range(13): # consume til end
            stream.LT(1)
            stream.consume()

        self.failUnlessEqual(EOF, stream.LT(1).getType())
        self.failUnlessEqual(UP, stream.LT(-1).getType())
        stream.rewind(m)      # REWIND

        # consume til end again :)
        for _ in range(13): # consume til end
            stream.LT(1)
            stream.consume()

        self.failUnlessEqual(EOF, stream.LT(1).getType())
        self.failUnlessEqual(UP, stream.LT(-1).getType())


    def testMarkRewindInMiddle(self):
        # ^(101 ^(102 103 ^(106 107) ) 104 105)
        # stream has 7 real + 6 nav nodes
        # Sequence of types: 101 DN 102 DN 103 106 DN 107 UP UP 104 105 UP EOF
        r0 = CommonTree(CommonToken(101))
        r1 = CommonTree(CommonToken(102))
        r0.addChild(r1)
        r1.addChild(CommonTree(CommonToken(103)))
        r2 = CommonTree(CommonToken(106))
        r2.addChild(CommonTree(CommonToken(107)))
        r1.addChild(r2)
        r0.addChild(CommonTree(CommonToken(104)))
        r0.addChild(CommonTree(CommonToken(105)))

        stream = CommonTreeNodeStream(r0)
        for _ in range(7): # consume til middle
            #System.out.println(tream.LT(1).getType())
            stream.consume()

        self.failUnlessEqual(107, stream.LT(1).getType())
        m = stream.mark() # MARK
        stream.consume() # consume 107
        stream.consume() # consume UP
        stream.consume() # consume UP
        stream.consume() # consume 104
        stream.rewind(m)      # REWIND

        self.failUnlessEqual(107, stream.LT(1).getType())
        stream.consume()
        self.failUnlessEqual(UP, stream.LT(1).getType())
        stream.consume()
        self.failUnlessEqual(UP, stream.LT(1).getType())
        stream.consume()
        self.failUnlessEqual(104, stream.LT(1).getType())
        stream.consume()
        # now we're past rewind position
        self.failUnlessEqual(105, stream.LT(1).getType())
        stream.consume()
        self.failUnlessEqual(UP, stream.LT(1).getType())
        stream.consume()
        self.failUnlessEqual(EOF, stream.LT(1).getType())
        self.failUnlessEqual(UP, stream.LT(-1).getType())


    def testMarkRewindNested(self):
        # ^(101 ^(102 103 ^(106 107) ) 104 105)
        # stream has 7 real + 6 nav nodes
        # Sequence of types: 101 DN 102 DN 103 106 DN 107 UP UP 104 105 UP EOF
        r0 = CommonTree(CommonToken(101))
        r1 = CommonTree(CommonToken(102))
        r0.addChild(r1)
        r1.addChild(CommonTree(CommonToken(103)))
        r2 = CommonTree(CommonToken(106))
        r2.addChild(CommonTree(CommonToken(107)))
        r1.addChild(r2)
        r0.addChild(CommonTree(CommonToken(104)))
        r0.addChild(CommonTree(CommonToken(105)))

        stream = CommonTreeNodeStream(r0)
        m = stream.mark() # MARK at start
        stream.consume() # consume 101
        stream.consume() # consume DN
        m2 = stream.mark() # MARK on 102
        stream.consume() # consume 102
        stream.consume() # consume DN
        stream.consume() # consume 103
        stream.consume() # consume 106
        stream.rewind(m2)      # REWIND to 102
        self.failUnlessEqual(102, stream.LT(1).getType())
        stream.consume()
        self.failUnlessEqual(DOWN, stream.LT(1).getType())
        stream.consume()
        # stop at 103 and rewind to start
        stream.rewind(m) # REWIND to 101
        self.failUnlessEqual(101, stream.LT(1).getType())
        stream.consume()
        self.failUnlessEqual(DOWN, stream.LT(1).getType())
        stream.consume()
        self.failUnlessEqual(102, stream.LT(1).getType())
        stream.consume()
        self.failUnlessEqual(DOWN, stream.LT(1).getType())


    def testSeek(self):
        # ^(101 ^(102 103 ^(106 107) ) 104 105)
        # stream has 7 real + 6 nav nodes
        # Sequence of types: 101 DN 102 DN 103 106 DN 107 UP UP 104 105 UP EOF
        r0 = CommonTree(CommonToken(101))
        r1 = CommonTree(CommonToken(102))
        r0.addChild(r1)
        r1.addChild(CommonTree(CommonToken(103)))
        r2 = CommonTree(CommonToken(106))
        r2.addChild(CommonTree(CommonToken(107)))
        r1.addChild(r2)
        r0.addChild(CommonTree(CommonToken(104)))
        r0.addChild(CommonTree(CommonToken(105)))

        stream = CommonTreeNodeStream(r0)
        stream.consume() # consume 101
        stream.consume() # consume DN
        stream.consume() # consume 102
        stream.seek(7)   # seek to 107
        self.failUnlessEqual(107, stream.LT(1).getType())
        stream.consume() # consume 107
        stream.consume() # consume UP
        stream.consume() # consume UP
        self.failUnlessEqual(104, stream.LT(1).getType())


    def testSeekFromStart(self):
        # ^(101 ^(102 103 ^(106 107) ) 104 105)
        # stream has 7 real + 6 nav nodes
        # Sequence of types: 101 DN 102 DN 103 106 DN 107 UP UP 104 105 UP EOF
        r0 = CommonTree(CommonToken(101))
        r1 = CommonTree(CommonToken(102))
        r0.addChild(r1)
        r1.addChild(CommonTree(CommonToken(103)))
        r2 = CommonTree(CommonToken(106))
        r2.addChild(CommonTree(CommonToken(107)))
        r1.addChild(r2)
        r0.addChild(CommonTree(CommonToken(104)))
        r0.addChild(CommonTree(CommonToken(105)))

        stream = CommonTreeNodeStream(r0)
        stream.seek(7)   # seek to 107
        self.failUnlessEqual(107, stream.LT(1).getType())
        stream.consume() # consume 107
        stream.consume() # consume UP
        stream.consume() # consume UP
        self.failUnlessEqual(104, stream.LT(1).getType())


    def toNodesOnlyString(self, nodes):
        buf = []
        for i in range(nodes.size()):
            t = nodes.LT(i+1)
            type = nodes.getTreeAdaptor().getType(t)
            if not (type==DOWN or type==UP):
                buf.append(str(type))

        return ' '.join(buf)
    

class TestCommonTreeNodeStream(unittest.TestCase):
    """Test case for the CommonTreeNodeStream class."""

    def testPushPop(self):
        # ^(101 ^(102 103) ^(104 105) ^(106 107) 108 109)
        # stream has 9 real + 8 nav nodes
        # Sequence of types: 101 DN 102 DN 103 UP 104 DN 105 UP 106 DN 107 UP 108 109 UP
        r0 = CommonTree(CommonToken(101))
        r1 = CommonTree(CommonToken(102))
        r1.addChild(CommonTree(CommonToken(103)))
        r0.addChild(r1)
        r2 = CommonTree(CommonToken(104))
        r2.addChild(CommonTree(CommonToken(105)))
        r0.addChild(r2)
        r3 = CommonTree(CommonToken(106))
        r3.addChild(CommonTree(CommonToken(107)))
        r0.addChild(r3)
        r0.addChild(CommonTree(CommonToken(108)))
        r0.addChild(CommonTree(CommonToken(109)))

        stream = CommonTreeNodeStream(r0)
        expecting = "101 2 102 2 103 3 104 2 105 3 106 2 107 3 108 109 3"
        found = str(stream)
        self.failUnlessEqual(expecting, found)

        # Assume we want to hit node 107 and then "call 102" then return

        indexOf102 = 2
        indexOf107 = 12
        for _ in range(indexOf107):# consume til 107 node
            stream.consume()
        
        # CALL 102
        self.failUnlessEqual(107, stream.LT(1).getType())
        stream.push(indexOf102)
        self.failUnlessEqual(102, stream.LT(1).getType())
        stream.consume() # consume 102
        self.failUnlessEqual(DOWN, stream.LT(1).getType())
        stream.consume() # consume DN
        self.failUnlessEqual(103, stream.LT(1).getType())
        stream.consume() # consume 103
        self.failUnlessEqual(UP, stream.LT(1).getType())
        # RETURN
        stream.pop()
        self.failUnlessEqual(107, stream.LT(1).getType())


    def testNestedPushPop(self):
        # ^(101 ^(102 103) ^(104 105) ^(106 107) 108 109)
        # stream has 9 real + 8 nav nodes
        # Sequence of types: 101 DN 102 DN 103 UP 104 DN 105 UP 106 DN 107 UP 108 109 UP
        r0 = CommonTree(CommonToken(101))
        r1 = CommonTree(CommonToken(102))
        r1.addChild(CommonTree(CommonToken(103)))
        r0.addChild(r1)
        r2 = CommonTree(CommonToken(104))
        r2.addChild(CommonTree(CommonToken(105)))
        r0.addChild(r2)
        r3 = CommonTree(CommonToken(106))
        r3.addChild(CommonTree(CommonToken(107)))
        r0.addChild(r3)
        r0.addChild(CommonTree(CommonToken(108)))
        r0.addChild(CommonTree(CommonToken(109)))

        stream = CommonTreeNodeStream(r0)

        # Assume we want to hit node 107 and then "call 102", which
        # calls 104, then return

        indexOf102 = 2
        indexOf107 = 12
        for _ in range(indexOf107): # consume til 107 node
            stream.consume()

        self.failUnlessEqual(107, stream.LT(1).getType())
        # CALL 102
        stream.push(indexOf102)
        self.failUnlessEqual(102, stream.LT(1).getType())
        stream.consume() # consume 102
        self.failUnlessEqual(DOWN, stream.LT(1).getType())
        stream.consume() # consume DN
        self.failUnlessEqual(103, stream.LT(1).getType())
        stream.consume() # consume 103

        # CALL 104
        indexOf104 = 6
        stream.push(indexOf104)
        self.failUnlessEqual(104, stream.LT(1).getType())
        stream.consume() # consume 102
        self.failUnlessEqual(DOWN, stream.LT(1).getType())
        stream.consume() # consume DN
        self.failUnlessEqual(105, stream.LT(1).getType())
        stream.consume() # consume 103
        self.failUnlessEqual(UP, stream.LT(1).getType())
        # RETURN (to UP node in 102 subtree)
        stream.pop()

        self.failUnlessEqual(UP, stream.LT(1).getType())
        # RETURN (to empty stack)
        stream.pop()
        self.failUnlessEqual(107, stream.LT(1).getType())


    def testPushPopFromEOF(self):
        # ^(101 ^(102 103) ^(104 105) ^(106 107) 108 109)
        # stream has 9 real + 8 nav nodes
        # Sequence of types: 101 DN 102 DN 103 UP 104 DN 105 UP 106 DN 107 UP 108 109 UP
        r0 = CommonTree(CommonToken(101))
        r1 = CommonTree(CommonToken(102))
        r1.addChild(CommonTree(CommonToken(103)))
        r0.addChild(r1)
        r2 = CommonTree(CommonToken(104))
        r2.addChild(CommonTree(CommonToken(105)))
        r0.addChild(r2)
        r3 = CommonTree(CommonToken(106))
        r3.addChild(CommonTree(CommonToken(107)))
        r0.addChild(r3)
        r0.addChild(CommonTree(CommonToken(108)))
        r0.addChild(CommonTree(CommonToken(109)))

        stream = CommonTreeNodeStream(r0)

        while stream.LA(1) != EOF:
            stream.consume()

        indexOf102 = 2
        indexOf104 = 6
        self.failUnlessEqual(EOF, stream.LT(1).getType())

        # CALL 102
        stream.push(indexOf102)
        self.failUnlessEqual(102, stream.LT(1).getType())
        stream.consume() # consume 102
        self.failUnlessEqual(DOWN, stream.LT(1).getType())
        stream.consume() # consume DN
        self.failUnlessEqual(103, stream.LT(1).getType())
        stream.consume() # consume 103
        self.failUnlessEqual(UP, stream.LT(1).getType())
        # RETURN (to empty stack)
        stream.pop()
        self.failUnlessEqual(EOF, stream.LT(1).getType())

        # CALL 104
        stream.push(indexOf104)
        self.failUnlessEqual(104, stream.LT(1).getType())
        stream.consume() # consume 102
        self.failUnlessEqual(DOWN, stream.LT(1).getType())
        stream.consume() # consume DN
        self.failUnlessEqual(105, stream.LT(1).getType())
        stream.consume() # consume 103
        self.failUnlessEqual(UP, stream.LT(1).getType())
        # RETURN (to empty stack)
        stream.pop()
        self.failUnlessEqual(EOF, stream.LT(1).getType())


if __name__ == "__main__":
    unittest.main(testRunner=unittest.TextTestRunner(verbosity=2))
