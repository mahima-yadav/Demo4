package com.youtube.jwt.controller;

import com.youtube.jwt.dao.UserDao;
import com.youtube.jwt.entity.User;
import com.youtube.jwt.service.UserService;

import exception.ResourceNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

@RestController
public class UserController {
	
	@Autowired
	private UserDao userDao;

    @Autowired
    private UserService userService;
    

    @PostConstruct
    public void initRoleAndUser() {
        userService.initRoleAndUser();
    }
    
    @GetMapping("/users")
	public List<User> getAllUsers()
	{
		return userService.getAllUsers();
	}

    @PostMapping({"/registerNewUser"})
    public User registerNewUser(@RequestBody User user) {
        return userService.registerNewUser(user);
    }
    
    @PutMapping("/update/{id}")
    @PreAuthorize("hasRole('Admin')")
	public String updateUser(@PathVariable String id, @RequestBody User user)
	{
		userService.updateUser(id, user);
		return "User record updated successfully. \nuserName : "+user.getUserName()+"\nuserPassword : "+userService.getEncodedPassword(user.getUserPassword())+"\nuserFirstName : "+user.getUserFirstName()+"\nuserLastName : "+user.getUserLastName();
	}
    
    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasRole('Admin')")
	public ResponseEntity<Map<String, Boolean>> deleteEmployee(@PathVariable String id){
		User user = userDao.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("User does not exist with id :" + id));
		
		userDao.delete(user);
		Map<String, Boolean> response = new HashMap<>();
		response.put("deleted", Boolean.TRUE);
		return ResponseEntity.ok(response);
	}
    
    @GetMapping({"/forAdmin"})
    @PreAuthorize("hasRole('Admin')")
    public String forAdmin(){
        return "This URL is only accessible to the admin";
    }

    @GetMapping({"/forUser"})
    @PreAuthorize("hasRole('User')")
    public String forUser(){
        return "This URL is only accessible to the user";
    }
}
