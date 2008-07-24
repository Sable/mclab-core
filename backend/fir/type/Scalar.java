package fir.type;
//a scalar is a rank 0 array

public class Scalar extends KnownShape {
	public int getRank() {return 0;}
	public int[] getShape() {return new int[0];}
}

