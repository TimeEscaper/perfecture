package ru.mail.tp.perfecture.storage;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.annotation.Unique;
import com.raizlabs.android.dbflow.structure.BaseModel;

@Table(database = AppDatabase.class)
public class PlaceModel extends BaseModel {

    @Column
    @PrimaryKey
    @Unique
    private long id;

    @Column
    private String title;

    @Column
    private String description;

    @Column
    private double latitude;

    @Column
    private double longitude;

    public PlaceModel() { super(); }

    public PlaceModel(long id, String title, String description, double latitude,
                      double longitude) {
        super();
        this.id = id;
        this.title = title;
        this.description = description;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) { this.latitude = latitude; }

    public double getLongitude() { return longitude; }

    public double setLongitude(double longitude) { return longitude; }
}
