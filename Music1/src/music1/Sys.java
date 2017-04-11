/*
 * Copyright Amanda Eller 2015
 */
package Music1;

import InkApp.Reaction;
import InkApp.Reaction.Button;
import InkApp.Reaction.Mass;
import InkApp.Stroke;
import InkApp.UC;
import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;
import music1.Bar;

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
    super(MusicApp.staffs);
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
      for (Staff.Fmt s : fmts) {
        s.showAt(g, y);
      }
      if (fmts.size() > 1) {
        g.drawLine(x1, fmts.get(0).dy + y, x1, guideline(y) - 5 * defaultH);
      }
      for (Bracket b : brackets) {
        b.showAt(g, y);
      }
    }

    public Staff.Fmt getLastFmt() {
      if (fmts.isEmpty()) {
        return null;
      }
      return fmts.get(fmts.size() - 1);
    }

    public int guideline(int y) {
      Staff.Fmt last = getLastFmt();
      if (last == null) {
        return y;
      }
      int lastStaffLine = last.lines[last.lines.length - 1] * last.H;
      return y + last.dy + lastStaffLine + 5 * defaultH;
    }

    private boolean alreadyBracketed(int nStaff) {
     boolean res = false;
     for(Bracket b:brackets){
       if(b.contains(nStaff)){
        res = true;
       }
     }
     return res;
    }
    

    public class SysEd extends Mass {

      public int y;
      public Button exit;
      //public ArrayList<Clef> clefs;

      public SysEd(int y) {
        super(MusicApp.staffs);
        this.y = y;
        exit = new Button("Exit", 750, 40) {
          @Override
          public void execute() {
            System.out.println("TODO: exit button : SysEd");
          }
        };
        addReaction(new Reaction("E-E", "add-staff to the sys-layout") {
          public int bid(Stroke g) {
            return (g.vs.loc.y > guideline(y)) ? 100 : UC.noBid;
          }

          public void act(Stroke g) {
            new Staff.Fmt(Layout.this, g.ym() - y);
          }
        });
        addReaction(new Reaction("S-S", "add-brackets to the sys-layout") {
          public int bid(Stroke g) {
            if(g.xm() > x1){return UC.noBid;}
            if(fmts.size() < 2){return UC.noBid;}
            return 50;
          }

          public void act(Stroke g) {
            Bracket.newBracket(Layout.this, g.yu()-y, g.yd()-y);
          }
        });
        addReaction(new Reaction("SW-SE", "add-braces to the sys-layout") {
          public int bid(Stroke g) {
            if(g.xm() > x1){return UC.noBid;}
            if(fmts.size() < 2){return UC.noBid;}
            return 50;
          }

          public void act(Stroke g) {
            Bracket.newBrace(Layout.this, g.yu()-y, g.yd()-y);
          }
        });
        //toggle bar connection S-S
        //add brace SW-SE
        //cycle lines and clefs E-E
      }

      @Override
      public void show(Graphics g) {
        g.setColor(Color.BLUE);
        g.drawLine(x1 + 100, guideline(y), x2 - 100, guideline(y));
        showAt(g, y);

        //clef
        //bar lines 
      }
    }

    public static class Bracket {

      public int nStaff1, nStaff2;
      public int eShape; // 0 = brace, 1 = primary bracket, 2 = secondary bracket
      public Layout layout;

      public Bracket(Layout layout, int eShape, int nStaff1, int nStaff2) {
        this.layout = layout;
        this.eShape = eShape;
        this.nStaff1 = nStaff1;
        this.nStaff2 = nStaff2;
        layout.brackets.add(this);
      }

      public static void newBracket(Layout layout, int dyHi, int dyLo) {
        int staff1 = getNStaff1(layout, dyHi);
        int staff2 = getNStaff2(layout, dyLo);
        if(staff1 >= 0  && staff2 >= 0 && staff1 != staff2){
          if(layout.alreadyBracketed(staff1)){
            new Bracket(layout, 2, staff1, staff2);
          }
          new Bracket(layout, 1, staff1, staff2);
        }
      }
      
      public static void newBrace(Layout layout, int dyHi, int dyLo) {
        int staff1 = getNStaff1(layout, dyHi);
        int staff2 = getNStaff2(layout, dyLo);
        if(staff1 >= 0  && staff2 >= 0 && staff1 != staff2){
          new Bracket(layout, 0, staff1, staff2);
        }
      }
      
      public void showAt(Graphics g, int y) {
        g.setColor(UC.inkColor);
        if (eShape == 0) {
          showBraceAt(g, y);
        } else {
          showBracketAt(g, y);
        }
      }

      private static int getNStaff1(Layout layout, int dyHi) { //move to layout class same with getnstaff2
        int res = -1;
        for (int i = layout.fmts.size() - 1; i >= 0; i--) {
          if (layout.fmts.get(i).dy > dyHi) {
            res = i;
          }
        }
        return res;
      }

      private static int getNStaff2(Layout layout, int dyLo) {
        int res = -1;
        for (int i = 0; i < layout.fmts.size(); i++) {
          if (layout.fmts.get(i).dy < dyLo) {
            res = i;
          }
        }
        return res;
      }

      private void showBraceAt(Graphics g, int y) {
        int y1 = layout.fmts.get(nStaff1).dy + y;
        int y2 = layout.fmts.get(nStaff2).dyLastLine()+y;
        int ym = (y1+y2)/2;
        int x = layout.x1;
        int h = layout.defaultH;
        Bar.drawWings(g, x-3*h, y1+h, y2-h, 2*h, h); // /
        Bar.drawWings(g, x-2*h, ym-h, ym+h, -h, ym-y1-2*h); // \
        Bar.drawWings(g, x-4*h, ym, ym, 2*h, h);// <
        
      }

      private void showBracketAt(Graphics g, int y) {
        int y1 = layout.fmts.get(nStaff1).dy + y;
        int y2 = layout.fmts.get(nStaff2).dyLastLine()+y;
        int x = layout.x1;
        int h = layout.defaultH;
        g.fillRect(x - 2 * h, y1, h, y2 - y1);
        Bar.drawWings(g, x-h, y1, y2, 2*h, h);
        if (eShape == 2) {
          g.drawLine(x - 3 * h, y1, x - 3 * h, y2);
          Bar.drawWings(g, x-3*h, y1, y2, h, 0);
        }
      }

      private boolean contains(int nStaff) {
       return nStaff >= nStaff1 && nStaff <= nStaff2;
       //TODO possible bug draws 2nd brackets when there is a brace 
      }
    }

  }
}
