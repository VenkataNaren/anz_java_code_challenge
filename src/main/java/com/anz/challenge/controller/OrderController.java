package com.anz.challenge.controller;

import com.anz.challenge.model.Order;
import com.anz.challenge.service.OrderService;
import com.anz.challenge.exception.OrderNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {

    @Autowired
    private OrderService service;

    // Create a single order
    @PostMapping
    public ResponseEntity<Order> createOrder(@RequestBody Order order) {
        return ResponseEntity.ok(service.createOrder(order));
    }

    // Create multiple orders in bulk
    @PostMapping("/bulkOrders")
    public ResponseEntity<List<Order>> createOrders(@RequestBody List<Order> orders) {
        return ResponseEntity.ok(service.createBulkOrders(orders));
    }

    // Get order by ID
    @GetMapping("/{id}")
    public ResponseEntity<Order> getOrder(@PathVariable Long id) {
        Order order = service.getOrder(id)
                .orElseThrow(() -> new OrderNotFoundException(id));
        return ResponseEntity.ok(order);
    }

    // Update order status
    @PutMapping("/{id}/status")
    public ResponseEntity<Order> updateStatus(@PathVariable Long id,
                                              @RequestParam Order.Status status) {
        return ResponseEntity.ok(service.updateStatus(id, status));
    }

    // Search orders with optional status and pagination
    @GetMapping
    public ResponseEntity<Page<Order>> searchOrders(
            @RequestParam(required = false) Order.Status status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        PageRequest pageable = PageRequest.of(page, size);
        Page<Order> ordersPage = service.searchOrders(status, pageable);
        return ResponseEntity.ok(ordersPage);
    }
}
