/*
 * Copyright Amanda Eller 2015
 */
package Music1;

import GraphicsLib.G.V;
import GraphicsLib.G.VS;
import InkApp.Reaction.Mass;
import java.awt.Color;
import java.awt.Graphics;

/**
 *
 * @author Amanda
 */
public class Staff extends Mass{
  public Sys sys;
  public int nStaff;
  public Fmt fmt; 
  
  public Staff(Sys sys, int nStaff){
    super(MusicApp.staffs);
    this.sys = sys;
    this.nStaff = nStaff;
    this.fmt = getFmt();
  }
  
  public Fmt getFmt(){return sys.layout.fmts.get(nStaff);}
  
  public int yOfLine(int line){return sys.y + fmt.dy + line*fmt.H;}
  
  @Override
  public void show(Graphics g) {
    g.setColor(Color.gray);
    for(int i= 0; i< fmt.lines.length; i++) {
      g.drawLine(sys.layout.x1, yOfLine(i), sys.layout.x2, yOfLine(i));
    }
  }

  public int yTop() {
    return yOfLine(fmt.lines[0]);
  }

  public int yBottom() {
    return yOfLine(fmt.lines[fmt.lines.length-1]);
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
      g.setColor(Color.BLACK);
      for(int i= 0; i< lines.length; i++) {
				int yVal = lines[i] * H + y + dy;
				g.drawLine(layout.x1, yVal, layout.x2, yVal);
			}
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
