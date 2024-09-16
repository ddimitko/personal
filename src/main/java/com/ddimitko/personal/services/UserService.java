package com.ddimitko.personal.services;

import com.ddimitko.personal.DTOs.SignupDto;
import com.ddimitko.personal.models.User;
import com.ddimitko.personal.repositories.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();

    public UserService(final UserRepository userRepository) {
        this.userRepository = userRepository;
    }


    public User addUser(SignupDto signupDto) throws Exception {
        if(userRepository.findUserByUserTag(signupDto.getUserTag()).isEmpty()){
            User user = new User();
            user.setUserTag(signupDto.getUserTag());
            user.setEmail(signupDto.getEmail());
            user.setPassword(bCryptPasswordEncoder.encode(signupDto.getPassword()));
            return userRepository.save(user);
        }
        else{
            throw new Exception("User already exists");
        }
    }

    public User updateUser(String userTag, String email, String fullName) throws Exception {
        User user = findUserByUserTag(userTag);
        user.setFullName(fullName);

        if(email != null && !email.isEmpty()){
            user.setEmail(email);
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

}
