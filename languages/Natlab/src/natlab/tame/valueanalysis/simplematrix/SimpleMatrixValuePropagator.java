package natlab.tame.valueanalysis.simplematrix;

import java.util.*;
import natlab.tame.builtin.*;
import natlab.tame.builtin.classprop.ClassPropTool;
import natlab.tame.builtin.classprop.HasClassPropagationInfo;
import natlab.tame.classes.reference.*;
import natlab.tame.valueanalysis.*;
import natlab.tame.valueanalysis.constant.*;
import natlab.tame.valueanalysis.value.*;
import natlab.tame.valueanalysis.value.composite.CellValue;

public class SimpleMatrixValuePropagator extends ValuePropagator<SimpleMatrixValue>{
    public static boolean DEBUG = false;
    ConstantPropagator constantProp = ConstantPropagator.getInstance();
    
    public SimpleMatrixValuePropagator() {
        super(new SimpleMatrixValueFactory());
    }

    /**
     * base case
     */
    @Override
    public Res<SimpleMatrixValue> caseBuiltin(Builtin builtin,
            Args<SimpleMatrixValue> arg) {
        //ceal with constants
        if (arg.isAllConstant()){
            ArrayList<Constant> args = new ArrayList<Constant>();
            for (Value<SimpleMatrixValue> a : arg){
                args.add(a.getConstant());
            }
            Constant cResult = builtin.visit(constantProp, args);
            if (cResult != null){
                return Res.newInstance(new SimpleMatrixValue(cResult));
            }
        }
        
        
        //if it's not constant check whether the Builtin implements class propagation
        if (builtin instanceof HasClassPropagationInfo){
            if (DEBUG) System.out.println("case builtin: "+builtin
                    +" prop: "+((HasClassPropagationInfo)builtin).getMatlabClassPropagationInfo()
                    +" args: "+arg);
            LinkedList<HashSet<ClassReference>> matchResult = 
                ClassPropTool.matchByValues(((HasClassPropagationInfo)builtin).getMatlabClassPropagationInfo(),arg);
            if (matchResult == null){
                return Res.newErrorResult(builtin.getName()+" is not defined for arguments "+arg);
            }
            return matchResultToRes(matchResult);
        }
        //else throw an error
        throw new UnsupportedOperationException("No class propagation defined for builtin "+builtin);
    }
    
    private Res<SimpleMatrixValue> matchResultToRes(LinkedList<HashSet<ClassReference>> matchResult){
        //go through and fill in result
        Res<SimpleMatrixValue> result = Res.newInstance();
        for (HashSet<ClassReference> values: matchResult){
            HashMap<ClassReference,SimpleMatrixValue> map = new HashMap<ClassReference,SimpleMatrixValue>();
            if (values.size() == 0){
                throw new UnsupportedOperationException("encountered match result with 0 values");
            }
            for (ClassReference classRef : values){
                map.put(classRef,new SimpleMatrixValue((PrimitiveClassReference)classRef));
            }
            result.add(ValueSet.newInstance(map));
        }
        return result;
    }
    
    
    
    @Override
    public Res<SimpleMatrixValue> caseAbstractConcatenation(Builtin builtin,
            Args<SimpleMatrixValue> arg) {
        return Res.newInstance(
                new SimpleMatrixValue(
                        (PrimitiveClassReference)getDominantCatArgClass(arg)));
    }
    
    
    //TODO - move to cell prop
    @Override
    public Res<SimpleMatrixValue> caseCellhorzcat(Builtin builtin,
            Args<SimpleMatrixValue> elements) {
        ValueSet<SimpleMatrixValue> values = ValueSet.newInstance(elements);
        Shape<SimpleMatrixValue> shape = Shape.fromIndizes(this.factory, 
                Args.newInstance(factory.newMatrixValue(1),factory.newMatrixValue(elements.size())));
        return Res.newInstance(new CellValue<SimpleMatrixValue>(this.factory, shape, values));
    }
    @Override
    public Res<SimpleMatrixValue> caseCellvertcat(Builtin builtin,
            Args<SimpleMatrixValue> elements) {
        ValueSet<SimpleMatrixValue> values = ValueSet.newInstance(elements);
        Shape<SimpleMatrixValue> shape = Shape.fromIndizes(this.factory, 
                Args.newInstance(factory.newMatrixValue(elements.size()),factory.newMatrixValue(1)));
        return Res.newInstance(new CellValue<SimpleMatrixValue>(this.factory, shape, values));
    }
    
    @Override
    public Res<SimpleMatrixValue> caseCell(Builtin builtin,
            Args<SimpleMatrixValue> arg) {
        return Res.newInstance(new CellValue<SimpleMatrixValue>(
                this.factory, this.factory.newShapeFromIndizes(arg),ValueSet.<SimpleMatrixValue>newInstance()));
    }
}
