package ntk.android.ticketing.room;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import androidx.room.Delete;
import java.util.List;

import ntk.android.ticketing.model.NotificationModel;

@Dao
public interface NotificationDoa {

    @Query("SELECT * FROM NotificationModel ORDER BY ID DESC")
    List<NotificationModel> All();

    @Query("SELECT * FROM NotificationModel WHERE IsRead == 0 ORDER BY ID DESC")
    List<NotificationModel> AllUnRead();

    @Insert
    void Insert(NotificationModel notificationModel);

    @Update
    void Update(NotificationModel notificationModel);

    @Delete
    void Delete(NotificationModel notificationModel);

}
