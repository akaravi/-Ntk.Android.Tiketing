package ntk.android.ticketing.service;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Objects;

import ntk.android.ticketing.model.Notify;
import ntk.android.ticketing.room.RoomDb;
import ntk.android.ticketing.utill.PugPush;

/**
 * Created by Mehrdad Safari on 18-Jan-17.
 */

public class SeFirebase extends FirebaseMessagingService {

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        Notify notification = new Notify();
        notification.Title = remoteMessage.getData().get("Title");
        if (remoteMessage.getData().get("ContentType") != null) {
            notification.ContentType = Integer.parseInt(Objects.requireNonNull(remoteMessage.getData().get("ContentType")));
        }
        notification.Content = remoteMessage.getData().get("Content");

        if (remoteMessage.getData().get("BigImageSrc")!=null) {
            notification.BigImageSrc = remoteMessage.getData().get("BigImageSrc");
        }else {
        }
        notification.IsRead = 0;
        RoomDb.getRoomDb(getApplicationContext()).NotificationDoa().Insert(notification);
        RoomDb.DestroyInstance();
        PugPush.ShowNotification(getApplicationContext(), notification);
    }
}
