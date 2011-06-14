// =========================================================================== //
//                                                                             //
// Copyright 2011 Amina Aslam and McGill University.                           //
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
//  limitations under the License.                                             //
//                                                                             //
// =========================================================================== //

package natlab.toolkits.DependenceAnalysis;

public class ProfiledData {
	private float loopNo;
	private ProfiledLowerBound lBound;
	private ProfiledLIF loopIncFac;
	private UpperBound uBound;
	private String lVName;//This stores the loop variable name
	
	public String getLVName() {
		return lVName;
	}
	public void setLVName(String name) {
		lVName = name;
	}
	public float getLoopNo() {
		return loopNo;
	}
	public void setLoopNo(float loopNo) {
		this.loopNo = loopNo;
	}
	public ProfiledLowerBound getLBound() {
		//return lBound=new LowerBound();
		return lBound;
	}
	public void setLBound(ProfiledLowerBound bound) {
		lBound = bound;
	}
	public ProfiledLIF getLoopIncFac() {
		//return loopIncFac=new LIF();
		return loopIncFac;
	}
	public void setLoopIncFac(ProfiledLIF loopIncFac) {
		this.loopIncFac = loopIncFac;
	}
	public UpperBound getUBound() {
		//return uBound=new UpperBound();
		return uBound;
	}
	public void setUBound(UpperBound bound) {
		uBound = bound;
	}	
	public UpperBound getNewUBound() {
		return uBound=new UpperBound();
	}
	public ProfiledLowerBound getNewLBound() {
		return lBound=new ProfiledLowerBound();
	}
	public ProfiledLIF getNewLoopIncFac() {
		return loopIncFac=new ProfiledLIF();		
	}


	

}//end of InputData
