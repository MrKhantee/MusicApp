package InkApp;

import java.awt.Graphics;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.swing.plaf.basic.BasicLabelUI;

import GraphicsLib.G.VS;
import InkApp.Stroke.Shape.DB;
import InkApp.Ink.Blend;
import InkApp.Ink.NamedInk;
import InkApp.Ink.Norm;
import InkApp.Reaction.Oto;

public class Stroke implements I.Act{
	public Shape shape;
	public VS vs;
	public static DB shapes = Shape.getDB();
	
	public Stroke(Shape s, VS vs) {
		this.shape = s;
		this.vs = vs;
	}
	
	public Stroke() {
		if(inkIsDot()) {
			shape = Shape.DOT;
		} else {
			shape = shapes.getBestMatch(Ink.BUFFER.norm);
      System.out.println(shape == null? "Null" : shape.name);
		}
		vs = new VS(Ink.BUFFER.bbox);
	}
	
	public int xm() {return vs.xm();}
	public int xl() {return vs.xl();}
	public int xr() {return vs.xr();}
	public int ym() {return vs.ym();}
	public int yu() {return vs.yu();}
	public int yd() {return vs.yd();}
  
  public int flagStem(int x, int ylo, int yhi){
    if(ym() < yhi || ym() > ylo){return UC.noBid;}
    if(xr() < x || xl() > x){return UC.noBid;}
    return Math.abs(xm() - x) + Math.abs(ym()- (yhi + ylo)/2);
  }
	
  public int middleTopInBox(int x1, int y1, int x2, int y2){
    if(yu() < y1 || yu() > y2){return UC.noBid;}
    if(xm() < x1 || xm() > x2){return UC.noBid;}
    return Math.abs(xm() - (x1+x2)/2) + Math.abs(ym()- (y1+y2)/2);
  }
  
  public int middleBotInBox(int x1, int y1, int x2, int y2){// used on DnArrow
    if(xr() < x1 || xl() > x2 || yd() < y1 || yd() > y2){return UC.noBid;}
    return Math.abs(xm()-(x1+x2)/2) + Math.abs(yd() - (y1+y2)/2);
  }
  
  public int hStrokeCrossesVLine(int x, int y1, int y2){
    System.out.println("Stroke Crosses " + y1 + ", " + ym() + ", " + y2);
    if(xr() < x || xl() > x){return UC.noBid;}
    if(ym() < y1 || ym() > y2){return UC.noBid;}
    return 20;
  }
  
	private static boolean inkIsDot() {
		return (Ink.BUFFER.bbox.h.s < UC.dotSize && Ink.BUFFER.bbox.v.s < UC.dotSize);
	}
	
	public static class Shape extends Ink.NamedBlends implements Serializable{
//		public String name;
//		public ArrayList<Ink.Blend> blends = new ArrayList<>();
		public static Shape DOT = new Shape("DOT");
		public static String FNAME = UC.shapeDBFilePath;
		
		public Shape(String name) {
			super(name);
		}
		
		public static Shape byName(String name) {
			if (name.equals("DOT")) {
				return DOT;
			}
			else {
				Shape res = shapes.shapeMap.get(name);
				if(res == null) {
					res = new Shape(name);
					shapes.add(res);
				}
				return res;
			}
		}
		
		public static DB getDB() {
			DB res;
			try {
		        FileInputStream fin = new FileInputStream(FNAME);
		        ObjectInputStream oin = new ObjectInputStream(fin);
		        res = (DB) oin.readObject();
		        oin.close();
		        fin.close();
		      }catch(Exception e) {
		    	  	System.out.println(e);
		        System.out.println("Couldn't find file "+ FNAME + " so used default values.");
		        res = new DB(); 
		      }
		      return res;
		}
		
		public static void saveDB() {
      try {
		        FileOutputStream fout = new FileOutputStream(FNAME);
		        ObjectOutputStream oout = new ObjectOutputStream(fout);
		        oout.writeObject(shapes);
		        oout.close();
		        fout.close();
		        }catch(Exception e) {
		          System.out.println("WTF? Saving file: "+ FNAME);
		          e.printStackTrace();
		        }  
		}
		
		public int dist(Ink.Norm n) {
			int res = UC.hugeDistance;
			if(blends.isEmpty()) {
				System.out.println("You need some training!!! for shape : "+name);
			}
			for(Blend b:blends) {
				int d = b.distToNorm(n);
				if(d<res) {
					res=d;
				}
			}
			return res;
		}
		
		public void train() {
			if(name.equals("DOT")) {
				return;
			}
			if(blends.isEmpty()) {
				blends.add(Ink.Blend.newBlendFromInk());
				return;
			}
			Blend best = blends.get(0);
			int bestD = best.distToNorm(Ink.BUFFER.norm);
			for(Blend b:blends){
				int d = b.distToNorm(Ink.BUFFER.norm);
				if(d<bestD){
					bestD = d;
					best = b;
				}
			}
			if(bestD < UC.hugeDistance) {
				best.blend(Ink.BUFFER.norm);
			} else {
				blends.add(Ink.Blend.newBlendFromInk());
			}
		}
		
		public static class DB implements Serializable{
			public ArrayList<Shape> shapes = new ArrayList<>();
			public Map<String, Shape> shapeMap = new HashMap<>();
			public Map<String, NamedInk> nInkMap = new HashMap<>();
			
			public DB() { }
			public void add(Shape s) {
				if(s.name.equals("DOT")) {
					return;
				}
				shapes.add(s);
				shapeMap.put(s.name, s);
			}
			
			public Shape getBestMatch(Ink.Norm n) {
				if(shapes.isEmpty()) {
					return Shape.DOT;
				}
				Shape bestShape = shapes.get(0);
				int bestDist = bestShape.dist(n);
				for(Shape s:shapes) {
					int d = s.dist(n);
					if(d<bestDist){
						bestDist = d;
						bestShape = s;
					}
				}
				return (bestDist < UC.hugeDistance) ? bestShape: null;	
			}
			
		}
	}

	@Override
	public void act(Stroke g) {
		Reaction reaction = Reaction.bestReaction(g);
		if(reaction != null) {
			System.out.println(reaction.purpose);
			Oto.clearAll();
      reaction.act(g);
		}		
	}
}
