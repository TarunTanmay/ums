package org.example.ums.controller;
import org.example.ums.dto.UserDetailsDTO;
import org.example.ums.model.User;
import org.example.ums.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
public class UserController {
    @Autowired
    private UserService userService;

    @PostMapping("/signup")
    public ResponseEntity<UserDetailsDTO> registerUser(@RequestBody User user) {
        return userService.registerUser(user).responseEntity();
    }

    @PostMapping("/login")
    public ResponseEntity<UserDetailsDTO> loginUser(@RequestBody User user) {
        return userService.loginUser(user.getUsername(), user.getEmail(), user.getPassword()).responseEntity();
    }
}
