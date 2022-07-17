package com.shopme.admin.user;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.shopme.common.entity.User;

@Controller
public class UserController {

	
	@Autowired
	private UserService userService;
	
	
	
	@GetMapping("/users")
	public String listAll(Model model) {
		
		
		List<User> listUser = userService.listAll();
		model.addAttribute("listUsers",listUser );
		
		return "users";
	}
	
	
	
	
	//New user route
	@GetMapping("/users/new")
	public String  newUser() {
		return "user_form";
	}
	
}

