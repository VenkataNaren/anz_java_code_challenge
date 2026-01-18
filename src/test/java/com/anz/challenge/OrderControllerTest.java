package com.anz.challenge;

import com.anz.challenge.controller.OrderController;
import com.anz.challenge.model.Order;
import com.anz.challenge.service.NotificationService;
import com.anz.challenge.service.OrderService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@WebMvcTest(OrderController.class)
@Import(com.anz.challenge.config.TestSecurityConfig.class)
public class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private OrderService orderService;

    @MockBean
    private NotificationService notificationService;

    @Test
    @WithMockUser(username = "admin", roles = {"USER"})
    public void testCreateOrder() throws Exception {
        Order order = new Order(null, "Test Order", Order.Status.CREATED);
        Order savedOrder = new Order(1L, "Test Order", Order.Status.CREATED);

        when(orderService.createOrder(any(Order.class))).thenReturn(savedOrder);

        mockMvc.perform(post("/orders")
                .content(objectMapper.writeValueAsString(order))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.description").value("Test Order"))
                .andExpect(jsonPath("$.status").value("CREATED"));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"USER"})
    public void testGetOrder() throws Exception {
        Order savedOrder = new Order(1L, "Test Order", Order.Status.CREATED);

        when(orderService.getOrder(1L)).thenReturn(Optional.of(savedOrder));

        mockMvc.perform(get("/orders/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.description").value("Test Order"))
                .andExpect(jsonPath("$.status").value("CREATED"));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"USER"})
    public void testUpdateOrderStatus() throws Exception {
        Order updatedOrder = new Order(1L, "Test Order", Order.Status.COMPLETED);

        when(orderService.updateStatus(1L, Order.Status.COMPLETED)).thenReturn(updatedOrder);

        mockMvc.perform(put("/orders/1/status")
                .param("status", "COMPLETED"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("COMPLETED"));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"USER"})
    public void testSearchOrders() throws Exception {
        Order order1 = new Order(1L, "Order 1", Order.Status.CREATED);
        Order order2 = new Order(2L, "Order 2", Order.Status.COMPLETED);

        when(orderService.searchOrders()).thenReturn(Arrays.asList(order1, order2));

        mockMvc.perform(get("/orders"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[1].id").value(2));
    }
}
