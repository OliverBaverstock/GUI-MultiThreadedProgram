package assign2package;



import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.border.EmptyBorder;

@SuppressWarnings("serial")
public class Server extends JFrame {
	
	///////// Variables used for server Frame ////////////

	private JPanel contentPane;
	public JTextArea jta;
	
	///////// Variables for data streams ////////////

	@SuppressWarnings("unused")
	private DataOutputStream toServer;
	@SuppressWarnings("unused")
	private DataInputStream fromServer;
	
	//////// Creating DBQueries object ////////////

	DBQueries dbqueries = new DBQueries();

	// Main function to start server
	public static void main(String[] args) {
		new Server();
	}

	// Creating variables for host name and IP
	private String hostname;
	private InetAddress ipAddress;

	public Server() {

		setTitle("Server");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 460, 320);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);

		JPanel panel = new JPanel();
		panel.setBounds(0, 0, 460, 298);
		contentPane.add(panel);
		panel.setLayout(null);

		JLabel lblServerLog = new JLabel("Server Log:");
		lblServerLog.setBounds(16, 6, 75, 16);
		panel.add(lblServerLog);

		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(16, 27, 427, 265);
		panel.add(scrollPane);

		jta = new JTextArea();
		jta.setEditable(false);
		scrollPane.setViewportView(jta);

		setVisible(true);

		try {
			// Create a server socket
			@SuppressWarnings("resource")
			ServerSocket serverSocket = new ServerSocket(8000);
			try {
				// Sleep .5 seconds before continuing
				Thread.sleep(500);
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}
			// Print server start time to text area
			jta.append("Server started at " + new Date() + '\n');
			// Connects to the DB
			dbqueries.run();
			// Writes successful or unsuccessful connection
			jta.append(dbqueries.ConnectedOrNot + '\n');
			while (true) {
				Socket socket = serverSocket.accept();
				myClient c = new myClient(socket);
				c.start();
			}

		} catch (IOException ex) {
			System.err.println(ex);
		}
	}

	class myClient extends Thread {
		// The socket the client is connected through
		private Socket socket;

		// The input and output streams to the client
		private DataInputStream inputFromClient;
		private DataOutputStream outputToClient;

		// Input variable used to choose which if statement
		private String input;
		// Variables used for log in
		private String username;
		private String password;

		// The method that runs when the thread starts
		public void run() {
			try {
				while (true) {

					// Reads the input
					input = inputFromClient.readUTF();

					// Sets the IP and host name
					ipAddress = InetAddress.getLocalHost();
					hostname = ipAddress.getHostName();
					String ip = ipAddress.getHostAddress();

					// If input is login run this if statement
					if (input.equals("Login")) {
						username = inputFromClient.readUTF();
						password = inputFromClient.readUTF();
						if (dbqueries.login(username, password) == true) {
							String fname = DBQueries.clientFName;
							String sname = DBQueries.clientSName;
							int tot_req = DBQueries.clientTotReq;
							// Print who connected to the server
							jta.append(fname + " " + sname + " has connected to the server succesfully\n");
							outputToClient.writeUTF("Success");
							// Return the first and last name for welcome messages and number of logins
							outputToClient.writeUTF(fname);
							outputToClient.flush();
							outputToClient.writeUTF(sname);
							outputToClient.flush();
							outputToClient.writeInt(tot_req);
							outputToClient.flush();
							outputToClient.writeInt(DBQueries.clientID);

						}
						// Prints this box if the login is unsuccessful
						else {
							outputToClient.writeUTF("UnSuccessful");
						}
					}

					// If input is radius run this if statement
					if (input.equals("Radius")) {
						// Takes in client hostname, ipaddress and radius
						String clientHostname = inputFromClient.readUTF();
						String clientIP = inputFromClient.readUTF();
						double radius = inputFromClient.readDouble();

						// Prints a String showing recieved values to the text area
						jta.append("Request recieved from Hostname: " + clientHostname + " with IPAddress: " + clientIP
								+ "\nRadius recieved: " + radius + "\n");

						// Calculates area of a circle
						double area = radius * radius * Math.PI;
						// Prints the area found to the text area
						jta.append("Area found:" + area + '\n');
						// Returns the server hostname, ipaddress and area found
						outputToClient.writeUTF(hostname);
						outputToClient.flush();
						outputToClient.writeUTF(ip);
						outputToClient.flush();
						outputToClient.writeDouble(area);
						outputToClient.flush();

					}
					
					//Exit message if a user exits
					if (input.equals("Exit")) {
						// Takes in client hostname, ipaddress and radius
						String fname = inputFromClient.readUTF();
						String lname = inputFromClient.readUTF();

						// Prints a String showing recieved values to the text area
						jta.append(fname + " " + lname + " has disconnected from the server\n");
					}
				}
			} catch (Exception e) {
				System.err.println(e + " on " + socket);
			}
		}

		// The Constructor for the client
		public myClient(Socket socket) throws IOException {
			// Declare & Initialise input/output streams
			inputFromClient = new DataInputStream(socket.getInputStream());
			outputToClient = new DataOutputStream(socket.getOutputStream());
		}
	}
}