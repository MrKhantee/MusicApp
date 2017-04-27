/*
 * Copyright Amanda Eller 2015
 */
package music1;

import GraphicsLib.G.V;
import GraphicsLib.G.VS;
import InkApp.Reaction;
import InkApp.Reaction.Mass;
import InkApp.Stroke;
import InkApp.UC;
import java.awt.Graphics;

/**
 *
 * @author Amanda
 */
public class Staff extends Mass{
  public Sys sys;
  public int nStaff;
  public Fmt fmt; 
  public Clef.List clefs = new Clef.List();
  public VS initialClefBox;
  
  public Staff(Sys sys, int nStaff){
    super(MusicApp.staffs);
    this.sys = sys;
    this.nStaff = nStaff;
    this.fmt = getFmt();
    int H = fmt.H;
    initialClefBox = new VS(new V(sys.layout.x1 + 2*H, fmt.dy+4*H + sys.y), new V(H,H));
    addReaction(new Reaction("SW-SE", "Add clef"){
      public int bid(Stroke s) {
        if(s.xm() < sys.layout.x1 || s.xm() > sys.layout.x2){return UC.noBid;}
        if(s.ym() < yTop() || s.ym() > yBottom()){return UC.noBid;}
        return 50;
      }
      public void act(Stroke s) {new Clef(s.xm(), Staff.this);}
    });
    addReaction(new Reaction("S-S","add Bar"){
      public int bid(Stroke s){
        if(s.xm() < sys.layout.x1 || s.xm() > sys.layout.x2){return UC.noBid;}
        return 15 + Math.abs(s.yu()-yTop()) + Math.abs(s.yd()-yBottom());
        // cycle existing bar bids 10
      }
      public void act(Stroke s){ new Bar(sys, s.xm());}
    });
    addReaction(new Reaction("SW-SW","add Head"){
      public int bid(Stroke s){
        int h = fmt.H;
        if(s.ym() < yTop() - h || s.ym() > yBottom() + h){return UC.noBid;}
        if(s.xm() < sys.layout.x1 || s.xm() > sys.layout.x2){return UC.noBid;}
        return 20;
      }
      public void act(Stroke s){new Head(Staff.this, 0, s.xm(), s.ym());}
    });  
    addReaction(new Reaction("E-E","add Ledger to Staff"){
      public int bid(Stroke s){
        int x = s.xm(); int y = s.ym(); int h = fmt.H;
        if(Math.abs(yTop() - 2*h - y)>2*h && Math.abs(yBottom() + 2*h - y)>2*h){return UC.noBid;}
        if(s.xm() < sys.layout.x1 || s.xm() > sys.layout.x2){return UC.noBid;}
        return 50;
      }
      public void act(Stroke s){
        int y = s.ym(); int h = fmt.H;
        Time t = Staff.this.sys.times.getTime(s.xm());
        Ledger l = t.getLedgerForStaff(Staff.this);
        if(l == null){l = new Ledger(Staff.this, t);}
        if(Math.abs(yTop() - 2*h - y)<=2*h){
          l.addLedgerUp();
        } else {
          l.addLedgerDn();
        }
      }
    });    
  }
  
  public Fmt getFmt(){return sys.layout.fmts.get(nStaff);}
  
  public int yOfLine(int line){return sys.y + fmt.dy + line*fmt.H;}
  
  @Override
  public void show(Graphics g) {
    Clef.showAt(g, getInitialClefShape(), initialClefBox);
//    g.setColor(UC.areaPurple);
//    for(int i= 0; i< fmt.lines.length; i++) {
//      g.drawLine(sys.layout.x1, yOfLine(fmt.lines[i]), sys.layout.x2, yOfLine(fmt.lines[i]));
//    }
  }

  public int yTop() {
    return yOfLine(fmt.lines[0]);
  }

  public int yBottom() {
    return yOfLine(fmt.lines[fmt.lines.length-1]);
  }
  
  public int keyAdj(int x){
    int res = (7 + 3*sys.keyAt(x))%7;
    return (res > 3)? res - 7 : res;
  }
  
  public int getInitialClefShape(){
   // System.out.println("getting initical clef shape for sys: "+this.sys.name);
    Staff s = this.prevStaff();
    Clef c = null;
    while(s != null && (c = s.clefs.lastClef())== null){
      s = s.prevStaff();
    }
    return (s == null) ? fmt.defaultClefEShape : c.eShape;
  }
  
  public int getClefShapeAt(int x){
    Clef c = clefs.clefAt(x);
    return (c == null) ? getInitialClefShape() : c.eShape;
  }
  
  public Staff prevStaff(){return (sys.prev != null) ? sys.prev.staffs.get(nStaff) : null;}

  public int getDeg(int x, int y) {
   // y translates directly to a line BUT 
    // someday use Key (based on x) to evaluate degree
    // for now degree = - line
    int h = fmt.H;
    int line = (100*h + y - yTop() + h/2)/h - 100;
    int deg = Clef.midCLine[getClefShapeAt(x)] - line;
    System.out.println("Degree: " + deg);
    //return -line;
    return deg; 
  }
  
  public static class Fmt{
    public static int[] MUSIC_STAFF = {0,2,4,6,8};
    public static int[] GUITAR_TAB = {0,2,4,6,8,10};
    public static int[] PERCUSSION_STAFF = {0};
    public static int[] STANDARD_TUNE = {0,1,2,3,4,5};
    
   // public ArrayList<Voice> voices; // only one or two currently allowed.
    public int dy, H; // dy from sys y.
    public int[] lines;
    public int[] tuning; // for tab
    public boolean tab, barContinues, divisi;
    public Sys.Layout layout;
    public int nStaff;
    public int defaultClefEShape = 0;
    public VS clefBox;
    
    public Fmt(Sys.Layout layout, int dy){
      this.layout = layout;
      this.H = layout.defaultH;
      this.dy = dy;
      this.lines = MUSIC_STAFF;
      this.tuning = STANDARD_TUNE;
      tab = false;
      barContinues = false;
      divisi = false;
      nStaff = layout.fmts.size();
      clefBox = new VS(new V(layout.x1 + 2*H, dy-4*H), new V(H,H));
      layout.fmts.add(this);
    }

    public Staff getNewStaff(Sys sys, int nStaff) {
      return new Staff(sys, nStaff);
    }

    void showAt(Graphics g, int y) {
      g.setColor(UC.areaPurple);
      for(int i= 0; i< lines.length; i++) {
				int yVal = lines[i] * H + y + dy;
				g.drawLine(layout.x1, yVal, layout.x2, yVal);
			}
    }
    
    public boolean isDrum(){
      return lines == PERCUSSION_STAFF;
    }
    
    public int dyLastLine(){
      return dy+lines[lines.length-1]*H;
    }

    public void cycleLines() {
      if(lines == MUSIC_STAFF){lines = GUITAR_TAB;}
      else if(lines == GUITAR_TAB){lines = PERCUSSION_STAFF;}
      else if(lines == PERCUSSION_STAFF){lines = MUSIC_STAFF;}
    }

  }
  
}
