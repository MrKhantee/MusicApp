/*
 * Copyright Amanda Eller 2015
 */
package music1;

import InkApp.UC;
import java.awt.Graphics;

/**
 *
 * @author Amanda
 */
public class Bar {
  public int eShape;
  public Time.Group times;
  
  public static void drawWings(Graphics g, int x, int y1, int y2, int dx, int dy){
    g.drawLine(x, y1, x+dx, y1-dy);
    g.drawLine(x, y2, x+dx, y2+dy);
  }
  
}
