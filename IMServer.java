package javahelp;

//uses streams and sockets, true networking example
import java.io.*;
import java.net.*;
import java.awt.*;
import java.awt.event.*;

import javax.swing.*;


public class IMServer extends JFrame {
	
	private JTextField userText;				//where message is typed
	private JTextArea chatWindow;
	private ObjectOutputStream output;   		// streams are like pipes between computers
	private ObjectInputStream input;
	private ServerSocket server;
	private Socket connection;					//socket = connection
	
	//constructor
	public IMServer(){
		super("2:32 Chat");
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
		add(new JScrollPane(chatWindow));
		setSize(250,210);
		setVisible(true);
	}
	
	//set up and run the server
	public void startRunning(){
		try{
			server = new ServerSocket(6789, 100);		//setup the port number and backlog(user queue length)
			while(true){
				try{
					//connect and have conversation
					waitForConnection();				//waits for computer to connect with
					setupStreams();						//sets-up input and output stream with that computer
					whileChatting();					//method that allows for sending messages back and fourth 
					
				}catch(EOFException eofException){
					//ends the connection
					showMessage("\nclient ended the connection! ");
				}finally{
					theCloser();
				}
			}
		}catch(IOException ioException){
			ioException.printStackTrace();	
		}	
	}
	//wait for connection, then display information
	private void waitForConnection() throws IOException{
		showMessage("Searching for clients... \n");
		connection = server.accept();										// Listens for a connection from client
		showMessage("Now connected to "+ connection.getInetAddress().getHostName());
	}
	//get stream to send and receive data
	private void setupStreams() throws IOException{
		output= new ObjectOutputStream(connection.getOutputStream());		//creates pathway for data to be sent
		output.flush();														//clears buffer in the Output Stream (good housekeeping)
		input= new ObjectInputStream(connection.getInputStream());			//creates pathway for data to be received
		showMessage("\nStreams - OK \n");
	}
	//during conversation
	private void whileChatting() throws IOException{
		String message= "We are now connected";
		sendMessage(message);
		ableToType(true);													//Makes JTextField editable 
		do{
			try{
				message = (String) input.readObject();						//clients message
				showMessage("\n"+ message);
			}catch(ClassNotFoundException classNotFoundException){
				showMessage("\nThis client is sending bad data");
			}
		}while(!message.equals("CLIENT - END"));							//END is how client will end the conversation
		
	}
	//closes streams and sockets after you are done chatting
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
	//send a message to client
	private void sendMessage(String message){							//gets message from text box
		try{
		//WRITE OBJECT IS BUILT INTO JAVA, SENDS STRING message TO OUTPUT STREAM
			output.writeObject("2:32Tutor - "+message);				
			output.flush();			
			showMessage("\n2:32Tutor - " +message);							//displays servers message in chat window
		}catch(IOException ioException){
		chatWindow.append("ERROR: MESSAGE CAN'T BE SENT ");				//adds text to chat window
		}
	}
	//updates chatWindow
	private void showMessage(final String text){
	//used to update text in chatWindow, sets aside a thread to update a part of the GUI
		SwingUtilities.invokeLater(										
				new Runnable(){
					public void run(){
						chatWindow.append(text);
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
	
	
	

