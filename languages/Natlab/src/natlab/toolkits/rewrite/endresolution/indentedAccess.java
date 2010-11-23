package natlab.toolkits.rewrite.endresolution;

public class indentedAccess {
	private String name;
	private int pos;
	private int dims;
	
	public indentedAccess(String callee,int pos,int dims){
		this.name = callee;
		this.pos = pos;
		this.dims = dims;	
	}
	public indentedAccess(String callee){
		this.name = callee;
		this.pos = 0;
		this.dims = 0;
	}
	
	public String getName(){
		return this.name;
	}
	
	
	public int getPos(){
		return this.pos;
	}
	
	public int getDims(){
		return this.dims;
	}
	public void setPos(int pos){
		this.pos = pos;
	}
	public void setDims(int dims){
		this.dims = dims;
	}
}
