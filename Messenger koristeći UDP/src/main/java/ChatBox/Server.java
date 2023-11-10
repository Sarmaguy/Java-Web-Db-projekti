package ChatBox;

import ChatBox.MsgModels.*;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;


public class Server {

    public static void main(String[] args) {

        if (args.length != 1) {
            System.err.println("Usage: java Server <port number>");
            System.exit(1);
        }

        int portNumber = 0;

        try {
            portNumber = Integer.parseInt(args[0]);

        } catch (NumberFormatException e) {
            System.err.println("Port number must be an integer");
            System.exit(1);
        }

        if (portNumber < 1 || portNumber > 65535) {
            System.err.println("Port number must be between 1 and 65535");
            System.exit(1);
        }

        DatagramSocket socket = null;

        try {
            socket = new DatagramSocket(portNumber);
        } catch (SocketException e) {
            System.err.println("Could not listen on port " + portNumber);
            System.exit(1);
        }
        Random random = new Random();
        //randomly generated long value
        //ee ovo bi trebo bit pozitivn broj
        long randomUID = random.nextLong();
        randomUID = Math.abs(randomUID);

        Map<Long, ClientInfo> clients = new HashMap<Long, ClientInfo>();

        System.out.println("Server is running");

        int retransmissionCounter = 0;
        while (retransmissionCounter < 10) {
            byte[] buffer = new byte[4096];
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length);

            try {
                socket.receive(packet);
                System.out.println("Received packet");
            } catch (IOException e) {
                retransmissionCounter++;
                System.err.println("Could not receive packet");
                continue;
            }
            retransmissionCounter = 0;

            byte[] data = packet.getData();

            switch (data[0]){

                case 1:{
                    Hello hello;

                    ByteArrayInputStream bis = new ByteArrayInputStream(data);
                    DataInputStream dis = new DataInputStream(bis);
                    try{
                        dis.readByte();
                        hello = new Hello(dis.readLong(), dis.readUTF(),dis.readLong());

                    } catch (IOException e) {
                        System.err.println("Could not read Hello message");
                        continue;
                    }

                    ClientInfo clientInfo = new ClientInfo(packet.getAddress(), packet.getPort(), hello.getUsername(), hello.getRandkey());
                    for(Map.Entry<Long, ClientInfo> entry : clients.entrySet()){
                        if(entry.getValue().equals(clientInfo)){
                            Ack ack = new Ack(hello.getNumber(), entry.getKey());
                            sendAck(socket, packet, ack);
                            break;
                        }
                    }

                    clientInfo.setUID(randomUID);
                    Thread thread = new Service(clientInfo, socket);
                    clientInfo.setThread(thread);
                    clients.put(randomUID++, clientInfo);
                    thread.start();

                    Ack ack = new Ack(hello.getNumber(), clientInfo.getUID());
                    sendAck(socket, packet, ack);
                    break;
                }

                case 2:{
                    Ack ack;

                    ByteArrayInputStream bis = new ByteArrayInputStream(data);
                    DataInputStream dis = new DataInputStream(bis);
                    try{
                        dis.readByte();
                        ack = new Ack(dis.readLong(), dis.readLong());

                    } catch (IOException e) {
                        System.err.println("Could not read Ack message");
                        continue;
                    }

                    ClientInfo clientInfo = clients.get(ack.getUID());
                    clientInfo.addAck(ack);

                    break;


                }
                case 3:
                {
                    Bye bye;

                    ByteArrayInputStream bis = new ByteArrayInputStream(data);
                    DataInputStream dis = new DataInputStream(bis);
                    try{
                        dis.readByte();
                        bye = new Bye(dis.readLong(), dis.readLong());

                    } catch (IOException e) {
                        System.err.println("Could not read Bye message");
                        continue;
                    }

                    ClientInfo clientInfo = clients.get(bye.getUID());
                    clientInfo.poison();

                    clients.remove(bye.getUID());

                    sendAck(socket, packet, new Ack(bye.getNumber(), bye.getUID()));

                    System.out.println("Client " + clientInfo.getUsername() + " said bye");

                    break;
                }

                case 4:
                {
                    OutMsg outMsg;

                    ByteArrayInputStream bis = new ByteArrayInputStream(data);
                    DataInputStream dis = new DataInputStream(bis);
                    try{
                        dis.readByte(); //ovdje se mozd dogodilo sranje
                        outMsg = new OutMsg(dis.readLong(), dis.readLong(), dis.readUTF());

                    } catch (IOException e) {
                        System.err.println("Could not read OutMsg message");
                        continue;
                    }

                    ClientInfo clientInfo = clients.get(outMsg.getUID());

                    for (ClientInfo c : clients.values()) {
                       c.addMsg(new InMsg(c.getNewMsgCounter(), clientInfo.getUsername(), outMsg.getMessage_text()));
                    }

                    sendAck(socket, packet, new Ack(outMsg.getNumber(), outMsg.getUID()));
                    break;
                }

            }




        }

        socket.close();

    }

    private static void sendAck(DatagramSocket socket, DatagramPacket packet, Ack ack) {
        byte[] ackBytes = ack.toBytes();
        DatagramPacket ackPacket = new DatagramPacket(ackBytes, ackBytes.length);
        ackPacket.setSocketAddress(packet.getSocketAddress());

        try {
            socket.send(ackPacket);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return;
    }

    protected static class Service extends Thread{
        private ClientInfo clientInfo;
        private DatagramSocket socket;

        public Service(ClientInfo clientInfo, DatagramSocket socket){
            super();
            this.clientInfo = clientInfo;
            this.socket = socket;

        }

        @Override
        public void run() {
            while (true) {
                Msg m = clientInfo.getMsg();
                byte[] msgBytes = m.toBytes();
                DatagramPacket msgPacket = new DatagramPacket(msgBytes, msgBytes.length);
                msgPacket.setAddress(clientInfo.getIp());
                msgPacket.setPort(clientInfo.getPort());

                int retransmissionCounter = 0;

                while (retransmissionCounter < 10) {
                    try {
                        socket.send(msgPacket);
                    }catch (IOException e){
                        retransmissionCounter++;
                        continue;
                    }



                    Ack ack = clientInfo.getAck();

                    if (ack != null && ack.getNumber() == m.getNumber()) {
                        break;
                    }

                }
            }
        }

    }
}
