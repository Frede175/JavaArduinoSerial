package com.fredtec;

import org.w3c.dom.svg.SVGPoint;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

/**
 * Created by fsr19 on 2/3/2017.
 */
public class Screen extends JPanel {
	
	int width, height;
	
	ArrayList<ArrayList<SVGPoint>> pointList;
	
	ArrayList<SVGPoint> pointsDrawn = new ArrayList<>();
	int headX = 0, headY = 0;
	
	public Screen(int width, int height) {
		this.width = width;
		this.height = height;
		setSize(new Dimension(width, height));
		setVisible(true);
	}

	
	public void paint(Graphics g) {
		super.paint(g);
		g.setColor(Color.WHITE);
		g.fillRect(0,0, width,height);
		g.setColor(Color.BLACK);
		
		if (pointList != null) {
			for (ArrayList<SVGPoint> points : pointList){
				for (SVGPoint point : points) {
					g.drawRect((int)point.getX(), (int)point.getY(), 1,1);
				}
			}
		}


		ArrayList<SVGPoint> pointsDrawn = (ArrayList<SVGPoint>) this.pointsDrawn.clone();
		
		
		g.setColor(Color.green);
		for (SVGPoint point : pointsDrawn) {
			g.drawRect((int)point.getX(), (int)point.getY(), 1,1);
		}
		
		g.setColor(Color.blue);
		g.drawLine(headX, 0, headX, height);
		g.drawLine(0, headY, width, headY);
		
	}
	
	public void setPointList(ArrayList<ArrayList<SVGPoint>> pointList) {
		this.pointList = pointList;
		repaint();
	}
}
