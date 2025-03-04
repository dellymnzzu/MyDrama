package com.MyDrama.constant;

public enum QuestionStatus {
    WAITING("답변대기"),
    COMPLETED("답변완료");
    
    private String description;
    
    QuestionStatus(String description) {
        this.description = description;
    }
    
    public String getDescription() {
        return description;
    }
} 