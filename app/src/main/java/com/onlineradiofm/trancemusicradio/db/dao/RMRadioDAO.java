package com.onlineradiofm.trancemusicradio.db.dao;

import com.onlineradiofm.trancemusicradio.db.entity.RMRadioEntity;

import java.util.List;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

@Dao
public interface RMRadioDAO {

    @Query("SELECT * from radios order by id DESC")
    List<RMRadioEntity> getAll();

    @Query("SELECT * from radios where id= :id limit 1")
    List<RMRadioEntity> getRadio(long id);

    @Insert
    long insert(RMRadioEntity radio);

    @Query("DELETE from radios where id= :radioId")
    void delete(long radioId);

    @Update
    int update(RMRadioEntity radio);

}
