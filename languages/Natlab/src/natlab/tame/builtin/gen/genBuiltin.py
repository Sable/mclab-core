# =========================================================================== #
#                                                                             #
# Copyright 2011 Anton Dubrau and McGill University.                          #
#                                                                             #
#   Licensed under the Apache License, Version 2.0 (the "License");           #
#   you may not use this file except in compliance with the License.          #
#   You may obtain a copy of the License at                                   #
#                                                                             #
#       http://www.apache.org/licenses/LICENSE-2.0                            #
#                                                                             #
#   Unless required by applicable law or agreed to in writing, software       #
#   distributed under the License is distributed on an "AS IS" BASIS,         #
#   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  #
#   See the License for the specific language governing permissions and       #
#  limitations under the License.                                             #
#                                                                             #
# =========================================================================== #


# This python script generates the the Builtin.java and BuiltinVisitor.java
# given the builtin.csv file.
# It can also be used as a module, in which case the the module provides
# a way to look a the information contained in the csv file(s) using some
# objects. The testing code in the tame package uses that to generate unit
# tests.
# 
# TODO - eventually move this from python back to java.

import csv;
import processTags;
import collections;
import itertools;


# caps first letter
def firstCaps(s):
   if (len(s) == 0): return ''
   return s[0].capitalize()+s[1:]


# returns the children of the given name
def getChildren(name,children,parents) :
  return [children[i] for i in range(0,len(children)) if parents[i] == name]


# function which constructs the Builtin.java file
def printBuiltinJava(file,builtins):
    N = len(builtins);
    file.write("""
package natlab.tame.builtin;

//decloare the required imports:
import java.util.*;
import natlab.toolkits.path.BuiltinQuery;
import natlab.tame.builtin.classprop.*;
import natlab.tame.builtin.classprop.ast.*;


public abstract class Builtin {
    private static HashMap<String, Builtin> builtinMap = new HashMap<String, Builtin>();
    public static void main(String[] args) {
        java.lang.System.out.println(getInstance("i"));
        Builtin b = builtinMap.get("i");
        java.lang.System.out.println(b);
        java.lang.System.out.println("number of builtins "+builtinMap.size());
        java.lang.System.out.println(builtinMap);
    }

    /**
     * returns the builtin from the given name (case sensitive)
     * if there is no builtin, returns null.
     */
    public static final Builtin getInstance(String name){
        if (builtinMap.containsKey(name)){
            return builtinMap.get(name);
        } else {
            return null;
        }
    }
    
    /**
     * returns a builtin query object returning true for all builtings in this class hierarchy
     */
    public static BuiltinQuery getBuiltinQuery() {
        return new BuiltinQuery(){
            public boolean isBuiltin(String functionname) 
              { return builtinMap.containsKey(functionname); }
        };
    }

    /**
     * returns the names of all defined Builtins as an unmodifiable set
     */
    public static Set<String> getAllBuiltinNames(){
      return Collections.unmodifiableSet(builtinMap.keySet());
    }

    
    /**
     * calls the BuiltinVisitor method associated with this Builtin, using the given argument,
     * and returns the value returned by the visitor.
     * (e.g. if this is a Builtin.Plus, calls visitor.casePlus)
     */
    public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
        return visitor.caseBuiltin(this,arg);
    }
    
    /**
     * allows the instantiation of Builtins - used to create more builtins using the builtinMap
     */
    //todo should all builtins of a given type be the same, so that this is not required?
    protected Builtin create(){return null;}

    /**
     * Returns the name of the builtin function that this object is referring to
     */
    public abstract String getName();
    
    public String toString() {
        return getName();
    }
    
    public int hashCode() {
        return getName().hashCode();
    }

    //static initializer fills in builtinMap
    static {
""");
    # print the placing of classes into the builtinMap
    for b in builtins:
        if (not b.isAbstract):
            file.write('        builtinMap.put("%s",%s.getInstance());\n' % (b.name,b.javaName))
    file.write( """    }    
    
    //the actual Builtin Classes:
    """)
    # print the classes
    for b in builtins:
        printClass(file,b)
    file.write( "\n}" )



# prints the static classes inside the Builtin class, for a given builtin operation
# note that operations can either be asbtract or non abstract
def printClass(file,b):
    # first define code for either abstract or non-abstract class
    # note that we reinsert %s, so that we can insert the 'implements' clause later
    iset = set();
    if (b.isAbstract):
        code =  """
    public static abstract class %s extends %s %s {
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.case%s(this,arg);
        }
        """ % (b.javaName,b.parentJavaName,"%s",b.javaName);
    else:
        code = """
    public static class %s extends %s %s {
        //returns the singleton instance of this class
        private static %s singleton = null;
        public static %s getInstance(){
            if (singleton == null) singleton = new %s();
            return singleton;
        }
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.case%s(this,arg);
        }
        //return name of builtin
        public String getName(){
            return "%s";
        }
        """ % (b.javaName,b.parentJavaName,"%s",b.javaName,b.javaName,b.javaName,b.javaName,b.name);
    # fill in code due to tags
    code += processTags.processTags(b,iset)    
    
    # other code that needs to be added to a class can be added here
    
    # finish classes
    implements = '';
    if (len(iset) > 0):
       implements = 'implements '+', '.join(iset)
    file.write(code % (implements) + """
    }""");


# prints the visitor class walking up the class hierarchy as a default case
def printBuiltinVisitorJava(file,builtin):
   file.write("""package natlab.tame.builtin;

public abstract class BuiltinVisitor<Arg,Ret> {
   public abstract Ret caseBuiltin(Builtin builtin,Arg arg);""")
   for b in builtin:
      if (b.isAbstract) : file.write('\n')
      if (len(b.comments) > 0) : file.write('    \n    //%s' % b.comments)
      file.write("""
    public Ret case%s(Builtin builtin,Arg arg){ return case%s(builtin,arg); }""" % (b.javaName,b.parentJavaName))
   file.write("""
}""")
    
# prints subtrees below the following nodes separately
printSeparateList = ['MatrixFn'];
# prints the the tree as a dot file
def printTreeDot(file,builtin):
   file.write("""digraph builtins{
       //size="10.25,7.75";
       rankdir=LR;
       graph [ranksep=.2,concentrate=true, nodesep=.3];
       //edge[ weight = 1.2 ];
       Builtin;

""");
   list = [];
   for b in builtin:
      if (b.isAbstract) :
         file.write('\n')
         shortName = b.shortName
         sepPrefix = 'sep' if shortName in printSeparateList else '' # sepPrefix is non-empty if subtre is separated
         if (len(b.comments) > 0) : file.write('    \n       //%s' % b.comments)
         file.write("""
       %s[shape=plaintext,color=none,label="%s"];""" % (sepPrefix+b.name,shortName));
         if (len(sepPrefix) > 0): file.write("""
       %s[shape=plaintext,color=none,label="%s"];""" % (b.name,shortName)); # print second node if tree is separated
         file.write("""
       %s -> %s;""" % (b.parentName,sepPrefix+b.name));
         # find the non abstract children - and insert new node for them
         cs =  [child.name for child in b.children if not child.isAbstract]
         if len(cs) >= 1 : 
           file.write("""
       %s[shape=box,label="%s",rank="max"];""" % (cs[0],"\\n".join(cs)));
           file.write("""
       %s -> %s;""" % (b.name,cs[0]));
           list.append(cs[0])

   file.write("""
       //  {rank=same; %s }
}""" % " ".join(list))
    

# used by printClass for tagMatching
import re
matchWord = re.compile('^\w*');

# given a list of strings which are tags, verbatim from the csv, returns the dictionary of tags->args
def getTagDict(taglist):
   taglist = ([s.strip() for s in taglist if s.strip() != '']) # read tags - remove empties and strip
   t = {}
   for tag in taglist:
      # find first word
      i = matchWord.match(tag).span();
      tagName = tag[i[0]:i[1]];
      tagArgs = tag[i[1]:].strip();
      t[tagName] = tagArgs;
   return t;





# definition for data
# use for Builtins: 'name javaName isAbstract parentName childNames parent children tags comment index'
class Builtin(object):
   def __init__(self,name,originalName,isAbstract,parentName,parent,childNames,children,tags,comments,index):
      self.__dict__.update(locals()); #this updates self as well - bad?
      self.javaName = firstCaps(name); 
      # define java name, parent java name, and short name
      self.parentJavaName = firstCaps(parentName);
      self.parent = None; self.children = None;
      shortName = self.name[8:] if self.isAbstract else self.name #removes the 'abstract'
      shortName = shortName[0:-8]+'Fn' if shortName[-8:] == 'Function' else shortName; # replace 'Function'-end
      self.shortName = shortName
   def __str__(self):
      return 'Builtin-{0.name}'.format(self);
   def __repr__(self):
      return str(self)
   #iterator
   def __iter__(self):
      return itertools.chain((self,),*self.children)
   # returns 
   def getByName(self,name):
      for builtin in self:
         if builtin.name == name:
            return builtin
   def getByOriginalName(self,name):
      for builtin in self:
         if builtin.originalName == name:
            return builtin
   # returns a list of all leafs
   def getLeafs(self):
      result = []
      for builtin in self:
         if not builtin.isAbstract:
            print '--',builtin
            result.append(builtin)
      return result
   # returns all defined tags, including tags defined for parents, as a dictionary tagName->builtin
   # where builtin is the the most recent ancestor for which the tag is defined
   def getAllTags(self):
      if self.parent:
         result = self.parent.getAllTags()
      else:
         result = {};
      result.update(dict((tagName,self) for tagName in self.tags.keys()))
      return result

   # TODO 
   # - tags from all parents
   # etc. etc.
   # - has/get parent
   # - has/get child
  


# converts csv data to Builtin definition
def createBuiltinDefs(csvData):
   dict = {}
   result = []
   # create objects
   for i in range(0,len(csvData.names)):
      builtin = Builtin(name=csvData.names[i],
                        originalName=csvData.originalNames[i],
                        isAbstract=csvData.isAbstracts[i],
                        parentName=csvData.parents[i],
                        parent=None,
                        childNames=[csvData.names[j] for j in range(0,len(csvData.names)) if csvData.parents[j] == csvData.names[i]],
                        children=[],
                        tags=csvData.tags[i],
                        comments=csvData.comments[i],
                        index=i);
      dict[builtin.name] = builtin
      result.append(builtin)
   # fill in parents, children
   for i in range(0,len(csvData.names)):
      result[i].children = [dict[name] for name in result[i].childNames]
      try: result[i].parent = dict[result[i].parentName];
      except:
         pass
   return result;


# reads the csv file description, and returns it as a Builtin tree
def readCSVData(fileName):
   # the script that reads the csv and prints the stuff
   reader = csv.reader(open(fileName), delimiter=';');
   list = []
   tree = {}
   currentParent = ''; # if no parent is defined, use the most recent child - no need to specify parents of leafs
   currentComment = ''; # comments are collected and output in the visitor class

   children = []
   parents = []
   comments = []
   tags = [] # list of map of tags for every builtin - starts after 2nd entry (tagName -> tagArgs)


   # read through all rows, filling in tree
   for row in reader:
      if (len(row) == 0): # empty row - skip
         3;
      elif (row[0][0] == '#'): # catch comment
         currentComment = row[0][1:].strip()
      elif (row[0].strip()[0] == '-'): # catch continuation
         row[0] = row[0].strip()[1:]; # remove the '-' and white space from first cell
         tags[-1].update(getTagDict(row)) # get the defined tags and update the most recent tag dict
      else:
         children.append(row[0].strip())
         # use most recent child (currentParent) if no parent i defined
         if ((len(row) < 2 or (len(row[1].strip()) ==  0))):
             parents.append(currentParent)
         else:
             parents.append(row[1].strip());
             currentParent = children[-1];

         tags.append(getTagDict(row[2:]));
         #print children[-1], tags[-1]
         comments.append(currentComment);
         currentComment = '';


   # rename all prents that are occuring, finding/setting the abstract classes
   abstract = [children[i] in parents or tags[i].has_key('abstract') for i in range(0,len(children))]
   originalNames = children
   children = ['abstract'+firstCaps(child)  if child in parents else child for child in children]
   parents  = ['abstract'+firstCaps(parent) for parent in parents]

   #  overall parent is Builtin, treat it specially
   parents = ['Builtin' if parent == 'abstractBuiltin' else parent for parent in parents]


   return  createBuiltinDefs(collections.namedtuple('CSVData','names originalNames parents isAbstracts tags comments')
                             (children,originalNames,parents,abstract,tags,comments));


# overall 'script' that actually generates all the code
def generate():
   # actually read the data
   builtins = readCSVData("builtins.csv");
   
   
   # write Builtin.java
   print 'generating Builtin.java...'
   file = open('../Builtin.java','w');
   printBuiltinJava(file,builtins)
   file.close();
   
   
   # write BuiltinVisitor.java
   print 'generating BuiltinVisitor.java...'
   file = open('../BuiltinVisitor.java','w');
   printBuiltinVisitorJava(file,builtins)
   file.close();
   
   
   # write the tree.dot
   print 'creating dot of tree'
   file = open('tree.dot','w');
   printTreeDot(file,builtins)
   file.close();
   
   # output which builtins have class prop
   for b in builtins:      
      if not b.isAbstract and not b.getAllTags().has_key("Class") and b.name in list:
         print b.name
   
   
   print 'genereated code for \n - %d builtins (including abstract builtins)' % sum([1 for b in builtins])
   print ' - %d non-abstract builtins' % sum([1 for b in builtins if not b.isAbstract])

   #for i in range(0,len(children)):
   #   if not abstract[i]: print children[i]


list = ['vertcat',
'cos',
'nargout',
'ge',
'round',
'mrdivide',
'dot',
'diag',
'abs',
'isinf',
'false',
'horzcat',
'power',
'rand',
'superiorfloat',
'mpower',
'eye',
'plus',
'minus',
'nargin',
'std',
'mod',
'imag',
'double',
'size',
'and',
'not',
'eps',
'exp',
'uminus',
'le',
'floor',
'realmax',
'zeros',
'rdivide',
'gt',
'numel',
'disp',
'ctranspose',
'pi',
'fprintf',
'mtimes',
'sort',
'transpose',
'ceil',
'sum',
'colon',
'lt',
'reshape',
'cumsum',
'times',
'fix',
'mean',
'find',
'end',
'min',
'xor',
'max',
'clock',
'sqrt',
'true',
'ones',
'histc',
'i',
'sin',
'ne',
'imwrite',
'eq']


if __name__ == "__main__":
   generate()




