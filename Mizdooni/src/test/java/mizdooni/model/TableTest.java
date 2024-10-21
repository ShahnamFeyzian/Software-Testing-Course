package mizdooni.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

class TableTest {

    private Table table;
    private Reservation reservation;


    @BeforeEach
    void setUp() {
        Restaurant restaurant1 = ModelTestUtils.getDefaultRestaurant();
        ModelTestUtils.addTablesWithDefaultSeatsToRestaurant(restaurant1, 3);
        table = restaurant1.getTable(1);
        User clientUser = ModelTestUtils.getDefaultClientUser();
        reservation = spy(ModelTestUtils.getDefaultReservation(clientUser, restaurant1, table));
        table.addReservation(reservation);
    }

    @Test
    void addReservation_EmptyTable_AddsReservation() {
        assertThat(table.getReservations().size()).isEqualTo(1);
        assertThat(table.getReservations()).contains(reservation);
    }

    @Test
    void isReserved_HasReservation_ReturnsTrue() {
        LocalDateTime dateTime = ModelTestUtils.DEFAULT_LOCAL_DATE_TIME;
        when(reservation.getDateTime()).thenReturn(dateTime);
        when(reservation.isCancelled()).thenReturn(false);

        boolean isReserved = table.isReserved(dateTime);

        assertThat(isReserved).isTrue();
    }

    @Test
    void isReserved_DifferentDateTime_ReturnsFalse() {
        LocalDateTime dateTime = ModelTestUtils.DEFAULT_LOCAL_DATE_TIME;
        when(reservation.isCancelled()).thenReturn(false);
        when(reservation.getDateTime()).thenReturn(dateTime.plusDays(1));

        boolean isReserved = table.isReserved(dateTime);

        assertThat(isReserved).isFalse();
    }

    @Test
    void isReserved_SameDateTimeButCanceled_ReturnsFalse() {
        LocalDateTime dateTime = ModelTestUtils.DEFAULT_LOCAL_DATE_TIME;
        when(reservation.isCancelled()).thenReturn(true);
        when(reservation.getDateTime()).thenReturn(dateTime);

        boolean isReserved = table.isReserved(dateTime);

        assertThat(isReserved).isFalse();
    }

}
