package com.fredtec.arduino;

import gnu.io.*;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.TooManyListenersException;

/**
 * Created by fsr19 on 2/3/2017.
 */
public class Arduino implements SerialPortEventListener {
	
	public static int OUTPUT = 1;
	public static int INPUT = 0;
	
	
	private static String appName = "Arduino";
	private static int DATA_RATE = 115200;
	private static final String PORT_NAMES[] = {
			"/dev/tty.usbmodem", // Mac OS X
			"/dev/usbdev", // Linux
			"/dev/tty", // Linux
			"/dev/serial", // Linux
			"COM3", "COM4"// Windows
	};
	private CommPortIdentifier portId;
	private SerialPort serial;
	private OutputStream out;

	public boolean isConnected() {
		return isConnected;
	}

	private boolean isConnected = false;
	
	public void disconnect() {
		serial.removeEventListener();
		serial.close();
		isConnected = false;
		System.out.println("INFO: Arduino Disconnected");
	}
	
	public void connect() throws PortInUseException, UnsupportedCommOperationException, TooManyListenersException, IOException {
		Enumeration portEnum = CommPortIdentifier.getPortIdentifiers();
		while (portId == null && portEnum.hasMoreElements()) {
			CommPortIdentifier currPortId = (CommPortIdentifier) portEnum.nextElement();
			for (String portName : PORT_NAMES) {
				if ( currPortId.getName().equals(portName) || currPortId.getName().startsWith(portName))
				{
					// Try to connect to the Arduino on this port
					serial = (SerialPort)currPortId.open(appName, 1000);
					portId = currPortId;
					break;
				}
			}
		}
		if (portId != null) {
			serial.setSerialPortParams(DATA_RATE, SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);
			serial.addEventListener(this);
			serial.notifyOnDataAvailable(true);
			out = serial.getOutputStream();
			isConnected = true;
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				System.err.println("Failed to sleep!");
			}
			sendData("Is open! \n");
		}
	}
	
	
	public void digitalWrite(int pin, int value) {
		if (isConnected) {
			String data = "W:D:" + pin + ":" + value + "\n";
			sendData(data);
		}
	}
	
	public void pinMode(int pin, int mode) {
		if (isConnected) {
			String data = "S:P:" + pin + ":" +  mode + "\n";
			sendData(data);
		}
	}
	
	public void attachServo(int index, int pin) {
		if (isConnected) {
			String data = "S:S:" + index + ":" +  pin + "\n";
			sendData(data);
		}
		
	}
	
	public void servoWrite(int index, int pos) {
		if (isConnected) {
			String data = "W:S:" + index + ":" +  pos + "\n";
			sendData(data);
		}
	}
	
	private void sendData(String data) {
		try {
			out.write(data.getBytes("ascii"));
		} catch (IOException e) {
			System.err.println("Error in write");
		}
	}
	

	@Override
	public void serialEvent(SerialPortEvent serialPortEvent) {
		
	}
}
