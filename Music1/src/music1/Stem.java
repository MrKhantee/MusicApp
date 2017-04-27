/*
 * Copyright Amanda Eller 2015
 */
package music1;

import InkApp.Reaction;
import InkApp.Reaction.Mass;
import InkApp.Stroke;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.Collections;

/**
 *
 * @author Amanda
 */
public class Stem extends Mass{
  public ArrayList<Head> heads;
  public int nFlag;
  public int nDot;
  public int H;
  public Beam beam;
  public Time time;
  public boolean rest;
  public boolean upStem;
  public int eHeadShape;
  
   public Stem(ArrayList<Head> tempheads, boolean upStem, Time time, int y1, int y2) {
    super(MusicApp.notes);
    this.heads = tempheads; this.upStem = upStem; this.time = time;
    Collections.sort(heads); // highest first, lowest last.
    
    H = heads.get(0).staff.fmt.H;
    adjustSeconds();
    System.out.print("Heads after sort");
    for(Head h : heads){
      h.stem = this;
      System.out.print(": "+ h.deg);
    }
    System.out.println();
    beam = Beam.getBeam(this, y1, y2);
    
    addReaction(new Reaction("E-E", "addFlag"){
      public int bid(Stroke s){return s.hStrokeCrossesVLine(time.x, y1(), y2());}
      public void act(Stroke s){Stem.this.nFlag++;}
    });
  }
  
  public int y1(){return upStem? freeY() : heads.get(heads.size()-1).yOfHead();}
  public int y2(){return upStem? heads.get(0).yOfHead() : freeY();}
  
  @Override
  public void show(Graphics g) {
    if(heads.isEmpty()){System.out.println("WTF? - stem with no heads: "); return;}
    
    Head lastHead = (upStem)? heads.get(heads.size()-1): heads.get(0);
    int x = x(); int y1 = freeY(); int y2 = lastHead.staff.yOfLine(lastHead.line(x));
    g.drawLine(x, y1, x, y2);
  }
  
  public int x(){return time.x + (upStem ? + 3*H/2 : -3*H/2);}
  
  private void adjustSeconds(){
    Head prev, h;
    int first, last, inc;
    if(heads.size() == 1){heads.get(0).dxSide = 0; return;} // single notes have no seconds
    
    if(upStem){ // work lowest to high
      first = heads.size() -1; last = 0; inc = -1;
    } else {
      first = 0; last = heads.size() -1; inc = 1;
    }
    prev = heads.get(first); first += inc;
    
    for(int i = first; i!=last; i += inc){
      h = heads.get(i);
      h.dxSide = (Math.abs(h.deg - prev.deg)<2 && prev.dxSide == 0)? -inc*3*H : 0;
      prev = h;
    }
    h = heads.get(last);
    h.dxSide = (Math.abs(h.deg - prev.deg)<2 && prev.dxSide == 0)? -inc*3*H : 0;
  }
  
  public int freeY(){
    // returns where the end would be if this were just a normal lengeth single flagged stem
    // a stem with many flags will have this valuse adjusted by the beam drawing code.
   int line; Head h;
   if(heads.isEmpty()){return 0;} // guard against incomplete stems
   if(upStem){
     h = heads.get(0);
     int deg = h.deg + 7;
     line = Head.degToLine(deg, h.staff, time.x);
     if(nFlag > 2){line -= 2*(nFlag - 2);} // make extra room for many flags
     if(line > 4){line = 4;} // free end of upStem must be above mid-line
   } else {
     h = heads.get(heads.size() - 1);
     int deg = h.deg - 7;
     line = Head.degToLine(deg, h.staff, time.x);
     if(nFlag > 2){line += 2*(nFlag - 2);} // make extra room for many flags
     if(line < 4){line = 4;} // free end of dnStem must be below mid-line
   }
   return h.staff.yOfLine(line); 
  }
  
  public void removeStem(){
    // if this is a supporting stem for its beam we must first unbeam.

   // System.out.print("starting delete " + id() + " "); MusicApp.notes.spewLayer();
    if(beam.s2 == this || beam.s1 == this){
      beam.unBeam(); // this stem was a support for a multi-stem beam
    }
    // now we know stem is NOT a support for multi-stem BUT it could still be in a multi-Stem
    if(beam.s2 != null){
      this.beam.stems.remove(this); // decouple from the beam
    } else {
      this.beam.delete();
      //System.out.println("just deleted beam: " +);
     // MusicApp.notes.spewLayer();
    } 
    this.delete(); // then remove the stem
   // System.out.println("removed of actual stem ");
   // MusicApp.notes.spewLayer();

  }
  
  public void removeHead(Head h){
    heads.remove(h); 
    h.stem = null;  // disconnect this head from stem
    if(heads.isEmpty()){ // check if stem is now empty
      //System.out.println("removing stem "+ id());
      removeStem();
    }
  }
  
}
