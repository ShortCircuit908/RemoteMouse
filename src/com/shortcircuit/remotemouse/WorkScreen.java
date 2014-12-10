package com.shortcircuit.remotemouse;

import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.MouseWheelEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.swing.JFrame;
import javax.swing.JTextPane;

/**
 * @author ShortCircuit908
 * 
 */
public class WorkScreen extends JFrame{
	private static final long serialVersionUID = 5754397350460950017L;
	private final boolean host;
	private ClientDataConnection client_connection;
	private HostDataConnection host_connection;
	private final String address;
	private final int port;
	private JTextPane log_text;
	private boolean connected = false;
	boolean fullscreen = false;
	private Thread work_thread;
	public WorkScreen(boolean host, String address, int port) {
		if(!host) {
			getContentPane().addMouseMotionListener(new MouseMotionAdapter() {
				@Override
				public void mouseMoved(MouseEvent e) {
					Point p = e.getPoint();
					client_connection.transmit("move:" + p.getX() + "," + p.getY());
				}
				@Override
				public void mouseDragged(MouseEvent e) {
					Point p = e.getPoint();
					client_connection.transmit("move:" + p.getX() + "," + p.getY());
				}
			});
		}
		this.host = host;
		this.port = port;
		this.address = address;
		initialize();
	}
	
	private void initialize() {
		getContentPane().setLayout(null);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		if(host) {
			setTitle("RemoteMouse host");
			setBounds(100, 100, 300, 0);
			setAutoRequestFocus(false);
			setResizable(false);
			host_connection = new HostDataConnection();
			work_thread = new Thread(host_connection);
			work_thread.start();
		}
		else {
			setTitle("RemoteMouse client");
			setBounds(100, 100, 450, 300);
			setAlwaysOnTop(true);
			setResizable(true);
			
			log_text = new JTextPane();
			getContentPane().add(log_text);
			log_text.setBounds(0, 0, 434, 262);
			log_text.setEditable(false);
			
			client_connection = new ClientDataConnection(this, address, port);
			work_thread = new Thread(client_connection);
			Thread t = new Thread(new Runnable() {
				private WorkScreen screen;
				public Runnable setScreen(WorkScreen screen) {
					this.screen = screen;
					return this;
				}
				public void run() {
					try {
						Thread.sleep(5);
					}
					catch(InterruptedException e) {
						e.printStackTrace();
					}
					screen.connect();
				}
			}.setScreen(this));
			t.start();
			work_thread.start();
			getContentPane().addMouseListener(new MouseAdapter() {
				@Override
				public void mousePressed(MouseEvent e) {
					client_connection.transmit("press:" + e.getButton());
				}
			});
			getContentPane().addMouseListener(new MouseAdapter() {
				@Override
				public void mouseReleased(MouseEvent e) {
					client_connection.transmit("release:" + e.getButton());
				}
			});
			getContentPane().addMouseListener(new MouseAdapter() {
				@Override
				public void mouseWheelMoved(MouseWheelEvent e) {
					client_connection.transmit("scroll:" + e.getPreciseWheelRotation());
				}
			});
			getContentPane().addComponentListener(new ComponentAdapter() {
				@Override
				public void componentResized(ComponentEvent e) {
					if(connected) {
						client_connection.transmit("resize:" + getContentPane().getWidth() + ","
								+ getContentPane().getHeight());
					}
				}
			});
		}
		setVisible(true);
	}
	public void connect() {
		log_text.setText("Connecting to " + address);
		for(int i = 1; i <= 10; i++) {
			try {
				client_connection.connect();
				connected = true;
				break;
			}
			catch(IOException e) {
				log_text.setText(log_text.getText() + "\n" + "Exception: " + e.getMessage()
						+ " (attempt " + i + "/10)");
				try {
					Thread.sleep(250);
				}
				catch(InterruptedException ex) {
					ex.printStackTrace();
				}
			}
		}
		if(!connected) {
			log_text.setText(log_text.getText() + "\nError connecting to host");
		}
		else {
			log_text.setText(log_text.getText() + "\n" + "Connected to " + address);
			log_text.setVisible(false);
			client_connection.transmit("resize:" + getWidth() + "," + getHeight());
		}
	}
	public void paintImage(BufferedImage image) {
		Graphics g = getContentPane().getGraphics();
		g.drawImage(image.getScaledInstance(getContentPane().getWidth(), getContentPane().getHeight(),
				BufferedImage.TYPE_4BYTE_ABGR), 0, 0, null);
	}
	@Override
	public void dispose() {
		if(work_thread != null) {
			work_thread.interrupt();
		}
	}
}
