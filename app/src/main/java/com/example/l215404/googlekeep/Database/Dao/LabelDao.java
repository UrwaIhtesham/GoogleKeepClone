package com.example.l215404.googlekeep.Database.Dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.l215404.googlekeep.Database.models.Label;
import com.example.l215404.googlekeep.Database.models.User;

@Dao
public interface LabelDao {
    @Insert
    void insertLabel(Label label);

    @Query("SELECT * FROM label WHERE labelName = :labelName")
    Label getLabel(String labelName);

    @Query("SELECT labelId FROM label WHERE labelName = :labelName")
    int getLabelID(String labelName);

    @Query("SELECT labelName FROM label WHERE labelId = :labelId")
    String getLabelName(int labelId);

    @Query("SELECT * FROM label WHERE labelId = :labelId")
    User getLabelByID(int labelId);
}
