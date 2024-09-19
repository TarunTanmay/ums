package org.example.ums.controller;
import org.example.ums.dto.UserDetailsDTO;
import org.example.ums.model.Roles;
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

    @PostMapping("user/signup")
    public ResponseEntity<UserDetailsDTO> registerUser(@RequestBody User user) {
        return userService.registerUser(user, "USER").responseEntity();
    }

    @PostMapping("admin/signup")
    public ResponseEntity<UserDetailsDTO> registerAdmin(@RequestBody User user) {
        return userService.registerUser(user, "ADMIN").responseEntity();
    }

    @PostMapping("user/login")
    public ResponseEntity<UserDetailsDTO> loginUser(@RequestBody User user) {
        return userService.loginUser(user.getCode(), user.getEmail(), user.getPassword()).responseEntity();
    }

    @PostMapping("/add/roles")
    public ResponseEntity<Roles> addRoles(@RequestBody Roles roles) {
        return userService.addRole(roles).responseEntity();
    }
}
