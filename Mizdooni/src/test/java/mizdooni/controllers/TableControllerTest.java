package mizdooni.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import mizdooni.database.Database;
import mizdooni.exceptions.InvalidManagerRestaurant;
import mizdooni.exceptions.RestaurantNotFound;
import mizdooni.exceptions.UserNotManager;
import mizdooni.model.Restaurant;
import mizdooni.model.RestaurantSearchFilter;
import mizdooni.model.Table;
import mizdooni.response.PagedList;
import mizdooni.service.RestaurantService;
import mizdooni.service.TableService;
import mizdooni.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.List;

import static mizdooni.controllers.ControllersTestUtils.getDataNode;
import static mizdooni.controllers.ControllersTestUtils.perform;
import static mizdooni.model.ModelTestUtils.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TableController.class)
@DirtiesContext
class TableControllerTest {
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

    @BeforeEach
    void setup() throws RestaurantNotFound {
        restaurant = getDefaultRestaurant();
        table = getTableWithDefaultSeatsForRestaurant(restaurant);
        when(restaurantService.getRestaurant(restaurant.getId())).thenReturn(restaurant);
        when(tableService.getTables(restaurant.getId())).thenReturn(List.of(table));
        when(restaurantService.getRestaurants(DEFAULT_PAGE_NUM, new RestaurantSearchFilter())).
                thenReturn(new PagedList<>(List.of(restaurant), 1, DEFAULT_RESTAURANT_PAGE_SIZE));
    }

    @Test
    void getTables_RestaurantIdTypeIsInvalid_ResponsesBadRequest() throws Exception {
        String url = "/tables/invalid_restaurant_id";

        perform(mockMvc, url).andExpect(status().isBadRequest());
    }

    @Test
    void getTables_RestaurantWithIdDoesNotExist_ResponsesNotFound() throws Exception {
        String url = "/tables/1234";
        when(restaurantService.getRestaurant(1234)).thenReturn(null);

        perform(mockMvc, url).andExpect(status().isNotFound());
    }

    @Test
    void getTables_RestaurantWithIdIsExist_ResponsesOkAndReturnsItsTables() throws Exception {
        String url = "/tables/" + restaurant.getId();
        String expectedTableListStr = "[{\"tableNumber\":0,\"seatsNumber\":4}]";

        ResultActions res = perform(mockMvc, url).andExpect(status().isOk());
        String tableListStr = getDataNode(mapper, res).toString();

        assertThat(tableListStr).isEqualTo(expectedTableListStr);
    }

    @Test
    void addTables_RestaurantIdIsInvalid_ResponseBadRequest() throws Exception {
        String url = "/tables/invalid_restaurant_id";

        perform(mockMvc, url).andExpect(status().isBadRequest());
    }

    @Test
    void addTables_NoRestaurantWithGivenIdExists_ResponsesNotFound() throws Exception {
        String url = "/tables/1234";
        when(restaurantService.getRestaurant(1234)).thenReturn(null);

        perform(mockMvc, url).andExpect(status().isNotFound());
    }

    @Test
    void addTables_SeatsNumberDoesNotExistsInParam_ResponsesBadRequest() throws Exception {
        String url = "/tables/" + restaurant.getId();
        String body = "{}";

        perform(mockMvc, url, body).andExpect(status().isBadRequest());
    }

    @Test
    void addTables_SeatsNumberHasNotIntegerTypeInParam_ResponsesBadRequest() throws Exception {
        String url = "/tables/" + restaurant.getId();
        String body = "{\"seatsNumber\":\"invalid_type_for_seatsNumber\"}";

        perform(mockMvc, url, body).andExpect(status().isBadRequest());
    }

    @Test
    void addTables_NoUserLoggedInOrUserIsNotManager_ResponsesBadRequest() throws Exception {
        String url = "/tables/" + restaurant.getId();
        String body = "{\"seatsNumber\":\"4\"}";
        doThrow(new UserNotManager()).when(tableService).addTable(restaurant.getId(), 4);

        perform(mockMvc, url, body).andExpect(status().isBadRequest());
    }

    @Test
    void addTables_LoggedInManagerIsNotRestaurantManager_ResponsesBadRequest() throws Exception {
        String url = "/tables/" + restaurant.getId();
        String body = "{\"seatsNumber\":\"4\"}";
        doThrow(new InvalidManagerRestaurant()).when(tableService).addTable(restaurant.getId(), 4);

        perform(mockMvc, url, body).andExpect(status().isBadRequest());
    }

    @Test
    void addTables_EverythingIsOk_ResponsesOkAndTableAddedToRestaurant() throws Exception {
        String url = "/tables/" + restaurant.getId();
        String body = "{\"seatsNumber\":\"4\"}";

        perform(mockMvc, url, body).andExpect(status().isOk());
    }
}
