/*
 * Copyright by Marlin Eller 2017
 */
package music1;

import InkApp.Reaction;
import InkApp.Reaction.Mass;
import InkApp.Stroke;
import InkApp.UC;
import java.awt.Graphics;
import java.util.ArrayList;

/**
 *
 * @author Marlin
 */
public class Ledger extends Mass{
  private static int min, max, min1, min2, max1, max2; // accumulators set by eval
  public Staff staff;
  public Time time;
  public ArrayList<Head> heads = new ArrayList<>(); 
  public int initialMin = 0, initialMax = 8;
  
  public Ledger(Staff staff, Time time){
    super(MusicApp.staffs);
    this.staff = staff;
    this.time = time;
    time.ledgers.add(this);
    addReaction(new Reaction("SW-SW","add Head on Ledger"){
      public int bid(Stroke s){
        int h = staff.fmt.H; int y = staff.yTop();
        if(s.xm() < time.x - 2*h || s.xm() > time.x + 2*h){return UC.noBid;}
        eval();
        System.out.println("y: "+ s.ym() + " min: " + (y - h*min - h) + " max: " + (y + h*max + h));
        // plus sign on h*min is tricky. Remmeber min is already 0 or negative
        if(s.ym() < y + h*min - h || s.ym() > y + h*max + h){return UC.noBid;}
        return 30;
      }
      public void act(Stroke s){new Head(staff, 0, s.xm(), s.ym());}
    }); 
  }

  @Override
  public void show(Graphics g){
    g.setColor(UC.staffLineColor);
    eval();
    int h = staff.fmt.H; int y = staff.yTop(); int x = time.x;
    if(min < 0){
      for(int i = -2; i >= min; i-=2){
        g.drawLine(x + min1 - 2*h, y + i*h, x + min2 + 2*h, y + i*h );
      }
    }
    if(max > 8){
      for(int i = 10; i <= max; i+=2){
        g.drawLine(x + min1 - 2*h, y + i*h, x + min2 + 2*h, y + i*h );
      }
    } 
  }
  
  public void addHead(Head h){heads.add(h); initialMin = 0; initialMax = 8;}
  public void removeHead(Head h){heads.remove(h);}
  public void addLedgerUp(){eval(); initialMin = min - 2;}
  public void addLedgerDn(){eval(); initialMax = max + 2;}
  
  private void eval(){ // determines how many lines to draw and how wide
    min = initialMin; max = initialMax;  // ledger may be user input with no head
    min1 = 0; min2 = 0; max1 = 0; max2 = 0; // assume 5 lines
    for(Head h : heads){
      int line = h.line(time.x);
      if(line < 0){
        if(line < min){min = line;}
        if(h.dxSide < min1){min1 = h.dxSide;}
        if(h.dxSide > min2){min2 = h.dxSide;}
      }
      if(line > 8){
        if(line > max){max = line;}
        if(h.dxSide < max1){max1 = h.dxSide;}
        if(h.dxSide > max2){max2 = h.dxSide;}
      }
    }
  }
  
}
