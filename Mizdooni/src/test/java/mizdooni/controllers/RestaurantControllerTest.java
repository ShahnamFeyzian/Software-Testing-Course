package mizdooni.controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import mizdooni.database.Database;
import mizdooni.exceptions.DuplicatedRestaurantName;
import mizdooni.exceptions.InvalidWorkingTime;
import mizdooni.exceptions.UserNotManager;
import mizdooni.model.Address;
import mizdooni.model.Restaurant;
import mizdooni.model.RestaurantSearchFilter;
import mizdooni.model.User;
import mizdooni.response.PagedList;
import mizdooni.service.RestaurantService;
import mizdooni.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.*;
import java.util.stream.Stream;

import static mizdooni.controllers.ControllersTestUtils.*;
import static mizdooni.model.ModelTestUtils.*;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.when;

@WebMvcTest(RestaurantController.class)
@DirtiesContext
public class RestaurantControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private RestaurantService restaurantService;
    @MockBean
    private Database db;
    @MockBean
    private UserService userService;

    private Restaurant restaurant;
    private User manager;

    private ResultActions perform(String url, String body) throws Exception {
        return mockMvc.perform(request(HttpMethod.POST, url)
                .content(body)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON));
    }

    private ResultActions perform(String url) throws Exception {
        return mockMvc.perform(get(url).accept(MediaType.APPLICATION_JSON));
    }

    private JsonNode getDataNode(ResultActions res) throws Exception {
        String body = res.andReturn().getResponse().getContentAsString();
        return mapper.readTree(body).get("data");
    }

    @BeforeEach
    public void setup() {
        restaurant = getDefaultRestaurant();
        manager = getDefaultManagerUser();
        when(restaurantService.getRestaurant(restaurant.getId())).thenReturn(restaurant);
        when(restaurantService.getRestaurants(DEFAULT_PAGE_NUM, new RestaurantSearchFilter())).
                thenReturn(new PagedList<>(List.of(restaurant), 1, DEFAULT_RESTAURANT_PAGE_SIZE));
        when(restaurantService.getManagerRestaurants(manager.getId())).thenReturn(List.of(restaurant));
    }

    @Test
    public void getRestaurant_BadParamTypeForRestaurantId_ResponsesBadRequest() throws Exception {
        String url = "/restaurants/invalid_type_instead_integer";

        perform(url).andExpect(status().isBadRequest());
    }

    @Test
    public void getRestaurant_RestaurantIdDoesNotExist_ResponsesNotFound() throws Exception {
        String url = "/restaurants/" + 1234567;
        when(restaurantService.getRestaurant(1234567)).thenReturn(null);

        perform(url).andExpect(status().isNotFound());
    }

    @Test
    public void getRestaurant_RestaurantExists_ResponsesOkAndReturnsRestaurant() throws Exception {
        String url = "/restaurants/" + restaurant.getId();

        ResultActions res = perform(url).andExpect(status().isOk());
        int restaurantId = Integer.parseInt(getDataNode(res).get("id").toString());

        assertThat(restaurantId).isEqualTo(restaurant.getId());
    }

    @Test
    public void getRestaurants_PageIsNotPass_ResponsesBadRequest() throws Exception {
        String url = "/restaurants";

        perform(url).andExpect(status().isBadRequest());
    }

    @Test
    public void getRestaurants_PageValueIsInvalid_ResponsesBadRequest() throws Exception {
        String url = "/restaurants?page=-1";
        RestaurantSearchFilter emptyFilter = new RestaurantSearchFilter();
        doThrow(new IllegalArgumentException("invalid page number")).
                when(restaurantService).getRestaurants(-1, emptyFilter);

        perform(url).andExpect(status().isBadRequest());
    }

    @Test
    public void getRestaurants_FilterIsEmpty_ResponsesOkAndReturnsAllRestaurants() throws Exception {
        String url = "/restaurants?page=" + DEFAULT_PAGE_NUM;

        ResultActions res = perform(url).andExpect(status().isOk());
        int pageNumber = Integer.parseInt(getDataNode(res).get("page").toString());
        int restaurantId = Integer.parseInt(getDataNode(res).get("pageList").get(0).get("id").toString());

        assertThat(pageNumber).isEqualTo(1);
        assertThat(restaurantId).isEqualTo(restaurant.getId());
    }

    @Test
    public void getRestaurants_FilterIsValid_ResponsesOkAndReturnsAllRestaurants() throws Exception {
        String url = "/restaurants?page=" + DEFAULT_PAGE_NUM + "&name=" + DEFAULT_NAME;
        RestaurantSearchFilter filter = new RestaurantSearchFilter();
        filter.setName(DEFAULT_NAME);
        when(restaurantService.getRestaurants(DEFAULT_PAGE_NUM, filter)).
                thenReturn(new PagedList<>(List.of(), 1, DEFAULT_RESTAURANT_PAGE_SIZE));

        ResultActions res = perform(url).andExpect(status().isOk());
        int pageNumber = Integer.parseInt(getDataNode(res).get("page").toString());
        String pageListStr = getDataNode(res).get("pageList").toString();

        assertThat(pageNumber).isEqualTo(1);
        assertThat(pageListStr).isEqualTo(List.of().toString());
    }

    @Test
    public void getManagerRestaurants_BadParamTypeForManagerId_ResponsesBadRequest() throws Exception {
        String url = "/restaurants/manager/invalid_type_instead_integer";

        perform(url).andExpect(status().isBadRequest());
    }

    @Test
    public void getManagerRestaurants_ManagerIdDoesNotExist_ResponsesOkAndReturnsEmptyList() throws Exception {
        String url = "/restaurants/manager/" + 1234567;
        when(restaurantService.getManagerRestaurants(1234567)).thenReturn(List.of());

        ResultActions res = perform(url).andExpect(status().isOk());
        String listStr = getDataNode(res).toString();

        assertThat(listStr).isEqualTo(List.of().toString());
    }

    @Test
    public void getManagerRestaurants_ManagerIdExists_ResponsesOkAndReturnsRestaurant() throws Exception {
        String url = "/restaurants/manager/" + manager.getId();

        ResultActions res = perform(url).andExpect(status().isOk());
        int restaurantId = Integer.parseInt(getDataNode(res).get(0).get("id").toString());

        assertThat(restaurantId).isEqualTo(restaurant.getId());
    }

    @ParameterizedTest(name = "Missed field: {0}")
    @MethodSource("addRestaurantParamsButOneOfThemDoesNotExist")
    public void addRestaurant_RequiredParamsDoNotExist_ResponsesBadRequest(String missedField, HashMap<String, String> params) throws Exception {
        String url = "/restaurants";

        perform(url, params.toString()).andExpect(status().isBadRequest());
    }
    private static Stream<Arguments> addRestaurantParamsButOneOfThemDoesNotExist() {
        List<String> paramsKey = getAddRestaurantParamsKeyLis();
        paramsKey.remove(ADDRESS_KEY);
        paramsKey.remove(IMAGE_LINK_KEY);
        List<Arguments> args = new ArrayList<>();
        for (String currentParam : paramsKey) {
            HashMap<String, Object> params = createAddRestaurantParams();
            params.remove(currentParam);
            args.add(Arguments.of(currentParam, params));
        }
        return args.stream();
    }

    @ParameterizedTest(name = "Blank field: {0}")
    @MethodSource("addRestaurantParamsButOneOfThemIsBlank")
    public void addRestaurant_ParamsAreBlank_ResponsesBadRequest(String blankField, HashMap<String, String> params) throws Exception {
        String url = "/restaurants";

        perform(url, params.toString()).andExpect(status().isBadRequest());
    }
    private static Stream<Arguments> addRestaurantParamsButOneOfThemIsBlank() {
        List<String> paramsKey = getAddRestaurantParamsKeyLis();
        paramsKey.remove(ADDRESS_KEY);
        paramsKey.remove(IMAGE_LINK_KEY);
        List<Arguments> args = new ArrayList<>();
        for (String currentParam : paramsKey) {
            HashMap<String, Object> params = createAddRestaurantParams();
            params.put(currentParam, "");
            args.add(Arguments.of(currentParam, params));
        }
        HashMap<String, Object> blankCountryAddressParams = new HashMap<>();
        HashMap<String, Object> blankCityAddressParams = new HashMap<>();
        HashMap<String, Object> blankStreetAddressParams = new HashMap<>();
        blankCountryAddressParams.put(ADDRESS_KEY, createAddressHashMap(new Address("", DEFAULT_CITY, DEFAULT_STREET)));
        blankCityAddressParams.put(ADDRESS_KEY, createAddressHashMap(new Address(DEFAULT_COUNTRY, "", DEFAULT_STREET)));
        blankStreetAddressParams.put(ADDRESS_KEY, createAddressHashMap(new Address(DEFAULT_COUNTRY, DEFAULT_CITY, "")));
        args.add(Arguments.of("address.country", blankCountryAddressParams));
        args.add(Arguments.of("address.city", blankCityAddressParams));
        args.add(Arguments.of("address.street", blankStreetAddressParams));
        return args.stream();
    }

    @ParameterizedTest(name = "Bad type field: {0}")
    @MethodSource("addRestaurantParamsButOneOfThemHasBadType")
    public void addRestaurant_ParamHasBadType_ResponsesBadRequest(String badTypeField, HashMap<String, String> params) throws Exception {
        String url = "/restaurants";

        perform(url, params.toString()).andExpect(status().isBadRequest());
    }
    private static Stream<Arguments> addRestaurantParamsButOneOfThemHasBadType() {
        List<String> paramsKey = getAddRestaurantParamsKeyLis();
        paramsKey.remove(ADDRESS_KEY);
        List<Arguments> args = new ArrayList<>();
        for (String currentParam : paramsKey) {
            HashMap<String, Object> params = createAddRestaurantParams();
            params.put(currentParam, new Object());
            args.add(Arguments.of(currentParam, params));
        }
        return args.stream();
    }

    @Test
    public void addRestaurant_RepetitiveRestaurantName_ResponsesBadRequest() throws Exception {
        String url = "/restaurants";
        doThrow(new DuplicatedRestaurantName()).when(restaurantService)
                .addRestaurant(DEFAULT_NAME, DEFAULT_TYPE, DEFAULT_LOCAL_TIME, DEFAULT_LOCAL_TIME, DEFAULT_DESCRIPTION, getDefaultAddress(), DEFAULT_IMAGE_LINK);

        String body = mapper.writeValueAsString(createAddRestaurantParams());
        perform(url, body).andExpect(status().isBadRequest());
    }

    @Test
    public void addRestaurant_LoggedInUserIsNotManager_ResponsesBadRequest() throws Exception {
        String url = "/restaurants";
        doThrow(new UserNotManager()).when(restaurantService)
                .addRestaurant(DEFAULT_NAME, DEFAULT_TYPE, DEFAULT_LOCAL_TIME, DEFAULT_LOCAL_TIME, DEFAULT_DESCRIPTION, getDefaultAddress(), DEFAULT_IMAGE_LINK);

        String body = mapper.writeValueAsString(createAddRestaurantParams());
        perform(url, body).andExpect(status().isBadRequest());
    }

    @Test
    public void addRestaurant_RestaurantTimeIsInvalid_ResponsesBadRequest() throws Exception {
        String url = "/restaurants";
        doThrow(new InvalidWorkingTime()).when(restaurantService)
                .addRestaurant(DEFAULT_NAME, DEFAULT_TYPE, DEFAULT_LOCAL_TIME, DEFAULT_LOCAL_TIME, DEFAULT_DESCRIPTION, getDefaultAddress(), DEFAULT_IMAGE_LINK);

        String body = mapper.writeValueAsString(createAddRestaurantParams());
        perform(url, body).andExpect(status().isBadRequest());
    }

    @Test
    public void addRestaurant_EveryThingIsOk_ResponsesOkAndReturnsNewRestaurantId() throws Exception {
        String url = "/restaurants";
        when(restaurantService.addRestaurant(DEFAULT_NAME, DEFAULT_TYPE, DEFAULT_LOCAL_TIME, DEFAULT_LOCAL_TIME, DEFAULT_DESCRIPTION, getDefaultAddress(), DEFAULT_IMAGE_LINK))
                .thenReturn(1234);

        String body = mapper.writeValueAsString(createAddRestaurantParams());
        ResultActions result = perform(url, body).andExpect(status().isOk());
        String restaurantIdStr = getDataNode(result).toString();

        assertThat(restaurantIdStr).isEqualTo("1234");
    }

    @Test
    public void validateRestaurantName_DataIsNotPass_ResponsesBadRequest() throws Exception {
        String url = "/validate/restaurant-name";

        perform(url).andExpect(status().isBadRequest());
    }

    @Test
    public void validateRestaurantName_RestaurantWithDataNameIsExists_ResponsesConflict() throws Exception {
        String repetitiveName = "repetitive_name";
        String url = "/validate/restaurant-name?data=" + repetitiveName;
        when(restaurantService.restaurantExists(repetitiveName)).thenReturn(true);

        perform(url).andExpect(status().isConflict());
    }

    @Test
    public void validateRestaurantName_RestaurantWithDataNameIsNotExist_ResponsesOk() throws Exception {
        String uniqueName = "unique_name";
        String url = "/validate/restaurant-name?data=" + uniqueName;
        when(restaurantService.restaurantExists(uniqueName)).thenReturn(false);

        perform(url).andExpect(status().isOk());
    }

    @Test
    public void getRestaurantTypes_TouchTheEndpoint_ResponsesOkAndReturnsASetOfAllRestaurantTypes() throws Exception {
        String url = "/restaurants/types";
        when(restaurantService.getRestaurantTypes()).thenReturn(Set.of(restaurant.getType()));
        String expectedSetStr = "[\"type\"]";

        ResultActions res = perform(url).andExpect(status().isOk());
        String setStr = getDataNode(res).toString();

        assertThat(setStr).isEqualTo(expectedSetStr);
    }

    @Test
    public void getRestaurantLocations_TouchTheEndpoint_ResponsesOkAndReturnsMapOfCountryToSetOfCities() throws Exception {
        String url = "/restaurants/locations";
        Map<String, Set<String>> returnedData = new HashMap<>();
        returnedData.put(restaurant.getAddress().getCountry(), Set.of(restaurant.getAddress().getCity()));
        when(restaurantService.getRestaurantLocations()).thenReturn(returnedData);
        String expectedMapStr = "{\"country\":[\"city\"]}";

        ResultActions res = perform(url).andExpect(status().isOk());
        String mapStr = getDataNode(res).toString();

        assertThat(mapStr).isEqualTo(expectedMapStr);
    }
}
