package ru.mail.tp.perfecture.storage;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.ForeignKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;

/**
 * Created by sibirsky on 14.04.17.
 */

@Table(database = Configuration.activeDb)
public class PhotoLinkModel extends BaseModel {

    @Column
    String url;

    @Column
    @ForeignKey(tableClass = PlaceModel.class)
    long placeId;

    public PhotoLinkModel(String url, long placeId) {
        super();
        this.url = url;
        this.placeId = placeId;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public long getPlaceId() {
        return placeId;
    }

    public void setPlaceId(long placeId) {
        this.placeId = placeId;
    }
}
