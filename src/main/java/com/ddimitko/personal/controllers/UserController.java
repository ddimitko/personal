package com.ddimitko.personal.controllers;

import com.ddimitko.personal.DTOs.UserDto;
import com.ddimitko.personal.models.User;
import com.ddimitko.personal.services.UserService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/user")
public class UserController {

    private final UserService userService;
    private final ModelMapper modelMapper;

    public UserController(final UserService userService, final ModelMapper modelMapper) {
        this.userService = userService;
        this.modelMapper = modelMapper;
    }

    private UserDto convertToDto(User user) {
        return modelMapper.map(user, UserDto.class);
    }

    /*private User convertToEntity(UserDto userDto) {
        return modelMapper.map(userDto, User.class);
    }*/

    @GetMapping("/all")
    public ResponseEntity<List<UserDto>> getAllUsers(){

        if(userService.findAllUsers().isEmpty()){
            return ResponseEntity.noContent().build();
        }
        else{
            List<User> users = userService.findAllUsers();
            List<UserDto> userDtoList = users.stream()
                    .map(this::convertToDto) // Use the convertToDto method
                    .collect(Collectors.toList());
            return ResponseEntity.ok(userDtoList);
        }
    }
    @GetMapping("/{userTag}")
    public ResponseEntity<UserDto> getUserByTag(@PathVariable String userTag) throws Exception {

        User user = new User();
        user.setUserTag(userTag);
        if(!userService.userExists(userTag)){
            return ResponseEntity.badRequest().build();
        }

        user = userService.findUserByUserTag(userTag);
        UserDto userDto = convertToDto(user);
        return ResponseEntity.ok(userDto);
    }

    @PutMapping("/{userTag}/edit")
    public ResponseEntity<UserDto> editUser(@PathVariable String userTag, @Valid @Email String email,
                                            @Valid @Size(max = 50) String fullName,
                                            @RequestParam("profilePic") MultipartFile file) throws Exception {
        User user = userService.findUserByUserTag(userTag);

        userService.updateUser(userTag, email, fullName, file);
        UserDto userCreated = convertToDto(user);
        return ResponseEntity.ok(userCreated);
    }

    @DeleteMapping("/delete")
    public ResponseEntity<User> deleteUser(@RequestBody User user){

        if(userService.userExists(user.getUserTag())){
            userService.deleteUserByUserTag(user.getUserTag());
            return ResponseEntity.accepted().build();
        }
        else{
            return ResponseEntity.badRequest().build();
        }
    }

}
