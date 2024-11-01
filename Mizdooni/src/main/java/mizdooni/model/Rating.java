package mizdooni.model;

import java.util.Objects;

public class Rating {
    public double food;
    public double service;
    public double ambiance;
    public double overall;

    public Rating() {}

    public Rating(double food, double service, double ambiance, double overall) {
        this.food = food;
        this.service = service;
        this.ambiance = ambiance;
        this.overall = overall;
    }

    public int getStarCount() {
        return (int) Math.min(Math.round(overall), 5);
    }

    @Override
    public boolean equals(Object obj){
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Rating)) {
            return false;
        }
        Rating other = (Rating) obj;
        return food == other.food && service == other.service &&
                ambiance == other.ambiance && overall == other.overall;
    }

    @Override
    public int hashCode() {
        return Objects.hash(food, service, ambiance, overall);
    }
}
