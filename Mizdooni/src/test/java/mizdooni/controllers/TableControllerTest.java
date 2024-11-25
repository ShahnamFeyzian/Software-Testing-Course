package mizdooni.controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import mizdooni.database.Database;
import mizdooni.exceptions.RestaurantNotFound;
import mizdooni.model.Restaurant;
import mizdooni.model.RestaurantSearchFilter;
import mizdooni.model.Table;
import mizdooni.model.User;
import mizdooni.response.PagedList;
import mizdooni.service.RestaurantService;
import mizdooni.service.TableService;
import mizdooni.service.UserService;
import org.junit.jupiter.api.BeforeEach;
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

import static mizdooni.controllers.ControllersTestUtils.DEFAULT_PAGE_NUM;
import static mizdooni.controllers.ControllersTestUtils.DEFAULT_RESTAURANT_PAGE_SIZE;
import static mizdooni.model.ModelTestUtils.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.request;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.assertj.core.api.Assertions.*;

@WebMvcTest(TableController.class)
@DirtiesContext
public class TableControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private RestaurantService restaurantService;
    @MockBean
    private TableService tableService;
    @MockBean
    private Database db;
    @MockBean
    private UserService userService;

    private Restaurant restaurant;
    private Table table;

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
    public void setup() throws RestaurantNotFound {
        restaurant = getDefaultRestaurant();
        table = getTableWithDefaultSeatsForRestaurant(restaurant);
        when(restaurantService.getRestaurant(restaurant.getId())).thenReturn(restaurant);
        when(tableService.getTables(restaurant.getId())).thenReturn(List.of(table));
        when(restaurantService.getRestaurants(DEFAULT_PAGE_NUM, new RestaurantSearchFilter())).
                thenReturn(new PagedList<>(List.of(restaurant), 1, DEFAULT_RESTAURANT_PAGE_SIZE));
    }

    @Test
    public void getTables_RestaurantIdTypeIsInvalid_ResponsesBadRequest() throws Exception {
        String url = "/tables/invalid_restaurant_id";

        perform(url).andExpect(status().isBadRequest());
    }

    @Test
    public void getTables_RestaurantWithIdDoesNotExist_ResponsesNotFound() throws Exception {
        String url = "/tables/1234";
        when(restaurantService.getRestaurant(1234)).thenReturn(null);

        perform(url).andExpect(status().isNotFound());
    }

    @Test
    public void getTables_RestaurantWithIdIsExist_ResponsesOkAndReturnsItsTables() throws Exception {
        String url = "/tables/" + restaurant.getId();
        String expectedTableListStr = "[{\"tableNumber\":0,\"seatsNumber\":4}]";

        ResultActions res = perform(url).andExpect(status().isOk());
        String tableListStr = getDataNode(res).toString();

        assertThat(tableListStr).isEqualTo(expectedTableListStr);
    }
}
