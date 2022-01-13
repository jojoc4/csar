package ch.hearc.csar.controller;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.view.RedirectView;

import ch.hearc.csar.model.Loan;
import ch.hearc.csar.model.Object;
import ch.hearc.csar.model.Tag;
import ch.hearc.csar.model.User;
import ch.hearc.csar.repository.LoanRepository;
import ch.hearc.csar.repository.ObjectRepository;
import ch.hearc.csar.service.UserService;
import ch.hearc.csar.util.GeneratePDF;

@Controller
public class LoanController {

	@Autowired
	private LoanRepository loanRepository;
	
	@Autowired
	private UserService userService;

	@Autowired
	private ObjectRepository objectRepository;
	
	@Autowired
	private JavaMailSender emailSender;
	
	@GetMapping(value="/loan-label", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<InputStreamResource>  label(@RequestParam(value = "id", required = true) int idLoan){
		 
		 ByteArrayInputStream bis = GeneratePDF.loanLabel(idLoan);
		 
		 var headers = new HttpHeaders();
	     headers.add("Content-Disposition", "inline; filename=label.pdf");
		
	     return ResponseEntity.ok().headers(headers).contentType(MediaType.APPLICATION_PDF).body(new InputStreamResource(bis));
	}
	
	@GetMapping(value="/ask")
    public RedirectView  ask(@RequestParam(value = "id", required = true) int id){
		RedirectView page = new RedirectView();
		
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		User user = userService.findUserByEmail(auth.getName());
		
		Optional<Object> oo = objectRepository.findById(id);
		
		if(oo.isPresent()) {
			Object o = oo.get();
			
			List<Loan> history = loanRepository.findByObjectOrderByDate(o);
			
			Loan l = new Loan();
			l.setObject(o);
			l.setBorrower(user);
			l.setDate(new Date());
			l.setState(1);
			
			SimpleMailMessage message = new SimpleMailMessage();
			message.setFrom("csar@pnplan.ch");
			message.setTo(o.getOwners().getEmail());
			message.setSubject("CSAR - Demande de pret");
			message.setText("Bonjour "+o.getOwners().getFirstname()+",\n\nL'objet "+o.getName()+" a été demandé par "+user.getFirstname()+". Connectez-vous au système csar pour valider ou refuser le prêt..\n\nBon jeu, votre système csar.\n\nEn cas de problème, merci de contacter les organisateurs");
			
			
			if(history.size()==0) {
				loanRepository.save(l);
				emailSender.send(message);
			}else {
				if(history.get(history.size()-1).getState()==3) {
					loanRepository.save(l);
					emailSender.send(message);
				}
			}
			
		}
		page.setUrl("/");
		return page;
	}
	
	@GetMapping(value="/confirm")
	public RedirectView confirm(@RequestParam(value = "id", required = true) int id) {
		RedirectView page = new RedirectView();

		// User data
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		User user = userService.findUserByEmail(auth.getName());
		if (user != null) {
			boolean isAdmin = auth.getAuthorities().stream().anyMatch(r -> r.getAuthority().equals("ADMIN"));
			Optional<Loan> l = loanRepository.findById(id);
			if (l.isPresent() && l.get().getState() == 1 && (isAdmin || l.get().getObject().getOwners().getId() == user.getId())) {
				l.get().setState(2);
				loanRepository.save(l.get());

				SimpleMailMessage message = new SimpleMailMessage();
				message.setFrom("csar@pnplan.ch");
				message.setTo(l.get().getBorrower().getEmail());
				message.setSubject("CSAR - Demande acceptée");
				message.setText("Bonjour "+l.get().getBorrower().getFirstname()+",\n\nLe prêt de l'objet "+l.get().getObject().getName()+" a été validé par "+user.getFirstname()+". Vous pouvez maintenant aller le chercher.\n\nBon jeu, votre système csar.\n\nEn cas de problème, merci de contacter les organisateurs");
				emailSender.send(message);
			} 
		}
		page.setUrl("/own ");
		return page;
	}
	
	@GetMapping(value="/refuse")
	public RedirectView refuse(@RequestParam(value = "id", required = true) int id) {
		RedirectView page = new RedirectView();

		// User data
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		User user = userService.findUserByEmail(auth.getName());
		if (user != null) {
			boolean isAdmin = auth.getAuthorities().stream().anyMatch(r -> r.getAuthority().equals("ADMIN"));
			Optional<Loan> l = loanRepository.findById(id);
			if (l.isPresent() && l.get().getState() == 1 && (isAdmin || l.get().getObject().getOwners().getId() == user.getId())) {
				l.get().setState(3);
				loanRepository.save(l.get());

				SimpleMailMessage message = new SimpleMailMessage();
				message.setFrom("csar@pnplan.ch");
				message.setTo(l.get().getBorrower().getEmail());
				message.setSubject("CSAR - Demande refusée");
				message.setText("Bonjour "+l.get().getBorrower().getFirstname()+",\n\nLe prêt de l'objet "+l.get().getObject().getName()+" a été refusé par "+user.getFirstname()+".\n\nBon jeu, votre système csar.\n\nEn cas de problème, merci de contacter les organisateurs");
				emailSender.send(message);
			} 
		}
		page.setUrl("/own ");
		return page;
	}
	
	@GetMapping(value="/askback")
	public RedirectView askback(@RequestParam(value = "id", required = true) int id) {
		RedirectView page = new RedirectView();

		// User data
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		User user = userService.findUserByEmail(auth.getName());
		if (user != null) {
			boolean isAdmin = auth.getAuthorities().stream().anyMatch(r -> r.getAuthority().equals("ADMIN"));
			Optional<Loan> l = loanRepository.findById(id);
			if (l.isPresent() && l.get().getState() == 2 && (isAdmin || l.get().getObject().getOwners().getId() == user.getId())) {
				l.get().setState(4);
				loanRepository.save(l.get());
				
				SimpleMailMessage message = new SimpleMailMessage();
				message.setFrom("csar@pnplan.ch");
				message.setTo(l.get().getBorrower().getEmail());
				message.setSubject("CSAR - Demande de retour");
				message.setText("Bonjour "+l.get().getBorrower().getFirstname()+",\n\nL'objet "+l.get().getObject().getName()+" a été demandé en retour par "+user.getFirstname()+". Merci de le lui rapporter au plus vite.\n\nBon jeu, votre système csar.\n\nEn cas de problème, merci de contacter les organisateurs");
				emailSender.send(message);
			} 
		}
		page.setUrl("/own ");
		return page;
	}
	
	@GetMapping(value="/back")
	public RedirectView back(@RequestParam(value = "id", required = true) int id, @RequestParam(value = "source", required = false) String source) {
		RedirectView page = new RedirectView();

		// User data
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		User user = userService.findUserByEmail(auth.getName());
		if (user != null) {
			boolean isAdmin = auth.getAuthorities().stream().anyMatch(r -> r.getAuthority().equals("ADMIN"));
			Optional<Loan> l = loanRepository.findById(id);
			if (l.isPresent() && (isAdmin || l.get().getState() == 2 || l.get().getState() == 4) && (isAdmin || l.get().getObject().getOwners().getId() == user.getId())) {
				l.get().setState(3);
				loanRepository.save(l.get());
			} 
		}
		if(source=="") {
			page.setUrl("/own");
		}else {
			page.setUrl("/admin");
		}
		return page;
	}
}
