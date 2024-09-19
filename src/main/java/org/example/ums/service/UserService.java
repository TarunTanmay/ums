package org.example.ums.service;

import org.example.ums.dto.UserDetailsDTO;
import org.example.ums.exception.BadEntryException;
import org.example.ums.exception.HttpResponse;
import org.example.ums.model.Roles;
import org.example.ums.model.User;
import org.example.ums.repository.UserRepository;
import org.example.ums.repository.UserRoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserRoleRepository userRoleRepository;

    public UserDetailsDTO getUserDetails(User user){
        UserDetailsDTO userDetailsDTO = new UserDetailsDTO();
        userDetailsDTO.setEmail(user.getEmail());
        userDetailsDTO.setPhone(user.getPhone());
        userDetailsDTO.setCode(user.getCode());
        return userDetailsDTO;
    }

    public HttpResponse<UserDetailsDTO> registerUser(User user, String type) {
        if (userRepository.findByCode(user.getCode()).isPresent() ||
                (userRepository.findByEmail(user.getEmail()).isPresent())) {
            throw new BadEntryException("Username is already exists");

        }else if (user.getPassword().isEmpty()){
            throw new BadEntryException("Password is empty");

        }else if (user.getPassword().length() < 6){
            throw new BadEntryException("Password must be at least 6 characters");

        }else if (String.valueOf(user.getPhone()).length() != 10) {
            throw new BadEntryException("Phone number must be at least 10 characters");
        }

        Roles defaultRole = userRoleRepository.findByName(type).orElseThrow(() -> new BadEntryException("Role not found"));
        user.setRoles(Collections.singleton(defaultRole));
        user.setPassword(user.getPassword());
        user.setCode("UM"+System.currentTimeMillis());
        user.setDeleted(false);
        user.setCreated_at(System.currentTimeMillis());
        user.setUpdated_at(System.currentTimeMillis());
        userRepository.save(user);
        return new HttpResponse<>(HttpStatus.OK, getUserDetails(user));
    }

    public HttpResponse<UserDetailsDTO> loginUser(String code, String email, String password) {
        if (!((userRepository.findByCode(code)).isPresent() || (userRepository.findByEmail(email)).isPresent())){
            throw new BadEntryException("User not found");
        }

        User user1;
        if (userRepository.findByCode(code).isPresent()) {
            user1 = userRepository.findByCode(code).get();

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

    public HttpResponse<Roles> addRole(Roles role){
        if (userRoleRepository.findByName(role.getName()).isPresent()){
            throw new BadEntryException("Role already exists");
        }
        userRoleRepository.save(role);
        return new HttpResponse<>(HttpStatus.OK, role);
    }
}
