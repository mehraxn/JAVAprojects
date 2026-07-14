package social;

import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;

/**
 * A person (account) in the social network, identified by a unique {@code code}.
 * <p>
 * Friendships are modelled as a self-referential many-to-many relationship
 * (kept symmetric by the facade). Group membership is a many-to-many relationship
 * owned by {@code Person}. Authored posts are the inverse side of {@link Post}.
 */
@Entity
public class Person {

  @Id
  private String code;

  @Column(nullable = false)
  private String name;

  @Column(nullable = false)
  private String surname;

  @ManyToMany
  @JoinTable(
      name = "PERSON_FRIENDS",
      joinColumns = @JoinColumn(name = "person_code"),
      inverseJoinColumns = @JoinColumn(name = "friend_code"))
  private Set<Person> friends = new HashSet<>();

  @ManyToMany
  @JoinTable(
      name = "PERSON_GROUPS",
      joinColumns = @JoinColumn(name = "person_code"),
      inverseJoinColumns = @JoinColumn(name = "group_name"))
  private Set<Group> groups = new HashSet<>();

  @OneToMany(mappedBy = "author")
  private Set<Post> posts = new HashSet<>();

  Person() {
    // default constructor required by JPA
  }

  Person(String code, String name, String surname) {
    this.code = code;
    this.name = name;
    this.surname = surname;
  }

  String getCode() {
    return code;
  }

  String getName() {
    return name;
  }

  String getSurname() {
    return surname;
  }

  public Set<Person> getFriends() {
    return friends;
  }

  public void addFriend(Person p) {
    this.friends.add(p);
  }

  public Set<Group> getGroups() {
    return groups;
  }

  public void addGroup(Group g) {
    this.groups.add(g);
  }

  public Set<Post> getPosts() {
    return posts;
  }
}
