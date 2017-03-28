package InkApp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public abstract class Reaction implements I.React{
	public Stroke.Shape shape;
	public String purpose;
	public static Map<Stroke.Shape, Reaction.List> shapeMap = new HashMap<>();
	
	public Reaction(String name, String purp) {
		this.purpose = purp;
		this.shape = Stroke.Shape.byName(name);
		enable();
	}
	
	public void enable() {
		Reaction.List rl = shapeMap.get(this.shape);
		if(rl == null) {
			rl = new Reaction.List();
			shapeMap.put(this.shape, rl);
		}
		rl.add(this);
	}
	
	public void disable() {
		Reaction.List rl = shapeMap.get(this.shape);
		if(rl != null) {
			rl.remove(this);
		} 
	}
	
	public static Reaction bestReaction(Stroke g) {
		Reaction.List rl = shapeMap.get(g.shape);
		if(rl == null) {
//			System.out.println("didnt find any reaction");
			return null;
		} else {
//			System.out.println("Reaction List size: " + rl.size());
			return rl.bestReaction(g);
		}
	}
	
	public static void clearMap() {
		shapeMap.clear();
	}
	
	public static class List extends ArrayList<Reaction>{
		
		public Reaction bestReaction(Stroke g) {
			Reaction res = null;
			int bestBid = UC.noBid;
			for(Reaction r : this) {
				int b = r.bid(g);
				if(b<bestBid) {
					bestBid = b;
					res = r;
				}
			}
			return res;
		}
		
		public void enable() {
			for(Reaction r : this) {
//				System.out.println("Enabling : "+ r.shape.name + "purp:" + r.purpose);
				r.enable();
			}
		}
		
		public void disable() {
			for(Reaction r : this) {
				r.disable();
			}
		}
	}
	
	public static abstract class Mass extends Reaction.List implements I.Show{
		public static Mass DEFAULT = null;
		
		public Layer getLayer() {
			return Layer.BACK;
		}
		
		@Override
		public void clear() {
			this.disable();
			super.clear();
		}
		
		@Override
		public boolean add(Reaction r) {
			return addReaction(r);
		}
		
		public boolean addReaction(Reaction r) {
			r.enable();
			return super.add(r);
		}
		
		public void removeReaction(Reaction r) {
			r.disable();
			super.remove(r);
		}
		
		public void delete() {
			getLayer().remove(this);
			clear();
		}
		
		//this is called by Undo
		public static void resetToDefault() {
			Reaction.clearMap();
			Layer.clearAll();
			if(DEFAULT != null) {
				DEFAULT.getLayer().add(DEFAULT);
				DEFAULT.enable();
			}
		}
	}
}
