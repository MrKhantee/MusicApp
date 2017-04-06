/*
 * Copyright Amanda Eller 2015
 */
package Music1;

import InkApp.Layer;
import InkApp.Reaction;
import InkApp.Reaction.Button;
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
public class Sys extends Mass {
  public int y;
  public ArrayList<Staff> staffs;
  public Sys.Layout layout;
  public int m1, m2;

  public Sys(Layout layout, int y, int m1, int m2) {
    this.layout = layout;
    this.y = y;
    this.m1 = m1;
    this.m2 = m2;
    this.staffs = new ArrayList<>();
    for (int i = 0; i < layout.fmts.size(); i++) {
      staffs.add(layout.fmts.get(i).getNewStaff(this, i));
    }
  }

  @Override
  public Layer getLayer() {
    return MusicApp.staffs;
  }

  @Override
  public void show(Graphics g) {
   layout.showAt(g, y);
  }

  public static class Layout {
    public int x1, x2;
    public int defaultH;
    public ArrayList<Staff.Fmt> fmts;
    public ArrayList<Bracket> brackets;

    public Layout(int x1, int x2, int defaultH) {
      this.x1 = x1;
      this.x2 = x2;
      this.defaultH = defaultH;
      fmts = new ArrayList<>();
      brackets = new ArrayList<>();
    }

    public void showAt(Graphics g, int y) {//forloop of staffs, the brackets, bar continue. everything but the clefs }
      for(Staff.Fmt s: fmts){
        s.showAt(g,y);
      }
      if(fmts.size() > 1){
        g.drawLine(x1, fmts.get(0).dy + y, x1, guideline(y)-5*defaultH);
      }
    }

    public Staff.Fmt getLastFmt(){
      if(fmts.isEmpty()){return null;}
      return fmts.get(fmts.size()-1);
    }
    
    public int guideline(int y){
      Staff.Fmt last = getLastFmt();
      if(last == null){return y;}
      int lastStaffLine = last.lines[last.lines.length-1] * last.H;
      return y + last.dy + lastStaffLine + 5*defaultH;
    }
    
    public class SysEd extends Mass{
      public int y;
      public Button exit;
      //public ArrayList<Clef> clefs;
      
      
      public SysEd(int y){
        this.y = y;
        getLayer().add(this);
        exit = new Button("Exit", 750, 40) {
          @Override
          public void execute() {
            System.out.println("TODO: exit button : SysEd");
          }
        };
        addReaction(new Reaction("E-E","add-staff to the sys-layout") {
          public int bid(Stroke g) {
             return (g.vs.loc.y > guideline(y)) ? 100 : UC.noBid;
          }
          public void act(Stroke g) {
           new Staff.Fmt(Layout.this, g.ym()-y);
          }
        });
        //toggle bar connection S-S
        //add bracket S-S
        //add brace S-s
        //cycle lines and clefs E-E
      }
      
      @Override
      public Layer getLayer() {
        return MusicApp.staffs;
      }

      @Override
      public void show(Graphics g) {
        //guidline
        g.setColor(Color.BLUE);
        g.drawLine(x1+100 ,guideline(y),x2-100,guideline(y));
        showAt(g,y);
        
        //system brackets
        //clef
        //bar lines 
      }
      
    }
    
    
    public static class Bracket {
      public int nStaff1, nStaff2;
      public int eShape;

    }

  }
}
