package InkApp;

import java.awt.Graphics;
import java.util.ArrayList;

import javax.swing.text.StyledEditorKit.ForegroundAction;

public class Layer extends ArrayList<I.Show> implements I.Show{
	public static List ALL = new List();
	public static Layer BACK = new Layer(true);
	public static Layer FORE = new Layer(false);
	
	private Layer(Boolean b) {
		super();
		if(b) {
			ALL.add(this);
		}
	}
	
	public static Layer getNewLayer() {
		return new Layer(true);
	}
	
	@Override
	public void show(Graphics g) {
		for (I.Show s : this) {
			s.show(g);
		}
	}
	
	public static void clearAll() {
		FORE.clear();
		for(Layer layer : ALL) {
			layer.clear();
		}
	}
	
	public static class List extends ArrayList<Layer> implements I.Show{
		
		@Override
		public void show(Graphics g) {
			//System.out.println(BACK.size());
			for (I.Show s : this) {
				s.show(g);
				
			}
			
			FORE.show(g);
			g.setColor(UC.inkColor);
			Ink.BUFFER.show(g);
		}
		
	}
}
