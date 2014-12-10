package com.shortcircuit.remotemouse;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * @author ShortCircuit908
 * 
 */
public class ClientDataConnection implements Runnable{
	private final int port;
	private Socket host_socket;
	private PrintWriter writer;
	private InputStream input;
	private final String address;
	private WorkScreen screen;
	public ClientDataConnection(WorkScreen screen, String address, int port) {
		this.screen = screen;
		this.address = address;
		this.port = port;
	}
	public void run() {
		while(!Thread.interrupted()) {
			/*
			try {
				if(input != null && input.available() > 0) {
					Thread.sleep(1000);
					try {
						screen.paintImage(ImageIO.read(input));
					}
					catch(NullPointerException e) {
						
					}
				}
			}
			catch(Exception e) {
				e.printStackTrace();
			}
			*/
		}
		release();
	}
	public void connect() throws IOException{
		host_socket = new Socket(address, port);
		writer = new PrintWriter(host_socket.getOutputStream(), true);
		input = host_socket.getInputStream();
	}
	public void transmit(String message) {
		if(writer == null) {
			System.out.println("Not connected");
		}
		writer.println(message);
	}
	public void release() {
		try {
			writer.close();
			host_socket.close();
			input.close();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}
}
