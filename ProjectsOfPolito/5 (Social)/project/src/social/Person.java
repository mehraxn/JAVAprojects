package social;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List; //ADDED FOR R2
import java.util.Set; //ADDED FOR R2

import jakarta.persistence.CascadeType; //ADDED FOR R2
import jakarta.persistence.Entity; //ADDED FOR R5
import jakarta.persistence.Id; //ADDED FOR R2
import jakarta.persistence.JoinColumn; //ADDED FOR R5
import jakarta.persistence.JoinTable; //ADDED FOR R3
import jakarta.persistence.ManyToMany; //ADDED FOR R2
import jakarta.persistence.ManyToOne; //ADDED FOR R2
import jakarta.persistence.OneToMany; //ADDED FOR R5
import jakarta.persistence.Table; //ADDED FOR R5

@Entity
class Person {
  @Id
  private String code;
  private String name;
  private String surname;

  @ManyToMany //ADDED FOR R2 - bidirectional friendship
  @JoinTable( //ADDED FOR R2 - define the join table for friendship
    name = "friendships", //ADDED FOR R2
    joinColumns = @JoinColumn(name = "person_code"), //ADDED FOR R2
    inverseJoinColumns = @JoinColumn(name = "friend_code") //ADDED FOR R2
  ) //ADDED FOR R2
  private Set<Person> friends = new HashSet<>(); //ADDED FOR R2

  @ManyToMany(mappedBy = "members") //ADDED FOR R3 - person can be in many groups
  private Set<SocialGroup> groups = new HashSet<>(); //ADDED FOR R3

  @OneToMany(mappedBy = "author", cascade = CascadeType.ALL) //ADDED FOR R5 - person can have many posts
  private List<Post> posts = new ArrayList<>(); //ADDED FOR R5

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

  Set<Person> getFriends() { //ADDED FOR R2
    return friends; //ADDED FOR R2
  } //ADDED FOR R2

  void addFriend(Person friend) { //ADDED FOR R2 - add a friend bidirectionally
    this.friends.add(friend); //ADDED FOR R2
    friend.friends.add(this); //ADDED FOR R2 - ensure bidirectional relationship
  } //ADDED FOR R2

  Set<SocialGroup> getGroups() { //ADDED FOR R3
    return groups; //ADDED FOR R3
  } //ADDED FOR R3

  List<Post> getPosts() { //ADDED FOR R5
    return posts; //ADDED FOR R5
  } //ADDED FOR R5

  //....
}

//ADDED FOR R3 - Group entity embedded in Person.java file, renamed to SocialGroup to avoid SQL reserved word
@Entity //ADDED FOR R3
@Table(name = "social_group") //ADDED FOR R3 - avoid "Group" reserved word in SQL
class SocialGroup { //ADDED FOR R3
  @Id //ADDED FOR R3
  private String name; //ADDED FOR R3

  @ManyToMany //ADDED FOR R3 - group can have many members
  @JoinTable( //ADDED FOR R3
    name = "group_members", //ADDED FOR R3
    joinColumns = @JoinColumn(name = "group_name"), //ADDED FOR R3
    inverseJoinColumns = @JoinColumn(name = "person_code") //ADDED FOR R3
  ) //ADDED FOR R3
  private Set<Person> members = new HashSet<>(); //ADDED FOR R3

  SocialGroup() { //ADDED FOR R3 - default constructor for JPA
    // default constructor is needed by JPA //ADDED FOR R3
  } //ADDED FOR R3

  SocialGroup(String name) { //ADDED FOR R3
    this.name = name; //ADDED FOR R3
  } //ADDED FOR R3

  String getName() { //ADDED FOR R3
    return name; //ADDED FOR R3
  } //ADDED FOR R3

  void setName(String name) { //ADDED FOR R3 - needed for updateGroupName
    this.name = name; //ADDED FOR R3
  } //ADDED FOR R3

  Set<Person> getMembers() { //ADDED FOR R3
    return members; //ADDED FOR R3
  } //ADDED FOR R3

  void addMember(Person person) { //ADDED FOR R3
    this.members.add(person); //ADDED FOR R3
    person.getGroups().add(this); //ADDED FOR R3 - maintain bidirectional relationship
  } //ADDED FOR R3
} //ADDED FOR R3

//ADDED FOR R5 - Post entity embedded in Person.java file
@Entity //ADDED FOR R5
class Post { //ADDED FOR R5
  @Id //ADDED FOR R5
  private String id; //ADDED FOR R5

  @ManyToOne //ADDED FOR R5 - each post has one author
  @JoinColumn(name = "author_code") //ADDED FOR R5
  private Person author; //ADDED FOR R5

  private String content; //ADDED FOR R5
  private long timestamp; //ADDED FOR R5

  Post() { //ADDED FOR R5 - default constructor for JPA
    // default constructor is needed by JPA //ADDED FOR R5
  } //ADDED FOR R5

  Post(String id, Person author, String content, long timestamp) { //ADDED FOR R5
    this.id = id; //ADDED FOR R5
    this.author = author; //ADDED FOR R5
    this.content = content; //ADDED FOR R5
    this.timestamp = timestamp; //ADDED FOR R5
  } //ADDED FOR R5

  String getId() { //ADDED FOR R5
    return id; //ADDED FOR R5
  } //ADDED FOR R5

  Person getAuthor() { //ADDED FOR R5
    return author; //ADDED FOR R5
  } //ADDED FOR R5

  String getContent() { //ADDED FOR R5
    return content; //ADDED FOR R5
  } //ADDED FOR R5

  long getTimestamp() { //ADDED FOR R5
    return timestamp; //ADDED FOR R5
  } //ADDED FOR R5
} //ADDED FOR R5