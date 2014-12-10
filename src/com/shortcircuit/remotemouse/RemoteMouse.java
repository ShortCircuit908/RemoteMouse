package com.shortcircuit.remotemouse;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.InputVerifier;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;

import org.apache.commons.lang3.StringUtils;

/**
 * @author ShortCircuit908
 * 
 */
public class RemoteMouse extends JFrame{
	private static final long serialVersionUID = 4599830422926953493L;
	private JTextField host_ip;
	private JFormattedTextField host_port;
	public static void main(String[] args) {
		new RemoteMouse();
	}
	
	/**
	 * Create the application.
	 */
	public RemoteMouse() {
		initialize();
	}
	
	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		setResizable(false);
		setBounds(100, 100, 217, 152);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		getContentPane().setLayout(null);
		
		JLabel label_mode = new JLabel("Select mode");
		label_mode.setBounds(10, 11, 82, 14);
		getContentPane().add(label_mode);
		
		JButton button_client = new JButton("Client");
		button_client.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				new WorkScreen(false, host_ip.getText(), Integer.parseInt(host_port.getText()))
				.setLocation(getLocation());
				dispose();
			}
		});
		button_client.setBounds(112, 36, 89, 23);
		getContentPane().add(button_client);
		
		JButton button_host = new JButton("Host");
		button_host.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				new WorkScreen(true, host_ip.getText(), Integer.parseInt(host_port.getText()))
				.setLocation(getLocation());;
				dispose();
			}
		});
		button_host.setBounds(10, 36, 89, 23);
		getContentPane().add(button_host);
		
		host_ip = new JTextField();
		host_ip.setText("localhost");
		host_ip.setBounds(10, 93, 145, 20);
		getContentPane().add(host_ip);
		host_ip.setColumns(10);
		
		JLabel label_ip = new JLabel("Host IP");
		label_ip.setBounds(10, 70, 46, 14);
		getContentPane().add(label_ip);
		
		host_port = new JFormattedTextField();
		host_port.setText("3306");
		host_port.setBounds(165, 93, 36, 20);
		host_port.setInputVerifier(new InputVerifier() {
			@Override
			public boolean verify(JComponent input) {
				JFormattedTextField field = (JFormattedTextField)input;
				String text = field.getText();
				return StringUtils.isNumeric(text) && Integer.parseInt(text) <= 9999;
			}
		});
		getContentPane().add(host_port);
		
		JLabel label_port = new JLabel("Port");
		label_port.setBounds(165, 70, 30, 14);
		getContentPane().add(label_port);
		setVisible(true);
	}
}
