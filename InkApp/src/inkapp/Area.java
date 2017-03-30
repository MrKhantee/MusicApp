package InkApp;

import java.awt.Color;
import java.util.ArrayList;

import GraphicsLib.G.VS;

public class Area {
	public VS rec;
	public Color color;
	public static Area active = null;
	public static List all = new List(); 
	public static boolean inking = false;
	public static Area DEFAULT = new GestureArea();
	
	public Area(VS vs, Color c, String s) {
		this.rec = vs;
		this.color = c;
		all.add(this);
		System.out.println("Comstructing area " + s + " size is: " + all.size());
	}
	
	public boolean hit (int x, int y) {
		return rec.contains(x, y);
	}
	
	public static void DN(int x, int y) {
		all.getActive(x, y);
		if (active == null) {return;}
		active.dn(x, y);
		if(inking) {
			Ink.BUFFER.addFirst(x,y);
		}
	}
	
	public static void UP(int x, int y) {
		if (active != null) {
			if(inking) {
				Ink.BUFFER.addLast(x, y);
			}
			inking = false;
			active.up(x, y);
			active = null;
			Ink.BUFFER.clear();
		}
	}
	
	public static void DRAG(int x, int y) {
		if(active != null) {
			if(inking) {
				Ink.BUFFER.add(x, y);
			}
			active.drag(x, y);
		}
	}
	
	public static void resetToDefault() {
		all.clear();
		if(DEFAULT != null) {
			all.add(DEFAULT);
		}
	}
	
	public void dn(int x, int y) {}
	public void up(int x, int y) {}
	public void drag(int x, int y) {}
	
	
	public static class List extends ArrayList<Area> {
		public void getActive(int x, int y) {
			active = null;
			for(int i = size()-1; i >=0; i--) {
				if(get(i).hit(x, y)) {
					active = get(i);
					return;
				}
			}
		}
	}
	
	public static class GestureArea extends Area {

		public GestureArea() {
			super(UC.bigVS, Color.white, "GestureArea");
		}
		
		public void dn(int x, int y) {
			Area.inking = true;
		}
    
		public void up(int x, int y) {
			Stroke g = new Stroke(); // created from the ink
		//	System.out.println("GestureArea class running.");
		//	System.out.println(g.shape.name);
			
			Reaction reaction = Reaction.bestReaction(g);
			if(reaction != null) {
				System.out.println(reaction.purpose);
				Undo.add(g);
				reaction.act(g);
        Reaction.Oto.clearAll();
			}		
//			System.out.println("**" + Undo.UNDO.list.size());
//			g.act(g);
		}
		public void drag(int x, int y) {}
	}
}
