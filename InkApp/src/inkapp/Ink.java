package InkApp;
  
import GraphicsLib.G.BBox;
import GraphicsLib.G.PL;
import GraphicsLib.G.V;
import GraphicsLib.G.VS;

import java.awt.Color;
import java.awt.Graphics;
import java.io.Serializable;
import java.util.ArrayList;

public class Ink {
	public static int BUFFER_LIMIT = UC.maximumNumberOfPointsInInkBuffer;
	public static Buffer BUFFER = new Buffer(BUFFER_LIMIT);
    
    //public Norm norm; // shape of the ink
    //public BBox bbox; // where on the screen it should go.
    public Blend blend;
    public VS vs;
    
    public Ink(Blend blend, BBox bbox){this.blend = blend; this.vs = new VS(bbox);}
    
    public void show(Graphics g){blend.showAt(g, vs);}
    
    public static class Buffer{
      private PL pl; // the polyline
      private int n; // how many coordinates are currenly used.
      public BBox bbox = new BBox(0,0);
      public Norm norm = new Norm();
      
      private Buffer(int max){ // singlton
        n = 0; 
        pl = new PL(max);  
      }
      
      public void clear(){n = 0; bbox.set(0,0);}
      
      public void addFirst(int x, int y) {
		clear();
		add(x, y);
	  }
      
      public void addLast(int x, int y) {
  		add(x, y);
  		norm.setNormFromBuffer();
  	  }
      
      public void add(int x, int y) {
        if(n==0){bbox.set(x,y);}
        
        if(n<(pl.size()-1)){
        	pl.points[n++].set(x,y);
        	bbox.add(x,y);
        }
      }
      public void show(Graphics g){pl.show(g, n);}
      public void showDots(Graphics g){pl.showDots(g, n);}
    }
    
    public static class Norm extends PL implements Serializable{
      public static final int COUNT = UC.numberOfPointsInNorm, SIZE = UC.maxCoordInNorm;
      public static final BBox BBOX = new BBox(0,0,SIZE,SIZE);
      public static Norm SHOW_AT_BUFFER = new Norm();
      
      public Norm() {
    	  super(COUNT);
      }
      
      public Norm setNormFromBuffer() {
    	  	BBox bb = BUFFER.bbox;
    	  	int iso = (bb.h.s < bb.v.s)?bb.v.s:bb.h.s;
        for(int i = 0; i<COUNT; i++){
          int j = i*(BUFFER.n - 1)/(COUNT - 1); // j is ndx in BUFFER.
          points[i].set(BUFFER.pl.points[j]);
          //points[i].x = (points[i].x - BUFFER.bbox.h.lo)*SIZE/BUFFER.bbox.h.s;
          //points[i].y = (points[i].y - BUFFER.bbox.v.lo)*SIZE/BUFFER.bbox.v.s;
          points[i].x = (points[i].x - BUFFER.bbox.h.lo)*SIZE/iso;
          points[i].y = (points[i].y - BUFFER.bbox.v.lo)*SIZE/iso;
        }
        return this;
      }
      
      public void showAt(Graphics g, VS vs){
      	  int iso = (vs.size.x > vs.size.y)?vs.size.x:vs.size.y;
    	  for(int i = 0; i<points.length; i++){
    		  //SHOW_AT_BUFFER.points[i].setToScale(vs, SIZE, this.points[i]);
    		  SHOW_AT_BUFFER.points[i].setToIsoScale(vs, SIZE, this.points[i], iso);
    	  }
    	  SHOW_AT_BUFFER.show(g);
      } 
      
      public int distToNorm(Norm n) {
    	  int res = 0;
    	  for(int i=0;i<COUNT;i++) {
    		  res += points[i].dist(n.points[i]);
    	  }
    	  return res;
      }
    }
    
    public static class Blend extends Norm implements Serializable {
	    	public int blendCount = 1;
	    	
	    	public Blend(int i) {
	    		super();
	    		blendCount = 0;
	    	}

		public Blend() { super();}

		public void blend(Norm n) {
			for(int i=0; i<COUNT;i++) {
				points[i].blend(n.points[i], blendCount);
			}
			blendCount++;
		}
    	
	    	public Blend setBlendFromBuffer() {
	    		setNormFromBuffer();
	    		return this;
	    	}
    	
	    	public static Blend newBlendFromInk(){
	    		Blend res = new Blend();
	    		res.setBlendFromBuffer();
	    		return res;
	    	}
    }
    
    public static class NamedBlends implements I.Show,Serializable{
	    	private static final int X = UC.shapeTrainerPrototypeViewMargin,
	    			W = UC.shapeTrainerPrototypeViewWidth;
	    	private static VS prototypeViewBox = new VS(new V(X,X), new V(W,W));
	    	public String name;
	    	public ArrayList<Blend> blends = new ArrayList<>();
	    	
	    	public NamedBlends(String name) {this.name = name;}
	    	
		@Override
		public void show(Graphics g) {
			prototypeViewBox.loc.x = X;
			g.setColor(Color.blue);
			for(Blend b:blends) {
				b.showAt(g, prototypeViewBox);
				g.drawString(""+b.blendCount, prototypeViewBox.loc.x, X+W);
				prototypeViewBox.loc.x += W;
			}
		}
    		
    }
    
    public static class NamedInk extends NamedBlends implements Serializable{

		public NamedInk(String name, int strokeCount) {
			super(name);
			for(int i=0; i< strokeCount; i++) {
				this.blends.add(new Blend(0));
			}
			Stroke.shapes.nInkMap.put(name,this);
		}
		
		public static NamedInk get(String name) {
			NamedInk res = Stroke.shapes.nInkMap.get(name);
			//if(res==null){ Stroke.Trainer.trainNamedInk(name);}
			return res;
		}

		public void showAt(Graphics g, VS vs) {
			for(Blend b:blends) {
				b.showAt(g,vs);
			}
		}
		
		public void train(int i, VS vs) {
			Ink.BUFFER.bbox.h.set(vs.loc.x);
			Ink.BUFFER.bbox.h.add(vs.loc.x + vs.size.x);
			Ink.BUFFER.bbox.v.set(vs.loc.y);
			Ink.BUFFER.bbox.v.add(vs.loc.y + vs.size.y);
			Ink.BUFFER.norm.setNormFromBuffer();
			blends.get(i).blend(Ink.BUFFER.norm);
		}
    }
     
  }