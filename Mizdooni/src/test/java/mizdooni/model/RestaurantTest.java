package mizdooni.model;

import org.junit.jupiter.api.BeforeEach;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class RestaurantTest {
    private Restaurant restaurant;
    private List<Table> tables;
    private List<User> clients;
    private List<Review> reviews;

    private void setupTables() {
        tables = new ArrayList<>() {{
            ModelTestUtils.getTableForRestaurant(restaurant);
            ModelTestUtils.getTableForRestaurant(restaurant);
            ModelTestUtils.getTableForRestaurant(restaurant);
        }};
        for (Table table : tables) {
            restaurant.addTable(table);
        }
    }

    private void setupClients() {
        clients = new ArrayList<>() {{
            ModelTestUtils.getDefaultClientUserWithName("client1");
            ModelTestUtils.getDefaultClientUserWithName("client2");
            ModelTestUtils.getDefaultClientUserWithName("client3");
        }};
    }

    private void setupReviews() {
        reviews = new ArrayList<>();
        for (User client : clients) {
            Review review = ModelTestUtils.getReviewForUser(client);
            reviews.add(review);
            restaurant.addReview(review);
        }
    }

    @BeforeEach
    public void setup() {
        restaurant = ModelTestUtils.getDefaultRestaurant();
        setupTables();
        setupClients();
        setupReviews();
    }
}
