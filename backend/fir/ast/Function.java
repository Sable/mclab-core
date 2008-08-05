package fir.ast;
//TODO nested,global table,name?

import fir.table.*;
import fir.type.*;

public class Function extends Procedure {
	/** constructors ***********************************************/
	//from signature - puts block as body
	public Function(Signature signature,String name){
		//todo make sure signature is function compatible ...
		super(signature,new Table(),new Block(),name);
	}
	
	//from signature,table

	/** query *****************************************************/
	public PartiallyKnownType getReturnType(){
		return signature.getOutputArgumentType(0).getPartiallyKnownType();
	}
	public Stmt getBody(){
		return body;
	}
	public void setBody(Stmt body){
		this.body = body;
	}
	
}
