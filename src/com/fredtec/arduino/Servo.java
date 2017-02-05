package com.fredtec.arduino;

import com.fredtec.Main;

/**
 * Created by fsr19 on 2/4/2017.
 */
public class Servo {
	private int index;
	private int pin;
	private int pos = 0;
	
	public Servo(int index, int pin) {
		this.index = index;
		this.pin = pin;
	}
	
	public void attach() {
		if (Main.arduino.isConnected()) {
			Main.arduino.attachServo(index, pin);
		}
	}
	
	public void write(int pos) {
		if (Main.arduino.isConnected()) {
			this.pos = pos;
			Main.arduino.servoWrite(index, pos);
		}
	}
	
}
