///*
// * Copyright Amanda Eller 2015
// */
//package inkapp;
//
//import GraphicsLib.G;
//import GraphicsLib.G.V;
//import GraphicsLib.G.VS;
//import java.awt.Color;
//import java.awt.Graphics;
//
///**
// *
// * @author Amanda
// */
//public class ShapeTrainer extends Area implements I.Show{
//  public String training = "";
//  private static final int X = UC.ShapeTrainerPrototypeViewMargin;
//  private static final int W = UC.ShapeTrainerPrototypeViewWidth;
//  private static VS prototypeViewBox = new VS(new V(X,X), new V(W,W));
//
//  public ShapeTrainer(G.VS rec, Color c) {
//    super(rec, c);
//  }
//
//  @Override
//  public void dn(int x, int y){
//    inking = true;
//  }
//  
//  @Override
//  public void drag(int x, int y){}
//  
//  @Override
//  public void up(int x, int y){
//    Gesture.Shape s = Gesture.Shape.byName(training); //byName gets the shape and forces the creation of the shape
//    s.train();
//  }
//
//  
//  @Override
//  public void show(Graphics g) {
//    prototypeViewBox.loc.x = X;
//    rec.fill(g, c);
//    g.setColor(Color.BLACK);
//    if(!training.equals("")){
//      Gesture.Shape s = Gesture.shapes.byNameMap.get(training); //byNameMap gets the shape but is allowed to return null
//      if(s == null){
//        g.drawString("Shape " +training+ " does not exist yet", 30,50);
//        return;
//      }
//      g.drawString("Training: " + training +" has "+ s.blends.size()+ " prototypes", 30,50);
//      g.setColor(UC.PrototypeVeiwInkColor);
//      for(Ink.Blend b : s.blends){
//        b.showAt(g, prototypeViewBox);
//        g.drawString("" + b.blendCount, prototypeViewBox.loc.x, X+W);
//        prototypeViewBox.loc.x += W;
//      }
//    }else{
//      g.drawString("Type name of shape you wish to train, bonehead!", 30,50);
//    }
//  }
//  
//}
