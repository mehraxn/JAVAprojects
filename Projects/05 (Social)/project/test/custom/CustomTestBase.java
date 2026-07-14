package custom;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;

import social.JPAUtil;
import social.Social;

/**
 * Base for custom tests: switches to the in-memory test persistence unit and
 * rebuilds a fresh (empty) schema before each test, so tests are isolated and
 * order-independent. The factory is closed once after each test class.
 */
abstract class CustomTestBase {

  protected Social social;

  @BeforeEach
  void resetDatabase() {
    // create-drop + closing the previous factory gives every test a clean schema.
    JPAUtil.setTestMode();
    social = new Social();
  }

  @AfterAll
  static void closeFactory() {
    JPAUtil.close();
  }
}
