package com.smart.controller;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.smart.dao.ContactRepository;
import com.smart.dao.UserRepository;
import com.smart.entities.Contact;
import com.smart.entities.User;
import com.smart.helper.Message;
import com.razorpay.*;

@Controller
@RequestMapping("/user")
public class UserController {
	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;
	@Autowired
	private ContactRepository contactRepository;
	@Autowired
	private UserRepository userRepository;
	@ModelAttribute
	public void commonData(Model model,Principal principal) {
		User user = this.userRepository.getUserByUsername(principal.getName());
		model.addAttribute("user", user);
	}
	
	@GetMapping("/index")
	public String dashboard() {
		return "normal/dashboard";
	}
	@GetMapping("/add_contact")
	public String addContact(Model model) {
		model.addAttribute("contact", new Contact());
		model.addAttribute("title", "Add Contact Page");
		return "normal/add_contact";
	}
	@PostMapping("/proccess_addcontact")
	public String proccess_addcontact(@Valid @ModelAttribute("contact")Contact contact,BindingResult result,@RequestParam("profileImage")MultipartFile file,Model model,HttpSession session,Principal principal)  {
		User user = this.userRepository.getUserByUsername(principal.getName());
		if(file.isEmpty()) {
			contact.setImage("default.png");
		}else {
			
			if(result.hasErrors()) {
				model.addAttribute("contact", contact);
				return "normal/add_contact";
			}
			try {
				File uploadImage = new ClassPathResource("static/img").getFile();
				Files.copy(file.getInputStream(), Paths.get(uploadImage.getAbsolutePath()+File.separator+file.getOriginalFilename()), StandardCopyOption.REPLACE_EXISTING);
				contact.setImage(file.getOriginalFilename());
			} catch (Exception e) {
				e.printStackTrace();
			}
		
		}
		contact.setUser(user);
		 user.getContacts().add(contact);
		 this.userRepository.save(user);
		 session.setAttribute("message", new Message("successfully update", "alert-success"));
		return "normal/add_contact";
	}
	@GetMapping("/show_contact/{page}")
	public String show_contact(@PathVariable("page")int page,Model model,Principal principal) {
		User user = this.userRepository.getUserByUsername(principal.getName());
		Pageable pageable=PageRequest.of(page, 3);
		Page<Contact> contact = this.contactRepository.findContactByUser(user.getId(),pageable);
		model.addAttribute("contact", contact);
		model.addAttribute("currentPage", page);
		model.addAttribute("totalPages", contact.getTotalPages());
		return "normal/show_contact";
	}
	@GetMapping("/{id}/contact")
	public String showcontactDetails(@PathVariable("id")int id,Model model) {
		Contact contact = this.contactRepository.findById(id).get();
		model.addAttribute("contact", contact);
		return "normal/showcontactDetails";
	}
	@GetMapping("/update/{id}")
	public String updateContact(@PathVariable("id")int id,Principal principal,Model model) {
		Contact contact = this.contactRepository.findById(id).get();
		User user = this.userRepository.getUserByUsername(principal.getName());
		if(contact.getUser().getId()==user.getId()) {
			model.addAttribute("contact", contact);
		}
		return "normal/updateContact";
	}
	@PostMapping("/proccess_updatecontact")
	public String proccess_updatecontact(@ModelAttribute("contact")Contact contact,@RequestParam("profileImage")MultipartFile file,Model model,HttpSession session,Principal principal) {
		User user = this.userRepository.getUserByUsername(principal.getName());
		Contact oldContact = this.contactRepository.findById(contact.getcId()).get();
		if(file.isEmpty()) {
			contact.setImage(oldContact.getImage());
		}
		try {
			File updateImage=new ClassPathResource("static/img").getFile();
			Files.copy(file.getInputStream(), Paths.get(updateImage.getAbsolutePath()+File.separator+file.getOriginalFilename()), StandardCopyOption.REPLACE_EXISTING);
			contact.setImage(file.getOriginalFilename());
		} catch (Exception e) {
			
			e.printStackTrace();
		}
		contact.setUser(user);
		user.getContacts().add(contact);
		this.userRepository.save(user);
		session.setAttribute("message", new Message("successfully updated", "alert-success"));
		return "redirect:/user/show_contact/0";
	}
	@GetMapping("/delete/{id}")
	public String deleteContact(@PathVariable("id")int id,Principal pr) {
		Contact contact = this.contactRepository.findById(id).get();
		File deeleteImage=null;
		try {
			deeleteImage=new ClassPathResource("static/img").getFile();
			File file=new File(deeleteImage, contact.getImage());
			file.delete();
		} catch (Exception e) {
		e.printStackTrace();
		}
		User user = this.userRepository.getUserByUsername(pr.getName());
		user.getContacts().remove(contact);
		this.userRepository.save(user);
		return "redirect:/user/show_contact/0";
	}
	@GetMapping("/profile")
	public String profile(Model model) {
		model.addAttribute("title", "Profile - smart contact manager");
		return "normal/profile";
	}
	@GetMapping("/settings")
	public String settings(Model model) {
		model.addAttribute("title", "Settings - smart contact manager");
		return "normal/settings";
	}
	@PostMapping("/changePassword")
	public String changePassword(@RequestParam("oldPassword")String oldPassword,@RequestParam("newPassword")String newPassword,Principal principal,HttpSession session) {
		User currentUser = this.userRepository.getUserByUsername(principal.getName());
		
		if(this.bCryptPasswordEncoder.matches(oldPassword, currentUser.getPassword())) {
				currentUser.setPassword(this.bCryptPasswordEncoder.encode(newPassword));
				this.userRepository.save(currentUser);
				return "redirect:/user/index";
		}else {
			session.setAttribute("message", new Message("your old password is wrong", "alert-danger"));
		return "redirect:/user/settings";
		}
	}
	@PostMapping("/create_order")
	@ResponseBody
	public String createOrder(@RequestBody Map<String, Object>data) throws Exception {
		int amt=Integer.parseInt(data.get("amount").toString());
		
			var client = new RazorpayClient("rzp_test_RMxk3b9Szit29G", "TRP50ooyemfxeXAKUSeAgM4p");
		JSONObject ob=new JSONObject();
		ob.put("amount", amt*100);
		ob.put("currency", "INR");
		ob.put("receipt", "txn_23354");
		Order order = client.Orders.create(ob);
		
		System.out.println("Order "+order);
		
		return order.toString();
	}
}
