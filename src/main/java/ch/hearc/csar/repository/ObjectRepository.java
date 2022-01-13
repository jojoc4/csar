package ch.hearc.csar.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import ch.hearc.csar.model.Object;
import ch.hearc.csar.model.User;

@Repository("ObjectRepository")
public interface ObjectRepository extends JpaRepository<Object, Integer> {
	
	@Query(value="SELECT * FROM object WHERE object.user_id = ?1", nativeQuery=true)
	List<Object> findMine(int user_id);
	
	@Query(value="SELECT * FROM object WHERE object.user_id <> ?1", nativeQuery=true)
	List<Object> findFromOthers(int user_id);
	
	List<Object> findByOwners(User owner);
	
	Object findByName(String name);
}
