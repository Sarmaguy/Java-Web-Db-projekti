package ChatBox.MsgModels;

public abstract class Msg {

    private long number;

    public Msg(long number) {
        this.number = number;
    }

    public long getNumber() {
        return number;
    }

    public abstract byte[] toBytes();
}
