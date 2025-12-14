package social;

import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.Entity; // ADDED FOR R2
import jakarta.persistence.Id; // ADDED FOR R5
import jakarta.persistence.ManyToMany; // ADDED FOR R2
import jakarta.persistence.OneToMany; // ADDED FOR R2

@Entity
class Person {
  @Id
  private String code;
  private String name;
  private String surname;

  @ManyToMany // ADDED FOR R2
  private Set<Person> friends = new HashSet<>(); // ADDED FOR R2

  @ManyToMany // ADDED FOR R3
  private Set<Group> groups = new HashSet<>(); // ADDED FOR R3

  @OneToMany(mappedBy = "author") // ADDED FOR R5
  private Set<Post> posts = new HashSet<>(); // ADDED FOR R5

  Person() {
    // default constructor is needed by JPA
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

  //....

  public Set<Person> getFriends() { // ADDED FOR R2
    return friends; // ADDED FOR R2
  } // ADDED FOR R2

  public void addFriend(Person p) { // ADDED FOR R2
    this.friends.add(p); // ADDED FOR R2
  } // ADDED FOR R2

  public Set<Group> getGroups() { // ADDED FOR R3
    return groups; // ADDED FOR R3
  } // ADDED FOR R3

  public void addGroup(Group g) { // ADDED FOR R3
    this.groups.add(g); // ADDED FOR R3
  } // ADDED FOR R3

  public Set<Post> getPosts() { // ADDED FOR R5
    return posts; // ADDED FOR R5
  } // ADDED FOR R5
}