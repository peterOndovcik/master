
import java.awt.event.KeyEvent;
import net.useobjects.draw.drawable.CircleView;
import net.useobjects.draw.drawable.GroupView;
import net.useobjects.draw.drawable.TextView;
import net.useobjects.draw.samples.Aircraft;
import net.useobjects.frame.MainWindow;
import net.useobjects.geom.Position;
import net.useobjects.timer.SimpleTimer;

/////////ssssssssssssssssss
public class cv01Hra {
    static final double AIRCRAFT_DEFAULT_SPEED = 10;     //defaultna rychlost lietadla
        static final double AIRCRAFT_SPEED_CHANGE = 1;      //velkost zmeny rychlosti lietadla
	static final double AIRCRAFT_ROTATION_CHANGE = 0.1; //zmena smeru v radianoch
        static final double BULLET_SPEED = 90;              //rychlost strely
	static final double CRASH_DISTANCE = 25; //maximalna vzdalenost, kedy sa lietadla zrazia
	static final int TIME_STEP = 100;        //interval v milisekundach
       
        
    public static void main(String[] args) {
        MainWindow window = new MainWindow("bojova hra",1000,600);
        GroupView group = window.getRootGroup();
        Aircraft plane1 = newAircraft(group,400,400,0);
        Aircraft plane2 = newAircraft(group,200,200,0);
        plane2.colorToneFilter(1f,0 );
           plane1.colorToneFilter(10, 1 );
        CircleView bullet1 = new CircleView(null, 0, 0, 5);
	CircleView bullet2 = new CircleView(null, 0, 0, 5);
        double speed1 = AIRCRAFT_DEFAULT_SPEED;
        double speed2 = AIRCRAFT_DEFAULT_SPEED;
         boolean collision = false; // ak je zasiahnute alebo sa zrazia lietadla
        while(!collision)
        {
        speed1 = aircraftControl(plane1 , window, speed1, KeyEvent.VK_UP, KeyEvent.VK_DOWN, KeyEvent.VK_LEFT, KeyEvent.VK_RIGHT);
         plane1.moveForwards(speed1);
         speed2 = aircraftControl(plane2 , window, speed2, KeyEvent.VK_W, KeyEvent.VK_S, KeyEvent.VK_A, KeyEvent.VK_D);
         plane2.moveForwards(speed2);
        SimpleTimer.sleep(50);
            if( aircraftCollision(plane1, plane2)){
                Collision(plane1,plane2);
                collision = true;
            }
            //vystrelenie strely
			fireControl(plane1, window, bullet1, KeyEvent.VK_UP);
			fireControl(plane2, window, bullet2, KeyEvent.VK_CONTROL);
            
            //detekcia zasahu lietadiel nabojomi
			if( bulletCollision(bullet1, plane2) ) {
				plane2.setColorToneFilter(2f,4);
				collision = true;
			}
			if( bulletCollision(bullet2, plane1) ) {
				plane1.setColorToneFilter(2f,4);
				collision = true;
			}
                        if (group.contains(bullet1)) { //ak je strela zobrazovana
				bullet1.moveForwards(BULLET_SPEED);
			}
			if (group.contains(bullet2)) { //ak je strela zobrazovana 
			   bullet2.moveForwards(BULLET_SPEED);
			}
            keepInArea(plane1,window);
             keepInArea(plane2,window);
        }
        TextView textView = new TextView(group,"anicka je super",100,10);
            SimpleTimer.sleep(500000000);
         //  new TextView(group,"anicka je super",100,10);
           window.dispose();
    }
    
private static  Aircraft newAircraft(GroupView group,int x,int y, int sklon )
{
    Aircraft plane = new Aircraft(group,x,y,sklon); 
   
    plane.moveForwards(AIRCRAFT_DEFAULT_SPEED);
    
 return plane;   
}

private static double aircraftControl(Aircraft aircraft, MainWindow window, double speed, int keySpeedUp, int keySlowDown, int keyLeft, int keyRight) {
                        
		//smer
		if (window.isKeyDown(keyRight)) {
			aircraft.setRotation(aircraft.getRotation() + AIRCRAFT_ROTATION_CHANGE);
		} else if (window.isKeyDown(keyLeft)) {
			aircraft.setRotation(aircraft.getRotation() - AIRCRAFT_ROTATION_CHANGE);
		}

		//rychlost
		if (window.isKeyDown(keySpeedUp)) {
			speed += AIRCRAFT_SPEED_CHANGE;
                        
		} else if (window.isKeyDown(keySlowDown)) {
			speed -= AIRCRAFT_SPEED_CHANGE;
                      
		} else {
			//postupne vratenie na povodnu hodnotu
			speed += AIRCRAFT_SPEED_CHANGE * Math.signum(AIRCRAFT_DEFAULT_SPEED - speed);
		}

		return speed;
	}
private static boolean isDistanceShort(Position position1, Position position2, double threshold) {
		double distance = Position.distance(position1, position2);
        return distance <= CRASH_DISTANCE;
	}


private static boolean aircraftCollision(Aircraft aircraft1, Aircraft aircraft2) {
		return isDistanceShort(aircraft1.getPosition(), aircraft2.getPosition(), CRASH_DISTANCE);
	}

private static void Collision(Aircraft plane1, Aircraft plane2)
{         
				plane1.setColorToneFilter(0,1);
				plane2.setColorToneFilter(0,1);
				
			}
private static void fireControl(Aircraft aircraft, MainWindow window, CircleView bullet, int keyShot) {
		GroupView group = window.getRootGroup();

		//vystrelenie strely
		if (window.isKeyDown(keyShot) && (!group.contains(bullet))) {
			bullet.setPosition(aircraft.getPosition());
			bullet.setRotation(aircraft.getRotation());
			group.add(bullet);
                        
		}
		//zrusenie strely
		if (group.contains(bullet) && (!isPositionInWindow(bullet.getPosition(), window))) {
			group.remove(bullet);
		}
	}
private static boolean bulletCollision(CircleView bullet, Aircraft aircraft) {
		return isDistanceShort(bullet.getPosition(), aircraft.getPosition(), CRASH_DISTANCE);
}

private static boolean isPositionInWindow(Position bPosition,MainWindow  window){

        return !(bPosition.getRoundedX()<=0 || bPosition.getRoundedX() > window.getInternalWidth() ||
                bPosition.getRoundedY()<=0 || bPosition.getRoundedY() >= window.getInternalHeight());
}

private static void keepInArea(Aircraft aircraft, MainWindow window) {
		double positionX = aircraft.getPositionX();
		double positionY = aircraft.getPositionY();
		int width = window.getInternalWidth();
		int height = window.getInternalHeight();

		if (positionX <= 0) {
			aircraft.setPositionX(width);
		}
		if (positionX >= width) {
			aircraft.setPositionX(0);
		}
		if (positionY <= 0) {
			aircraft.setPositionY(height);
		}
		if (positionY >= height) {
			aircraft.setPositionY(0);
		}
	}
}




