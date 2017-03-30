package Music1;
import InkApp.*;
import GraphicsLib.Window;
import InkApp.Reaction.Mass;
import InkApp.Reaction.Oto;
import InkApp.Reaction.Oto.Action;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

public class MusicApp extends Window{
	    public static final int W = UC.initialWindowWidth,  
	    		H = UC.initialWindowWidth; 
//	    		GBL = UC.greenBoxLoc,
//	    		GBS = UC.greenBoxSize;
//	    public static G.VS BACKGROUND = new VS(new V(), new V(W,H)); 
//	    public static VS nb = new VS(new BBox(GBL,GBS));
	 //   public static InkList inkList = null;
	 //   public static Music1 music1 = new Music1();
	  //  public static Layer layer =  Layer.getNewLayer();
      //public static Demo dem = new Demo(40,40);
	     
	    public MusicApp() {
	      super("Music App", W, H);
        new Oto("Train Shapes", 100,100, new Action(){
          public void execute(){
            Trainer.trainShapes("NE-SE");
          }
        });
        new Oto("Train Named Shapes", 200,100, new Action(){
          public void execute(){
           Trainer.trainNamedInk("df1");
          }
        });
        new Oto("Define System", 100,150, new Action(){
          public void execute(){
          new Demo(300,300); 
          //TODO LayoutEditor more Robust!!
          }
        });
	    }
	    
	    protected void paintComponent(Graphics g){
	      UC.bigVS.fill(g, Color.WHITE);
	      //g.setColor(Color.BLACK);
	      Layer.ALL.show(g); 
	    }
	    
	    public void mousePressed(MouseEvent me){
	      int mx = me.getX(), my = me.getY();
	      Area.DN(mx, my);
	      repaint();
	    }
	    
	    @Override
	    public void mouseReleased(MouseEvent me){
	      int mx = me.getX(), my = me.getY();
	      Area.UP(mx, my);
	      repaint();
	    }
	    
	    @Override
	    public void mouseDragged(MouseEvent me){
	      int mx = me.getX(), my = me.getY();
	      Area.DRAG(mx, my);
	      repaint();
	    }
	    
	    @Override
	    public void keyTyped(KeyEvent ke) {
		    	char c = ke.getKeyChar();
		    	if(Trainer.isActive) {
		    		Trainer.theTrainer.charTyped(c);
		    		repaint();
		    	}
	    }
      
      public static void main(String[] args) {
	      PANEL = new MusicApp();
	      launch();
	    }
	   
	    public static class Demo extends Mass{
        public int x, y;
        
        public Demo(int x, int y){
          Mass.DEFAULT = this;
          this.x = x; this.y = y;
          getLayer().add(this);
          addReaction(new Reaction("E-E","show dude") {
            public int bid(Stroke g) {
              return 50; 
            }
            public void act(Stroke g) {
                new Demo(g.xm(),g.ym());
            }
          });
        }

        @Override
        public Layer getLayer() {
          return Layer.BACK;
        }

        @Override
        public void show(Graphics g) {
          g.setColor(Color.black);
          g.drawString("DUDE!!", x, y);
        }
      }
      
      
//	    public static class InkArea extends Area {
//
//			public InkArea() {
//				super(BACKGROUND, Color.white, "MusicApp : InkArea");
//			}
//
//			public void dn(int x, int y) {
//				inking = true;
//			}
//			
//			public void up(int x, int y) {
//				inkList.addNew();
//			}
//	    }
	    
//	    public static class InkList extends ArrayList<Ink> implements I.Show{
//	    	
//	      public void addNew(){
//	    	  Blend newbl = (new Ink.Blend()).setBlendFromBuffer();
//	    	  if(size() == 0) {
//	    		  this.add(new Ink(newbl, new BBox(Ink.BUFFER.bbox)));
//	    	  }
//	    	  else {
//	    		  Blend blend = findClosest(newbl);
//	    		  if(blend.distToNorm(newbl) > UC.hugeDistance) {
//	    			  this.add(new Ink(newbl, new BBox(Ink.BUFFER.bbox)));
//	    		  } else {
//	    			  blend.blend(newbl);
//	    			  this.add(new Ink(blend, new BBox(Ink.BUFFER.bbox)));
//	    		  }
//	    		  
//	    	  }
//	      }
//	      
//	      public Blend findClosest(Ink.Norm n) {  
//	    	  Blend res = get(0).blend;
//	    	  int d = res.distToNorm(n);
//	    	  for(int i=1;i<size();i++) {
//	    		  int k = get(i).blend.distToNorm(n);
//	    		  if(k<d) {
//	    			  d = k;
//	    			  res = get(i).blend;
//	    		  }
//	    	  }
//	    	  return res;
//		  }
//	      
//	      public void show(Graphics g){for(Ink ink:this){ink.show(g);}}
//	    }
	   
}
