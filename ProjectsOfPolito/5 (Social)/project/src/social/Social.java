package social;

import java.util.ArrayList;
import java.util.Collection; //ADDED FOR R2, R3, R4, and R5
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Facade class for the social network system.
 * 
 */
public class Social {

  private final PersonRepository personRepository = new PersonRepository();
  private int postCounter = 0; //ADDED FOR R5 - counter for generating unique post IDs
  
  /**
   * Creates a new account for a person
   * 
   * @param code    nickname of the account
   * @param name    first name
   * @param surname last name
   * @throws PersonExistsException in case of duplicate code
   */
  public void addPerson(String code, String name, String surname) throws PersonExistsException {
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
   * 
   * @param code account code
   * @return the information of the person
   * @throws NoSuchCodeException if a person with that code does not exist
   */
  public String getPerson(String code) throws NoSuchCodeException {
    Optional<Person> personOpt = personRepository.findById(code); //ADDED FOR R1 - find person by code
    if (personOpt.isEmpty()) { //ADDED FOR R1 - check if person exists
      throw new NoSuchCodeException(); //ADDED FOR R1
    } //ADDED FOR R1
    Person person = personOpt.get(); //ADDED FOR R1
    return code + " " + person.getName() + " " + person.getSurname(); //ADDED FOR R1 - return formatted string
  }

  /**
   * Define a friendship relationship between two persons given their codes.
   * <p>
   * Friendship is bidirectional: if person A is adding as friend person B, that means
   * that person B automatically adds as friend person A.
   * 
   * @param codePerson1 first person code
   * @param codePerson2 second person code
   * @throws NoSuchCodeException in case either code does not exist
   */
  public void addFriendship(String codePerson1, String codePerson2)
      throws NoSuchCodeException {
    JPAUtil.executeInTransaction(() -> { //ADDED FOR R2 - wrap in transaction to avoid LazyInitializationException
      Optional<Person> person1Opt = personRepository.findById(codePerson1); //ADDED FOR R2 - find first person
      Optional<Person> person2Opt = personRepository.findById(codePerson2); //ADDED FOR R2 - find second person
      
      if (person1Opt.isEmpty() || person2Opt.isEmpty()) { //ADDED FOR R2 - check if both persons exist
        throw new NoSuchCodeException(); //ADDED FOR R2
      } //ADDED FOR R2
      
      Person person1 = person1Opt.get(); //ADDED FOR R2
      Person person2 = person2Opt.get(); //ADDED FOR R2
      
      person1.addFriend(person2); //ADDED FOR R2 - add bidirectional friendship
      
      personRepository.update(person1); //ADDED FOR R2 - save the updated relationship
      personRepository.update(person2); //ADDED FOR R2 - save the updated relationship
    }); //ADDED FOR R2
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
    return JPAUtil.executeInContext(() -> { //ADDED FOR R2 - wrap in context to allow lazy loading
      Optional<Person> personOpt = personRepository.findById(codePerson); //ADDED FOR R2 - find person
      if (personOpt.isEmpty()) { //ADDED FOR R2 - check if person exists
        throw new NoSuchCodeException(); //ADDED FOR R2
      } //ADDED FOR R2
      
      Person person = personOpt.get(); //ADDED FOR R2
      return person.getFriends().stream() //ADDED FOR R2 - get friends collection
                   .map(Person::getCode) //ADDED FOR R2 - map to codes
                   .collect(Collectors.toList()); //ADDED FOR R2 - collect to list
    }); //ADDED FOR R2
  }

  /**
   * Creates a new group with the given name
   * 
   * @param groupName name of the group
   * @throws GroupExistsException if a group with given name does not exist
   */
  public void addGroup(String groupName) throws GroupExistsException {
    if (personRepository.findGroupByName(groupName).isPresent()) { //ADDED FOR R3 - check if group already exists
      throw new GroupExistsException(); //ADDED FOR R3
    } //ADDED FOR R3
    
    SocialGroup group = new SocialGroup(groupName); //ADDED FOR R3 - create new group
    personRepository.saveGroup(group); //ADDED FOR R3 - save to db
  }

  /**
   * Deletes the group with the given name
   * 
   * @param groupName name of the group
   * @throws NoSuchCodeException if a group with given name does not exist
   */
  public void deleteGroup(String groupName) throws NoSuchCodeException {
    Optional<SocialGroup> groupOpt = personRepository.findGroupByName(groupName); //ADDED FOR R3 - find group
    if (groupOpt.isEmpty()) { //ADDED FOR R3 - check if group exists
      throw new NoSuchCodeException(); //ADDED FOR R3
    } //ADDED FOR R3
    
    personRepository.deleteGroup(groupOpt.get()); //ADDED FOR R3 - delete group
  }

  /**
   * Modifies the group name
   * 
   * @param groupName name of the group
   * @throws NoSuchCodeException if the original group name does not exist
   * @throws GroupExistsException if the target group name already exist
   */
  public void updateGroupName(String groupName, String newName) throws NoSuchCodeException, GroupExistsException {
    Optional<SocialGroup> groupOpt = personRepository.findGroupByName(groupName); //ADDED FOR R3 - find original group
    if (groupOpt.isEmpty()) { //ADDED FOR R3 - check if original group exists
      throw new NoSuchCodeException(); //ADDED FOR R3
    } //ADDED FOR R3
    
    if (personRepository.findGroupByName(newName).isPresent()) { //ADDED FOR R3 - check if new name already exists
      throw new GroupExistsException(); //ADDED FOR R3
    } //ADDED FOR R3
    
    SocialGroup group = groupOpt.get(); //ADDED FOR R3
    group.setName(newName); //ADDED FOR R3 - update name
    personRepository.updateGroup(group); //ADDED FOR R3 - save changes
  }

  /**
   * Retrieves the list of groups.
   * 
   * @return the collection of group names
   */
  public Collection<String> listOfGroups() {
    return personRepository.findAllGroups().stream() //ADDED FOR R3 - get all groups
                          .map(SocialGroup::getName) //ADDED FOR R3 - map to names
                          .collect(Collectors.toList()); //ADDED FOR R3 - collect to list
  }

  /**
   * Add a person to a group
   * 
   * @param codePerson person code
   * @param groupName  name of the group
   * @throws NoSuchCodeException in case the code or group name do not exist
   */
  public void addPersonToGroup(String codePerson, String groupName) throws NoSuchCodeException {
    JPAUtil.executeInTransaction(() -> { //ADDED FOR R3 - wrap in transaction to avoid LazyInitializationException
      Optional<Person> personOpt = personRepository.findById(codePerson); //ADDED FOR R3 - find person
      Optional<SocialGroup> groupOpt = personRepository.findGroupByName(groupName); //ADDED FOR R3 - find group
      
      if (personOpt.isEmpty() || groupOpt.isEmpty()) { //ADDED FOR R3 - check if both exist
        throw new NoSuchCodeException(); //ADDED FOR R3
      } //ADDED FOR R3
      
      SocialGroup group = groupOpt.get(); //ADDED FOR R3
      Person person = personOpt.get(); //ADDED FOR R3
      
      group.addMember(person); //ADDED FOR R3 - add person to group
      
      personRepository.updateGroup(group); //ADDED FOR R3 - save changes
    }); //ADDED FOR R3
  }

  /**
   * Retrieves the list of people on a group
   * 
   * @param groupName name of the group
   * @return collection of person codes
   */
  public Collection<String> listOfPeopleInGroup(String groupName) {
    return JPAUtil.executeInContext(() -> { //ADDED FOR R3 - wrap in context to allow lazy loading
      Optional<SocialGroup> groupOpt = personRepository.findGroupByName(groupName); //ADDED FOR R3 - find group
      if (groupOpt.isEmpty()) { //ADDED FOR R3 - if group doesn't exist return empty list
        return new ArrayList<String>(); //ADDED FOR R3
      } //ADDED FOR R3
      
      return groupOpt.get().getMembers().stream() //ADDED FOR R3 - get members
                     .map(Person::getCode) //ADDED FOR R3 - map to codes
                     .collect(Collectors.toList()); //ADDED FOR R3 - collect to list
    }); //ADDED FOR R3
  }

  /**
   * Retrieves the code of the person having the largest
   * group of friends
   * 
   * @return the code of the person
   */
  public String personWithLargestNumberOfFriends() {
    return JPAUtil.executeInContext(() -> { //ADDED FOR R4 - wrap in context to allow lazy loading
      List<Person> allPersons = personRepository.findAll(); //ADDED FOR R4 - get all persons
      
      Person personWithMostFriends = null; //ADDED FOR R4
      int maxFriends = 0; //ADDED FOR R4
      
      for (Person person : allPersons) { //ADDED FOR R4 - iterate through all persons
        int friendCount = person.getFriends().size(); //ADDED FOR R4 - count friends
        if (friendCount > maxFriends) { //ADDED FOR R4 - check if this person has more friends
          maxFriends = friendCount; //ADDED FOR R4
          personWithMostFriends = person; //ADDED FOR R4
        } //ADDED FOR R4
      } //ADDED FOR R4
      
      return personWithMostFriends != null ? personWithMostFriends.getCode() : null; //ADDED FOR R4 - return code or null
    }); //ADDED FOR R4
  }

  /**
   * Find the name of group with the largest number of members
   * 
   * @return the name of the group
   */
  public String largestGroup() {
    return JPAUtil.executeInContext(() -> { //ADDED FOR R4 - wrap in context to allow lazy loading
      List<SocialGroup> allGroups = personRepository.findAllGroups(); //ADDED FOR R4 - get all groups
      
      SocialGroup largestGroup = null; //ADDED FOR R4
      int maxMembers = 0; //ADDED FOR R4
      
      for (SocialGroup group : allGroups) { //ADDED FOR R4 - iterate through all groups
        int memberCount = group.getMembers().size(); //ADDED FOR R4 - count members
        if (memberCount > maxMembers) { //ADDED FOR R4 - check if this group has more members
          maxMembers = memberCount; //ADDED FOR R4
          largestGroup = group; //ADDED FOR R4
        } //ADDED FOR R4
      } //ADDED FOR R4
      
      return largestGroup != null ? largestGroup.getName() : null; //ADDED FOR R4 - return name or null
    }); //ADDED FOR R4
  }

  /**
   * Find the code of the person that is member of
   * the largest number of groups
   * 
   * @return the code of the person
   */
  public String personInLargestNumberOfGroups() {
    return JPAUtil.executeInContext(() -> { //ADDED FOR R4 - wrap in context to allow lazy loading
      List<Person> allPersons = personRepository.findAll(); //ADDED FOR R4 - get all persons
      
      Person personInMostGroups = null; //ADDED FOR R4
      int maxGroups = 0; //ADDED FOR R4
      
      for (Person person : allPersons) { //ADDED FOR R4 - iterate through all persons
        int groupCount = person.getGroups().size(); //ADDED FOR R4 - count groups
        if (groupCount > maxGroups) { //ADDED FOR R4 - check if this person is in more groups
          maxGroups = groupCount; //ADDED FOR R4
          personInMostGroups = person; //ADDED FOR R4
        } //ADDED FOR R4
      } //ADDED FOR R4
      
      return personInMostGroups != null ? personInMostGroups.getCode() : null; //ADDED FOR R4 - return code or null
    }); //ADDED FOR R4
  }

  // R5

  /**
   * add a new post by a given account
   * 
   * @param authorCode the id of the post author
   * @param text   the content of the post
   * @return a unique id of the post
   */
  public String post(String authorCode, String text) {
    Optional<Person> authorOpt = personRepository.findById(authorCode); //ADDED FOR R5 - find author
    if (authorOpt.isEmpty()) { //ADDED FOR R5 - if author doesn't exist return null
      return null; //ADDED FOR R5
    } //ADDED FOR R5
    
    Person author = authorOpt.get(); //ADDED FOR R5
    long timestamp = System.currentTimeMillis(); //ADDED FOR R5 - get current time
    String postId = "p" + (++postCounter); //ADDED FOR R5 - generate unique ID with letters and digits
    
    Post post = new Post(postId, author, text, timestamp); //ADDED FOR R5 - create post
    personRepository.savePost(post); //ADDED FOR R5 - save to db
    
    return postId; //ADDED FOR R5 - return the post ID
  }

  /**
   * retrieves the content of the given post
   * 
   * @param pid    the id of the post
   * @return the content of the post
   */
  public String getPostContent(String pid) {
    Optional<Post> postOpt = personRepository.findPostById(pid); //ADDED FOR R5 - find post
    if (postOpt.isEmpty()) { //ADDED FOR R5 - if post doesn't exist return null
      return null; //ADDED FOR R5
    } //ADDED FOR R5
    
    return postOpt.get().getContent(); //ADDED FOR R5 - return content
  }

  /**
   * retrieves the timestamp of the given post
   * 
   * @param pid    the id of the post
   * @return the timestamp of the post
   */
  public long getTimestamp(String pid) {
    Optional<Post> postOpt = personRepository.findPostById(pid); //ADDED FOR R5 - find post
    if (postOpt.isEmpty()) { //ADDED FOR R5 - if post doesn't exist return -1
      return -1; //ADDED FOR R5
    } //ADDED FOR R5
    
    return postOpt.get().getTimestamp(); //ADDED FOR R5 - return timestamp
  }

  /**
   * returns the list of post of a given author paginated
   * 
   * @param author     author of the post
   * @param pageNo     page number (starting at 1)
   * @param pageLength page length
   * @return the list of posts id
   */
  public List<String> getPaginatedUserPosts(String author, int pageNo, int pageLength) {
    List<Post> allPosts = personRepository.findPostsByAuthorOrderByTimestampDesc(author); //ADDED FOR R5 - get all posts by author sorted by timestamp
    
    int startIndex = (pageNo - 1) * pageLength; //ADDED FOR R5 - calculate start index for pagination
    int endIndex = Math.min(startIndex + pageLength, allPosts.size()); //ADDED FOR R5 - calculate end index
    
    if (startIndex >= allPosts.size()) { //ADDED FOR R5 - if page is out of range return empty list
      return new ArrayList<>(); //ADDED FOR R5
    } //ADDED FOR R5
    
    return allPosts.subList(startIndex, endIndex).stream() //ADDED FOR R5 - get posts for this page
                   .map(Post::getId) //ADDED FOR R5 - map to IDs
                   .collect(Collectors.toList()); //ADDED FOR R5 - collect to list
  }

  /**
   * returns the paginated list of post of friends.
   * The returned list contains the author and the id of a post separated by ":"
   * 
   * @param author     author of the post
   * @param pageNo     page number (starting at 1)
   * @param pageLength page length
   * @return the list of posts key elements
   */
  public List<String> getPaginatedFriendPosts(String author, int pageNo, int pageLength) {
    return JPAUtil.executeInContext(() -> { //ADDED FOR R5 - wrap in context to allow lazy loading
      List<Post> allPosts = personRepository.findPostsByFriendsOrderByTimestampDesc(author); //ADDED FOR R5 - get all posts by friends sorted by timestamp
      
      int startIndex = (pageNo - 1) * pageLength; //ADDED FOR R5 - calculate start index for pagination
      int endIndex = Math.min(startIndex + pageLength, allPosts.size()); //ADDED FOR R5 - calculate end index
      
      if (startIndex >= allPosts.size()) { //ADDED FOR R5 - if page is out of range return empty list
        return new ArrayList<String>(); //ADDED FOR R5
      } //ADDED FOR R5
      
      return allPosts.subList(startIndex, endIndex).stream() //ADDED FOR R5 - get posts for this page
                     .map(post -> post.getAuthor().getCode() + ":" + post.getId()) //ADDED FOR R5 - format as "authorCode:postId"
                     .collect(Collectors.toList()); //ADDED FOR R5 - collect to list
    }); //ADDED FOR R5
  }

}