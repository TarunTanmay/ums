package org.example.ums.service;

import org.example.ums.dto.UserDetailsDTO;
import org.example.ums.exception.BadEntryException;
import org.example.ums.exception.HttpResponse;
import org.example.ums.model.User;
import org.example.ums.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    public UserDetailsDTO getUserDetails(User user){
        UserDetailsDTO userDetailsDTO = new UserDetailsDTO();
        userDetailsDTO.setUsername(user.getUsername());
        userDetailsDTO.setEmail(user.getEmail());
        userDetailsDTO.setPhone(user.getPhone());
        userDetailsDTO.setCode(user.getCode());
        return userDetailsDTO;
    }

    public HttpResponse<UserDetailsDTO> registerUser(User user) {
        if (userRepository.findByUsername(user.getUsername()).isPresent() ||
                (userRepository.findByEmail(user.getEmail()).isPresent())) {
            throw new BadEntryException("Username is already exists");

        }else if (user.getPassword().isEmpty()){
            throw new BadEntryException("Password is empty");

        }else if (user.getPassword().length() < 6){
            throw new BadEntryException("Password must be at least 6 characters");

        }else if (String.valueOf(user.getPhone()).length() != 10) {
            throw new BadEntryException("Phone number must be at least 10 characters");
        }
        user.setPassword(user.getPassword());
        userRepository.save(user);
        user.setCode("UM"+user.getId());
        userRepository.save(user);
        return new HttpResponse<>(HttpStatus.OK,getUserDetails(user));
    }

    public HttpResponse<UserDetailsDTO> loginUser(String userName, String email, String password) {
        if (!((userRepository.findByUsername(userName)).isPresent() || (userRepository.findByEmail(email)).isPresent())){
            throw new BadEntryException("User not found");
        }

        User user1;
        if (userRepository.findByUsername(userName).isPresent()) {
            user1 = userRepository.findByUsername(userName).get();

        }else if (userRepository.findByEmail(email).isPresent()) {
            user1 = userRepository.findByEmail(email).get();
        }else{
            throw new BadEntryException("Something went wrong");
        }

        if (!user1.getPassword().equals(password)) {
            throw new BadEntryException("Incorrect Password");
        }
        return new HttpResponse<>(HttpStatus.OK,getUserDetails(user1));
    }
}
