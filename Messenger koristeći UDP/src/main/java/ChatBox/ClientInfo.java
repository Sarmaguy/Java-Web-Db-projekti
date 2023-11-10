package ChatBox;

import ChatBox.MsgModels.Ack;
import ChatBox.MsgModels.InMsg;
import ChatBox.MsgModels.Msg;

import java.net.InetAddress;
import java.util.Objects;
import java.util.concurrent.LinkedBlockingQueue;

public class ClientInfo {

    private InetAddress ip;
    private int port;
    private String username;
    private long randkey;
    private long UID;
    private int msgCounter;
    private Thread thread;
    private LinkedBlockingQueue<Ack> acks = new LinkedBlockingQueue<>();
    private LinkedBlockingQueue<Msg> msgs = new LinkedBlockingQueue<>();
    private LinkedBlockingQueue<InMsg> inMsgs = new LinkedBlockingQueue<>();


    public ClientInfo(InetAddress ip, int port, String username, long randkey) {
        this.ip = ip;
        this.port = port;
        this.username = username;
        this.randkey = randkey;
        this.msgCounter = 1;
    }

    public InetAddress getIp() {
        return ip;
    }

    public int getPort() {
        return port;
    }

    public String getUsername() {
        return username;
    }

    public long getRandkey() {
        return randkey;
    }

    public long getUID() {
        return UID;
    }

    public void setUID(long UID) {
        this.UID = UID;
    }

    public int getNewMsgCounter() {
        return msgCounter++;
    }

    public void setThread(Thread thread) {
        this.thread = thread;
    }

    public void addAck(Ack ack){
        while (true) {
            try {
                acks.put(ack);
                break;
            } catch (InterruptedException e) {
            }
        }
    }

    public void addMsg(Msg msg){
        while (true) {
            try {
                msgs.put(msg);
                break;
            } catch (InterruptedException e) {
            }
        }
    }

    public Msg getMsg(){
        while (true) {
            try {
                return msgs.take();
            } catch (InterruptedException e) {
            }
        }
    }

    public Ack getAck(){
        while (true) {
            try {
                return acks.take();
            } catch (InterruptedException e) {
            }
        }
    }

    public void addInMsg(InMsg msg){
        while (true) {
            try {
                inMsgs.put(msg);
                break;
            } catch (InterruptedException e) {
            }
        }
    }

    public InMsg getInMsg(){
        while (true) {
            try {
                return inMsgs.take();
            } catch (InterruptedException e) {
            }
        }
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ClientInfo that)) return false;
        return port == that.port && randkey == that.randkey && UID == that.UID && msgCounter == that.msgCounter && Objects.equals(ip, that.ip) && Objects.equals(username, that.username);
    }

    @Override
    public int hashCode() {
        return Objects.hash(ip, port, username, randkey, UID, msgCounter);
    }



    public void poison() {
        thread.interrupt();
    }
}
