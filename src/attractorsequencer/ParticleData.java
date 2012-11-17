package attractorsequencer;

public class ParticleData {

	float xPos, yPos, curParticleSize, particleTint;
	
	boolean checked; 
	
	private float maxParticleSize;
	
	ParticleData(float px, float py, float pSize, float pTint){
		xPos = px;
		yPos = py;
		curParticleSize = pSize;
		particleTint = pTint;
		
		maxParticleSize = pSize;
		
		checked = false;
	}
	
	
	//the getters
	public float getXpos(){
		return xPos;
	}
	
	public float getYpos(){
		return yPos;
	}
	
	public float getSize(){
		return curParticleSize;
	}
	
	public float getTint(){
		return particleTint;
	}
	
	//the setters
	public void setXpos(float x){
		xPos = x;
	}
	
	public void setYpos(float y){
		yPos = y;
	}
	
	public void setSize(float s){
		curParticleSize = s;
	}
	
	public void setTint(float t){
		particleTint = t;
	}
	
	public void update(){
		if(particleTint > 50  && checked == true){
			particleTint--;
			curParticleSize--;
		}else if(particleTint <= 50){
			//particleTint = 50;
			checked = false;
			curParticleSize = maxParticleSize;
		}
	}
	
	
}
