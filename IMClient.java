import java.io.*;
import java.net.*;
import java.awt.*;
import java.awt.event.*;

import javax.swing.*;

@SuppressWarnings("serial")
public class IMClient extends JFrame{

	private JTextField userText;				//where message is typed
	private JTextArea chatWindow;
	private ObjectOutputStream output; 
	private ObjectInputStream input;
	private String serverIP;					//address of server
	private Socket connection;

	//constructor
	public IMClient(String host){
		super("2:32 Chat");
		serverIP = host;
		userText = new JTextField();
		userText.setEditable(false);			//set to false until connected will change status later
		userText.addActionListener(
				new ActionListener(){
					public void actionPerformed(ActionEvent event){
						sendMessage(event.getActionCommand());
						userText.setText("");
					}   
				}
				);
		add(userText, BorderLayout.NORTH);
		chatWindow = new JTextArea();
		add(new JScrollPane(chatWindow),BorderLayout.CENTER);
		setSize(275,210);
		setVisible(true);
	}
	public void startRunning(){
		try{									//connect to server and have conversation
			connectToServer();					
			setupStreams();						//sets-up input and output stream with that computer
			whileChatting();					//method that allows for sending messages back and fourth 

		}catch(EOFException eofException){
			//ends the connection
			showMessage("\nServer ended the connection! ");
		}catch(IOException ioException){
			ioException.printStackTrace();
		}finally{
			theCloser();
		}
	}
	//connect to server
	private void connectToServer() throws IOException{
		showMessage("Attempting Connection... \n");
		connection = new Socket(InetAddress.getByName(serverIP), 6789);		//server name(sets name of host) and port
		showMessage("Connected to a 2:32Tutor @"+connection.getInetAddress().getHostName());	

	}

	private void setupStreams() throws IOException{
		output= new ObjectOutputStream(connection.getOutputStream());		//creates pathway for data to be sent
		output.flush();														//clears buffer in the Output Stream (good housekeeping)
		input= new ObjectInputStream(connection.getInputStream());			//creates pathway for data to be received
		showMessage("\nStreams - OK. \n");
	}

	private void whileChatting() throws IOException{
		String message= "Send a Message";
		ableToType(true);		//Makes JTextField editable 
		do{
			try{
				message = (String) input.readObject(); //client message
				showMessage("\n"+ message);
			}catch(ClassNotFoundException classNotFoundException){
				showMessage("\nOOPs! This is not right, let us get back to you");
			}
		}while(!message.equals("232Tutor - END"));							
//END is how server will end the conversation
	}
	private void theCloser(){
		showMessage("\nClosing connections... \n");
		ableToType(false);
		try{
			output.close();
			input.close();
			connection.close();
		}catch(IOException ioException){
			ioException.printStackTrace();
		}
	}
	//gets message from text box
	private void sendMessage(String message){							
		try{
		//WRITE OBJECT IS BUILT INTO JAVA, SENDS STRING message TO OUTPUT STREAM
			output.writeObject("Client  -  "+message);				
			output.flush();			
			showMessage("\nClient - " +message); //displays servers message in chat window
		}catch(IOException ioException){
			chatWindow.append("ERROR: MESSAGE CAN'T BE SENT ");	//adds text to chat window
		}
	}
	//updates chatWindow
	private void showMessage(final String m){
	//used to update text in chatWindow, sets aside a thread to update a part of the GUI
		SwingUtilities.invokeLater(										
				new Runnable(){
					public void run(){
						chatWindow.append(m);
					}
				}
				);
	}
	//let's client type into their box
	private void ableToType(final boolean tof){
//used to update text area to editable or not  , sets aside a thread to update a part of the GUI
		SwingUtilities.invokeLater(		
				new Runnable(){
					public void run(){
						userText.setEditable(tof);
					}
				}
				);
	}
}
