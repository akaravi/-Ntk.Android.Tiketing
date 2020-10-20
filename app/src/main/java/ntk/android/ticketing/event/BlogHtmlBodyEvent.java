package ntk.android.ticketing.event;

public class BlogHtmlBodyEvent {

    private String Html;

    public BlogHtmlBodyEvent(String m) {
        this.Html = m;
    }

    public String GetMessage() {
        return Html;
    }
}
