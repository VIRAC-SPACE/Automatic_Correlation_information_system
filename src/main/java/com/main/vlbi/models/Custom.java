package com.main.vlbi.models;



public class Custom {
    private String text;

    public Custom() {
       this.text = "";
    }

    public void setText(String text){
        this.text = text;
    }

    public String getText(){
        return  this.text;
    }

}