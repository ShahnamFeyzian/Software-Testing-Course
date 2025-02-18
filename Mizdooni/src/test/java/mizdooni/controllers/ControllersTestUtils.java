package mizdooni.controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import mizdooni.model.Address;
import mizdooni.model.Rating;
import mizdooni.model.User;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static mizdooni.controllers.ControllerUtils.*;
import static mizdooni.model.ModelTestUtils.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.request;

public class ControllersTestUtils {
    public static final String DEFAULT_DATE_FORMAT = DEFAULT_LOCAL_DATE.format(DATE_FORMATTER);
    public static final String DEFAULT_TIME_FORMAT = DEFAULT_LOCAL_TIME.format(TIME_FORMATTER);
    public static final String DEFAULT_DATE_TIME_FORMAT = DEFAULT_LOCAL_DATE_TIME.format(DATETIME_FORMATTER);
    public static final String USER_NAME_KEY = "username";
    public static final String USER_PASS_KEY = "password";
    public static final String EMAIL_KEY = "email";
    public static final String ADDRESS_KEY = "address";
    public static final String ROLE_KEY = "role";
    public static final String PEOPLE_NUMBER_KEY = "people";
    public static final String DATE_TIME_KEY = "datetime";
    public static final String COMMENT_KEY = "comment";
    public static final String RATING_KEY = "rating";
    public static final String NAME_KEY = "name";
    public static final String TYPE_KEY = "type";
    public static final String START_TIME_KEY = "startTime";
    public static final String END_TIME_KEY = "endTime";
    public static final String DESCRIPTION_KEY = "description";
    public static final String IMAGE_LINK_KEY = "image";

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
    public static List<String> getAddRestaurantParamsKeyLis() {
        List<String> params = new ArrayList<>();
        params.add(NAME_KEY);
        params.add(TYPE_KEY);
        params.add(START_TIME_KEY);
        params.add(END_TIME_KEY);
        params.add(DESCRIPTION_KEY);
        params.add(ADDRESS_KEY);
        params.add(IMAGE_LINK_KEY);
        return params;
    }
    public static List<String> getAddReviewParamsKeyList() {
        List<String> params = new ArrayList<>();
        params.add(COMMENT_KEY);
        params.add(RATING_KEY);
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
    public static HashMap<String, Object> createAddReviewParams() {
        HashMap<String, Object> params = new HashMap<>();
        params.put(COMMENT_KEY, DEFAULT_COMMENT);
        params.put(RATING_KEY, createRatingHashMap(getDefaultRating()));
        return params;
    }
    public static HashMap<String, Object> createAddRestaurantParams() {
        HashMap<String, Object> params = new HashMap<>();
        params.put(NAME_KEY, DEFAULT_NAME);
        params.put(TYPE_KEY, DEFAULT_TYPE);
        params.put(START_TIME_KEY,  DEFAULT_TIME_FORMAT);
        params.put(END_TIME_KEY,  DEFAULT_TIME_FORMAT);
        params.put(DESCRIPTION_KEY, DEFAULT_DESCRIPTION);
        params.put(ADDRESS_KEY, createAddressHashMap(getDefaultAddress()));
        params.put(IMAGE_LINK_KEY, DEFAULT_IMAGE_LINK);
        return params;
    }
    public static HashMap<String, String> createAddressHashMap(Address address) {
        HashMap<String, String> addressMap = new HashMap<>();
        addressMap.put("country", address.getCountry());
        addressMap.put("city", address.getCity());
        addressMap.put("street", address.getStreet());
        return addressMap;
    }
    public static HashMap<String, Number> createRatingHashMap(Rating rating) {
        HashMap<String, Number> ratingMap = new HashMap<>();
        ratingMap.put("food", rating.food);
        ratingMap.put("service", rating.service);
        ratingMap.put("ambiance", rating.ambiance);
        ratingMap.put("overall", rating.overall);
        return ratingMap;
    }

    public static ResultActions perform(MockMvc mockMvc, String url, String body) throws Exception {
        return mockMvc.perform(request(HttpMethod.POST, url)
                .content(body)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON));
    }

    public static ResultActions perform(MockMvc mockMvc, String url) throws Exception {
        return mockMvc.perform(get(url).accept(MediaType.APPLICATION_JSON));
    }

    public static JsonNode getDataNode(ObjectMapper mapper, ResultActions res) throws Exception {
        String body = res.andReturn().getResponse().getContentAsString();
        return mapper.readTree(body).get("data");
    }
}
