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
