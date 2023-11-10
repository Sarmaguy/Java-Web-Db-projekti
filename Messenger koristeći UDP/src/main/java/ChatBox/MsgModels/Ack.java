package ChatBox.MsgModels;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class Ack extends Msg{

        private static final byte code = (byte) 2;
        private long UID;

        public Ack(long number, long UID) {
            super(number);
            this.UID = UID;
        }


        public byte getCode() {
            return code;
        }

        public long getUID() {
            return UID;
        }

        public byte[] toBytes(){
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            DataOutputStream dos = new DataOutputStream(bos);
            try {
                dos.writeByte(code);
                dos.writeLong(super.getNumber());
                dos.writeLong(UID);
                dos.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            return bos.toByteArray();
        }
}
