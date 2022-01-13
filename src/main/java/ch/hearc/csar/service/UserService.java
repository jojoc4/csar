package ch.hearc.csar.service;

import java.util.Arrays;
import java.util.HashSet;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import ch.hearc.csar.model.Role;
import ch.hearc.csar.model.User;
import ch.hearc.csar.repository.RoleRepository;
import ch.hearc.csar.repository.UserRepository;

@Service("userService")
public class UserService 
{

	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private RoleRepository roleRepository;

	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;
	
	
	public User findUserByEmail(String email) 
	{
		return userRepository.findByEmail(email);
	}

	
	public void saveUser(User user) 
	{
		user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
		Role userRole = roleRepository.findByName("USER");
		user.setRoles(new HashSet<Role>(Arrays.asList(userRole)));
		userRepository.save(user);
	}
	
	public boolean modifyPassword(User user, String oldPassword, String newPassword) 
	{
		if(bCryptPasswordEncoder.matches(oldPassword, user.getPassword())) {
			user.setPassword(bCryptPasswordEncoder.encode(newPassword));
			userRepository.save(user);
			return true;
		}else {
			return false;
		}
	}
	
}
