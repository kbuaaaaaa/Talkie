package com.example.talkie.Model;

public class Chat {


    private String sender;
    private String receiver;
    private String message;
    private String seen;

    public Chat(String sender, String receiver, String message,String seen) {
        this.sender = sender;
        this.receiver = receiver;
        this.message = message;
        this.seen = seen;
    }

    public Chat(){
    }

    public String getSender() {
        return sender;
    }

    public String getReceiver() {
        return receiver;
    }

    public String getMessage() {
        return message;
    }

    public String getSeen() {
        return seen;
    }

    public void setSeen(String seen) {
        this.seen = seen;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
