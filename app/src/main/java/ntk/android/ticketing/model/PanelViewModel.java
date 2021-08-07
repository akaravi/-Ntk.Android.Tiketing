package ntk.android.ticketing.model;

public class PanelViewModel {
    public int tag;
    public String Title;
    public int ImageId;
    public int badgeCount;

    public PanelViewModel setTitle(String title) {
        Title = title;
        return this;
    }

    public PanelViewModel setImageId(int imageId) {
        ImageId = imageId;
        return this;
    }

    public PanelViewModel setBadgeCount(int badgeCount) {
        this.badgeCount = badgeCount;
        return this;
    }

    public PanelViewModel setTag(int tag) {
        this.tag = tag;
        return this;
    }
}
