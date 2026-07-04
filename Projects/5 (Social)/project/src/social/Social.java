package social;

import java.util.ArrayList;
import java.util.Collection; // ADDED FOR R3
import java.util.HashSet; // ADDED FOR R3
import java.util.List; // ADDED FOR R3
import java.util.Set; // ADDED FOR R5
import java.util.UUID; // ADDED FOR R3

import jakarta.persistence.Entity; // ADDED FOR R5
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

/**
 * Facade class for the social network system.
 * */
public class Social {

  private final PersonRepository personRepository = new PersonRepository();
  // We use GenericRepository directly since we cannot create new files // ADDED FOR R3
  private final GenericRepository<Group, String> groupRepository = new GenericRepository<>(Group.class); // ADDED FOR R3
  private final GenericRepository<Post, String> postRepository = new GenericRepository<>(Post.class); // ADDED FOR R5
  
  /**
   * Creates a new account for a person
   * * @param code    nickname of the account
   * @param name    first name
   * @param surname last name
   * @throws PersonExistsException in case of duplicate code
   */
  public void addPerson(String code, String name, String surname) throws PersonExistsException {
    // Simple checks do not need transaction wrapper if they are single operations
    if (personRepository.findById(code).isPresent()){    // check if db already contains the code
        throw new PersonExistsException();
    }
    Person person = new Person(code, name, surname);    // create the person as a POJO
    personRepository.save(person);                      // save it to db
  }

  /**
   * Retrieves information about the person given their account code.
   * The info consists in name and surname of the person, in order, separated by
   * blanks.
   * * @param code account code
   * @return the information of the person
   * @throws NoSuchCodeException if a person with that code does not exist
   */
  public String getPerson(String code) throws NoSuchCodeException {
    Person p = personRepository.findById(code).orElse(null); // ADDED FOR R1
    if (p == null) throw new NoSuchCodeException(); // ADDED FOR R1
    return p.getCode() + " " + p.getName() + " " + p.getSurname(); // ADDED FOR R1
  }

  /**
   * Define a friendship relationship between two persons given their codes.
   * <p>
   * Friendship is bidirectional: if person A is adding as friend person B, that means
   * that person B automatically adds as friend person A.
   * * @param codePerson1 first person code
   * @param codePerson2 second person code
   * @throws NoSuchCodeException in case either code does not exist
   */
  public void addFriendship(String codePerson1, String codePerson2)
      throws NoSuchCodeException {
    try { // ADDED FOR R2
        JPAUtil.executeInTransaction(() -> { // ADDED FOR R2
            Person p1 = personRepository.findById(codePerson1).orElse(null); // ADDED FOR R2
            Person p2 = personRepository.findById(codePerson2).orElse(null); // ADDED FOR R2
            if (p1 == null || p2 == null) throw new NoSuchCodeException(); // ADDED FOR R2
            p1.addFriend(p2); // ADDED FOR R2
            p2.addFriend(p1); // ADDED FOR R2
            personRepository.update(p1); // ADDED FOR R2
            personRepository.update(p2); // ADDED FOR R2
        }); // ADDED FOR R2
    } catch (NoSuchCodeException e) { // ADDED FOR R2
        throw e; // ADDED FOR R2
    } catch (Exception e) { // ADDED FOR R2
        throw new RuntimeException(e); // ADDED FOR R2
    } // ADDED FOR R2
  }

  /**
   * Retrieve the collection of their friends given the code of a person.
   *
   * @param codePerson code of the person
   * @return the list of person codes
   * @throws NoSuchCodeException in case the code does not exist
   */
  public Collection<String> listOfFriends(String codePerson)
      throws NoSuchCodeException {
    try { // ADDED FOR R2
        return JPAUtil.executeInContext(() -> { // ADDED FOR R2
            Person p = personRepository.findById(codePerson).orElse(null); // ADDED FOR R2
            if (p == null) throw new NoSuchCodeException(); // ADDED FOR R2
            List<String> friendsCodes = new ArrayList<>(); // ADDED FOR R2
            for (Person friend : p.getFriends()) { // ADDED FOR R2
                friendsCodes.add(friend.getCode()); // ADDED FOR R2
            } // ADDED FOR R2
            return friendsCodes; // ADDED FOR R2
        }); // ADDED FOR R2
    } catch (NoSuchCodeException e) { // ADDED FOR R2
        throw e; // ADDED FOR R2
    } catch (Exception e) { // ADDED FOR R2
        throw new RuntimeException(e); // ADDED FOR R2
    } // ADDED FOR R2
  }

  /**
   * Creates a new group with the given name
   * * @param groupName name of the group
   * @throws GroupExistsException if a group with given name does not exist
   */
  public void addGroup(String groupName) throws GroupExistsException {
    try { // ADDED FOR R3
        JPAUtil.executeInTransaction(() -> { // ADDED FOR R3
            if (groupRepository.findById(groupName).isPresent()) { // ADDED FOR R3
                throw new GroupExistsException(); // ADDED FOR R3
            } // ADDED FOR R3
            Group g = new Group(groupName); // ADDED FOR R3
            groupRepository.save(g); // ADDED FOR R3
        }); // ADDED FOR R3
    } catch (GroupExistsException e) { // ADDED FOR R3
        throw e; // ADDED FOR R3
    } catch (Exception e) { // ADDED FOR R3
        throw new RuntimeException(e); // ADDED FOR R3
    } // ADDED FOR R3
  }

  /**
   * Deletes the group with the given name
   * * @param groupName name of the group
   * @throws NoSuchCodeException if a group with given name does not exist
   */
  public void deleteGroup(String groupName) throws NoSuchCodeException {
    try { // ADDED FOR R3
        JPAUtil.executeInTransaction(() -> { // ADDED FOR R3
            Group g = groupRepository.findById(groupName).orElse(null); // ADDED FOR R3
            if (g == null) throw new NoSuchCodeException(); // ADDED FOR R3
            groupRepository.delete(g); // ADDED FOR R3
        }); // ADDED FOR R3
    } catch (NoSuchCodeException e) { // ADDED FOR R3
        throw e; // ADDED FOR R3
    } catch (Exception e) { // ADDED FOR R3
        throw new RuntimeException(e); // ADDED FOR R3
    } // ADDED FOR R3
  }

  /**
   * Modifies the group name
   * * @param groupName name of the group
   * @throws NoSuchCodeException if the original group name does not exist
   * @throws GroupExistsException if the target group name already exist
   */
  public void updateGroupName(String groupName, String newName) throws NoSuchCodeException, GroupExistsException {
    try { // ADDED FOR R3
        JPAUtil.executeInTransaction(() -> { // ADDED FOR R3
            Group g = groupRepository.findById(groupName).orElse(null); // ADDED FOR R3
            if (g == null) throw new NoSuchCodeException(); // ADDED FOR R3
            if (groupRepository.findById(newName).isPresent()) throw new GroupExistsException(); // ADDED FOR R3
            Group newGroup = new Group(newName); // ADDED FOR R3
            groupRepository.save(newGroup); // ADDED FOR R3
            // We have to move members because we are creating a new group entity // ADDED FOR R3
            for(Person member : g.getMembers()) { // ADDED FOR R3
                newGroup.getMembers().add(member); // ADDED FOR R3
                member.getGroups().remove(g); // ADDED FOR R3
                member.getGroups().add(newGroup); // ADDED FOR R3
                personRepository.update(member); // ADDED FOR R3
            } // ADDED FOR R3
            groupRepository.delete(g); // ADDED FOR R3
        }); // ADDED FOR R3
    } catch (NoSuchCodeException e) { // ADDED FOR R3
        throw e; // ADDED FOR R3
    } catch (GroupExistsException e) { // ADDED FOR R3
        throw e; // ADDED FOR R3
    } catch (Exception e) { // ADDED FOR R3
        // If it's another runtime exception, just rethrow it // ADDED FOR R3
        throw new RuntimeException(e); // ADDED FOR R3
    } // ADDED FOR R3
  }

  /**
   * Retrieves the list of groups.
   * * @return the collection of group names
   */
  public Collection<String> listOfGroups() {
    try { // ADDED FOR R3
        return JPAUtil.executeInContext(() -> { // ADDED FOR R3
            List<String> names = new ArrayList<>(); // ADDED FOR R3
            for (Group g : groupRepository.findAll()) { // ADDED FOR R3
                names.add(g.getName()); // ADDED FOR R3
            } // ADDED FOR R3
            return names; // ADDED FOR R3
        }); // ADDED FOR R3
    } catch (Exception e) { // ADDED FOR R3
        return new ArrayList<>(); // ADDED FOR R3
    } // ADDED FOR R3
  }

  /**
   * Add a person to a group
   * * @param codePerson person code
   * @param groupName  name of the group
   * @throws NoSuchCodeException in case the code or group name do not exist
   */
  public void addPersonToGroup(String codePerson, String groupName) throws NoSuchCodeException {
    try { // ADDED FOR R3
        JPAUtil.executeInTransaction(() -> { // ADDED FOR R3
            Person p = personRepository.findById(codePerson).orElse(null); // ADDED FOR R3
            Group g = groupRepository.findById(groupName).orElse(null); // ADDED FOR R3
            if (p == null || g == null) throw new NoSuchCodeException(); // ADDED FOR R3
            p.addGroup(g); // ADDED FOR R3
            personRepository.update(p); // ADDED FOR R3
        }); // ADDED FOR R3
    } catch (NoSuchCodeException e) { // ADDED FOR R3
        throw e; // ADDED FOR R3
    } catch (Exception e) { // ADDED FOR R3
        throw new RuntimeException(e); // ADDED FOR R3
    } // ADDED FOR R3
  }

  /**
   * Retrieves the list of people on a group
   * * @param groupName name of the group
   * @return collection of person codes
   */
  public Collection<String> listOfPeopleInGroup(String groupName) {
    try { // ADDED FOR R3
        return JPAUtil.executeInContext(() -> { // ADDED FOR R3
            Group g = groupRepository.findById(groupName).orElse(null); // ADDED FOR R3
            List<String> codes = new ArrayList<>(); // ADDED FOR R3
            if (g == null) return codes; // ADDED FOR R3
            for (Person p : g.getMembers()) { // ADDED FOR R3
                codes.add(p.getCode()); // ADDED FOR R3
            } // ADDED FOR R3
            return codes; // ADDED FOR R3
        }); // ADDED FOR R3
    } catch (Exception e) { // ADDED FOR R3
        return new ArrayList<>(); // ADDED FOR R3
    } // ADDED FOR R3
  }

  /**
   * Retrieves the code of the person having the largest
   * group of friends
   * * @return the code of the person
   */
  public String personWithLargestNumberOfFriends() {
    try { // ADDED FOR R4
        return JPAUtil.executeInContext(() -> { // ADDED FOR R4
            String maxPersonCode = null; // ADDED FOR R4
            int maxFriends = -1; // ADDED FOR R4
            for(Person p : personRepository.findAll()) { // ADDED FOR R4
                if(p.getFriends().size() > maxFriends) { // ADDED FOR R4
                    maxFriends = p.getFriends().size(); // ADDED FOR R4
                    maxPersonCode = p.getCode(); // ADDED FOR R4
                } // ADDED FOR R4
            } // ADDED FOR R4
            return maxPersonCode; // ADDED FOR R4
        }); // ADDED FOR R4
    } catch (Exception e) { // ADDED FOR R4
        return null; // ADDED FOR R4
    } // ADDED FOR R4
  }

  /**
   * Find the name of group with the largest number of members
   * * @return the name of the group
   */
  public String largestGroup() {
    try { // ADDED FOR R4
        return JPAUtil.executeInContext(() -> { // ADDED FOR R4
            String maxGroup = null; // ADDED FOR R4
            int maxMembers = -1; // ADDED FOR R4
            for(Group g : groupRepository.findAll()) { // ADDED FOR R4
                if(g.getMembers().size() > maxMembers) { // ADDED FOR R4
                    maxMembers = g.getMembers().size(); // ADDED FOR R4
                    maxGroup = g.getName(); // ADDED FOR R4
                } // ADDED FOR R4
            } // ADDED FOR R4
            return maxGroup; // ADDED FOR R4
        }); // ADDED FOR R4
    } catch (Exception e) { // ADDED FOR R4
        return null; // ADDED FOR R4
    } // ADDED FOR R4
  }

  /**
   * Find the code of the person that is member of
   * the largest number of groups
   * * @return the code of the person
   */
  public String personInLargestNumberOfGroups() {
    try { // ADDED FOR R4
        return JPAUtil.executeInContext(() -> { // ADDED FOR R4
            String maxPerson = null; // ADDED FOR R4
            int maxGroups = -1; // ADDED FOR R4
            for(Person p : personRepository.findAll()) { // ADDED FOR R4
                if (p.getGroups().size() > maxGroups) { // ADDED FOR R4
                    maxGroups = p.getGroups().size(); // ADDED FOR R4
                    maxPerson = p.getCode(); // ADDED FOR R4
                } // ADDED FOR R4
            } // ADDED FOR R4
            return maxPerson; // ADDED FOR R4
        }); // ADDED FOR R4
    } catch (Exception e) { // ADDED FOR R4
        return null; // ADDED FOR R4
    } // ADDED FOR R4
  }

  // R5

  /**
   * add a new post by a given account
   * * @param authorCode the id of the post author
   * @param text   the content of the post
   * @return a unique id of the post
   */
  public String post(String authorCode, String text) {
    try { // ADDED FOR R5
        String[] result = new String[1]; // array to store result from inside lambda // ADDED FOR R5
        JPAUtil.executeInTransaction(() -> { // ADDED FOR R5
            Person p = personRepository.findById(authorCode).orElse(null); // ADDED FOR R5
            if (p != null) { // ADDED FOR R5
                String id = UUID.randomUUID().toString().replaceAll("-", ""); // ADDED FOR R5
                Post post = new Post(id, text, System.currentTimeMillis(), p); // ADDED FOR R5
                postRepository.save(post); // ADDED FOR R5
                result[0] = id; // ADDED FOR R5
            } // ADDED FOR R5
        }); // ADDED FOR R5
        return result[0]; // ADDED FOR R5
    } catch (Exception e) { // ADDED FOR R5
        return null; // ADDED FOR R5
    } // ADDED FOR R5
  }

  /**
   * retrieves the content of the given post
   * * @param pid    the id of the post
   * @return the content of the post
   */
  public String getPostContent(String pid) {
    try { // ADDED FOR R5
        return JPAUtil.executeInContext(() -> { // ADDED FOR R5
            Post p = postRepository.findById(pid).orElse(null); // ADDED FOR R5
            if (p != null) return p.getText(); // ADDED FOR R5
            return null; // ADDED FOR R5
        }); // ADDED FOR R5
    } catch (Exception e) { // ADDED FOR R5
        return null; // ADDED FOR R5
    } // ADDED FOR R5
  }

  /**
   * retrieves the timestamp of the given post
   * * @param pid    the id of the post
   * @return the timestamp of the post
   */
  public long getTimestamp(String pid) {
    try { // ADDED FOR R5
        return JPAUtil.executeInContext(() -> { // ADDED FOR R5
            Post p = postRepository.findById(pid).orElse(null); // ADDED FOR R5
            if (p != null) return p.getTimestamp(); // ADDED FOR R5
            return -1L; // ADDED FOR R5
        }); // ADDED FOR R5
    } catch (Exception e) { // ADDED FOR R5
        return -1L; // ADDED FOR R5
    } // ADDED FOR R5
  }

  /**
   * returns the list of post of a given author paginated
   * * @param author     author of the post
   * @param pageNo     page number (starting at 1)
   * @param pageLength page length
   * @return the list of posts id
   */
  public List<String> getPaginatedUserPosts(String author, int pageNo, int pageLength) {
    try { // ADDED FOR R5
        return JPAUtil.executeInContext(() -> { // ADDED FOR R5
            List<Post> allPosts = postRepository.findAll(); // ADDED FOR R5
            List<Post> userPosts = new ArrayList<>(); // ADDED FOR R5
            for(Post p : allPosts) { // ADDED FOR R5
                if(p.getAuthor().getCode().equals(author)) { // ADDED FOR R5
                    userPosts.add(p); // ADDED FOR R5
                } // ADDED FOR R5
            } // ADDED FOR R5
            userPosts.sort((p1, p2) -> Long.compare(p2.getTimestamp(), p1.getTimestamp())); // ADDED FOR R5
            List<String> result = new ArrayList<>(); // ADDED FOR R5
            int start = (pageNo - 1) * pageLength; // ADDED FOR R5
            int end = Math.min(start + pageLength, userPosts.size()); // ADDED FOR R5
            if(start >= userPosts.size()) return result; // ADDED FOR R5
            for(int i = start; i < end; i++) { // ADDED FOR R5
                result.add(userPosts.get(i).getId()); // ADDED FOR R5
            } // ADDED FOR R5
            return result; // ADDED FOR R5
        }); // ADDED FOR R5
    } catch (Exception e) { // ADDED FOR R5
        return new ArrayList<>(); // ADDED FOR R5
    } // ADDED FOR R5
  }

  /**
   * returns the paginated list of post of friends.
   * The returned list contains the author and the id of a post separated by ":"
   * * @param author     author of the post
   * @param pageNo     page number (starting at 1)
   * @param pageLength page length
   * @return the list of posts key elements
   */
  public List<String> getPaginatedFriendPosts(String author, int pageNo, int pageLength) {
    try { // ADDED FOR R5
        return JPAUtil.executeInContext(() -> { // ADDED FOR R5
            Person p = personRepository.findById(author).orElse(null); // ADDED FOR R5
            List<Post> friendPosts = new ArrayList<>(); // ADDED FOR R5
            if (p != null) { // ADDED FOR R5
                for (Person friend : p.getFriends()) { // ADDED FOR R5
                    for (Post post : friend.getPosts()) { // ADDED FOR R5
                        friendPosts.add(post); // ADDED FOR R5
                    } // ADDED FOR R5
                } // ADDED FOR R5
            } // ADDED FOR R5
            friendPosts.sort((p1, p2) -> Long.compare(p2.getTimestamp(), p1.getTimestamp())); // ADDED FOR R5
            List<String> result = new ArrayList<>(); // ADDED FOR R5
            int start = (pageNo - 1) * pageLength; // ADDED FOR R5
            int end = Math.min(start + pageLength, friendPosts.size()); // ADDED FOR R5
            if (start >= friendPosts.size()) return result; // ADDED FOR R5
            for (int i = start; i < end; i++) { // ADDED FOR R5
                Post post = friendPosts.get(i); // ADDED FOR R5
                result.add(post.getAuthor().getCode() + ":" + post.getId()); // ADDED FOR R5
            } // ADDED FOR R5
            return result; // ADDED FOR R5
        }); // ADDED FOR R5
    } catch (Exception e) { // ADDED FOR R5
        return new ArrayList<>(); // ADDED FOR R5
    } // ADDED FOR R5
  }

}

// Extra classes added to the bottom of the file to avoid creating new files // ADDED FOR R3
@Entity // ADDED FOR R3
@Table(name = "SocialGroup") // ADDED FOR R3
class Group { // ADDED FOR R3

    @Id // ADDED FOR R3
    private String name; // ADDED FOR R3

    @ManyToMany(mappedBy = "groups") // ADDED FOR R3
    private Set<Person> members = new HashSet<>(); // ADDED FOR R3

    public Group() {} // ADDED FOR R3

    public Group(String name) { // ADDED FOR R3
        this.name = name; // ADDED FOR R3
    } // ADDED FOR R3

    public String getName() { // ADDED FOR R3
        return name; // ADDED FOR R3
    } // ADDED FOR R3

    public Set<Person> getMembers() { // ADDED FOR R3
        return members; // ADDED FOR R3
    } // ADDED FOR R3
} // ADDED FOR R3

@Entity // ADDED FOR R5
class Post { // ADDED FOR R5
    
    @Id // ADDED FOR R5
    private String id; // ADDED FOR R5
    
    private String text; // ADDED FOR R5
    private long timestamp; // ADDED FOR R5

    @ManyToOne // ADDED FOR R5
    private Person author; // ADDED FOR R5

    public Post() {} // ADDED FOR R5

    public Post(String id, String text, long timestamp, Person author) { // ADDED FOR R5
        this.id = id; // ADDED FOR R5
        this.text = text; // ADDED FOR R5
        this.timestamp = timestamp; // ADDED FOR R5
        this.author = author; // ADDED FOR R5
    } // ADDED FOR R5

    public String getId() { // ADDED FOR R5
        return id; // ADDED FOR R5
    } // ADDED FOR R5

    public String getText() { // ADDED FOR R5
        return text; // ADDED FOR R5
    } // ADDED FOR R5

    public long getTimestamp() { // ADDED FOR R5
        return timestamp; // ADDED FOR R5
    } // ADDED FOR R5

    public Person getAuthor() { // ADDED FOR R5
        return author; // ADDED FOR R5
    } // ADDED FOR R5
} // ADDED FOR R5