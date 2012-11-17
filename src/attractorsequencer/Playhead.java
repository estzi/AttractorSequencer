package attractorsequencer;

import processing.core.PApplet;
import java.util.*;

public class Playhead {
	PApplet parent;
	
	float xPos, xSpeed;
	ArrayList<Float> pPosX;
	
	Playhead(PApplet p){
		parent = p;
		xPos = 0;
		xSpeed = 1;
	}
	
	public float getPosition(){
		return xPos;
	}
	
	public boolean particleCheck(float px){
		if(xPos > px){
			return true;
		}else{
			return false;
		}
	}
	
	public void update(){
		//update xPos in here
		if(xPos > parent.width){
			xPos = 0;
			xSpeed = parent.random(1, 5);
			//PApplet.println(xSpeed);
		}else{
			xPos += xSpeed;	
		}
	}
	
	public void display(){
		parent.stroke(255);
		parent.strokeWeight(1);
		parent.line(xPos, 0, xPos, parent.height);
	}
}
