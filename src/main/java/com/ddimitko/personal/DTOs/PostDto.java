package com.ddimitko.personal.DTOs;

import lombok.Data;

import java.text.SimpleDateFormat;
import java.util.List;

@Data
public class PostDto {

    private static final SimpleDateFormat dateFormat
            = new SimpleDateFormat("dd-MM-yyyy HH:mm");

    private String content;
    private String createdAt;

    private String userTag;
    private List<CommentDto> comments;

}