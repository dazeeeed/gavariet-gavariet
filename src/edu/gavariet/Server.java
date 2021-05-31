package edu.gavariet;

/**
 * @author Krzysztof Palmi, Jan Lozinski
 * @version 1.0
 */

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {

   public static void main(String[] args) throws IOException {
	   final int socket_id = 12347;
       ServerSocket serverSocket = new ServerSocket(socket_id);
       ExecutorService executorService = Executors.newFixedThreadPool(10);
       while (true){
           final Socket socket = serverSocket.accept();
           System.out.println("New connection.");
           Runnable connection = new Runnable() {
               @Override
               public void run() {
                   try {
                       BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                       BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
                       bufferedWriter.write("Write: \"END\" to close the connection.\n");
                       bufferedWriter.flush();
                       String line = bufferedReader.readLine();
                       while (!line.contains("END")){
                           bufferedWriter.write("Server says: ");
                           bufferedWriter.write(line);
                           bufferedWriter.write("\n");
                           bufferedWriter.flush();
                           line = bufferedReader.readLine();
                       }
                       socket.close();
                   } catch (IOException e) {
                       e.printStackTrace();
                   }
               }
           };
           executorService.submit(connection);
       }
   }
}
