package com.shortcircuit.remotemouse;

import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.event.InputEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

/**
 * @author ShortCircuit908
 * 
 */
public class HostDataConnection implements Runnable{
	private final int port = 3306;
	private ServerSocket server_socket;
	private Socket client_socket;
	private Scanner scanner;
	private OutputStream output;
	private Robot robot;
	private final double size_y = Toolkit.getDefaultToolkit().getScreenSize().getHeight();
	private final double size_x = Toolkit.getDefaultToolkit().getScreenSize().getWidth();
	private double scale_x = size_x / 450.0;
	private double scale_y = size_y / 300.0;
	public HostDataConnection() {
	}
	public void run() {
		connect();
		listen();
	}
	public void connect() {
		try {
			server_socket = new ServerSocket(port);
			client_socket = server_socket.accept();
			scanner = new Scanner(client_socket.getInputStream());
			output = client_socket.getOutputStream();
			robot = new Robot();
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
	public void listen() {
		while(!Thread.interrupted()) {
			try {
				transmitImage(robot.createScreenCapture(new Rectangle(Toolkit.getDefaultToolkit().getScreenSize())));
			}
			catch (IOException e) {
				release();
				return;
			}
			if(scanner.hasNextLine()) {
				String line = scanner.nextLine();
				String command = line.split(":")[0];
				switch(command) {
				case "press":
					robot.mousePress(InputEvent.getMaskForButton(Integer.parseInt(line.split(":")[1])));
					break;
				case "release":
					robot.mouseRelease(InputEvent.getMaskForButton(Integer.parseInt(line.split(":")[1])));
					break;
				case "move":
					String[] coords = line.split(":")[1].split(",");
					robot.mouseMove((int)(Double.parseDouble(coords[0]) * scale_x),
							(int)(Double.parseDouble(coords[1]) * scale_y));
					break;
				case "resize":
					String[] dimensions = line.split(":")[1].split(",");
					calculateScale(Integer.parseInt(dimensions[0]), Integer.parseInt(dimensions[1]));
				}
			}
		}
		release();
	}
	public void calculateScale(double client_width, double client_height){
		scale_x = size_x / client_width;
		scale_y = size_y / client_height;
	}
	public void transmitImage(BufferedImage image) throws IOException{
		/*
		ImageIO.write(image, "JPG", output);
		output.flush();
		 */
	}
	public void release() {
		scanner.close();
		try {
			client_socket.close();
			server_socket.close();
			output.close();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}
}
