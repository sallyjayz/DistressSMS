package com.sallyjayz.distresssms.model;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "Distress_Contact")
public class DistressContact {

    @ColumnInfo(name = "Contact_Image")
    private String contactImage;

    @NonNull
    @ColumnInfo(name = "Contact_Name")
    private String contactName;

    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "Phone_Number")
    private String contactNumber;


    public DistressContact() {
    }

    public String getContactImage() {
        return contactImage;
    }

    public void setContactImage(String contactImage) {
        this.contactImage = contactImage;
    }

    public String getContactName() {
        return contactName;
    }

    public void setContactName(String contactName) {
        this.contactName = contactName;
    }

    public String getContactNumber() {
        return contactNumber;
    }

    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
    }

}
