package natlab.tame.mc4.test;

import java.io.File;
import java.io.FilenameFilter;
import java.util.*;

import natlab.FlowAnalysisTestTool;
import natlab.tame.builtin.Builtin;
import natlab.tame.builtin.classprop.ClassPropTool;
import natlab.tame.callgraph.SimpleFunctionCollection;
import natlab.tame.callgraph.StaticFunction;
import natlab.tame.classes.reference.ClassReference;
import natlab.tame.classes.reference.PrimitiveClassReference;
import natlab.tame.valueanalysis.IntraproceduralValueAnalysis;
import natlab.tame.valueanalysis.ValueSet;
import natlab.tame.valueanalysis.aggrvalue.AggrValue;
import natlab.tame.valueanalysis.simplematrix.SimpleMatrixValue;
import natlab.tame.valueanalysis.value.*;
import natlab.toolkits.filehandling.GenericFile;
import natlab.toolkits.path.FileEnvironment;

public class Test {    
    static String testDir = "languages\\Natlab\\src\\natlab\\Static\\mc4\\test\\unit";
    private static List<GenericFile> getBenchmarksInDir(File dir){
        LinkedList<GenericFile> result = new LinkedList<GenericFile>();
        for (File f : dir.listFiles(new FilenameFilter() {
            LinkedList<GenericFile> result = new LinkedList<GenericFile>();
            public boolean accept(File adir, String name) {
                return name.endsWith(".m")
                && (name.toLowerCase().charAt(0) == name.charAt(0))
                && new File(adir,name.replaceFirst(".m", ".xml")).exists();
            }
        })){
            result.add(GenericFile.create(f));
        }
        return result;
    }
    
    public static List<GenericFile> getUnitTestsFiles(){
        LinkedList<GenericFile> result = new LinkedList<GenericFile>();
        File dir = new File(testDir);
        for (File d : dir.listFiles()){
            if (d.isDirectory()){
                result.addAll(getBenchmarksInDir(d));
                for (File d2 : d.listFiles()){
                    if (d2.isDirectory()){
                        result.addAll(getBenchmarksInDir(d2));
                    }
                }
            }
        }
        return result;
    }
    
    
    public static ResultValue.XMLResult getExpectedResult(GenericFile testFile){
        //read the expected matlab result from xml
        GenericFile xmlFile = GenericFile.create(testFile.getPath().replace(".m", ".xml"));
        ResultValue.XMLResult matlabResult = ResultValue.readResult(xmlFile);
        //System.out.println("matlab result "+matlabResult);
        return matlabResult;
    }
    
    public static Res<?> runTest(GenericFile testFile){        
        //load the unit test
        FileEnvironment fileEnvironment = new FileEnvironment(testFile);
        SimpleFunctionCollection functions = new SimpleFunctionCollection(fileEnvironment);
        StaticFunction aFunction = functions.getAsInlinedStaticFunction();
        IntraproceduralValueAnalysis<AggrValue<SimpleMatrixValue>> classAnalysis =
            new IntraproceduralValueAnalysis<AggrValue<SimpleMatrixValue>>(null,
                aFunction,SimpleMatrixValue.FACTORY,
                Args.<AggrValue<SimpleMatrixValue>>newInstance(new SimpleMatrixValue(null, PrimitiveClassReference.DOUBLE)));
        try{
            FlowAnalysisTestTool test = new FlowAnalysisTestTool(classAnalysis);
            String s = test.run(true,true);
            //System.out.println(s);
        } catch (UnsupportedOperationException e){
            System.err.println(e.getMessage());
            return Res.<AggrValue<SimpleMatrixValue>>newInstance();
        }
        System.out.println("test result for "+testFile.getName()+": "+classAnalysis.getResult());
        return classAnalysis.getResult();
    }
    
    
    public static class TestResult{
        //the analysis should be aware of the following errors
        public static HashSet<String> knownErrors = new HashSet<String>(Arrays.asList(new String[]{
                "MATLAB:TooManyInputs"
                ,"MATLAB:UndefinedFunction"
                ,"MATLAB:minrhs"
                //,"MATLAB:mixedClasses" //gets raised when mixing non-scalar
        }));
        
        String message = "";
        ResultType type;
        ResultValue.XMLResult expectedResult;
        enum ResultType{
            MATCH{{success = true;}},
            SUBSET_MATCH{{success = true;}},
            RESULT_MISMATCH{{success = false; }},
            ANAL_ERR_MATLAB_PASS{{success = false;}},
            PASSING_ANAL_PASS_MATLAB_ERR{{success = true;}},
            FAILING_ANAL_PASS_MATLAB_ERR{{success = false;}},
            ANAL_ERR_MATLAB_ERR{{success = true;}},
            ANAL_MULTIPLE_RETURN_VALUES{{success = false;}},
            EXCEPTION{{success = false;}},
            ANAL_EMPTY_RETURN{{success = false;}};
            boolean success;            
        }
        public boolean isSuccess(){ return type.success; }
        public TestResult(Res<?> result, ResultValue.XMLResult matlabResult){
            expectedResult = matlabResult;
            if (result.size() == 0){
                type = ResultType.EXCEPTION;
                message = type.toString();
            } else if (result.size() > 1){
                type = ResultType.ANAL_MULTIPLE_RETURN_VALUES;
                message = type.toString();
            } else {
                ValueSet<?> valueSet =  result.get(0);
                if (valueSet.size() == 0){
                    type = ResultType.ANAL_EMPTY_RETURN;
                    message = type.toString();
                }
                //Value<?> value = valueSet.iterator().next();
                else if (!result.isViable()){ //analysis error
                    if (!matlabResult.success){ //matlab error
                        type = ResultType.ANAL_ERR_MATLAB_ERR;
                    } else {
                        type = ResultType.ANAL_ERR_MATLAB_PASS;
                        message = ("-a expected:\n"+matlabResult+"\n-received:\n"+valueSet);
                    }
                } else { //analysis pass
                    if (matlabResult.success == true){ //matlab pass
                        if (valueSet.contains(matlabResult.resultValue.matlabClass)){
                            if (valueSet.size() == 1){
                                type = ResultType.MATCH;
                            } else {
                                type = ResultType.SUBSET_MATCH;
                            }
                        } else {
                            type = ResultType.RESULT_MISMATCH;
                            message = ("-b expected:\n"+matlabResult+"\n-received:\n"+valueSet);
                        }
                    } else { //matlab fail
                        //fail if the errors is among known errors
                        if (knownErrors.contains(expectedResult.error)){
                            message = ("-b expected:\n"+matlabResult.error+"-"+matlabResult.message+"\n-received:\n"+valueSet);
                            type = ResultType.FAILING_ANAL_PASS_MATLAB_ERR;
                        } else {
                            type = ResultType.PASSING_ANAL_PASS_MATLAB_ERR;
                        }
                    }
                }
            }
        }
    }
        
    public static void main(String[] args) {
        List<GenericFile> tests = getUnitTestsFiles();
        List<Res<?>> results = new LinkedList<Res<?>>();
        int failing = 0;
        int i = 0;
        for (GenericFile testFile : tests){
            System.out.println(testFile);
            results.add(runTest(testFile));
            //if (i++ == 1000) break;
        }
        //print results
        HashMap<String,Integer> resultStats = new HashMap<String,Integer>();
        HashMap<String,Integer> passingErrors = new HashMap<String,Integer>();
        for (i = 0; i < results.size();i++){
            TestResult testResult = new TestResult(results.get(i),getExpectedResult(tests.get(i)));
            if (!testResult.isSuccess()){
                failing++;
                System.out.println("failed: "+tests.get(i));
                System.out.println(testResult.message);
            } else {
                if (testResult.type == TestResult.ResultType.PASSING_ANAL_PASS_MATLAB_ERR){
                    String k = testResult.expectedResult.error;
                    if (!passingErrors.containsKey(k)) passingErrors.put(k,0);
                    passingErrors.put(k,passingErrors.get(k)+1);
                }
            }
            String resultName = testResult.type.toString();
            if (!resultStats.containsKey(resultName)) resultStats.put(resultName, 0);
            resultStats.put(resultName,resultStats.get(resultName)+1);
        }
        //print all errors
        for (String k : passingErrors.keySet()){
            System.out.println(k+" "+passingErrors.get(k));
        }
        
        System.out.println("");
        //print stats
        for (String k : resultStats.keySet()){
            System.out.println(k+": "+resultStats.get(k));
        }
        System.out.println("total: "+results.size()+" (passing "+(results.size()-failing)+"/failing "+failing+")");
    }
}
