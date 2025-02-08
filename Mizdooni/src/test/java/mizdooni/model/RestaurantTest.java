package mizdooni.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static mizdooni.model.ModelTestUtils.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class RestaurantTest {
    private Restaurant restaurant;

    @BeforeEach
    void setup() {
        restaurant = getDefaultRestaurant();
        addTablesWithDefaultSeatsToRestaurant(restaurant, 3);
        addReviewsWithDefaultRatingWithUniqueUserToRestaurant(restaurant, 3);
    }

    @Test
    void getTable_GiveValidNumber_ReturnsTableWithGivenNumber() {
        int targetNumber = 2;
        Table targetTable = restaurant.getTable(targetNumber);

        assertThat(targetTable).isNotNull();
        assertThat(targetTable.getTableNumber()).isEqualTo(targetNumber);
    }

    @Test
    void getTable_GiveInvalidNumber_ReturnsNullTable() {
        int targetNumber = 5;
        Table targetTable = restaurant.getTable(targetNumber);

        assertThat(targetTable).isNull();
    }

    @Test
    void addTable_AddNewTable_ItsTableNumberAndRestaurantTableListSizeIncreasesByOne() {
        restaurant.addTable(getTableWithDefaultSeatsForRestaurant(restaurant));

        List<Table> restaurantTables = restaurant.getTables();
        Table newTable = restaurantTables.get(restaurantTables.size() - 1);
        int expectedTableNumberAndListSize = 4;

        assertThat(restaurantTables).hasSize(expectedTableNumberAndListSize);
        assertThat(newTable.getTableNumber()).isEqualTo(expectedTableNumberAndListSize);
    }

    @Test
    void addReview_ReviewWithNewUser_AddsReviewToRestaurantReviewList() {
        User newUser = getDefaultClientUser();
        Review newReview = getReviewWithDefaultRatingForUser(newUser);

        restaurant.addReview(newReview);
        List<Review> restaurantReviews = restaurant.getReviews();
        Review addedReview = restaurantReviews.get(restaurantReviews.size() - 1);

        assertThat(restaurantReviews).hasSize(4);
        assertThat(addedReview.getUser()).isEqualTo(newUser);
    }

    @Test
    void addReview_ReviewWithUserThatAlreadyHasReview_DeletesPreviousReview() {
        User repeatedUser = restaurant.getReviews().get(0).getUser();
        Review repeatedUserReview = getReviewWithDefaultRatingForUser(repeatedUser);

        restaurant.addReview(repeatedUserReview);
        List<Review> restaurantReviews = restaurant.getReviews();
        int previousReviewIndex = 0;
        Review newReview = restaurantReviews.get(restaurantReviews.size() - 1);

        assertThat(restaurantReviews.get(previousReviewIndex).getUser()).isNotEqualTo(repeatedUser);
        assertThat(newReview).isEqualTo(repeatedUserReview);
    }

    @Test
    void getAverageRating_RestaurantWithNoReview_AllScoreAreZero() {
        Restaurant tempRestaurant = getDefaultRestaurant();

        Rating avgRating = tempRestaurant.getAverageRating();

        assertThat(avgRating.food).isZero();
        assertThat(avgRating.service).isZero();
        assertThat(avgRating.ambiance).isZero();
        assertThat(avgRating.overall).isZero();
    }

    @Test
    void getAverageRating_RestaurantWithThreeRandomReviews_ResultIsAverageOfThem() {
        Restaurant randomRatedRestaurant = getDefaultRestaurant();
        addReviewsWithRandomRatingWithUniqueUserToRestaurant(randomRatedRestaurant, 3);
        List<Review> reviews = randomRatedRestaurant.getReviews();

        Rating avgRating = randomRatedRestaurant.getAverageRating();
        double expectedFood = (reviews.get(0).getRating().food + reviews.get(1).getRating().food + reviews.get(2).getRating().food) / 3;
        double expectedService = (reviews.get(0).getRating().service + reviews.get(1).getRating().service + reviews.get(2).getRating().service) / 3;
        double expectedAmbiance = (reviews.get(0).getRating().ambiance + reviews.get(1).getRating().ambiance + reviews.get(2).getRating().ambiance) / 3;
        double expectedOverall = (reviews.get(0).getRating().overall + reviews.get(1).getRating().overall + reviews.get(2).getRating().overall) / 3;

        assertThat(avgRating.food).isEqualTo(expectedFood);
        assertThat(avgRating.service).isEqualTo(expectedService);
        assertThat(avgRating.ambiance).isEqualTo(expectedAmbiance);
        assertThat(avgRating.overall).isEqualTo(expectedOverall);
    }

    @Test
    void getStarCount_SpyRestaurantAndSpyRating_GetAverageRatingAndRatingsGetStarCountBeCalled() {
        Restaurant spyRestaurant = spy(getDefaultRestaurant());
        Rating spyRating = spy(Rating.class);
        when(spyRestaurant.getAverageRating()).thenReturn(spyRating);

        spyRestaurant.getStarCount();

        verify(spyRestaurant).getAverageRating();
        verify(spyRestaurant).getStarCount();
    }

    @Test
    void getMaxSeatsNumber_RestaurantWithNoTable_ReturnsZero() {
        Restaurant tempRestaurant = getDefaultRestaurant();

        int maxSeatsNumber = tempRestaurant.getMaxSeatsNumber();

        assertThat(maxSeatsNumber).isZero();
    }

    @Test
    void getMaxSeatsNumber_RestaurantWithTwoRandomTables_ReturnsBiggerNumber() {
        Restaurant restaurantWithRandomTables = getDefaultRestaurant();
        addTablesWithRandomSeatsToRestaurant(restaurantWithRandomTables, 2);

        int maxSeatsNumber = restaurantWithRandomTables.getMaxSeatsNumber();
        int seat1 = restaurantWithRandomTables.getTables().get(0).getSeatsNumber();
        int seat2 = restaurantWithRandomTables.getTables().get(1).getSeatsNumber();
        int expectedSeats = Math.max(seat1, seat2);

        assertThat(maxSeatsNumber).isEqualTo(expectedSeats);
    }
}
