package gpsproject;

import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.awt.*;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;

/**
 * Prepares the GUI to test the GPS code
 * @author Belig Borjiged
 *
 */
class Gpsgui extends JFrame {

	private static final long serialVersionUID = 1L;
	private JMenuBar bar = new JMenuBar();
	private JMenu fileMenu = new JMenu("File");
	private JTextArea messagesReceived = new JTextArea();
	private JButton startButton = new JButton("Start");
	private JButton endButton = new JButton("End");
	private static Socket s;
	private static OutputStream out; 

	public static void main(String[] args) throws UnknownHostException, IOException {
		new Gpsgui().createGui();
	}

	/**
	 * Creates the GUI for the GPS application
	 */
	void createGui() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		JPanel giantPanel = new JPanel();
		giantPanel.setLayout(new BorderLayout());
		// Create the menu bar
		bar.add(fileMenu);
		this.setJMenuBar(bar);

		JMenuItem saveAs = new JMenuItem("Save As");
		JMenuItem quit = new JMenuItem("Quit");
		fileMenu.add(saveAs);
		fileMenu.add(quit);

		// Add textArea
		JPanel secondPanel = new JPanel();
		secondPanel.setLayout(new BorderLayout());
		JScrollPane topScroll = new JScrollPane(messagesReceived, 
				JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		secondPanel.add(topScroll);
		messagesReceived.setEditable(false);
		messagesReceived.setLineWrap(true);		
		Border simple = BorderFactory.createLineBorder(Color.black);
		TitledBorder readings = BorderFactory.createTitledBorder(simple, 
				"GPS Readings", TitledBorder.LEFT, TitledBorder.TOP);
		messagesReceived.setBorder(readings);
		giantPanel.add(secondPanel, BorderLayout.CENTER);

		// Add start and end buttons
		JPanel buttonPanel = new JPanel();
		buttonPanel.add(startButton, new FlowLayout());
		buttonPanel.add(endButton, new FlowLayout());
		buttonPanel.setBackground(Color.white);
		giantPanel.add(buttonPanel, BorderLayout.SOUTH);

		// Add a listener for the buttons
		this.add(giantPanel);

		setVisible(true);
		this.pack();
		this.setSize(400, 300);
		this.setTitle("GPS Tracker");

		saveAs.addActionListener(new ActionListener()  {            
			public void actionPerformed(ActionEvent arg0) {                	
				JFrame saveFrame = new JFrame();
				JFileChooser chooser = new JFileChooser();
				chooser.setDialogTitle("Save file as?");
				int result = chooser.showSaveDialog(saveFrame);
				if (result == JFileChooser.APPROVE_OPTION) {
					try {
						File saveFile = chooser.getSelectedFile();
						String saveName = saveFile.getCanonicalPath();
						FileOutputStream stream = new FileOutputStream(saveName);
						PrintWriter writer = new PrintWriter(stream, true);
						writer.write(messagesReceived.getText());  	
						writer.close();
						stream.close();
					} catch (IOException e) {
						System.out.println("GPS readings could not be saved.");
					}
				}
			}
		});

		quit.addActionListener(new ActionListener()  {            
			public void actionPerformed(ActionEvent arg0) {                	
				dispose();
			}
		});

		startButton.addActionListener(new ActionListener()  {            
			public void actionPerformed(ActionEvent arg0) {   
		        try {
		    		s = new Socket("localhost", 12345);
		    		PrintWriter out = new PrintWriter(s.getOutputStream(), true);
		    		InputStream in = s.getInputStream(); 
		    		out.println("4,0.");
		    		
		    		while (in.available() == 0);
		    		
//		    		while(true) {
		    			byte[] buf = new byte[in.available()];
		    			
		    			in.read(buf);
		    			Gpsproject gps = new Gpsproject();
		    			String line = new String(buf);
		    			System.out.println(line);
		    			if(!line.isEmpty()) {
		    				System.out.println("not empty");
		    				String[] str = gps.parse(new String(buf));
		    				if(str == null) {
		    					messagesReceived.setText("GPS is stopped.");
		    				} else {
		    					for(int i = 0; i < str.length; i++) {
		    						messagesReceived.append(str[i]+ "\n");
		    					}
		    					messagesReceived.append("\n");
		    				}
		    			}
		    			
						s.close();
				} catch (IOException e) {
					messagesReceived.setText("An error has occurred.  No message was received.");
				}
				
			}
		});

		endButton.addActionListener(new ActionListener()  {            
			public void actionPerformed(ActionEvent arg0) {                	
				try {
		    		s = new Socket("localhost", 12345);
		    		PrintWriter out = new PrintWriter(s.getOutputStream(), true);
		    		InputStream in = s.getInputStream(); 
		    		out.println("0,0.");
		    		while (in.available() == 0);
		    		
//		    		while(true) {
		    			byte[] buf = new byte[in.available()];
		    			
		    			in.read(buf);
		    			
		    			
						s.close();
				} catch (IOException e) {
	                // TODO Auto-generated catch block
					messagesReceived.setText("An error has occurred.  Socket was not closed.");
                }
			}
		});
	}
}
