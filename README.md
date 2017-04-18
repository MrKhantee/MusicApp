# MusicApp
Gesture base music app that lets users create music notations by using very basic gestures to create the different features. 

This framework was created in collaborations with Proffessor Marlin Eller at Northeastern University. 

## There are three main components to this project

1. GraphicsLib - as points accumulates from the gesture, the GraphicLibs creates a bounding box around the points in order to scale the stroke for furture comparison. Holds 2D vectors and coordinates.

2. InkApp - this holds the gestures recognition and mappings for gestures and their name.

3. Music1 - this is the application that uses basic gestures to create music notation. 
