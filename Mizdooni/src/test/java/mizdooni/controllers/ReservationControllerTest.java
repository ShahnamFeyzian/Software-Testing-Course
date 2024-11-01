package mizdooni.controllers;

import mizdooni.exceptions.*;
import mizdooni.model.Reservation;
import mizdooni.model.Restaurant;
import mizdooni.response.Response;
import mizdooni.response.ResponseException;
import mizdooni.service.ReservationService;
import mizdooni.service.RestaurantService;
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
    public void getReservations_SuccessGettingReservations_ReturnsOkStatusWithReservations()
            throws UserNotManager, TableNotFound, InvalidManagerRestaurant, RestaurantNotFound {
        List<Reservation> expectedResult = new ArrayList<>();
        Reservation dummyReservation = mock(Reservation.class);
        expectedResult.add(dummyReservation);
        when(reservationService.getReservations(DEFAULT_RESTAURANT_ID, DEFAULT_TABLE_ID, DEFAULT_LOCAL_DATE))
                .thenReturn(expectedResult);

        Response response = controller.getReservations(DEFAULT_RESTAURANT_ID, DEFAULT_TABLE_ID, DEFAULT_DATE_FORMAT);
        List<Reservation> actualData = (List<Reservation>) response.getData();
        HttpStatus actualStatus = response.getStatus();

        assertThat(actualStatus).isEqualTo(HttpStatus.OK);
        assertThat(actualData).containsExactly(dummyReservation);
    }

    @Test
    public void getReservations_SuccessGettingReservationsAndLocalDateIsNull_ReturnsOkStatusWithReservations()
            throws UserNotManager, TableNotFound, InvalidManagerRestaurant, RestaurantNotFound {
        List<Reservation> expectedResult = new ArrayList<>();
        Reservation dummyReservation = mock(Reservation.class);
        expectedResult.add(dummyReservation);
        when(reservationService.getReservations(DEFAULT_RESTAURANT_ID, DEFAULT_TABLE_ID, null))
                .thenReturn(expectedResult);

        Response response = controller.getReservations(DEFAULT_RESTAURANT_ID, DEFAULT_TABLE_ID, null);
        List<Reservation> actualData = (List<Reservation>) response.getData();
        HttpStatus actualStatus = response.getStatus();

        assertThat(actualStatus).isEqualTo(HttpStatus.OK);
        assertThat(actualData).containsExactly(dummyReservation);
    }

    @Test
    public void getCustomerReservations_GetCustomerReservationsFailed_ThrowsBadRequest() throws UserNotFound, UserNoAccess {
        doThrow(new UserNoAccess()).when(reservationService).getCustomerReservations(DEFAULT_CUSTOMER_ID);

        assertThatThrownBy(() -> controller.getCustomerReservations(DEFAULT_CUSTOMER_ID))
                .isInstanceOf(ResponseException.class)
                .extracting("status")
                .isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    public void getCustomerReservation_SuccessGetCustomerReservation_ReturnsOkStatusWithReservations()
            throws UserNotFound, UserNoAccess {
        List<Reservation> expectedResult = new ArrayList<>();
        Reservation dummyReservation = mock(Reservation.class);
        expectedResult.add(dummyReservation);
        when(reservationService.getCustomerReservations(DEFAULT_CUSTOMER_ID)).thenReturn(expectedResult);

        Response response = controller.getCustomerReservations(DEFAULT_CUSTOMER_ID);
        List<Reservation> actualData = (List<Reservation>) response.getData();
        HttpStatus actualStatus = response.getStatus();

        assertThat(actualData).containsExactly(dummyReservation);
        assertThat(actualStatus).isEqualTo(HttpStatus.OK);
    }

    @Test
    public void getAvailableTimes_RestaurantIdDoesNotExist_ThrowsNotFound() {
        when(restaurantService.getRestaurant(DEFAULT_RESTAURANT_ID)).thenReturn(null);

        assertThatThrownBy(() -> controller.getAvailableTimes(DEFAULT_RESTAURANT_ID, DEFAULT_PEOPLE_NUMBER, DEFAULT_DATE_FORMAT))
                .isInstanceOf(ResponseException.class)
                .hasMessage("restaurant not found")
                .extracting("status")
                .isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    public void getAvailableTimes_InvalidLocalDateFormat_ThrowsBadRequest() {
        assertThatThrownBy(() -> controller.getAvailableTimes(DEFAULT_RESTAURANT_ID, DEFAULT_PEOPLE_NUMBER, "!@#$%^"))
                .isInstanceOf(ResponseException.class)
                .hasMessage(PARAMS_BAD_TYPE)
                .extracting("status")
                .isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    public void getAvailableTimes_GetAvailableTimesFailed_ThrowsBadRequest()
            throws DateTimeInThePast, RestaurantNotFound, BadPeopleNumber {
        doThrow(new DateTimeInThePast()).when(reservationService).
                getAvailableTimes(DEFAULT_RESTAURANT_ID, DEFAULT_PEOPLE_NUMBER, DEFAULT_LOCAL_DATE);

        assertThatThrownBy(() -> controller.getAvailableTimes(DEFAULT_RESTAURANT_ID, DEFAULT_PEOPLE_NUMBER, DEFAULT_DATE_FORMAT))
                .isInstanceOf(ResponseException.class)
                .extracting("status")
                .isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    public void getAvailableTimes_SuccessGetAvailableTimes_ReturnsOkStatusWithTimes()
            throws DateTimeInThePast, RestaurantNotFound, BadPeopleNumber {
        List<LocalTime> expectedResult = new ArrayList<>();
        LocalTime dummyTime = mock(LocalTime.class);
        expectedResult.add(dummyTime);
        when(reservationService.getAvailableTimes(DEFAULT_RESTAURANT_ID, DEFAULT_PEOPLE_NUMBER, DEFAULT_LOCAL_DATE))
                .thenReturn(expectedResult);

        Response response = controller.getAvailableTimes(DEFAULT_RESTAURANT_ID, DEFAULT_PEOPLE_NUMBER, DEFAULT_DATE_FORMAT);
        List<LocalTime> actualData = (List<LocalTime>) response.getData();
        HttpStatus actualStatus = response.getStatus();

        assertThat(actualData).containsExactly(dummyTime);
        assertThat(actualStatus).isEqualTo(HttpStatus.OK);
    }

    @Test
    public void addReservation_RestaurantIdDoesNotExist_ThrowsNotFound() {
        when(restaurantService.getRestaurant(DEFAULT_RESTAURANT_ID)).thenReturn(null);

        assertThatThrownBy(() -> controller.addReservation(DEFAULT_RESTAURANT_ID, null))
                .isInstanceOf(ResponseException.class)
                .hasMessage("restaurant not found")
                .extracting("status")
                .isEqualTo(HttpStatus.NOT_FOUND);
    }

    @ParameterizedTest(name = "Missed field: {0}")
    @MethodSource("addReservationParamsButOneOfThemDoesNotExist")
    public void addReservation_NecessaryParamsAreNotExist_ThrowsBadRequest(String missedField, HashMap<String, String> params) {
        assertThatThrownBy(() -> controller.addReservation(DEFAULT_RESTAURANT_ID, params))
                .isInstanceOf(ResponseException.class)
                .hasMessage(PARAMS_MISSING)
                .extracting("status")
                .isEqualTo(HttpStatus.BAD_REQUEST);
    }
    private static Stream<Arguments> addReservationParamsButOneOfThemDoesNotExist() {
        List<String> paramsKey = getAddReservationParamsKeyLis();
        List<Arguments> args = new ArrayList<>();
        for (String currentParam : paramsKey) {
            HashMap<String, String> params = createAddReservationParams();
            params.remove(currentParam);
            args.add(Arguments.of(currentParam, params));
        }
        return args.stream();
    }

    @ParameterizedTest(name = "Bad type field: {0}")
    @MethodSource("addReservationParamsButOneOfThemIsNull")
    public void addReservation_PassedParamsDoNotHaveCorrectType_ThrowsBadRequest(String field, HashMap<String, String> params) {
        assertThatThrownBy(() -> controller.addReservation(DEFAULT_RESTAURANT_ID, params))
                .isInstanceOf(ResponseException.class)
                .hasMessage(PARAMS_BAD_TYPE)
                .extracting("status")
                .isEqualTo(HttpStatus.BAD_REQUEST);
    }
    private static Stream<Arguments> addReservationParamsButOneOfThemIsNull() {
        List<String> paramsKey = getAddReservationParamsKeyLis();
        List<Arguments> args = new ArrayList<>();
        for (String currentParam : paramsKey) {
            HashMap<String, String> params = createAddReservationParams();
            params.put(currentParam, "!@#$%");
            args.add(Arguments.of(currentParam, params));
        }
        return args.stream();
    }

    @Test
    public void addReservation_ReserveTableFailed_ThrowsBadRequest()
            throws UserNotFound, DateTimeInThePast, TableNotFound, ReservationNotInOpenTimes,
            ManagerReservationNotAllowed, RestaurantNotFound, InvalidWorkingTime {
        doThrow(new DateTimeInThePast()).when(reservationService).
                reserveTable(DEFAULT_RESTAURANT_ID, DEFAULT_PEOPLE_NUMBER, DEFAULT_LOCAL_DATE_TIME);

        assertThatThrownBy(() -> controller.addReservation(DEFAULT_RESTAURANT_ID, createAddReservationParams()))
                .isInstanceOf(ResponseException.class)
                .extracting("status")
                .isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    public void addReservation_SuccessReserveTable_ReturnsOkStatusWithReservation()
            throws UserNotFound, DateTimeInThePast, TableNotFound, ReservationNotInOpenTimes,
            ManagerReservationNotAllowed, RestaurantNotFound, InvalidWorkingTime {
        Reservation dummyReservation = mock(Reservation.class);
        when(reservationService.reserveTable(DEFAULT_RESTAURANT_ID, DEFAULT_PEOPLE_NUMBER, DEFAULT_LOCAL_DATE_TIME))
                .thenReturn(dummyReservation);

        Response response = controller.addReservation(DEFAULT_RESTAURANT_ID, createAddReservationParams());
        Reservation actualReservation = (Reservation) response.getData();
        HttpStatus actualStatus = response.getStatus();

        assertThat(actualReservation).isEqualTo(dummyReservation);
        assertThat(actualStatus).isEqualTo(HttpStatus.OK);
    }

    @Test
    public void cancelReservation_CancelReservationFailed_ThrowsBadRequest()
            throws ReservationCannotBeCancelled, UserNotFound, ReservationNotFound {
        doThrow(new UserNotFound()).when(reservationService).cancelReservation(DEFAULT_RESERVATION_NUM);

        assertThatThrownBy(() -> controller.cancelReservation(DEFAULT_RESERVATION_NUM))
                .isInstanceOf(ResponseException.class)
                .extracting("status")
                .isEqualTo(HttpStatus.BAD_REQUEST);
    }
    @Test
    public void cancelReservation_SuccessfulCancelReservation_ReturnsOkStatus()
            throws ReservationCannotBeCancelled, UserNotFound, ReservationNotFound {
        doNothing().when(reservationService).cancelReservation(DEFAULT_RESERVATION_NUM);

        Response response = controller.cancelReservation(DEFAULT_RESERVATION_NUM);
        HttpStatus actualStatus = response.getStatus();

        assertThat(actualStatus).isEqualTo(HttpStatus.OK);
    }
}
