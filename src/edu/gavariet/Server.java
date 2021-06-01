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
            	   String username = "", password = "";
                   try {
                       BufferedReader bReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                       BufferedWriter bWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
                       
                       boolean isLogged = false, printMenu = true;                    
                       bWriter.write("Write: \"END\" to close the connection.\n");
                       bWriter.flush();
                       
                       String userInput = "";

                       while (!userInput.contains("END")){
                    	   //System.out.println("While-start");
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
                    	   if(printMenu) {
                    	   	   bWriter.write("MENU\n");
                    		   bWriter.flush();
                    		   bWriter.write("List active users: \"LIST\", show help: \"HELP\"\n");
                    		   bWriter.flush();
                    		   printMenu = false;
                    	   }
                    	   if(userInput.equals("LIST")) {
                    		   bWriter.write("Active users: "+ loggedIn.toString() +"\n");
                    		   bWriter.flush();
                    	   } else if(userInput.equals("HELP")){
                    		   printMenu = true;
                    		   continue;
                    	   }
                		   
                		   userInput = bReader.readLine();
                		   //System.out.println("Waiting.");
                    	   
                       }
                       System.out.println("Connection closed.");
                       socket.close();
                   } catch (IOException e1) {
                	   System.out.println("Connection closed.");
                       //e.printStackTrace();
                   }
                   if(loggedIn.contains(username)) {
                	   loggedIn.remove(username);
                   }
                   
                   
               }
           };
           executorService.submit(connection);
       }
   }
}
