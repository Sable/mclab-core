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
	private Map<LoopBounds,String> rMap;
	public RangeMap()
	{
		rMap=new HashMap<LoopBounds,String>(); //create a new hash map;		
	}
	public void createMapping(LoopBounds lBounds,String fileName)
	{		
		rMap.put(lBounds, fileName);
	}

}
