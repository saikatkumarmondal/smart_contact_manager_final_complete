package com.smart.controller;

import java.security.Principal;
import java.util.Random;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.smart.dao.UserRepository;
import com.smart.entities.User;
import com.smart.helper.Message;
import com.smart.service.EmailService;

@Controller
public class ForgotPassword {
	private Random random=new Random(1000);
	@Autowired
	private EmailService emailService;
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;
	@GetMapping("/forgot")
	public String forgotPassword() {
		
		return "forgot_password_form";
	}
	@PostMapping("/send-otp")
	public String sendOtp(@RequestParam("email")String email,Principal principal,HttpSession session){
		User user = this.userRepository.getUserByUsername(email);
		if(user==null) {
			
			session.setAttribute("message", new Message("Your email id is wrong", "alert-danger"));
			return "forgot_password_form";
		}else{
			int otp=random.nextInt(9999);
		
		String message=""
				+ "<div style='border:1px solid red;padding:20px;'>"
				+ "<h1>"
				+ "<b> your OTP is "+otp
				+ "</b>"
				+ "</h1>"
				+ "</div>";
		
		String to=email;
		String subject="OTP Code from Smart Contact Manager";
		boolean flag = this.emailService.sendEmail(subject, message, to);
		if(flag) {
			
			session.setAttribute("oldOtp", otp)	;
			session.setAttribute("email", email);
			return "verify_otp";
		}else {
			session.setAttribute("message", new Message("Invalid Email Id", "alert-danger"));
			return "forgot_password_form";
		}
		}
	}
	@PostMapping("/verify-otp")
	public String verifyOTP(@RequestParam("otp")int otp,HttpSession session) {
		int oldOtp=(int)session.getAttribute("oldOtp");
		String email=(String)session.getAttribute("email");
		if(otp==oldOtp) {
			return "change_password_form";
		}else {
			session.setAttribute("message", new Message("Invalid OTP", "alert-danger"));
			return "verify_otp";
		}
	}
	
	public String changePassword(@RequestParam("newPassword")String newPassword,HttpSession session) {
		String email=(String)session.getAttribute("email");
		User user = this.userRepository.getUserByUsername(email);
		if(user==null) {
			session.setAttribute("message", "Invalid Password");
			return "change_password_form";
		}else {
			user.setPassword(this.bCryptPasswordEncoder.encode(newPassword));
			this.userRepository.save(user);
			return "redirect:/user/signin?change=password changed succesfully...";
		}
	}
	
}
