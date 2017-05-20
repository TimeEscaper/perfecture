package ru.mail.tp.perfecture.storage;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.ForeignKey;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;

@SuppressWarnings("unused")
@Table(database = AppDatabase.class)
public class PhotoLinkModel extends BaseModel {

    @Column
    @PrimaryKey
    String url;

    @Column
    @ForeignKey(tableClass = PlaceModel.class)
    long placeId;

    public PhotoLinkModel() { super(); };

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
