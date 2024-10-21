package mizdooni.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.*;

class TableTest {

    private Table table;
    private Reservation reservation1;


    @BeforeEach
    void setUp() {
        Restaurant restaurant1 = ModelTestUtils.getDefaultRestaurant();
        ModelTestUtils.addTablesWithDefaultSeatsToRestaurant(restaurant1, 3);
        table = restaurant1.getTable(1);
        User clientUser = ModelTestUtils.getDefaultClientUser();
        reservation1 = ModelTestUtils.getDefaultReservation(clientUser, restaurant1, table);
        table.addReservation(reservation1);
    }

    @Test
    void testAddReservation() {
        assertEquals(1, table.getReservations().size());
        assertTrue(table.getReservations().contains(reservation1));
    }

    @Test
    void testIsReservedTrue() {
        LocalDateTime dateTime = reservation1.getDateTime();
        assertTrue(table.isReserved(dateTime));
    }

    @Test
    void testIsReservedFalseForDifferentTime() {
        LocalDateTime dateTime = LocalDateTime.now();
        assertFalse(table.isReserved(dateTime));
    }

    @Test
    void testIsReservedFalseForCancelledReservation() {
        reservation1.cancel();
        LocalDateTime dateTime = reservation1.getDateTime();
        assertFalse(table.isReserved(dateTime));
    }

}