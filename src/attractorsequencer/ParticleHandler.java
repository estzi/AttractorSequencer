package attractorsequencer;

import java.util.*;
import processing.core.PApplet;
import processing.core.PImage;
import toxi.geom.*;
import toxi.physics2d.*;
import toxi.physics2d.behaviors.*;
import hypermedia.net.*;

public class ParticleHandler {
	PApplet parent;
	
	UDP udp;
	int sendPort = 6100;
	String ip = "localhost";
	
	int NUM_PARTICLES = 50;
	
	VerletPhysics2D physics;
	AttractionBehavior bodyAttractor;
	
	
	//particle variables
	PImage particle;
	float particleSize = 20;
	float maxGrowthRate = 3;
	float growthRate = 0.2f;
	float particleTint = 30;
	ArrayList <ParticleData> pData = new ArrayList <ParticleData> ();
	
	//keep track of all tracked users so we can update the points
	ArrayList <Body> trackedBodies = new ArrayList <Body> ();
	
	ParticleHandler(PApplet pA){
		parent = pA;
		
		physics = new VerletPhysics2D();
		physics.setDrag(0.05f);
		physics.setWorldBounds(new Rect(0, 0, parent.width, parent.height));
		particle = parent.loadImage("texture.png");
		particleSize = 5;
		
		udp = new UDP(this, 6000);
		udp.log(false);
		udp.listen(true);
		
	}
	
	public void addParticle(){
		VerletParticle2D p = new VerletParticle2D(Vec2D.randomVector().scale(5).addSelf(parent.random(parent.width), parent.random(parent.height)));
		//register the points with the physical world.
		physics.addParticle(p);
		//add repulsion force  on each particle point
		physics.addBehavior(new AttractionBehavior(p, 20, -1.2f, 0.01f));
		//keep track of each particle's data
		pData.add(new ParticleData(p.x, p.y, particleSize, particleTint));
	}
	
	//this must run once per new user
	public void setAttractionPoint(float x, float y, int id){
		Body body =  new Body(x, y, id, parent);
		trackedBodies.add(body);
		AttractionBehavior bodyAttractor = new AttractionBehavior(body.getVec(), 250, 0.5f);
		
		physics.addBehavior(bodyAttractor);
	}
	
	public void updateAttractionPoint(float x, float y, int id){
		if(trackedBodies.size() > 0){
			//create holder vector
			Vec2D tb_points;
			//create holder body
			Body oldPos = trackedBodies.get(id-1);
			tb_points = oldPos.getVec();
			tb_points.set(x, y);
			trackedBodies.set(id-1, oldPos);
		}
	}
	
	public int getNumParticles(){
		return pData.size();
	}
	
	//return a particles xPos
	public ParticleData getParticleData(int index){
		ParticleData p  = pData.get(index);
		return p;
	}
	

	public void display(float ax, float ay){
		Vec2D curBodyPos = new Vec2D();
		curBodyPos.set(ax, ay);
		
		//add our declared maximum number of particles
		if(physics.particles.size() < NUM_PARTICLES){
			addParticle();
		}
		
		physics.update();
		
		int counter = 0;

		//for each point declared as a particle
		//create a visual representation of it
		for (VerletParticle2D p : physics.particles){
			parent.fill(255);
			if(Float.isNaN(curBodyPos.x) || Float.isNaN(curBodyPos.y)){
				//particleSize = 25;
				//particleTint = 25;	
			}else{
				float mapDist = p.distanceTo(curBodyPos);
				particleSize = PApplet.map(mapDist, 0, parent.width, 75, 10);
				particleTint = PApplet.map(mapDist, 0, parent.width, 255, 10);
				parent.tint(particleTint);
			}
			
			//update the data for each particle
			ParticleData tempData;
			tempData = pData.get(counter);
			tempData.setXpos(p.x);
			tempData.setYpos(p.y);
			//now calculate the size and tint
			//get tempData's size, 
			tempData.update();

			parent.tint(tempData.getTint());
			
			//send the data to the paired max patch
			String data = String.valueOf(tempData.getTint());
			String index = String.valueOf(counter);
			udp.send(index+" "+data, ip, sendPort);
			
			parent.image(particle, tempData.getXpos(), tempData.getYpos(), tempData.getSize(), tempData.getSize());
			counter++;
		}
	}
	
}
