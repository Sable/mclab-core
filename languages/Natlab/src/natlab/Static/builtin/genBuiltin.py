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

import csv;
import processTags;



# caps first letter
def firstCaps(s):
   if (len(s) == 0): return ''
   return s[0].capitalize()+s[1:]


# returns the children of the given name
def getChildren(name,children,parents) :
  return [children[i] for i in range(0,len(children)) if parents[i] == name]


# function which constructs the Builtin.java file
def printBuiltinJava(file,children,parents,abstract,comments):
    N = len(children);
    file.write("""
package natlab.Static.builtin;

import java.util.HashMap;
import natlab.toolkits.path.BuiltinQuery;


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
    for i in range(0,N):
        if (not abstract[i]):
            file.write('        builtinMap.put("%s",%s.getInstance());\n' % (children[i],firstCaps(children[i])))
    file.write( """    }    
    
    //the actual Builtin Classes:
    """)
    # print the classes
    for i in range(0,N):
        printClass(file,parents[i],children[i],abstract[i],tags[i])
    file.write( "\n}" )


# used by printClass for tagMatching
import re
matchWord = re.compile('^\w*');

# prints the static classes inside the Builtin class, for a given builtin operation
# note that operations can either be asbtract or non abstract
def printClass(file,parent,child,isAbstract,tags):
    # first define code for either abstract or non-abstract class
    # note that we reinsert %s, so that we can insert the 'implements' clause later
    iset = set();
    if (isAbstract):
        code =  """
    public static abstract class %s extends %s %s {
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.case%s(this,arg);
        }
        """ % (firstCaps(child),firstCaps(parent),"%s",firstCaps(child));
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
        """ % (firstCaps(child),firstCaps(parent),"%s",firstCaps(child),firstCaps(child),firstCaps(child),firstCaps(child),child);
    # fill in code due to tags
    for tagName in tags.keys():
       tagArgs = tags[tagName];
       # the function that deals with the tag needs to have the same name as the tag 
       f = eval('processTags.'+tagName)
       # calls with (name, java-name, parent-java-name, is-abstract, tag-arguments, implements-set, tag-map)
       code += f(child,firstCaps(child),firstCaps(parent),isAbstract,tagArgs,iset,tags)

    # other code that needs to be added to a class can be added here


    # finish classes
    implements = '';
    if (len(iset) > 0):
       implements = 'implements '+', '.join(iset)
    file.write(code % (implements) + """
    }""");


# prints the visitor class walking up the class hierarchy as a default case
def printBuiltinVisitorJava(file,children,parents,abstract,comments):
   N = len(children);
   file.write("""package natlab.Static.builtin;

public abstract class BuiltinVisitor<Arg,Ret> {
   public abstract Ret caseBuiltin(Builtin builtin,Arg arg);""")
   for i in range(0,N):
      if (abstract[i]) : file.write('\n')
      if (len(comments[i]) > 0) : file.write('    \n    //%s' % comments[i])
      file.write("""
    public Ret case%s(Builtin builtin,Arg arg){ return case%s(builtin,arg); }""" % (firstCaps(children[i]),firstCaps(parents[i])))
   file.write("""
}""")
    

# prints the the tree as a dot file
def printTreeDot(file,children,parents,abstract,comments):
   N = len(children);
   file.write("""digraph builtins{
       //size="10.25,7.75";
       rankdir=LR;
       graph [ranksep=0];
       edge[ weight = 1.2 ];
       Builtin;

""");
   list = [];
   for i in range(0,N):
      if (abstract[i]) :
         file.write('\n')
         shortName = children[i][8:] #removes the 'abstract'
         if (len(comments[i]) > 0) : file.write('    \n    //%s' % comments[i])
         file.write("""
       %s[shape=plaintext,color=none,label="%s"];""" % (children[i],shortName));
         file.write("""
       %s -> %s;""" % (parents[i],children[i]));
         # find the non abstract children - and insert new node for them
         cs =  [children[j] for j in range(0,N) if parents[j] == children[i] and not abstract[j]]
         if len(cs) >= 1 : 
           file.write("""
       %s[shape=box,label="%s",rank="max"];""" % (cs[0],"\\n".join(cs)));
           file.write("""
       %s -> %s;""" % (children[i],cs[0]));
           list.append(cs[0])

   file.write("""
       //  {rank=same; %s }
}""" % " ".join(list))
    






# the script that reads the csv and prints the stuff
reader = csv.reader(open("builtins.csv"), delimiter=';');
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
    if (len(row) == 0):
        3;
    elif (row[0][0] == '#'): #catch comment
        currentComment = row[0][1:].strip()
    else:
        children.append(row[0])
        # use most recent child (currentParent) if no parent i defined
        if ((len(row) < 2 or (len(row[1].strip()) ==  0))):
            parents.append(currentParent)
        else:
            parents.append(row[1].strip());
            currentParent = children[-1];
        taglist = ([s.strip() for s in row[2:] if s.strip() != '']) # read tags - remove empties and strip
        t = {}
        for tag in taglist:
           # find first word
           i = matchWord.match(tag).span();
           tagName = tag[i[0]:i[1]];
           tagArgs = tag[i[1]:].strip();
           t[tagName] = tagArgs;
        tags.append(t);
        print t
        comments.append(currentComment);
        currentComment = '';


# rename all prents that are occuring, finding/setting the abstract classes
abstract = [True if child in parents else False for child in children]
children = ['abstract'+firstCaps(child)  if child in parents else child for child in children]
parents  = ['abstract'+firstCaps(parent) for parent in parents]





#  overall parent is Builtin, treat it specially
parents = ['Builtin' if parent == 'abstractBuiltin' else parent for parent in parents]


# write Builtin.java
print 'generating Builtin.java...'
file = open('Builtin.java','w');
printBuiltinJava(file,children,parents,abstract,comments)
file.close();


# write BuiltinVisitor.java
print 'generating BuiltinVisitor.java...'
file = open('BuiltinVisitor.java','w');
printBuiltinVisitorJava(file,children,parents,abstract,comments)
file.close();


# write the tree.dot
print 'creating dot of tree'
file = open('tree.dot','w');
printTreeDot(file,children,parents,abstract,comments)
file.close();

print 'genereated code for %d builtings (including abstract builtins)' % (len(children))

for i in range(0,len(children)):
   if not abstract[i]: print children[i]

print 'number of Builtins generated: %d' % (len(children))
