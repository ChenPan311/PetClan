package com.example.android2project.model;

import com.google.firebase.firestore.GeoPoint;

import java.io.Serializable;

public class User implements Comparable<User>, Serializable {
    private String mEmail;
    private String mFirstName;
    private String mLastName;
    private String mPhotoUri;
    private GeoPoint mGeoPoint;
    private String mToken;

    public User() {}

    public User(String email, String firstName, String lastName, String photoUri, String token) {
        this.mEmail = email;
        this.mFirstName = firstName;
        this.mLastName = lastName;
        this.mPhotoUri = photoUri;
        this.mToken = token;
    }

    public String getEmail() {
        return mEmail;
    }

    public void setEmail(String email) {
        mEmail = email;
    }

    public String getFirstName() {
        return mFirstName;
    }

    public void setFirstName(String firstName) {
        this.mFirstName = firstName;
    }

    public String getLastName() {
        return mLastName;
    }

    public void setLastName(String lastName) {
        this.mLastName = lastName;
    }

    public String getPhotoUri() {
        return mPhotoUri;
    }

    public void setPhotoUri(String photoUri) {
        this.mPhotoUri = photoUri;
    }

    public String getToken() {
        return mToken;
    }

    public void setToken(String token) {
        this.mToken = token;
    }

    public GeoPoint getGeoPoint() {
        return mGeoPoint;
    }

    public void setGeoPoint(GeoPoint geoPoint) {
        this.mGeoPoint = geoPoint;
    }

    @Override
    public int compareTo(User otherUser) {
        if (otherUser != null) {
            if (this.getFirstName().compareTo(otherUser.mFirstName) == 0) {
                return (this.getLastName().compareTo(otherUser.mLastName));
            } else {
                return (this.getFirstName().compareTo(otherUser.mFirstName));
            }
        }
        return 0;
    }

    @Override
    public String toString() {
        return "User{" +
                "mEmail='" + mEmail + '\'' +
                ", mFirstName='" + mFirstName + '\'' +
                ", mLastName='" + mLastName + '\'' +
                ", mPhotoUri='" + mPhotoUri + '\'' +
                ", mToken='" + mToken + '\'' +
                '}';
    }
}
