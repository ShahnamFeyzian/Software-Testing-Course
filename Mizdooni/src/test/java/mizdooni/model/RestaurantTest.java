package mizdooni.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class RestaurantTest {
    private Restaurant restaurant;
    private List<Table> tables;
    private List<User> clients;
    private List<Review> reviews;

    private void setupTables() {
        tables = new ArrayList<>();
        tables.add(ModelTestUtils.getTableForRestaurant(restaurant));
        tables.add(ModelTestUtils.getTableForRestaurant(restaurant));
        tables.add(ModelTestUtils.getTableForRestaurant(restaurant));
        for (Table table : tables) {
            restaurant.addTable(table);
        }
    }

    private void setupClients() {
        clients = new ArrayList<>();
        clients.add(ModelTestUtils.getDefaultClientUserWithName("client1"));
        clients.add(ModelTestUtils.getDefaultClientUserWithName("client2"));
        clients.add(ModelTestUtils.getDefaultClientUserWithName("client3"));
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

    @Test
    public void getTable_GiveValidNumber_ReturnsTableWithGivenNumber() {
        int targetNumber = 2;
        Table targetTable = restaurant.getTable(targetNumber);

        assertThat(targetTable).isNotNull();
        assertThat(targetTable.getTableNumber()).isEqualTo(targetNumber);
    }

    @Test
    public void getTable_GiveInvalidNumber_ReturnsNullTable() {
        int targetNumber = 5;
        Table targetTable = restaurant.getTable(targetNumber);

        assertThat(targetTable).isNull();
    }
}
