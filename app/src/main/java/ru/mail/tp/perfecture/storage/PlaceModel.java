package ru.mail.tp.perfecture.storage;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;

/**
 * Created by sibirsky on 14.04.17.
 */

@Table(database = Configuration.activeDb)
public class PlaceModel extends BaseModel {

    @PrimaryKey
    private long id;

    @Column
    private String title;

    @Column
    private String location;

    @Column
    private String description;

    public PlaceModel(long id, String title, String location, String description) {
        super();
        this.id = id;
        this.title = title;
        this.location = location;
        this.description = description;
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

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
