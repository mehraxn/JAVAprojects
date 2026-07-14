package social;

import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;

/**
 * A group identified by its (unique) name. Membership is the inverse side of the
 * {@link Person}-owned many-to-many relationship.
 * <p>
 * The table is named {@code SocialGroup} because {@code GROUP} is a reserved SQL word.
 */
@Entity
@Table(name = "SocialGroup")
public class Group {

  @Id
  private String name;

  @ManyToMany(mappedBy = "groups")
  private Set<Person> members = new HashSet<>();

  Group() {
    // default constructor required by JPA
  }

  Group(String name) {
    this.name = name;
  }

  public String getName() {
    return name;
  }

  public Set<Person> getMembers() {
    return members;
  }
}
