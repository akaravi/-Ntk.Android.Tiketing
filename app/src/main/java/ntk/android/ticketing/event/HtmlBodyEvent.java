package ntk.android.ticketing.event;

public class HtmlBodyEvent {

    private String Html;

    public HtmlBodyEvent(String m) {
        this.Html = m;
    }

    public String GetMessage() {
        return Html;
    }
}
