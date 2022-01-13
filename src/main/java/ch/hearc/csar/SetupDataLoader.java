package ch.hearc.csar;

import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import ch.hearc.csar.model.Role;
import ch.hearc.csar.model.Tag;
import ch.hearc.csar.model.User;
import ch.hearc.csar.model.Object;
import ch.hearc.csar.model.Loan;
import ch.hearc.csar.repository.RoleRepository;
import ch.hearc.csar.repository.TagRepository;
import ch.hearc.csar.repository.UserRepository;
import ch.hearc.csar.repository.ObjectRepository;
import ch.hearc.csar.repository.LoanRepository;
import ch.hearc.csar.service.UserService;

@Component
public class SetupDataLoader implements ApplicationListener<ContextRefreshedEvent> {

	boolean alreadySetup = false;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private UserService userService;

	@Autowired
	private RoleRepository roleRepository;

	@Autowired
	private TagRepository tagRepository;

	@Autowired
	private ObjectRepository objectRepository;

	@Autowired
	private LoanRepository loanRepository;

	@Override
	@Transactional
	public void onApplicationEvent(ContextRefreshedEvent event) {

		if (alreadySetup)
			return;

		createRoleIfNotFound("ADMIN");
		createRoleIfNotFound("USER");

		// Add temporarily data to test home page
		// Add 2 tags
		Tag tagOrganisateur = createTagIfNotFound("Organisateur");
		Tag tagCable = createTagIfNotFound("Cable");
		Set<Tag> tags = new HashSet<>(Arrays.asList(tagOrganisateur, tagCable));
		// Add 2 users
		User jeandubois = createUserIfNotFound("Jean", "Dubois", "test@test.test", "1234");
		User adolphedubois = createUserIfNotFound("Adolphe", "Dubois", "adolphe@dubois.ch", "1234");
		// Add 3 objects
		createObjectIfNotFound("Écran", jeandubois, tags);
		createObjectIfNotFound("Cable RJ45", adolphedubois, tags);
		Object objectMultiprise = createObjectIfNotFound("Multiprise", adolphedubois, tags);
		// Add 1 loan
		createLoanIfNotFound(objectMultiprise, jeandubois, new Date(System.currentTimeMillis()), 1);

		alreadySetup = true;
	}

	@Transactional
	Role createRoleIfNotFound(String name) {
		Role role = roleRepository.findByName(name);
		if (role == null) {
			role = new Role(name);
			roleRepository.save(role);
		}
		return role;
	}

	@Transactional
	Tag createTagIfNotFound(String name) {
		Tag tag = tagRepository.findByName(name);
		if (tag == null) {
			tag = new Tag(name);
			tagRepository.save(tag);
		}
		return tag;
	}

	@Transactional
	User createUserIfNotFound(String firstname, String lastname, String email, String password) {
		User user = userRepository.findByEmail(email);
		if (user == null) {
			user = new User(firstname, lastname, email, password);
			userService.saveUser(user);
		}
		return user;
	}

	@Transactional
	Object createObjectIfNotFound(String name, User user, Set<Tag> tags) {
		Object object = objectRepository.findByName(name);
		if (object == null) {
			object = new Object(name, user, tags);
			objectRepository.save(object);
		}
		return object;
	}

	@Transactional
	Loan createLoanIfNotFound(Object object, User user, Date date, int state) {
		Loan loan = loanRepository.findByObjectAndBorrower(object, user);
		if (loan == null) {
			loan = new Loan(object, user, date, state);
			loanRepository.save(loan);
		}
		return loan;
	}
}