package mizdooni.model;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class RestaurantSearchFilter {
    private String name;
    private String type;
    private String location;
    private String sort;
    private String order;

    public List<Restaurant> filter(List<Restaurant> restaurants) {
        List<Restaurant> rest = new ArrayList<>(restaurants);
        if (name != null) {
            rest = rest.stream().filter(r -> r.getName().contains(name)).collect(Collectors.toList());
        }
        if (type != null) {
            rest = rest.stream().filter(r -> r.getType().equals(type)).collect(Collectors.toList());
        }
        if (location != null) {
            rest = rest.stream().filter(r -> r.getAddress().getCity().equals(location)).collect(Collectors.toList());
        }
        if (sort != null) {
            if (sort.equals("rating")) {
                Comparator<Restaurant> comparator = Comparator.comparing(r -> r.getAverageRating().overall);
                if (order != null && order.equals("asc")) {
                    comparator = comparator.reversed();
                }
                rest.sort(comparator.reversed());
            } else if (sort.equals("reviews")) {
                Comparator<Restaurant> comparator = Comparator.comparingInt(r -> r.getReviews().size());
                if (order != null && order.equals("asc")) {
                    comparator = comparator.reversed();
                }
                rest.sort(comparator.reversed());
            }
        }
        return rest;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setSort(String sort) {
        this.sort = sort;
    }

    public void setOrder(String order) {
        this.order = order;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RestaurantSearchFilter that = (RestaurantSearchFilter) o;
        if (!Objects.equals(name, that.name)) return false;
        if (!Objects.equals(type, that.type)) return false;
        if (!Objects.equals(location, that.location)) return false;
        if (!Objects.equals(sort, that.sort)) return false;
        if (!Objects.equals(order, that.order)) return false;
        return true;
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, type, location, sort, order);
    }
}
