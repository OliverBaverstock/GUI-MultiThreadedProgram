import java.util.Date;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.SwingConstants;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowListener;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

import javax.swing.JTextField;
import javax.swing.JPasswordField;
import javax.swing.JButton;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

@SuppressWarnings("serial")
public class ServerLogIn extends JFrame {

	private JPanel contentPane;
	private JTextField usernameField;
	private JPasswordField passwordField;
	public JTextArea jta;

	private DataOutputStream toServer;
	@SuppressWarnings("unused")
	private DataInputStream fromServer;

	DBQueries dbqueries = new DBQueries();

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) throws InterruptedException {
		new ServerLogIn();
	}

	/**
	 * Create the frame and adds all the components
	 */
	public ServerLogIn() {
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
		// setVisible(true);

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

		JButton loginButton = new JButton("Log In");
		loginButton.addActionListener(new Listener());

		loginButton.setBounds(38, 106, 117, 29);
		panel.add(loginButton);

		JButton resetButton = new JButton("Reset");
		// Action listener to reset fields when reset button clicked
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
		// Action listener to exit when exit button is clicked
		exitButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int confirm = JOptionPane.showOptionDialog(contentPane, "Are You Sure to Close this Application?",
						"Exit Confirmation", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, null, null);
				if (confirm == 0) {
					System.exit(0);
				}
			}
		});

		panel.add(exitButton);

		JLabel lblServerLog = new JLabel("Server Log:");
		lblServerLog.setBounds(16, 140, 75, 16);
		panel.add(lblServerLog);

		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(16, 158, 427, 134);
		panel.add(scrollPane);

		jta = new JTextArea();
		scrollPane.setViewportView(jta);

		setVisible(true);

	}

	// Connects LoginPage to Server
	public void connectToServer() throws InterruptedException {
		// Waits 1 second before continuing
		Thread.sleep(1000);
		try {
			// Create a socket to connect to the server
			@SuppressWarnings("resource")
			Socket socket = new Socket("localhost", 8000);

			// Create an input stream to receive data from the server
			fromServer = new DataInputStream(socket.getInputStream());

			// Create an output stream to send data to the server
			toServer = new DataOutputStream(socket.getOutputStream());
			jta.append("Log In Connected to Server Succesfully at " + new Date() + '\n');
		} catch (IOException ex) {
			jta.append(ex.toString() + '\n');
		}
		// Waits .5 seconds before continuing
		Thread.sleep(500);
	}

	// Listener used for carrying out the login and sending data and receiving data
	// from server
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
				// Reset fields to black after login attempt
				usernameField.setText("");
				passwordField.setText("");
			} catch (Exception e1) {
				System.out.print(e1.getMessage());
			}
		}
	}
}