package com.onlineradiofm.trancemusicradio.db.entity;

import com.onlineradiofm.trancemusicradio.model.RadioModel;

import androidx.room.ColumnInfo;
import androidx.room.Entity;


@Entity(tableName = "radios")
public class RMRadioEntity extends RMAbstractEntity<RadioModel> {

    @ColumnInfo(name = "link")
    public String linkTrack;

    @ColumnInfo(name = "is_mp3")
    public int isMp3;

    public RMRadioEntity(String name, String linkTrack, int isMp3) {
        super(0, name);
        this.linkTrack = linkTrack;
        this.isMp3 = isMp3;
    }

    @Override
    public RadioModel createToRealModel() {
        RadioModel model = new RadioModel(id, name, null);
        model.setLinkRadio(linkTrack);
        model.setMyRadio(true);
        model.setSourceRadio(isMp3 > 0 ? "Other" : "Shoutcast");
        return model;
    }
}
