// =========================================================================== //
//                                                                             //
// Copyright 2008-2011 Andrew Casey, Jun Li, Jesse Doherty,                    //
//   Maxime Chevalier-Boisvert, Toheed Aslam, Anton Dubrau, Nurudeen Lameed,   //
//   Amina Aslam, Rahul Garg, Soroush Radpour, Olivier Savary Belanger,        //
//   Laurie Hendren, Clark Verbrugge and McGill University.                    //
//                                                                             //
//   Licensed under the Apache License, Version 2.0 (the "License");           //
//   you may not use this file except in compliance with the License.          //
//   You may obtain a copy of the License at                                   //
//                                                                             //
//       http://www.apache.org/licenses/LICENSE-2.0                            //
//                                                                             //
//   Unless required by applicable law or agreed to in writing, software       //
//   distributed under the License is distributed on an "AS IS" BASIS,         //
//   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  //
//   See the License for the specific language governing permissions and       //
//   limitations under the License.                                            //
//                                                                             //
// =========================================================================== //

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
