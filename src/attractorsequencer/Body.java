package attractorsequencer;

import toxi.geom.*;
import processing.core.*;

public class Body {
	Vec2D bPoint;
	float x, y;
	int userId;
	int userPointSize = 25;
	PApplet parent;
	
	Body(float x, float y, int userId, PApplet p){
		parent = p;
		
		this.x = x;
		this.y = y;
		this.userId = userId;
		
		bPoint = new Vec2D(x, y);
	}
	
	Vec2D getVec(){
		return bPoint;
	}
	
	public void display(){
		parent.ellipse(bPoint.x, bPoint.y, userPointSize, userPointSize);
	}
}
