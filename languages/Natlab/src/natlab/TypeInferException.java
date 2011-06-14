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

package natlab;

public class TypeInferException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public Object node;
	public TypeInferException() {
		// TODO Auto-generated constructor stub
	}

	public TypeInferException(String arg0) {
		super(arg0);
		// TODO Auto-generated constructor stub
	}

	public TypeInferException(Throwable arg0) {
		super(arg0);
		// TODO Auto-generated constructor stub
	}

	public TypeInferException(String arg0, Throwable arg1) {
		super(arg0, arg1);
		// TODO Auto-generated constructor stub
	}
	// Passing some object back, such as node pointer, 
	public TypeInferException(String arg0, Object node) {
		super(arg0);
		this.node = node; 
	}
}
