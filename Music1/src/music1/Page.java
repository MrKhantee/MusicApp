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
public class Page extends Mass {
  public ArrayList<Sys> systems;
  
  public Page() {
    super(MusicApp.staffs);
    systems = new ArrayList<>();
    addReaction(new Reaction("E-E", "add-sys to the page") {
          public int bid(Stroke s) {
            return (s.ym() > guideline()) ? 100 : UC.noBid;
          }
          public void act(Stroke s) {
            Sys sy = lastSys();
            systems.add(new Sys(MusicApp.theLayout, s.ym(), sy));
          }
        });
  }
  
  public int guideline(){
    return systems.isEmpty() ? 5 : lastSys().yBot();
  }
  
  public Sys lastSys(){
    return systems.isEmpty() ? null : systems.get(systems.size()-1);
  }
  
  @Override
  public void show(Graphics g) {
    g.setColor(Color.BLUE);
    g.drawLine(MusicApp.theLayout.x1 + 100, guideline(), MusicApp.theLayout.x2 - 100, guideline());
  }
  
}
