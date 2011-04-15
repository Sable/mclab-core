import csv;

# caps first letter
def firstCaps(s):
   if (len(s) == 0): return ''
   return s[0].capitalize()+s[1:]


# function which constructs the Builtin.java file
def printBuiltinJava(file,children,parents,abstract,comments):
    N = len(children);
    file.write("""
package natlab.Static.builtin;

import java.util.HashMap;
//import natlab.toolkits.path.BuiltinQuery;


public abstract class Builtin {
    private static HashMap<String, Builtin> builtinMap = new HashMap<String, Builtin>();
    public static void main(String[] args) {
        System.out.println(create("i"));
        Builtin b = builtinMap.get("i");
        System.out.println(b+"  "+b.create());
        System.out.println("number of builtins "+builtinMap.size());
        System.out.println(builtinMap);
    }

    /**
     * creates a builtin from the given name (case sensitive)
     */
    public static final Builtin create(String name){
        if (builtinMap.containsKey(name)){
            return builtinMap.get(name).create();
        } else {
            throw new UnsupportedOperationException("cannot create nonexistent builtin "+name);
        }
    }
    
    /**
     * returns a builtin query object returning true for all builtings in this class hierarchy
     *
    public static BuiltinQuery getBuiltinQuery() {
        return new BuiltinQuery(){
            public boolean isBuiltin(String functionname) 
              { return builtinMap.containsKey(functionname); }
        };
    } /* */

    
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
            file.write('        builtinMap.put("%s",new %s());\n' % (children[i],firstCaps(children[i])))
    file.write( """    }    
    
    //the actual Builtin Classes:
    """)
    # print the classes
    for i in range(0,N):
        printClass(file,parents[i],children[i],abstract[i])
    file.write( "\n}" )

# prints the static classes inside the Builtin class, for a given builtin operation
# note that operations can either be asbtract or non abstract
def printClass(file,parent,child,isAbstract):
    if (isAbstract):
        file.write( """
    public static abstract class %s extends %s {
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.case%s(this,arg);
        }
    }""" % (firstCaps(child),firstCaps(parent),firstCaps(child)) )

    else:
        file.write( """
    public static class %s extends %s {
        //creates a new instance of this class
        protected Builtin create(){
            return new %s();
        }
        //visit visitor
        public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor, Arg arg){
            return visitor.case%s(this,arg);
        }
        //return name of builtin
        public String getName(){
            return "%s";
        }
    }""" % (firstCaps(child),firstCaps(parent),firstCaps(child),firstCaps(child),child) )


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
    






# the script that reads the csv and prints the stuff
reader = csv.reader(open("builtins.csv"));
list = []
tree = {}
currentParent = ''; # if no parent is defined, use the most child - no need to specify parents of leafs
currentComment = ''; # comments are collected and output in the visitor class

children = []
parents = []
comments = []

# read through all rows, filling in tree
for row in reader:
    if (len(row) == 0):
        3;
    elif (row[0][0] == '#'):
        currentComment = row[0][1:].strip()
    else:
        children.append(row[0])
        if ((len(row) < 2 or (len(row[1]) ==  0))):
            parents.append(currentParent)
        else:
            parents.append(row[1].strip());
            currentParent = children[-1];
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

print 'genereated code for %d builtings (including abstract builtins)' % (len(children))

for i in range(0,len(children)):
   if not abstract[i]: print children[i]
