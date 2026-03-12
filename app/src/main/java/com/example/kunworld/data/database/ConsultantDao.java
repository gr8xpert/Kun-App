package com.example.kunworld.data.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.kunworld.data.models.Consultant;

import java.util.List;

@Dao
public interface ConsultantDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Consultant consultant);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<Consultant> consultants);

    @Update
    void update(Consultant consultant);

    @Delete
    void delete(Consultant consultant);

    @Query("SELECT * FROM consultants ORDER BY rating DESC")
    LiveData<List<Consultant>> getAllConsultants();

    @Query("SELECT * FROM consultants WHERE available = 1 ORDER BY rating DESC")
    LiveData<List<Consultant>> getAvailableConsultants();

    @Query("SELECT * FROM consultants WHERE specialty = :specialty ORDER BY rating DESC")
    LiveData<List<Consultant>> getConsultantsBySpecialty(String specialty);

    @Query("SELECT * FROM consultants WHERE id = :consultantId")
    LiveData<Consultant> getConsultantById(String consultantId);

    @Query("SELECT * FROM consultants WHERE name LIKE '%' || :query || '%' OR specialty LIKE '%' || :query || '%'")
    LiveData<List<Consultant>> searchConsultants(String query);

    @Query("SELECT * FROM consultants ORDER BY rating DESC LIMIT 5")
    LiveData<List<Consultant>> getPopularConsultants();

    @Query("DELETE FROM consultants")
    void deleteAll();
}
