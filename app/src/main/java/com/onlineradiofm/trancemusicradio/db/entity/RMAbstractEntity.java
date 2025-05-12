package com.onlineradiofm.trancemusicradio.db.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public abstract class RMAbstractEntity<T> {

    @ColumnInfo(name = "id")
    @PrimaryKey(autoGenerate = true)
    public long id;

    @ColumnInfo(name = "name")
    public String name;

    public RMAbstractEntity(long id, String name) {
        this.id = id;
        this.name = name;
    }

    public abstract T createToRealModel();

}
