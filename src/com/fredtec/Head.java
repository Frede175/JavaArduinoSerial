package com.fredtec;

import com.fredtec.arduino.Servo;
import com.fredtec.arduino.Stepper;
import org.apache.batik.dom.svg.SVGOMPoint;
import org.w3c.dom.svg.SVGPoint;

import java.util.ArrayList;

/**
 * Created by fsr19 on 2/4/2017.
 */
public class Head implements Runnable {
	int x = 0, y = 0;
	Stepper stepperX, stepperY;
	Servo pen;
	
	int penUp = 46, penDown = 40;
	
	
	boolean suspended = false;
	boolean isRunning = false;

	ArrayList<ArrayList<SVGPoint>> pointList;
	
	ArrayList<SVGPoint> pointsDrawn = new ArrayList<>();
	
	
	public Head(Stepper stepX, Stepper stepY, int penPort) {
		stepperX = stepX;
		stepperY = stepY;
		pen = new Servo(0, penPort);
	}
	
	public void connect() {
		stepperX.connect();
		stepperY.connect();
		pen.attach();
		penUp();
	}

	public void SetPoints(ArrayList<ArrayList<SVGPoint>> pointList) {
		this.pointList = pointList;
	}
	
	synchronized void suspend() {
		suspended = true;
	}
	
	synchronized void startDrawing() {
		pointsDrawn.clear();
		isRunning = true;
		resume();
	}
	
	synchronized void stopDrawing() {
		isRunning = false;
		
	}
	
	synchronized void resume() {
		suspended = false;
		notify();
	}
	
	private void draw() {
		penUp();
		for (ArrayList<SVGPoint> points : pointList) {
			if (waitForSuspend()) return;
			penUp();
			if (points.size() == 0) continue;
			System.out.println("Going to point: " + (int)points.get(0).getX() + " " + (int)points.get(0).getY());
			gotoPoint(points.get(0));
			if (waitForSuspend()) return;
			penDown();
			for (int i = 1; i < points.size(); i++) {
				if (waitForSuspend()) return;
				gotoPoint(points.get(i));
				pointsDrawn.add(points.get(i));
			}
		}
		penUp();
		//Going to start pos!
		gotoPoint(new SVGOMPoint(0,0));
		isRunning = false;
	}
	
	private void gotoPoint(SVGPoint point) {
		int xDiff = (int)point.getX() - x; 
		int yDiff = (int)point.getY() - y;
		
		if (xDiff == yDiff) return;
		
		int yDir = 0;
		if (yDiff > 0) yDir = 1;
		else if (yDiff < 0) yDir = -1;
		int xDir = 0;
		if (xDiff > 0) xDir = 1;
		else if (xDiff < 0) xDir = -1;
		//Straight line
		if (Math.abs(xDiff) == Math.abs(yDiff) || xDiff == 0 || yDiff == 0) {
			int toMove = 0;
			if (xDiff == 0) toMove = Math.abs(yDiff);
			else toMove = Math.abs(xDiff);
			
			while (toMove > 0) {
				if (waitForSuspend()) return;
				stepperY.step(yDir);
				y += yDir;
				stepperX.step(xDir);
				x += xDir;
				toMove--;
			}
		} else { //None straight line
			float ratio = (float)Math.abs(xDiff)/(float)Math.abs(yDiff);
			if (ratio < 1) {
				float fx = ratio;
				while (y != (int)point.getY()) {
					while (Math.round(fx) >= 1) {
						stepperX.step(xDir);
						x += xDir;
						fx--;
						if (waitForSuspend()) return;
					}
					stepperY.step(yDir);
					y += yDir;
					fx += ratio;
					if (waitForSuspend()) return;
				}
				while (x != (int)point.getX()) {
					int dir = (int)point.getX() - x;
					stepperX.step(dir);
					x += dir;
					if (waitForSuspend()) return;
				}
			} else {
				ratio = (float)Math.abs(yDiff)/(float)Math.abs(xDiff);
				float fy = ratio;
				while (x != (int)point.getX()) {
					while (Math.round(fy) >= 1) {
						stepperY.step(yDir);
						y += yDir;
						fy--;
						if (waitForSuspend()) return;
					}
					stepperX.step(xDir);
					x += xDir;
					fy += ratio;
					if (waitForSuspend()) return;
				}
				while (y != (int)point.getY()) {
					int dir = (int)point.getY() - y;
					stepperY.step(dir);
					y += dir;
					if (waitForSuspend()) return;
				}
			}
		}
	}

	public void penUp() {
		pen.write(penUp);
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public void penDown() {
		pen.write(penDown);
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void run() {
		try {
			while (true) {
				waitForSuspend();
				if (isRunning) draw();
				Thread.sleep(50);
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
	}
	
	private boolean waitForSuspend() {
		
		System.out.println(Main.arduino.analogRead(0));
		
		
		synchronized (this) {
			while (suspended) try {
				wait();
			} catch (InterruptedException e) {
				System.err.println("Interrupted!");
			}
			if (!isRunning) return true;
		}
		return false;
	}

	public void setSpeed(int speed) {
		stepperX.setSpeed(speed);
		stepperY.setSpeed(speed);
	}
}
