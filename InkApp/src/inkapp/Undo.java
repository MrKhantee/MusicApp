package InkApp;

import java.util.ArrayList;


public class Undo {
	public ArrayList<I.Act> list = new ArrayList<>();
	public static Reaction.List INITIAL_REACTIONS = new Reaction.List();
	public static Undo UNDO = new Undo();
	
	private Undo() {
		addInitialReaction(new Reaction("N-N","Undo") {
			public int bid(Stroke g) {
				return UC.undoBid;
			}
			public void act(Stroke g) {
				undo();
			}
		});
	}
	
	public static void addInitialReaction(Reaction r) {
		INITIAL_REACTIONS.add(r);
		r.enable();
	}
	
	public static void undo() {
    System.out.println("UNDO!");
		UNDO.list.remove(last()); // remove the undo stroke itself
		if(!UNDO.list.isEmpty()) {
			UNDO.list.remove(last());
		}
    Area.resetToDefault();
		Reaction.Mass.resetToDefault();
		INITIAL_REACTIONS.enable();
		for(I.Act a: UNDO.list) {
			if(a instanceof Stroke) {
				a.act((Stroke)a);
			} else {
				a.act(null);
			}
		}
	}
	
	public static int last() {
		return UNDO.list.size()-1;
	}

	public static void add(I.Act a) {
		UNDO.list.add(a);
	}
}
