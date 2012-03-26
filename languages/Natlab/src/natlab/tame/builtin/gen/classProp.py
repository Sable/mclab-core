# DEPRECATED - THERE IS A PARSER FOR THE CLASS LANGUAGE IN JAVA NOW
# TODO -delete

# processing Class tag - class propagation language
import processTags
import sys


# definition of the class propagation language - in a dictionary
# helper method - converts numbers to a MatlabClassVar
def convertNum(a):          return CPNum(a) if isinstance(a, (long, int)) else a;
# 1) python classes
# general matlab class used by the class tag (CP = ClassProp..Info) - defines the operators
class CP():
  def __or__  (self, other): return CPUnion(convertNum(self),convertNum(other));
  def __ror__ (self, other): return CPUnion(convertNum(self),convertNum(other));
  def __and__ (self, other): return CPChain(convertNum(self),convertNum(other));
  def __rand__(self, other): return CPChain(convertNum(other),convertNum(self));
  def __gt__  (self, other): return CPMap(convertNum(self),convertNum(other));
  def __lt__  (self, other): return CPMap(convertNum(other),convertNum(self));
  def __repr__(self):        return str(self);
# <class1> - represents Matlab builtin class
class CPBuiltin(CP):
  def __init__(self,name):   self.name = name;
  def __str__ (self):        return self.name;
  def toJava(self):          return 'new CPBuiltin("'+self.name+'")';
# class1 | clas2 - mulitple possibilities for one type
class CPUnion(CP):
  def __init__(self,a,b):    self.class1 = convertNum(a); self.class2 = convertNum(b);
  def __str__ (self):        return '('+str(self.class1)+'|'+str(self.class2)+')';
  def toJava  (self):        return 'new CPUnion('+self.class1.toJava()+','+self.class2.toJava()+')';
# class1 & class2 - sequences of types
class CPChain(CP):
  def __init__(self,a,b):    self.class1 = convertNum(a); self.class2 = convertNum(b);
  def __str__ (self):        return '('+str(self.class1)+')&('+str(self.class2)+')';   
  def toJava  (self):        return 'new CPChain('+self.class1.toJava()+','+self.class2.toJava()+')';
# class1 > class2 - matches lhs, emits rhs
class CPMap(CP):
  def __init__(self,a,b):    self.args = convertNum(a); self.res = convertNum(b);
  def __str__ (self):        return str(self.args)+'>'+str(self.res);   
  def toJava  (self):        return 'new CPMap('+self.args.toJava()+','+self.res.toJava()+')';
# <n> - a specific other argument, defined by a number - negative is counted from back (i.e. -1 is last)
class CPNum(CP):
  def __init__(self,num):    self.num = num;
  def __str__ (self):        return str(self.num);
  def toJava  (self):        return 'new CPNum('+str(self.num)+')';
# coerce(CP denoting replacement expr for every argument, CP affeced expr)
# example: coerce((char|logical)>double, (numerical&numerical)>double )
# TODO: this should be a McFunction
class CPCoerce(CP):
  def __init__(self,replaceExpr,expr): self.replaceExpr=replaceExpr; self.expr=expr;
  def __str__ (self):         return 'coerce('+str(self.replaceExpr)+','+str(self.expr)+')'
  def toJava  (self):         return 'new CPCoerce('+self.replaceExpr.toJava()+','+self.expr.toJava()+')'
# unparametric expressions of the language - the string and java representation are given by the constructor
class CPNonParametric(CP):
  def __init__(self,str,java): self.str = str; self.java = java;
  def __str__(self):          return self.str;
  def toJava (self):          return self.java;
# function of the form name(<expr>,<expr>,...) - the expresions, and string, java are given by the constructor
class CPFunction(CP):
  def __init__(self,str,java,*exprs): self.exprs = exprs; self.java = java; self.str = str;
  def __str__(self):          return self.str+"("+','.join([str(e) for e in self.exprs])+")"
  def toJava (self):          return self.java+"("+','.join([e.toJava() for e in self.exprs])+")"



# 2) set up keywords of the language in a dictionary
# basic types:
lang = dict(double=CPBuiltin('double'),single=CPBuiltin('single'),char=CPBuiltin('char'),logical=CPBuiltin('logical'),
            uint8=CPBuiltin('uint8'),uint16=CPBuiltin('uint16'),uint32=CPBuiltin('uint32'),uint64=CPBuiltin('uint64'),
            int8=CPBuiltin('int8'),int16=CPBuiltin('int16'),int32=CPBuiltin('int32'),int64=CPBuiltin('int64'),
            function_handle=CPBuiltin('function_handle'))
# union types
lang.update(dict(float=lang['single']|lang['double'], uint=(lang['uint8']|lang['uint16']|lang['uint32']|lang['uint64']), 
                 sint=(lang['int8']|lang['int16']|lang['int32']|lang['int64'])));  
lang['int']    = (lang['uint']|lang['sint']);
lang['numeric']= (lang['float']|lang['int']);
lang['matrix'] = (lang['numeric']|lang['char']|lang['logical']);
# non-parametric bits
lang['none'] =   CPNonParametric('none',  'new CPNone()');
lang['end'] =    CPNonParametric('end',   'new CPEnd()');
lang['begin'] =  CPNonParametric('begin', 'new CPBegin()');
lang['any'] =    CPNonParametric('any',   'new CPAny()');
lang['parent'] = CPNonParametric('parent','parentClassPropInfo'); # java code is the local variable
lang['error']  = CPNonParametric('error', 'new CPError()');
lang['natlab'] = CPNonParametric('class', 'getClassPropagationInfo()');
lang['matlab'] = CPNonParametric('class', 'getMatlabClassPropagationInfo()');
lang['scalar'] = CPNonParametric('scalar','new CPScalar()');
# other bits of the language
lang['coerce'] = lambda replaceExpr, expr: CPCoerce(replaceExpr,expr)
lang['opt'] = lambda expr: (expr|lang['none']) #note: op(x), being (x|none), will cause an error on the rhs
lang['not'] = lambda typesExpr: CPFunction('not','new CPNot',typesExpr)
lang['arg'] = lambda num : CPNum(num)
# todo - so far opt only allows up to 10 repititions
opt = lang['opt']
lang['star']= lambda expr: opt(expr)&opt(expr)&opt(expr)&opt(expr)&opt(expr)&opt(expr)&opt(expr)&opt(expr)&opt(expr)&opt(expr)
# functions
lang['typeString'] = lambda typesExpr: CPFunction('typeString','new CPTypeString',typesExpr)



# TODO - other possible language features
#variables?
#mult(x,[max],[min]) #tries to match as many as possible max,min may be 0
#matlab - allows matching the current matlab tree for a MatlabClass

# helper method - turns a sequence of CP objects into CPUnion objects
def tupleToCP(seq):
  if len(seq) == 1: return seq[0]
  return CPUnion(seq[0],tupleToCP(seq[1:]))

# produces the CP tree from the given tagArgs
def parseTagArgs(tagArgs,builtin):
  # parse arg
  try:
      args = processTags.makeArgString(tagArgs);
      tree = convertNum(eval(args,lang))
  except:
      sys.stderr.write(("ERROR: cannot parse/build class propagation information for builtin: "+builtin.name+"\ndef: "+tagArgs+"\n"));
      raise
  # turn tuple into chain of Unions
  if isinstance(tree, tuple): tree = tupleToCP(tree)
  return tree


# actual tag definition
def Class(builtin, tagArgs, iset):
  # add the interface
  iset.add("HasClassPropagationInfo");
  
  # create CP tree
  tree = parseTagArgs(tagArgs,builtin)
  
  if (processTags.DEBUG):  
      print "Class args: ",tagArgs
      print "tree:       ", tree
      #print "java:       ", tree.toJava()

  # java expr for parent info - find if tag 'Class' is defined for a parent
  if (builtin.parent and builtin.parent.getAllTags().has_key('Class')):
    parentInfo = 'super.getClassPropagationInfo()'
  else:
    parentInfo = 'new CPNone()'
  
  # deal with the matlabClass info - check if there is a matlabClass tag defined - if not, emit the default
  if (not builtin.getAllTags().has_key('MatlabClass')):
    matlabClassMethod = """
        public CP getMatlabClassPropagationInfo(){{
            return getClassPropagationInfo();
        }}
"""; # there's no explicit tag for matlab - just return the class info
  else:
    matlabClassMethod = ''; # emit nothing - the matlabClass tag will deal with it
  
  
  # produce code
  return matlabClassMethod+"""
        private CP classPropInfo = null; //{tree};
        public CP getClassPropagationInfo(){{
            //set classPropInfo if not defined
            if (classPropInfo == null){{
                CP parentClassPropInfo = {parentInfo};
                classPropInfo = {tree};
            }}
            return classPropInfo;
        }}
""".format(tree=tree.toJava(), javaName=builtin.javaName, parentInfo=parentInfo);

# matlabClass tag definition
def MatlabClass(builtin, tagArgs, iset):
  if not builtin.getAllTags().has_key('Class'):
    raise Exception('MatlabClass tag defined for builtin '+builtin.name+', but there is no Class tag defined')

  # create CP tree
  tree = parseTagArgs(tagArgs,builtin)
  
  if (processTags.DEBUG):  
      print "MatlabClass args: ",tagArgs
      print "tree:             ", tree

  # java expr for parent info - find if tag 'Class' is defined for a parent
  if (builtin.parent and builtin.parent.getAllTags().has_key('Class')):
    parentInfo = 'super.getMatlabClassPropagationInfo()'
  else:
    parentInfo = 'new CPNone()'

  # produce code
  return """
        private CP matlabClassPropInfo = null; //{tree};
        public CP getMatlabClassPropagationInfo(){{
            //set classPropInfo if not defined
            if (matlabClassPropInfo == null){{
                CP parentClassPropInfo = {parentInfo};
                matlabClassPropInfo = {tree};
            }}
            return matlabClassPropInfo;
        }}
""".format(tree=tree.toJava(), javaName=builtin.javaName, parentInfo=parentInfo);
