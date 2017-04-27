/*
 * Copyright by Marlin Eller 2017
 */
package music1;

import java.awt.Graphics;

/** contains routines for drawing the shapes
 *  and routines for the logic of what type of shape to place where.
 *  These routines draw every accid at most 2*H wide
 *  
 * @author Marlin
 */
public class Accid {
  
   
  public static void sharpAt(Graphics g, int x, int y, int h){
    int ho2 = h/2;
    g.drawLine(x-h, y-ho2, x+h, y-h);    
    g.drawLine(x-h, y+h, x+h, y+ho2);    
    g.drawLine(x-ho2, y-h, x-ho2, y+h+ho2);    
    g.drawLine(x+ho2, y-h-ho2, x+ho2, y+h);    
    
  }
  public static void flatAt(Graphics g, int x, int y, int h){
    g.drawLine(x, y-2*h, x, y+h);
    g.drawLine(x, y+h, x+h, y);
    g.drawLine(x+h, y, x, y-h/2);
  }
  public static void naturalAt(Graphics g, int x, int y, int h){
    int ho2 = h/2; int h2 = 2*h;
    g.drawLine(x-ho2, y-h2, x-ho2, y+h);
    g.drawLine(x-ho2, y+h, x+ho2, y+ho2);
    g.drawLine(x+ho2, y+h2, x+ho2, y-h);
    g.drawLine(x+ho2, y-h, x-ho2, y-ho2);
    
  }
  public static void doubleSharpAt(Graphics g, int x, int y, int h){
    g.drawLine(x-h, y-h, x+h, y+h);
    g.drawLine(x-h, y+h, x+h, y-h);
  }

  public static void doubleFlatAt(Graphics g, int x, int y, int h){
    flatAt(g,x,y,h); flatAt(g,x-h,y,h);
  }
  
 

  public static int[] sharpLines = {0,3,-1,2,5,1,4};
  public static int[] flatLines = {4,1,5,2,6,3,7};
  public static int[] clefOffset = {0,2,1};
  
  public static void keySigAt(Graphics g, int key, int dKey, int x, int y, int eClefShape, int h){
    // music principle - if dKey is -3, you are going from a key to its
    // relative minor, It is courtasy to show (with naturals) the removal
    // also, if the result is C, it is customary to show the removal with
    // naturals
    int cs = clefOffset[eClefShape];
    if(key < 0){
      key = -key;
      for(int i = 0; i<key; i++){
        flatAt(g, x, y + (flatLines[i]+cs)*h, h);
        x+=2*h;
      }
      
    } else {
      for(int i = 0; i<key; i++){
        sharpAt(g, x, y + (sharpLines[i]+cs)*h, h);
        x+=2*h;
      }      
    }
    
    if(dKey != 0 && key == 0){
      if(dKey > 0){ // delta was positive so we are removing flats
        naturalLoop(g,x,y,h,flatLines, cs, 0, dKey);
      }else{
        naturalLoop(g,x,y,h,sharpLines,cs,0,-dKey);
      }
    }
  } 
  
  private static void  naturalLoop(Graphics g, int x, int y, int h, int[] lines, int cs, int i1, int i2){
    for(int i = i1; i<i2; i++){
      naturalAt(g, x, y + (lines[i] + cs)*h, h);
      x+=2*h;
    }
  }
 
}
