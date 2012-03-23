# on import, read all the builtins
# NOTE - changing the directory where this is will break the load
import sys
import random
import os.path
import fnmatch

ignoreBuiltinSet = ['end']
int8 =  ['int8({0})']
int16 = ['int16({0})']
int32 = ['int32({0})']
int64 = ['int64({0})']
uint8 =  ['uint8({0})']
uint16 = ['uint16({0})']
uint32 = ['uint32({0})']
uint64 = ['uint64({0})']
uint = uint8+uint16+uint32+uint64
sint = int8+int16+int32+int64
int = uint+sint
double = ['double({0})']
single = ['single({0})']
float = double+single
numerical = float+uint+sint
someNumerical = single+double+int8+uint64
char = ['char(32+80*{0})']
logical = ['({0} > .5)']
matrix = numerical+char+logical
someMatrix = someNumerical+char+logical



# cross lists
def cross(*args):
    ans = [[]]
    for arg in args:
        ans = [x+y for x in ans for y in arg]
    return ans

# copy n times
def copyNTimes(array,n):
    if (n > 0):
        return [array]+copyNTimes(array,n-1)
    else:
        return []


# generates a matlab matrix expression of the given type and shape
def genMatrix(argType, argShape, seed=0):
    random.seed(seed);
    r = '['
    for i in range(0,argShape[0]):
        for j in range(0,argShape[1]):
            r+=str(random.random())+',';
        r = r[:-1]+';';
    r = r[:-1]+']*n';
    r = argType.format(r);
    return r;

# generates a list of all combinations of argNum Matlab expressions with the given argTypes, argShapes lists
def genArgs(argNum, argTypes, argShapes):
    argList = [];
    seed = 0;
    for t in argTypes:
        for s in argShapes:
            argList.append([genMatrix(t,s,seed)]);
            seed = seed+1;
    crossedArgs = cross(*copyNTimes(argList,argNum));
    return crossedArgs;


def genUnitFunction(testName, builtinName, args):
    s = 'function [result] = {0}(n)\n'.format(testName)
    for i in range(0,len(args)):
        s += '  x{0} = {1};\n'.format(i,args[i]);
    s += '  result = {0}({1});\n'.format(builtinName,','.join(['x'+s for s in map(str,range(0,len(args)))]));
    s += 'end\n';
    return s

# for given builtin, finds next name for a unit case as (path,name)
def pathNameOfBuiltin(builtin):
    # find dir - create it if non existent
    path = os.path.realpath('./builtin/'+builtin.name);
    if not os.path.exists(path):
        os.makedirs(path);
    # how many automatically gen files - that's the new index
    list = fnmatch.filter(os.listdir(path),"u*_auto.m")
    n = len(list)
    return (path,'u'+str(n)+'_auto')



def genBuiltinTest(b,args):
    # generate unit case path/name/code
    path,name = pathNameOfBuiltin(b)
    fn = genUnitFunction(name,b.name,args)                
    # write to file
    print path+'/'+name+'.m';
    file = open(path+'/'+name+'.m','w')
    file.write(fn)
    file.close();


# generates tests for the given list of builtins, and list of arguments
# argsList is a set of arguments, where every entry is list of lists
# example
# genTests(builtin, [['3','6'],['2']]) - will create two tests
# gen args generates tests
def genTests(builtins, *argsList):
    crossedArgs = cross(*argsList)
    for btree in builtins:
        for b in btree:
            if (not b.isAbstract) and (not b.name in ignoreBuiltinSet):
                for args in crossedArgs:
                    genBuiltinTest(b,args);


# generates tests for the given list of builtins, arg types and shapes
def genTestsByTypeAndShape(builtins, argNum, argTypes, argShapes):
    crossedArgs = genArgs(argNum, argTypes, argShapes);
    genTests(builtins,crossedArgs)


# deletes previous builtin tests
def delBuiltinTests():
   # delete files
   for dirpath, dirnames, filenames in os.walk('./builtin/'):
       for f in fnmatch.filter(filenames,"u*_auto.m"):
           os.remove(os.path.join(dirpath,f))
   # delete empty directories
   for dirpath, dirnames, filenames in os.walk('./builtin/'):
       try:
           os.removedirs(dirpath)
       except:
           pass



builtinPath=os.path.realpath('../../builtin');
sys.path.append(builtinPath)
builtinPath=os.path.realpath('../../../builtin');
sys.path.append(builtinPath)
import genBuiltin
builtins=genBuiltin.readCSVData(builtinPath+'/builtins.csv')[0];





