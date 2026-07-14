package diet;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Represents a takeaway restaurant chain.
 */
public class Takeaway {
	private final Map<String, Restaurant> restaurants = new TreeMap<>();
	private final List<Customer> customers = new ArrayList<>();

	/**
	 * Creates a new restaurant with a given name.
	 *
	 * @param restaurantName name of the restaurant
	 * @return the new or existing restaurant
	 */
	public Restaurant addRestaurant(String restaurantName) {
		String validName = ValidationUtils.requireNotBlank(restaurantName, "restaurant name");
		return restaurants.computeIfAbsent(validName, Restaurant::new);
	}

	/**
	 * Retrieves the names of all restaurants.
	 *
	 * @return collection of restaurant names
	 */
	public Collection<String> restaurants() {
		return new ArrayList<>(restaurants.keySet());
	}

	/**
	 * Creates a new customer for the takeaway.
	 *
	 * @param firstName first name of the customer
	 * @param lastName last name of the customer
	 * @param email email of the customer
	 * @param phoneNumber mobile phone number
	 * @return the object representing the newly created customer
	 */
	public Customer registerCustomer(String firstName, String lastName, String email, String phoneNumber) {
		Customer customer = new Customer(firstName, lastName, email, phoneNumber);
		customers.add(customer);
		return customer;
	}

	/**
	 * Retrieves all registered customers.
	 *
	 * @return sorted collection of customers
	 */
	public Collection<Customer> customers(){
		return customers.stream()
				.sorted(Comparator
						.comparing(Customer::getLastName)
						.thenComparing(Customer::getFirstName)
						.thenComparing(Customer::getEmail))
				.toList();
	}

	/**
	 * Creates a new order for the chain.
	 *
	 * @param customer customer issuing the order
	 * @param restaurantName name of the restaurant that will take the order
	 * @param time time of desired delivery
	 * @return order object
	 */
	public Order createOrder(Customer customer, String restaurantName, String time) {
		if (customer == null) {
			throw new IllegalArgumentException("customer cannot be null");
		}
		String validRestaurantName = ValidationUtils.requireNotBlank(restaurantName, "restaurant name");
		Restaurant restaurant = restaurants.get(validRestaurantName);
		if (restaurant == null) {
			throw new IllegalArgumentException("Unknown restaurant: " + validRestaurantName);
		}

		LocalTime requestedTime = ValidationUtils.parseTime(time, "delivery time");
		LocalTime deliveryTime = restaurant.adjustDeliveryTime(requestedTime);
		Order order = new Order(customer, restaurant, deliveryTime);
		restaurant.addOrder(order);
		return order;
	}

	/**
	 * Finds all restaurants that are open at a given time.
	 *
	 * @param time the time with format {@code "HH:MM"}
	 * @return the sorted collection of restaurants
	 */
	public Collection<Restaurant> openRestaurants(String time){
		LocalTime parsedTime = ValidationUtils.parseTime(time, "time");
		return restaurants.values().stream()
				.filter(restaurant -> restaurant.isOpenAt(parsedTime))
				.sorted(Comparator.comparing(Restaurant::getName))
				.toList();
	}
}
