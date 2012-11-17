package attractorsequencer;

import processing.core.PVector;
import processing.core.PApplet;
import SimpleOpenNI.*;

/**
 * 
 * @author Estzi
 * there is a matching max patch for this here:
 * Users/Estzi/Documents/_MaxMSP/AttractorSequencer/
 * 
 */

public class AttractorSequencer extends PApplet {

	SimpleOpenNI kinect;
	ParticleHandler particleHandler;
	Playhead playhead;
			
	boolean showDepthImage = true;
	boolean showUserPoint = true;
	
	boolean kinectConnected = false;
	boolean setInitPoint = false;
	int mouseUser = 1;
	
	//our global vars for the user
	float numUsers;
	float previousUsers = 0;
	float userPointX, userPointY;
	int centerAdjust = 50;
	
	ParticleData tempData;
	
	public void setup() {
		size(640, 480, P2D);
				
		if(kinectConnected){
			kinect = new SimpleOpenNI(this);
			kinect.setMirror(true);
			kinect.enableDepth();
			kinect.enableUser(SimpleOpenNI.SKEL_PROFILE_NONE);
		}
		
		particleHandler = new ParticleHandler(this);
		playhead = new Playhead(this);
		smooth();
	}

	public void draw() {
		blendMode(ADD);
		background(0);
		
		if(kinectConnected){
			kinect.update();
			
			if(showDepthImage){
				image(kinect.depthImage(), 0, 0, width, height);
			}
			
			IntVector userList = new IntVector();
			kinect.getUsers(userList);
			numUsers = userList.size();
			
			//every new user set new attraction point
			if(numUsers > previousUsers){
				int curUser = (int)numUsers;
				PVector position = new PVector();
				kinect.getCoM(curUser, position);
				kinect.convertRealWorldToProjective(position, position);
				particleHandler.setAttractionPoint(position.x, position.y, curUser);
				
				previousUsers = numUsers;
			}
			
			for (int i=0; i<userList.size(); i++){
				int userId = userList.get(i);
				PVector position = new PVector();
				kinect.getCoM(userId, position);
				kinect.convertRealWorldToProjective(position, position);
				//if we scale the canvas, we always get
				//an accurate point
				userPointX = map(position.x, 0, 640, 0, width);
				userPointY = map(position.y, 0, 480, 0, height)-centerAdjust;
				
				//create a point for each body
				if(showUserPoint){
					fill(255, 0, 0);
					ellipse(userPointX, userPointY, 25, 25);
				}
				
				if(userList.size() > 0){
					particleHandler.updateAttractionPoint(userPointX, userPointY, userId);
				}
			}
		}else{
			//set our initial point as the mouseUser
			if(!setInitPoint){
				particleHandler.setAttractionPoint(mouseX, mouseY, mouseUser);
				setInitPoint = true;
			}
			
			particleHandler.updateAttractionPoint(mouseX, mouseY, mouseUser);
		}
		
		particleHandler.display(userPointX, userPointY);
		
		playhead.display();
		playhead.update();
		
		
		
		for(int i=0; i < particleHandler.getNumParticles(); i++){
			tempData = particleHandler.getParticleData(i);
			if(playhead.particleCheck(tempData.getXpos()) && tempData.checked == false){
				tempData.setTint(255);
				tempData.checked = true;
			}else{
				tempData.setTint(50);
			}
		}	
		
	}
	
	public void keyPressed(){
		if(key == 'd' || key == 'D'){
			showDepthImage = !showDepthImage;
			println("Depth Image Enabled");
		}else{
			println("Depth Image Disabled");
		}
	}
}
