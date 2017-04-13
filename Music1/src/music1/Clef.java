/*
 * Copyright Amanda Eller 2015
 */
package music1;

import InkApp.Ink;
import InkApp.Ink.NamedInk;
import InkApp.Stroke;

/**
 *
 * @author Amanda
 */
public class Clef implements Elt{
  public int eShape;
  public static String[] clefNames= {"g-clef", "f-clef","c-clef"};

  @Override
  public Measure getMeasure() {
    return null;
  }
  
  public static NamedInk getNamedInk(int eShape){return Stroke.shapes.nInkMap.get(clefNames[eShape]);}
  
  public static int cycle(int shape) {
    return (shape+1)%clefNames.length;
  }
}
