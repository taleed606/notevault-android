package com.example.project_1220006_1220204;

public class User {

    private String mEmail;
    private String mFirstName;
    private String mLastName;
    private String mPassword;

    public User() {
    }

    public User(String mEmail, String mFirstName, String mLastName, String mPassword) {
        this.mEmail = mEmail;
        this.mFirstName = mFirstName;
        this.mLastName = mLastName;
        this.mPassword = mPassword;
    }

    public String getmEmail() {
        return mEmail;
    }

    public void setmEmail(String mEmail) {
        this.mEmail = mEmail;
    }

    public String getmFirstName() {
        return mFirstName;
    }

    public void setmFirstName(String mFirstName) {
        this.mFirstName = mFirstName;
    }

    public String getmLastName() {
        return mLastName;
    }

    public void setmLastName(String mLastName) {
        this.mLastName = mLastName;
    }

    public String getmPassword() {
        return mPassword;
    }

    public void setmPassword(String mPassword) {
        this.mPassword = mPassword;
    }

    @Override
    public String toString() {
        return "User{" +
                "\nmEmail='" + mEmail + '\'' +
                "\n, mFirstName='" + mFirstName + '\'' +
                "\n, mLastName='" + mLastName + '\'' +
                "\n}\n\n";
    }
}