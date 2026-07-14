package diet;

/**
 * Represents a registered takeaway customer.
 */
public class Customer {
	private final String firstName;
	private final String lastName;
	private String email;
	private String phone;

	Customer(String firstName, String lastName, String email, String phone) {
		this.firstName = ValidationUtils.requireNotBlank(firstName, "first name");
		this.lastName = ValidationUtils.requireNotBlank(lastName, "last name");
		setEmail(email);
		setPhone(phone);
	}

	public String getLastName() {
		return lastName;
	}

	public String getFirstName() {
		return firstName;
	}

	public String getEmail() {
		return email;
	}

	public String getPhone() {
		return phone;
	}

	public void setEmail(String email) {
		this.email = ValidationUtils.requireNotBlank(email, "email");
	}

	public void SetEmail(String email) {
		setEmail(email);
	}

	public void setPhone(String phone) {
		this.phone = ValidationUtils.requireNotBlank(phone, "phone");
	}

	@Override
	public String toString() {
		return firstName + " " + lastName;
	}
}
