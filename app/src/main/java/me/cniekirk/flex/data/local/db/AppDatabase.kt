package me.cniekirk.flex.data.local.db

import androidx.room.Database
import androidx.room.RoomDatabase
import me.cniekirk.flex.data.local.db.dao.PreLoginUserDao
import me.cniekirk.flex.data.local.db.dao.UserDao
import me.cniekirk.flex.data.local.db.entity.PreLoginUser
import me.cniekirk.flex.data.local.db.entity.User

@Database(entities = [User::class, PreLoginUser::class], version = 2)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun preLoginUserDao(): PreLoginUserDao
}