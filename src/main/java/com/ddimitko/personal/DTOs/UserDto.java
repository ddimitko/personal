package com.ddimitko.personal.DTOs;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

@Data
public class UserDto {

    private String userTag;

    @Size(max = 50)
    private String fullName;

    private String profilePicture;

    @Email
    private String email;

    private List<GroupDto> groupList;
    private List<PostDto> postList;

}
