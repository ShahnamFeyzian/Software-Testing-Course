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
    public static final LocalDateTime DEFAULT_LOCAL_DATE_TIME = LocalDateTime.of(
            2000, 1, 1, 12, 0, 0
    );
    public static final String DEFAULT_DESCRIPTION = "description";
    public static final String DEFAULT_IMAGE_LINK = "image_link";
    public static final int DEFAULT_SEATS_NUMBER = 4;
    public static final String DEFAULT_COMMENT = "comment";
    public static final double DEFAULT_RATING_NUMBER = 4.5;
    public static final boolean DEFAULT_CANCELLED_RESERVATION = false;


    public static Rating getDefaultRating() {
        return new Rating(DEFAULT_RATING_NUMBER, DEFAULT_RATING_NUMBER, DEFAULT_RATING_NUMBER, DEFAULT_RATING_NUMBER);
    }

    public static Rating getRandomRating() {
        double food = Math.random()*100;
        double service = Math.random()*100;
        double ambiance = Math.random()*100;
        double overall = Math.random()*100;
        return new Rating(food, service, ambiance, overall);
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

    public static Table getTableWithDefaultSeatsForRestaurant(Restaurant restaurant) {
        return new Table(0, restaurant.getId(), DEFAULT_SEATS_NUMBER);
    }

    public static Table getTableWithRandomSeatsForRestaurant(Restaurant restaurant) {
        int randomSeats = (int) Math.floor(Math.random()*100);
        return new Table(0, restaurant.getId(), randomSeats);
    }

    public static void addTablesWithDefaultSeatsToRestaurant(Restaurant restaurant, int num) {
        for (int i = 0; i < num; i++) {
            Table table = getTableWithDefaultSeatsForRestaurant(restaurant);
            restaurant.addTable(table);
        }
    }

    public static void addTablesWithRandomSeatsToRestaurant(Restaurant restaurant, int num) {
        for (int i = 0; i < num; i++) {
            Table table = getTableWithRandomSeatsForRestaurant(restaurant);
            restaurant.addTable(table);
        }
    }

    public static Review getReviewWithDefaultRatingForUser(User user) {
        return new Review(user, getDefaultRating(), DEFAULT_COMMENT, DEFAULT_LOCAL_DATE_TIME);
    }

    public static Review getReviewWithRandomRatingForUser(User user) {
        return new Review(user, getRandomRating(), DEFAULT_COMMENT, DEFAULT_LOCAL_DATE_TIME);
    }

    public static void addReviewsWithDefaultRatingWithUniqueUserToRestaurant(Restaurant restaurant, int num) {
        for (int i = 0; i < num; i++) {
            User user = getDefaultClientUserWithName("client" + i);
            Review review = getReviewWithDefaultRatingForUser(user);
            restaurant.addReview(review);
        }
    }

    public static void addReviewsWithRandomRatingWithUniqueUserToRestaurant(Restaurant restaurant, int num) {
        for (int i = 0; i < num; i++) {
            User user = getDefaultClientUserWithName("client" + i);
            Review review = getReviewWithRandomRatingForUser(user);
            restaurant.addReview(review);
        }
    }

    public static Restaurant getDefaultRestaurant() {
        return new Restaurant(
                DEFAULT_NAME, getDefaultManagerUser(),
                DEFAULT_TYPE, DEFAULT_LOCAL_TIME, DEFAULT_LOCAL_TIME,
                DEFAULT_DESCRIPTION,getDefaultAddress(), DEFAULT_IMAGE_LINK
        );
    }

    public static Reservation getDefaultReservation(User user , Restaurant restaurant , Table table){
        return new Reservation(
                user , restaurant , table , DEFAULT_LOCAL_DATE_TIME
        );
    }
}
