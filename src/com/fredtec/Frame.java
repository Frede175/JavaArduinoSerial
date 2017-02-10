package com.fredtec;

import gnu.io.PortInUseException;
import gnu.io.UnsupportedCommOperationException;
import javafx.scene.Scene;
import javafx.stage.FileChooser;
import oracle.jrockit.jfr.JFR;
import org.w3c.dom.svg.SVGPoint;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.TooManyListenersException;

/**
 * Created by fsr19 on 2/3/2017.
 */
public class Frame extends JFrame implements Runnable{
	
	int width, height;
	Screen screen;
	
	JButton findSVG;
	JButton connect;
	JButton start;
	JButton resume;
	JButton stop;
	JButton pause;
	
	JButton stepXPos;
	JButton stepXNeg;
	JButton stepYPos;
	JButton stepYNeg;
	
	JButton up;
	JButton down;
	
	JLabel connectStatus;
	JLabel headX, headY;
	
	
	public Frame(int width, int height) {
		this.width = width;
		this.height = height;
		configure();
	}

	private void configure() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLayout(null);
		setSize(new Dimension(width, height));
		setResizable(false);
		setName("Arduino");
		screen = new Screen(800, height);
		add(screen);
		
		//Buttons:
		findSVG = new JButton("Load svg");
		findSVG.setBounds(820, 20, 150, 25);
		findSVG.addActionListener(new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JFileChooser fileChooser = new JFileChooser();
				fileChooser.addChoosableFileFilter(new FileNameExtensionFilter("SVG files (*.svg)", "svg"));
				fileChooser.setAcceptAllFileFilterUsed(false);
				if (fileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
					File svgFile = fileChooser.getSelectedFile();
					if (svgFile != null) {
						try {
							SVGReader svg = new SVGReader(svgFile.toURI().toString());
							ArrayList<ArrayList<SVGPoint>> points = svg.getPoints();
							screen.setPointList(points);
							Main.head.SetPoints(points);
						} catch (IOException e1) {
							System.err.println("Error!");
						}
					}
				}
			}
		});
		findSVG.setVisible(true);
		add(findSVG);

		connect = new JButton("Connect to arduino");
		connect.setBounds(820, 55, 150, 25);
		connect.addActionListener(new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (Main.arduino.isConnected()) return;
				try {
					Main.arduino.connect();
				} catch (PortInUseException | UnsupportedCommOperationException | TooManyListenersException | IOException e1) {
					System.err.println("Error in connection! " + e1.toString());
				}
				if (!Main.arduino.isConnected()) {
					System.err.println("Failed to connect to Arduino!");
					connectStatus.setText("Error while connection!");
				}
				else {
					connectStatus.setText("Connected!");
					Main.head.connect();
				}
			}
		});
		connect.setVisible(true);
		add(connect);

		start = new JButton("Start");
		start.setBounds(820, 90, 150, 25);
		start.addActionListener(new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Main.head.startDrawing();
			}
		});
		start.setVisible(true);
		add(start);

		stop = new JButton("Stop");
		stop.setBounds(820, 125, 150, 25);
		stop.addActionListener(new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Main.head.stopDrawing();
			}
		});
		stop.setVisible(true);
		add(stop);
		
		
		pause = new JButton("Pause");
		pause.setBounds(820, 160, 150, 25);
		pause.addActionListener(new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Main.head.suspend();
			}
		});
		pause.setVisible(true);
		add(pause);

		resume = new JButton("Resume");
		resume.setBounds(820, 195, 150, 25);
		resume.addActionListener(new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Main.head.resume();
			}
		});
		resume.setVisible(true);
		add(resume);
		
		connectStatus = new JLabel("Disconnected");
		connectStatus.setBounds(990, 55, 150, 25);
		connectStatus.setVisible(true);
		add(connectStatus);

		headX = new JLabel("x: 0");
		headX.setBounds(990, 90, 50, 25);
		headX.setVisible(true);
		add(headX);

		stepXNeg = new JButton("Step x: -10");
		stepXNeg.setBounds(820, 230, 150, 25);
		stepXNeg.addActionListener(new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Main.head.stepperX.step(-10);
				Main.head.x -= 10;
			}
		});
		stepXNeg.setVisible(true);
		add(stepXNeg);

		stepXPos = new JButton("Step x: 10");
		stepXPos.setBounds(990, 230, 150, 25);
		stepXPos.addActionListener(new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Main.head.stepperX.step(10);
				Main.head.x += 10;
			}
		});
		stepXPos.setVisible(true);
		add(stepXPos);

		stepYNeg = new JButton("Step y: -10");
		stepYNeg.setBounds(820, 265, 150, 25);
		stepYNeg.addActionListener(new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Main.head.stepperY.step(-10);
				Main.head.y -= 10;
			}
		});
		stepYNeg.setVisible(true);
		add(stepYNeg);

		stepYPos = new JButton("Step y: 10");
		stepYPos.setBounds(990, 265, 150, 25);
		stepYPos.addActionListener(new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Main.head.stepperY.step(10);
				Main.head.y += 10;
			}
		});
		stepYPos.setVisible(true);
		add(stepYPos);


		up = new JButton("Up");
		up.setBounds(820, 300, 150, 25);
		up.addActionListener(new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Main.head.penUp();
			}
		});
		up.setVisible(true);
		add(up);

		up = new JButton("Down");
		up.setBounds(990, 300, 150, 25);
		up.addActionListener(new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Main.head.penDown();
			}
		});
		up.setVisible(true);
		add(up);
		
		headY = new JLabel("y: 0");
		headY.setBounds(1060, 90, 150, 25);
		headY.setVisible(true);
		add(headY);
		
		setVisible(true);
		
	}

	@Override
	public void paintComponents(Graphics g) {
		super.paintComponents(g);
		
	}

	public void DrawPoints(ArrayList<ArrayList<SVGPoint>> pointList) {
		screen.setPointList(pointList);
	}

	@Override
	public void run() {
		long lastDraw = 0;
		while (true) {
			long now = System.currentTimeMillis();
			if (now - lastDraw >= 1000/30) {
				lastDraw = now;
				
				if (Main.head != null) {
					screen.pointsDrawn = Main.head.pointsDrawn;
					int x = Main.head.x;
					int y = Main.head.y;
					screen.headX = x;
					screen.headY = y;
					headX.setText("x: " + x);
					headY.setText("y: " + y);
				}
				
				
				repaint();
			}
		}
	}
}
