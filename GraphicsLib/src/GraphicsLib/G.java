package GraphicsLib;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.io.Serializable;
import java.security.InvalidKeyException;
import java.time.chrono.MinguoChronology;
import java.util.Random;

import javax.security.auth.x500.X500Principal;
import javax.xml.bind.annotation.adapters.NormalizedStringAdapter;

import GraphicsLib.G.V;

public class G {
	
	public static Random R = new Random();
	
	public static class LoHi {
		public int lo, hi, s, m;
		
		public LoHi(int min, int max) {
			lo = min;
			hi = max;
			sm();
		}
		public LoHi(int v){this.lo = v; this.hi = v; sm();}
	    public LoHi(LoHi x){lo = x.lo; hi = x.hi; sm();}
		
		public void sm() {
			m = (lo+hi) / 2;
			s = (hi-lo);
			if(s==0)
				s=1;
		}
		
		public int random() {
			return R.nextInt(s)+lo;
		}
		
		public int constrain(int v){if(v<lo){return lo;}; return (v>hi)?hi:v;}
	    public void set(int x){lo = x; hi = x; sm();}
	    public void add(int v) {
	    	if(lo>v){lo = v;} 
	    	if(hi<v){hi = v;}
	    	sm();
	    }
	    
	    public int fromNorm(int x, int ns){return x*s/ns + lo;} // ns is norm size
        // allows you to decode an x in ns coordinates into a bounding box range.
	}
	
	public static class BBox {
		public LoHi h,v;
		public BBox(int x1, int y1, int x2, int y2) {
			h =  new LoHi(x1, x2);
			v = new LoHi(y1, y2);
		}
		public BBox(int x, int y) {
			h = new LoHi(x,x); v = new LoHi(y,y);
		}
		
		public BBox(BBox b){h = new LoHi(b.h); v = new LoHi(b.v);}
		public void show(Graphics g){
			g.drawRect(h.lo, v.lo, h.s, v.s);
		}
		
		public void show(Graphics g, Color c){g.setColor(c); this.show(g);}
		public void draw(Graphics g, Color c){this.show(g,c);}
		public void set(int x, int y){h.set(x); v.set(y);}
		public void add(int x, int y){h.add(x); v.add(y);}
		public void add(V vec){h.add(vec.x); v.add(vec.y);}
		public String toString() {
			return h.lo + ", " + h.hi + ", " + v.lo + ", " + v.hi;
		}
	}
	
	public static class V implements Serializable{
		public int x,y;
		
		public V(int x, int y) {
			this.x = x;
			this.y = y;
		}
		
		public V() {
			x = 0;
			y = 0;
		}
		
		public V(V v) {
			this.x = v.x;
			this.y = v.y;
		}
		
		public V randomize(BBox box) {
			x = box.h.random();
			y = box.v.random();
			return this;
		}
		
		public static V randomize(VS vs) {
			int x = R.nextInt(vs.size.x) + vs.loc.x;
			int y = R.nextInt(vs.size.y) + vs.loc.y;
			return new V(x, y);
		}
		
		public V add(V v) {
			x += v.x;
			y += v.y;
			return this;
		}
		
		public V add(int a,int b) {
			x += a;
			y += b;
			return this;
		}
		
		public V constrain(BBox b) {
			if(x < b.h.lo) x = b.h.lo;
			if(x > b.h.hi) x = b.h.hi;
			if(y < b.v.lo) y = b.v.lo;
			if(y > b.v.hi) y = b.v.hi;
			return this;
		}
		
		public V bounce(BBox b, V vel){
			if(x == b.h.lo && vel.x < 0) vel.x = -vel.x;
			if(x == b.h.hi && vel.x > 0) vel.x = -vel.x;
			if(y == b.v.lo && vel.y < 0) vel.y = -vel.y;
			if(y == b.h.hi && vel.y > 0) vel.y = -vel.y;
			return this;
		}
		
		public V copy() {
			return new V(this.x, this.y);
		}
		
		public V set(int xx, int yy){x = xx; y = yy; return this;}
	    public V set(V c){x = c.x; y = c.y; return this;} // copy values
	    
	    public void blend(V point, int blendCount) {
	    	x = (x*blendCount + point.x) / (blendCount + 1);
	    	y = (y*blendCount + point.y) / (blendCount + 1);
		}
	    
	    public void setToScale(VS vs, int size, V v) {
			x = (v.x * vs.size.x) / size + vs.loc.x;
			y = (v.y * vs.size.y) / size + vs.loc.y;
		}
	    
	    public void setToIsoScale(VS vs, int size, V v, int iso) {
			x = (v.x * iso) / size + vs.loc.x;
			y = (v.y * iso) / size + vs.loc.y;
		}
	    
	    public int dist(V v) {
			return (x-v.x)*(x-v.x) + (y-v.y)*(y-v.y);
		}
	
	}
	
	public static class VS {
		public V loc , size;
		
		public VS(V loc, V size) {
			this.loc = loc;
			this.size = size;
		}
		
		public VS(BBox bbox) {
			loc = new V(bbox.h.lo,bbox.v.lo);
			size = new V(bbox.h.s,bbox.v.s);
		}
		
		public boolean contains(int x , int y) {
			return (x >= loc.x && y >= loc.y && x < (loc.x + size.x) && y < (loc.y + size.y));
		}
		
		public void draw(Graphics g, Color c) {
			g.setColor(c);
			g.drawRect(loc.x, loc.y, size.x, size.y);
		}
		
		public void fill(Graphics g, Color c) {
			g.setColor(c);
			g.fillRect(loc.x, loc.y, size.x, size.y);	
		}
		
		public VS copy() {
			return new VS(loc.copy(), size.copy());
		}
		
		public VS contract(int m) {
			loc.x += m;
			loc.y += m;
			size.x -= 2*m;
			size.y -= 2*m;
			return this;
		}

		// X mid
		public int xm() {return loc.x + size.x/2;}
		public int xl() {return loc.x;}
		public int xr() {return loc.x + size.x;}
		// Y mid
		public int ym() {return loc.y + size.y/2;}
		public int yu() {return loc.y;}
		public int yd() {return loc.y + size.y;}
		
	}
	
	public static class PL implements Serializable{ // polyline
	      public V[] points;
	      public PL(int count){
	        points = new V[count]; 
	        for(int i = 0; i<count; i++){points[i] = new V();}
	      }
	      public int size(){return points.length;}
	      public void show(Graphics g, int n){ // allows me to show first n points of Ink.BUFFER
	        for(int i = 1; i<n; i++){
	          g.drawLine(points[i-1].x, points[i-1].y, points[i].x, points[i].y);
	        }
	      }
	      public void show(Graphics g){show(g,points.length);} // shows whole thing
	  
	      public void showDots(Graphics g, int n){
	        for(int i = 0; i<n; i++){
	          g.fillOval(points[i].x, points[i].y, 3, 3);}
	      }
	      public void showDots(Graphics g){showDots(g, points.length);}
	    }
	
}
