package com.anz.challenge;

import com.anz.challenge.controller.OrderController;
import com.anz.challenge.model.Order;
import com.anz.challenge.security.JwtFilter;
import com.anz.challenge.security.JwtUtil;
import com.anz.challenge.service.NotificationService;
import com.anz.challenge.service.OrderService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.Arrays;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(
        controllers = OrderController.class,
        excludeAutoConfiguration = {
                com.anz.challenge.config.SecurityConfig.class
        }
)
@AutoConfigureMockMvc(addFilters = false)
public class OrderControllerTest {

    private static final Logger logger = LoggerFactory.getLogger(OrderControllerTest.class);

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private OrderService orderService;

    @MockBean
    private NotificationService notificationService;

    @MockBean
    private JwtUtil jwtUtil;

    @MockBean
    private JwtFilter jwtFilter;

    @BeforeEach
    void setup() {
        logger.info("Setting up mocks before each test");
        when(jwtUtil.extractUsername(any(String.class))).thenReturn("admin");
    }

    @Test
    @WithMockUser(username = "admin", roles = {"USER"})
    public void testCreateOrder() throws Exception {
        Order order = new Order(null, "Test Order", Order.Status.CREATED);
        Order savedOrder = new Order(1L, "Test Order", Order.Status.CREATED);

        when(orderService.createOrder(any(Order.class))).thenReturn(savedOrder);

        String requestJson = objectMapper.writeValueAsString(order);
        logger.info("testCreateOrder - Input: {}", requestJson);

        MvcResult result = mockMvc.perform(post("/orders")
                        .content(requestJson)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        String responseJson = result.getResponse().getContentAsString();
        logger.info("testCreateOrder - Output: {}", responseJson);
    }

    @Test
    @WithMockUser(username = "admin", roles = {"USER"})
    public void testGetOrder() throws Exception {
        Order savedOrder = new Order(1L, "Test Order", Order.Status.CREATED);
        when(orderService.getOrder(1L)).thenReturn(Optional.of(savedOrder));

        logger.info("testGetOrder - Input: Order ID = 1");

        MvcResult result = mockMvc.perform(get("/orders/1"))
                .andExpect(status().isOk())
                .andReturn();

        String responseJson = result.getResponse().getContentAsString();
        logger.info("testGetOrder - Output: {}", responseJson);
    }

    @Test
    @WithMockUser(username = "admin", roles = {"USER"})
    public void testUpdateOrderStatus() throws Exception {
        Order updatedOrder = new Order(1L, "Test Order", Order.Status.COMPLETED);
        when(orderService.updateStatus(1L, Order.Status.COMPLETED)).thenReturn(updatedOrder);

        logger.info("testUpdateOrderStatus - Input: Order ID = 1, Status = COMPLETED");

        MvcResult result = mockMvc.perform(put("/orders/1/status")
                        .param("status", "COMPLETED"))
                .andExpect(status().isOk())
                .andReturn();

        String responseJson = result.getResponse().getContentAsString();
        logger.info("testUpdateOrderStatus - Output: {}", responseJson);
    }

    @Test
    @WithMockUser(username = "admin", roles = {"USER"})
    public void testSearchOrders() throws Exception {
        Order order1 = new Order(1L, "Order 1", Order.Status.CREATED);
        Order order2 = new Order(2L, "Order 2", Order.Status.COMPLETED);

        Page<Order> page = new PageImpl<>(Arrays.asList(order1, order2), PageRequest.of(0, 10), 2);
        when(orderService.searchOrders(null, PageRequest.of(0, 10))).thenReturn(page);

        logger.info("testSearchOrders - Input: search=null, page=0, size=10");

        MvcResult result = mockMvc.perform(get("/orders"))
                .andExpect(status().isOk())
                .andReturn();

        String responseJson = result.getResponse().getContentAsString();
        logger.info("testSearchOrders - Output: {}", responseJson);
    }
}
