package me.cniekirk.flex.data.local.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import me.cniekirk.flex.data.local.db.entity.User

@Dao
interface UserDao {

    @Query("SELECT * FROM user")
    fun getAll(): List<User>

    @Insert
    fun insert(user: User)

    @Delete
    fun delete(user: User)

}