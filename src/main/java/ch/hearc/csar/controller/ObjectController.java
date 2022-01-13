package ch.hearc.csar.controller;

import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.view.RedirectView;

import ch.hearc.csar.model.User;
import ch.hearc.csar.service.UserService;
import ch.hearc.csar.model.Loan;
import ch.hearc.csar.model.Object;
import ch.hearc.csar.model.Tag;
import ch.hearc.csar.repository.LoanRepository;
import ch.hearc.csar.repository.ObjectRepository;
import ch.hearc.csar.model.Loan;
import ch.hearc.csar.repository.LoanRepository;
import ch.hearc.csar.repository.TagRepository;
import ch.hearc.csar.repository.UserRepository;

@Controller
public class ObjectController {

	@Autowired
	private ObjectRepository objectRepository;

	@Autowired
	private LoanRepository loanRepository;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private TagRepository tagRepository;

	@Autowired
	private UserService userService;

	@GetMapping(value = { "/", "/home" })
	public String home_page(Model model) {

		List<Object> finalObjects  = new  ArrayList<>();
		
		// User data
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		User user = userService.findUserByEmail(auth.getName());
		if (user != null) {
			boolean isAdmin = auth.getAuthorities().stream().anyMatch(r -> r.getAuthority().equals("ADMIN"));
			model.addAttribute("isUserAdmin", isAdmin);
			model.addAttribute("isUserConnected", true);
			
			List<Object> objects = objectRepository.findFromOthers(user.getId());
			for(Object o : objects) {
				List<Loan> loans = loanRepository.findByObjectOrderByDate(o);
				if(loans.size()==0||loans.get(loans.size()-1).getState()==3) {
					finalObjects.add(o);
				}
			}
		} else {
			List<Object> objects = objectRepository.findAll();
			for(Object o : objects) {
				List<Loan> loans = loanRepository.findByObjectOrderByDate(o);
				if(loans.size()==0||loans.get(loans.size()-1).getState()==3) {
					finalObjects.add(o);
				}
			}
		}

		model.addAttribute("objects", finalObjects);
		return "home";
	}

	@GetMapping(value = { "/admin" })
	public String admin_page(Model model) {
		// User data
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		User user = userService.findUserByEmail(auth.getName());
		if (user != null) {
			boolean isAdmin = auth.getAuthorities().stream().anyMatch(r -> r.getAuthority().equals("ADMIN"));
			model.addAttribute("isUserAdmin", isAdmin);
			model.addAttribute("isUserConnected", true);
		}
		List<Object> objects = objectRepository.findAll();
		int[] state = new int[1000000];
		int[] loan = new int[100000];
		User[] users = new User[100000];
		for (Object o : objects) {
			List<Loan> loans = loanRepository.findByObjectOrderByDate(o);
			if (loans.size() > 0) {
				state[o.getId()] = loans.get(loans.size() - 1).getState();
				users[o.getId()] = loans.get(loans.size() - 1).getBorrower();
				loan[o.getId()] = loans.get(loans.size() - 1).getId();
			}
		}
		model.addAttribute("objects", objects);
		model.addAttribute("state", state);
		model.addAttribute("users", users);
		model.addAttribute("loan", loan);
		
		model.addAttribute("tags", tagRepository.findAll());

		return "admin";
	}

	@GetMapping(value = "/remove")
	public RedirectView remove(@RequestParam(value = "id", required = true) int id,
			@RequestParam(value = "source", required = true) String source) {
		RedirectView page = new RedirectView();

		// User data
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		User user = userService.findUserByEmail(auth.getName());
		if (user != null) {
			boolean isAdmin = auth.getAuthorities().stream().anyMatch(r -> r.getAuthority().equals("ADMIN"));
			Optional<Object> o = objectRepository.findById(id);
			if (o.isPresent() && (isAdmin || o.get().getOwners().getId() == user.getId())) {
				o.get().getTags().clear();
				objectRepository.save(o.get());
				objectRepository.delete(o.get());
				page.setUrl("/" + source);
			} else {
				page.setUrl("/");
			}
		} else {
			page.setUrl("/");
		}
		return page;
	}

	@GetMapping("/edit")
	public String edit(Model model, @RequestParam(value = "id", required = true) int id) {

		model.addAttribute("tags", tagRepository.findAll());
		
		// User data
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		User user = userService.findUserByEmail(auth.getName());
		if (user != null) {
			boolean isAdmin = auth.getAuthorities().stream().anyMatch(r -> r.getAuthority().equals("ADMIN"));
			model.addAttribute("isUserAdmin", isAdmin);
			model.addAttribute("isUserConnected", true);
			Optional<Object> o = objectRepository.findById(id);
			if (o.isPresent() && (isAdmin || o.get().getOwners().getId() == user.getId())) {
				model.addAttribute("object", o.get());
			}
		}
		return "edit";
	}

	@PostMapping("/edit")
	public String editPost(Model model, @RequestParam(value = "id", required = true) int id,
			@RequestParam(value = "name", required = true) String name) {

		model.addAttribute("tags", tagRepository.findAll());
		
		// User data
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		User user = userService.findUserByEmail(auth.getName());
		if (user != null) {
			boolean isAdmin = auth.getAuthorities().stream().anyMatch(r -> r.getAuthority().equals("ADMIN"));
			model.addAttribute("isUserAdmin", isAdmin);
			model.addAttribute("isUserConnected", true);
			Optional<Object> o = objectRepository.findById(id);
			if (o.isPresent() && (isAdmin || o.get().getOwners().getId() == user.getId())) {
				o.get().setName(name);
				model.addAttribute("object", objectRepository.save(o.get()));
			}
		}
		return "edit";
	}
	
	@GetMapping(value = "/add")
	public String add(Model model) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		User user = userService.findUserByEmail(auth.getName());
		if (user != null) {
			boolean isAdmin = auth.getAuthorities().stream().anyMatch(r -> r.getAuthority().equals("ADMIN"));
			model.addAttribute("isUserAdmin", isAdmin);
			model.addAttribute("isUserConnected", true);
		}
		
		return "add";
	}
	
	@PostMapping("/add")
	public String addPost(Model model, @RequestParam(value = "name", required = true) String name) {

		model.addAttribute("tags", tagRepository.findAll());
		
		// User data
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		User user = userService.findUserByEmail(auth.getName());
		if (user != null) {
			boolean isAdmin = auth.getAuthorities().stream().anyMatch(r -> r.getAuthority().equals("ADMIN"));
			model.addAttribute("isUserAdmin", isAdmin);
			model.addAttribute("isUserConnected", true);
			Object o = new Object();
			o.setName(name);
			o.setOwners(user);
			model.addAttribute("object", objectRepository.save(o));
		}
		return "edit";
	}
	
	@GetMapping(value = "/removeTag")
	public RedirectView removeTag(@RequestParam(value = "idObj", required = true) int idObj,
			@RequestParam(value = "idTag", required = true) int idTag) {
		RedirectView page = new RedirectView();

		// User data
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		User user = userService.findUserByEmail(auth.getName());
		if (user != null) {
			boolean isAdmin = auth.getAuthorities().stream().anyMatch(r -> r.getAuthority().equals("ADMIN"));
			Optional<Object> o = objectRepository.findById(idObj);
			if (o.isPresent() && (isAdmin || o.get().getOwners().getId() == user.getId())) {
				o.get().getTags().remove(tagRepository.findById(idTag).get());
				objectRepository.save(o.get());
			} 
		} 
		page.setUrl("/edit?id=" + idObj);
		return page;
	}
	
	@PostMapping(value = "/addTag")
	public RedirectView addTag(@RequestParam(value = "idObj", required = true) int idObj,
			@RequestParam(value = "idTag", required = true) String idTag) {
		RedirectView page = new RedirectView();

		// User data
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		User user = userService.findUserByEmail(auth.getName());
		if (user != null) {
			boolean isAdmin = auth.getAuthorities().stream().anyMatch(r -> r.getAuthority().equals("ADMIN"));
			Optional<Object> o = objectRepository.findById(idObj);
			if (o.isPresent() && (isAdmin || o.get().getOwners().getId() == user.getId())) {
				o.get().getTags().add(tagRepository.findById(Integer.parseInt(idTag)).get());
				objectRepository.save(o.get());
			} 
		} 
		page.setUrl("/edit?id=" + idObj);
		return page;
	}
	
	@PostMapping(value = "/createTag")
	public RedirectView createTag(@RequestParam(value = "name", required = true) String name) {
		RedirectView page = new RedirectView();

		Tag t = new Tag();
		t.setName(name);
		tagRepository.save(t);
				
		page.setUrl("/admin");
		return page;
	}
	
	@GetMapping(value = "/deleteTag")
	public RedirectView DeleteTag(@RequestParam(value = "idTag", required = true) int idTag) {
		RedirectView page = new RedirectView();

		Optional<Tag> t = tagRepository.findById(idTag);
		tagRepository.delete(t.get());
				
		page.setUrl("/admin");
		return page;
	}

	@GetMapping(value = "/own")
	public String own_page(Model model) {
		// User data
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		User user = userService.findUserByEmail(auth.getName());
		if (user != null) {
			boolean isAdmin = auth.getAuthorities().stream().anyMatch(r -> r.getAuthority().equals("ADMIN"));
			model.addAttribute("isUserAdmin", isAdmin);
			model.addAttribute("isUserConnected", true);

			List<Object> myObjects = objectRepository.findMine(user.getId());
			model.addAttribute("objects", myObjects);

			Map<Object, Loan> myLoans = new HashMap<Object, Loan>();
			for (Object myObject : myObjects) {
				Loan lastLoan = loanRepository.findByObjectOrderByDateDesc(myObject);
				myLoans.put(myObject, lastLoan);
			}
			model.addAttribute("loans", myLoans);
		}

		return "own";
	}
}
