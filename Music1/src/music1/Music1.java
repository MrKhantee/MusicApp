package Music1;

import java.awt.Color;
import java.awt.Graphics;

import org.omg.PortableServer.ID_ASSIGNMENT_POLICY_ID;

import GraphicsLib.G.V;
import GraphicsLib.G.VS;
import InkApp.Stroke;
import InkApp.Layer;
import InkApp.Reaction;
import InkApp.Reaction.Mass;
import InkApp.UC;
import InkApp.Ink.NamedInk;

public class Music1 extends Mass{
	public static final int marX = 50, MarX = 750, H = 8;
	public static Layer staffs = Layer.getNewLayer();
	public static Layer bars = Layer.getNewLayer();
	public static VS tempBox = new VS(new V(), new V(H,H));
	public static String[] clefNames= {"g-clef", "f-clef","c-clef"};
  
	public Music1() {
		Mass.DEFAULT = this;
		addReaction(new Reaction("E-E","add-staff"){

			public int bid(Stroke g) {
				if(staffs.isEmpty()) {
					return 100;
				} else {
					Staff last = (Staff) staffs.get(staffs.size()-1);
					return (g.vs.loc.y > last.y + 12*H) ? 100 : UC.noBid ;
				}
			}

			public void act(Stroke g) {
				new Staff(g.vs.loc.y);
			}
			
		});
	}
	
	public void show(Graphics g) {
		UC.bigVS.fill(g, Color.white);
	}
	
	public static class Staff extends Mass{
		public static int[] LINES = {-4, -2, 0, 2, 4};
		public int y;
		public int[] lines = LINES;
		
		public Staff(int y2) {
			y = y2;
			staffs.add(this);
			addReaction(new Reaction("S-S","add-bar"){
				public int bid(Stroke g) {
					if(g.yu() > Staff.this.yTop()) {return UC.noBid;}
					if(g.yd() < Staff.this.yBottom()) {return UC.noBid;}
					// can do better
					return 10;
				}
				public void act(Stroke g) {
					new Bar(g.xm());
				}	
			});
			addReaction(new Reaction("SW-SE","add-gclef"){
				public int bid(Stroke g) {
					return Math.abs(y-g.ym());
				}
				public void act(Stroke g) {
					new Clef(g.xm());
				}	
			});
      addReaction(new Reaction("SW-SW","add-note"){
				public int bid(Stroke g) {
          if(g.xm()< marX || g.xm() > MarX){return UC.noBid;}
          if(g.ym()< Staff.this.yTop()-2*H || g.ym() > Staff.this.yBottom()+2*H){return UC.noBid;}
          return 30;
				}
				public void act(Stroke g) {
          new Head(g.xm(),g.ym());
				}	
			});
		}
    @Override
		public void show(Graphics g) {
			g.setColor(Color.gray);
			for(int i= 0; i< lines.length; i++) {
				int dy = lines[i] * H;
				g.drawLine(marX, y+dy, MarX, y+dy);
			}
		}
		
		public int yTop() {
			return y+lines[0]*H;
		}
		
		public int yBottom() {
			return y+lines[lines.length-1]*H;
		}
		
		public class Bar extends Mass {
			public int x, style=0;
			public boolean repeatLeft = false, repeatRight = false;

			public Bar(int x1) {
				x = x1;
				bars.add(this);
        addReaction(new Reaction("S-N", "deletes bar") {
          public int bid(Stroke g) {return g.middleTopInBox(x-3*H, Staff.this.yTop(), x+(3*H), Staff.this.yBottom());}
          public void act(Stroke g) {delete();}
         });
        addReaction(new Reaction("E-E", "increment bar style") {
          public int bid(Stroke g) {return g.flagStem(x, Staff.this.yBottom(), Staff.this.yTop());}
          public void act(Stroke g) {style = (style + 1) % 3;}
        });
        addReaction(new Reaction("DOT", "repeat bars") {
          public int bid(Stroke g) {return g.middleTopInBox(x-2*H,Staff.this.yTop(),x+3*H, Staff.this.yBottom());}
          public void act(Stroke g) {
            if(g.xm()<x){
              repeatLeft = !repeatLeft;
            }else{
              repeatRight=!repeatRight;
            }
          }
        });
			}
      
      @Override
      public Layer getLayer(){return bars;}
			
      @Override
			public void show(Graphics g) {
        int top = Staff.this.yTop(), bottom = Staff.this.yBottom();
        int mid = Staff.this.y;
        g.setColor(Color.red);
        if(repeatLeft || repeatRight){
            g.fillRect(x-H/2, top, H, bottom-top);
            if(repeatLeft){
              g.drawLine(x-3*H/2, top, x-3*H/2, bottom);
              g.fillOval(x-5*H/2, mid-H, 3 , 3);
              g.fillOval(x-5*H/2, mid+H, 3, 3);
            }
            if(repeatRight){
              g.drawLine(x+3*H/2, top, x+3*H/2, bottom);
              g.fillOval(x+5*H/2, mid-H, 3, 3 );
              g.fillOval(x+5*H/2, mid+H, 3, 3 );
            }
        }else{
          if(style == 0){
            g.drawLine(x, top, x, bottom);
          }else if(style == 1){
            g.drawLine(x-H/2, top, x-H/2, bottom);
            g.drawLine(x+H/2, top, x+H/2, bottom);
          }else{
            g.drawLine(x-H, top, x-H, bottom);
            g.fillRect(x, top, H, bottom-top);
          }
        }
      }
			
		}
		
		public class Clef extends Mass {
			int x;
      int type = 0;
			
			public Clef(int x) {
				this.x = x;
				bars.add(this);
        addReaction(new Reaction("E-E", "increment clef type") {
          public int bid(Stroke g) {return g.flagStem(x, Staff.this.yBottom(), Staff.this.yTop());}
          public void act(Stroke g) {type = (type + 1) % clefNames.length;}
        });
         addReaction(new Reaction("W-W", "decrement clef type") {
          public int bid(Stroke g) {return g.flagStem(x, Staff.this.yBottom(), Staff.this.yTop());}
          public void act(Stroke g) {type = (type - 1 + clefNames.length) % clefNames.length;}
         });
         addReaction(new Reaction("S-N", "deletes clef") {
          public int bid(Stroke g) {return g.middleTopInBox(x-3*H, Staff.this.yTop(), x+(3*H), Staff.this.yBottom());}
          public void act(Stroke g) {delete();}
         });
			}
			
			@Override
			public Layer getLayer() {return bars;}
			
			public void show(Graphics g) {
				g.setColor(Color.pink);
				NamedInk ink = Stroke.shapes.nInkMap.get(clefNames[type]);
				if(ink != null) {
					tempBox.loc.x = x;
					tempBox.loc.y = Staff.this.y;
					ink.showAt(g, tempBox);
				}
			}
		}
   
    public class Head extends Mass{
      int x;
      int line;
      
      public Head(int x, int y){
        this.x = x;
        this.line = (30*H+y-Staff.this.y+H/2)/H-30;
        System.out.println("Line is set to: "+line);
        bars.add(this);
      }
      
      @Override
      public Layer getLayer(){
        return bars;
      }

      @Override
      public void show(Graphics g) {
        g.setColor(Color.black);
        g.fillOval(x-3*H/2, Staff.this.y+line*H-H, 3*H, 2*H);
      }
    }
    
	}
 
}
