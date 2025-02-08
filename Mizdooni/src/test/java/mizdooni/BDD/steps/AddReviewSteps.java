package mizdooni.BDD.steps;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import mizdooni.model.Restaurant;
import mizdooni.model.Review;
import mizdooni.model.User;

import java.util.List;

import static mizdooni.model.ModelTestUtils.*;
import static org.assertj.core.api.Assertions.assertThat;

public class AddReviewSteps {

    private Restaurant restaurant;
    private Review review;
    private int initialReviewCount;

    @Given("a restaurant with {int} reviews")
    public void aRestaurantWithReviews(int count) {
        restaurant = getDefaultRestaurant();
        addReviewsWithRandomRatingWithUniqueUserToRestaurant(restaurant, count);
    }

    @Given("a new user wants to add a review")
    public void aNewUserWantsToAddAReview() {
        User newUser = getDefaultClientUser();
        review = getReviewWithDefaultRatingForUser(newUser);
    }

    @Given("a user has already left a review")
    public void aUserHasAlreadyLeftAReview() {
        review = restaurant.getReviews().get(0);
    }

    @When("the user adds their review")
    public void theUserAddsTheirReview() {
        initialReviewCount = restaurant.getReviews().size();
        restaurant.addReview(review);
    }

    @Then("the review should be added to the restaurant's review list")
    public void theReviewShouldBeAddedToTheRestaurantsReviewList() {
        List<Review> restaurantReviews = restaurant.getReviews();
        assertThat(restaurantReviews).hasSize(initialReviewCount + 1);
        assertThat(restaurantReviews.get(restaurantReviews.size() - 1).getUser()).isEqualTo(review.getUser());
    }

    @Then("the total number of reviews should increase by {int}")
    public void theTotalNumberOfReviewsShouldIncreaseBy(int increase) {
        List<Review> restaurantReviews = restaurant.getReviews();
        assertThat(restaurantReviews).hasSize(initialReviewCount + increase);
    }

    @Then("the previous review from that user should be replaced")
    public void thePreviousReviewFromThatUserShouldBeReplaced() {
        List<Review> restaurantReviews = restaurant.getReviews();
        assertThat(restaurantReviews).noneMatch(r -> r.getUser().equals(review.getUser()) && !r.equals(review));
    }

    @Then("the total number of reviews should remain the same")
    public void theTotalNumberOfReviewsShouldRemainTheSame() {
        List<Review> restaurantReviews = restaurant.getReviews();
        assertThat(restaurantReviews).hasSize(initialReviewCount);
    }

    @Then("no duplicate review should be added")
    public void noDuplicateReviewShouldBeAdded() {
        List<Review> restaurantReviews = restaurant.getReviews();
        assertThat(restaurantReviews.stream().filter(r -> r.equals(review)).count()).isEqualTo(1);
    }
}
