/*
 * Copyright Amanda Eller 2015
 */
package music1;

import InkApp.Reaction;
import InkApp.Reaction.Mass;
import InkApp.Stroke;
import InkApp.UC;
import java.awt.Graphics;
import java.util.ArrayList;

/**
 *
 * @author Amanda
 */
public class Time extends Mass{
  public Sys sys;
  public int x; 
  public ArrayList<Ledger> ledgers = new ArrayList<>();
  
  public Time(Sys sys, int x){
    super(MusicApp.times);
    this.sys = sys; 
    this.x = x;
    addReaction(new Reaction("S-S","Stem heads"){
      public int bid(Stroke s){
        int h = sys.layout.defaultH;
        if(s.xm() > x + 3*h  || s.xm() < x - 3*h ){return UC.noBid;}
        int y1 = sys.y; int y2 = sys.yBot();
        if(s.yu() >y2 || s.yd() < y1){return UC.noBid;}
        return Math.abs(x - s.xm());
      }
      public void act(Stroke s){
        boolean upStem = s.xm() > Time.this.x;
        ArrayList<Head> stemHeads = new ArrayList<Head>();
        for(Ledger l: ledgers){
          for(Head h: l.heads){
            h.joinStem(stemHeads, s.yu(), s.yd());
          }
        }
        if(! stemHeads.isEmpty()){new Stem(stemHeads, upStem, Time.this, s.yu(), s.yd());}
      }
    });
  }
  
  public Ledger getLedgerForStaff(Staff staff){ // can return null
    Ledger res = null;
    for(Ledger l : ledgers){if(l.staff == staff){return l;}}
    return res;
  }

  public int dist(int x){return Math.abs(x - this.x);}
  
  @Override
  public void show(Graphics g) {}
  
  public static class Group extends ArrayList<Time>{
    Sys sys;
    
    public Group(Sys sys){super(); this.sys = sys;}
    
     public Time getTime(int x){ // factory method creates time if needed
      Time res = getClosestTime(x);
      int H = sys.layout.defaultH;
      if(res == null || res.dist(x) > 3*H){
        res = new Time(sys,x); 
        sys.times.add(res);
      }
      return res;
    }
    
    public Time getClosestTime(int x){ // can return null
      Time res = null; int bestDist = 1000000;
        for(Time t : this){
          int dist = Math.abs(x - t.x);
          if(dist < bestDist){
            bestDist = dist;
            res = t;
          }
        }
        return res;
    }
  }
}
