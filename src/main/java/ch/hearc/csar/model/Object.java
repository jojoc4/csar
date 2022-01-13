package ch.hearc.csar.model;

import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotEmpty;

@Entity
@Table(name = "object")
public class Object {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column
	private int id;

	@Column
	@NotEmpty(message = "*Please provide a name")
	private String name;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="user_ID")
	private User owners;
	
	@ManyToMany(cascade = CascadeType.ALL)
	@JoinTable(name = "object_tag", joinColumns = @JoinColumn(name = "object_id"), inverseJoinColumns = @JoinColumn(name = "tag_id"))
	private Set<Tag> tags;
	

	public Object() {
	}

	public Object(@NotEmpty(message = "*Please provide a name") String name, User owners, Set<Tag> tags) {
		this.name = name;
		this.owners = owners;
		this.tags = tags;
	}
	

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public User getOwners() {
		return owners;
	}

	public void setOwners(User owners) {
		this.owners = owners;
	}

	public Set<Tag> getTags() {
		return tags;
	}

	public void setTags(Set<Tag> tags) {
		this.tags = tags;
	}

}
