/*
 * Copyright Amanda Eller 2015
 */
package music1;

import InkApp.Reaction;
import InkApp.Reaction.Mass;
import InkApp.Stroke;
import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;

/**
 *
 * @author Amanda
 */
public class Head extends Mass implements Comparable<Head>{
  public static int QUARTER = 0;
  public static int HALF = 1;
  public Staff staff;
  public Time time;
  public Stem stem = null;
  public int eShape; // 0 = quarter, 1 = half & whole
  public int deg; // octave + scale degree
  public int raise = 0;
  public int dxSide = 0; // if Head is shifted by stem, the shift is kept here

  public Head(Staff staff, int eShape, int x, int y) {
    super(MusicApp.notes);
    this.staff = staff;
    this.eShape = eShape;
    deg = staff.getDeg(x, y);
    time = staff.sys.times.getTime(x);
    Ledger l = time.getLedgerForStaff(staff);
    if (l == null) {
      l = new Ledger(staff, time);
    }
    l.addHead(this);
    raise = 0;
    addReaction(new Reaction("NE-SE","sharp head"){
      public int bid(Stroke s){
        int x = time.x; int y = staff.yOfLine(line(x)); int h = staff.fmt.H;
        return s.middleTopInBox(x - 3*h/2, y-h, x+ 3*h/2, y+h);
      }
      public void act(Stroke s){if(raise < 1){raise++;}}
    });
    
    addReaction(new Reaction("SE-NE","flat head"){
      public int bid(Stroke s){
        int x = time.x; int y = staff.yOfLine(line(x)); int h = staff.fmt.H;
        return s.middleBotInBox(x - 3*h/2, y-h, x+ 3*h/2, y+h);
      }
      public void act(Stroke s){if(raise >-1){raise--;}}
    });
  }

  public void joinStem(ArrayList<Head> stemHeads, int y1, int y2){
    // if in range and unstemed, join. if alread stemmed unStem.
    int y = staff.yOfLine(line(time.x));
    if(y > y1 && y <y2){
      if(stem == null){
        stemHeads.add(this);
      } else {
        stem.removeHead(this);
        dxSide = 0; // when you leave a stem - lose your second offset
      }
    }
  }

  public int yOfHead(){
    int H = staff.fmt.H;
    int x = time.x + dxSide;
    int y = staff.yTop() + line(x)*H;
    return y;
  }
  
  
  @Override
  public void show(Graphics g) {
    g.setColor(Color.BLACK);
    int H = staff.fmt.H;
    int x = time.x + dxSide;
    int y = staff.yTop() + line(x) * H;
    if(raise == 1){
      g.setColor(Color.RED);
    } else {
      g.setColor(raise == -1 ? Color.BLUE : Color.BLACK);      
    }
    int key = staff.sys.keyAt(time.x);
    if(raise == 1){
      if(raisedByKey(key)){
        Accid.doubleSharpAt(g, x-3*H, y, H);
      } else if(loweredByKey(key)){
        Accid.naturalAt(g, x-3*H, y, H);
      } else {
        Accid.sharpAt(g, x-3*H, y, H);        
      }
    }
    if(raise == -1){
      if(raisedByKey(key)){
        Accid.naturalAt(g, x-3*H, y, H);
      } else if(loweredByKey(key)){
        Accid.doubleFlatAt(g, x-3*H, y, H);
      } else {
        Accid.flatAt(g, x-3*H, y, H);        
      }
    }
    
    if (eShape == QUARTER) {
      g.fillOval(x - 3 * H / 2, y - H, 3 * H, 2 * H);
    } else {
      g.drawOval(x - 3 * H / 2, y - H, 3 * H, 2 * H);
    }
  }

  public static int degToLine(int deg, Staff staff, int x){
    return Clef.midCLine[staff.getClefShapeAt(x)] + staff.keyAdj(x) - deg;    
  }
  
  public int line(int x){return degToLine(deg, staff, x);}
  
  public boolean raisedByKey(int key){return key > (5*(deg+700)+5)%7;}
  public boolean loweredByKey(int key){return -key > (2*(deg+700) +1)%7;}

  @Override
  public int compareTo(Head t) {return t.deg - this.deg;}
}
