package com.onlineradiofm.trancemusicradio.db.dao;

import java.util.ArrayList;
import java.util.List;

import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Transaction;
import androidx.room.Update;

public abstract class RMAbstractDAO<T> {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    public  abstract long insert(T model);

    /**
     * Insert an array of objects in the database.
     * @param listModels the objects to be inserted.
     * @return The SQLite row ids
     */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    public abstract List<Long> insert(List<T> listModels);

    /**
     * Update an object from the database.
     * @param model the object to be updated
     */
    @Update
    public abstract void update(T model);

    /**
     * Update an array of objects from the database.
     * @param list the object to be updated
     */
    @Update
    public abstract void update(List<T> list);


    /**
     * Delete an object from the database
     * @param model the object to be deleted
     */
    @Delete
    public abstract void delete(T model);

    @Transaction
    public void updateOrInsert(T model) {
        long id = insert(model);
        if (id == -1) {
            update(model);
        }
    }

    @Transaction
    public void updateOrInsert(List<T> listModel) {
        List<Long> insertResult = insert(listModel);
        List<T> updateList = new ArrayList<>();
        int size=insertResult.size();
        for (int i = 0; i < size; i++) {
            if (insertResult.get(i) == -1) {
                updateList.add(listModel.get(i));
            }
        }
        if (!updateList.isEmpty()) {
            update(updateList);
        }
    }
}
