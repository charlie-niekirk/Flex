package me.cniekirk.flex.data.local.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import me.cniekirk.flex.data.local.db.entity.PreLoginUser

@Dao
interface PreLoginUserDao {

    @Query("SELECT * FROM preloginuser")
    fun getAll(): List<PreLoginUser>

    @Insert
    fun insert(user: PreLoginUser)

    @Delete
    fun delete(user: PreLoginUser)

}