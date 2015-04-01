package natlab.tame.valueanalysis.components.isComplex;

import natlab.tame.valueanalysis.value.Value;
import natlab.tame.valueanalysis.value.ValueFactory;
import natlab.toolkits.analysis.Mergable;


public class isComplexInfo<V extends Value<V>> implements Mergable<isComplexInfo<V>> {
	static boolean Debug = false;
	private ValueFactory<V> factory;
	String icType;                       
	boolean isTop = false;
	boolean isError = false;
	
	public isComplexInfo(ValueFactory<V> factory, String icType)
	{
		this.factory = factory;
		this.icType = icType;
		if (icType.equals("ANY")) flagItsTop();
			
	}
	
	public isComplexInfo(String icType)
	{
	//	this.factory = factory;
		this.icType = icType;
	//	newisComplexInfoFromStr(icType);
		if (icType.equals("ANY")) flagItsTop();
			
	}
	
	public String geticType()
	{
		return icType;
	}
	
	public String toString(){
    	if(this.isTop==true){
    		return "ANY";
    	}
    	else if(this.isError==true){
    		return "[MATLAB syntax error, check your code]";
    	}
    	else{
    		return icType;
    	}
    }
	
	public void printisComplexInfo()
	{
		System.out.println(icType);
	}
	
	public void flagItsTop(){
    	this.isTop=true;
    }
    
    public void flagItsError(){
    	this.isError=true;
    }
    
    public boolean equals(isComplexInfo<V> o){
    	if(this.geticType()==o.geticType()){
    		return true;
    	}
    	else
    	return false;//FIXME
    }
	
	@Override
	public isComplexInfo<V> merge(isComplexInfo<V> o) {
		
		String afterMerge="ANY";
		if(this.equals(o)){
    		return this;
    	}
		
		
		
		
		else
		{
			
			if (this.geticType().equals("ANY") || !this.geticType().equals(o.geticType()))
			{
				afterMerge = "ANY";
			}
			else if (this.geticType().equals(o.geticType()))
			{
				afterMerge = this.geticType();
			}
		}
		
		return new isComplexInfo<V>(this.factory, afterMerge);
	}

}


