package ChatBox.MsgModels;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class Hello extends Msg{

    private static final byte code = (byte) 1;
    private String username;
    private long randkey;

    public Hello(long number, String username, long randkey) {
        super(number);
        this.username = username;
        this.randkey = randkey;
    }

    public String getUsername() {
        return username;
    }

    public long getRandkey() {
        return randkey;
    }

    public byte getCode() {
        return code;
    }

    /*
    ByteArrayOutputStream bos = new ByteArrayOutputStream();
    DataOutputStream dos = new DataOutputStream(bos);
    dos.writeByte(42);
    dos.writeLong(75L);
    dos.writeUTF("Ovo je string");
    dos.close();
    byte[] buf = dos.toByteArray();

     */
    public byte[] toBytes(){
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(bos);
        try {
            dos.writeByte(code);
            dos.writeLong(super.getNumber());
            dos.writeUTF(username);
            dos.writeLong(randkey);
            dos.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return bos.toByteArray();
    }

}
