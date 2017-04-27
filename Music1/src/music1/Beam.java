/*
 * Copyright Amanda Eller 2015
 */
package music1;

import InkApp.Reaction.Mass;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Polygon;
import java.util.ArrayList;

/**
 *
 * @author Amanda
 */
public class Beam extends Mass{
  public ArrayList<Stem> stems = new ArrayList<>();
  public Stem s1;
  public Stem s2; // could be null
  public int H;
  
  public Beam(Stem s1, Stem s2) {
    super(MusicApp.notes);
    this.s1 = s1;
    this.s2 = s2;
    stems.add(s1);
    if(s2 != null){stems.add(s2);}
    H = s1.H;
  }
  
  public int yOfX(int x){
    // beam is line from (x,y1())-(x,y2())
    if(s2 == null){return 0;} // presumably this is never called for single stem beams    
    int y1 = y1(), y2 = y2(), dy = y2-y1, x1 = s1.time.x;
    int dx = s2.time.x;
    return (x - x1)*dy/dx;
  }
  
  public int y1(){return s1.freeY();}
  public int y2(){return s2!=null ? s2.freeY() : (s1.upStem? y1()+2*H : y1()-2*H);}
  public int x1(){return s1.x();}
  public int x2(){return (s2 != null) ? s2.x() : s1.x() + 4*H;}
  
  public boolean lineCrossesBeam(int x, int y1, int y2){
    if(s2 == null || x < s1.time.x || x > s2.time.x){return false;}
    int y = yOfX(x);
    if(y1 > y || y2 < y) return false;
    return true;
  }
  
  public static Beam getBeam(Stem s, int y1, int y2){
    // if the vertical line described by the arguments crosses an existing beam
    // return it. else create a new one.
    Beam b = new Beam(s, null);
    System.out.print("getBeam - ");
   // MusicApp.notes.spewLayer();
    return b;
  }

  @Override
  public void show(Graphics g) {
    int y1 = y1(); int y2 = y2(); int x1 = x1(); int x2 = x2();
    int nB = s1.nFlag;
    if(s2!= null && s2.nFlag < nB){nB = s2.nFlag;}
    for(int i = 0; i<nB; i++){
      if(s1.upStem){
        drawBeam(g, x1,y1,x2,y2); 
        y1 += 2*H; y2 += 2*H;
      } else {
        drawBeam(g, x1,y1-H,x2,y2-H); 
        y1 -= 2*H; y2 -= 2*H;
      }
    }
  }

  public void drawBeam(Graphics g, int x1, int y1, int x2, int y2){
     g.setColor(Color.RED);
     Polygon p = new Polygon();
     p.addPoint(x1,y1); 
     p.addPoint(x2,y2); 
     p.addPoint(x2,y2+H);
     p.addPoint(x1,y1+H);
     g.fillPolygon(p);
  }
  
  public void unBeam(){
   // System.out.print("About to unbeam "); MusicApp.notes.spewLayer();
    if(s2 == null){return;} // single stem beam is alread unbeamed.
    for(Stem s : stems){
      s.beam = new Beam(s, null);
    }
    this.delete();
  }
}
