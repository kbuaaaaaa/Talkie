package com.example.talkie.Model;

import java.util.ArrayList;

public class Users {
    private  String id;
    private  String username;
    private String imageURL;
    private String status;
    private ArrayList<Users> friends;
    private ArrayList<Users> requests;

    public Users(){
    }

    public Users(String id, String username, String imageURL,String status, ArrayList<Users> friend , ArrayList<Users> request) {
        this.id = id;
        this.username = username;
        this.imageURL = imageURL;
        this.status = status;
        this.friends = friend;
        this.requests = request;
    }

    public String getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getImageURL() {
        return imageURL;
    }

    public String getStatus() {
        return status;
    }

    public ArrayList<Users> getFriends() {
        return friends;
    }

    public void setFriends(ArrayList<Users> friends) {
        this.friends = friends;
    }

    public ArrayList<Users> getRequests() {
        return requests;
    }

    public void setRequests(ArrayList<Users> requests) {
        this.requests = requests;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }


}
