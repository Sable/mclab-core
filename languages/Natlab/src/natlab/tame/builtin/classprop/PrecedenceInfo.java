package natlab.tame.builtin.classprop;

import java.util.HashMap;

import natlab.tame.builtin.classprop.ast.CP;
import natlab.tame.builtin.classprop.ast.CPAny;
import natlab.tame.builtin.classprop.ast.CPBegin;
import natlab.tame.builtin.classprop.ast.CPBuiltin;
import natlab.tame.builtin.classprop.ast.CPChain;
import natlab.tame.builtin.classprop.ast.CPCoerce;
import natlab.tame.builtin.classprop.ast.CPEnd;
import natlab.tame.builtin.classprop.ast.CPError;
import natlab.tame.builtin.classprop.ast.CPMap;
import natlab.tame.builtin.classprop.ast.CPNone;
import natlab.tame.builtin.classprop.ast.CPNum;
import natlab.tame.builtin.classprop.ast.CPPackaged;
import natlab.tame.builtin.classprop.ast.CPScalar;
import natlab.tame.builtin.classprop.ast.CPTypeString;
import natlab.tame.builtin.classprop.ast.CPUnion;

/**
 * a class that just give the precedence info for ast nodes.
 * This is in a separate class to keep things in one place, but maybe should be moved
 * into the respective ast nodes.
 */
public class PrecedenceInfo {
	static private HashMap<Class<? extends CP>, Integer> pMap = new HashMap<Class<? extends CP>, Integer>();
	static{
		pMap.put(CPAny.class, 0);
		pMap.put(CPBegin.class, 0);
		pMap.put(CPBuiltin.class, 0);
		pMap.put(CPEnd.class, 0);
		pMap.put(CPError.class, 0);
		pMap.put(CPNone.class, 0);
		pMap.put(CPNum.class, 0);
		pMap.put(CPPackaged.class, 0);
		pMap.put(CPScalar.class, 0);

		pMap.put(CPCoerce.class, 0);
		pMap.put(CPTypeString.class, 0);

		pMap.put(CPUnion.class, -1);
		pMap.put(CPChain.class, -2);
		pMap.put(CPMap.class, -3);
	}
	
	
	public static int getPrecedence(Class<? extends CP> astClass){
		if (!pMap.containsKey(astClass)) return 0;
		return pMap.get(astClass);
	}
}
