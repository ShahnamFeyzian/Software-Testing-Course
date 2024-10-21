package mizdooni.model;

import org.junit.jupiter.api.Test;

import static mizdooni.model.ModelTestUtils.getRatingWithOverallRate;
import static org.assertj.core.api.Assertions.assertThat;


class RatingTest {

    @Test
    void getStarCount_RatingWithOverallFive_ReturnsFive() {
        Rating rating = getRatingWithOverallRate(5);

        int starCount = rating.getStarCount();

        assertThat(starCount).isEqualTo(5);
    }

    @Test
    void getStarCount_RatingWithOverallGreaterThanFive_ReturnsFive() {
        Rating rating = getRatingWithOverallRate(5.9);

        int starCount = rating.getStarCount();

        assertThat(starCount).isEqualTo(5);
    }

    @Test
    void getStarCount_OverallRatingDecimalPartIsLessThanHalf_RoundsDown() {
        Rating rating = getRatingWithOverallRate(2.4);

        int starCount = rating.getStarCount();

        assertThat(starCount).isEqualTo(2);
    }

    @Test
    void getStarCount_OverallRatingDecimalPartIsGreaterThanHalf_RoundsUp() {
        Rating rating = getRatingWithOverallRate(3.8);

        int starCount = rating.getStarCount();

        assertThat(starCount).isEqualTo(4);
    }
}
