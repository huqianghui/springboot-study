package com.hu.study.chapter43.resobject;

public class Greeting {

    private long id;
    private String content;

    public Greeting() {
        this.id = -1;
        this.content = "";
    }

    public void setId(long id){
        this.id = id;
    }

    public void setContent(String content){
        this.content = content;
    }

    public Greeting(long id, String content) {
        this.id = id;
        this.content = content;
    }

    public long getId() {
        return id;
    }

    public String getContent() {
        return content;
    }
}
