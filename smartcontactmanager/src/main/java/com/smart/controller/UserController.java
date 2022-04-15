package com.smart.controller;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.smart.dao.ContactRepository;
import com.smart.dao.UserRepository;
import com.smart.entities.Contact;
import com.smart.entities.User;
import com.smart.helper.Message;

@Controller
@RequestMapping("/user")
public class UserController {
	@Autowired
	private UserRepository userRepo;
	@Autowired
	private ContactRepository contRepo;
	
	@ModelAttribute
	public void addCommonData(Model m,Principal principal) {
		String name = principal.getName();
		System.out.println(name);
		
		User user = userRepo.getUserByUserName(name);
		System.out.println(user);
		m.addAttribute("user",user);
	}
	@RequestMapping("/index")
	public String dashboard(Model model, Principal principal)
	{
		return "normal/user_dashboard";
	}
	@GetMapping("/add-contact")
	public String openAddContactForm(Model model)
	{
		model.addAttribute("title","Add Contact");
		model.addAttribute("contact",new Contact());
		return "normal/add_contact_form";
	}
	//processing and contact form
	@PostMapping("/process-contact")
	public String processContact(@ModelAttribute Contact contact,
			@RequestParam("profileImage") MultipartFile file, 
			Principal principal,HttpSession session)
	{
		try {
		String name=principal.getName();
		User user=this.userRepo.getUserByUserName(name);
		
		if(file.isEmpty())
		{
			contact.setImage("contact.png");
			System.out.println(file.getName());
		}
		else {
			contact.setImage(file.getOriginalFilename());
			
			
			File file2 = new ClassPathResource("static/image").getFile();
			
			Path path = Paths.get(file2.getAbsolutePath()+File.separator+file.getOriginalFilename());
			Files.copy(file.getInputStream(),path, StandardCopyOption.REPLACE_EXISTING);
		    System.out.println("Image uploaded");
		}
		contact.setUser(user);
		user.getContacts().add(contact);
		this.userRepo.save(user);
		System.out.println(contact);
		//success message
		session.setAttribute("message", new Message("Your contact is added !!","success"));
		}
		catch(Exception e)
		{
			e.printStackTrace();
			//error message
			session.setAttribute("message", new Message("Somethinng went wrong, try again","danger"));

		}
		return "normal/add_contact_form";
	}
	//show contact handler
	@GetMapping("/show-contact")
	public String showContact(Model m,Principal principal)
	{
		m.addAttribute("title","Show User Contacts");
		String name = principal.getName();
		User user2 = this.userRepo.getUserByUserName(name);
		List<Contact> contact = this.contRepo.findContactsByUser(user2.getId());
		m.addAttribute("contact",contact);
		return "normal/show_contact";
	}
	@RequestMapping("/contact/{cid}")
	public String showContactDetail(@PathVariable("cid") Integer cid,Model m)
	{
		System.out.println(cid);
		Optional<Contact> id = this.contRepo.findById(cid);
		Contact contact = id.get();
		m.addAttribute("contact",contact);
		return "normal/contact_detail";
	}
	
	// delete contact handler
	@GetMapping("/delete/{cid}")
	public String deleteContact(@PathVariable("cid") Integer cid, Model m,HttpSession session)
	{
		Optional<Contact> optional = this.contRepo.findById(cid);
		Contact contact = optional.get();
		contact.setUser(null);
		this.contRepo.delete(contact);
		
		session.setAttribute("message", new Message("Contact deleted successfully","success"));
		return "redirect:/user/show-contact";
	}
	@GetMapping("/profile")
	public String yourProfile(Model model)
	{
		model.addAttribute("title","Profile Page");
		return "normal/profile";
	}
	
	@PostMapping("/update-contact/{cid}")
	public String updateForm(@PathVariable("cid") Integer cid,Model m)
	{
		m.addAttribute("title","Update Contact");  
		Contact contact = this.contRepo.findById(cid).get();
		m.addAttribute("contact",contact);
		return "normal/update-form";
	}
	@PostMapping("/process-update/{cid}")
	public String processUpdate(@ModelAttribute Contact contact,
			@RequestParam("profileImage") MultipartFile file, 
			Principal principal,HttpSession session)
	{
		try {
		String name=principal.getName();
		User user=this.userRepo.getUserByUserName(name);
		
		if(file.isEmpty())
		{
			contact.setImage("contact.png");
			System.out.println(file.getName());
		}
		else {
			contact.setImage(file.getOriginalFilename());
			
			
			File file2 = new ClassPathResource("static/image").getFile();
			
			Path path = Paths.get(file2.getAbsolutePath()+File.separator+file.getOriginalFilename());
			Files.copy(file.getInputStream(),path, StandardCopyOption.REPLACE_EXISTING);
		    System.out.println("Image uploaded");
		}
		contact.setUser(user);
		user.getContacts().add(contact);
		this.userRepo.save(user);
		
		//success message
		session.setAttribute("message", new Message("Your contact is successfully updated !!","success"));
		}
		catch(Exception e)
		{
			session.setAttribute("message", new Message("Your contact is successfully updated !!","success"));

			//error message
		}
		return "normal/contact_detail";
	}
}
