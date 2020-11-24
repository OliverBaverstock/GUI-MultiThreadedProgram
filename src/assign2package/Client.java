package assign2package;
import java.io.*;
import java.net.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.net.InetAddress;

@SuppressWarnings("serial")
public class Client extends JFrame {
	// Text field for receiving radius
	private JTextField jtf = new JTextField();

	// Text area to display contents
	private JTextArea jta = new JTextArea();

	// IO streams
	private DataOutputStream toServer;
	private DataInputStream fromServer;

	private String hostname;
	private InetAddress ipAddress;

	// Main function to call client
	public static void main(String[] args) {
		new Client();
	}

	public Client() {

		// Panel p to hold the label and text field
		JPanel p = new JPanel();
		p.setLayout(new BorderLayout());
		p.add(new JLabel("Enter radius"), BorderLayout.WEST);
		p.add(jtf, BorderLayout.CENTER);
		jtf.setHorizontalAlignment(JTextField.RIGHT);

		setLayout(new BorderLayout());
		add(p, BorderLayout.NORTH);
		add(new JScrollPane(jta), BorderLayout.CENTER);

		jtf.addActionListener(new Listener()); // Register listener

		// Sets the title and adds the users SID to the title
		setTitle("Client - " + DBQueries.clientID);
		setSize(500, 300);
		setVisible(true);

		try {
			// Create a socket to connect to the server
			@SuppressWarnings("resource")
			Socket socket = new Socket("localhost", 8000);

			// Create an input stream to receive data from the server
			fromServer = new DataInputStream(socket.getInputStream());

			// Create an output stream to send data to the server
			toServer = new DataOutputStream(socket.getOutputStream());

			// Used to initiate the welcome function to welcome the user
			toServer.writeUTF("Welcome");
			// Reads in the users first and last name and number of logins(TOT_REQ)
			String clientFName = fromServer.readUTF();
			String clientSName = fromServer.readUTF();
			int tot_req = fromServer.readInt();
			// Writes a welcome message to the text area with the users name and shows the
			// number of logins
			// Also asked the user to enter the area of a circle
			jta.append("Welcome " + clientFName + " " + clientSName
					+ ", you have connected to the server succesfully!\nNumber of Logins: " + tot_req
					+ "\nPlease enter the radius of the circle\n");
		} catch (IOException ex) {
			jta.append(ex.toString() + '\n');
		}
	}

	// Listener used for when the user enters a radius
	private class Listener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			try {
				// Sets the client ip address and hostname
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

					// Sender hostname and IP to server
					toServer.writeUTF(hostname);
					toServer.flush();
					toServer.writeUTF(ip);
					toServer.flush();

					// Send the radius to the server
					toServer.writeDouble(radius);
					toServer.flush();

					// Reading Server Hostname and IP
					String serverHostname = fromServer.readUTF();
					String serverIP = fromServer.readUTF();

					// Get area from the server
					double area = fromServer.readDouble();

					// Displays radius sent, a response message with server hostname and ipaddress
					// and displays the area found sent by the server
					jta.append("Radius is " + radius + "\n");
					jta.append(
							"Response received from Server: " + serverHostname + " at IPAddress: " + serverIP + "\n");
					jta.append("Area found: " + area + '\n');
				} else {
					JOptionPane.showMessageDialog(null, "Please enter a valid radius\n");
				}
			} catch (IOException ex) {
				System.err.println(ex);
			}
		}
	}
}