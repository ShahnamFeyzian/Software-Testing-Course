package mizdooni.controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import mizdooni.database.Database;
import mizdooni.model.Restaurant;
import mizdooni.model.RestaurantSearchFilter;
import mizdooni.response.PagedList;
import mizdooni.service.RestaurantService;
import mizdooni.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.List;

import static mizdooni.controllers.ControllersTestUtils.*;
import static mizdooni.model.ModelTestUtils.DEFAULT_NAME;
import static mizdooni.model.ModelTestUtils.getDefaultRestaurant;
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

    private ResultActions perform(String url, String body) throws Exception {
        return mockMvc.perform(request(HttpMethod.GET, url)
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
        when(restaurantService.getRestaurant(restaurant.getId())).thenReturn(restaurant);
        when(restaurantService.getRestaurants(DEFAULT_PAGE_NUM, new RestaurantSearchFilter())).
                thenReturn(new PagedList<>(List.of(restaurant), 1, DEFAULT_RESTAURANT_PAGE_SIZE));
    }

    @Test
    void getRestaurant_BadParamTypeForRestaurantId_ResponsesBadRequest() throws Exception {
        String url = "/restaurants/invalid_type_instead_integer";

        perform(url).andExpect(status().isBadRequest());
    }

    @Test
    void getRestaurant_RestaurantIdDoesNotExist_ResponsesNotFound() throws Exception {
        String url = "/restaurants/" + 1234567;
        when(restaurantService.getRestaurant(1234567)).thenReturn(null);

        perform(url).andExpect(status().isNotFound());
    }

    @Test
    void getRestaurant_RestaurantExists_ResponsesOkAndReturnsRestaurant() throws Exception {
        String url = "/restaurants/" + restaurant.getId();

        ResultActions res = perform(url).andExpect(status().isOk());
        int restaurantId = Integer.parseInt(getDataNode(res).get("id").toString());

        assertThat(restaurantId).isEqualTo(restaurant.getId());
    }

    @Test
    void getRestaurants_PageIsNotPass_ResponsesBadRequest() throws Exception {
        String url = "/restaurants";

        perform(url).andExpect(status().isBadRequest());
    }

    @Test
    void getRestaurants_PageValueIsInvalid_ResponsesBadRequest() throws Exception {
        String url = "/restaurants?page=-1";
        RestaurantSearchFilter emptyFilter = new RestaurantSearchFilter();
        doThrow(new IllegalArgumentException("invalid page number")).
                when(restaurantService).getRestaurants(-1, emptyFilter);

        perform(url).andExpect(status().isBadRequest());
    }

    @Test
    void getRestaurants_FilterIsEmpty_ResponsesOkAndReturnsAllRestaurants() throws Exception {
        String url = "/restaurants?page=" + DEFAULT_PAGE_NUM;

        ResultActions res = perform(url).andExpect(status().isOk());
        int pageNumber = Integer.parseInt(getDataNode(res).get("page").toString());
        int restaurantId = Integer.parseInt(getDataNode(res).get("pageList").get(0).get("id").toString());

        assertThat(pageNumber).isEqualTo(1);
        assertThat(restaurantId).isEqualTo(restaurant.getId());
    }

    @Test
    @Disabled
    // TODO: find its fucking problem to convert the body to filter argument in controller endpoint
    void getRestaurants_FilterIsValid_ResponsesOkAndReturnsAllRestaurants() throws Exception {
        String url = "/restaurants?page=" + DEFAULT_PAGE_NUM;
        RestaurantSearchFilter filter = new RestaurantSearchFilter();
        filter.setName(DEFAULT_NAME);
        when(restaurantService.getRestaurants(DEFAULT_PAGE_NUM, filter)).
                thenReturn(new PagedList<>(List.of(), 1, DEFAULT_RESTAURANT_PAGE_SIZE));


        String body = "{filter:" + mapper.writeValueAsString(filter) + "}";
        ResultActions res = perform(url, body).andExpect(status().isOk());
        int pageNumber = Integer.parseInt(getDataNode(res).get("page").toString());
        String pageListStr = getDataNode(res).get("pageList").toString();

        assertThat(pageNumber).isEqualTo(1);
        assertThat(pageListStr).isEqualTo(List.of().toString());
    }
}
