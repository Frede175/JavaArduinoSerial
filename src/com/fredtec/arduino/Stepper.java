package com.fredtec.arduino;

import com.fredtec.Main;

/**
 * Created by fsr19 on 2/3/2017.
 */
public class Stepper {
	
	private int pin1, pin2, pin3, pin4;
	private int numberOfSteps;

	private int dir = 0;
	private int step_number = 0;
	private long lastStepTime = 0L;

	private int delay = 0;
	
	private boolean isConnected = false;
	
	public Stepper(int pin1, int pin2, int pin3, int pin4, int numberOfSteps) {
		this.pin1 = pin1; this.pin2 = pin2; this.pin3 = pin3; this.pin4 = pin4;
		this.numberOfSteps = numberOfSteps;
		delay = 60 * 1000 / numberOfSteps;
	}
	
	public void setSpeed(int speed) {
		delay = 60 * 1000 / numberOfSteps / speed;
	}
	
	public void connect() {
		if (Main.arduino.isConnected() && !isConnected) {
			Main.arduino.pinMode(pin1, Arduino.OUTPUT);
			Main.arduino.pinMode(pin2, Arduino.OUTPUT);
			Main.arduino.pinMode(pin3, Arduino.OUTPUT);
			Main.arduino.pinMode(pin4, Arduino.OUTPUT);
			isConnected = true;
		}
	}
	
	
	public void step(int steps) {
		if (!isConnected) return;
		int stepsLeft = Math.abs(steps);
		if (steps < 0) {
			dir = 0;
		} else if (steps > 0) {
			dir = 1;
		}
		while (stepsLeft > 0) {
			long now = System.currentTimeMillis();
			if (now - lastStepTime >= delay) {
				lastStepTime = now;
				if (dir == 1) {
					step_number++;
					if (step_number == numberOfSteps) step_number = 0;
				}
				if (dir == 0) {
					if (step_number == 0) step_number = numberOfSteps;
					step_number--;
				}
				stepsLeft--;
				stepMotor(step_number % 4);
			}
		}
	}
	
	private void stepMotor(int step) {
		switch (step) {
			case 0:
				Main.arduino.digitalWrite(pin1,1);
				Main.arduino.digitalWrite(pin2,0);
				Main.arduino.digitalWrite(pin3,1);
				Main.arduino.digitalWrite(pin4,0);
				break;
			case 1:
				Main.arduino.digitalWrite(pin1,0);
				Main.arduino.digitalWrite(pin2,1);
				Main.arduino.digitalWrite(pin3,1);
				Main.arduino.digitalWrite(pin4,0);
				break;
			case 2:
				Main.arduino.digitalWrite(pin1,0);
				Main.arduino.digitalWrite(pin2,1);
				Main.arduino.digitalWrite(pin3,0);
				Main.arduino.digitalWrite(pin4,1);
				break;
			case 3:
				Main.arduino.digitalWrite(pin1,1);
				Main.arduino.digitalWrite(pin2,0);
				Main.arduino.digitalWrite(pin3,0);
				Main.arduino.digitalWrite(pin4,1);
				break;
		}
	}
	
	
}
