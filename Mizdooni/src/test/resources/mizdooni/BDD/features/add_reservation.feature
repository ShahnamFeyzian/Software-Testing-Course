Feature: Add reservation to a user
  As a system user
  I want to be able to add reservations to a user's account
  So that reservations can be tracked and managed.

  Scenario: Add a valid reservation
    Given a user with no reservations
    When a reservation is added to the user
    Then the user's reservation list should contain the reservation
    And the reservation number should be 0

  Scenario: Add multiple reservations
    Given a user with one reservation
    When another reservation is added to the user
    Then the user's reservation list should contain both reservations
    And the second reservation's number should be 1
