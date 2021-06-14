package edu.gavariet;

/**
 * @author Krzysztof Palmi, Jan Lozinski
 * @version 1.0
 */

import java.io.*;
import java.net.BindException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.HashMap;
import java.util.ArrayList;

public class Server {
	public static final String incorrectPasswordMsg = "User or password incorrect!";
	public static final String correctPasswordMsg = "Password correct!";
	
	public static void main(String[] args) throws IOException {
	   
	   final int SOCKET_ID = 12347;
       
	   HashMap<String, String> logins = new HashMap<String, String>();
	   HashMap<String, String> awaitingMsg = new HashMap<String, String>(); //key: USER, value: awaiting msg to user
       ArrayList<String> loggedIn = new ArrayList<String>();
       
       // username and password database 
       logins.put("abc", "password");
       logins.put("gunwiak", "maslo");
	   
	   ServerSocket serverSocket = new ServerSocket(SOCKET_ID);
       ExecutorService executorService = Executors.newFixedThreadPool(10);
       while (true){
           final Socket socket = serverSocket.accept();
           System.out.println("New connection.");
           Runnable connection = new Runnable() {
               @Override
               public void run() {
            	   String username = "", password = "", connectToUser = "";
                   try {
                       BufferedReader bReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                       BufferedWriter bWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
                       
                       boolean isLogged = false, printMenu = true, connectedToSomeone = false;                    
                       bWriter.write("Write: \"END\" to close the connection.\n");
                       bWriter.flush();
                       
                       String userInput = "";

                       while (!userInput.contains("END")){

                    	   if(!isLogged) {	// check if user is logged or not
                    		   bWriter.write("Enter username: ");
                               bWriter.write("\n");
                               bWriter.flush();
                               username = bReader.readLine();
                               
                               bWriter.write("Enter password: ");
                               bWriter.write("\n");
                               bWriter.flush();
                               password = bReader.readLine();

                               if(password.strip().equals(logins.get(username))) {
                            	   bWriter.write(correctPasswordMsg);
                            	   isLogged = true;
                            	   loggedIn.add(username);
                               } else {
                            	  bWriter.write(incorrectPasswordMsg);
                               }
                               bWriter.write("\n");
                               bWriter.flush();
                               continue;	// next try of login (skip whats below)
                    	   }
                    	   
                    	   if(connectedToSomeone) {
                    		   userInput = bReader.readLine();
                    		   continue;
                    	   }
                    	   if(printMenu) {
                    	   	   bWriter.write("MENU");
                    	   	   bWriter.write("\n");
                    		   bWriter.flush();
                    		   bWriter.write("List active users: \"LIST\", show help: \"HELP\", start conversation: \"CONNECT <user>\"");
                    		   bWriter.write("\n");
                    		   bWriter.flush();
                    		   printMenu = false;
                    	   } else if(userInput.equals("LIST")) {
                    		   bWriter.write("Active users: "+ loggedIn.toString() +"\n");
                    		   bWriter.flush();
                    	   } else if(userInput.strip().equals("HELP")){
                    		   printMenu = true;
                    		   //userInput = "";
                    		   continue;
                    	   } else if(userInput.length() >= 8){
                    		   if(userInput.subSequence(0, 7).equals("CONNECT")) {
                    			   connectToUser = userInput.substring(8, userInput.length());
                    			   if(loggedIn.contains(connectToUser) & !(username.equals(connectToUser))) {
                    				   connectedToSomeone = true;
                    				   bWriter.write("Connected to "+connectToUser+".\n");
                    			   } else {
                    				   bWriter.write("No such person on server or cannot connect to yourself.\n");
                    			   }
                        		   bWriter.flush();
                    		   }
                    	   } else if(userInput.length() >= 0) {
                    		   // Wrong command condition
                    		   bWriter.write("Wrong command.\n");
                    		   bWriter.flush();
                    	   }
                		   
                		   userInput = bReader.readLine();
                		   System.out.println(userInput);
                		   //System.out.println("Waiting.");
                    	   
                       }
                       System.out.println("Connection closed.");
                       socket.close();
                       loggedIn.remove(username);
                   } catch (IOException e1) {
                	   System.out.println("Connection closed.");
                	   loggedIn.remove(username);
                       //e.printStackTrace();
                   }
//                   if(loggedIn.contains(username)) {
//                	   loggedIn.remove(username);
//                   }
                   
                   
               }
           };
           executorService.submit(connection);
       }
   }
}
