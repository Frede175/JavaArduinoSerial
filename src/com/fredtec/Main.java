package com.fredtec;

import com.fredtec.arduino.Arduino;
import com.fredtec.arduino.Stepper;
import gnu.io.PortInUseException;
import gnu.io.UnsupportedCommOperationException;
import org.w3c.dom.svg.SVGPoint;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.TooManyListenersException;

public class Main {
	private Frame frame;
	
	public static Arduino arduino;
	public static Head head;
	
	
	private Main() {
		arduino = new Arduino();
		frame = new Frame(1200,800);
		new Thread(frame).start();
		setup();
		head = new Head(new Stepper(2,3,4,5,200), new Stepper(6,7,8,9,200), 10);
		head.setSpeed(10);
		new Thread(head).start();
		
		
		
		
	}
	
	private void setup() {
		frame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				if (arduino.isConnected()) {
					arduino.disconnect();
				}
			}
		});
	}
	
	
    public static void main(String[] args) throws IOException {
	// write your code here
		new Main();
    }
}
