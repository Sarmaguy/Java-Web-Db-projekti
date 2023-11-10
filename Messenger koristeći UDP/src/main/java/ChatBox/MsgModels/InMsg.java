package ChatBox.MsgModels;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class InMsg extends Msg{

    private String username;
    private String message_text;
    private static final byte code = (byte) 5;

    public InMsg(long number, String username, String message_text) {
        super(number);
        this.username = username;
        this.message_text = message_text;
    }

    public byte getCode() {
        return code;
    }

    public String getUsername() {
        return username;
    }

    public String getMessage_text() {
        return message_text;
    }

    public byte[] toBytes(){
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(bos);
        try {
            dos.writeByte(code);
            dos.writeLong(super.getNumber());
            dos.writeUTF(username);
            dos.writeUTF(message_text);
            dos.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return bos.toByteArray();
    }
}
