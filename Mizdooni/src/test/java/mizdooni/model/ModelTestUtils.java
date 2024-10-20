package mizdooni.model;

import java.time.LocalDateTime;
import java.time.LocalTime;

public class ModelTestUtils {
    public static final String DEFAULT_COUNTRY = "country";
    public static final String DEFAULT_CITY = "city";
    public static final String DEFAULT_STREET = "street";
    public static final String DEFAULT_NAME = "name";
    public static final String DEFAULT_PASS = "password";
    public static final String DEFAULT_EMAIL = "email";
    public static final String DEFAULT_TYPE = "type";
    public static final LocalTime DEFAULT_LOCAL_TIME = LocalTime.of(12, 0, 0);
    public static final LocalDateTime DEFAULT_LOCAL_DATE_TIME = LocalDateTime.of(2000, 1, 1, 12, 0, 0);
    public static final String DEFAULT_DESCRIPTION = "description";
    public static final String DEFAULT_IMAGE_LINK = "image_link";
    public static final int DEFAULT_SEATS_NUMBER = 4;
    public static final String DEFAULT_COMMENT = "comment";
    public static final double DEFAULT_RATING_NUMBER = 4.5;


    public static Rating getDefaultRating() {
        return new Rating(DEFAULT_RATING_NUMBER, DEFAULT_RATING_NUMBER, DEFAULT_RATING_NUMBER, DEFAULT_RATING_NUMBER);
    }

    public static Address getDefaultAddress() {
        return new Address(DEFAULT_COUNTRY, DEFAULT_CITY, DEFAULT_STREET);
    }

    public static User getDefaultClientUser() {
        return new User(DEFAULT_NAME, DEFAULT_PASS, DEFAULT_EMAIL, getDefaultAddress(), User.Role.client);
    }

    public static User getDefaultClientUserWithName(String name) {
        return new User(name, DEFAULT_PASS, DEFAULT_EMAIL, getDefaultAddress(), User.Role.client);
    }

    public static User getDefaultManagerUser() {
        return new User(DEFAULT_NAME, DEFAULT_PASS, DEFAULT_EMAIL, getDefaultAddress(), User.Role.manager);
    }

    public static User getDefaultManagerUserWithName(String name) {
        return new User(name, DEFAULT_PASS, DEFAULT_EMAIL, getDefaultAddress(), User.Role.manager);
    }

    public static Table getTableForRestaurant(Restaurant restaurant) {
        return new Table(0, restaurant.getId(), DEFAULT_SEATS_NUMBER);
    }

    public static Review getReviewForUser(User user) {
        return new Review(user, getDefaultRating(), DEFAULT_COMMENT, DEFAULT_LOCAL_DATE_TIME);
    }

    public static Restaurant getDefaultRestaurant() {
        return new Restaurant(
                DEFAULT_NAME, getDefaultManagerUser(),
                DEFAULT_TYPE, DEFAULT_LOCAL_TIME, DEFAULT_LOCAL_TIME,
                DEFAULT_DESCRIPTION,getDefaultAddress(), DEFAULT_IMAGE_LINK
        );
    }
}
