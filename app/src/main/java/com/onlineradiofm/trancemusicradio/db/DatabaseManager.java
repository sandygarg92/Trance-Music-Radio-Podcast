package com.onlineradiofm.trancemusicradio.db;

import android.content.Context;

import com.onlineradiofm.trancemusicradio.R;
import com.onlineradiofm.trancemusicradio.db.entity.RMRadioEntity;
import com.onlineradiofm.trancemusicradio.model.RadioModel;
import com.onlineradiofm.trancemusicradio.ypylibs.model.ResultModel;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.room.Room;


public class DatabaseManager {

    private static final String DATABASE_NAME = "radios";

    private static DatabaseManager ourInstance;
    private AppDatabase mAppDatabase;

    public synchronized static DatabaseManager getInstance(Context mContext) {
        if (ourInstance == null) {
            ourInstance = new DatabaseManager(mContext);
        }
        return ourInstance;
    }

    private DatabaseManager(Context mContext) {
        this.mAppDatabase = Room.databaseBuilder(mContext, AppDatabase.class, DATABASE_NAME).build();
    }

    public void onDestroy() {
        try {
            if (mAppDatabase != null) {
                mAppDatabase.close();
                mAppDatabase = null;
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        ourInstance = null;
    }

    public ResultModel<RadioModel> getRadioWithId(@NonNull Context context, long id) {
        ResultModel<RadioModel> result = new ResultModel<>(ResultModel.STATUS_OK);
        try {
            if (mAppDatabase != null) {
                List<RMRadioEntity> listEntities = mAppDatabase.radioDAO().getRadio(id);
                if (listEntities != null && listEntities.size() > 0) {
                    ArrayList<RadioModel> listRadios = new ArrayList<>();
                    for (RMRadioEntity entity : listEntities) {
                        RadioModel radio = entity.createToRealModel();
                        radio.setTags(context.getString(R.string.title_my_radio));
                        listRadios.add(radio);
                    }
                    result.setListModels(listRadios);
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public ResultModel<RadioModel> getAllRadios(@NonNull Context context) {
        ResultModel<RadioModel> result = new ResultModel<>(ResultModel.STATUS_OK);
        try {
            if (mAppDatabase != null) {
                List<RMRadioEntity> listEntities = mAppDatabase.radioDAO().getAll();
                if (listEntities != null && listEntities.size() > 0) {
                    ArrayList<RadioModel> listRadios = new ArrayList<>();
                    for (RMRadioEntity entity : listEntities) {
                        RadioModel radio = entity.createToRealModel();
                        radio.setTags(context.getString(R.string.title_my_radio));
                        listRadios.add(radio);
                    }
                    result.setListModels(listRadios);
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public long updateRadio(@NonNull RMRadioEntity radio) {
        try {
            if (mAppDatabase != null) {
                return mAppDatabase.radioDAO().update(radio);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return -1L;
    }

    public long insertRadio(@NonNull RMRadioEntity model) {
        try {
            if (mAppDatabase != null) {
                return mAppDatabase.radioDAO().insert(model);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }

    public void deleteRadio(long radioId) {
        try {
            if (mAppDatabase != null) {
                mAppDatabase.radioDAO().delete(radioId);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}
