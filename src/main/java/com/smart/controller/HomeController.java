package com.smart.controller;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.smart.dao.UserRepository;
import com.smart.entities.User;
import com.smart.helper.Message;

@Controller
public class HomeController {
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;
	@GetMapping("/")
	public String home(Model model) {
		model.addAttribute("title", "Home page of smart contact manager");
		return "home";
	}
	@GetMapping("/signup")
	public String signup(Model model) {
		model.addAttribute("title", "Signup Page");
		model.addAttribute("user", new User());
		return "signup";
	}
	@PostMapping("/proccess_signup")
	public String proccess_signup( @Valid @ModelAttribute("user")User user,BindingResult result,@RequestParam(value = "agreement",defaultValue = "false")boolean agreement, Model model,HttpSession session) throws Exception {
		if(!agreement) {
			model.addAttribute("user", user);
			throw new Exception("must accept terms and conditions");
			
		}
		if(result.hasErrors()) {
			model.addAttribute("user", user);
			return "signup";
		}
		try {
			user.setRole("ROLE_USER");
			user.setEnabled(true);
			user.setImageUrl("default.png");
			user.setPassword(this.bCryptPasswordEncoder.encode(user.getPassword()));
			this.userRepository.save(user);
			session.setAttribute("message", new Message("successfully updated", "alert-success"));
		} catch (Exception e) {
			e.printStackTrace();
			session.setAttribute("message", new Message("something went wrong", "alert-danger"));
		}
		System.out.println(user);
		
		return "signup";
	}
	@GetMapping("/signin")
	public String login(Model model) {
		model.addAttribute("title", "Login Page");
		return "login";
	}
}
