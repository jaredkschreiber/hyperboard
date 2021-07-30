package com.assemblynext.hyperboard.controllers;

import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;


@Controller
@RequestMapping("/auth")
public class SecurityController {

    @RequestMapping("")
	public String authPage(Model model
	,@RequestParam(name = "error", required = false) Optional<String> error,HttpServletRequest request){

		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if(authentication == null || authentication instanceof AnonymousAuthenticationToken) {
			if (error.isPresent()){
				model.addAttribute("error",true);
			}
			CsrfToken csrfToken = (CsrfToken) request.getAttribute(CsrfToken.class.getName());
			if (csrfToken != null) {
				model.addAttribute("csrf", csrfToken.getToken());
				model.addAttribute("csrfp", csrfToken.getParameterName());
			}
			return "auth";
		} else {
			String userName = authentication.getName();
			var userAuthorities = authentication.getAuthorities();
			model.addAttribute("username",userName);
			model.addAttribute("authorities",userAuthorities);
			Boolean mod = false;
			Boolean admin = false;
			for (var role : userAuthorities){
				if (role.getAuthority().equals("ADMIN")){
					admin = true;
				}
				if (role.getAuthority().equals("MOD")){
					mod = true;
				}
			}
			model.addAttribute("mod", mod);	
			model.addAttribute("admin", admin);		
			return "logout";
		}
	}
}
