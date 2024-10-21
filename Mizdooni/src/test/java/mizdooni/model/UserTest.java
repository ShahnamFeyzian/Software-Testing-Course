package mizdooni.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.List;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;


public class UserTest {

    private User clientUser;
    private Restaurant restaurant1;
    private Reservation reservation1;

    @BeforeEach
    public void setUp() {
        clientUser = ModelTestUtils.getDefaultClientUser();
        restaurant1 = ModelTestUtils.getDefaultRestaurant();
        ModelTestUtils.addTablesWithDefaultSeatsToRestaurant(restaurant1, 3);

        Table table1 = restaurant1.getTable(1);
        reservation1 = ModelTestUtils.getDefaultReservation(clientUser, restaurant1, table1);
        clientUser.addReservation(reservation1);
    }

    @Test
    public void testAddReservationBeAddedToReservationsSuccessfully() {
        assertEquals(1, clientUser.getReservations().size());
        assertEquals(0, reservation1.getReservationNumber());

        Table table2 = restaurant1.getTable(2);
        Reservation reservation2 = ModelTestUtils.getDefaultReservation(clientUser, restaurant1, table2);
        clientUser.addReservation(reservation2);

        assertEquals(2, clientUser.getReservations().size());
        assertEquals(1, reservation2.getReservationNumber());
    }

    @Test
    public void testCheckReservedTable(){
        assertTrue(clientUser.checkReserved(restaurant1));
    }


    @Test
    public void testCheckUnreservedTable(){
        Restaurant restaurant2;
        restaurant2 = ModelTestUtils.getDefaultRestaurant();
        ModelTestUtils.addTablesWithDefaultSeatsToRestaurant(restaurant2, 3);

        assertFalse(clientUser.checkReserved(restaurant2));
    }

    @Test
    public void testGetReservationValid() {
        int reservation1Number = 0;
        Reservation result = clientUser.getReservation(reservation1Number);
        assertNotNull(result);
        assertEquals(reservation1, result);
    }

    @Test
    public void testGetReservationInvalid() {
        int reservation1Number = 0;
        Reservation result = clientUser.getReservation(1);
        assertNull(result);
    }

    @Test
    public void testCheckPasswordIsTrue(){
        assertTrue(clientUser.checkPassword(ModelTestUtils.DEFAULT_PASS));
    }

    @Test
    public void testCheckPasswordIsFalse(){
        assertFalse(clientUser.checkPassword("incorrect pass"));
    }

}