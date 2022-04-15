package com.smart.controller;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;



import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.smart.dao.UserRepository;
import com.smart.entities.User;
import com.smart.helper.Message;

@Controller
public class HomeController {
	@Autowired
	private BCryptPasswordEncoder passencoder;
	@Autowired
	private UserRepository userrepo;
	@RequestMapping("/")
	public String home(Model model)
	{
		model.addAttribute("title","Home - Smart Contact Manager");
		return "home";
	}
	
	@RequestMapping("/about")
	public String about(Model model)
	{
		model.addAttribute("title","About - Smart Contact Manager");
		return "about";
	}
	
	@RequestMapping("/signup")
	public String signup(Model model)
	{
		model.addAttribute("title","About - Smart Contact Manager");
		model.addAttribute("user",new User());
		return "signup";
	}
	@GetMapping("/login")
	public String customLogin(Model model)
	{
		model.addAttribute("title","Login Page");
		return "login";
	}
		@RequestMapping(value="/do_register", method=RequestMethod.POST)
		public String registerUser(@ModelAttribute("user") User user,@RequestParam(value="agreement",defaultValue="false")boolean agreement,
				Model model,HttpSession session)
		{
			try {
				if(!agreement)
				{
					System.out.println("you have not agreed the terms and condition");
					throw new Exception("you have not agreed the terms and condition");
				}
				user.setRole("ROLE_USER");
				user.setEnable(true);
				user.setPassword(passencoder.encode(user.getPassword()));
				System.out.println("Agreement "+agreement);
				System.out.println("user"+user);
				User result = this.userrepo.save(user);
				System.out.println(result);
				model.addAttribute("user",user);
				session.setAttribute("message", new Message("successfully registered","alert-success"));
			}catch(Exception e)
			{
				e.printStackTrace();
				model.addAttribute("user", user);
				session.setAttribute("message", new Message("Something went wrong !!","alert-danger"));
			}
			return "signup";
		}
}