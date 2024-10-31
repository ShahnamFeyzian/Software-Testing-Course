package mizdooni.controllers;

import mizdooni.exceptions.InvalidManagerRestaurant;
import mizdooni.exceptions.RestaurantNotFound;
import mizdooni.exceptions.TableNotFound;
import mizdooni.exceptions.UserNotManager;
import mizdooni.model.Reservation;
import mizdooni.model.Restaurant;
import mizdooni.response.Response;
import mizdooni.response.ResponseException;
import mizdooni.service.ReservationService;
import mizdooni.service.RestaurantService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.util.ArrayList;

import static mizdooni.controllers.ControllerUtils.PARAMS_BAD_TYPE;
import static mizdooni.controllers.ControllersTestUtils.*;
import static mizdooni.model.ModelTestUtils.*;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.*;

public class ReservationControllerTest {
    private Restaurant dummyRestaurant;
    private RestaurantService restaurantService;
    private ReservationService reservationService;
    private ReservationController controller;

    @BeforeEach
    public void setup() {
        dummyRestaurant = mock(Restaurant.class);
        restaurantService = mock(RestaurantService.class);
        reservationService = mock(ReservationService.class);
        when(restaurantService.getRestaurant(DEFAULT_RESTAURANT_ID)).thenReturn(dummyRestaurant);
        controller = new ReservationController(restaurantService, reservationService);
    }

    @Test
    public void getReservations_RestaurantIdDoesNotExist_ThrowsNotFound() {
        when(restaurantService.getRestaurant(DEFAULT_RESTAURANT_ID)).thenReturn(null);

        assertThatThrownBy(() -> controller.getReservations(DEFAULT_RESTAURANT_ID, DEFAULT_TABLE_ID, null))
                .isInstanceOf(ResponseException.class)
                .hasMessage("restaurant not found")
                .extracting("status")
                .isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    public void getReservations_InvalidLocalDateFormat_ThrowsBadRequest() {
        assertThatThrownBy(() -> controller.getReservations(DEFAULT_RESTAURANT_ID, DEFAULT_TABLE_ID, "!@#$%^&*("))
                .isInstanceOf(ResponseException.class)
                .hasMessage(PARAMS_BAD_TYPE)
                .extracting("status")
                .isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    public void getReservations_GettingReservationsFailed_ThrowsBadRequest()
            throws UserNotManager, TableNotFound, InvalidManagerRestaurant, RestaurantNotFound {
        doThrow(new RestaurantNotFound()).when(reservationService)
                .getReservations(DEFAULT_RESTAURANT_ID, DEFAULT_TABLE_ID, DEFAULT_LOCAL_DATE);

        assertThatThrownBy(() -> controller.getReservations(DEFAULT_RESTAURANT_ID, DEFAULT_TABLE_ID, DEFAULT_DATE_FORMAT))
                .isInstanceOf(ResponseException.class)
                .extracting("status")
                .isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    public void getReservations_SuccessGettingReservations_ReturnsOkStatusAndGetReservationsCalled()
            throws UserNotManager, TableNotFound, InvalidManagerRestaurant, RestaurantNotFound {
        when(reservationService.getReservations(DEFAULT_RESTAURANT_ID, DEFAULT_TABLE_ID, DEFAULT_LOCAL_DATE))
                .thenReturn(new ArrayList<>());

        Response response = controller.getReservations(DEFAULT_RESTAURANT_ID, DEFAULT_TABLE_ID, DEFAULT_DATE_FORMAT);
        HttpStatus actualStatus = response.getStatus();

        assertThat(actualStatus).isEqualTo(HttpStatus.OK);
        verify(reservationService).getReservations(DEFAULT_RESTAURANT_ID, DEFAULT_TABLE_ID, DEFAULT_LOCAL_DATE);
    }

    @Test
    public void getReservations_SuccessGettingReservationsAndLocalDateIsNull_ReturnsOkStatusAndGetReservationsCalled()
            throws UserNotManager, TableNotFound, InvalidManagerRestaurant, RestaurantNotFound {
        when(reservationService.getReservations(DEFAULT_RESTAURANT_ID, DEFAULT_TABLE_ID, null))
                .thenReturn(new ArrayList<>());

        Response response = controller.getReservations(DEFAULT_RESTAURANT_ID, DEFAULT_TABLE_ID, null);
        HttpStatus actualStatus = response.getStatus();

        assertThat(actualStatus).isEqualTo(HttpStatus.OK);
        verify(reservationService).getReservations(DEFAULT_RESTAURANT_ID, DEFAULT_TABLE_ID, null);
    }
}
