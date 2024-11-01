package mizdooni.controllers;

import mizdooni.model.Address;
import mizdooni.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static mizdooni.controllers.ControllerUtils.DATETIME_FORMATTER;
import static mizdooni.controllers.ControllerUtils.DATE_FORMATTER;
import static mizdooni.model.ModelTestUtils.*;

public class ControllersTestUtils {
    public static final String DEFAULT_DATE_FORMAT = DEFAULT_LOCAL_DATE.format(DATE_FORMATTER);
    public static final String DEFAULT_DATE_TIME_FORMAT = DEFAULT_LOCAL_DATE_TIME.format(DATETIME_FORMATTER);
    public static final String USER_NAME_KEY = "username";
    public static final String USER_PASS_KEY = "password";
    public static final String EMAIL_KEY = "email";
    public static final String ADDRESS_KEY = "address";
    public static final String ROLE_KEY = "role";
    public static final String PEOPLE_NUMBER_KEY = "people";
    public static final String DATE_TIME_KEY = "datetime";
    public static final int DEFAULT_RESTAURANT_ID = 123;
    public static final int DEFAULT_TABLE_ID = 123;
    public static final int DEFAULT_CUSTOMER_ID = 123;
    public static final int DEFAULT_RESERVATION_NUM = 2;
    public static final int DEFAULT_PEOPLE_NUM = 4;
    public static final int DEFAULT_PAGE_NUM = 1;
    public static HashMap<String, String> createLoginParams() {
        HashMap<String, String> params = new HashMap<>();
        params.put(USER_NAME_KEY, DEFAULT_NAME);
        params.put(USER_PASS_KEY, DEFAULT_PASS);
        return params;
    }
    public static List<String> getSignupParamsKeyList() {
        List<String> params = new ArrayList<>();
        params.add(USER_NAME_KEY);
        params.add(USER_PASS_KEY);
        params.add(EMAIL_KEY);
        params.add(ADDRESS_KEY);
        params.add(ROLE_KEY);
        return params;
    }
    public static List<String> getAddReservationParamsKeyLis() {
        List<String> params = new ArrayList<>();
        params.add(PEOPLE_NUMBER_KEY);
        params.add(DATE_TIME_KEY);
        return params;
    }
    public static HashMap<String, Object> createSignupParamsBasedOn(HashMap<String, Object> baseParams) {
        List<String> allKeys = getSignupParamsKeyList();
        HashMap<String, Object> defaultParams = createSignupParams();
        HashMap<String, Object> params = new HashMap<>();
        for (String key : allKeys) {
            if (baseParams.containsKey(key)) {
                params.put(key, baseParams.get(key));
            } else {
                params.put(key, defaultParams.get(key));
            }
        }
        return params;
    }
    public static HashMap<String, Object> createSignupNullParams() {
        HashMap<String, Object> params = new HashMap<>();
        params.put(USER_NAME_KEY, null);
        params.put(USER_PASS_KEY, null);
        params.put(EMAIL_KEY, null);
        params.put(ADDRESS_KEY, null);
        params.put(ROLE_KEY, null);
        return params;
    }
    public static HashMap<String, Object> createSignupBlankParams() {
        HashMap<String, Object> params = new HashMap<>();
        params.put(USER_NAME_KEY, "");
        params.put(USER_PASS_KEY, "");
        params.put(EMAIL_KEY, "");
        params.put(ADDRESS_KEY, getDefaultAddress());
        params.put(ROLE_KEY, User.Role.client);
        return params;
    }
    public static HashMap<String, Object> createSignupParams() {
        HashMap<String, Object> params = new HashMap<>();
        params.put(USER_NAME_KEY, DEFAULT_NAME);
        params.put(USER_PASS_KEY, DEFAULT_PASS);
        params.put(EMAIL_KEY, DEFAULT_EMAIL);
        params.put(ADDRESS_KEY, createAddressHashMap(getDefaultAddress()));
        params.put(ROLE_KEY, User.Role.client.name());
        return params;
    }
    public static HashMap<String, String> createAddReservationParams() {
        HashMap<String, String> params = new HashMap<>();
        params.put(PEOPLE_NUMBER_KEY, Integer.toString(DEFAULT_PEOPLE_NUM));
        params.put(DATE_TIME_KEY, DEFAULT_DATE_TIME_FORMAT);
        return params;
    }
    public static HashMap<String, String> createAddressHashMap(Address address) {
        HashMap<String, String> addressMap = new HashMap<>();
        addressMap.put("country", address.getCountry());
        addressMap.put("city", address.getCity());
        addressMap.put("street", address.getStreet());
        return addressMap;
    }
}
