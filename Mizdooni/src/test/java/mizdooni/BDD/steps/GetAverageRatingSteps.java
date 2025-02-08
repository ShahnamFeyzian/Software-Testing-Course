package mizdooni.BDD.steps;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import mizdooni.model.Rating;
import mizdooni.model.Restaurant;

import static mizdooni.model.ModelTestUtils.addReviewsWithRandomRatingWithUniqueUserToRestaurant;
import static mizdooni.model.ModelTestUtils.getDefaultRestaurant;
import static org.assertj.core.api.Assertions.assertThat;

public class GetAverageRatingSteps {
    private Restaurant restaurant;
    private Rating avgRating;

    @Given("a restaurant with no reviews")
    public void aRestaurantWithNoReviews() {
        restaurant = getDefaultRestaurant();
    }

    @Given("a restaurant with {int} random reviews")
    public void aRestaurantWithRandomReviews(int count) {
        restaurant = getDefaultRestaurant();
        addReviewsWithRandomRatingWithUniqueUserToRestaurant(restaurant, count);
    }


    @When("I calculate the average rating")
    public void iCalculateTheAverageRating() {
        avgRating = restaurant.getAverageRating();
    }

    @Then("all average scores should be zero")
    public void allAverageScoresShouldBeZero() {
        assertThat(avgRating.food).isZero();
        assertThat(avgRating.service).isZero();
        assertThat(avgRating.ambiance).isZero();
        assertThat(avgRating.overall).isZero();
    }

    @Then("the average scores should be the average of all reviews")
    public void theAverageScoresShouldBeTheAverageOfAllReviews() {
        double expectedFood = restaurant.getReviews().stream().mapToDouble(r -> r.getRating().food).average().orElse(0);
        double expectedService = restaurant.getReviews().stream().mapToDouble(r -> r.getRating().service).average().orElse(0);
        double expectedAmbiance = restaurant.getReviews().stream().mapToDouble(r -> r.getRating().ambiance).average().orElse(0);
        double expectedOverall = restaurant.getReviews().stream().mapToDouble(r -> r.getRating().overall).average().orElse(0);

        assertThat(avgRating.food).isEqualTo(expectedFood);
        assertThat(avgRating.service).isEqualTo(expectedService);
        assertThat(avgRating.ambiance).isEqualTo(expectedAmbiance);
        assertThat(avgRating.overall).isEqualTo(expectedOverall);
    }
}