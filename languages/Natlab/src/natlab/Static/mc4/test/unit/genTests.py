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



# binaries
bUnits.genTests([binaryNumeric,binaryFloat],
                bUnits.genArgs(2,bUnits.double,[(1,1),(3,3),(3,1),(1,3)])+
                bUnits.genArgs(2,bUnits.someMatrix,[(1,1)])+
                bUnits.genArgs(2,bUnits.someMatrix,[(2,2)])+
                bUnits.genArgs(3,bUnits.double,[(1,1)])+
                bUnits.genArgs(1,bUnits.double,[(1,1)]))

# unaries
bUnits.genTests([unaryNumeric,unaryAnyMatrix,unaryFloat],
                bUnits.genArgs(1,bUnits.double,[(1,1),(3,3),(3,1),(1,3)])+
                bUnits.genArgs(1,bUnits.someMatrix,[(2,2)])+
                bUnits.genArgs(1,bUnits.someMatrix,[(1,1)])+
                bUnits.genArgs(2,bUnits.double,[(1,1)])+
                bUnits.genArgs(3,bUnits.double,[(1,1)]))

#byShapeAndTypeMatrixCreation
typeNames = [["'uint8'"],["'int32'"],["'double'"],["'single'"],["'logical'"],["'char'"],[]]
bUnits.genTests([bUnits.builtins.getByOriginalName('byShapeAndTypeMatrixCreation')],
                bUnits.genArgs(1,bUnits.someMatrix,[(1,1)])+
                bUnits.genArgs(1,bUnits.someMatrix,[(1,3),(3,1)])+
                bUnits.genArgs(2,bUnits.someNumerical,[(1,1)])+
                bUnits.genArgs(3,bUnits.double,[(1,1)]),
                typeNames)

#queries
matrixQuery = bUnits.builtins.getByOriginalName('matrixQuery')
versatileQuery = bUnits.builtins.getByOriginalName('versatileQuery')

bUnits.genTests([matrixQuery,versatileQuery],
                bUnits.genArgs(1,bUnits.double,[(1,1),(3,3),(3,1),(1,3)])+
                bUnits.genArgs(2,bUnits.double,[(1,1),(3,3),(3,1),(1,3)])+
                bUnits.genArgs(1,bUnits.someMatrix,[(1,1)])+
                bUnits.genArgs(1,bUnits.someMatrix,[(2,2)])+
                bUnits.genArgs(2,bUnits.someMatrix,[(1,1)])+
                bUnits.genArgs(2,bUnits.someMatrix,[(2,2)])+
                bUnits.genArgs(3,bUnits.double,[(1,1)]))



# specific functions ***********************************************************
bUnits.genTests([bUnits.builtins.getByOriginalName('colon')],
                bUnits.genArgs(2,bUnits.someMatrix,[(1,1)])+
                bUnits.genArgs(3,bUnits.someMatrix,[(1,1)]));
