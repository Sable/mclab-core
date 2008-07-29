package fir.type;

public class InternalType {
	Intrinsic intrinsic;
	int[] shape;
	
	public InternalType(Intrinsic intrinsic,int[] shape){
		this.intrinsic = intrinsic;
		this.shape = shape;

	}
	public static InternalType getScalarInteger(){
		return new InternalType(new Integer(),new int[0]);
	}
	
}
