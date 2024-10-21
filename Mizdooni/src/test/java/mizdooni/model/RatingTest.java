package mizdooni.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class RatingTest {

    @Test
    void testGetStarCount_ExactlyFive() {
        Rating rating = ModelTestUtils.getRatingWithOverallRate(5);
        int starCount = rating.getStarCount();
        assertEquals(5, starCount);
    }

    @Test
    void testGetStarCount_GreaterThanFive() {
        Rating rating = ModelTestUtils.getRatingWithOverallRate(5.9);
        int starCount = rating.getStarCount();
        assertEquals(5, starCount);
    }

    @Test
    void testGetStarCount_RoundsDown() {
        Rating rating = ModelTestUtils.getRatingWithOverallRate(2.4);
        int starCount = rating.getStarCount();
        assertEquals(2, starCount);
    }

    @Test
    void testGetStarCount_RoundsUp() {
        Rating rating = ModelTestUtils.getRatingWithOverallRate(3.8);
        int starCount = rating.getStarCount();
        assertEquals(4, starCount);
    }
}
