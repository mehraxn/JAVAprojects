package social;

/**
 * Repository for {@link Group} (keyed by group name). Uses the generic CRUD
 * operations; declared for symmetry with {@link PersonRepository} and
 * {@link PostRepository} and as a home for any future group-specific queries.
 */
public class GroupRepository extends GenericRepository<Group, String> {

  public GroupRepository() {
    super(Group.class);
  }
}
