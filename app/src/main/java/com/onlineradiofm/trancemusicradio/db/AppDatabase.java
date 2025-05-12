package com.onlineradiofm.trancemusicradio.db;

import com.onlineradiofm.trancemusicradio.db.dao.RMRadioDAO;
import com.onlineradiofm.trancemusicradio.db.entity.RMRadioEntity;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {RMRadioEntity.class}, version = 1)

public abstract class AppDatabase extends RoomDatabase {
    public abstract RMRadioDAO radioDAO();
}
