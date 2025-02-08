package mizdooni.BDD.steps;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import mizdooni.model.Address;
import mizdooni.model.Reservation;
import mizdooni.model.User;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.spy;

public class AddReservationStepDefs {

    private User user;
    private Reservation firstReservation;
    private Reservation secondReservation;

    @Given("a user with no reservations")
    public void a_user_with_no_reservations() {
        user = new User("testuser", "password", "test@example.com",
                new Address("Country", "City", "Street"), User.Role.client);
    }

    @When("a reservation is added to the user")
    public void a_reservation_is_added_to_the_user() {
        firstReservation = spy(new Reservation(user, null, null, null));
        user.addReservation(firstReservation);
    }

    @Then("the user's reservation list should contain the reservation")
    public void the_users_reservation_list_should_contain_the_reservation() {
        assertThat(user.getReservations()).containsExactly(firstReservation);
    }

    @And("the reservation number should be {int}")
    public void the_reservation_number_should_be(Integer expectedNumber) {
        assertThat(firstReservation.getReservationNumber()).isEqualTo(expectedNumber);
    }

    @Given("a user with one reservation")
    public void a_user_with_one_reservation() {
        a_user_with_no_reservations();
        a_reservation_is_added_to_the_user();
    }

    @When("another reservation is added to the user")
    public void another_reservation_is_added_to_the_user() {
        secondReservation = spy(new Reservation(user, null, null, null));
        user.addReservation(secondReservation);
    }

    @Then("the user's reservation list should contain both reservations")
    public void the_users_reservation_list_should_contain_both_reservations() {
        assertThat(user.getReservations()).containsExactly(firstReservation, secondReservation);
    }

    @And("the second reservation's number should be {int}")
    public void the_second_reservations_number_should_be(Integer expectedNumber) {
        assertThat(secondReservation.getReservationNumber()).isEqualTo(expectedNumber);
    }
}