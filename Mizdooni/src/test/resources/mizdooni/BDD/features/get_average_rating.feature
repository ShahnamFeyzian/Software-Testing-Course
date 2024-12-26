Feature: Calculate average rating

  Scenario: Restaurant with no reviews
    Given a restaurant with no reviews
    When I calculate the average rating
    Then all average scores should be zero

  Scenario: Restaurant with multiple reviews
    Given a restaurant with 3 random reviews
    When I calculate the average rating
    Then the average scores should be the average of all reviews