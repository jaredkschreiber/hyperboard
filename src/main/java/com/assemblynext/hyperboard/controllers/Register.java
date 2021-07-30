package com.assemblynext.hyperboard.controllers;

import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

import com.assemblynext.hyperboard.entities.User;
import com.assemblynext.hyperboard.repositories.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class Register {

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/register")
	public String register(Model model,HttpServletRequest request){
		CsrfToken csrfToken = (CsrfToken) request.getAttribute(CsrfToken.class.getName());
		if (csrfToken != null) {
			model.addAttribute("csrf", csrfToken.getToken());
			model.addAttribute("csrfp", csrfToken.getParameterName());
		}
		return "register";
	}

	@PostMapping("/register")
	public String registerAccount(@RequestParam(name = "username") String username
                                ,@RequestParam(name = "password") String password
                                ,@RequestParam(name = "password2") String password2){
		//check if passwords match
		if (password.equals(password2)){

			//check if username is unique
			Optional<User> dup = userRepository.findUserByUsername(username);
			
			if (dup.isPresent()){
				return "registerdup";
			} else {
				User newUser = new User();

				newUser.setUsername(username);
		
				BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
				String encodedPassword = encoder.encode(password);
				newUser.setPassword(encodedPassword);	
		
				userRepository.save(newUser);
				//return to login screen
				return "redirect:/auth";
			}
		} else {
			return "registernonmatch";
		}
	}
}

