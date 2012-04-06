package natlab.tame.valueanalysis.simplematrix;

import java.util.*;
import natlab.tame.builtin.*;
import natlab.tame.builtin.classprop.*;
import natlab.tame.classes.reference.*;
import natlab.tame.valueanalysis.*;
import natlab.tame.valueanalysis.aggrvalue.*;
import natlab.tame.valueanalysis.components.constant.Constant;
import natlab.tame.valueanalysis.components.constant.ConstantPropagator;
import natlab.tame.valueanalysis.components.shape.Shape;
import natlab.tame.valueanalysis.value.*;

public class SimpleMatrixValuePropagator extends AggrValuePropagator<SimpleMatrixValue>{
    public static boolean DEBUG = false;
    ConstantPropagator<AggrValue<SimpleMatrixValue>> constantProp = ConstantPropagator.getInstance();
    
    public SimpleMatrixValuePropagator() {
        super(new SimpleMatrixValueFactory());
    }

    /**
     * base case
     */
    @Override
    public Res<AggrValue<SimpleMatrixValue>> caseBuiltin(Builtin builtin,
            Args<AggrValue<SimpleMatrixValue>> arg) {
        //ceal with constants
        if (arg.isAllConstant()){ //FIXME
            Constant cResult = builtin.visit(constantProp, arg);
            if (cResult != null){
                return Res.<AggrValue<SimpleMatrixValue>>newInstance(new SimpleMatrixValue(cResult));
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
    
    private Res<AggrValue<SimpleMatrixValue>> matchResultToRes(LinkedList<HashSet<ClassReference>> matchResult){
        //go through and fill in result
        Res<AggrValue<SimpleMatrixValue>> result = Res.newInstance();
        for (HashSet<ClassReference> values: matchResult){
            HashMap<ClassReference,AggrValue<SimpleMatrixValue>> map = new HashMap<ClassReference,AggrValue<SimpleMatrixValue>>();
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
    public Res<AggrValue<SimpleMatrixValue>> caseAbstractConcatenation(Builtin builtin,
            Args<AggrValue<SimpleMatrixValue>> arg) {
        return Res.<AggrValue<SimpleMatrixValue>>newInstance(
                new SimpleMatrixValue(
                        (PrimitiveClassReference)getDominantCatArgClass(arg)));
    }
    
    
    //TODO - move to aggr value prop
    @Override
    public Res<AggrValue<SimpleMatrixValue>> caseCellhorzcat(Builtin builtin,
            Args<AggrValue<SimpleMatrixValue>> elements) {
        ValueSet<AggrValue<SimpleMatrixValue>> values = ValueSet.newInstance(elements);
        Shape<AggrValue<SimpleMatrixValue>> shape = factory.getShapeFactory().newShapeFromValues( 
                Args.newInstance(factory.newMatrixValue(1),factory.newMatrixValue(elements.size())));
        return Res.<AggrValue<SimpleMatrixValue>>newInstance(new CellValue<SimpleMatrixValue>(this.factory, shape, values));
    }
    @Override
    public Res<AggrValue<SimpleMatrixValue>> caseCellvertcat(Builtin builtin,
            Args<AggrValue<SimpleMatrixValue>> elements) {
        ValueSet<AggrValue<SimpleMatrixValue>> values = ValueSet.newInstance(elements);
        Shape<AggrValue<SimpleMatrixValue>> shape = factory.getShapeFactory().newShapeFromValues(
                Args.newInstance(factory.newMatrixValue(elements.size()),factory.newMatrixValue(1)));
        return Res.<AggrValue<SimpleMatrixValue>>newInstance(new CellValue<SimpleMatrixValue>(this.factory, shape, values));
    }
    
    @Override
    public Res<AggrValue<SimpleMatrixValue>> caseCell(Builtin builtin,
            Args<AggrValue<SimpleMatrixValue>> arg) {
        return Res.<AggrValue<SimpleMatrixValue>>newInstance(new CellValue<SimpleMatrixValue>(
                this.factory, factory.getShapeFactory().newShapeFromValues(arg),ValueSet.<AggrValue<SimpleMatrixValue>>newInstance()));
    }
}



