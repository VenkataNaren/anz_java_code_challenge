package com.anz.challenge;

import com.anz.challenge.exception.OrderNotFoundException;
import com.anz.challenge.model.Order;
import com.anz.challenge.repository.OrderRepository;
import com.anz.challenge.service.NotificationService;
import com.anz.challenge.service.OrderService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class OrderServiceTest {

    private static final Logger log = LoggerFactory.getLogger(OrderServiceTest.class);

    @Mock
    private OrderRepository repository;

    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private OrderService orderService;
    
    private ObjectMapper objectMapper;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        objectMapper = new ObjectMapper();
    }

    @Test
    void testCreateOrder() throws JsonProcessingException {
        Order input = new Order(null, "Test Order", null);
        Order saved = new Order(1L, "Test Order", Order.Status.CREATED);

        when(repository.save(any(Order.class))).thenReturn(saved);

        log.info("Input Order: {}", objectMapper.writeValueAsString(input));

        Order result = orderService.createOrder(input);

        log.info("Saved Order: {}", objectMapper.writeValueAsString(result));

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals(Order.Status.CREATED, result.getStatus());
        verify(notificationService, times(1)).notifyStatusChange(saved.getId(), "CREATED");
    }

    @Test
    void testCreateBulkOrders() throws Exception {
        Order o1 = new Order(null, "O1", null);
        Order o2 = new Order(null, "O2", Order.Status.CREATED);
        List<Order> orders = Arrays.asList(o1, o2);

        List<Order> savedOrders = Arrays.asList(
                new Order(1L, "O1", Order.Status.CREATED),
                new Order(2L, "O2", Order.Status.CREATED)
        );

        when(repository.saveAll(anyList())).thenReturn(savedOrders);

        // Log input orders in JSON
        log.info("Input Orders: {}", objectMapper.writeValueAsString(orders));

        List<Order> result = orderService.createBulkOrders(orders);

        // Log saved orders in JSON
        log.info("Saved Orders: {}", objectMapper.writeValueAsString(result));

        assertEquals(2, result.size());
        verify(notificationService, times(2)).notifyStatusChange(anyLong(), eq("CREATED"));
    }

    @Test
    void testGetOrderFound() throws Exception {
        Order saved = new Order(1L, "Order", Order.Status.CREATED);
        when(repository.findById(1L)).thenReturn(Optional.of(saved));

        log.info("Fetching order with ID: 1");

        Optional<Order> result = orderService.getOrder(1L);

        // Log fetched order in JSON
        log.info("Fetched Order: {}", result.map(t -> {
			try {
				return objectMapper.writeValueAsString(t);
			} catch (JsonProcessingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;
		}).orElse("Not Found"));

        assertTrue(result.isPresent());
        assertEquals(1L, result.get().getId());
    }


    @Test
    void testGetOrderNotFound() throws Exception {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        log.info("Fetching order with ID: 99");

        Optional<Order> result = orderService.getOrder(99L);

        // Log fetched order in readable format
        log.info("Fetched Order: {}", result.map(t -> {
			try {
				return objectMapper.writeValueAsString(t);
			} catch (JsonProcessingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;
		}).orElse("Not Found"));

        assertFalse(result.isPresent());
    }


    @Test
    void testUpdateStatusSuccess() throws Exception {
        Order existing = new Order(1L, "Order", Order.Status.CREATED);
        when(repository.findById(1L)).thenReturn(Optional.of(existing));
        when(repository.save(any(Order.class))).thenReturn(new Order(1L, "Order", Order.Status.COMPLETED));

        log.info("Updating Order ID 1 status from {} to COMPLETED", existing.getStatus());

        Order result = orderService.updateStatus(1L, Order.Status.COMPLETED);

        // Log updated order in readable JSON format
        log.info("Updated Order: {}", objectMapper.writeValueAsString(result));

        assertEquals(Order.Status.COMPLETED, result.getStatus());
        verify(notificationService, times(1)).notifyStatusChange(1L, "COMPLETED");
    }

    @Test
    void testUpdateStatusInvalidTransition() {
        Order existing = new Order(1L, "Order", Order.Status.COMPLETED);
        when(repository.findById(1L)).thenReturn(Optional.of(existing));

        log.info("Attempting invalid status change on Order ID {} from {} to CREATED", 
                 existing.getId(), existing.getStatus());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                orderService.updateStatus(1L, Order.Status.CREATED)
        );

        log.info("Caught expected exception: {}", exception.getMessage());

        verify(repository, never()).save(any(Order.class));
        verify(notificationService, never()).notifyStatusChange(anyLong(), anyString());
    }


    @Test
    void testUpdateStatusOrderNotFound() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        log.info("Attempting status update on non-existing Order ID {}", 99L);

        OrderNotFoundException exception = assertThrows(OrderNotFoundException.class, () ->
                orderService.updateStatus(99L, Order.Status.CREATED)
        );

        log.info("Caught expected exception: {}", exception.getMessage());

        verify(repository, never()).save(any(Order.class));
        verify(notificationService, never()).notifyStatusChange(anyLong(), anyString());
    }


    @Test
    void testSearchOrdersWithStatus() throws JsonProcessingException {
        Order o1 = new Order(1L, "O1", Order.Status.CREATED);
        Page<Order> page = new PageImpl<>(List.of(o1), PageRequest.of(0, 10), 1);

        when(repository.findByStatus(Order.Status.CREATED, PageRequest.of(0, 10))).thenReturn(page);

        log.info("Searching Orders with status: {}, page: {}, size: {}", 
                 Order.Status.CREATED, 0, 10);

        Page<Order> result = orderService.searchOrders(Order.Status.CREATED, PageRequest.of(0, 10));

     // Convert content to JSON for readable logging
        String ordersJson = objectMapper.writeValueAsString(result.getContent());
        log.info("Search Result - Total Elements: {}, Orders: {}", result.getTotalElements(), ordersJson);

        assertEquals(1, result.getTotalElements());
        assertEquals(Order.Status.CREATED, result.getContent().get(0).getStatus());
    }

    @Test
    void testSearchOrdersWithoutStatus() throws JsonProcessingException {
        Order o1 = new Order(1L, "O1", Order.Status.CREATED);
        Page<Order> page = new PageImpl<>(List.of(o1), PageRequest.of(0, 10), 1);

        when(repository.findAll(PageRequest.of(0, 10))).thenReturn(page);

        log.info("Searching Orders without status filter, page: {}, size: {}", 0, 10);

        Page<Order> result = orderService.searchOrders(null, PageRequest.of(0, 10));

     // Convert content to JSON for readable logging
        String ordersJson = objectMapper.writeValueAsString(result.getContent());
        log.info("Search Result - Total Elements: {}, Orders: {}", result.getTotalElements(), ordersJson);

        assertEquals(1, result.getTotalElements());
    }

}
