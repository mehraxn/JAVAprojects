package social;

import java.util.List;

import jakarta.persistence.TypedQuery;

/**
 * Repository for {@link Post} with database-side (JPQL) pagination, so the facade
 * no longer loads every post into memory to filter/sort/paginate in Java.
 * <p>
 * All queries use named parameters and a deterministic ordering
 * ({@code timestamp DESC, id DESC}); pagination uses {@code setFirstResult}/
 * {@code setMaxResults}. Page numbers are 1-based.
 */
public class PostRepository extends GenericRepository<Post, String> {

  public PostRepository() {
    super(Post.class);
  }

  /** Posts authored by {@code authorCode}, most recent first, for the given page. */
  public List<Post> findByAuthorPaginated(String authorCode, int pageNo, int pageLength) {
    return JPAUtil.withEntityManager(em -> {
      TypedQuery<Post> query = em.createQuery(
          "SELECT p FROM Post p WHERE p.author.code = :code "
              + "ORDER BY p.timestamp DESC, p.id DESC",
          Post.class);
      query.setParameter("code", authorCode);
      query.setFirstResult((pageNo - 1) * pageLength);
      query.setMaxResults(pageLength);
      return query.getResultList();
    });
  }

  /** Posts authored by the friends of {@code personCode}, most recent first, for the given page. */
  public List<Post> findFriendPostsPaginated(String personCode, int pageNo, int pageLength) {
    return JPAUtil.withEntityManager(em -> {
      TypedQuery<Post> query = em.createQuery(
          "SELECT p FROM Post p WHERE p.author IN "
              + "(SELECT f FROM Person pr JOIN pr.friends f WHERE pr.code = :code) "
              + "ORDER BY p.timestamp DESC, p.id DESC",
          Post.class);
      query.setParameter("code", personCode);
      query.setFirstResult((pageNo - 1) * pageLength);
      query.setMaxResults(pageLength);
      return query.getResultList();
    });
  }
}
