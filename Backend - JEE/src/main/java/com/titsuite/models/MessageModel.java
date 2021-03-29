package com.titsuite.models;

public class MessageModel {

    private String title;
    private String body;

    public MessageModel() {}

    public MessageModel(String title, String body) {
        setTitle(title);
        setBody(body);
    }

    public String getTitle() { return this.title; }

    public void setTitle(String title) { this.title = title; }

    public String getBody() { return this.body; }

    public void setBody(String body) { this.body = body; }

}
