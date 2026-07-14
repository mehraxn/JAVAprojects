package custom;

import diet.Customer;
import diet.Takeaway;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CustomR6CustomersTest {

	@Test
	void customerStoresFieldsAndToString() {
		Customer customer = new Takeaway()
				.registerCustomer("Ralph", "Fiennes", "r@example.com", "123");

		assertEquals("Ralph", customer.getFirstName());
		assertEquals("Fiennes", customer.getLastName());
		assertEquals("r@example.com", customer.getEmail());
		assertEquals("123", customer.getPhone());
		assertEquals("Ralph Fiennes", customer.toString());
	}

	@Test
	void customerSettersUpdateContactData() {
		Customer customer = new Takeaway()
				.registerCustomer("Ralph", "Fiennes", "r@example.com", "123");

		customer.setEmail("new@example.com");
		customer.SetEmail("alias@example.com");
		customer.setPhone("456");

		assertEquals("alias@example.com", customer.getEmail());
		assertEquals("456", customer.getPhone());
	}

	@Test
	void customersAreSortedByLastNameThenFirstNameThenEmail() {
		Takeaway takeaway = new Takeaway();
		takeaway.registerCustomer("Ralph", "Fiennes", "r@example.com", "123");
		takeaway.registerCustomer("Judi", "Dench", "j@example.com", "456");
		takeaway.registerCustomer("Ian", "McKellen", "i@example.com", "789");

		List<String> names = takeaway.customers().stream()
				.map(Customer::toString)
				.toList();

		assertEquals(List.of("Judi Dench", "Ralph Fiennes", "Ian McKellen"), names);
	}
}
