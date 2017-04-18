/*
 * Copyright Amanda Eller 2015
 */
package music1;

import GraphicsLib.G;
import GraphicsLib.G.VS;
import InkApp.Ink.NamedInk;
import InkApp.Reaction;
import InkApp.Reaction.Mass;
import InkApp.Stroke;
import Music1.MusicApp;
import Music1.Staff;
import java.awt.Graphics;

/**
 *
 * @author Amanda
 */
public class Clef extends Mass implements Elt {
  public int eShape;
  public static String[] clefNames = {"g-clef", "f-clef", "c-clef"};
  public G.VS vs;

  public Clef(int x, Staff sf) {
    super(MusicApp.bars);
    eShape = 0;
    int h = sf.fmt.H;
    this.vs = new VS(new G.V(x,sf.yTop()+4*h), new G.V(h,h));
    addReaction(new Reaction("E-E", "cycle-clefs") {
          public int bid(Stroke s) {
            int x = vs.loc.x-4*h;
            int y = vs.loc.y-4*h;
            return s.middleTopInBox(x, y, x+8*h, y+8*h);
          }
          public void act(Stroke s) {
            cycleShape();
          }
        });
  }

  @Override
  public Measure getMeasure() {
    return null;
  }

  public void cycleShape(){eShape = cycle(eShape);}
  
  public static NamedInk getNamedInk(int eShape) {
    return Stroke.shapes.nInkMap.get(clefNames[eShape]);
  }

  public static int cycle(int shape) {
    return (shape + 1) % clefNames.length;
  }

  @Override
  public void show(Graphics g) {
    NamedInk ink = getNamedInk(eShape);
    if (ink != null) {
      ink.showAt(g, vs);
    }
  }
}
