/*
 * Copyright Amanda Eller 2015
 */
package music1;

import InkApp.Layer;
import InkApp.Reaction.Mass;
import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;

/**
 *
 * @author Amanda
 */
public class Staff extends Mass{
  public Sys sys;
  public int nStaff;
  public Fmt fmt; 
  
  public Staff(Sys sys, int nStaff){
    this.sys = sys;
    this.nStaff = nStaff;
    this.fmt = getFmt();
    Music1.staffs.add(this);
  }
  
  @Override
  public Layer getLayer(){return Music1.staffs;}
  
  public Fmt getFmt(){return sys.layout.fmts.get(nStaff);}
  
  public int lineY(int line){return sys.y + fmt.dy + line*fmt.H;}
  
  @Override
  public void show(Graphics g) {
    g.setColor(Color.gray);
    for(int i= 0; i< fmt.lines.length; i++) {
      g.drawLine(sys.layout.x1, lineY(i), sys.layout.x2, lineY(i));
    }
  }

  public int yTop() {
    return lineY(fmt.lines[0]);
  }

  public int yBottom() {
    return lineY(fmt.lines[fmt.lines.length-1]);
  }

  
  
  
  public static class Fmt{
    public static int[] MUSIC_STAFF = {0,2,4,6,8};
    public static int[] GUITAR_TAB = {0,2,4,6,8,10};
    public static int[] PERCUSSION_STAFF = {0};
    public static int[] STANDARD_TUNE = {0,1,2,3,4,5};
    
    public ArrayList<Voice> voices; // only one or two currently allowed.
    public int dy, H; // dy from sys y.
    public int[] lines;
    public int[] tuning; // for tab
    public boolean tab, barContinues, divisi;
    
    public Fmt(Sys.Layout layout, int dy){
      this.H = layout.defaultH;
      this.dy = dy;
      this.lines = MUSIC_STAFF;
      this.tuning = STANDARD_TUNE;
      tab = false;
      barContinues = false;
      divisi = false;
      layout.fmts.add(this);
    }

    public Staff getNewStaff(Sys sys, int nStaff) {
      return new Staff(sys, nStaff);
    }
    
    
    
    
  }
  
}
