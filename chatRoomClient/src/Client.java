/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Er Vaibhav Karnwal
 */
import java.io.*;
import java.net.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class Client extends JFrame{
        private JTextField userText;
	private JTextArea chatWindow;
	private ObjectOutputStream output;
        private ObjectInputStream input;
        private String message="";
	private String serverIP;
	private Socket connection;
        
        
    public Client(String host){
	super("Client mofol");
        serverIP = host;
	userText=new JTextField();
	userText.setEditable(false);
	userText.addActionListener(
		new ActionListener(){
			public void actionPerformed(ActionEvent event){
				sendData(event.getActionCommand());
				userText.setText("");
			}
		}
	);

	add(userText, BorderLayout.NORTH);
	chatWindow =new JTextArea();
	add(new JScrollPane(chatWindow), BorderLayout.CENTER);
	setSize(300,150);
	setVisible(true);
    }
    public void startRunning(){
        try{
            connectToServer();
            setupStreams();
            whileChatting();
        }catch(EOFException eofException){
            showMessage("\n Client terminated Connection !");
	}catch(IOException ioException){
            ioException.printStackTrace();
	}finally{
            closeCrap();
        }
    }
    private void connectToServer() throws IOException{
	showMessage("Attempting connection.... \n");
	connection=new Socket("192.168.43.250",6789);
        showMessage("connected to "+ connection.getInetAddress().getHostName());	
    }
    private void setupStreams() throws IOException{
	output= new ObjectOutputStream(connection.getOutputStream());
	output.flush();
        input = new ObjectInputStream(connection.getInputStream());
	showMessage("\n You can type your message now! \n");
	}
    
	//while chatting with server
    
    private void whileChatting() throws IOException{
        ableToType(true);
	do{
            try{
                message=(String)input.readObject();
		showMessage("\n"+message);
            }catch(ClassNotFoundException classNotFoundException){
                showMessage("\nI dont know that object type!");
            }
	}while(!message.equals("SERVER - END"));
    }
        //close streams and sockets after you are done chatting
        private void closeCrap(){
            showMessage("\n Closing crap down.....\n");
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
        private void sendData(String message){
            try{
                output.writeObject("CLIENT -"+ message);
                output.flush();
                showMessage("\nCLIENT -"+message);
            }catch(IOException ioException){
                chatWindow.append("\n something messed up sending message hoss!");
            }
        }
        private void showMessage(final String m){
            SwingUtilities.invokeLater(
                    new Runnable(){
                        public void run(){
                            chatWindow.append(m);
                        }
                    }
            );
        }
        
        // gives permission to type crap into the text box
        private void ableToType(final boolean tof){
            SwingUtilities.invokeLater(
                    new Runnable(){
                        public void run(){
                            userText.setEditable(tof);
                        }
                    }
            );
        }
}
