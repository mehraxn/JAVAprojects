package diet;

import diet.Order.OrderStatus;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Represents a restaurant with opening hours, menus, and orders.
 */
public class Restaurant {
	private static class OpeningInterval {
		private final LocalTime start;
		private final LocalTime end;

		OpeningInterval(LocalTime start, LocalTime end) {
			this.start = start;
			this.end = end;
		}

		boolean contains(LocalTime time) {
			if (start.equals(end)) {
				return false;
			}
			if (start.isBefore(end)) {
				return !time.isBefore(start) && time.isBefore(end);
			}
			return !time.isBefore(start) || time.isBefore(end);
		}
	}

	private final String name;
	private final Map<String, Menu> menus = new TreeMap<>();
	private final List<OpeningInterval> hours = new ArrayList<>();
	private final List<Order> orders = new ArrayList<>();

	Restaurant(String name) {
		this.name = ValidationUtils.requireNotBlank(name, "restaurant name");
	}

	/**
	 * Retrieves the name of the restaurant.
	 *
	 * @return name of the restaurant
	 */
	public String getName() {
		return name;
	}

	/**
	 * Defines opening times as pairs of opening and closing values.
	 *
	 * @param hm sequence of opening and closing times
	 */
	public void setHours(String ... hm) {
		if (hm == null || hm.length == 0 || hm.length % 2 != 0) {
			throw new IllegalArgumentException("opening hours must be provided as start/end pairs");
		}

		List<OpeningInterval> parsed = new ArrayList<>();
		for (int i = 0; i < hm.length; i += 2) {
			LocalTime start = ValidationUtils.parseTime(hm[i], "opening time");
			LocalTime end = ValidationUtils.parseTime(hm[i + 1], "closing time");
			parsed.add(new OpeningInterval(start, end));
		}
		hours.clear();
		hours.addAll(parsed);
	}

	/**
	 * Checks whether the restaurant is open at the given time.
	 *
	 * @param time time to check
	 * @return {@code true} if the restaurant is open at that time
	 */
	public boolean isOpenAt(String time){
		LocalTime parsed = ValidationUtils.parseTime(time, "time");
		return isOpenAt(parsed);
	}

	/**
	 * Adds a menu to the list of menus offered by the restaurant.
	 *
	 * @param menu the menu
	 */
	public void addMenu(Menu menu) {
		if (menu == null) {
			throw new IllegalArgumentException("menu cannot be null");
		}
		menus.put(menu.getName(), menu);
	}

	/**
	 * Gets the restaurant menu with the given name.
	 *
	 * @param name name of the required menu
	 * @return menu with the given name, or {@code null}
	 */
	public Menu getMenu(String name) {
		if (name == null) {
			return null;
		}
		return menus.get(name);
	}

	/**
	 * Retrieves all orders with a given status as deterministic text.
	 *
	 * @param status the status to be matched
	 * @return textual representation of orders
	 */
	public String ordersWithStatus(OrderStatus status) {
		if (status == null) {
			throw new IllegalArgumentException("order status cannot be null");
		}

		return orders.stream()
				.filter(order -> order.getStatus() == status)
				.sorted(Comparator
						.comparing((Order order) -> order.getRestaurant().getName())
						.thenComparing(order -> order.getCustomer().toString())
						.thenComparing(Order::getDeliveryTime))
				.map(Order::toString)
				.reduce((left, right) -> left + System.lineSeparator() + right)
				.orElse("");
	}

	boolean isOpenAt(LocalTime time) {
		for (OpeningInterval interval : hours) {
			if (interval.contains(time)) {
				return true;
			}
		}
		return false;
	}

	LocalTime adjustDeliveryTime(LocalTime requestedTime) {
		if (isOpenAt(requestedTime)) {
			return requestedTime;
		}

		LocalTime best = null;
		for (OpeningInterval interval : hours) {
			if (requestedTime.isBefore(interval.start)) {
				if (best == null || interval.start.isBefore(best)) {
					best = interval.start;
				}
			}
		}

		if (best != null) {
			return best;
		}

		return hours.stream()
				.map(interval -> interval.start)
				.min(LocalTime::compareTo)
				.orElse(requestedTime);
	}

	void addOrder(Order order) {
		if (order == null) {
			throw new IllegalArgumentException("order cannot be null");
		}
		orders.add(order);
	}
}
