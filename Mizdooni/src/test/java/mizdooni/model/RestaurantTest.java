package mizdooni.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class RestaurantTest {
    private Restaurant restaurant;

    @BeforeEach
    public void setup() {
        restaurant = ModelTestUtils.getDefaultRestaurant();
        ModelTestUtils.addTablesToRestaurant(restaurant, 3);
        ModelTestUtils.addReviewsWithDefaultRatingWithUniqueUserToRestaurant(restaurant, 3);
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

    @Test
    public void addTable_AddNewTable_ItsTableNumberAndRestaurantTableListSizeIncreasesByOne() {
        restaurant.addTable(ModelTestUtils.getTableForRestaurant(restaurant));

        List<Table> restaurantTables = restaurant.getTables();
        Table newTable = restaurantTables.getLast();
        int expectedTableNumberAndListSize = 4;

        assertThat(restaurantTables).hasSize(expectedTableNumberAndListSize);
        assertThat(newTable.getTableNumber()).isEqualTo(expectedTableNumberAndListSize);
    }

    @Test
    public void addReview_ReviewWithNewUser_AddsReviewToRestaurantReviewList() {
        User newUser = ModelTestUtils.getDefaultClientUser();
        Review newReview = ModelTestUtils.getReviewWithDefaultRatingForUser(newUser);

        restaurant.addReview(newReview);
        List<Review> restaurantReviews = restaurant.getReviews();
        Review addedReview = restaurantReviews.getLast();

        assertThat(restaurantReviews).hasSize(4);
        assertThat(addedReview.getUser()).isEqualTo(newUser);
    }

    @Test
    public void addReview_ReviewWithUserThatAlreadyHasReview_DeletesPreviousReview() {
        User repeatedUser = restaurant.getReviews().getFirst().getUser();
        Review repeatedUserReview = ModelTestUtils.getReviewWithDefaultRatingForUser(repeatedUser);

        restaurant.addReview(repeatedUserReview);
        List<Review> restaurantReviews = restaurant.getReviews();
        int previousReviewIndex = 0;
        Review newReview = restaurantReviews.getLast();

        assertThat(restaurantReviews.get(previousReviewIndex).getUser()).isNotEqualTo(repeatedUser);
        assertThat(newReview).isEqualTo(repeatedUserReview);
    }
}
