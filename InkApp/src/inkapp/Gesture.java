///*
// * Copyright Amanda Eller 2015
// */
//package inkapp;
//
//import GraphicsLib.G.VS;
//import java.io.FileInputStream;
//import java.io.FileOutputStream;
//import java.io.ObjectInputStream;
//import java.io.ObjectOutputStream;
//import java.io.Serializable;
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.Map;
//
///**
// *
// * @author Amanda
// */
//public class Gesture implements I.Act{
// public Shape shape;
// public VS vs;
// 
// public static Shape.DB shapes = Shape.getDB();
// 
// public Gesture(Shape shape, VS vs){
//  this.shape = shape;
//  this.vs = vs;
// }
// 
// public Gesture(){
//   if(inkIsDot()){shape = Shape.DOT;}else{shape = shapes.getBestMatch(Ink.BUFFER.norm);}
//   vs = new VS(Ink.BUFFER.bbox);
// }
//
//  private static boolean inkIsDot() {
//    return (Ink.BUFFER.bbox.h.s < UC.DotSize && Ink.BUFFER.bbox.v.s < UC.DotSize);
//  }
//
//
//  public void act(Gesture g) {
//    Reaction r = Reaction.bestReaction(g);
//    if(r != null){
//      System.out.println(r.purpose);
//      r.act(g);
//    }
//  }
// 
//  public int xm(){return vs.xm();}
//  public int xl(){return vs.xl();}
//  public int xr(){return vs.xr();}
//
//  public int ym(){return vs.ym();}   
//  public int yu(){return vs.yu();}
//  public int yd(){return vs.yd();} 
//  
// public static class Shape implements Serializable{
//   public String name;
//   public ArrayList<Ink.Blend> blends = new ArrayList<>();
//   public static Shape DOT = new Shape("DOT");
//   public static String FNAME = UC.ShapeDBPathName;
//   
//   public Shape(String name){
//     this.name = name;
//   }
//   
//   public static Shape byName(String name){
//     if(name.equals("DOT")){
//       return DOT;
//     }
//     Shape res = shapes.byNameMap.get(name);
//     if(res==null){
//       res = new Shape(name);
//       shapes.add(res);
//     }
//     return res;
//   }
//   
//   public static DB getDB(){
//     DB res;
//     try {
//        FileInputStream fin = new FileInputStream(FNAME);
//        ObjectInputStream oin = new ObjectInputStream(fin);
//        res = (DB) oin.readObject();
//        oin.close();
//        fin.close();
//      }catch(Exception e) {
//        System.out.println("Couldn't find file "+ FNAME + " so used default values.");
//        res = new DB();
//      }
//      return res;
//   }
//   
//   public static void saveDB(){
//    try {
//        FileOutputStream fout =new FileOutputStream(FNAME);
//        ObjectOutputStream oout = new ObjectOutputStream(fout);
//        oout.writeObject(shapes);
//        oout.close();
//        fout.close();
//      }catch(Exception e) {
//          System.out.println("WTF? Saving file: "+ FNAME);
//          e.printStackTrace();
//        }   
//   }
//
//    public int dist(Ink.Norm n) {
//      int res = UC.HugeDistance;
//      if(blends.isEmpty()){System.out.println("you need some training for shape: " + name);}
//      for(Ink.Blend b:blends){
//        int d = b.distToNorm(n);
//        if(d<res){res = d;}
//      }
//      return res;
//    }
//    
//    
//    public void train(){
//      if(name.equals("DOT")){return;}
//      if(blends.isEmpty()){
//        blends.add(Ink.Blend.newBlendFromInk());
//        return;
//      }
//      Ink.Blend best = blends.get(0);
//      int bestD = best.distToNorm(Ink.BUFFER.norm);
//      for(Ink.Blend b: blends){
//         int d = b.distToNorm(Ink.BUFFER.norm);
//         if(d<bestD){bestD = d; best = b;}
//      }
//      System.out.println("train dist = " + bestD);
//      if(bestD < UC.HugeDistance){
//        best.blend(Ink.BUFFER.norm);
//      }else{
//        blends.add(Ink.Blend.newBlendFromInk());
//      }
//    }
//    
//   public static class DB implements Serializable{
//     public ArrayList<Shape> shapes = new ArrayList<>();
//     public Map<String, Shape> byNameMap = new HashMap<>();
//   
//     public DB(){}
//  
//     public void add(Shape s) {
//       if(s.name.equals("DOT")){return;}
//       shapes.add(s);
//       byNameMap.put(s.name,s);       
//     }   
//     
//     public Shape getBestMatch(Ink.Norm n){
//       if(shapes.isEmpty()){return Shape.DOT;}
//       Shape bestShape = shapes.get(0);
//       int bestDist = bestShape.dist(n);
//       for(Shape s: shapes){
//         int d = s.dist(n);
//         if(d<bestDist){bestDist = d; bestShape = s;}
//       }
//       return bestShape;
//     }
//   }
// }
//}
