package com.gdevs.firetvappbygdevelopers.Utils;


import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.gdevs.firetvappbygdevelopers.Model.Channel;


@Database(entities={Channel.class},version = 1)
public abstract class FavoriteDatabase extends RoomDatabase {

    public abstract FavoriteDao favoriteDao();


}
