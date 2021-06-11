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
	
    public static boolean compareStrings(String input, String stringToCheck){
        for(int i = 0; i<stringToCheck.length(); i++){
            if(input.charAt(i) != stringToCheck.charAt(i)){
                return false;
            } 
        }
        return true;
    }
	
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
            	   String username = "", password = "", String = "kupa";
                   try {
                       BufferedReader bReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                       BufferedWriter bWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
                       
                       boolean isLogged = false, printMenu = true;                    
                       bWriter.write("Write: \"END\" to close the connection.\n");
                       bWriter.flush();
                       
                       String userInput = ""; int i = 0;

                       while (!userInput.contains("END")){
                    	   i++;
//                    	   if(userInput.equals("")) {
//                    		   System.out.println(i);
//                    	   } else {
//                    		   System.out.println("Empty");
//                    	   }
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
                    	   	   bWriter.write("MENU");
                    	   	   bWriter.write("\n");
                    		   bWriter.flush();
                    		   bWriter.write("List active users: \"LIST\", show help: \"HELP\", start conversation: \"CONNECT <user>\"");
                    		   bWriter.write("\n");
                    		   bWriter.flush();
                    		   printMenu = false;
                    	   } else if(userInput.equals("LIST") | userInput.equals("LIST\n")) {
                    		   bWriter.write("Active users: "+ loggedIn.toString() +"\n");
                    		   bWriter.flush();
                    	   } else if(userInput.strip().equals("HELP")){
                    		   printMenu = true;
                    		   //userInput = "";
                    		   continue;
                    	   } else if(userInput.equalsIgnoreCase("DROP")){
                    		   bWriter.write("Doing something.\n");
                    		   bWriter.flush();
                    	   } else if((userInput.subSequence(0, 7)).equals("CONNECT")){
                    		   bWriter.write("Connecting.\n");
                    		   bWriter.flush();
                    	   } 
//                    	   else if(userInput.equalsIgnoreCase("DROP")){
//                    		   bWriter.write("Wrong command.\n");
//                    		   bWriter.flush();
//                    	   }
                		   
                		   userInput = bReader.readLine();
                		   System.out.println(userInput);
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
