package InkApp;

import java.awt.Color;
import java.awt.Graphics;

import GraphicsLib.G.V;
import GraphicsLib.G.VS;
import InkApp.Stroke.Shape;

public class Trainer extends Area implements I.Show{
	private static final int X = UC.shapeTrainerPrototypeViewMargin,
			W = UC.shapeTrainerPrototypeViewWidth;
	private static VS prototypeViewBox = new VS(new V(X,X), new V(W,W));
	
	public static Trainer theTrainer = new Trainer();
	
	public static boolean isActive = false;
	public static boolean trainingShapes = true;
	public static String trainingName = "";
	public static int iStroke = 0, maxStroke = 0;
	
	private Trainer() {
		super(UC.bigVS,Color.yellow, "Trainer");
		Area.all.remove(this); 
//		System.out.println("In trainer constructor : size of area : "+Area.all.size());
		//we want the trainer to go on the area list only when activated
	}
	
	public static void trainShapes(String name) {activate(name,true);}
	public static void trainNamedInk(String name) {activate(name,false);}

	private static void activate(String name, boolean b) {
		trainingShapes = b;
		isActive = true;
		Area.all.add(theTrainer);
		iStroke = 0;
		maxStroke = 0;
		trainingName = name;
		Layer.FORE.add(theTrainer);
//		System.out.println("In trainer activate : size of area : "+Area.all.size());
	}
	
	private static void deactivate() {
//		System.out.println("from Trainer -> deactivate : length of area.all "+Area.all.size());
		Area.all.remove(theTrainer);
		Layer.FORE.remove(theTrainer);
		isActive = false;
		trainingName = "";
//		System.out.println("from Trainer -> deactivate : length of area.all updated "+Area.all.size());
	}

	@Override
	public void show(Graphics g) {
		UC.bigVS.fill(g, Color.yellow);
		if(trainingName == "") {
			showInstructions(g);
			return;
		}
		Shape shape;
		Ink.NamedInk ink;
		String msg = "Training ";
		if(trainingShapes) {
			msg += "shape: " + trainingName;
			shape = Stroke.shapes.shapeMap.get(trainingName);
			if(shape != null) {
				shape.show(g);
			} else {
				msg += " NOT DEFINED";
			}
		} else {
			showGuidelines(g);
			msg += "namedink: " + trainingName;
			ink = Stroke.shapes.nInkMap.get(trainingName);
			if(ink != null) {
				if(maxStroke==0) {
					maxStroke = ink.blends.size();
				}
			} else {
				msg += " NOT DEFINED - Enter strokecount";
			}
			if(ink!=null) {
				showPartialNamedInk(g, ink);
			}
		}
		g.setColor(Color.black);
		g.drawString(msg, 20, 30);
		Ink.BUFFER.show(g);
	}
	
	private void showInstructions(Graphics g) {
		g.setColor(Color.BLACK);
		g.drawString("Type the name of the or NamedInk to see it and to train it", 30, 50);
		g.drawString("Hit Enter key to clear the name and return to these instructions", 30, 80);
		g.drawString("If you want to keep all the training, clear name (Enter) and type SAVE", 30, 110);
		g.drawString("to Quit training, clear name (Enter) and type EXIT", 30, 140);
	}
	
	private void showGuidelines(Graphics g) {
		VS vs = UC.namedInkTargetVS;
		g.setColor(Color.lightGray);
		for(int i=-4;i<5;i+=2) {
			int y = vs.loc.y + i*vs.size.y;
			g.drawLine(0, y, 1000, y);
		}
		vs.draw(g, Color.green);
		g.setColor(Color.red);
		g.drawOval(vs.loc.x-2, vs.loc.y-2, 4, 4);
	}
	
	private void showPartialNamedInk(Graphics g, Ink.NamedInk ink) {
		//completed strokes : Black, current stroke : Red, future strokes : Blue
		Color c = Color.black;
		for(int i=0;i<ink.blends.size(); i++) {
			if(i == iStroke) {
				c= color.red;
			}
			if(i > iStroke) {
				c = Color.BLUE;
			}
			g.setColor(c);
			ink.blends.get(i).showAt(g, UC.namedInkTargetVS);
		}
	}
	
	@Override
	public void dn(int x, int y) {
		inking = true;
	}
	@Override
	public void drag(int x, int y) {}
	@Override
	public void up(int x, int y) {
		if(trainingName != "") {
			if(trainingShapes) {
				Stroke.Shape s = Stroke.Shape.byName(trainingName);
				s.train();
			} else {
				Ink.NamedInk t = Stroke.shapes.nInkMap.get(trainingName);
				if(t==null && maxStroke>0) {
					t = new Ink.NamedInk(trainingName, maxStroke);
				}
				if(t!=null) {
					t.train(iStroke, UC.namedInkTargetVS);
					iStroke = (iStroke + 1) % maxStroke;
				}
			}
		}
	}
	
	public void charTyped(char c) {
	    	String t =  trainingName;
	    	iStroke = 0;
	    	System.out.println("In trainer chartyped : maxstrocke count : " + maxStroke);
	    	if(c >= '1' && c<='9' && maxStroke == 0 && !trainingShapes){
	    		maxStroke = c-'0';
	    	} else {
	    		maxStroke = 0;
	    		trainingName = c == '\n' ? "" : t+c;
	    	}
	    	if(trainingName.equals("SAVE")) {
	    		Stroke.Shape.saveDB();
	    	}
	    	if(trainingName.equals("EXIT")) {
	    		deactivate();
	    	}
	}

}
