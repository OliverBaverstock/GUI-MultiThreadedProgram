package assign2package;


import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

import javax.swing.JScrollPane;

@SuppressWarnings("serial")
public class NewClient extends JFrame {
	
	///////////// Variables for Creating Screen ////////////////

	private JPanel contentPane;
	private JTextField usernameField;
	private JPasswordField passwordField;
	public JTextField jtf;
	private JPanel panel;
	private JButton loginButton;
	private JButton resetButton;
	private JTextArea textArea;
	private JButton sendButton;
	private String hostname;
	private InetAddress ipAddress;
	
	//////////// Variables to receive and send data /////////////////

	private DataOutputStream toServer;
	private DataInputStream fromServer;
	private Socket socket;
	
	/////////// Variables to store client info //////////////////////

	private String fname;
	private String lname;
	
	/////////// Main function to start client ////////////

	public static void main(String[] args) {
		new NewClient();
	}

	
	/////////// Creating the Frame //////////////
	
	public NewClient() {

		setTitle("Client Login");
		setBounds(100, 100, 460, 320);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);

		panel = new JPanel();
		panel.setBounds(0, 0, 460, 298);
		contentPane.add(panel);
		panel.setLayout(null);
		
		JLabel lblNewLabel = new JLabel("Log In");
		lblNewLabel.setFont(new Font("Lucida Grande", Font.PLAIN, 20));
		lblNewLabel.setHorizontalAlignment(SwingConstants.CENTER);
		lblNewLabel.setBounds(0, 6, 460, 30);
		panel.add(lblNewLabel);

		usernameField = new JTextField();
		usernameField.setBounds(241, 43, 167, 26);
		panel.add(usernameField);
		usernameField.setColumns(10);

		passwordField = new JPasswordField();
		passwordField.setBounds(241, 73, 167, 26);
		panel.add(passwordField);

		JLabel usernameLabel = new JLabel("User Name");
		usernameLabel.setBounds(107, 48, 94, 16);
		panel.add(usernameLabel);

		JLabel passwordLabel = new JLabel("Password");
		passwordLabel.setBounds(107, 78, 94, 16);
		panel.add(passwordLabel);

		loginButton = new JButton("Log In");
		loginButton.addActionListener(new Listener());

		loginButton.setBounds(38, 106, 117, 29);
		panel.add(loginButton);

		resetButton = new JButton("Reset");
		
		///////// Action listener to reset fields when reset button clicked //////////////
		
		resetButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				usernameField.setText("");
				passwordField.setText("");
			}
		});
		resetButton.setBounds(167, 106, 117, 29);
		panel.add(resetButton);

		JButton exitButton = new JButton("Exit");

		exitButton.setBounds(296, 106, 117, 29);
		
		/////////// Action listener to exit when exit button is clicked ////////////
		
		exitButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int confirm = JOptionPane.showOptionDialog(contentPane, "Are You Sure to Close this Application?",
						"Exit Confirmation", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, null, null);
				if (confirm == 0) {
					try {
						toServer.writeUTF("Exit");
						toServer.flush();
						toServer.writeUTF(fname);
						toServer.flush();
						toServer.writeUTF(lname);
						toServer.flush();
						//Close socket and data streams
						fromServer.close();
						toServer.close();
						socket.close();
					} catch (IOException e1) {
						e1.printStackTrace();
					}
					System.exit(0);
				}
			}
		});
		panel.add(exitButton);

		JLabel radiusLabel = new JLabel("Enter Radius:");
		radiusLabel.setBounds(16, 140, 82, 16);
		panel.add(radiusLabel);

		jtf = new JTextField();
		jtf.setBounds(100, 135, 182, 26);
		panel.add(jtf);
		jtf.setColumns(10);
		//Field is disabled until the user successfully logs in
		jtf.setEnabled(false);

		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(16, 164, 438, 128);
		panel.add(scrollPane);

		textArea = new JTextArea();
		//Cannot edit the text area
		textArea.setEditable(false);
		scrollPane.setViewportView(textArea);

		sendButton = new JButton("Send");
		sendButton.setBounds(296, 135, 117, 29);
		sendButton.setEnabled(false);
		sendButton.addActionListener(new ActionListener() {
			
			///////// Action listener to send radius, receive area and display results ////////////
			
			public void actionPerformed(ActionEvent e) {
				try {
					// Sets the client IP address and host name
					ipAddress = InetAddress.getLocalHost();
					hostname = ipAddress.getHostName();
					String ip = ipAddress.getHostAddress();
					// Get the radius from the text field
					double radius = 0;
					if (!jtf.getText().isEmpty()) {
						radius = Double.parseDouble(jtf.getText().trim());

						// Initiate the radius function on the server
						toServer.writeUTF("Radius");
						toServer.flush();

						// Sender host name and IP to server
						toServer.writeUTF(hostname);
						toServer.flush();
						toServer.writeUTF(ip);
						toServer.flush();

						// Send the radius to the server
						toServer.writeDouble(radius);
						toServer.flush();

						// Reading Server Host name and IP
						String serverHostname = fromServer.readUTF();
						String serverIP = fromServer.readUTF();

						// Get area from the server
						double area = fromServer.readDouble();

						// Displays radius sent, a response message with server host name and IP
						// and displays the area found sent by the server
						textArea.append("Radius is " + radius + "\n");
						textArea.append("Response received from Server: " + serverHostname + " at IPAddress: "
								+ serverIP + "\n");
						textArea.append("Area found: " + area + '\n');
					} else {
						JOptionPane.showMessageDialog(null, "Please enter a valid radius\n");
					}
				} catch (Exception e1) {
					System.out.print(e1.getMessage());
				}
			}
		});
		panel.add(sendButton);

		
		//Displays everything
		setVisible(true);

		//////////// Creating the Socket and Data Streams /////////////
		
		try {
			// Create a socket to connect to the server
			socket = new Socket("localhost", 8000);

			// Create an input stream to receive data from the server
			fromServer = new DataInputStream(socket.getInputStream());

			// Create an output stream to send data to the server
			toServer = new DataOutputStream(socket.getOutputStream());

		} catch (IOException ex) {
			System.out.print(ex.toString() + '\n');
		}

	}
	
	////////////// Action Listener Send Login Data and Display Response //////////////

	private class Listener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			try {
				// Gets the text from the username and password field
				String username = usernameField.getText();
				char[] password = passwordField.getPassword();
				// Creates a string for password to be used in query
				String pass = new String(password);

				// Initiate the Login function on the server
				toServer.writeUTF("Login");
				toServer.flush();

				// Send username and password to server
				toServer.writeUTF(username);
				toServer.flush();
				toServer.writeUTF(pass);
				toServer.flush();
				String resp = fromServer.readUTF();
				if (resp.equals("Success")) {

					usernameField.setText("");
					passwordField.setText("");

					loginButton.setEnabled(false);
					resetButton.setEnabled(false);
					usernameField.setEnabled(false);
					passwordField.setEnabled(false);

					jtf.setEnabled(true);
					textArea.setEditable(true);
					sendButton.setEnabled(true);

					// Welcome
					// Reads in the users first and last name and number of logins(TOT_REQ)
					fname = fromServer.readUTF();
					lname = fromServer.readUTF();
					int tot_req = fromServer.readInt();
					int clientID = fromServer.readInt();
					// Writes a welcome message to the text area with the users name and shows the
					// number of logins
					// Also asked the user to enter the area of a circle
					textArea.append("Welcome " + fname + " " + lname
							+ ", you have connected to the server succesfully!\nNumber of Logins: " + tot_req
							+ "\nPlease enter the radius of the circle\n");
					// Displays the logged in users ID in the title
					setTitle("Client - " + clientID);
					//If unsuccessful login display error
				} else if (resp.equals("UnSuccessful")) {
					JOptionPane.showMessageDialog(null,
							"Record Not Found \nPlease enter a valid Username and Password");
				}
			} catch (IOException ex) {
				System.err.println(ex);
			}
		}
	}
}
