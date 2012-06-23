package natlab.tame.interproceduralAnalysis.examples.live;

import natlab.toolkits.analysis.Mergable;

public class LiveValue implements Mergable<LiveValue>{
	public LiveState state;
	
	public static enum LiveState{
		MAY_BE_LIVE,
		IS_LIVE,
		DEAD;
		
		public LiveState merge(LiveState other){
			if (this == DEAD && other == DEAD) return DEAD;
			if (this == IS_LIVE && other == IS_LIVE) return IS_LIVE;
			return MAY_BE_LIVE;
		}
	}
	
	public LiveValue(LiveState state){
		this.state = state;
	}
	
	static public LiveValue getLive(){ return new LiveValue(LiveState.IS_LIVE); }
	static public LiveValue getMaybeLive(){ return new LiveValue(LiveState.MAY_BE_LIVE); }
	static public LiveValue getDead(){ return new LiveValue(LiveState.DEAD); }
	
	@Override
	public LiveValue merge(LiveValue o) {
		return new LiveValue(o.state.merge(this.state));
	}
	public LiveState getState(){
		return state;
	}
	@Override
	public String toString() {
		return state.toString();
	}
	
	/**
	 * for example for the snippet of code
	 *   a = 3;
	 *   foo(a)
	 *   bar(a)
	 * when doing a backward analysis, a may or may not be live before
	 * the call to bar. It may or may not be live before the call to foo.
	 * The reRead method returns a live value for before the call for foo,
	 * given a live value for after the call to foo, and the live value
	 * do to the call to foo. It is analoguous to a merge operation, except
	 * that actual merges happen at confluence points, and reread values
	 * happen when variables are used multiple times in a row.
	 */
	public LiveValue reRead(LiveValue newLiveValue){
		if (newLiveValue.state == LiveState.IS_LIVE || state == LiveState.IS_LIVE)
			return newLiveValue;
		if (newLiveValue.state == LiveState.DEAD && state == LiveState.DEAD)
			return this;
		return getMaybeLive();
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof LiveValue) return ((LiveValue)obj).state.equals(state);
		return false;
	}
	@Override
	public int hashCode() {
		return state.hashCode();
	}
}
