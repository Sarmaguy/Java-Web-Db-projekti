package ChatBox;

import ChatBox.MsgModels.Ack;
import ChatBox.MsgModels.Hello;
import ChatBox.MsgModels.InMsg;

import javax.swing.*;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.net.*;
import java.util.Random;

public class Client {

    public static void main(String[] args) throws UnknownHostException, SocketException {

        if (args.length != 3) {
            throw new IllegalArgumentException("Host, port and username required");
        }

        String host = args[0];
        int port = Integer.parseInt(args[1]);
        String username = args[2];

        InetAddress ip = InetAddress.getByName(host);
        DatagramSocket socket = new DatagramSocket();
        Random random = new Random();
        long randomUID = random.nextLong();
        randomUID = Math.abs(randomUID);
        ClientInfo client = new ClientInfo(ip, port, username, randomUID);



        Hello hello = new Hello(0,username, randomUID);
        byte[] helloBytes = hello.toBytes();
        Ack ack = null;
        DatagramPacket packet = new DatagramPacket(helloBytes, helloBytes.length);
        packet.setAddress(ip);
        packet.setPort(port);

        int retransmissionCounter = 0;
        //send hello message and wait for ack
        while (retransmissionCounter < 10) {
            try {
                socket.send(packet);
            } catch (Exception e) {
                retransmissionCounter++;
                continue;
            }

            byte[] ackBytes = new byte[50];
            DatagramPacket ackPacket = new DatagramPacket(ackBytes, ackBytes.length);

            try{
                socket.setSoTimeout(5000);
            }catch (SocketException ignored){
            }

            try {
                socket.receive(ackPacket); //zasto ne cekas ?
                System.out.println("Received ack");
            } catch (Exception ignored){
                retransmissionCounter++;
                continue;
            }


            ByteArrayInputStream bais = new ByteArrayInputStream(ackPacket.getData());
            DataInputStream dis = new DataInputStream(bais);

            try{
                dis.readByte();
                ack = new Ack(dis.readLong(), dis.readLong());

            } catch (IOException e) {
                System.err.println("Could not read Ack message");
                continue;
            }

            if (ack.getNumber() == 0) break;

        }

        client.setUID(ack.getUID());


        Thread thread = new Server.Service(client, socket);
        client.setThread(thread);
        thread.start();

        System.out.println("Client is running");

        SwingUtilities.invokeLater(() -> new ClientWindow(client, socket).setVisible(true));

        //recieve messages
        long lastNumber = -4;
        while (true){
            if (socket.isClosed()) break;

            byte[] buffer = new byte[4096];
            DatagramPacket receving_packet = new DatagramPacket(buffer, buffer.length);

            try {
                socket.receive(receving_packet);
            } catch (IOException  e) {
                if (socket.isClosed()) break;
                e.printStackTrace();
            }

            byte[] data = receving_packet.getData();

            if (data[0]==2) {
                ByteArrayInputStream bais = new ByteArrayInputStream(data);
                DataInputStream dis = new DataInputStream(bais);
                Ack ack1;
                try {
                    dis.readByte();
                    ack1 = new Ack(dis.readLong(), dis.readLong());
                } catch (IOException e) {
                    System.err.println("Could not read Ack message");
                    continue;
                }

                client.addAck(ack1);
            } else if (data[0]==5) {
                InMsg inMsg;
                ByteArrayInputStream bais = new ByteArrayInputStream(data);
                DataInputStream dis = new DataInputStream(bais);
                try {
                    dis.readByte();
                    inMsg = new InMsg(dis.readLong(), dis.readUTF(), dis.readUTF());
                } catch (IOException e) {
                    System.err.println("Could not read InMsg message");
                    continue;
                }

                if (inMsg.getNumber() > lastNumber) {
                    lastNumber = inMsg.getNumber();

                    ByteArrayInputStream bais1 = new ByteArrayInputStream(data);
                    DataInputStream dis1 = new DataInputStream(bais1);
                    try {
                        dis1.readByte();
                        inMsg = new InMsg(dis1.readLong(), dis1.readUTF(), dis1.readUTF());
                    } catch (IOException e) {
                        System.err.println("Could not read InMsg message");
                        continue;
                    }

                    client.addInMsg(inMsg);
                }

                Ack ack1 = new Ack(inMsg.getNumber(), client.getUID());
                byte[] ackBytes = ack1.toBytes();
                DatagramPacket ackPacket = new DatagramPacket(ackBytes, ackBytes.length);
                ackPacket.setSocketAddress(receving_packet.getSocketAddress());

                try {
                    socket.send(ackPacket);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        System.out.println("Client is closed");
        socket.close();
        client.poison();
        System.exit(0);
    }
}
