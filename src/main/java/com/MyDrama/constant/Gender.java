package com.MyDrama.constant;

public enum Gender {
    MEN("남자"),
    WOMEN("여자");
    
    private String description;
    
    Gender(String description) {
        this.description = description;
    }
    
    public String getDescription() {
        return description;
    }
}
