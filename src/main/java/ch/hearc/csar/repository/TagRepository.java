package ch.hearc.csar.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ch.hearc.csar.model.Tag;

@Repository("TagRepository")
public interface TagRepository extends JpaRepository<Tag, Integer> {
	Tag findByName(String name);
}
