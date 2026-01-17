package com.anz.challenge.controller;

import com.anz.challenge.model.Order;
import com.anz.challenge.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService service;

    @PostMapping
    public ResponseEntity<Order> createOrder(@RequestBody Order order) {
        return ResponseEntity.ok(service.createOrder(order));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Order> getOrder(@PathVariable Long id) {
        return service.getOrder(id).map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<Order> updateStatus(@PathVariable Long id, @RequestParam Order.Status status) {
        return ResponseEntity.ok(service.updateStatus(id, status));
    }

    @GetMapping
    public ResponseEntity<List<Order>> searchOrders() {
        return ResponseEntity.ok(service.searchOrders());
    }
}
