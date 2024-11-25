package mizdooni.controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import mizdooni.database.Database;
import mizdooni.model.Restaurant;
import mizdooni.service.RestaurantService;
import mizdooni.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static mizdooni.model.ModelTestUtils.getDefaultRestaurant;
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
}
