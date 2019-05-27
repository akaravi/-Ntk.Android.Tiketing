package ntk.android.ticketing.event;

public class EvRemoveAttach {

    private int position;

    public EvRemoveAttach(int p) {
        this.position = p;
    }

    public int GetPosition() {
        return position;
    }
}
