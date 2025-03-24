package com.olegandreevich.tms.dto;

public class CommentDTO {
    private String content;

    public CommentDTO(String content) {
        this.content = content;
    }

    public CommentDTO() {

    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
