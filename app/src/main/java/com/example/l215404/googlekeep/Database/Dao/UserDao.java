package com.example.l215404.googlekeep.Database.Dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.l215404.googlekeep.Database.models.User;

@Dao
public interface UserDao {
    @Insert
    void insertUser(User user);

    @Query("SELECT * FROM users WHERE email = :email")
    User getUserByEmail(String email);

    @Query("SELECT * FROM users WHERE email = :email AND password = :password")
    User getUser(String email, String password);

    @Query("SELECT userId FROM users WHERE email = :email")
    int getUserID(String email);

    @Query("SELECT email FROM users WHERE userId = :userId")
    String getUserEmail(int userId);

    @Query("SELECT * FROM users WHERE userId = :userId")
    User getUserByID(int userId);

    @Query("SELECT * FROM users WHERE email = :email")
    User checkIFEmailExists(String email);
}
