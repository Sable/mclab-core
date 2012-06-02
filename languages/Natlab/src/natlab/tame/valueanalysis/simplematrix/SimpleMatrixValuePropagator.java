package natlab.tame.valueanalysis.simplematrix;

import java.util.*;
import natlab.tame.builtin.*;
import natlab.tame.classes.reference.*;
import natlab.tame.valueanalysis.*;
import natlab.tame.valueanalysis.aggrvalue.*;
import natlab.tame.valueanalysis.components.constant.*;
import natlab.tame.valueanalysis.components.mclass.ClassPropagator;
import natlab.tame.valueanalysis.components.shape.Shape;
import natlab.tame.valueanalysis.value.*;

public class SimpleMatrixValuePropagator extends MatrixPropagator<SimpleMatrixValue>{
    public static boolean DEBUG = false;
    ConstantPropagator<AggrValue<SimpleMatrixValue>> constantProp = ConstantPropagator.getInstance();
    
    public SimpleMatrixValuePropagator() {
        super(new SimpleMatrixValueFactory());
    }

    /**
     * base case
     */
    
    @Override
    //XU add this function to support...doesn't change anything of class propagation!
    public Res<AggrValue<SimpleMatrixValue>> caseBuiltin(Builtin builtin,
            Args<AggrValue<SimpleMatrixValue>> arg,int num) {
        //ceal with constants
    	Constant cResult = builtin.visit(constantProp, arg, num);
    	if (cResult != null){
    		return Res.<AggrValue<SimpleMatrixValue>>newInstance(new SimpleMatrixValue(cResult));
    	}
    	
    	//if the result is not a constant, just do mclass propagation
        List<Set<ClassReference>> matchResult = 
                builtin.visit(ClassPropagator.<AggrValue<SimpleMatrixValue>>getInstance(),arg, num);
        if (matchResult == null){ //class prop returned error
            return Res.newErrorResult(builtin.getName()+" is not defined for arguments "+arg);
        }
    	
        //build results out of the result classes
        return matchResultToRes(matchResult);
    }
    
    
    private Res<AggrValue<SimpleMatrixValue>> matchResultToRes(List<Set<ClassReference>> matchResult){
        //go through and fill in result
        Res<AggrValue<SimpleMatrixValue>> result = Res.newInstance();
        for (Set<ClassReference> values: matchResult){
            HashMap<ClassReference,AggrValue<SimpleMatrixValue>> map = new HashMap<ClassReference,AggrValue<SimpleMatrixValue>>();
            for (ClassReference classRef : values){
                map.put(classRef,new SimpleMatrixValue((PrimitiveClassReference)classRef));
            }
            result.add(ValueSet.newInstance(map));
        }
        return result;
    }
    
    
    
    @Override
    public Res<AggrValue<SimpleMatrixValue>> caseAbstractConcatenation(Builtin builtin,
            Args<AggrValue<SimpleMatrixValue>> arg, int num) {
        return Res.<AggrValue<SimpleMatrixValue>>newInstance(
                new SimpleMatrixValue(
                        (PrimitiveClassReference)getDominantCatArgClass(arg)));
    }
    
    
    //TODO - move to aggr value prop
    @Override
    public Res<AggrValue<SimpleMatrixValue>> caseCellhorzcat(Builtin builtin,
            Args<AggrValue<SimpleMatrixValue>> elements, int num) {
        ValueSet<AggrValue<SimpleMatrixValue>> values = ValueSet.newInstance(elements);
        Shape<AggrValue<SimpleMatrixValue>> shape = factory.getShapeFactory().newShapeFromValues( 
                Args.newInstance(factory.newMatrixValue(1),factory.newMatrixValue(elements.size())));
        return Res.<AggrValue<SimpleMatrixValue>>newInstance(new CellValue<SimpleMatrixValue>(this.factory, shape, values));
    }
    @Override
    public Res<AggrValue<SimpleMatrixValue>> caseCellvertcat(Builtin builtin,
            Args<AggrValue<SimpleMatrixValue>> elements, int num) {
        ValueSet<AggrValue<SimpleMatrixValue>> values = ValueSet.newInstance(elements);
        Shape<AggrValue<SimpleMatrixValue>> shape = factory.getShapeFactory().newShapeFromValues(
                Args.newInstance(factory.newMatrixValue(elements.size()),factory.newMatrixValue(1)));
        return Res.<AggrValue<SimpleMatrixValue>>newInstance(new CellValue<SimpleMatrixValue>(this.factory, shape, values));
    }
    
    @Override
    public Res<AggrValue<SimpleMatrixValue>> caseCell(Builtin builtin,
            Args<AggrValue<SimpleMatrixValue>> arg, int num) {
        return Res.<AggrValue<SimpleMatrixValue>>newInstance(new CellValue<SimpleMatrixValue>(
                this.factory, factory.getShapeFactory().newShapeFromValues(arg),ValueSet.<AggrValue<SimpleMatrixValue>>newInstance()));
    }
}



