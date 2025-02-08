package mizdooni.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static mizdooni.model.ModelTestUtils.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;


public class UserTest {

    private User clientUser;
    private Restaurant restaurant;
    private Reservation reservation;

    @BeforeEach
    public void setUp() {
        clientUser = getDefaultClientUser();
        restaurant = getDefaultRestaurant();
        addTablesWithDefaultSeatsToRestaurant(restaurant, 3);

        Table table = restaurant.getTable(1);
        reservation = spy(getDefaultReservation(clientUser, restaurant, table));
        clientUser.addReservation(reservation);
    }

    @Test
    public void addReservation_TempUserDummyReservation_AddsReservation() {
        Reservation dummyReservation = getDummyReservation();
        User user = getDefaultClientUser();

        user.addReservation(dummyReservation);

        assertThat(user.getReservations()).containsExactly(dummyReservation);
    }

    @Test
    public void addReservation_TempUserDummyReservation_SetReservationNumberCalledWithZero() {
        Reservation dummyReservation = getDummyReservation();
        User user = getDefaultClientUser();

        user.addReservation(dummyReservation);

        verify(dummyReservation).setReservationNumber(0);
    }

    @Test
    public void checkReservation_EmptyUserWithEmptyReservation_ReturnsFalse() {
        User user = getDefaultClientUser();

        boolean hasReservation = user.checkReserved(restaurant);

        assertThat(hasReservation).isFalse();
    }

    @Test
    public void checkReservation_ReservationHasBeenCanceled_ReturnsFalse() {
        when(reservation.isCancelled()).thenReturn(true);

        boolean hasReservation = clientUser.checkReserved(restaurant);

        assertThat(hasReservation).isFalse();
    }

    @Test
    public void checkReservation_ReservationDateTimePassed_ReturnsFalse() {
        when(reservation.getDateTime()).thenReturn(LocalDateTime.now().minusDays(1));

        boolean hasReservation = clientUser.checkReserved(restaurant);

        assertThat(hasReservation).isFalse();
    }

    @Test
    public void checkReservation_ReservationHasDifferentRestaurant_ReturnsFalse() {
        when(reservation.getRestaurant()).thenReturn(getDefaultRestaurant());

        boolean hasReservation = clientUser.checkReserved(restaurant);

        assertThat(hasReservation).isFalse();
    }

    @Test
    public void checkReserved_HasProperReservation_ReturnsTrue() {
        when(reservation.isCancelled()).thenReturn(false);
        when(reservation.getDateTime()).thenReturn(LocalDateTime.now().plusDays(1));
        when(reservation.getRestaurant()).thenReturn(restaurant);

        boolean hasReservation = clientUser.checkReserved(restaurant);

        assertThat(hasReservation).isTrue();
    }

    @Test
    public void getReservation_HasProperReservation_ReturnsReservation() {
        int reservationNumber = 4;
        Reservation targetReservation = getDummyReservation();
        when(targetReservation.getReservationNumber()).thenReturn(reservationNumber);
        when(targetReservation.isCancelled()).thenReturn(false);
        clientUser.addReservation(targetReservation);

        Reservation resultReservation = clientUser.getReservation(reservationNumber);

        assertThat(resultReservation).isEqualTo(targetReservation);
    }

    @Test
    public void getReservation_ReservationHasDifferentReservationNumber_ReturnsNull() {
        int reservationNumber = 4;
        Reservation targetReservation = getDummyReservation();
        when(targetReservation.getReservationNumber()).thenReturn(reservationNumber+1);
        when(targetReservation.isCancelled()).thenReturn(false);
        clientUser.addReservation(targetReservation);

        Reservation resultReservation = clientUser.getReservation(reservationNumber);

        assertThat(resultReservation).isNull();
    }

    @Test
    public void getReservation_ReservationHasBeenCanceled_ReturnsNull() {
        int reservationNumber = 4;
        Reservation targetReservation = getDummyReservation();
        when(targetReservation.getReservationNumber()).thenReturn(reservationNumber);
        when(targetReservation.isCancelled()).thenReturn(true);
        clientUser.addReservation(targetReservation);

        Reservation resultReservation = clientUser.getReservation(reservationNumber);

        assertThat(resultReservation).isNull();
    }

    @Test
    public void checkPassword_RightPassword_ReturnsTrue(){
        assertThat(clientUser.checkPassword(DEFAULT_PASS)).isTrue();
    }

    @Test
    public void checkPassword_WrongPassword_ReturnsFalse(){
        assertThat(clientUser.checkPassword(DEFAULT_PASS+"for being wrong")).isFalse();
    }
}
