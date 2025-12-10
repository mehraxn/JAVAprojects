package social;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

/**
 * Facade class for the social network system.
 * */
public class Social {

  private final PersonRepository personRepository = new PersonRepository();
  
  /**
   * Creates a new account for a person
   * * @param code    nickname of the account
   * @param name    first name
   * @param surname last name
   * @throws PersonExistsException in case of duplicate code
   */
  public void addPerson(String code, String name, String surname) throws PersonExistsException {
    Optional<Person> result = personRepository.findById(code); //TASKF FOR R1
    
    boolean exists = result.isPresent(); //TASKF FOR R1
    if (exists == true){ //TASKF FOR R1
        throw new PersonExistsException(); //TASKF FOR R1
    } //TASKF FOR R1

    Person newPerson = new Person(code, name, surname); //TASKF FOR R1
    personRepository.save(newPerson); //TASKF FOR R1
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
    Optional<Person> result = personRepository.findById(code); //TASKF FOR R1
    
    if (result.isPresent()) { //TASKF FOR R1
        Person foundPerson = result.get(); //TASKF FOR R1
        String id = foundPerson.getCode(); //TASKF FOR R1
        String n = foundPerson.getName(); //TASKF FOR R1
        String s = foundPerson.getSurname(); //TASKF FOR R1
        return id + " " + n + " " + s; //TASKF FOR R1
    } else { //TASKF FOR R1
        throw new NoSuchCodeException(); //TASKF FOR R1
    } //TASKF FOR R1
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
    // TO BE IMPLEMENTED
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
    return null; // TO BE IMPLEMENTED
  }

  /**
   * Creates a new group with the given name
   * * @param groupName name of the group
   * @throws GroupExistsException if a group with given name does not exist
   */
  public void addGroup(String groupName) throws GroupExistsException {
    // TO BE IMPLEMENTED
  }

  /**
   * Deletes the group with the given name
   * * @param groupName name of the group
   * @throws NoSuchCodeException if a group with given name does not exist
   */
  public void deleteGroup(String groupName) throws NoSuchCodeException {
    // TO BE IMPLEMENTED
  }

  /**
   * Modifies the group name
   * * @param groupName name of the group
   * @throws NoSuchCodeException if the original group name does not exist
   * @throws GroupExistsException if the target group name already exist
   */
  public void updateGroupName(String groupName, String newName) throws NoSuchCodeException, GroupExistsException {
    // TO BE IMPLEMENTED
  }

  /**
   * Retrieves the list of groups.
   * * @return the collection of group names
   */
  public Collection<String> listOfGroups() {
    return null; // TO BE IMPLEMENTED
  }

  /**
   * Add a person to a group
   * * @param codePerson person code
   * @param groupName  name of the group
   * @throws NoSuchCodeException in case the code or group name do not exist
   */
  public void addPersonToGroup(String codePerson, String groupName) throws NoSuchCodeException {
    // TO BE IMPLEMENTED
  }

  /**
   * Retrieves the list of people on a group
   * * @param groupName name of the group
   * @return collection of person codes
   */
  public Collection<String> listOfPeopleInGroup(String groupName) {
    return null; // TO BE IMPLEMENTED
  }

  /**
   * Retrieves the code of the person having the largest
   * group of friends
   * * @return the code of the person
   */
  public String personWithLargestNumberOfFriends() {
    return null; // TO BE IMPLEMENTED
  }

  /**
   * Find the name of group with the largest number of members
   * * @return the name of the group
   */
  public String largestGroup() {
    return null; // TO BE IMPLEMENTED
  }

  /**
   * Find the code of the person that is member of
   * the largest number of groups
   * * @return the code of the person
   */
  public String personInLargestNumberOfGroups() {
    return null; // TO BE IMPLEMENTED
  }

  // R5

  /**
   * add a new post by a given account
   * * @param authorCode the id of the post author
   * @param text   the content of the post
   * @return a unique id of the post
   */
  public String post(String authorCode, String text) {
    return null; // TO BE IMPLEMENTED
  }

  /**
   * retrieves the content of the given post
   * * @param pid    the id of the post
   * @return the content of the post
   */
  public String getPostContent(String pid) {
    return null; // TO BE IMPLEMENTED
  }

  /**
   * retrieves the timestamp of the given post
   * * @param pid    the id of the post
   * @return the timestamp of the post
   */
  public long getTimestamp(String pid) {
    return -1; // TO BE IMPLEMENTED
  }

  /**
   * returns the list of post of a given author paginated
   * * @param author     author of the post
   * @param pageNo     page number (starting at 1)
   * @param pageLength page length
   * @return the list of posts id
   */
  public List<String> getPaginatedUserPosts(String author, int pageNo, int pageLength) {
    return null; // TO BE IMPLEMENTED
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
    return null; // TO BE IMPLEMENTED
  }

}