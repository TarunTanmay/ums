package org.example.ums.service;

import org.example.ums.dto.LoginDetailsDTO;
import org.example.ums.dto.UserDetailsDTO;
import org.example.ums.exception.BadEntryException;
import org.example.ums.exception.HttpResponse;
import org.example.ums.model.Roles;
import org.example.ums.model.User;
import org.example.ums.model.UserLogin;
import org.example.ums.repository.UserLoginRepository;
import org.example.ums.repository.UserRepository;
import org.example.ums.repository.UserRoleRepository;
import org.example.ums.utils.UUIDGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Collections;
import java.util.UUID;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserRoleRepository userRoleRepository;
    @Autowired
    private UserLoginRepository userLoginRepository;

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    @Transactional
    public HttpResponse<UserDetailsDTO> registerUser(User user, String type) {
        Roles defaultRole = userRoleRepository.findByName(type).orElseThrow(() -> new BadEntryException("Role not found"));
        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            throw new BadEntryException("email is already exists");

        }else if (user.getPassword().isEmpty()){
            throw new BadEntryException("Password is empty");

        }else if (user.getPassword().length() < 6){
            throw new BadEntryException("Password must be at least 6 characters");

        }else if (String.valueOf(user.getPhone()).length() != 10) {
            throw new BadEntryException("Phone number must be at least 10 characters");
        }

        user.setPassword(user.getPassword());
        user.setDeleted(false);
        user.setCreated_at(System.currentTimeMillis());
        user.setUpdated_at(System.currentTimeMillis());
        user.setRoles(Collections.singleton(defaultRole));
        User savedUser = userRepository.save(user);
        updateUserCode(savedUser.getId());
        return new HttpResponse<>(HttpStatus.OK, getUserDetails(savedUser));
    }

    public HttpResponse<LoginDetailsDTO> loginUser(String code, String email, String password) {
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

        return new HttpResponse<>(HttpStatus.OK, getLoginDetails(user1));
    }

    public HttpResponse<Roles> addRole(Roles role){
        if (userRoleRepository.findByName(role.getName()).isPresent()){
            throw new BadEntryException("Role already exists");
        }
        userRoleRepository.save(role);
        return new HttpResponse<>(HttpStatus.OK, role);
    }

    public UserDetailsDTO getUserDetails(User user){
        UserDetailsDTO userDetailsDTO = new UserDetailsDTO();
        userDetailsDTO.setEmail(user.getEmail());
        userDetailsDTO.setPhone(user.getPhone());
        userDetailsDTO.setCode(user.getCode());
        return userDetailsDTO;
    }

    public LoginDetailsDTO getLoginDetails(User user){
        LoginDetailsDTO loginDetailsDTO = new LoginDetailsDTO();
        if (userRepository.findByEmail(user.getEmail()).isPresent()){
            loginDetailsDTO.setName(user.getName());
            loginDetailsDTO.setEmail(user.getEmail());
            loginDetailsDTO.setPhone(user.getPhone());
            loginDetailsDTO.setCode(user.getCode());
            loginDetailsDTO.setToken(createUserToken(user.getEmail()));

            UserLogin userLogin = new UserLogin();
            userLogin.setToken(createUserToken(user.getEmail()).toString());
            userLogin.setDeleted(false);
            userLogin.setUser(user);
            userLogin.setExpiredAt(System.currentTimeMillis()+19800);
            userLogin.setCreatedAt(System.currentTimeMillis());
            userLogin.setUpdatedAt(System.currentTimeMillis());
            userLoginRepository.save(userLogin);

        }else{
            throw new BadEntryException("User not found");
        }
        return loginDetailsDTO;
    }

    public String generateUserCode(Long id) {
        return "UM" + id;
    }

    @Transactional
    public void updateUserCode(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new BadEntryException("User not found with id: " + id));
        String code = generateUserCode(id);
        user.setCode(code);
        userRepository.updateUserById(id, code);
    }

    public UUID createUserToken(String email) {
        return UUIDGenerator.generateUUID(email);
    }
}
