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
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.http.HttpStatus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Stream;

import static mizdooni.controllers.ControllerUtils.PARAMS_BAD_TYPE;
import static mizdooni.controllers.ControllerUtils.PARAMS_MISSING;
import static mizdooni.controllers.ControllersTestUtils.*;
import static mizdooni.model.ModelTestUtils.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

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

    @Test
    public void addReview_RestaurantIdDoesNotExist_ThrowsNotFound() {
        when(restaurantService.getRestaurant(DEFAULT_RESTAURANT_ID)).thenReturn(null);

        assertThatThrownBy(() -> controller.addReview(DEFAULT_RESTAURANT_ID, createAddReviewParams()))
                .isInstanceOf(ResponseException.class)
                .hasMessage("restaurant not found")
                .extracting("status")
                .isEqualTo(HttpStatus.NOT_FOUND);
    }

    @ParameterizedTest(name = "Missed field: {0}")
    @MethodSource("addReviewParamsButOneOfThemDoesNotExist")
    public void addReview_NecessaryParamsAreNotExist_ThrowsBadRequest(String missedField, HashMap<String, Object> params) {
        assertThatThrownBy(() -> controller.addReview(DEFAULT_RESTAURANT_ID, params))
                .isInstanceOf(ResponseException.class)
                .hasMessage(PARAMS_MISSING)
                .extracting("status")
                .isEqualTo(HttpStatus.BAD_REQUEST);
    }
    private static Stream<Arguments> addReviewParamsButOneOfThemDoesNotExist() {
        List<String> paramsKey = getAddReviewParamsKeyList();
        List<Arguments> args = new ArrayList<>();
        for (String currentParam : paramsKey) {
            HashMap<String, Object> params = createAddReviewParams();
            params.remove(currentParam);
            args.add(Arguments.of(currentParam, params));
        }
        return args.stream();
    }

    @ParameterizedTest(name = "Bad type field: {0}")
    @MethodSource("addReviewParamsButOneOfThemIsNull")
    public void addReview_PassedParamsDoNotHaveCorrectType_ThrowsBadRequest(String field, HashMap<String, Object> params) {
        assertThatThrownBy(() -> controller.addReview(DEFAULT_RESTAURANT_ID, params))
                .isInstanceOf(ResponseException.class)
                .hasMessage(PARAMS_BAD_TYPE)
                .extracting("status")
                .isEqualTo(HttpStatus.BAD_REQUEST);
    }
    private static Stream<Arguments> addReviewParamsButOneOfThemIsNull() {
        List<String> paramsKey = getAddReviewParamsKeyList();
        List<Arguments> args = new ArrayList<>();
        for (String currentParam : paramsKey) {
            HashMap<String, Object> params = createAddReviewParams();
            params.put(currentParam, new Object());
            args.add(Arguments.of(currentParam, params));
        }
        return args.stream();
    }

    @Test
    public void addReview_AddReviewFailed_ThrowsBadRequest()
            throws UserNotFound, ManagerCannotReview, UserHasNotReserved, RestaurantNotFound, InvalidReviewRating {
        doThrow(new RestaurantNotFound()).when(reviewService)
                .addReview(DEFAULT_RESTAURANT_ID, getDefaultRating(), DEFAULT_COMMENT);


        assertThatThrownBy(() -> controller.addReview(DEFAULT_RESTAURANT_ID, createAddReviewParams()))
                .isInstanceOf(ResponseException.class)
                .extracting("status")
                .isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    public void addReview_SuccessAddReview_ReturnsOkStatus()
            throws UserNotFound, ManagerCannotReview, UserHasNotReserved, RestaurantNotFound, InvalidReviewRating {
        doNothing().when(reviewService).addReview(DEFAULT_RESTAURANT_ID, getDefaultRating(), DEFAULT_COMMENT);

        Response response = controller.addReview(DEFAULT_RESTAURANT_ID, createAddReviewParams());
        HttpStatus actualStatus = response.getStatus();

        assertThat(actualStatus).isEqualTo(HttpStatus.OK);
    }
}
