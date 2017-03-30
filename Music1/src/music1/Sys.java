/*
 * Copyright Amanda Eller 2015
 */
package Music1;

import java.util.ArrayList;

/**
 *
 * @author Amanda
 */
public class Sys {
  public int y;
  public ArrayList<Staff> staffs;
  public Sys.Layout layout;
  public int m1, m2;
  
  public Sys(Layout layout, int y, int m1, int m2){
    this.layout = layout;
    this.y = y;
    this.m1 = m1;
    this.m2 = m2;
    this.staffs = new ArrayList<>();
    for(int i = 0; i< layout.fmts.size(); i++){
      staffs.add(layout.fmts.get(i).getNewStaff(this,i));
    }
  }
  
  
  public static class Layout{
    public int x1, x2;
    public int defaultH;
    public ArrayList<Staff.Fmt> fmts;
    public ArrayList<Bracket> brackets;
    
    public Layout(int x1, int x2, int defaultH){
      this.x1 = x1;
      this.x2 = x2;
      this.defaultH = defaultH;
      fmts = new ArrayList<>();
      brackets = new ArrayList<>();
    }
    
  }
  
  
  public static class Bracket{
    public int nStaff1, nStaff2;
    public int eShape;
  
  }
  
}
