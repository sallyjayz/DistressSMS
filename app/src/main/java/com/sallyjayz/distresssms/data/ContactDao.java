package com.sallyjayz.distresssms.data;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.sallyjayz.distresssms.model.DistressContact;

import java.util.List;

@Dao
public interface ContactDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insert(DistressContact distressContact);

    @Query("SELECT * from Distress_Contact ORDER BY Contact_Name ASC")
    LiveData<List<DistressContact>> getAllContacts();

    @Query("SELECT COUNT(*) from Distress_Contact")
    LiveData<Integer> getAllCount();

    @Query("SELECT Phone_Number from Distress_Contact")
    LiveData<List<String>> getAllPhoneNumbers();

    @Query("DELETE FROM Distress_Contact")
    void deleteAll();

    @Delete
    void deleteContact(DistressContact distressContact);
}
