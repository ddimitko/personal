package com.ddimitko.personal.services;

import com.ddimitko.personal.DTOs.SignupDto;
import com.ddimitko.personal.models.User;
import com.ddimitko.personal.repositories.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final PictureService pictureService;
    private final BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();

    public UserService(final UserRepository userRepository, PictureService pictureService) {
        this.userRepository = userRepository;
        this.pictureService = pictureService;
    }


    public User addUser(SignupDto signupDto) throws Exception {
        if (userExists(signupDto.getUserTag())) {
            throw new RuntimeException("User already exists");
        }

        User user = new User();
        user.setUserTag(signupDto.getUserTag());
        user.setPassword(bCryptPasswordEncoder.encode(signupDto.getPassword()));
        user.setEmail(signupDto.getEmail());

        userRepository.save(user);
        return user;
    }

    public User updateUser(String userTag, String email, String fullName, MultipartFile file) throws Exception {
        User user = findUserByUserTag(userTag);
        user.setFullName(fullName);

        if(email != null && !email.isEmpty()){
            user.setEmail(email);
        }

        if(file != null && !file.isEmpty()){
            pictureService.uploadProfilePicture(user, file);
        }

        return userRepository.save(user);
    }

    public List<User> findAllUsers() {
        return userRepository.findAll();
    }

    public User findUserByUserTag(String userTag) throws Exception {
        if(userExists(userTag)){
            return userRepository.findUserByUserTag(userTag).get();
        }
        else{
            throw new Exception("User does not exist");
        }
    }

    public Boolean userExists(String userTag) {
        if(userRepository.findUserByUserTag(userTag).isPresent()){
            return true;
        }
        else{
            return false;
        }
    }

    public void deleteUserByUserTag(String userTag) {
        if(userRepository.findUserByUserTag(userTag).isPresent()){
            User user = userRepository.findUserByUserTag(userTag).get();
            userRepository.delete(user);
        }
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findUserByUserTag(username).orElseThrow(() ->
                new UsernameNotFoundException("User with usertag does not exist"));

        return new org.springframework.security.core.userdetails.User(username, user.getPassword(), new ArrayList<>());
    }
}
