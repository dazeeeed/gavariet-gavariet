package edu.gavariet;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;
import java.util.NoSuchElementException;

public class Client {
	public static void main(String[] args) throws Exception{
	   try {
	       Socket socket = new Socket("192.168.8.189", 12347);
	       BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
	       BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
	       Scanner sc = new Scanner(System.in);		// stdin scanner
	              
	       String serverLine = reader.readLine();	//get response from server
	       String userInput = "";
	
	       System.out.println(serverLine);
	       System.out.flush();
	       serverLine = reader.readLine();
	
	       while (serverLine != null){
	           System.out.println(serverLine);
	           System.out.flush();

	           if(serverLine.equals(Server.incorrectPasswordMsg) | serverLine.equals(Server.correctPasswordMsg)) {   
	        	   serverLine = reader.readLine();
	        	   continue;
	           } else if(serverLine.equals("MENU")){
	        	   serverLine = reader.readLine();
	        	   continue;
	           }

	           userInput = sc.nextLine();
        	   writer.write(userInput + '\n');
        	   writer.flush(); 	   
	    	   
	           serverLine = reader.readLine();
	           
	           // exit condition
	           if(userInput.contains("END")){
	    		   break;
	    	   }
	       }
	   } catch (IOException | NoSuchElementException e) {
		   System.out.println("\nEXIT.\n");
		   //e.printStackTrace();
	   }
   }
}