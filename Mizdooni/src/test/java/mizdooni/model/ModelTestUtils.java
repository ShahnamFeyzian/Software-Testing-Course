package mizdooni.model;

public class ModelTestUtils {
    public static final String DEFAULT_COUNTRY = "country";
    public static final String DEFAULT_CITY = "city";
    public static final String DEFAULT_STREET = "street";
    public static final String DEFAULT_NAME = "name";
    public static final String DEFAULT_PASS = "password";
    public static final String DEFAULT_EMAIL = "email";

    public Address getDefaultAddress() {
        return new Address(DEFAULT_COUNTRY, DEFAULT_CITY, DEFAULT_STREET);
    }

    public User getClientUser() {
        return new User(DEFAULT_NAME, DEFAULT_PASS, DEFAULT_EMAIL, getDefaultAddress(), User.Role.client);
    }

    public User getManagerUser() {
        return new User(DEFAULT_NAME, DEFAULT_PASS, DEFAULT_EMAIL, getDefaultAddress(), User.Role.manager);
    }
}
