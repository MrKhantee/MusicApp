package InkApp;

import java.awt.Graphics;

public interface I {
	public interface Show {
		public void show(Graphics g);
	}
	
	public interface Act {
		public void act(Stroke s);
	}
  
	public interface React extends Act{
		public int bid(Stroke s);
	}
}
