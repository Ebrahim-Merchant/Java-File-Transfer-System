import javax.swing.*;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

public class MTRecievingServer {

    public static void main(String[] args) {
        try {
            ServerSocket serverSocket = new ServerSocket(5000);
            while(true) {
                Socket client = serverSocket.accept();
                FileTransferThread thread = new FileTransferThread(client); //Initialize the thread

            }

        } catch (IOException e) {

        }
    }

}

class FileTransferThread extends Thread{

    Socket clientConn;
    public FileTransferThread(Socket client){
        clientConn = client;
        this.start();
    }

    public void run(){
        try{
            System.out.println("Connected to client " + clientConn.getInetAddress().getHostAddress());
            DataInputStream dataInputStream = new DataInputStream(clientConn.getInputStream());
            String fileName = dataInputStream.readUTF();
            long fileSize = dataInputStream.readLong();
            byte data[] = new byte[2048]; // Here you can increase the size also which will receive it faster
            String filePathToSave = "C:\\Users\\emerc\\Documents\\test\\"+fileName; // The fileName
            File newFile = new File(fileName);
            /*
             * Creating a prompt the check with server if they want to download the application
             * */
            Object[] options = {"Yes", "No"};
            int n = JOptionPane.showOptionDialog(null,
                    clientConn.getInetAddress().getHostAddress()+" is trying to send you the file: "+ fileName + ". Do you want to accept?",
                    "File Request",
                    JOptionPane.YES_NO_CANCEL_OPTION,
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    options,
                    options[1]);
            if(n==1){
                clientConn.close();
                System.out.println("Connection has been closed");

            }
            DataOutputStream dataOutputStream = new DataOutputStream(new FileOutputStream(filePathToSave));
            int count;
            int sum = 0;
            while ((count = dataInputStream.read(data)) > 0) {
                sum += count;
                dataOutputStream.write(data, 0, count);
                System.out.println("Data received : " + sum);
                dataOutputStream.flush();
            }
            dataOutputStream.close();
            dataInputStream.close();
            /*
             * Checks if the file is a text file if so then counts the words in them
             * */
            String[] fileNameArray = fileName.split("\\.");
            int wordCount = 0;
            if(fileNameArray[fileNameArray.length-1].equals("txt")){
                try(Scanner sc = new Scanner(new FileInputStream(new File(filePathToSave)))){
                    while(sc.hasNext()){
                        sc.next();
                        wordCount++;
                    }
                    System.out.println("Number of words: " + wordCount);
                }
            }
            /*
             * Creates a log file
             * */
            File logFile = new File("C:\\Users\\emerc\\Documents\\test\\log.csv");
            if(!logFile.exists())
            {
                logFile.createNewFile();

            }
            FileWriter logFileWriter = new FileWriter(logFile, true);
            String logText = "Time:,"+java.time.LocalDateTime.now()+",Client Address:,"+clientConn.getInetAddress().getHostAddress()+",File Received:,"+fileName+",";
            if(wordCount != 0){
                logText += "Word Count:,"+wordCount;
            }
            logFileWriter.write(logText);
            logFileWriter.flush();
            logFileWriter.close();
            //Lets user know that the File has been received
            JOptionPane.showMessageDialog(null, "File received");

           }
        catch(IOException e){

        }

    }
}
