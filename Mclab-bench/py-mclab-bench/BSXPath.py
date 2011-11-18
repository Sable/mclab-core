# -*- coding: utf-8 -*-

"""
BSXPath.py: XPathEvaluator Extension for BeautifulSoup

"""
__version__ = '0.01e'             # based on JavaScript-XPath 0.1.11 (c) 2007 Cybozu Labs, Inc. (http://coderepos.org/share/wiki/JavaScript-XPath)
__date__    = '2009-04-12'
__license__ = 'MIT-style license'
__author__  = 'furyu'             # http://furyu.tea-nifty.com/annex/
                                  # http://d.hatena.ne.jp/furyu-tei/
"""
Usage:
  from BSXPath import BSXPathEvaluator,XPathResult
  
  #*** PREPARATION (create object)
  document = BSXPathEvaluator(<html>) # BSXPathEvaluator is sub-class of BeautifulSoup
    # html: HTML (text string)
  
  #*** BASIC OPERATIONS
  result = document.evaluate(<expression>,<node>,None,<type>,None)
    # expression: XPath expression
    # node      : base context-node(document is document-root)
    # type      : XPathResult.<name>
    #   name      : ANY_TYPE, NUMBER_TYPE, STRING_TYPE, BOOLEAN_TYPE, UNORDERED_NODE_ITERATOR_TYPE, ORDERED_NODE_ITERATOR_TYPE
    #               UNORDERED_NODE_SNAPSHOT_TYPE, ORDERED_NODE_SNAPSHOT_TYPE, ANY_UNORDERED_NODE_TYPE, FIRST_ORDERED_NODE_TYPE
    # (*) 3rd(resolver) and 5th(result) arguments are not implemented
  length = result.snapshotLength
  node   = result.snapshotItem(<number>)
  
  #*** USEFUL WRAPPER-FUNCTIONS
  nodes = document.getItemList(<expression>[,<node>])
  first = document.getFirstItem(<expression>[,<node>])
    # expression: XPath expression
    # node(optional): base context-node(default: document(document-root))


Examples:
  from BSXPath import BSXPathEvaluator,XPathResult
  
  html = '<html><head><title>Hello, DOM 3 XPath!</title></head><body><h1>Hello, DOM 3 XPath!</h1><p>This is XPathEvaluator Extension for BeautifulSoup.</p><p>This is based on JavaScript-XPath!</p></body>'
  
  document = BSXPathEvaluator(html)
  
  result = document.evaluate('//h1/text()[1]',document,None,XPathResult.STRING_TYPE,None)
  print result.stringValue
  # Hello, DOM 3 XPath!

  result = document.evaluate('//h1',document,None,XPathResult.ORDERED_NODE_SNAPSHOT_TYPE,None)
  print result.snapshotLength
  # 1
  print result.snapshotItem(0)
  # <h1>Hello, DOM 3 XPath!</h1>
  
  nodes = document.getItemList('//p')
  print len(nodes)
  # 2
  print nodes
  # [<p>This is XPathEvaluator Extension for BeautifulSoup.</p>, <p>This is based on JavaScript-XPath!</p>]
  
  first = document.getFirstItem('//p')
  print first
  # <p>This is XPathEvaluator Extension for BeautifulSoup.</p>


Notice:
  - This is based on JavaScript-XPath (c) 2007 Cybozu Labs, Inc. (http://coderepos.org/share/wiki/JavaScript-XPath)
  - Required:
     - Python 2.5+
     - BeautifulSoup 3.0.7+(recommended) or 3.1.0+

"""
import re,types,math,datetime
#import logging
from BeautifulSoup import *

try:
  if DEFAULT_OUTPUT_ENCODING:
    pass
except:
  DEFAULT_OUTPUT_ENCODING='utf-8'


#***** Optional Parameters
USE_NODE_CACHE=True
USE_NODE_INDEX=True


#***** General Functions
def throwError(str):
  raise ValueError, str
  

def typeof(obj):
  if isinstance(obj,bool):
    return 'boolean'
  if isinstance(obj,int) or isinstance(obj,float):
    return 'number'
  if isinstance(obj,basestring):
    return 'string'
  if isinstance(obj,types.FunctionType):
    return 'function'
  return 'object'


def isNaN(obj):
  if isinstance(obj,int) or isinstance(obj,float):
    return False
  if not isinstance(obj,basestring):
    return True
  if obj.isdigit():
    return False
  try:
    float(obj)
    return False
  except:
    return True


def toNumber(obj):
  if isinstance(obj,int) or isinstance(obj,float):
    return obj
  if isinstance(obj,basestring):
    if obj.isdigit():
      return int(obj)
    try:
      return float(obj)
    except:
      return obj
  return obj

def toBoolean(obj):
  return bool(obj)


def toString(obj):
  if isinstance(obj,bool):
    return u'true' if obj else u'false'
  if isinstance(obj,str) or isinstance(obj,int) or isinstance(obj,float):
    return unicode(obj)
  return obj



#***** General Classes
class ExtDict(dict):
  def __getattr__(self,name):
    try:
      attr=super(ExtDict,self).__getattr__(name)
    except:
      if not self.has_key(name):
        raise AttributeError,name
      attr=self.get(name)
    return attr



#***** Common Definitions
indent_space='    '


#{ // Regular Expressions
re_has_ualpha=re.compile(r'(?![0-9])[\w]')
re_seqspace=re.compile(r'\s+')
re_firstspace=re.compile(r'^\s')
re_lastspace=re.compile(r'\s$')
#} // end of Regular Expressions


#{ // NodeTypeDOM
NodeTypeDOM=ExtDict({
  'ANY_NODE'                   :0
, 'ELEMENT_NODE'               :1
, 'ATTRIBUTE_NODE'             :2
, 'TEXT_NODE'                  :3
, 'CDATA_SECTION_NODE'         :4
, 'ENTITY_REFERENCE_NODE'      :5
, 'ENTITY_NODE'                :6
, 'PROCESSING_INSTRUCTION_NODE':7
, 'COMMENT_NODE'               :8
, 'DOCUMENT_NODE'              :9
, 'DOCUMENT_TYPE_NODE'         :10
, 'DOCUMENT_FRAGMENT_NODE'     :11
, 'NOTATION_NODE'              :12
})

NodeTypeBS=ExtDict({
  'BSXPathEvaluator'     :NodeTypeDOM.DOCUMENT_NODE
, 'NavigableString'      :NodeTypeDOM.TEXT_NODE
, 'CData'                :NodeTypeDOM.CDATA_SECTION_NODE
, 'ProcessingInstruction':NodeTypeDOM.PROCESSING_INSTRUCTION_NODE
, 'Comment'              :NodeTypeDOM.COMMENT_NODE
, 'Declaration'          :NodeTypeDOM.ANY_NODE
, 'Tag'                  :NodeTypeDOM.ELEMENT_NODE
})
#} // end of NodeTypeDOM


#{ // NodeUtil
def makeNodeUtils():
  re_type_document_type=re.compile(r'^DOCTYPE\s')
  re_type_entity       =re.compile(r'^ENTITY\s')
  re_type_notation     =re.compile(r'^NOTATION\s')
  
  #re_processing_instruction=re.compile(r'^(.*?)\s+(.*?)\?*$')
  re_processing_instruction=re.compile(r'^(.*?)(\s+.*?)\?*$')
  
  re_declaration_name=re.compile(r'^([^\s]+)\s+([\%]?)\s*([^\s]+)\s')
  
  def makeNU_BS():
    def _nodeType(node):
      if getattr(node,'nodeType',None)==NodeTypeDOM.ATTRIBUTE_NODE:
        return node.nodeType
      nodeType=NodeTypeBS.get(node.__class__.__name__)
      if nodeType==NodeTypeDOM.ANY_NODE:
        str=NavigableString.encode(node,DEFAULT_OUTPUT_ENCODING)
        if re_type_document_type.search(str):
          nodeType=NodeTypeDOM.DOCUMENT_TYPE_NODE
        elif re_type_entity.search(str):
          nodeType=NodeTypeDOM.ENTITY_NODE
        elif re_type_notation.search(str):
          nodeType=NodeTypeDOM.NOTATION_NODE
      return nodeType
    
    def _nodeName(node):
      if getattr(node,'nodeType',None)==NodeTypeDOM.ATTRIBUTE_NODE:
        return node.nodeName.lower()
      nodeType=_nodeType(node)
      if nodeType==NodeTypeDOM.DOCUMENT_NODE:
        return '#document'
      elif nodeType==NodeTypeDOM.TEXT_NODE:
        return '#text'
      elif nodeType==NodeTypeDOM.CDATA_SECTION_NODE:
        return '#cdata-section'
      elif nodeType==NodeTypeDOM.PROCESSING_INSTRUCTION_NODE:
        mrslt=re_processing_instruction.search(NavigableString.encode(node,DEFAULT_OUTPUT_ENCODING))
        if mrslt:
          return mrslt.group(1)
        else:
          return NavigableString.encode(node,DEFAULT_OUTPUT_ENCODING)
      elif nodeType==NodeTypeDOM.COMMENT_NODE:
        return '#comment'
      elif nodeType==NodeTypeDOM.DOCUMENT_TYPE_NODE or nodeType==NodeTypeDOM.ENTITY_NODE or nodeType==NodeTypeDOM.NOTATION_NODE:
        mrslt=re_declaration_name.search(NavigableString.encode(node,DEFAULT_OUTPUT_ENCODING))
        if mrslt:
          return mrslt.group(2)
        else:
          return NavigableString.encode(node,DEFAULT_OUTPUT_ENCODING)
      else:
        return node.name.lower()
    
    def _nodeValue(node):
      if getattr(node,'nodeType',None)==NodeTypeDOM.ATTRIBUTE_NODE:
        return node.nodeValue
      nodeType=_nodeType(node)
      if nodeType==NodeTypeDOM.CDATA_SECTION_NODE or \
         nodeType==NodeTypeDOM.COMMENT_NODE or \
         nodeType==NodeTypeDOM.TEXT_NODE:
        return NavigableString.encode(node, DEFAULT_OUTPUT_ENCODING)
      if nodeType==NodeTypeDOM.PROCESSING_INSTRUCTION_NODE:
        mrslt=re_processing_instruction.search(NavigableString.encode(node,DEFAULT_OUTPUT_ENCODING))
        if mrslt:
          return mrslt.group(2)
        else:
          return None
      return None
    
    def _nodeAttrValue(node,attrName):
      if getattr(node,'nodeType',None)==NodeTypeDOM.ATTRIBUTE_NODE:
        return None
      nodeType=_nodeType(node)
      if nodeType!=NodeTypeDOM.ELEMENT_NODE:
        return None
      return node.get(attrName)
    
    def _parentNode(node):
      if getattr(node,'nodeType',None)==NodeTypeDOM.ATTRIBUTE_NODE:
        return node.parentNode
      return node.parent
    
    def _ownerDocument(node):
      owner=getattr(node,'_owner',None)
      if owner:
        return owner
      if getattr(node,'nodeType',None)==NodeTypeDOM.ATTRIBUTE_NODE:
        owner=node.parentNode
      else:
        owner=node
      while True:
        parent=owner.parent
        if not parent:
          break
        owner=parent
      try:
        node._owner=owner
      except:
        pass
      return owner
    
    def pairwise(iterable):
      itnext = iter(iterable).next
      while True:
          yield itnext(), itnext()
    
    def _attributes(node):
      if _nodeType(node)==NodeTypeDOM.ELEMENT_NODE:
        #return node._getAttrMap()
        if not getattr(node,'attrMap'):
          node.attrMap=dict(pairwise(node.attrs))
        return node.attrMap
      else:
        return None
    
    def _contains(node,cnode):
      if _nodeType(node)==NodeTypeDOM.ATTRIBUTE_NODE: node=node.parentNode
      if _nodeType(cnode)==NodeTypeDOM.ATTRIBUTE_NODE: cnode=cnode.parentNode
      return node in cnode.findParents()
    
    def _preceding(node,cnode):
      if _nodeType(node)==NodeTypeDOM.ATTRIBUTE_NODE: node=node.parentNode
      if _nodeType(cnode)==NodeTypeDOM.ATTRIBUTE_NODE: cnode=cnode.parentNode
      #return cnode in node.findAllPrevious()
      return cnode in node.findPreviousSiblings()
    
    def _following(node,cnode):
      if _nodeType(node)==NodeTypeDOM.ATTRIBUTE_NODE: node=node.parentNode
      if _nodeType(cnode)==NodeTypeDOM.ATTRIBUTE_NODE: cnode=cnode.parentNode
      #return cnode in node.findAllNext()
      return cnode in node.findNextSiblings()
    
    def d_getattr(self,name):
      raise AttributeError,name
    
    #{ // ExtPageElement
    class ExtPageElement:
      def __getattr__(self,name):
        if name=='nodeType': return _nodeType(self)
        if name=='nodeName': return _nodeName(self)
        if name=='nodeValue': return _nodeValue(self)
        if name=='parentNode': return _parentNode(self)
        if name=='ownerDocument': return _ownerDocument(self)
        if name=='attributes': return _attributes(self)
        if name=='get': return self.get
        if name=='contains': return self.contains
        if name=='preceding': return self.preceding
        if name=='following': return self.following
        d_getattr(self,name)
      
      def get(self,key,default=None):
        return _nodeAttrValue(self,key)
      
      def contains(self,cnode):
        return _contains(self,cnode)
      
      def preceding(self,cnode):
        return _preceding(self,cnode)
      
      def following(self,cnode):
        return _following(self,cnode)
    
    PageElement.__bases__+=(ExtPageElement,)
    BeautifulSoup.__bases__+=(ExtPageElement,)
    NavigableString.__bases__+=(ExtPageElement,)
    CData.__bases__+=(ExtPageElement,)
    ProcessingInstruction.__bases__+=(ExtPageElement,)
    Comment.__bases__+=(ExtPageElement,)
    Declaration.__bases__+=(ExtPageElement,)
    Tag.__bases__+=(ExtPageElement,)
    
    #} // ExtPageElement
    
    #{ // _extBeautifulSoup
    def _extBeautifulSoup():
      o_getattr=getattr(BeautifulSoup,'__getattr__',d_getattr)
      def e_getattr(self,name):
        if name=='nodeType': return NodeTypeDOM.DOCUMENT_NODE
        if name=='nodeName': return '#document'
        if name=='nodeValue': return None
        if name=='parentNode': return None
        if name=='ownerDocument': return None
        if name=='attributes': return None
        return o_getattr(self,name)
      BeautifulSoup.__getattr__=e_getattr
    _extBeautifulSoup()
    #} // _extBeautifulSoup
    
    #{ // _extNavigableString
    def _extNavigableString():
      o_getattr=getattr(NavigableString,'__getattr__',d_getattr)
      def e_getattr(self,name):
        if name=='nodeType': return NodeTypeDOM.TEXT_NODE
        if name=='nodeName': return '#text'
        if name=='nodeValue': return NavigableString.encode(self,DEFAULT_OUTPUT_ENCODING)
        if name=='parentNode': return self.parent
        if name=='ownerDocument': return _ownerDocument(self)
        if name=='attributes': return None
        return o_getattr(self,name)
      NavigableString.__getattr__=e_getattr
    _extNavigableString()
    #} // _extNavigableString
    
    #{ // _extCData
    def _extCData():
      o_getattr=getattr(CData,'__getattr__',d_getattr)
      def e_getattr(self,name):
        if name=='nodeType': return NodeTypeDOM.CDATA_SECTION_NODE
        if name=='nodeName': return '#cdata-section'
        if name=='nodeValue': return NavigableString.encode(self,DEFAULT_OUTPUT_ENCODING)
        if name=='parentNode': return self.parent
        if name=='ownerDocument': return _ownerDocument(self)
        if name=='attributes': return None
        return o_getattr(self,name)
      CData.__getattr__=e_getattr
    _extCData()
    #} // _extCData
    
    #{ // _extProcessingInstruction
    def _extProcessingInstruction():
      o_getattr=getattr(ProcessingInstruction,'__getattr__',d_getattr)
      def e_getattr(self,name):
        if name=='nodeType': return NodeTypeDOM.PROCESSING_INSTRUCTION_NODE
        if name=='nodeName':
          mrslt=re_processing_instruction.search(NavigableString.encode(self,DEFAULT_OUTPUT_ENCODING))
          return mrslt.group(1) if mrslt else NavigableString.encode(self,DEFAULT_OUTPUT_ENCODING)
        if name=='nodeValue':
          mrslt=re_processing_instruction.search(NavigableString.encode(self,DEFAULT_OUTPUT_ENCODING))
          return mrslt.group(2) if mrslt else None
        if name=='parentNode': return self.parent
        if name=='ownerDocument': return _ownerDocument(self)
        if name=='attributes': return None
        return o_getattr(self,name)
      ProcessingInstruction.__getattr__=e_getattr
    _extProcessingInstruction()
    #} // _extProcessingInstruction
    
    #{ // _extComment
    def _extComment():
      o_getattr=getattr(Comment,'__getattr__',d_getattr)
      def e_getattr(self,name):
        if name=='nodeType': return NodeTypeDOM.COMMENT_NODE
        if name=='nodeName': return '#comment'
        if name=='nodeValue': return NavigableString.encode(self, DEFAULT_OUTPUT_ENCODING)
        if name=='parentNode': return self.parent
        if name=='ownerDocument': return _ownerDocument(self)
        if name=='attributes': return None
        return o_getattr(self,name)
      Comment.__getattr__=e_getattr
    _extComment()
    #} // _extComment
    
    #{ // _extDeclaration
    def _extDeclaration():
      o_getattr=getattr(Declaration,'__getattr__',d_getattr)
      def e_getattr(self,name):
        if name=='nodeType':
          str=NavigableString.encode(self,DEFAULT_OUTPUT_ENCODING)
          if re_type_document_type.search(str):
            return NodeTypeDOM.DOCUMENT_TYPE_NODE
          elif re_type_entity.search(str):
            return NodeTypeDOM.ENTITY_NODE
          elif re_type_notation.search(str):
            return NodeTypeDOM.NOTATION_NODE
          else:
            return NodeTypeDOM.ANY_NODE
        if name=='nodeName':
          mrslt=re_declaration_name.search(NavigableString.encode(self,DEFAULT_OUTPUT_ENCODING))
          return mrslt.group(2) if mrslt else NavigableString.encode(self,DEFAULT_OUTPUT_ENCODING)
        if name=='nodeValue': return None
        if name=='parentNode': return self.parent
        if name=='ownerDocument': return _ownerDocument(self)
        if name=='attributes': return None
        return o_getattr(self,name)
      Declaration.__getattr__=e_getattr
    _extDeclaration()
    #} // _extDeclaration
    
    #{ // _extTag
    def _extTag():
      o_getattr=getattr(Tag,'__getattr__',d_getattr)
      def e_getattr(self,name):
        if name=='nodeType': return NodeTypeDOM.ELEMENT_NODE
        if name=='nodeName': return self.name.lower()
        if name=='nodeValue': return None
        if name=='parentNode': return self.parent
        if name=='ownerDocument': return _ownerDocument(self)
        if name=='attributes': return self._getAttrMap()
        return o_getattr(self,name)
      Tag.__getattr__=e_getattr
    _extTag()
    #} // _extTag
    
    def _it_deepNodes(node):
      child_next=iter(getattr(node,'contents',[])).next
      while True:
        child=child_next()
        yield child
        for gchild in _it_deepNodes(child):
          yield gchild
    
    return ExtDict({
      'nodeType'     :_nodeType
    , 'nodeName'     :_nodeName
    , 'nodeValue'    :_nodeValue
    , 'nodeAttrValue':_nodeAttrValue
    , 'parentNode'   :_parentNode
    , 'ownerDocument':_ownerDocument
    , 'attributes'   :_attributes
    , 'contains'     :_contains
    , 'preceding'    :_preceding
    , 'following'    :_following
    , 'it_deepNodes' :_it_deepNodes
    })
    return
    
  def makeNU():
    def _to(valueType,node):
      if typeof(node)=='string':
        result=node
      else:
        nodeType=node.nodeType
        if nodeType==NodeTypeDOM.ATTRIBUTE_NODE:
          result=node.nodeValue
        else:
          strings=[]
          for _node in NodeUtilBS.it_deepNodes(node):
            if _node.nodeType==NodeTypeDOM.TEXT_NODE:
              strings.append(unicode(_node))
          result=''.join(strings)
      
      if valueType=='number':
        return toNumber(result)
      elif valueType=='boolean':
        return toBoolean(result)
      else:
        return result
    
    def _attrMatch(node,attrName,attrValue):
      if not attrName or \
         not attrValue and node.get(attrName) or \
         (attrValue and node.get(attrName)==attrValue):
        return True
      else:
        return False
    
    def _getDescendantNodes(test,node,nodeset,attrName,attrValue,prevNodeset,prevIndex):
      if prevNodeset:
        prevNodeset.delDescendant(node,prevIndex)
      
      if USE_NODE_CACHE:
        _cachemap=getattr(node,'_cachemap',None)
        if not _cachemap:
          _cachemap=node._cachemap=ExtDict({'attrib':ExtDict({}),'all':None,'tag':ExtDict({})})
        
        if attrValue and attrName:
          _cm=_cachemap.attrib
          _anmap=_cm.get(attrName)
          if not _anmap:
            _anmap=_cm[attrName]=ExtDict({})
          nodes=_anmap.get(attrValue)
          if not nodes:
            nodes=_anmap[attrValue]=[]
            if getattr(node,'findAll',None):
              nodes.extend(node.findAll(attrs={attrName:attrValue}))
          for elm in nodes:
            if test.match(elm):
              nodeset.push(elm)
        
        elif getattr(test,'notOnlyElement',None):
          nodes=_cachemap.all
          if not nodes:
            nodes=_cachemap.all=[]
            for elm in NodeUtilBS.it_deepNodes(node):
              nodes.append(elm)
          
          for elm in nodes:
            if NodeUtil.attrMatch(elm,attrName,attrValue) and test.match(elm):
              nodeset.push(elm)
        
        else:
          nodeType=node.nodeType
          if nodeType==NodeTypeDOM.ELEMENT_NODE or nodeType==NodeTypeDOM.DOCUMENT_NODE:
            _cm=_cachemap.tag
            name=getattr(test,'name',None)
            if not name or name=='*':
              nodes=_cm.get('*')
              if not nodes:
                nodes=_cm['*']=node.findAll()
            else:
              nodes=_cm.get(name)
              if not nodes:
                nodes=_cm[name]=node.findAll([name])
            for elm in nodes:
              if NodeUtil.attrMatch(elm,attrName,attrValue):
                nodeset.push(elm)
      
      else: # USE_NODE_CACHE is False
        if attrValue and attrName:
          if getattr(node,'findAll',None):
            for elm in node.findAll(attrs={attrName:attrValue}):
              if test.match(elm):
                nodeset.push(elm)
          
        elif getattr(test,'notOnlyElement',None):
          for elm in NodeUtilBS.it_deepNodes(node):
            if NodeUtil.attrMatch(elm,attrName,attrValue) and test.match(elm):
              nodeset.push(elm)
          
        else:
          nodeType=node.nodeType
          if nodeType==NodeTypeDOM.ELEMENT_NODE or nodeType==NodeTypeDOM.DOCUMENT_NODE:
            name=getattr(test,'name',None)
            if not name or name=='*':
              nodes=node.findAll()
            else:
              nodes=node.findAll([name])
            for elm in nodes:
              if NodeUtil.attrMatch(elm,attrName,attrValue):
                nodeset.push(elm)
      
      return nodeset
    
    def _getChildNodes(test,node,nodeset,attrName,attrValue,prevNodeset,prevIndex):
      contents=getattr(node,'contents',[])
      for elm in contents:
        if NodeUtil.attrMatch(elm,attrName,attrValue) and test.match(elm):
           nodeset.push(elm)
      
      return nodeset
    
    return ExtDict({
      'to'                :_to
    , 'attrMatch'         :_attrMatch
    , 'getDescendantNodes':_getDescendantNodes
    , 'getChildNodes'     :_getChildNodes
    })
  
  return (makeNU_BS(),makeNU())
  
(NodeUtilBS,NodeUtil)=makeNodeUtils()

#} // end of NodeUtil



#***** Application Classes
#{ // Lexer
class Lexer(object):
  def __init__(self,source):
    tokens=self.tokens=[]
    def anlz_token(mrslt):
      token=mrslt.group()
      if not self.re_strip.search(token):
        tokens.append(token)
      return token
    self.re_token.sub(anlz_token,source,count=0)
    self.index=0
    
  def peek(self,i=0):
    token=self.tokens[self.index+i] if self.index+i<len(self.tokens) else None
    return token
  
  def next(self):
    token=self.tokens[self.index] if self.index<len(self.tokens) else None
    self.index+=1
    return token
  
  def back(self):
    self.index-=1
    token=self.tokens[self.index] if self.index<len(self.tokens) else None
  
  def empty(self):
    return (len(self.tokens)<=self.index)
  
  re_token=re.compile(r'\$?(?:(?![0-9-])[\w-]+:)?(?![0-9-])[\w-]+|\/\/|\.\.|::|\d+(?:\.\d*)?|\.\d+|"[^"]*"|\'[^\']*\'|[!<>]=|(?![0-9-])[\w-]+:\*|\s+|.')
  re_strip=re.compile(r'^\s')
  
#} // end of Lexer


#{ // Ctx
class Ctx(object):
  def __init__(self,node,position=1,last=1):
    self.node=node
    self.position=position
    self.last=last

#} // end of Ctx


#{ // AttributeWrapper
class AttributeWrapper(object):
  def __init__(self,name,value,parent):
    self.nodeType=NodeTypeDOM.ATTRIBUTE_NODE
    self.nodeName=name
    self.nodeValue=value
    self.parentNode=parent
    self.ownerElement=parent
  
  def get(self,key,default=None):
    return None
  
  def contains(self,cnode):
    return NodeUtilBS.contains(self,cnode)
  
  def preceding(self,cnode):
    return NodeUtilBS.preceding(self,cnode)
  
  def following(self,cnode):
    return NodeUtilBS.following(self,cnode)
  
  def __str__(self,encoding=DEFAULT_OUTPUT_ENCODING):
    if encoding:
      return self.nodeValue.encode(encoding)
    else:
      return self.nodeValue
  
  def __unicode__(self):
    return str(self).decode(DEFAULT_OUTPUT_ENCODING)
  
  @classmethod
  def getAttributeWrapper(cls,name,value,parent):
    _mapattr=getattr(parent,'_mapattr',None)
    if not _mapattr:
      _mapattr=parent._mapattr=ExtDict({})
    if _mapattr.get(name):
      return _mapattr[name]
    _mapattr[name]=cls(name,value,parent)
    return _mapattr[name]
  
#} // end of AttributeWrapper


#{ // BaseExpr
class BaseExpr(object):
  def __init__(self):
    pass
  
  def number(self,ctx):
    exrs=self.evaluate(ctx)
    if getattr(exrs,'isNodeSet',None):
      result=exrs.number()
    else:
      result=toNumber(exrs)
    return result
  
  def string(self,ctx):
    exrs=self.evaluate(ctx)
    if getattr(exrs,'isNodeSet',None):
      result=exrs.string()
    else:
      result=toString(exrs)
    return result
  
  def bool(self,ctx):
    exrs=self.evaluate(ctx)
    if getattr(exrs,'isNodeSet',None):
      result=exrs.bool()
    else:
      result=toBoolean(exrs)
    return result
  
#} // end of BaseExpr


#{ // BaseExprHasPredicates
class BaseExprHasPredicates(BaseExpr):
  def __init__(self):
    pass
  
  def evaluatePredicates(self,nodeset,start=0):
    reverse=getattr(self,'reverse',False)
    predicates=getattr(self,'predicates',[])
    
    nodeset.sort()
    
    l0=len(predicates)
    for i in range(start,l0):
      predicate=predicates[i]
      deleteIndexes=[]
      nodes=nodeset.list()
      
      l1=len(nodes)
      for j in range(0,l1):
        position=(l1-j) if reverse else (j+1)
        exrs=predicate.evaluate(Ctx(nodes[j],position,l1))
        
        if typeof(exrs)=='number':
          exrs=(position==exrs)
        elif typeof(exrs)=='string':
          exrs=False if exrs=='' else True
        elif typeof(exrs)=='object':
          exrs=exrs.bool()
        
        if not exrs:
          deleteIndexes.append(j)
      
      r=range(0,len(deleteIndexes))
      r.sort(reverse=True)
      for j in r:
        nodeset._del(deleteIndexes[j])
    
    return nodeset
  
  @classmethod
  def parsePredicates(cls,lexer,expr):
    while lexer.peek()=='[':
      lexer.next()
      if lexer.empty():
        throwError(u'missing predicate expr')
      predicate=BinaryExpr.parse(lexer)
      expr.predicate(predicate)
      if lexer.empty():
        throwError(u'unclosed predicate expr')
      if lexer.next() != ']':
        lexer.back()
        throwError(u'bad token: %s' % (lexer.next()))

#} // end of BaseExprHasPredicates


#{ // BinaryExpr
class BinaryExpr(BaseExpr):
  def __init__(self,op,left,right):
    self.op=op
    self.left=left
    self.right=right
    self.dataType=BinaryExpr.ops[op][2]
    (lneedContextPosition,rneedContextPosition)=(getattr(left,'needContextPosition',None),getattr(right,'needContextPosition',None))
    (lneedContextNode,rneedContextNode)=(getattr(left,'needContextNode',None),getattr(right,'needContextNode',None))
    self.needContextPosition=lneedContextPosition or rneedContextPosition
    self.needContextNode=lneedContextNode or rneedContextNode
    
    if op=='=':
      (ldatatype,rdatatype)=(getattr(left,'datatype',None),getattr(right,'datatype',None))
      (lqattr,rqattr)=(getattr(left,'quickAttr',None),getattr(right,'quickAttr',None))
      
      if not rneedContextNode and not rneedContextPosition and rdatatype!='nodeset' and rdatatype!='void' and lqattr:
        self.quickAttr=True
        self.attrName=left.attrName
        self.attrValueExpr=right
      elif not lneedContextNode and not lneedContextPosition and ldatatype!='nodeset' and ldatatype!='void' and rqattr:
        self.quickAttr=True
        self.attrName=right.attrName
        self.attrValueExpr=left
  
  def evaluate(self,ctx):
    result=BinaryExpr.ops[self.op][1](self.left,self.right,ctx)
    return result
  
  def show(self,indent=''):
    t=''
    t+=indent+'binary: '+self.op+'\n'
    indent+=indent_space
    t+=self.left.show(indent)
    t+=self.right.show(indent)
    return t
  
  # --- Local Functions
  @staticmethod
  def _compare(op,comp,left,right,ctx):
    left=left.evaluate(ctx)
    right=right.evaluate(ctx)
    
    if getattr(left,'isNodeSet',None) and getattr(right,'isNodeSet',None):
      lnodes=left.list()
      rnodes=right.list()
      for lnode in lnodes:
        for rnode in rnodes:
          if comp(NodeUtil.to('string',lnode),NodeUtil.to('string',rnode)):
            return True
      return False
    
    if getattr(left,'isNodeSet',None) or getattr(right,'isNodeSet',None):
      if getattr(left,'isNodeSet',None):
        (nodeset,primitive)=(left,right)
      else:
        (nodeset,primitive)=(right,left)
      
      nodes=nodeset.list()
      type=typeof(primitive)
      for node in nodes:
        if comp(NodeUtil.to(type,node),primitive):
          return True
      return False
    
    if op=='=' or op=='!=':
      if typeof(left)=='boolean' or typeof(right)=='boolean':
        return comp(toBoolean(left),toBoolean(right))
      if typeof(left)=='number' or typeof(right)=='number':
        return comp(toNumber(left),toNumber(right))
      return comp(left,right)
    
    return comp(toNumber(left),toNumber(right))
  
  def _div(left,right,ctx):
    l=left.number(ctx)
    r=right.number(ctx)
    if typeof(l)!='number' or typeof(r)!='number': return 'NaN'
    if r==0:
      sign=int(getattr(left,'op','+')+'1')*int(getattr(right,'op','+')+'1')
      if l==0: return 'NaN'
      elif sign<0: return '-Infinity'
      else: return 'Infinity'
    n=float(l) / float(r)
    n1=int(n)
    return n1 if n1==n else n
  
  def _mod(left,right,ctx):
    l=left.number(ctx)
    r=right.number(ctx)
    if typeof(l)!='number' or typeof(r)!='number': return 'NaN'
    if r==0:
      if l==0: return 'NaN'
      else: return 0
    return l % r
  
  def _mul(left,right,ctx):
    l=left.number(ctx)
    r=right.number(ctx)
    if typeof(l)!='number' or typeof(r)!='number': return 'NaN'
    n=l * r
    n1=int(n)
    return n1 if n1==n else n
  
  def _add(left,right,ctx):
    l=left.number(ctx)
    r=right.number(ctx)
    if typeof(l)!='number' or typeof(r)!='number': return 'NaN'
    n=l + r
    n1=int(n)
    return n1 if n1==n else n
  
  def _sub(left,right,ctx):
    l=left.number(ctx)
    r=right.number(ctx)
    if typeof(l)!='number' or typeof(r)!='number': return 'NaN'
    n=l - r
    n1=int(n)
    return n1 if n1==n else n
  
  def _lt(left,right,ctx):
    return BinaryExpr._compare('<',(lambda a,b:a<b),left,right,ctx)
  
  def _gt(left,right,ctx):
    return BinaryExpr._compare('>',(lambda a,b:a>b),left,right,ctx)
  
  def _le(left,right,ctx):
    return BinaryExpr._compare('<=',(lambda a,b:a<=b),left,right,ctx)
  
  def _ge(left,right,ctx):
    return BinaryExpr._compare('>=',(lambda a,b:a>=b),left,right,ctx)
  
  def _eq(left,right,ctx):
    return BinaryExpr._compare('=',(lambda a,b:a==b),left,right,ctx)
  
  def _ne(left,right,ctx):
    return BinaryExpr._compare('!=',(lambda a,b:a!=b),left,right,ctx)
  
  def _and(left,right,ctx):
    return left.bool(ctx) & right.bool(ctx)
  
  def _or(left,right,ctx):
    return left.bool(ctx) | right.bool(ctx)
  
  ops=ExtDict({
      'div':[6,_div,'number' ]
    , 'mod':[6,_mod,'number' ]
    , '*'  :[6,_mul,'number' ]
    , '+'  :[5,_add,'number' ]
    , '-'  :[5,_sub,'number' ]
    , '<'  :[4,_lt ,'boolean']
    , '>'  :[4,_gt ,'boolean']
    , '<=' :[4,_le ,'boolean']
    , '>=' :[4,_ge ,'boolean']
    , '='  :[3,_eq ,'boolean']
    , '!=' :[3,_ne ,'boolean']
    , 'and':[2,_and,'boolean']
    , 'or' :[1,_or ,'boolean']
  })
  
  @classmethod
  def parse(cls,lexer):
    ops=cls.ops
    stack=[]
    index=lexer.index
    
    while True:
      if lexer.empty():
        throwError(u'missing right expression')
      expr=UnaryExpr.parse(lexer)
      
      op=lexer.next()
      if not op:
        break
      
      info=ops.get(op)
      precedence=info and info[0]
      if not precedence:
        lexer.back()
        break
      
      while 0<len(stack) and precedence<=ops[stack[len(stack)-1]][0]:
        expr=BinaryExpr(stack.pop(),stack.pop(),expr)
      
      stack.extend([expr,op])
    
    while 0<len(stack):
      expr=BinaryExpr(stack.pop(),stack.pop(),expr)
    
    return expr

#} // end of BinaryExpr


#{ // UnaryExpr
class UnaryExpr(BaseExpr):
  def __init__(self,op,expr):
    self.op=op
    self.expr=expr
    
    self.needContextPosition=getattr(expr,'needContextPosition',None)
    self.needContextNode=getattr(expr,'needContextNode',None)
    
    self.datatype='number'
  
  def evaluate(self,ctx):
    result=-self.expr.number(ctx)
    return result
  
  def show(self,indent=''):
    t=''
    t+=indent+'unary: '+self.op+'\n'
    indent+=indent_space
    t+=self.expr.show(indent)
    return t

  ops=ExtDict({
    '-':1
  })
  
  @classmethod
  def parse(cls,lexer):
    ops=cls.ops
    if ops.get(lexer.peek()):
      return cls(lexer.next(),cls.parse(lexer))
    else:
      return UnionExpr.parse(lexer)

#} // end of UnaryExpr


#{ // UnionExpr
class UnionExpr(BaseExpr):
  def __init__(self):
    self.paths=[]
    self.datatype='nodeset'
  
  def evaluate(self,ctx):
    paths=self.paths
    nodeset=NodeSet()
    for path in paths:
      exrs=path.evaluate(ctx)
      if not getattr(exrs,'isNodeSet',None):
        throwError(u'PathExpr must be nodeset')
      nodeset.merge(exrs)
    
    return nodeset
  
  def path(self,path):
    self.paths.append(path)
    
    if getattr(path,'needContextPosition',None):
      self.needContextPosition=True
    if getattr(path,'needContextNode',None):
      self.needContextNode=True
  
  def show(self,indent=''):
    t=''
    t+=indent+'union: '+'\n'
    indent+=indent_space
    for path in self.paths:
      t+=path.show(indent)
    return t
  
  ops=ExtDict({
    '|':1
  })
  
  @classmethod
  def parse(cls,lexer):
    ops=cls.ops
    expr=PathExpr.parse(lexer)
    if not ops.get(lexer.peek()):
      return expr
    
    union=UnionExpr()
    union.path(expr)
    
    while True:
      if not ops.get(lexer.next()):
        break
      if lexer.empty():
        throwError(u'missing next union location path')
      union.path(PathExpr.parse(lexer))
    
    lexer.back()
    return union

#} // end of UnionExpr


#{ // PathExpr
class PathExpr(BaseExpr):
  def __init__(self,filter):
    self.filter=filter
    self.steps=[]
    self.datatype=filter.datatype
    self.needContextPosition=filter.needContextPosition
    self.needContextNode=filter.needContextNode
  
  def evaluate(self,ctx):
    nodeset=self.filter.evaluate(ctx)
    if not getattr(nodeset,'isNodeSet',None):
      throwException('Filter nodeset must be nodeset type')
    
    for _step in self.steps:
      if nodeset.length<=0:
        break
      step=_step[1] # _step=[op,step]
      reverse=step.reverse
      iter=nodeset.iterator(reverse)
      prevNodeset=nodeset
      nodeset=None
      needContextPosition=getattr(step,'needContextPosition',None)
      axis=step.axis
      if not needContextPosition and axis=='following':
        node=iter()
        while True:
          next=iter()
          if not next:
            break
          if not node.contains(next):
            break
          node=next
        
        nodeset=step.evaluate(Ctx(node))
      
      elif not needContextPosition and axis=='preceding':
        node=iter()
        nodeset=step.evaluate(Ctx(node))
      
      else:
        node=iter()
        j=0
        nodeset=step.evaluate(Ctx(node),False,prevNodeset,j)
        while True:
          node=iter()
          if not node:
            break
          j+=1
          nodeset.merge(step.evaluate(Ctx(node),False,prevNodeset,j))
    
    return nodeset
  
  def step(self,op,step):
    step.op=op
    self.steps.append([op,step])
    self.quickAttr=False
    
    if len(self.steps)==1:
      if op=='/' and step.axis=='attribute':
        test=step.test
        if not getattr(test,'notOnlyElement',None) and test.name!='*':
          self.quickAttr=True
          self.attrName=test.name
  
  def show(self,indent=''):
    t=''
    t+=indent+'path: '+'\n'
    indent+=indent_space
    t+=indent+'filter:'+'\n'
    t+=self.filter.show(indent+indent_space)
    if 0<len(self.steps):
      t+=indent+'steps:'+'\n'
      indent+=indent_space
      for _step in self.steps:
        t+=indent+'operator: '+step[0]+'\n'
        t+=_step[1].show(indent) # _step=[op,step]
    return t
  
  ops=ExtDict({
    '//':1
  , '/': 1
  })
  
  @classmethod
  def parse(cls,lexer):
    ops=cls.ops
    if ops.get(lexer.peek()):
      op=lexer.next()
      token=lexer.peek()
      
      if op=='/' and lexer.empty() or (token!='.' and token!='..' and token!='@' and token!='*' and not re_has_ualpha.search(token)):
        return FilterExpr.root()
      
      path=PathExpr(FilterExpr.root()) # RootExpr
      
      if lexer.empty():
        throwError(u'missing next location step')
      expr=Step.parse(lexer)
      path.step(op,expr)
    
    else:
      expr=FilterExpr.parse(lexer)
      if not expr:
        expr=Step.parse(lexer)
        path=PathExpr(FilterExpr.context())
        path.step('/',expr)
      elif not ops.get(lexer.peek()):
        return expr
      else:
        path=PathExpr(expr)
    
    while True:
      if not ops.get(lexer.peek()):
        break
      op=lexer.next()
      if lexer.empty():
        throwError(u'missing next location step')
      path.step(op,Step.parse(lexer))
    
    return path
  
#} // end of PathExpr


#{ // FilterExpr
class FilterExpr(BaseExprHasPredicates):
  def __init__(self,primary):
    self.primary=primary
    self.predicates=[]
    self.datatype=primary.datatype
    self.needContextPosition=primary.needContextPosition
    self.needContextNode=primary.needContextNode
  
  def evaluate(self,ctx):
    nodeset=self.primary.evaluate(ctx)
    if not getattr(nodeset,'isNodeSet',None):
      if 0<len(self.predicates):
        throwError(u'Primary result must be nodeset type if filter have predicate expression')
        return nodeset
    
    return self.evaluatePredicates(nodeset)
  
  def predicate(self,predicate):
    self.predicates.append(predicate)
  
  def show(self,indent=''):
    t=''
    t+=indent+'filter: '+'\n'
    indent+=indent_space
    t+=self.primary.show(indent+indent_space)
    if 0<len(self.predicates):
      t+=indent+'predicates:'+'\n'
      indent+=indent_space
      for predicate in self.predicates:
        t+=predicate.show(indent)
    return t

  @classmethod
  def root(cls):
    return FunctionCall('root-node')
  
  @classmethod
  def context(cls):
    return FunctionCall('context-node')
  
  @classmethod
  def parse(cls,lexer):
    token=lexer.peek()
    ch=token[0:1]
    
    if ch=='$':
      expr=VariableReference.parse(lexer)
    elif ch=='(':
      lexer.next()
      expr=BinaryExpr.parse(lexer)
      if lexer.empty():
        throwError(u'unclosed "("')
      if lexer.next()!=')':
        lexer.back()
        throwError(u'bad token: %s' % (lexer.next()))
    elif ch=='"' or ch=="'":
      expr=Literal.parse(lexer)
    else:
      if not isNaN(token):
        expr=Number.parse(lexer)
      elif NodeType.types.get(token):
        return None
      elif re_has_ualpha.search(ch) and lexer.peek(1)=='(':
        expr=FunctionCall.parse(lexer)
      else:
        return None
    
    if lexer.peek()!='[':
      return expr
    
    filter=FilterExpr(expr)
    
    BaseExprHasPredicates.parsePredicates(lexer,filter)
    
    return filter

#} // end of FilterExpr


#{ // Step
class Step(BaseExprHasPredicates):
  def __init__(self,axis,test):
    self.axis=axis
    self.reverse=self.axises[axis][0]
    self.func=self.axises[axis][1]
    self.test=test
    self.predicates=[]
    self._quickAttr=self.axises[axis][2]
    self.quickAttr=False
    self.needContextPosition=False
  
  def evaluate(self,ctx,special=False,prevNodeset=None,prevIndex=None):
    node=ctx.node
    reverse=False

    if not special and getattr(self,'op',None)=='//':
      if not self.needContextPosition and self.axis=='child':
        if getattr(self,'quickAttr',None):
          attrValueExpr=getattr(self,'attrValueExpr',None)
          attrValue=attrValueExpr.string(ctx) if attrValueExpr else None
          nodeset=NodeUtil.getDescendantNodes(self.test,node,NodeSet(),self.attrName,attrValue,prevNodeset,prevIndex)
          nodeset=self.evaluatePredicates(nodeset,1)
        else:
          nodeset=NodeUtil.getDescendantNodes(self.test,node,NodeSet(),None,None,prevNodeset,prevIndex)
          nodeset=self.evaluatePredicates(nodeset)
      else:
        step=Step('descendant-or-self',NodeType('node'))
        nodes=step.evaluate(ctx,False,prevNodeset,prevIndex).list()
        nodeset=None
        step.op='/'
        for _node in nodes:
          if not nodeset:
            nodeset=self.evaluate(Ctx(_node),True,None,None)
          else:
            nodeset.merge(self.evaluate(Ctx(_node),True,None,None))
        nodeset=nodeset or NodeSet()
    else:
      if getattr(self,'needContextPosition',None):
        prevNodeset=None
        prevIndex=None
      if getattr(self,'quickAttr',None):
        attrValueExpr=getattr(self,'attrValueExpr',None)
        attrValue=attrValueExpr.string(ctx) if attrValueExpr else None
        nodeset=self.func(self.test,node,NodeSet(),self.attrName,attrValue,prevNodeset,prevIndex)
        nodeset=self.evaluatePredicates(nodeset,1)
      else:
        nodeset=self.func(self.test,node,NodeSet(),None,None,prevNodeset,prevIndex)
        nodeset=self.evaluatePredicates(nodeset)
      if prevNodeset:
        prevNodeset.doDel()
    
    return nodeset
  
  def predicate(self,predicate):
    self.predicates.append(predicate)
    datatype=getattr(predicate,'datatype',None)
    if getattr(predicate,'needContextPosition',None) or datatype=='number' or datatype=='void':
      self.needContextPosition=True
    
    if getattr(self,'_quickAttr',None) and len(self.predicates)==1 and getattr(predicate,'quickAttr',None):
      attrName=predicate.attrName
      self.attrName=attrName
      self.attrValueExpr=getattr(predicate,'attrValueExpr',None)
      self.quickAttr=True
  
  def show(self,indent=''):
    t=''
    t+=indent+'step: '+'\n'
    indent+=indent_space
    if self.axis:
      t+=indent+'axis: '+self.axis+'\n'
    t+=self.test.show(indent)
    if 0<len(self.predicates):
      t+=indent+'predicates:'+'\n'
      indent+=indent_space
      for predicate in self.predicates:
        t+=predicate.show(indent)
    return t
  
  # --- Local Functions
  def _ancestor(test,node,nodeset,attrName,attrValue,prevNodeset,prevIndex):
    while True:
      node=node.parentNode
      if not node:
        break
      if prevNodeset and node.nodeType==NodeTypeDOM.ELEMENT_NODE:
        prevNodeset.reserveDelByNode(node,prevIndex,True)
      if test.match(node):
        nodeset.unshift(node)
    return nodeset
  
  def _ancestorOrSelf(test,node,nodeset,attrName,attrValue,prevNodeset,prevIndex):
    while True:
      if prevNodeset and node.nodeType==NodeTypeDOM.ELEMENT_NODE:
        prevNodeset.reserveDelByNode(node,prevIndex,True)
      if test.match(node):
        nodeset.unshift(node)
      node=node.parentNode
      if not node:
        break
    return nodeset
  
  def _attribute(test,node,nodeset,attrName,attrValue,prevNodeset,prevIndex):
    attrs=node.attributes
    if attrs:
      if getattr(test,'notOnlyElement',None) and test.type==NodeTypeDOM.ANY_NODE or test.name=='*':
        for name in attrs.keys():
          #nodeset.push(AttributeWrapper(name,attrs[name],node))
          nodeset.push(AttributeWrapper.getAttributeWrapper(name,attrs[name],node))
      else:
        attr=attrs.get(test.name)
        if attr!=None:
          #nodeset.push(AttributeWrapper(test.name,attr,node))
          nodeset.push(AttributeWrapper.getAttributeWrapper(test.name,attr,node))
    return nodeset
  
  def _child(test,node,nodeset,attrName,attrValue,prevNodeset,prevIndex):
    return NodeUtil.getChildNodes(test,node,nodeset,attrName,attrValue,prevNodeset,prevIndex)
  
  def _descendant(test,node,nodeset,attrName,attrValue,prevNodeset,prevIndex):
    return NodeUtil.getDescendantNodes(test,node,nodeset,attrName,attrValue,prevNodeset,prevIndex)
  
  def _descendantOrSelf(test,node,nodeset,attrName,attrValue,prevNodeset,prevIndex):
    if NodeUtil.attrMatch(node,attrName,attrValue) and test.match(node):
      nodeset.push(node)
    return NodeUtil.getDescendantNodes(test,node,nodeset,attrName,attrValue,prevNodeset,prevIndex)
  
  def _following(test,node,nodeset,attrName,attrValue,prevNodeset,prevIndex):
    while True:
      child=node
      while True:
        child=child.nextSibling
        if not child:
          break
        if NodeUtil.attrMatch(child,attrName,attrValue) and test.match(child):
          nodeset.push(child)
        nodeset=NodeUtil.getDescendantNodes(test,child,nodeset,attrName,attrValue,None,None)
      node=node.parentNode
      if not node:
        break
    return nodeset
  
  def _followingSibling(test,node,nodeset,attrName,attrValue,prevNodeset,prevIndex):
    while True:
      node=node.nextSibling
      if not node:
        break
      if prevNodeset and node.nodeType==NodeTypeDOM.ELEMENT_NODE:
        prevNodeset.reserveDelByNode(node,prevIndex)
      if test.match(node):
        nodeset.push(node)
    return nodeset;
  
  def _namespace(test,node,nodeset,attrName,attrValue,prevNodeset,prevIndex):
    # not implemented
    return nodeset
  
  def _parent(test,node,nodeset,attrName,attrValue,prevNodeset,prevIndex):
    nodeType=node.nodeType
    if nodeType==NodeTypeDOM.DOCUMENT_NODE:
      return nodeset
    if nodeType==NodeTypeDOM.ATTRIBUTE_NODE:
      nodeset.push(node.ownerElement)
      return nodeset
    node=node.parentNode
    if test.match(node):
      nodeset.push(node)
    return nodeset
  
  def _preceding(test,node,nodeset,attrName,attrValue,prevNodeset,prevIndex):
    parents=[]
    while True:
      parents.insert(0,node)
      node=node.parentNode
      if not node:
        break
    
    for node in parents[1:]:
      siblings=[]
      while True:
        node=node.previousSibling
        if not node:
          break
        siblings.insert(0,node)
      
      for node in siblings:
        if NodeUtil.attrMatch(node,attrName,attrValue) and test.match(node):
          nodeset.push(node)
        nodeset=NodeUtil.getDescendantNodes(test,node,nodeset,attrName,attrValue,None,None)
    return nodeset
  
  def _precedingSibling(test,node,nodeset,attrName,attrValue,prevNodeset,prevIndex):
    while True:
      node=node.previousSibling
      if not node:
        break
      if prevNodeset and node.nodeType==NodeTypeDOM.ELEMENT_NODE:
        prevNodeset.reserveDelByNode(node,prevIndex,True)
      if test.match(node):
        nodeset.unshift(node)
    return nodeset
  
  def _self(test,node,nodeset,attrName,attrValue,prevNodeset,prevIndex):
    if test.match(node):
      nodeset.push(node)
    return nodeset
  
  axises=ExtDict({
    'ancestor'          :[True ,_ancestor        ,False]
  , 'ancestor-or-self'  :[True ,_ancestorOrSelf  ,False]
  , 'attribute'         :[False,_attribute       ,False]
  , 'child'             :[False,_child,True      ,False]
  , 'descendant'        :[False,_descendant      ,True ]
  , 'descendant-or-self':[False,_descendantOrSelf,True ]
  , 'following'         :[False,_following       ,True ]
  , 'following-sibling' :[False,_followingSibling,False]
  , 'namespace'         :[False,_namespace       ,False]
  , 'parent'            :[False,_parent          ,False]
  , 'preceding'         :[True ,_preceding       ,True ]
  , 'preceding-sibling' :[True ,_precedingSibling,False]
  , 'self'              :[False,_self            ,False]
  })
  
  @classmethod
  def _cself(cls):
    return cls('self',NodeType('node'))
  
  @classmethod
  def parent(cls):
    return cls('parent',NodeType('node'))
  
  @classmethod
  def parse(cls,lexer):
    (parent,_cself,axises)=(cls.parent,cls._cself,cls.axises)
    if lexer.peek()=='.':
      step=_cself()
      lexer.next()
    elif lexer.peek()=='..':
      step=parent()
      lexer.next()
    else:
      if lexer.peek()=='@':
        axis='attribute'
        lexer.next()
        if lexer.empty():
          throwError(u'missing attribute name')
      else:
        if lexer.peek(1)=='::':
          ch=lexer.peek()[0:1]
          if not re_has_ualpha.search(ch):
            throwError(u'bad token: %s' % (lexer.next()))
          
          axis=lexer.next()
          lexer.next()
          
          if not axises.get(axis):
            throwError(u'invalid axis: %s' % (axis))
          
          if lexer.empty():
            throwError(u'missing node name')
        else:
          axis='child'
      
      token=lexer.peek()
      ch=token[0:1]
      if not re_has_ualpha.search(ch):
        if token=='*':
          test=NameTest.parse(lexer)
        else:
          throwError(u'bad token: %s' % (lexer.next()))
      else:
        if lexer.peek(1)=='(':
          if not NodeType.types.get(token):
            throwError(u'invalid node type: %s' % (token))
          test=NodeType.parse(lexer)
        else:
          test=NameTest.parse(lexer)
      step=Step(axis,test)
    
    BaseExprHasPredicates.parsePredicates(lexer,step)
    
    return step

#} // end of Step


#{ // NodeType
class NodeType(BaseExpr):
  def __init__(self,name,literal=None):
    self.name=name
    self.literal=literal
    self.type=NodeType.typeNums.get(name,NodeType.typeNums.node)
    self.notOnlyElement=True
  
  def match(self,node):
    return self.type==NodeTypeDOM.ANY_NODE or self.type==node.nodeType
  
  def show(self,indent=''):
    t=''
    t+=indent+'nodetype: '+toString(self.type)+'\n'
    if self.literal:
      indent+=indent_space
      t+=self.literal.show(indent)
    return t
  
  types=ExtDict({
    'comment'               :1
  , 'text'                  :1
  , 'processing-instruction':1
  , 'node'                  :1
  })
  
  typeNums=ExtDict({
    'comment'               :NodeTypeDOM.COMMENT_NODE
  , 'text'                  :NodeTypeDOM.TEXT_NODE
  , 'processing-instruction':NodeTypeDOM.PROCESSING_INSTRUCTION_NODE
  , 'node'                  :NodeTypeDOM.ANY_NODE
  })
  
  @classmethod
  def parse(cls,lexer):
    type=lexer.next()
    lexer.next()
    if lexer.empty():
      throwError(u'bad nodetype')
    ch=lexer.peek()[0:1]
    literal=None
    if ch=='"' or ch=="'":
      literal=Literal.parse(lexer)
    if lexer.empty():
      throwError(u'bad nodetype')
    if lexer.next()!=')':
      lexer.back()
      throwError(u'bad token: %s' % (lexer.next()))
    
    return cls(type,literal)

#} // end of NodeType


#{ // NameTest
class NameTest(BaseExpr):
  def __init__(self,name):
    self.name=name.lower()
  
  def match(self,node):
    type=node.nodeType
    if type==NodeTypeDOM.ELEMENT_NODE or type==NodeTypeDOM.ATTRIBUTE_NODE:
      if self.name=='*' or self.name==node.nodeName:
        return True
    return False
  
  def show(self,indent=''):
    t=''
    t+=indent+'nametest: '+self.name+'\n'
    return t
  
  @classmethod
  def parse(cls,lexer):
    if lexer.peek()!= '*' and lexer.peek(1)==':' and lexer.peek(2)=='*':
      return cls(lexer.next()+lexer.next()+lexer.next())
    return cls(lexer.next())

#} // end of NameTest


#{ // VariableReference
class VariableReference(BaseExpr):
  def __init__(self,name):
    self.name=name[1:]
    self.datatype='void'
  
  def show(self,indent=''):
    t=''
    t+=indent+'variable: '+self.name+'\n'
    return t
  
  @classmethod
  def parse(cls,lexer):
      token=lexer.next()
      if len(token)<2:
        throwError(u'unnamed variable reference')
      return cls(token)

#} // end of VariableReference


#{ // Literal
class Literal(BaseExpr):
  def __init__(self,text):
    self.text=text[1:-1]
    self.datatype='string'
  
  def evaluate(self,ctx):
    result=self.text
    return result
  
  def show(self,indent=''):
    t=''
    t+=indent+'literal: '+self.text+'\n'
    return t
  
  @classmethod
  def parse(cls,lexer):
    token=lexer.next()
    if len(token)<2:
      throwError(u'unclosed literal string')
    return cls(token)

#} // end of Literal


#{ // Number
class Number(BaseExpr):
  def __init__(self,digit):
    self.digit=toNumber(digit)
    self.datatype='number'
  
  def evaluate(self,ctx):
    result=self.digit
    return result
  
  def show(self,indent=''):
    t=''
    t+=indent+'number: '+toString(self.digit)+'\n'
    return t
  
  @classmethod
  def parse(cls,lexer):
    return cls(lexer.next())

#} // end of Number


#{ // FunctionCall
class FunctionCall(BaseExpr):
  def __init__(self,name):
    info=self.funcs.get(name)
    if not info:
      throwError(u'%s is not a function' % (name))
    self.name=name
    self.func=info[0]
    self.args=[]
    self.datatype=info[1]
    self.needContextPosition=True if info[2] else False
    self.needContextNodeInfo=info[3]
    self.needContextNode=self.needContextNodeInfo[0] if 0<len(self.needContextNodeInfo) else False
  
  def evaluate(self,ctx):
    result=self.func(ctx,*self.args)
    return result
  
  def arg(self,arg):
    self.args.append(arg)
    if getattr(arg,'needContextPosition',None):
      self.needContextPosition=True
    args=self.args
    if getattr(arg,'needContextNode',None):
      #args.needContextNode=True
      self.needContextNode=True
    #self.needContextNode=args.needContextNode or self.needContextNodeInfo[len(args)]
    if not getattr(self,'needContextNode',None) and len(args)<len(self.needContextNodeInfo):
      self.needContextNode=self.needContextNodeInfo[len(args)]
  
  def show(self,indent=''):
    t=''
    t+=indent+'function: '+self.name+'\n'
    indent+=indent_space
    if 0<len(self.args):
      t+=indent+'arguments: '+'\n'
      indent+=indent_space
      for arg in self.args:
        t+=arg.show(indent)
    return t
  
  # --- Local Functions
  def _contextNode(self,*arguments):
    if len(arguments)!=0:
      throwError(u'Function context-node expects ()')
    nodeset=NodeSet()
    nodeset.push(self.node)
    return nodeset
  
  def _rootNode(self,*arguments):
    if len(arguments)!=0:
      throwError(u'Function root-node expects ()')
    nodeset=NodeSet()
    ctxn=self.node
    if ctxn.nodeType==NodeTypeDOM.DOCUMENT_NODE:
      nodeset.push(ctxn)
    else:
      nodeset.push(ctxn.ownerDocument)
    return nodeset
  
  def _last(self,*arguments):
    if len(arguments)!=0:
      throwError(u'Function last expects ()')
    return self.last
  
  def _position(self,*arguments):
    if len(arguments)!=0:
      throwError(u'Function position expects ()')
    return self.position
  
  def _count(self,*arguments):
    if len(arguments)!=1:
      throwError(u'Function count expects (nodeset)')
    nodeset=arguments[0].evaluate(self)
    if not nodeset.isNodeSet:
      throwError(u'Function count expects (nodeset)')
    return nodeset.length
  
  def _id(self,*arguments):
    if len(arguments)!=1:
      throwError(u'Function id expects (object)')
    
    s=arguments[0]
    ctxn=self.node
    if ctxn.nodeType==NodeTypeDOM.DOCUMENT_NODE:
      doc=ctxn
    else:
      doc=ctxn.ownerDocument
    
    s=s.string(self)
    ids=re_seqspace.split(s)
    nodeset=NodeSet()
    for id in ids:
      for elm in doc.findAll(id=id):
        nodeset.push(elm)
    nodeset.isSorted=False
    return nodeset
  
  def _localName(self,*arguments):
    alen=len(arguments)
    if alen<0 or 1<alen:
      throwError(u'Function local-name expects (nodeset?)')
    if alen==0:
      node=self.node
    else:
      nodeset=arguments[0]
      nodeset=nodeset.evaluate(self)
      if getattr(nodeset,'isNodeSet',None):
        node=nodeset.first()
    return ''+node.nodeName
    
  
  def _name(self,*arguments):
    # not implemented
    return FunctionCall.funcs['local-name'][0](self,*arguments)
  
  def _namespaceUri(self,*arguments):
    # not implemented
    return ''
  
  def _string(self,*arguments):
    alen=len(arguments)
    if alen==0:
      s=NodeUtil.to('string',self.node)
    elif alen==1:
      s=arguments[0]
      s=s.string(self)
    else:
      throwError(u'Function string expects (object?)')
    return s
  
  def _concat(self,*arguments):
    if len(arguments)<2:
      throwError('Function concat expects (string, string[, ...])')
    t=''
    for argument in arguments:
      t+=argument.string(self)
    return t
  
  def _startsWith(self,*arguments):
    if len(arguments)!=2:
      throwError('Function starts-with expects (string, string)')
    (s1,s2)=(arguments[0],arguments[1])
    s1=s1.string(self)
    s2=s2.string(self)
    #if s2 in s1 and s1.index(s2)==0:
    #  return True
    #else:
    #  return False
    if s1.find(s2)==0:
      return True
    else:
      return False
  
  def _contains(self,*arguments):
    if len(arguments)!=2:
      throwError('Function contains expects (string, string)')
    (s1,s2)=(arguments[0],arguments[1])
    s1=s1.string(self)
    s2=s2.string(self)
    #if s2 in s1:
    #  return True
    #else:
    #  return False
    n=s1.find(s2)
    if n<0:
      return False
    else:
      return True
  
  def _substring(self,*arguments):
    alen=len(arguments)
    if alen<2 or 3<alen:
      throwError(u'Function substring expects (string, string)')
    (s,n1)=(arguments[0],arguments[1])
    s=s.string(self)
    n1=n1.number(self)
    if alen==2:
      n2=len(s)-n1+1
    elif alen==3:
      n2=arguments[2]
      n2=n2.number(self)
    
    if n1=='NaN' or n2=='NaN' or n1=='-Infinity' or n2=='-Infinity' or n1=='Infinity':
      return u''
    
    # n1,n2:origin=1  a1,a2:origin=0
    n1=int(round(n1))
    a1=n1-1
    if a1<0: a1=0
    if n2=='Infinity':
      return s[a1:]
    else:
      n2=int(round(n2))
      a2=n1+n2-1
      return s[a1:a2]
  
  def _substringBefore(self,*arguments):
    if len(arguments)!=2:
      throwError('Function substring-before expects (string, string)')
    (s1,s2)=(arguments[0],arguments[1])
    s1=s1.string(self)
    s2=s2.string(self)
    #if s2 in s1:
    #  n=s1.index(s2)
    #else:
    #  return ''
    n=s1.find(s2)
    if n<0:
      return ''
    return s1[:n]
  
  def _substringAfter(self,*arguments):
    if len(arguments)!=2:
      throwError('Function substring-after expects (string, string)')
    (s1,s2)=(arguments[0],arguments[1])
    s1=s1.string(self)
    s2=s2.string(self)
    #if s2 in s1:
    #  n=s1.index(s2)
    #else:
    #  return ''
    n=s1.find(s2)
    if n<0:
      return ''
    return s1[n+len(s2):]
  
  def _substringLength(self,*arguments):
    alen=len(arguments)
    if alen==0:
      s=NodeUtil.to('string',self.node)
    elif alen==1:
      s=arguments[0]
      s=s.string(self)
    else:
      throwError(u'Function string-length expects (string?)')
    return len(s)
  
  def _normalizeSpace(self,*arguments):
    alen=len(arguments)
    if alen==0:
      s=NodeUtil.to('string',self.node)
    elif alen==1:
      s=arguments[0]
      s=s.string(self)
    else:
      throwError(u'Function normalize-space expects (string?)')
    return re_lastspace.sub('',re_firstspace.sub('',re_seqspace.sub(' ',s)))
  
  def _translate(self,*arguments):
    if len(arguments)!=3:
      throwError('Function translate expects (string, string, string)')
    (s1,s2,s3)=(arguments[0],arguments[1],arguments[2])
    s1=s1.string(self)
    s2=s2.string(self)
    s3=s3.string(self)
    _map={}
    for i in range(0,len(s2)):
      ch=s2[i]
      if not _map.get(ch):
        _map[ch]=s3[i] if i<len(s3) else ''
    t=''
    for ch in s1:
      replace=_map.get(ch)
      t+=replace if replace!=None else ch
    return t
  
  def _boolean(self,*arguments):
    if len(arguments)!=1:
      throwError(u'Function not expects (object)')
    b=arguments[0]
    b=b.bool(self)
    return b
  
  def _not(self,*arguments):
    if len(arguments)!=1:
      throwError(u'Function not expects (object)')
    b=arguments[0]
    b=b.bool(self)
    return not b
  
  def _true(self,*arguments):
    if len(arguments)!=0:
      throwError(u'Function false expects ()')
    return True
  
  def _false(self,*arguments):
    if len(arguments)!=0:
      throwError(u'Function false expects ()')
    return False
  
  def _lang(self,*arguments):
    # not implemented
    return False
  
  def _number(self,*arguments):
    alen=len(arguments)
    if alen==0:
      n=NodeUtil.to('number',self.node)
    elif alen==1:
      n=arguments[0]
      n=n.number(self)
    else:
      throwError(u'Function number expects (object?)')
    if isinstance(n,int):
      return n
    elif isinstance(n,float):
      n1=int(n)
      return n1 if n1==n else n
    else:
      return 'NaN'
  
  def _sum(self,*arguments):
    if len(arguments)!=1:
      throwError(u'Function sum expects (nodeset)')
    nodeset=arguments[0]
    nodeset=nodeset.evaluate(self)
    if not getattr(nodeset,'isNodeSet',None):
      throwError(u'Function sum expects (nodeset)')
    nodes=nodeset.list()
    n=0
    for node in nodes:
      n+=NodeUtil.to('number',node)
    return n
  
  def _floor(self,*arguments):
    if len(arguments)!=1:
      throwError(u'Function floor expects (number)')
    n=arguments[0]
    n=n.number(self)
    return int(math.floor(n))
  
  def _ceiling(self,*arguments):
    if len(arguments)!=1:
      throwError(u'Function ceiling expects (number)')
    n=arguments[0]
    n=n.number(self)
    return int(math.ceil(n))
  
  def _round(self,*arguments):
    if len(arguments)!=1:
      throwError(u'Function round expects (number)')
    n=arguments[0]
    n=n.number(self)
    return int(round(n))
  
  funcs=ExtDict({
    'context-node'    :[_contextNode    ,'nodeset',False,[True]]
  , 'root-node'       :[_rootNode       ,'nodeset',False,[]]
  , 'last'            :[_last           ,'number' ,True ,[]]
  , 'position'        :[_position       ,'number' ,True ,[]]
  , 'count'           :[_count          ,'number' ,False,[]]
  , 'id'              :[_id             ,'nodeset',False,[]]
  , 'local-name'      :[_localName      ,'string' ,False,[True ,False]]
  , 'name'            :[_name           ,'string' ,False,[True ,False]]
  , 'namespace-uri'   :[_namespaceUri   ,'string' ,False,[True ,False]]
  , 'string'          :[_string         ,'string' ,False,[True ,False]]
  , 'concat'          :[_concat         ,'string' ,False,[]]
  , 'starts-with'     :[_startsWith     ,'boolean',False,[]]
  , 'contains'        :[_contains       ,'boolean',False,[]]
  , 'substring'       :[_substring      ,'string' ,False,[]]
  , 'substring-before':[_substringBefore,'string' ,False,[]]
  , 'substring-after' :[_substringAfter ,'string' ,False,[]]
  , 'string-length'   :[_substringLength,'number' ,False,[True ,False]]
  , 'normalize-space' :[_normalizeSpace ,'string' ,False,[True ,False]]
  , 'translate'       :[_translate      ,'string' ,False,[]]
  , 'boolean'         :[_boolean        ,'boolean',False,[]]
  , 'not'             :[_not            ,'boolean',False,[]]
  , 'true'            :[_true           ,'boolean',False,[]]
  , 'false'           :[_false          ,'boolean',False,[]]
  , 'lang'            :[_lang           ,'boolean',False,[]]
  , 'number'          :[_number         ,'number' ,False,[True ,False]]
  , 'sum'             :[_sum            ,'number' ,False,[]]
  , 'floor'           :[_floor          ,'number' ,False,[]]
  , 'ceiling'         :[_ceiling        ,'number' ,False,[]]
  , 'round'           :[_round          ,'number' ,False,[]]
  })
  
  @classmethod
  def parse(cls,lexer):
    func=cls(lexer.next())
    lexer.next()
    while lexer.peek()!=')':
      if lexer.empty():
        throwError(u'missing function argument list')
      expr=BinaryExpr.parse(lexer)
      func.arg(expr)
      if lexer.peek()!=',':
        break
      lexer.next()
    if lexer.empty():
      throwError(u'unclosed function argument list')
    if lexer.next()!=')':
      lexer.back()
      throwError(u'bad token: %s' % (lexer.next()))
    return func

#} // end of FunctionCall


#{ // NodeSet
class NodeSet(object):
  def __init__(self):
    self.length=0
    self.nodes=[]
    self.seen={}
    self.idIndexMap=None
    self.reserveDels=[]
    self.isNodeSet=True
    self.isSorted=True
    self.sortOff=False
    self.only=None
  
  def merge(self,nodeset):
    self.isSorted=False
    if getattr(nodeset,'only',None):
      return self.push(nodeset.only)
    
    if getattr(self,'only',None):
      only=self.only
      self.only=None
      self.push(only)
      self.length-=1
    
    map(self._add,nodeset.nodes)
  
  def sort(self):
    if getattr(self,'only',None):
      return
    if getattr(self,'sortOff',None):
      return
    if getattr(self,'isSorted',None):
      return
    
    self.isSorted=True
    self.idIndexMap=None
    nodes=self.nodes
    
    def _comp(a,b):
      if a.nodeType==NodeTypeDOM.ATTRIBUTE_NODE: a=a.parentNode
      if b.nodeType==NodeTypeDOM.ATTRIBUTE_NODE: b=b.parentNode
      if a==b:
        return 0
      (node1,node2)=(a,b)
      (ancestor1,ancestor2)=(a,b)
      (deep1,deep2)=(0,0)
      
      while True:
        ancestor1=ancestor1.parentNode
        if not ancestor1:
          break
        deep1+=1
      
      while True:
        ancestor2=ancestor2.parentNode
        if not ancestor2:
          break
        deep2+=1
      
      if deep1>deep2:
        while deep1!=deep2:
          deep1-=1
          node1=node1.parentNode
          if node1==node2:
            return 1
      elif deep2>deep1:
        while deep2!=deep1:
          deep2-=1
          node2=node2.parentNode
          if node1==node2:
            return -1
      
      while True:
        ancestor1=node1.parentNode
        ancestor2=node2.parentNode
        if ancestor1==ancestor2:
          break
        node1=ancestor1
        node2=ancestor2
      
      while True:
        node1=node1.nextSibling
        if not node1:
          break
        if node1==node2:
          return -1
      
      return 1
    
    def index_comp(a,b):
      if a.nodeType==NodeTypeDOM.ATTRIBUTE_NODE: a=a.parentNode
      if b.nodeType==NodeTypeDOM.ATTRIBUTE_NODE: b=b.parentNode
      return cmp(a._sortindex,b._sortindex)
    
    if USE_NODE_INDEX:
      nodes.sort(index_comp)
    else:
      nodes.sort(_comp)
  
  def reserveDelByNodeID(self,id,offset,reverse):
    _map=self.createIdIndexMap()
    index=_map.get(id)
    if index:
      if reverse and index<(self.length-offset-1) or not reverse and offset<index:
        self.reserveDels.append(index)
  
  def reserveDelByNode(self,node,offset,reverse=False):
    self.reserveDelByNodeID(self.NodeID.get(node),offset,reverse)
  
  def doDel(self):
    if len(self.reserveDels)<=0:
      return
    map(self._del,sorted(self.reserveDels,lambda x,y:cmp(y,x)))
    self.reserveDels=[]
    self.idIndexMap=None
  
  def createIdIndexMap(self):
    if getattr(self,'idIndexMap',None):
      return self.idIndexMap
    else:
      _map=self.idIndexMap={}
      nodes=self.nodes
      for i in range(0,len(nodes)):
        node=nodes[i]
        id=self.NodeID.get(node)
        _map[id]=i
      return _map
  
  def _del(self,index):
    self.length-=1
    if getattr(self,'only',None):
      self.only=None
    else:
      node=self.nodes[index]
      if getattr(self,'_first',None)==node:
        self._first=None
        self._firstSourceIndex=None
        self._firstSubIndex=None
      del(self.seen[self.NodeID.get(node)])
      del(self.nodes[index])
  
  def delDescendant(self,elm,offset):
    if getattr(self,'only',None):
      return
    nodeType=elm.nodeType
    if nodeType!=NodeTypeDOM.ELEMENT_NODE and nodeType!=NodeTypeDOM.DOCUMENT_NODE:
      return
    
    nodes=self.nodes
    i=offset+1
    while i<len(nodes):
      if elm.contains(nodes[i]):
        self._del(i)
        i-=1
      i+=1
  
  def _add(self,node,reverse=False):
    seen=self.seen
    id=self.NodeID.get(node)
    if seen.get(id):
      return
    seen[id]=True
    self.length+=1
    if reverse:
      self.nodes.insert(0,node)
    else:
      self.nodes.append(node)
  
  def unshift(self,node):
    if self.length<=0:
      self.length+=1
      self.only=node
      return
    if getattr(self,'only',None):
      only=self.only
      self.only=None
      self.unshift(only)
      self.length-=1
    return self._add(node,True)
  
  def push(self,node):
    if self.length<=0:
      self.length+=1
      self.only=node
      return
    if getattr(self,'only',None):
      only=self.only
      self.only=None
      self.push(only)
      self.length-=1
    return self._add(node)
  
  def first(self):
    if getattr(self,'only',None):
      return self.only
    if 0<len(self.nodes):
      self.sort()
      return self.nodes[0]
    else:
      return None
  
  def list(self):
    if getattr(self,'only',None):
      return [self.only]
    self.sort()
    return self.nodes
  
  def string(self):
    node=self.only or self.first()
    return NodeUtil.to('string',node) if node else ''
  
  def bool(self):
    return toBoolean(self.length or self.only)
  
  def number(self):
    return toNumber(self.string())
  
  def iterator(self,reverse=False):
    self.sort()
    _info=ExtDict({
      'nodeset':self
    , 'count':0
    })
    if not reverse:
      calcIndex=(lambda x,y:x)
    else:
      calcIndex=(lambda x,y:y.length-x-1)
    def iter():
      nodeset=_info.nodeset
      index=calcIndex(_info.count,nodeset)
      _info['count']+=1
      if getattr(nodeset,'only',None) and index==0:
        return nodeset.only
      return nodeset.nodes[index] if 0<=index and index<len(nodeset.nodes) else None
    return iter

  class nodeID(object):
    def __init__(self):
      self.uuid=1
    
    def get(self,node):
      id=getattr(node,'__bsxpath_id__',None)
      if id:
        return id
      id=node.__bsxpath_id__=self.uuid
      self.uuid+=1
      return id
  
  NodeID=nodeID()
  
#} // end of NodeSet


#{ // XPathEvaluator
class XPathResult(object):
  ANY_TYPE                    =0
  NUMBER_TYPE                 =1
  STRING_TYPE                 =2
  BOOLEAN_TYPE                =3
  UNORDERED_NODE_ITERATOR_TYPE=4
  ORDERED_NODE_ITERATOR_TYPE  =5
  UNORDERED_NODE_SNAPSHOT_TYPE=6
  ORDERED_NODE_SNAPSHOT_TYPE  =7
  ANY_UNORDERED_NODE_TYPE     =8
  FIRST_ORDERED_NODE_TYPE     =9
  
  def __init__(self,value,type):
    if type==XPathResult.ANY_TYPE:
      tov=typeof(value)
      if tov=='object' : type=self.UNORDERED_NODE_ITERATOR_TYPE
      if tov=='boolean': type=self.BOOLEAN_TYPE
      if tov=='string' : type=self.STRING_TYPE
      if tov=='number' : type=self.NUMBER_TYPE
    
    if type<self.NUMBER_TYPE or self.FIRST_ORDERED_NODE_TYPE<type:
      throwError(u'unknown type: %d' %(type))
    self.resultType=type
    
    if type==self.NUMBER_TYPE:
      self.numberValue=value.number() if getattr(value,'isNodeSet',None) else toNumber(value)
    elif type==self.STRING_TYPE:
      self.stringValue=value.string() if getattr(value,'isNodeSet',None) else toString(value)
    elif type==self.BOOLEAN_TYPE:
      self.booleanValue=value.bool() if getattr(value,'isNodeSet',None) else toBoolean(value)
    elif type==self.ANY_UNORDERED_NODE_TYPE or type==self.FIRST_ORDERED_NODE_TYPE:
      self.singleNodeValue=value.first()
    else:
      self.nodes=value.list()
      self.snapshotLength=value.length
      self.index=0
      self.invalidIteratorState=False
  
  def iterateNext(self):
    node=self.nodes[self.index]
    self.index+=1
    return node
  
  def snapshotItem(self,i):
    return self.nodes[i]
  

class XPathExpression(object):
  def __init__(self,expr,resolver):
    if len(expr)<=0:
      throwError(u'no expression')
    lexer=self.lexer=Lexer(expr)
    
    if lexer.empty():
      throwError(u'no expression')
    self.expr=BinaryExpr.parse(lexer)
    if not lexer.empty():
      throwError(u'bad token: %s' % (lexer.next()))
  
  def evaluate(self,node,type):
    return XPathResult(self.expr.evaluate(Ctx(node)),type)


class BSXPathEvaluator(BeautifulSoup):
  def __init__(self, *args, **kwargs):
    BeautifulSoup.__init__(self, *args, **kwargs)
    self._string=u'[object HTMLDocument]'
    self._fix_table()
    
    self._init_index()
    
  SELF_CLOSING_TAGS=buildTagMap(None,['br','hr','input','img','meta','spacer','frame','base'])
  # exclude 'link' for XML
  
  def _init_index(self):
    idx=self._sortindex=1
    self._cachemap=None
    
    for node in NodeUtilBS.it_deepNodes(self):
      idx=node._sortindex=idx+1
    for node in self.findAll():
      node.attrMap=dict(node.attrs)
  
  def _fix_table(self):
    tables=self.findAll('table')
    for table in tables:
      parent=table.parent
      contents=getattr(table,'contents',[])
      if len(contents)<=0: continue
      (tbody,tr)=(None,None)
      node=table.contents[0]
      while node:
        _next=node.nextSibling
        name=getattr(node,'name',None)
        if name in ('thead','tbody','tfoot',):
          (tbody,tr)=(None,None)
        elif name in ('tr',):
          tr=None
          if not tbody:
            tbody=Tag(self,'tbody')
            table.insert(table.contents.index(node),tbody)
          tbody.append(node)
        elif name in ('th','td',):
          if not tbody:
            tbody=Tag(self,'tbody')
            table.insert(table.contents.index(node),tbody)
          if not tr:
            tr=Tag(self,'tr')
            tbody.append(tr)
          tr.append(node)
        else:
          parent.insert(parent.contents.index(table),node)
        node=_next
  
  def __str__(self,encoding=DEFAULT_OUTPUT_ENCODING):
    return self._string
  
  def __unicode__(self):
    return self._string
  
  def decode(self):
    return self._string
  
  def createExpression(self,expr,resolver):
    return XPathExpression(expr,resolver)
  
  def createNSResolver(self,nodeResolver):
    # not implemented
    pass
  
  def evaluate(self,expr,context,resolver,type,result):
    if not context:
      context=self
    if isinstance(context,list):
      context=context[0]
    return self.createExpression(expr,resolver).evaluate(context,type)
  
  def getItemList(self,expr,context=None):
    elms=[]
    result=self.evaluate(expr,context,None,XPathResult.ORDERED_NODE_SNAPSHOT_TYPE,None)
    for i in range(0,result.snapshotLength):
      elms.append(result.snapshotItem(i))
    return elms
  
  def getFirstItem(self,expr,context=None):
    elm=self.evaluate(expr,context,None,XPathResult.FIRST_ORDERED_NODE_TYPE,None).singleNodeValue
    return elm
  
  def applyXPath(self,context,expr):
    start_t=datetime.datetime.now()
    expression=self.createExpression(expr,None)
    result=expression.evaluate(context,XPathResult.ANY_TYPE)
    time=datetime.datetime.now()-start_t
    
    resultType=result.resultType
    if XPathResult.BOOLEAN_TYPE<resultType:
      result=expression.evaluate(context,XPathResult.ORDERED_NODE_SNAPSHOT_TYPE)
      array=[]
      for i in range(0,result.snapshotLength):
        array.append(result.snapshotItem(i))
      resultItems=array
    else:
      if resultType==XPathResult.NUMBER_TYPE:
        resultItems=result.numberValue
      elif resultType==XPathResult.STRING_TYPE:
        resultItems=result.stringValue
      elif resultType==XPathResult.BOOLEAN_TYPE:
        resultItems=result.booleanValue
      else:
        resultItems=None
    
    return (resultItems,time,resultType)
  

#} // end of XPathEvaluator



if __name__ == '__main__':
  import sys
  import optparse
  import pdb
  
  options=None
  
  def prn(obj):
    def prn_sub(obj,indent):
      indent+=u'  '
      if isinstance(obj,list):
        for i in range(0,len(obj)):
          print u'[%d]' % (i)
          prn_sub(obj[i],indent)
      elif isinstance(obj,dict):
        for mem in obj:
          print u'[%s]' % (mem)
          prn_sub(obj[mem],indent)
      elif getattr(obj,'nodeType',None) or isinstance(obj,basestring):
        str=indent+re.sub(r'([\r?\n])',r'\1'+indent,unicode(obj))
        print str
      else:
        print obj
    prn_sub(obj,u'')
  
  def test():
    global options
    
    if options.expr:
      if options.html:
        document=BSXPathEvaluator(options.html)
      elif options.file:
        fp=open(options.file)
        document=BSXPathEvaluator(fp.read())
        fp.close()
      else:
        document=BSXPathEvaluator(sys.stdin.read())
      
      (result,time,resultType)=document.applyXPath(document,options.expr)
      
      prn(result)
      
    else:
      optparser.print_help()
  
  optparser=optparse.OptionParser()
  optparser.add_option(
    '-e','--expr'
  , action='store'
  , metavar='<expression>'
  , help=u'expression: XPATH expression'
  , dest='expr'
  )
  optparser.add_option(
    '-t','--html'
  , action='store'
  , metavar='<text>'
  , help=u'text: HTML text'
  , dest='html'
  )
  optparser.add_option(
    '-f','--file'
  , action='store'
  , metavar='<filename>'
  , help=u'filename: HTML filename'
  , dest='file'
  )
  optparser.add_option(
    '-d','--debug'
  , action='store_true'
  , help=u'use pdb'
  , dest='debug'
  )
  (options,args)=optparser.parse_args()
  
  if options.debug:
    pdb.run('test()') 
  else:
    test()
  
#[History]
#
#  0.01e: 2009-04-12
#    - exclude 'link' tag from SELF_CLOSING_TAGS (for XML)
#    - add __str__() and __unicode__() to AttributeWrapper class
#  
#  0.01d: 2009-03-28
#    - performance improvement: node searching(make attrMap in advance)
#  
#  0.01c: 2009-03-28
#    - performance improvement: node sorting(indexing) and node search(caching)
# 
#  0.01b: 2009-03-27
#    - fixed 'singleNodeValue' bug
#      result = document.evaluate('//title[1]',document,None,XPathResult.FIRST_ORDERED_NODE_TYPE,None).singleNodeValue
#      # returnd 'None', even though first-value exists
# 
#  0.01a: 2009-03-27
#    - fixed string() bug
#      BSXPath.py -e "boolean(//p[contains(string(),\"br\")])" -t "<html><head></head><body><p>text before<br />text after</p></body></html>"
#      # returned 'True', even though 'False' is right
#    - cope with <table> problems on malformed HTML
#      may convert '<table><th></th><td></td></table>' to '<table><tbody><tr><th></th><td></td></tr></tbody></table>' automatically
# 
#  0.01 : 2009-03-25
#    first release
#
#■ End of BSXPath.py 
