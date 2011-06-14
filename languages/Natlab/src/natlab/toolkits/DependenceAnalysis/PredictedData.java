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

public class PredictedData {
	private float loopNo;
	private int lowerBound;
	private int upperBound;
	private int loopIncFactor;
	private String lVName;
	public void setLoopNo(float loopNo) {
		this.loopNo = loopNo;
	}
	public void setLowerBound(int lowerBound) {
		this.lowerBound = lowerBound;
	}
	public void setUpperBound(int upperBound) {
		this.upperBound = upperBound;
	}
	public void setLoopIncFactor(int loopIncFactor) {
		this.loopIncFactor = loopIncFactor;
	}
	public float getLoopNo() {
		return loopNo;
	}
	public int getLowerBound() {
		return lowerBound;
	}
	public int getUpperBound() {
		return upperBound;
	}
	public int getLoopIncFactor() {
		return loopIncFactor;
	}
	public String getLVName() {
		return lVName;
	}
	public void setLVName(String name) {
		lVName = name;
	}
	

}
