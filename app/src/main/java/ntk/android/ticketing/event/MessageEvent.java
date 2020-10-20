package ntk.android.ticketing.event;

public class MessageEvent {

    private String Message;

    public MessageEvent(String m) {
        this.Message = m;
    }

    public String GetMessage() {
        return Message;
    }
}
