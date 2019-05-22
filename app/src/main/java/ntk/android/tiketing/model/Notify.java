package ntk.android.tiketing.model;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;

@Entity(tableName = "Notification")
public class Notify {

    @PrimaryKey(autoGenerate = true)
    public int ID;

    @SerializedName("Title")
    public String Title;

    @SerializedName("Content")
    public String Content;

    @SerializedName("ContentType")
    public int ContentType;

    @SerializedName("ContentJson")
    public String ContentJson;

    @SerializedName("ContentJsonClass")
    public String ContentJsonClass;

    @SerializedName("SmallImageSrc")
    public String SmallImageSrc;

    @SerializedName("BigImageSrc")
    public String BigImageSrc;

    public int IsRead;
}
