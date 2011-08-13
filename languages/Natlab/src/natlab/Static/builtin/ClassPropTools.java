package natlab.Static.builtin;
/**
 * tools for building class propagation information for builtins.
 * This works together with the class propagation tag of the builtins.csv,
 * and implements the java backend for the tiny class propagation langauge
 * @author ant6n
 */


public class ClassPropTools {

    
    
    /**
     * matlab classes 
     */
    //represents a general matlab class in the class propagation language
    public static abstract class MC{
    }
    
    public static  class MCBuiltin extends MC{
        String name;
        public MCBuiltin(String name){this.name = name;}
        public String toString() {return name;}
    }
    
    //class1 | class2
    public static  class MCUnion extends MC{
        MC class1,class2;
        public MCUnion(MC class1, MC class2){
            this.class1 = class1; this.class2 = class2;
        }
        public String toString() {return class1+"|"+class2;}        
    }
    //class1 & class2
    public static class MCChain extends MC{
        MC class1,class2;
        public MCChain(MC class1, MC class2){
            this.class1 = class1; this.class2 = class2;
        }
        public String toString() {return "("+class1+")&("+class2+")";}        
    }
    //class1 > class2
    public static class MCMap extends MC{
        MC class1,class2;
        public MCMap(MC class1, MC class2){
            this.class1 = class1; this.class2 = class2;
        }
        public String toString() {return "("+class1+">"+class2+")";}        
    }
    //<n>
    public static class MCNum extends MC{
        int num;
        public MCNum(int num){this.num = num;}
        public String toString() { return num+""; }
    }
    
    /**
     * main for testing
     */
    public static void main(String[] args) {
        MC dbl = new MCBuiltin("double");
        MC chr = new MCBuiltin("char");
        MC itg = new MCBuiltin("int");
        MC log = new MCBuiltin("logical");
        
        MC a = new MCMap(new MCUnion(dbl,chr),new MCChain(log,itg));
        System.out.println(a);
        MC b = new MCChain(new MCMap(dbl,dbl),new MCMap(itg,itg));
        System.out.println(b);
        MC c = new MCUnion(new MCMap(dbl,dbl),new MCMap(new MCChain(new MCUnion(itg,dbl),dbl),chr));
        System.out.println(c);
    }
}

