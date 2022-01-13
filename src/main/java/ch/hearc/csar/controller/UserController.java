package ch.hearc.csar.controller;

import java.util.List;
import java.util.Map;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.view.RedirectView;

import ch.hearc.csar.model.Loan;
import ch.hearc.csar.model.Object;
import ch.hearc.csar.model.User;
import ch.hearc.csar.repository.LoanRepository;
import ch.hearc.csar.repository.ObjectRepository;
import ch.hearc.csar.repository.UserRepository;
import ch.hearc.csar.service.UserService;


//////////////
//  UPDATE  //
//  CHAP_6 //
//////////////

@Controller
public class UserController {

	@Autowired
	private UserService userService;
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private LoanRepository loanRepository;
	
	@Autowired
	private ObjectRepository objectRepository;

	/**
	 * Select the login view to render by returning its name
	 */
	@GetMapping("/login")
	public String login(Map<String, Object> model) {		
		
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		User user = userService.findUserByEmail(auth.getName());
		if (user != null) 
		{
			return "redirect:/";
		}
		
		return "login";
	}
	
	@GetMapping("/account/delete")
    public String deleteUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.findUserByEmail(auth.getName());
		
		// 1. supprimer les loans ou on est borrower
		List<Loan> loansBorrower = loanRepository.findByBorrower(user);
		for (Loan loanBorrower : loansBorrower)
			loanRepository.delete(loanBorrower);
		
		// 2. supprimer les loans de nos objets + 3. delete myobjects
		List<Object> myObjects = objectRepository.findByOwners(user);
		for (Object myObject : myObjects)
		{
			for (Loan myLoan : loanRepository.findByObject(myObject))
				loanRepository.delete(myLoan);

			myObject.getTags().clear();
			objectRepository.save(myObject);			
			objectRepository.delete(myObject);
		}
        
		user.getRoles().clear();
		userRepository.save(user);
        userRepository.delete(user);
        
        return "redirect:/";
    }	

	@GetMapping("/signup")
	public String signup(Map<String, User> model) {
		
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		User user = userService.findUserByEmail(auth.getName());
		if (user != null) 
		{
			return "redirect:/";
		}
		
		user = new User();
		model.put("user", user);
		
		return "signup";
	}

	@PostMapping("/signup")
	public String createUser(@Valid User user, BindingResult bindingResult, Map<String, Object> model) {

		User userExists = userService.findUserByEmail(user.getEmail());

		if (userExists != null) {
			return ("redirect:/signup?error=true");
		}
		
		if (bindingResult.hasErrors()) 
		{
			return ("redirect:/signup?error=true");
		} 
		else 
		{
			userService.saveUser(user);

			return ("redirect:login?success=true");
		}
	}
	
	/**
	 * Select the login view to render by returning its name
	 */
	@GetMapping("/account")
	public String account(Model model) {		
		
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		User user = userService.findUserByEmail(auth.getName());
		boolean isAdmin = auth.getAuthorities().stream().anyMatch(r -> r.getAuthority().equals("ADMIN"));
		model.addAttribute("isUserAdmin", isAdmin);
		model.addAttribute("isUserConnected", true);
		model.addAttribute("user", user);
		
		return "account";
	}
	
	@PostMapping(value = "/account")
	public RedirectView Postaccount(@RequestParam(value = "lastname", required = true) String lastname,
			@RequestParam(value = "firstname", required = true) String firstname,
			@RequestParam(value = "passwordold", required = false) String passwordold,
			@RequestParam(value = "passwordnew", required = false) String passwordnew) {
		RedirectView page = new RedirectView();

		// User data
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		User user = userService.findUserByEmail(auth.getName());
		if (user != null) {
			user.setFirstname(firstname);
			user.setLastname(lastname);
			userRepository.save(user);
			if(!passwordold.equals(""))
			{
				if(!passwordnew.equals(""))
				{
					if(userService.modifyPassword(user, passwordold, passwordnew))
					{
						page.setUrl("/account?success");
					}else {
						page.setUrl("/account?error");
					}
				}else {
					page.setUrl("/account?error");
				}
			}else {
				page.setUrl("/account?success");
			}
		} 
		return page;
	}
	
	

}