package ChatBox.MsgModels;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class OutMsg extends Msg{

    private String message_text;
    private static final byte code = (byte) 4;
    private long UID;

    public OutMsg(long number, long UID, String message_text) {
        super(number);
        this.UID = UID;
        this.message_text = message_text;
    }

    public byte getCode() {
        return code;
    }

    public long getUID() {
        return UID;
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
            dos.writeLong(UID);
            dos.writeUTF(message_text);
            dos.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return bos.toByteArray();
    }
}
