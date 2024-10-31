package mizdooni.model;

import java.util.Objects;

public class Address {
    private String country;
    private String city;
    private String street;

    public Address(String country, String city, String street) {
        this.country = country;
        this.city = city;
        this.street = street;
    }

    public String getCountry() {
        return country;
    }

    public String getCity() {
        return city;
    }

    public String getStreet() {
        return street;
    }

    @Override
    public boolean equals(Object obj){
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Address)) {
            return false;
        }
        Address other = (Address) obj;
        return country.equals(other.country) && city.equals(other.city) && street.equals(other.street);
    }

    @Override
    public int hashCode() {
        return Objects.hash(country, city, street);
    }
}
