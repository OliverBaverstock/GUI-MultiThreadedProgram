import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;
import javax.swing.JOptionPane;

public class Server {
	// Main function to start app
	public static void main(String[] args) {
		new Server();
	}

	// Creating ServerLogIn object
	ServerLogIn serverlogin = new ServerLogIn();
	// Creating DBQueries object
	DBQueries dbqueries = new DBQueries();

	// Creating variables for hostname and ipaddress
	private String hostname;
	private InetAddress ipAddress;

	public Server() {

		try {
			// Create a server socket
			@SuppressWarnings("resource")
			ServerSocket serverSocket = new ServerSocket(8000);
			try {
				// Sleep .5 seconds before continuing
				Thread.sleep(500);
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			// Print server start time to text area
			serverlogin.jta.append("Server started at " + new Date() + '\n');
			try {
				// Calls function to connect to Login to the Server
				serverlogin.connectToServer();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			// Connects to the DB
			dbqueries.run();
			// Writes succesful or unsucessful connection
			serverlogin.jta.append(dbqueries.ConnectedOrNot + '\n');
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

					// Sets the ipaddress and hostname
					ipAddress = InetAddress.getLocalHost();
					hostname = ipAddress.getHostName();
					String ip = ipAddress.getHostAddress();

					// If input is login run this if statement
					if (input.equals("Login")) {
						username = inputFromClient.readUTF();
						password = inputFromClient.readUTF();
						if (dbqueries.login(username, password) == true) {

							@SuppressWarnings("unused")
							Client client = new Client();
						}
						// Prints this box if the login is unsuccessful
						else {
							JOptionPane.showMessageDialog(null,
									"Record Not Found \nPlease enter a valid Username and Password");
						}
					}

					// If input is radius run this if statement
					if (input.equals("Radius")) {
						// Takes in client hostname, ipaddress and radius
						String clientHostname = inputFromClient.readUTF();
						String clientIP = inputFromClient.readUTF();
						double radius = inputFromClient.readDouble();

						// Prints a String showing recieved values to the text area
						serverlogin.jta.append("Request recieved from Hostname: " + clientHostname + " with IPAddress: "
								+ clientIP + "\nRadius recieved: " + radius + "\n");

						// Calculates area of a circle
						double area = radius * radius * Math.PI;
						// Prints the area found to the text area
						serverlogin.jta.append("Area found:" + area + '\n');
						// Returns the server hostname, ipaddress and area found
						outputToClient.writeUTF(hostname);
						outputToClient.flush();
						outputToClient.writeUTF(ip);
						outputToClient.flush();
						outputToClient.writeDouble(area);
						outputToClient.flush();

					}

					// If input is welcome run this if statement
					if (input.equals("Welcome")) {
						// Sets variables to the client first and last name to welcome them and total
						// requests
						String fname = DBQueries.clientFName;
						String sname = DBQueries.clientSName;
						int tot_req = DBQueries.clientTotReq;

						// Print who connected to the server
						serverlogin.jta.append(fname + " " + sname + " has connected to the server succesfully\n");

						// Return the first and last name for welcome messages and number of logins
						outputToClient.writeUTF(fname);
						outputToClient.flush();
						outputToClient.writeUTF(sname);
						outputToClient.flush();
						outputToClient.writeInt(tot_req);
						outputToClient.flush();
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
}// End Server Construct}