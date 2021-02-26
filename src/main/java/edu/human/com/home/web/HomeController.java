package edu.human.com.home.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

@Controller
public class HomeController {
	
	
	//==================================================
	@RequestMapping("/logout.do")
	public String logout() throws Exception {
		RequestContextHolder.getRequestAttributes().removeAttribute("LoginVO", RequestAttributes.SCOPE_SESSION);
		return "redirect:/";
	}
	
	@RequestMapping("/home.do")
	public String home() throws Exception {
		
		return "home/home";
	}
	
	
	//method, RequestMethod=GET[POST]없이 사용하면, 둘 다 허용이 되는 매핑이 됨.
}
