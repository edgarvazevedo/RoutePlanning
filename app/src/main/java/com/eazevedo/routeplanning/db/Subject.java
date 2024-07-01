package com.eazevedo.routeplanning.db;

import android.os.Parcel;
import android.os.Parcelable;

public class Subject implements Parcelable {

    private String uid;
    private String title;
    private String description;

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

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.uid);
        dest.writeString(this.title);
        dest.writeString(this.description);
    }

    public Subject() {
    }

    protected Subject(Parcel in) {
        this.uid = in.readString();
        this.title = in.readString();
        this.description = in.readString();
    }

    public static final Creator<Subject> CREATOR = new Creator<Subject>() {
        @Override
        public Subject createFromParcel(Parcel source) {
            return new Subject(source);
        }

        @Override
        public Subject[] newArray(int size) {
            return new Subject[size];
        }
    };

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Subject note = (Subject) o;

        if (uid != null ? !uid.equals(note.uid) : note.uid != null) return false;
        if (title != null ? !title.equals(note.title) : note.title != null) return false;
        return description != null ? description.equals(note.description) : note.description == null;

    }

    @Override
    public int hashCode() {
        int result = uid != null ? uid.hashCode() : 0;
        result = 31 * result + (title != null ? title.hashCode() : 0);
        result = 31 * result + (description != null ? description.hashCode() : 0);
        return result;
    }
}
