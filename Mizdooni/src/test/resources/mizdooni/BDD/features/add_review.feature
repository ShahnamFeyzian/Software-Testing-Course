Feature: Add review to restaurant

  Scenario: Adding a review from a new user
    Given a restaurant with 3 reviews
    And a new user wants to add a review
    When the user adds their review
    Then the review should be added to the restaurant's review list
    And the total number of reviews should increase by 1


  Scenario: Adding a review from a user who already has a review
    Given a restaurant with 3 reviews
    And a user has already left a review
    When the user adds their review
    Then the previous review from that user should be replaced
    And the total number of reviews should remain the same