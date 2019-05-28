package ntk.android.ticketing.room;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;
import android.arch.persistence.room.Delete;
import java.util.List;

import ntk.android.ticketing.model.Notify;

@Dao
public interface NotificationDoa {

    @Query("SELECT * FROM Notification ORDER BY ID DESC")
    List<Notify> All();

    @Query("SELECT * FROM Notification WHERE IsRead == 0 ORDER BY ID DESC")
    List<Notify> AllUnRead();

    @Insert
    void Insert(Notify notify);

    @Update
    void Update(Notify notify);

    @Delete
    void Delete(Notify notify);

}
