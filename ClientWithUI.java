import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class ClientWithUI {
    public static void main(String[] args) {
        /*
        * Initializing the components
        * */
        JTextField textField = new JTextField();
        textField.setPreferredSize(new Dimension(250, 20));
        JLabel heading = new JLabel("Enter Receiver's IP Address and Port Number:");
        JTextField ipAddress = new JTextField("127.0.0.1");
        JTextField portNum = new JTextField("5000");
        JLabel filePath = new JLabel("File Path");
        JButton openFile = new JButton();
        openFile.setText("Open");
        JLabel downloadInfo = new JLabel("");
        JButton sendFile = new JButton("Send");
        /*
         * Initializing the frame
         * */
        JFrame frame = new JFrame("Select a file to send");
        JPanel panel = new JPanel();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        /*
         * Creating group layouts
         * */
        GroupLayout layout = new GroupLayout(panel);
        panel.setLayout(layout);
        //Creating the columns
        GroupLayout.SequentialGroup leftToRight = layout.createSequentialGroup();
        GroupLayout.ParallelGroup firstColumn = layout.createParallelGroup();
        GroupLayout.ParallelGroup middleColumn = layout.createParallelGroup();
        GroupLayout.ParallelGroup lastColumn = layout.createParallelGroup();
        firstColumn.addComponent(heading);
        firstColumn.addComponent(filePath);
        leftToRight.addGroup(firstColumn);
        middleColumn.addComponent(ipAddress);
        middleColumn.addComponent(textField);
        middleColumn.addComponent(downloadInfo);
        leftToRight.addGroup(middleColumn);
        lastColumn.addComponent(portNum);
        lastColumn.addComponent(openFile);
        lastColumn.addComponent(sendFile);
        leftToRight.addGroup(lastColumn);
        //Creating the rows
        GroupLayout.SequentialGroup topToBottom = layout.createSequentialGroup();
        GroupLayout.ParallelGroup rowTop = layout.createParallelGroup();
        rowTop.addComponent(heading);
        rowTop.addComponent(ipAddress);
        rowTop.addComponent(portNum);
        topToBottom.addGroup(rowTop);
        GroupLayout.ParallelGroup rowMiddle = layout.createParallelGroup();
        GroupLayout.ParallelGroup lastRow = layout.createParallelGroup();
        rowMiddle.addComponent(filePath);
        rowMiddle.addComponent(textField);
        rowMiddle.addComponent(openFile);
        topToBottom.addGroup(rowMiddle);
        lastRow.addComponent(downloadInfo);
        lastRow.addComponent(sendFile);
        topToBottom.addGroup(lastRow);
        //Adding the rows and column layout to the main layout
        layout.setAutoCreateGaps(true);
        layout.setAutoCreateContainerGaps(true);
        layout.setHorizontalGroup(leftToRight);
        layout.setVerticalGroup(topToBottom);
        //Add the panels to the frames and make it visible
        frame.add(panel);
        frame.pack();
        frame.setVisible(true);


        /*
         * Creating Event Listener's for the buttons
         * */
        openFile.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                downloadInfo.setText("");
                //Creates a File Chooser Object and get the path the user selected
                JFileChooser fileChooser = new JFileChooser();
                int result = fileChooser.showOpenDialog(openFile);
                if (result == JFileChooser.APPROVE_OPTION) {
                    File selectedFile = fileChooser.getSelectedFile();
                    textField.setText(selectedFile.getAbsolutePath());
                }
            }
        });

        sendFile.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                BufferedInputStream bufferedInputStream;
                try{
                    byte data[];
                    String ipAddr = ipAddress.getText();
                    int port = Integer.parseInt(portNum.getText());
                    Socket sendSocket = new Socket(ipAddr, port);
                    System.out.println("Connected to server");
                    data = new byte[2048];
                    File sendingFile;
                    //Checks if user has selected a file
                    if(!textField.getText().equals("")){
                        sendingFile= new File(textField.getText());
                    }
                    else {
                        downloadInfo.setText("Unable to open file. Please try again");
                        return;
                    }
                    FileReader fileReader = new FileReader(sendingFile);
                    String fileName = sendingFile.getName();
                    long fileSize = sendingFile.length();
                    DataOutputStream DOS = new DataOutputStream(sendSocket.getOutputStream());
                    DOS.writeUTF(fileName);
                    DOS.writeLong(fileSize);
                    bufferedInputStream = new BufferedInputStream(new FileInputStream(sendingFile));
                    int count;
                    //Updating user
                    downloadInfo.setText("Sending file");
                    //Send data to the server
                    while((count = bufferedInputStream.read(data))>0)
                    {
                        DOS.write(data, 0, count);
                        DOS.flush();
                    }
                    DOS.close();
                    downloadInfo.setText("File send");
                    bufferedInputStream.close();
                    fileReader.close();
                }
                catch (Exception ie)
                {
                    downloadInfo.setText("<html>Please check if the server is <br>active or enter the right address</html>");

                }
            }
        });

    }
    }
