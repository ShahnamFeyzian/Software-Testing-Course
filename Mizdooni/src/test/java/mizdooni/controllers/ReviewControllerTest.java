package mizdooni.controllers;

import mizdooni.exceptions.*;
import mizdooni.model.Restaurant;
import mizdooni.model.Review;
import mizdooni.response.PagedList;
import mizdooni.response.Response;
import mizdooni.response.ResponseException;
import mizdooni.service.RestaurantService;
import mizdooni.service.ReviewService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import static mizdooni.controllers.ControllersTestUtils.*;
import static mizdooni.model.ModelTestUtils.*;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.*;

public class ReviewControllerTest {
    private Restaurant restaurant;
    private RestaurantService restaurantService;
    private ReviewService reviewService;
    private ReviewController controller;

    @BeforeEach
    public void setup() {
        restaurant = mock(Restaurant.class);
        when(restaurant.getId()).thenReturn(DEFAULT_RESTAURANT_ID);
        when(restaurant.getName()).thenReturn(DEFAULT_NAME);
        restaurantService = mock(RestaurantService.class);
        when(restaurantService.getRestaurant(DEFAULT_RESTAURANT_ID)).thenReturn(restaurant);
        reviewService = mock(ReviewService.class);
        controller = new ReviewController(restaurantService, reviewService);
    }

    @Test
    public void getReviews_RestaurantIdDoesNotExist_ThrowsNotFound() {
        when(restaurantService.getRestaurant(DEFAULT_RESTAURANT_ID)).thenReturn(null);

        assertThatThrownBy(() -> controller.getReviews(DEFAULT_RESTAURANT_ID, DEFAULT_PAGE_NUM))
                .isInstanceOf(ResponseException.class)
                .hasMessage("restaurant not found")
                .extracting("status")
                .isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    public void getReviews_GetReviewsFailed_ThrowsBadRequest() throws RestaurantNotFound {
        doThrow(new RestaurantNotFound()).when(reviewService).getReviews(DEFAULT_RESTAURANT_ID, DEFAULT_PAGE_NUM);

        assertThatThrownBy(() -> controller.getReviews(DEFAULT_RESTAURANT_ID, DEFAULT_PAGE_NUM))
                .isInstanceOf(ResponseException.class)
                .extracting("status")
                .isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    public void getReviews_SuccessfulGetReviews_ReturnsOkStatusWithPagedReviews() throws RestaurantNotFound {
        PagedList<Review> expectedReviews = mock(PagedList.class);
        when(reviewService.getReviews(DEFAULT_RESTAURANT_ID, DEFAULT_PAGE_NUM)).thenReturn(expectedReviews);
        String expectedMessage = "reviews for restaurant (" + DEFAULT_RESTAURANT_ID + "): " + DEFAULT_NAME;

        Response response = controller.getReviews(DEFAULT_RESTAURANT_ID, DEFAULT_PAGE_NUM);
        PagedList<Review> actualReviews = (PagedList<Review>) response.getData();
        HttpStatus actualStatus = response.getStatus();
        String actualMessage = response.getMessage();

        assertThat(actualReviews).isEqualTo(expectedReviews);
        assertThat(actualStatus).isEqualTo(HttpStatus.OK);
        assertThat(actualMessage).isEqualTo(expectedMessage);
    }
}
