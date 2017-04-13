/*
 * Copyright Amanda Eller 2015
 */
package Music1;

import InkApp.Ink.NamedInk;
import InkApp.Reaction;
import InkApp.Reaction.Button;
import InkApp.Reaction.Mass;
import InkApp.Stroke;
import InkApp.UC;
import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;
import music1.Bar;
import music1.Clef;

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

    public Staff.Fmt getFmtBeforeDy(int dy) {
      Staff.Fmt res = null;
      for (Staff.Fmt f : fmts) {
        if (f.dy < dy) {
          res = f;
        }
      }
      return res;
    }
    
    public Bracket findBracket(int nStaff){
      Bracket res = null;
      for(Bracket b: brackets){
        if(b.contains(nStaff)){
          res = b;
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
        addReaction(new Reaction("E-E", "cycle-staffs") {
          public int bid(Stroke g) {
            if(g.xl() < 100){return UC.noBid;}
            return (g.vs.loc.y < guideline(y)) ? 100: UC.noBid;
          }
          public void act(Stroke g) {
            Staff.Fmt s = getFmtBeforeDy(g.yu()-y);
            if(s == null){s = fmts.get(0);}
            if(s == null){return;}
            s.cycleLines();
          }
        });
        addReaction(new Reaction("E-E", "cycle-clefs") {
          public int bid(Stroke g) {
            if(g.xm() > x1+50){return UC.noBid;}
            return (g.vs.loc.y < guideline(y)) ? 100: UC.noBid;
          }
          public void act(Stroke g) {
            Staff.Fmt s = getFmtBeforeDy(g.yu()-y);
            if(s == null){s = fmts.get(0);}
            if(s == null){return;}
            s.defaultClefEShape = Clef.cycle(s.defaultClefEShape);
          }
        });
        addReaction(new Reaction("S-S", "add-brackets to the sys-layout") {
          public int bid(Stroke g) {
            if (g.xm() > x1) {return UC.noBid;}
            if (fmts.size() < 2) {return UC.noBid;}
            return 50;
          }

          public void act(Stroke g) {
            Bracket.newBracket(Layout.this, g.yu() - y, g.yd() - y);
          }
        });
        addReaction(new Reaction("SW-SE", "add-braces to the sys-layout") {
          public int bid(Stroke g) {
            if (g.xm() > x1) {return UC.noBid;}
            if (fmts.size() < 2) {return UC.noBid;}
            return 50;
          }

          public void act(Stroke g) {
            Bracket.newBrace(Layout.this, g.ym() - y);
          }
        });
        addReaction(new Reaction("S-S", "adding a Bar continues to the sys-layout") {
          public int bid(Stroke g) {
            if (g.xm() < x1) {return UC.noBid;}
            if (fmts.size() < 2) {return UC.noBid;}
            Staff.Fmt s = getFmtBeforeDy(g.yu()-y);
            if(s == null){s = fmts.get(0);}
            if(s.nStaff == fmts.size()-1){return UC.noBid;}
            return Math.abs(s.dyLastLine()+y - g.yu()) + Math.abs(fmts.get(s.nStaff+1).dy+y - g.yd());
          }

          public void act(Stroke g) {
            Staff.Fmt s = getFmtBeforeDy(g.yu()-y);
            if(s == null){s = fmts.get(0);}
            s.barContinues = !s.barContinues;
          }
        });
        //toggle bar connection S-S
        //cycle lines and clefs E-E
      }

      @Override
      public void show(Graphics g) {
        g.setColor(Color.BLUE);
        g.drawLine(x1 + 100, guideline(y), x2 - 100, guideline(y));
        showAt(g, y);
        int x = 350;
        for(Staff.Fmt f:fmts){
          g.drawLine(x, f.dy+y, x, f.dyLastLine()+y);
          if(f.barContinues){
            g.drawLine(x, f.dyLastLine()+y, x, fmts.get(f.nStaff+1).dy +y);
          }
          if(f.lines == Staff.Fmt.MUSIC_STAFF){
            NamedInk ink = Clef.getNamedInk(f.defaultClefEShape);
            if(ink != null) {
              f.clefBox.loc.y = f.dy+y+4*defaultH;
              ink.showAt(g, f.clefBox);
            }
          }
        }
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
        Staff.Fmt s1 = layout.getFmtBeforeDy(dyHi);
        Staff.Fmt s2 = layout.getFmtBeforeDy(dyLo);
        if (s2 == null) {return;}
        int staff1 = (s1 == null) ? 0 : s1.nStaff + 1;
        int staff2 = s2.nStaff;
        if (staff1 >= 0 && staff2 >= 0 && staff1 != staff2) {
          Bracket b = layout.findBracket(staff1);
          if (b != null) {
            new Bracket(layout, 2, staff1, staff2);
          }
          new Bracket(layout, 1, staff1, staff2);
        }
      }

      public static void newBrace(Layout layout, int dy) {
        Staff.Fmt s1 = layout.getFmtBeforeDy(dy);
        if (s1 == null) {return;}
        int staff1 = s1.nStaff;
        if (staff1 >= 0 && staff1 < layout.fmts.size() - 1) {
          Bracket b = layout.findBracket(staff1);
          Bracket b2 = layout.findBracket(staff1 +1);
          if(b2 != null){return;}
          if(b != null){
            b.nStaff2++;
            System.out.println("eShape of extending bracket " +b.eShape);
          }else{
            new Bracket(layout, 0, staff1, staff1 + 1);
          }
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

      private void showBraceAt(Graphics g, int y) {
        int y1 = layout.fmts.get(nStaff1).dy + y;
        int y2 = layout.fmts.get(nStaff2).dyLastLine() + y;
        int ym = (y1 + y2) / 2;
        int x = layout.x1;
        int h = layout.defaultH;
        Bar.drawWings(g, x - 3 * h, y1 + h, y2 - h, 2 * h, h); // /
        Bar.drawWings(g, x - 2 * h, ym - h, ym + h, -h, ym - y1 - 2 * h); // \
        Bar.drawWings(g, x - 4 * h, ym, ym, 2 * h, h);// < 
      }

      private void showBracketAt(Graphics g, int y) {
        int y1 = layout.fmts.get(nStaff1).dy + y;
        int y2 = layout.fmts.get(nStaff2).dyLastLine() + y;
        int x = layout.x1;
        int h = layout.defaultH;
        g.fillRect(x - 2 * h, y1, h, y2 - y1 + 1);
        Bar.drawWings(g, x - h, y1, y2, 2 * h, h);
        if (eShape == 2) {
          g.drawLine(x - 3 * h, y1, x - 3 * h, y2);
          Bar.drawWings(g, x - 3 * h, y1, y2, h, 0);
        }
      }

      private boolean contains(int nStaff) {return nStaff >= nStaff1 && nStaff <= nStaff2;}
    }

  }
}
