package InkApp;

import GraphicsLib.G;
import java.awt.Font;
import java.awt.Graphics;
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
		
		public abstract Layer getLayer();
		
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
  
  public static class Oto extends Mass{
    public String btnName;
    public G.VS box;
    public static ArrayList<Oto> all = new ArrayList<>();
    
    public Oto(String btnName, int x, int y, Oto.Action oa){
      this.btnName = btnName;
      this.box = new G.VS(new G.V(x, y), new G.V(1,1));
      addReaction(new Reaction("DOT", "Oto action") {

        public int bid(Stroke g) {
          if(box.contains(g.xm(), g.ym())){
            return 10;
          }else{return UC.noBid;}
        }
        public void act(Stroke g) {
         oa.execute();
        }
      });
      all.add(this);
      getLayer().add(this);
    }
    
    public static void clearAll() {
      for(Oto o:all){
        o.delete();
      }
    }
    
    @Override
    public void show(Graphics g) {
      g.setFont(new Font("Comic Sans", Font.BOLD, 14));
      int s = g.getFont().getSize();
      int w = g.getFontMetrics().stringWidth(btnName);
      int h = g.getFontMetrics().getHeight();
      int a = g.getFontMetrics().getAscent();
      box.size = new G.V(w,h);
      box.fill(g, UC.otoBackColor);
      g.setColor(UC.otoTextColor);
      g.drawString(btnName, box.loc.x, box.loc.y+a);
    }

    @Override
    public Layer getLayer() {
      return Layer.FORE;
    }

    public static abstract class Action { public abstract void execute();}
  }
}
