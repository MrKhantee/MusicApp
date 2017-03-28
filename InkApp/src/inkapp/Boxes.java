package InkApp;

import java.awt.Color;
import java.awt.Graphics;
import GraphicsLib.G.V;

public class Boxes extends Reaction.Mass{
	public static final int XS = 40, YS = 25;
	public static Reaction box = new Reaction("SW-SW","box") {
		public int bid(Stroke g) {
			return 10;
		}
		public void act(Stroke g) {
			new Box(false, g.vs.xm(), g.vs.ym());
		}
	};
	
	public Boxes() {
		this.add(box);
		Reaction.Mass.DEFAULT = this;
		Layer.BACK.add(this);
		addReaction(box);
	}
	
	@Override
	public void show(Graphics g) {
		UC.bigVS.fill(g, Color.white);
		g.setColor(Color.black);
		g.drawString("BOXES", 100, 100);
	}
	
	public static class Box extends Reaction.Mass{
		public boolean isOval = false;
		public V center;
		public Box(boolean b, int x, int y) {
			isOval = b;
			center = new V(x, y);
			getLayer().add(this);
		}
		
		@Override
		public void show(Graphics g) {
			g.setColor(Color.red);
			if(!isOval){
				g.drawRect(center.x-XS, center.y-YS, 2*XS, 2*YS);
			} else {
				g.drawOval(center.x-XS, center.y-YS, 2*XS, 2*YS);
			}
		}
	}
	
}
