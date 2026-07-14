package social;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Facade for the social network system.
 * <p>
 * Exposes people, bidirectional friendships, groups and memberships, and posts
 * with paginated feeds. Persistence is handled through JPA/Hibernate repositories
 * ({@link PersonRepository}, {@link GroupRepository}, {@link PostRepository}) over
 * an H2 database managed by {@link JPAUtil}.
 * <p>
 * Input validation throws {@link IllegalArgumentException}; domain errors use the
 * checked exceptions {@link PersonExistsException}, {@link GroupExistsException} and
 * {@link NoSuchCodeException}. Returned collections are defensive copies with a
 * deterministic ordering.
 */
public class Social {

  private final PersonRepository personRepository = new PersonRepository();
  private final GroupRepository groupRepository = new GroupRepository();
  private final PostRepository postRepository = new PostRepository();

  /**
   * Creates a new account for a person.
   *
   * @param code    nickname of the account
   * @param name    first name
   * @param surname last name
   * @throws PersonExistsException in case of duplicate code
   */
  public void addPerson(String code, String name, String surname) throws PersonExistsException {
    ValidationUtils.requireNotBlank(code, "code");
    ValidationUtils.requireNotBlank(name, "name");
    ValidationUtils.requireNotBlank(surname, "surname");
    if (personRepository.findById(code).isPresent()) {
      throw new PersonExistsException();
    }
    personRepository.save(new Person(code, name, surname));
  }

  /**
   * Retrieves information about the person given their account code, as
   * {@code code name surname} separated by blanks.
   *
   * @param code account code
   * @return the information of the person
   * @throws NoSuchCodeException if a person with that code does not exist
   */
  public String getPerson(String code) throws NoSuchCodeException {
    ValidationUtils.requireNotBlank(code, "code");
    Person p = personRepository.findById(code).orElse(null);
    if (p == null) {
      throw new NoSuchCodeException();
    }
    return p.getCode() + " " + p.getName() + " " + p.getSurname();
  }

  /**
   * Defines a bidirectional friendship between two persons given their codes.
   *
   * @param codePerson1 first person code
   * @param codePerson2 second person code
   * @throws NoSuchCodeException in case either code does not exist
   * @throws IllegalArgumentException if the two codes are equal (self-friendship)
   */
  public void addFriendship(String codePerson1, String codePerson2) throws NoSuchCodeException {
    ValidationUtils.requireNotBlank(codePerson1, "codePerson1");
    ValidationUtils.requireNotBlank(codePerson2, "codePerson2");
    if (codePerson1.equals(codePerson2)) {
      throw new IllegalArgumentException("A person cannot be friends with themselves");
    }
    JPAUtil.executeInTransaction(() -> {
      Person p1 = personRepository.findById(codePerson1).orElse(null);
      Person p2 = personRepository.findById(codePerson2).orElse(null);
      if (p1 == null || p2 == null) {
        throw new NoSuchCodeException();
      }
      // Sets make a repeated friendship idempotent; both sides are kept symmetric.
      p1.addFriend(p2);
      p2.addFriend(p1);
      personRepository.update(p1);
      personRepository.update(p2);
    });
  }

  /**
   * Retrieves the friends of a person, as a list of person codes sorted by code.
   *
   * @param codePerson code of the person
   * @return the sorted list of friend codes
   * @throws NoSuchCodeException in case the code does not exist
   */
  public Collection<String> listOfFriends(String codePerson) throws NoSuchCodeException {
    ValidationUtils.requireNotBlank(codePerson, "codePerson");
    return JPAUtil.executeInContext(() -> {
      Person p = personRepository.findById(codePerson).orElse(null);
      if (p == null) {
        throw new NoSuchCodeException();
      }
      return p.getFriends().stream()
          .map(Person::getCode)
          .sorted()
          .collect(Collectors.toCollection(ArrayList::new));
    });
  }

  /**
   * Creates a new group with the given name.
   *
   * @param groupName name of the group
   * @throws GroupExistsException if a group with the given name already exists
   */
  public void addGroup(String groupName) throws GroupExistsException {
    ValidationUtils.requireNotBlank(groupName, "groupName");
    JPAUtil.executeInTransaction(() -> {
      if (groupRepository.findById(groupName).isPresent()) {
        throw new GroupExistsException();
      }
      groupRepository.save(new Group(groupName));
    });
  }

  /**
   * Deletes the group with the given name, detaching it from all members first so
   * the membership join table stays consistent.
   *
   * @param groupName name of the group
   * @throws NoSuchCodeException if a group with the given name does not exist
   */
  public void deleteGroup(String groupName) throws NoSuchCodeException {
    ValidationUtils.requireNotBlank(groupName, "groupName");
    JPAUtil.executeInTransaction(() -> {
      Group g = groupRepository.findById(groupName).orElse(null);
      if (g == null) {
        throw new NoSuchCodeException();
      }
      for (Person member : new ArrayList<>(g.getMembers())) {
        member.getGroups().remove(g);
        personRepository.update(member);
      }
      g.getMembers().clear();
      groupRepository.delete(g);
    });
  }

  /**
   * Renames a group. Because the group name is its identifier, this creates a group
   * with the new name, moves the members over, and removes the old group.
   *
   * @param groupName current name of the group
   * @param newName   new name of the group
   * @throws NoSuchCodeException   if the original group name does not exist
   * @throws GroupExistsException  if the target group name already exists
   */
  public void updateGroupName(String groupName, String newName)
      throws NoSuchCodeException, GroupExistsException {
    ValidationUtils.requireNotBlank(groupName, "groupName");
    ValidationUtils.requireNotBlank(newName, "newName");
    try {
      JPAUtil.executeInTransaction(() -> {
        Group g = groupRepository.findById(groupName).orElse(null);
        if (g == null) {
          throw new NoSuchCodeException();
        }
        if (groupRepository.findById(newName).isPresent()) {
          throw new GroupExistsException();
        }
        Group newGroup = new Group(newName);
        groupRepository.save(newGroup);
        for (Person member : new ArrayList<>(g.getMembers())) {
          member.getGroups().remove(g);
          member.getGroups().add(newGroup);
          newGroup.getMembers().add(member);
          personRepository.update(member);
        }
        g.getMembers().clear();
        groupRepository.delete(g);
      });
    } catch (NoSuchCodeException | GroupExistsException e) {
      throw e;
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * Retrieves the list of group names, sorted by name.
   *
   * @return the sorted collection of group names
   */
  public Collection<String> listOfGroups() {
    return JPAUtil.executeInContext(() -> groupRepository.findAll().stream()
        .map(Group::getName)
        .sorted()
        .collect(Collectors.toCollection(ArrayList::new)));
  }

  /**
   * Adds a person to a group (idempotent for repeated memberships).
   *
   * @param codePerson person code
   * @param groupName  name of the group
   * @throws NoSuchCodeException in case the code or group name do not exist
   */
  public void addPersonToGroup(String codePerson, String groupName) throws NoSuchCodeException {
    ValidationUtils.requireNotBlank(codePerson, "codePerson");
    ValidationUtils.requireNotBlank(groupName, "groupName");
    JPAUtil.executeInTransaction(() -> {
      Person p = personRepository.findById(codePerson).orElse(null);
      Group g = groupRepository.findById(groupName).orElse(null);
      if (p == null || g == null) {
        throw new NoSuchCodeException();
      }
      p.addGroup(g);
      g.getMembers().add(p);
      personRepository.update(p);
    });
  }

  /**
   * Retrieves the people in a group, as a list of person codes sorted by code.
   * Returns an empty list if the group does not exist.
   *
   * @param groupName name of the group
   * @return the sorted collection of person codes
   */
  public Collection<String> listOfPeopleInGroup(String groupName) {
    return JPAUtil.executeInContext(() -> {
      Group g = groupRepository.findById(groupName).orElse(null);
      if (g == null) {
        return new ArrayList<String>();
      }
      return g.getMembers().stream()
          .map(Person::getCode)
          .sorted()
          .collect(Collectors.toCollection(ArrayList::new));
    });
  }

  /**
   * Retrieves the code of the person having the largest number of friends.
   * Ties are broken by the lexicographically smallest code.
   *
   * @return the code of the person, or {@code null} if there are no people
   */
  public String personWithLargestNumberOfFriends() {
    return JPAUtil.executeInContext(() -> {
      String best = null;
      int bestCount = -1;
      for (Person p : personRepository.findAll()) {
        int count = p.getFriends().size();
        if (count > bestCount || (count == bestCount && p.getCode().compareTo(best) < 0)) {
          bestCount = count;
          best = p.getCode();
        }
      }
      return best;
    });
  }

  /**
   * Finds the name of the group with the largest number of members.
   * Ties are broken by the lexicographically smallest name.
   *
   * @return the name of the group, or {@code null} if there are no groups
   */
  public String largestGroup() {
    return JPAUtil.executeInContext(() -> {
      String best = null;
      int bestCount = -1;
      for (Group g : groupRepository.findAll()) {
        int count = g.getMembers().size();
        if (count > bestCount || (count == bestCount && g.getName().compareTo(best) < 0)) {
          bestCount = count;
          best = g.getName();
        }
      }
      return best;
    });
  }

  /**
   * Finds the code of the person that is a member of the largest number of groups.
   * Ties are broken by the lexicographically smallest code.
   *
   * @return the code of the person, or {@code null} if there are no people
   */
  public String personInLargestNumberOfGroups() {
    return JPAUtil.executeInContext(() -> {
      String best = null;
      int bestCount = -1;
      for (Person p : personRepository.findAll()) {
        int count = p.getGroups().size();
        if (count > bestCount || (count == bestCount && p.getCode().compareTo(best) < 0)) {
          bestCount = count;
          best = p.getCode();
        }
      }
      return best;
    });
  }

  /**
   * Adds a new post by a given account.
   *
   * @param authorCode the code of the post author
   * @param text       the content of the post
   * @return a unique id of the post, or {@code null} if the author does not exist
   */
  public String post(String authorCode, String text) {
    ValidationUtils.requireNotBlank(authorCode, "authorCode");
    ValidationUtils.requireNotBlank(text, "text");
    String[] result = new String[1];
    JPAUtil.executeInTransaction(() -> {
      Person p = personRepository.findById(authorCode).orElse(null);
      if (p != null) {
        String id = UUID.randomUUID().toString().replace("-", "");
        postRepository.save(new Post(id, text, System.currentTimeMillis(), p));
        result[0] = id;
      }
    });
    return result[0];
  }

  /**
   * Retrieves the content of the given post.
   *
   * @param pid the id of the post
   * @return the content of the post, or {@code null} if it does not exist
   */
  public String getPostContent(String pid) {
    ValidationUtils.requireNotBlank(pid, "pid");
    return JPAUtil.executeInContext(() -> {
      Post p = postRepository.findById(pid).orElse(null);
      return p != null ? p.getText() : null;
    });
  }

  /**
   * Retrieves the timestamp of the given post.
   *
   * @param pid the id of the post
   * @return the timestamp of the post, or {@code -1} if it does not exist
   */
  public long getTimestamp(String pid) {
    ValidationUtils.requireNotBlank(pid, "pid");
    return JPAUtil.executeInContext(() -> {
      Post p = postRepository.findById(pid).orElse(null);
      return p != null ? p.getTimestamp() : -1L;
    });
  }

  /**
   * Returns the paginated list of a given author's post ids, most recent first.
   *
   * @param author     author of the posts
   * @param pageNo     page number (1-based)
   * @param pageLength page length
   * @return the list of post ids for the requested page
   */
  public List<String> getPaginatedUserPosts(String author, int pageNo, int pageLength) {
    ValidationUtils.requireNotBlank(author, "author");
    ValidationUtils.requirePositivePage(pageNo, "pageNo");
    ValidationUtils.requirePositive(pageLength, "pageLength");
    return JPAUtil.executeInContext(() -> postRepository
        .findByAuthorPaginated(author, pageNo, pageLength).stream()
        .map(Post::getId)
        .collect(Collectors.toCollection(ArrayList::new)));
  }

  /**
   * Returns the paginated list of the posts of a person's friends, most recent
   * first. Each element is the author code and post id separated by {@code ":"}.
   *
   * @param author     person whose friends' posts are requested
   * @param pageNo     page number (1-based)
   * @param pageLength page length
   * @return the list of {@code authorCode:postId} entries for the requested page
   */
  public List<String> getPaginatedFriendPosts(String author, int pageNo, int pageLength) {
    ValidationUtils.requireNotBlank(author, "author");
    ValidationUtils.requirePositivePage(pageNo, "pageNo");
    ValidationUtils.requirePositive(pageLength, "pageLength");
    return JPAUtil.executeInContext(() -> postRepository
        .findFriendPostsPaginated(author, pageNo, pageLength).stream()
        .map(p -> p.getAuthor().getCode() + ":" + p.getId())
        .collect(Collectors.toCollection(ArrayList::new)));
  }
}
