package diet;

import java.time.LocalTime;
import java.util.Map;
import java.util.TreeMap;

/**
 * Represents an order issued by a {@link Customer} for a {@link Restaurant}.
 */
public class Order {

	/**
	 * Possible order statuses.
	 */
	public enum OrderStatus {
		ORDERED, READY, DELIVERED
	}

	/**
	 * Accepted payment methods.
	 */
	public enum PaymentMethod {
		PAID, CASH, CARD
	}

	private final Customer customer;
	private final Restaurant restaurant;
	private final LocalTime deliveryTime;
	private final Map<String, Integer> menuLines = new TreeMap<>();
	private PaymentMethod paymentMethod = PaymentMethod.CASH;
	private OrderStatus status = OrderStatus.ORDERED;

	Order(Customer customer, Restaurant restaurant, LocalTime deliveryTime) {
		if (customer == null) {
			throw new IllegalArgumentException("customer cannot be null");
		}
		if (restaurant == null) {
			throw new IllegalArgumentException("restaurant cannot be null");
		}
		if (deliveryTime == null) {
			throw new IllegalArgumentException("delivery time cannot be null");
		}
		this.customer = customer;
		this.restaurant = restaurant;
		this.deliveryTime = deliveryTime;
	}

	/**
	 * Set payment method.
	 *
	 * @param pm the payment method
	 */
	public void setPaymentMethod(PaymentMethod pm) {
		if (pm == null) {
			throw new IllegalArgumentException("payment method cannot be null");
		}
		this.paymentMethod = pm;
	}

	/**
	 * Retrieves current payment method.
	 *
	 * @return the current method
	 */
	public PaymentMethod getPaymentMethod() {
		return paymentMethod;
	}

	/**
	 * Set the new status for the order.
	 *
	 * @param os new status
	 */
	public void setStatus(OrderStatus os) {
		if (os == null) {
			throw new IllegalArgumentException("order status cannot be null");
		}
		this.status = os;
	}

	/**
	 * Retrieves the current status of the order.
	 *
	 * @return current status
	 */
	public OrderStatus getStatus() {
		return status;
	}

	/**
	 * Add or replace a menu line with a given quantity.
	 *
	 * @param menu menu to be added
	 * @param quantity quantity
	 * @return the order itself for method chaining
	 */
	public Order addMenus(String menu, int quantity) {
		String menuName = ValidationUtils.requireNotBlank(menu, "menu name");
		ValidationUtils.requirePositiveInt(quantity, "menu quantity");
		if (restaurant.getMenu(menuName) == null) {
			throw new IllegalArgumentException("Unknown menu for restaurant: " + menuName);
		}
		menuLines.put(menuName, quantity);
		return this;
	}

	public Restaurant getRestaurant() {
		return restaurant;
	}

	public Customer getCustomer() {
		return customer;
	}

	public String getDeliveryTime() {
		return ValidationUtils.formatTime(deliveryTime);
	}

	@Override
	public String toString() {
		StringBuilder result = new StringBuilder();
		result.append(restaurant.getName())
				.append(", ")
				.append(customer)
				.append(" : (")
				.append(getDeliveryTime())
				.append("):");

		for (Map.Entry<String, Integer> entry : menuLines.entrySet()) {
			result.append(System.lineSeparator())
					.append('\t')
					.append(entry.getKey())
					.append("->")
					.append(entry.getValue());
		}
		return result.toString();
	}
}
