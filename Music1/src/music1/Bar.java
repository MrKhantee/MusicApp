/*
 * Copyright Amanda Eller 2015
 */
package music1;

import InkApp.Reaction;
import InkApp.Reaction.Mass;
import InkApp.Stroke;
import InkApp.UC;
import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;

/**
 *
 * @author Amanda
 */
public class Bar  extends Mass{
  public int eShape;
  public int x;
  public Sys sys;
  public int dKey;
  //public Time.Group times;
  
  public Bar(Sys sys, int x){
    super(MusicApp.bars);
    this.sys = sys;
    this.x = x;
    addReaction(new Reaction("DOT","dot a Bar"){
      public int bid(Stroke s){
        if(s.ym() < sys.y || s.ym() > sys.yBot()){return UC.noBid;}
        int dist = Math.abs(Bar.this.x - s.xm());
        int H = sys.layout.defaultH;
        if(dist > 3*H){return UC.noBid;}
        return dist;
      }
      public void act(Stroke s){ Bar.this.dotBar(s.xm());}
    }); 
    addReaction(new Reaction("S-S","cycle Bar"){
      public int bid(Stroke s){
        int H = sys.layout.defaultH;
        if(s.xm() < Bar.this.x - 3*H || s.xm() > Bar.this.x + 3*H){return UC.noBid;}
        if(s.ym() < Bar.this.sys.y || s.ym() > Bar.this.sys.yBot()){return UC.noBid;}
        return 10;
        // add Bar bids 15++
      }
      public void act(Stroke s){Bar.this.cycleBarShape();}
    });
    addReaction(new Reaction("E-E","key up"){
      public int bid(Stroke s){
        int H = sys.layout.defaultH;
        int bx = Bar.this.x;
        if(Bar.this.eShape != 1){return UC.noBid;}
        if(s.xl() < bx - 6*H || s.xl() > bx){return UC.noBid;}
        if(s.xr() > bx + 6*H || s.xr() < bx){return UC.noBid;}
        if(s.ym() < Bar.this.sys.y || s.ym() > Bar.this.sys.yBot()){return UC.noBid;}
        return 10;
      }
      public void act(Stroke s){Bar.this.dKey++;}
    });
    addReaction(new Reaction("W-W","key dn"){
      public int bid(Stroke s){
        int H = sys.layout.defaultH;
        int bx = Bar.this.x;
        if(Bar.this.eShape != 1){return UC.noBid;}
        if(s.xl() < bx - 6*H || s.xl() > bx){return UC.noBid;}
        if(s.xr() > bx + 6*H || s.xr() < bx){return UC.noBid;}
        if(s.ym() < Bar.this.sys.y || s.ym() > Bar.this.sys.yBot()){return UC.noBid;}
        return 10;
      }
      public void act(Stroke s){Bar.this.dKey--;}
    });  
  }
  
  public void cycleBarShape(){
    eShape++;
    if(eShape > 2){eShape = 0;} // returns collapsed back to single bar
  }
  
  public void dotBar(int x){
    if(this.x < x){ // dot Right side
      if(eShape == 6){eShape = 4; return;}
      if(eShape == 4){eShape = 6; return;}
      if(eShape == 5){eShape = 0; return;}
      if(eShape < 4){eShape = 5; return;}
    }else {
      if(eShape == 6){eShape = 5; return;}
      if(eShape == 5){eShape = 6; return;}
      if(eShape == 4){eShape = 0; return;}
      if(eShape < 4){eShape = 4; return;}      
    }
  }
  
  public int dist(int x){return Math.abs(x - this.x);}
  
    @Override
  public void show(Graphics g) {
    g.setColor(Color.BLACK);
    int y = sys.y; 
    int top = y, bot;
    int h = sys.layout.defaultH;
    boolean barBreak = true;
    for(Staff.Fmt f : sys.layout.fmts){
      if(barBreak){
        top = y + f.dy - (f.isDrum() ? f.H*2 : 0);
        barBreak = false;
      }
      if(!f.barContinues){
        barBreak = true;
        bot = y + f.dy + f.lines[f.lines.length - 1]*f.H + (f.isDrum() ? f.H*2 : 0);
        // now we have both top and bot of a single line so can draw based on shape
        showComponent(g, top, bot, h);
       }
      int yt = y + f.dy;
      if(eShape == 5 || eShape == 6){ // dots (if needed) drawn on each fmt
        g.fillOval(x+3*h/2, yt+11*h/4, h/2, h/2);
        g.fillOval(x+3*h/2, yt+19*h/4, h/2, h/2); 
      } 
      if(eShape == 4 || eShape == 6){ // dots (if needed) drawn on each fmt
        g.fillOval(x-3*h/2, yt+11*h/4, h/2, h/2);
        g.fillOval(x-3*h/2, yt+19*h/4, h/2, h/2); 
      } 
    }
    if(dKey != 0){
      g.setColor(Color.LIGHT_GRAY);
      g.drawString(""+dKey, x, sys.y - 5);
    }
  }
  
   private void showComponent(Graphics g, int top, int bot, int h){
    g.drawLine(x, top, x, bot);
    if(eShape > 1){g.fillRect(x,top, h,bot-top+1);} //fat line;
    if(eShape == 4 || eShape == 6){drawWings(g,x,top,bot,-2*h,h);}  
    if(eShape == 5 || eShape == 6){drawWings(g,x+h,top,bot,2*h,h);} 
    if(eShape == 1 || eShape == 2){g.drawLine(x-h, top, x-h, bot);}
  }  

   public static void drawWings(Graphics g, int x, int y1, int y2, int dx, int dy){
    g.drawLine(x, y1, x+dx, y1-dy);
    g.drawLine(x, y2, x+dx, y2+dy);
  }
   
    public static class List extends ArrayList<Bar>{
    public Bar first, last;
   
    public Bar closestBar(int x){
      Bar res = first; int d = res.dist(x);
      for(Bar b : this){
        int nd = b.dist(x);
        if(nd < d){d = nd; res = b;}
      }
      return res;
    }
    
    public void show(Graphics g){ for(Bar b : this){b.show(g);}}
  }
  
}
