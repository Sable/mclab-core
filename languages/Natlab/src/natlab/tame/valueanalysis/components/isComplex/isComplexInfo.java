package natlab.tame.valueanalysis.components.isComplex;

import natlab.toolkits.analysis.Mergable;


public class isComplexInfo implements Mergable<isComplexInfo> {
	static boolean Debug = false;

	String icType;                       
	boolean isTop = false;
	boolean isError = false;
	
	public isComplexInfo(String icType)
	{

		this.icType = icType;
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
    
    public boolean equals(isComplexInfo o){
    	if(this.geticType().equals(o.geticType())) {
    		return true;
    	}
    	else
    	return false;//FIXME
    }
	
	@Override
	public isComplexInfo merge(isComplexInfo o) {
		
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
		
		return new isComplexInfo(afterMerge);
	}
	
	public int hashCode() {
		return icType.hashCode();
	}

}


