package social;

import java.util.List; //ADDED FOR R3 and R5

public class PersonRepository extends GenericRepository<Person, String> {

  public PersonRepository() {
    super(Person.class);
  }

  //ADDED FOR R3 - Methods for Group operations
  public java.util.Optional<SocialGroup> findGroupByName(String name) { //ADDED FOR R3
    return JPAUtil.withEntityManager( //ADDED FOR R3
      em -> java.util.Optional.ofNullable(em.find(SocialGroup.class, name)) //ADDED FOR R3
    ); //ADDED FOR R3
  } //ADDED FOR R3

  public List<SocialGroup> findAllGroups() { //ADDED FOR R3
    return JPAUtil.withEntityManager( //ADDED FOR R3
      em -> em.createQuery("SELECT g FROM SocialGroup g", SocialGroup.class) //ADDED FOR R3
              .getResultList() //ADDED FOR R3
    ); //ADDED FOR R3
  } //ADDED FOR R3

  public void saveGroup(SocialGroup group) { //ADDED FOR R3
    JPAUtil.transaction(em -> em.persist(group)); //ADDED FOR R3
  } //ADDED FOR R3

  public void updateGroup(SocialGroup group) { //ADDED FOR R3
    JPAUtil.transaction(em -> em.merge(group)); //ADDED FOR R3
  } //ADDED FOR R3

  public void deleteGroup(SocialGroup group) { //ADDED FOR R3
    JPAUtil.transaction(em -> em.remove(em.contains(group) ? group : em.merge(group))); //ADDED FOR R3
  } //ADDED FOR R3

  //ADDED FOR R5 - Methods for Post operations
  public java.util.Optional<Post> findPostById(String id) { //ADDED FOR R5
    return JPAUtil.withEntityManager( //ADDED FOR R5
      em -> java.util.Optional.ofNullable(em.find(Post.class, id)) //ADDED FOR R5
    ); //ADDED FOR R5
  } //ADDED FOR R5

  public void savePost(Post post) { //ADDED FOR R5
    JPAUtil.transaction(em -> em.persist(post)); //ADDED FOR R5
  } //ADDED FOR R5

  public List<Post> findPostsByAuthorOrderByTimestampDesc(String authorCode) { //ADDED FOR R5
    return JPAUtil.withEntityManager( //ADDED FOR R5
      em -> em.createQuery("SELECT p FROM Post p WHERE p.author.code = :authorCode ORDER BY p.timestamp DESC", Post.class) //ADDED FOR R5
              .setParameter("authorCode", authorCode) //ADDED FOR R5
              .getResultList() //ADDED FOR R5
    ); //ADDED FOR R5
  } //ADDED FOR R5

  public List<Post> findPostsByFriendsOrderByTimestampDesc(String authorCode) { //ADDED FOR R5
    return JPAUtil.withEntityManager( //ADDED FOR R5
      em -> em.createQuery("SELECT p FROM Post p WHERE p.author IN (SELECT f FROM Person per JOIN per.friends f WHERE per.code = :authorCode) ORDER BY p.timestamp DESC", Post.class) //ADDED FOR R5
              .setParameter("authorCode", authorCode) //ADDED FOR R5
              .getResultList() //ADDED FOR R5
    ); //ADDED FOR R5
  } //ADDED FOR R5

}