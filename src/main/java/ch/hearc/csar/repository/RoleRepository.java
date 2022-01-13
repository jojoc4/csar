package ch.hearc.csar.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ch.hearc.csar.model.Role;


@Repository("RoleRepository")
public interface RoleRepository extends JpaRepository<Role, Integer>
{
	Role findByName(String name);
}