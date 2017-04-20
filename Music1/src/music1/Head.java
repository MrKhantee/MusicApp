/*
 * Copyright Amanda Eller 2015
 */
package music1;

import InkApp.Reaction.Mass;
import java.awt.Color;
import java.awt.Graphics;

/**
 *
 * @author Amanda
 */
public class Head extends Mass {
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
  }

  @Override
  public void show(Graphics g) {
    g.setColor(Color.BLACK);
    int H = staff.fmt.H;
    int x = time.x + dxSide;
    int y = staff.yTop() + line(x) * H;
    if (eShape == QUARTER) {
      g.fillOval(x - 3 * H / 2, y - H, 3 * H, 2 * H);
    } else {
      g.drawOval(x - 3 * H / 2, y - H, 3 * H, 2 * H);
    }
  }

  //public int lineFromY(int y){return (y - (ytop()-H) + H/2)/H;}
  public int line(int x) {
    return Clef.midCLine[staff.getClefShapeAt(x)] - deg;
  }
}
