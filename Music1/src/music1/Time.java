/*
 * Copyright Amanda Eller 2015
 */
package music1;

import java.util.ArrayList;

/**
 *
 * @author Amanda
 */
public class Time {
  public Sys sys;
  public int x; 
  public ArrayList<Ledger> ledgers = new ArrayList<>();
  
  public Time(Sys sys, int x){this.sys = sys; this.x = x;}
  
  public Ledger getLedgerForStaff(Staff staff){ // can return null
    Ledger res = null;
    for(Ledger l : ledgers){if(l.staff == staff){return l;}}
    return res;
  }
  
  public static class Group extends ArrayList<Time>{
    Sys sys;
    
    public Group(Sys sys){super(); this.sys = sys;}
    
    public Time getTime(int x){
      Time res;
      if(isEmpty()){res = new Time(sys,x); sys.times.add(res); return res;}
      res = get(0); int bestDist = Math.abs(x - res.x);
      for(Time t : this){
        int dist = Math.abs(x - t.x);
        if(dist < bestDist){
          bestDist = dist;
          res = t;
        }
      }
      int H = sys.layout.defaultH;
      if(bestDist > 3*H){res = new Time(sys,x); sys.times.add(res);}
      return res;
    }
  }    
}
