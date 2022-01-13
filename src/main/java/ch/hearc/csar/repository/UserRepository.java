package ch.hearc.csar.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ch.hearc.csar.model.User;

@Repository("UserRepository")
public interface UserRepository extends JpaRepository<User, Long> {
	User findByEmail(String email);
}
