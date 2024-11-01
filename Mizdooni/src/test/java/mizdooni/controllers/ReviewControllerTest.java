package mizdooni.controllers;

import mizdooni.exceptions.*;
import mizdooni.model.Reservation;
import mizdooni.model.Restaurant;
import mizdooni.response.Response;
import mizdooni.response.ResponseException;
import mizdooni.service.ReservationService;
import mizdooni.service.RestaurantService;
import mizdooni.service.ReviewService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.http.HttpStatus;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Stream;

import static mizdooni.controllers.ControllerUtils.PARAMS_BAD_TYPE;
import static mizdooni.controllers.ControllerUtils.PARAMS_MISSING;
import static mizdooni.controllers.ControllersTestUtils.*;
import static mizdooni.model.ModelTestUtils.*;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.*;

public class ReviewControllerTest {
    private Restaurant dummyRestaurant;
    private RestaurantService restaurantService;
    private ReviewService reviewService;
    private ReviewController controller;

    @BeforeEach
    public void setup() {
        dummyRestaurant = mock(Restaurant.class);
        restaurantService = mock(RestaurantService.class);
        when(restaurantService.getRestaurant(DEFAULT_RESTAURANT_ID)).thenReturn(dummyRestaurant);
        reviewService = mock(ReviewService.class);
        controller = new ReviewController(restaurantService, reviewService);
    }


}
