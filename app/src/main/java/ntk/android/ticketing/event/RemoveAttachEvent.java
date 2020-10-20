package ntk.android.ticketing.event;

public class RemoveAttachEvent {

    private int position;

    public RemoveAttachEvent(int p) {
        this.position = p;
    }

    public int GetPosition() {
        return position;
    }
}
