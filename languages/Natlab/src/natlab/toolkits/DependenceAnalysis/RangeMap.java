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
import java.util.HashMap;
import java.util.Map;
/*
 * Author:Amina Aslam
 * Date:24 Aug,2009
 * RangeMap class stores the mapping between loop bounds and results file. 
 * 
 */

public class RangeMap {
	private Map<ProfiledLoopBounds,String> rMap;
	public RangeMap()
	{
		rMap=new HashMap<ProfiledLoopBounds,String>(); //create a new hash map;		
	}
	public void createMapping(ProfiledLoopBounds lBounds,String fileName)
	{		
		rMap.put(lBounds, fileName);
	}

}
