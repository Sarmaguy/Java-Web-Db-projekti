package ChatBox;

import ChatBox.MsgModels.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

public class ClientWindow extends JFrame {

    private ClientInfo clientInfo;
    private DatagramSocket socket;
    private JTextArea textArea;
    public ClientWindow(ClientInfo clientInfo, DatagramSocket socket) {
        super("ChatBox");
        this.clientInfo = clientInfo;
        this.socket = socket;
        addWindowListener(new closeWindow());
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setSize(400, 400);
        setVisible(true);
        initGUI();
        Thread thread = new MessageThread(textArea, clientInfo);
        thread.start();
    }

    private void initGUI(){
        textArea = new JTextArea();
        JTextField textField = new JTextField();
        textField.addActionListener(e -> {
            clientInfo.addMsg(new OutMsg(clientInfo.getNewMsgCounter(), clientInfo.getUID(), textField.getText()));
            textField.setText("");
        });

        Container container = getContentPane();
        container.setLayout(new BorderLayout());
        container.add(textArea, BorderLayout.CENTER);
        container.add(textField, BorderLayout.NORTH);


    }

    private class MessageThread extends Thread{
        JTextArea textArea;
        ClientInfo clientInfo;

        public MessageThread(JTextArea textArea, ClientInfo clientInfo){
            super();
            this.textArea = textArea;
            this.clientInfo = clientInfo;
        }

        @Override
        public void run() {
            while (true) {
                InMsg m = clientInfo.getInMsg();
                String s= "Message from " + m.getUsername() + ": \n" + m.getMessage_text()+"\n";
                textArea.append(s);
            }
        }


    }

    private class closeWindow extends WindowAdapter{
        @Override
        public void windowClosing(WindowEvent e){
            Bye bye = new Bye(clientInfo.getNewMsgCounter(),clientInfo.getUID());
            byte[] byeBytes = bye.toBytes();
            DatagramPacket byePacket = new DatagramPacket(byeBytes, byeBytes.length);
            byePacket.setAddress(clientInfo.getIp());
            byePacket.setPort(clientInfo.getPort());

            int retransmissionCounter = 0;
            while (retransmissionCounter < 10) {


                try {
                    socket.send(byePacket);
                }catch (IOException ex){
                    retransmissionCounter++;
                    continue;
                }

                Ack ack = clientInfo.getAck();

                if (ack!=null && ack.getNumber()==bye.getNumber()) {
                    clientInfo.poison();
                    socket.close();
                    break;
                }

                else retransmissionCounter++;

            }
            System.out.println("ovo se desi");
            dispose();
        }
    }
}

