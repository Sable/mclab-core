package fir.codegen;
//a Uid is a string, which is unique within a CodeGen object

public class Uid {
	String uid;
	public String getString(){return uid;};
	public Uid(CodeGen codeGen,String name){
		uid = name+"_"+codeGen.getUniqueNumber(); //TODO - check whether '_' is allowed		
	}
	public Uid(CodeGen codeGen){
		uid = new Uid(codeGen,"uid").uid;		
	}

}
