package InkApp;

import java.awt.Color;

import GraphicsLib.G.V;
import GraphicsLib.G.VS;

public class UC {
	public static final int 
          	//Ink
            maximumNumberOfPointsInInkBuffer = 1000,
            numberOfPointsInNorm = 25,
            maxCoordInNorm = 1000,
            //InkApp
            initialWindowHeight = 650,
            initialWindowWidth = 800,
            greenBoxLoc = 40,
            greenBoxSize = 120,
            //Gesture
            dotSize = 5,
            hugeDistance = 1000000,
            //Shape Trainer
            shapeTrainerPrototypeViewMargin = 40,
            shapeTrainerPrototypeViewWidth = 100,
            noBid = 10000,
            undoBid = 10,
            namedInkScale=30;

	public static final String 
            //Gesture
            shapeDBFilePath = "/Users/Amanda/Documents/B_Binder/DadJava/MusicApp/InkApp/shapeDB.dat";
	
	public static final Color inkColor = Color.black,
              prototypeInkColor = Color.red,
              areaSky = new Color(174, 196, 232),
              areaRed = new Color(229, 162, 162),
              areaGreen = new Color(175, 229, 162),
              areaPurple = new Color(186, 162, 229),
              areaTan = new Color(229, 204, 162),
              staffLineColor = Color.LIGHT_GRAY,
              otoBackColor = areaSky,
              otoTextColor = inkColor,
              btnBackColor = areaGreen,
              btnTextColor = inkColor;
              
	
	public static final VS bigVS = new VS(new V(0,0), new V(2000, 2000));

	public static VS namedInkTargetVS = new VS(new V(300,300), new V(namedInkScale, namedInkScale));
  
}
