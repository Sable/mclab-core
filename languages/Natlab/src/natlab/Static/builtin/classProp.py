
# processing Class tag - class propagation language
import processTags
import sys


# definition of the class propagation language - in a dictionary
# helper method - converts numbers to a MatlabClassVar
def convertNum(a):          return MCNum(a) if isinstance(a, (long, int)) else a;
# 1) python classes
# general matlab class used by the class tag (MC = MatlabClass) - defines the operators
class MC():
  def __or__  (self, other): return MCUnion(convertNum(self),convertNum(other));
  def __ror__ (self, other): return MCUnion(convertNum(self),convertNum(other));
  def __and__ (self, other): return MCChain(convertNum(self),convertNum(other));
  def __rand__(self, other): return MCChain(convertNum(self),convertNum(other));
  def __gt__  (self, other): return MCMap(convertNum(self),convertNum(other));
  def __lt__  (self, other): return MCMap(convertNum(other),convertNum(self));
  def __repr__(self):        return str(self);
# <class1> - represents Matlab builtin class
class MCBuiltin(MC):
  def __init__(self,name):   self.name = name;
  def __str__ (self):        return self.name;
  def toJava(self):          return 'new ClassPropTools.MCBuiltin("'+self.name+'")';
# class1 | clas2 - mulitple possibilities for one type
class MCUnion(MC):
  def __init__(self,a,b):    self.class1 = convertNum(a); self.class2 = convertNum(b);
  def __str__ (self):        return '('+str(self.class1)+'|'+str(self.class2)+')';
  def toJava  (self):        return 'new ClassPropTools.MCUnion('+self.class1.toJava()+','+self.class2.toJava()+')';
# class1 & class2 - sequences of types
class MCChain(MC):
  def __init__(self,a,b):    self.class1 = convertNum(a); self.class2 = convertNum(b);
  def __str__ (self):        return '('+str(self.class1)+')&('+str(self.class2)+')';   
  def toJava  (self):        return 'new ClassPropTools.MCChain('+self.class1.toJava()+','+self.class2.toJava()+')';
# class1 > class2 - matches lhs, emits rhs
class MCMap(MC):
  def __init__(self,a,b):    self.args = convertNum(a); self.res = convertNum(b);
  def __str__ (self):        return str(self.args)+'>'+str(self.res);   
  def toJava  (self):        return 'new ClassPropTools.MCMap('+self.args.toJava()+','+self.res.toJava()+')';
# <n> - a specific other argument, defined by a number - negative is counted from back (i.e. -1 is last)
class MCNum(MC):
  def __init__(self,num):    self.num = num;
  def __str__ (self):        return str(self.num);
  def toJava  (self):        return 'new ClassPropTools.MCNum('+str(self.num)+')';
# coerce(MC denoting replacement expr for every argument, MC affeced expr)
# example: coerce((char|logical)>double, (numerical&numerical)>double )
# TODO: this should be a McFunction
class MCCoerce(MC):
  def __init__(self,replaceExpr,expr): self.replaceExpr=replaceExpr; self.expr=expr;
  def __str__ (self):         return 'coerce('+str(self.replaceExpr)+','+str(self.expr)+')'
  def toJava  (self):         return 'new ClassPropTools.MCCoerce('+self.replaceExpr.toJava()+','+self.expr.toJava()+')'
# unparametric expressions of the language - the string and java representation are given by the constructor
class MCNonParametric(MC):
  def __init__(self,str,java): self.str = str; self.java = java;
  def __str__(self):          return self.str;
  def toJava (self):          return self.java;
# function of the form name(<expr>,<expr>,...) - the expresions, and string, java are given by the constructor
class MCFunction(MC):
  def __init__(self,str,java,*exprs): self.exprs = exprs; self.java = java; self.str = str;
  def __str__(self):          return self.str+"("+','.join([str(e) for e in self.exprs])+")"
  def toJava (self):          return self.java+"("+','.join([e.toJava() for e in self.exprs])+")"



# 2) set up keywords of the language in a dictionary
# basic types:
lang = dict(double=MCBuiltin('double'),single=MCBuiltin('single'),char=MCBuiltin('char'),logical=MCBuiltin('logical'),
            uint8=MCBuiltin('uint8'),uint16=MCBuiltin('uint16'),uint32=MCBuiltin('uint32'),uint64=MCBuiltin('uint64'),
            int8=MCBuiltin('int8'),int16=MCBuiltin('int16'),int32=MCBuiltin('int32'),int64=MCBuiltin('int64'),
            function_handle=MCBuiltin('function_handle'))
# union types
lang.update(dict(float=lang['single']|lang['double'], uint=(lang['uint8']|lang['uint16']|lang['uint32']|lang['uint64']), 
                 sint=(lang['int8']|lang['int16']|lang['int32']|lang['int64'])));  
lang['int']    = (lang['uint']|lang['sint']);
lang['numeric']= (lang['float']|lang['int']);
lang['matrix'] = (lang['numeric']|lang['char']|lang['logical']);
# non-parametric bits
lang['none'] =   MCNonParametric('none',  'new ClassPropTools.MCNone()');
lang['end'] =    MCNonParametric('end',   'new ClassPropTools.MCEnd()');
lang['begin'] =  MCNonParametric('begin', 'new ClassPropTools.MCBegin()');
lang['any'] =    MCNonParametric('any',   'new ClassPropTools.MCAny()');
lang['parent'] = MCNonParametric('parent','parentClassPropInfo'); # java code is the local variable
lang['error']  = MCNonParametric('error', 'new ClassPropTools.MCError()');
lang['natlab'] = MCNonParametric('class', 'getClassPropagationInfo()');
lang['matlab'] = MCNonParametric('class', 'getMatlabClassPropagationInfo()');
lang['scalar'] = MCNonParametric('scalar','new ClassPropTools.MCScalar()');
# other bits of the language
lang['coerce'] = lambda replaceExpr, expr: MCCoerce(replaceExpr,expr)
lang['opt'] = lambda expr: (expr|lang['none']) #note: op(x), being (x|none), will cause an error on the rhs
lang['not'] = lambda typesExpr: MCFunction('not','new ClassPropTools.MCNot',typesExpr)
# todo - so far opt only allows up to 10 repititions
opt = lang['opt']
lang['star']= lambda expr: opt(expr)&opt(expr)&opt(expr)&opt(expr)&opt(expr)&opt(expr)&opt(expr)&opt(expr)&opt(expr)&opt(expr)
# functions
lang['typeString'] = lambda typesExpr: MCFunction('typeString','new ClassPropTools.MCTypeString',typesExpr)



# TODO - other possible language features
#variables?
#mult(x,[max],[min]) #tries to match as many as possible max,min may be 0
#matlab - allows matching the current matlab tree for a MatlabClass

# helper method - turns a sequence of MC objects into MCUnion objects
def tupleToMC(seq):
  if len(seq) == 1: return seq[0]
  return MCUnion(seq[0],tupleToMC(seq[1:]))

# produces the MC tree from the given tagArgs
def parseTagArgs(tagArgs,builtin):
  # parse arg
  try:
      args = processTags.makeArgString(tagArgs);
      tree = convertNum(eval(args,lang))
  except:
      sys.stderr.write(("ERROR: cannot parse/build class propagation information for builtin: "+builtin.name+"\ndef: "+tagArgs+"\n"));
      raise
  # turn tuple into chain of Unions
  if isinstance(tree, tuple): tree = tupleToMC(tree)
  return tree


# actual tag definition
def Class(builtin, tagArgs, iset):
  # add the interface
  iset.add("ClassPropagationDefined");
  
  # create MC tree
  tree = parseTagArgs(tagArgs,builtin)
  
  if (processTags.DEBUG):  
      print "Class args: ",tagArgs
      print "tree:       ", tree
      #print "java:       ", tree.toJava()

  # java expr for parent info - find if tag 'Class' is defined for a parent
  if (builtin.parent and builtin.parent.getAllTags().has_key('Class')):
    parentInfo = 'super.getClassPropagationInfo()'
  else:
    parentInfo = 'new ClassPropTools.MCNone()'
  
  # deal with the matlabClass info - check if there is a matlabClass tag defined - if not, emit the default
  if (not builtin.getAllTags().has_key('MatlabClass')):
    matlabClassMethod = """
        public ClassPropTools.MC getMatlabClassPropagationInfo(){{
            return getClassPropagationInfo();
        }}
"""; # there's no explicit tag for matlab - just return the class info
  else:
    matlabClassMethod = ''; # emit nothing - the matlabClass tag will deal with it
  
  
  # produce code
  return matlabClassMethod+"""
        private ClassPropTools.MC classPropInfo = null; //{tree};
        public ClassPropTools.MC getClassPropagationInfo(){{
            //set classPropInfo if not defined
            if (classPropInfo == null){{
                ClassPropTools.MC parentClassPropInfo = {parentInfo};
                classPropInfo = {tree};
            }}
            return classPropInfo;
        }}
""".format(tree=tree.toJava(), javaName=builtin.javaName, parentInfo=parentInfo);

# matlabClass tag definition
def MatlabClass(builtin, tagArgs, iset):
  if not builtin.getAllTags().has_key('Class'):
    raise Exception('MatlabClass tag defined for builtin '+builtin.name+', but there is no Class tag defined')

  # create MC tree
  tree = parseTagArgs(tagArgs,builtin)
  
  if (processTags.DEBUG):  
      print "MatlabClass args: ",tagArgs
      print "tree:             ", tree

  # java expr for parent info - find if tag 'Class' is defined for a parent
  if (builtin.parent and builtin.parent.getAllTags().has_key('Class')):
    parentInfo = 'super.getMatlabClassPropagationInfo()'
  else:
    parentInfo = 'new ClassPropTools.MCNone()'

  # produce code
  return """
        private ClassPropTools.MC matlabClassPropInfo = null; //{tree};
        public ClassPropTools.MC getMatlabClassPropagationInfo(){{
            //set classPropInfo if not defined
            if (matlabClassPropInfo == null){{
                ClassPropTools.MC parentClassPropInfo = {parentInfo};
                matlabClassPropInfo = {tree};
            }}
            return matlabClassPropInfo;
        }}
""".format(tree=tree.toJava(), javaName=builtin.javaName, parentInfo=parentInfo);
