# overall file that generates unit tests

# 1) generate builtin tests in files
# 2) run matlab scripts that reads all generated (auto) files, and turns them into 'pass-auto' or 'fail-auto' files.
# - we can even find the exceptions:
# MEXception - identifier - MATLAB:dimagree
# MEXception - identifier - MATLAB:mixedClasses
# MEXception - identifier - MATLAB:undefinedFunction


# *** generate builtins *************************************************************
import bUnits
# delete previously generated builtin tests
bUnits.delBuiltinTests()

# define some groups of functions
binaryNumeric = bUnits.builtins.getByOriginalName('binaryNumericFunction')
unaryNumeric =  bUnits.builtins.getByOriginalName('unaryNumericFunction')

unaryAnyMatrix = bUnits.builtins.getByOriginalName('unaryAnyMatrixFunction')

binaryFloat = bUnits.builtins.getByOriginalName('binaryFloatFunction')
unaryFloat =  bUnits.builtins.getByOriginalName('unaryFloatFunction')


# create unit tests
bUnits.genBuiltinTests([binaryNumeric,binaryFloat],2,bUnits.double,[(1,1),(3,3),(3,1),(1,3)])
bUnits.genBuiltinTests([binaryNumeric,binaryFloat],2,bUnits.someMatrix,[(2,2)])

bUnits.genBuiltinTests([unaryNumeric,unaryAnyMatrix,unaryFloat],1,bUnits.double,[(1,1),(3,3),(3,1),(1,3)])
bUnits.genBuiltinTests([unaryNumeric,unaryAnyMatrix,unaryFloat],1,bUnits.someMatrix,[(2,2)])

bUnits.genBuiltinTests([bUnits.builtins.getByOriginalName('colon')],2,bUnits.someMatrix,[(1,1)])
bUnits.genBuiltinTests([bUnits.builtins.getByOriginalName('colon')],3,bUnits.someMatrix,[(1,1)])


