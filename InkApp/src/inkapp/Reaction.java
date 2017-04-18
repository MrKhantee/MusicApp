package InkApp;

import GraphicsLib.G;
import GraphicsLib.Window;
import java.awt.Color;
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
		//enable();
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
    public Layer layer;
		
    public Mass(Layer layer){
      this.layer = layer;
      layer.add(this);
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
			layer.remove(this);
			clear();
		}
		
    public void enable(){
      if(!layer.contains(this)){
        layer.add(this);
      }
      super.enable();
    }
    
    public void disable(){
      layer.remove(this);
      super.disable();
    }
    
		//this is called by Undo
		public static void resetToDefault() {
			Reaction.clearMap();
			Layer.clearAll();
			if(DEFAULT != null) {
				DEFAULT.layer.add(DEFAULT);
				DEFAULT.enable();
			}
		}
	}
  
  public static abstract class Oto extends Button{
    public static ArrayList<Oto> all = new ArrayList<>();
    
    public Oto(String btnName, int x, int y){
      super(btnName, x, y);
      all.add(this);
    }
    
    public static void clearAll() {
      for(Oto o:all){
        //o.delete();
        o.disable();
      }
    }
    
    @Override
    public void show(Graphics g) {
        showInColor(g, UC.otoBackColor, UC.otoTextColor);
    }
  }
  
   public static abstract class Button extends Mass{
    public String btnName;
    public G.VS box;
    public int w = 1, h = 1, a;
    
    
    public Button(String btnName, int x, int y){
      super(Layer.FORE);
      this.btnName = btnName;
      if(Window.PANEL != null){
        Graphics g = Window.PANEL.getGraphics();
        w = g.getFontMetrics().stringWidth(btnName);
        h = g.getFontMetrics().getHeight();
        a = g.getFontMetrics().getAscent();
      }
      this.box = new G.VS(new G.V(x, y), new G.V(w,h));
      addReaction(new Reaction("DOT", "Button action") {
        public int bid(Stroke g) {
          if(box.contains(g.xm(), g.ym())){
            return 10;
          }else{return UC.noBid;}
        }
        public void act(Stroke g) {
         execute();
        }
      });
    }
    
    public void showInColor(Graphics g, Color bk, Color fg) {
      g.setFont(new Font("Comic Sans", Font.BOLD, 14));
      int s = g.getFont().getSize();
      if(Window.PANEL != null){ //patch to fix buttons contructed before PANEL exsists. 
        w = g.getFontMetrics().stringWidth(btnName);
        h = g.getFontMetrics().getHeight();
        a = g.getFontMetrics().getAscent();
        box.size = new G.V(w,h);
      }
      box.fill(g,bk);
      g.setColor(fg);
      g.drawString(btnName, box.loc.x, box.loc.y+a);
    }
    
    @Override
    public void show(Graphics g){
      showInColor(g, UC.btnBackColor, UC.btnTextColor);
    }

    public abstract void execute();
  }
  
}
