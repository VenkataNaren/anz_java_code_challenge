package com.anz.challenge.controller;

import com.anz.challenge.model.Order;
import com.anz.challenge.service.OrderService;
import com.anz.challenge.exception.OrderNotFoundException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
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

    @Operation(summary = "Create a new order")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Order created successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Order.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input")
    })
    @PostMapping
    public ResponseEntity<Order> createOrder(
            @Parameter(description = "Order object to be created", required = true)
            @Valid @RequestBody Order order) {
        return ResponseEntity.ok(service.createOrder(order));
    }

    @Operation(summary = "Create multiple orders in bulk")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Orders created successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Order.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input")
    })
    @PostMapping("/bulkOrders")
    public ResponseEntity<List<Order>> createOrders(
            @Parameter(description = "List of orders to be created", required = true)
            @Valid @RequestBody List<Order> orders) {
        return ResponseEntity.ok(service.createBulkOrders(orders));
    }

    @Operation(summary = "Retrieve an order by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Order retrieved successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Order.class))),
            @ApiResponse(responseCode = "404", description = "Order not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<Order> getOrder(
            @Parameter(description = "ID of the order to retrieve", required = true)
            @PathVariable Long id) {
        Order order = service.getOrder(id)
                .orElseThrow(() -> new OrderNotFoundException(id));
        return ResponseEntity.ok(order);
    }

    @Operation(summary = "Update order status")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Order status updated successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Order.class))),
            @ApiResponse(responseCode = "404", description = "Order not found"),
            @ApiResponse(responseCode = "400", description = "Invalid status value")
    })
    @PutMapping("/{id}/status")
    public ResponseEntity<Order> updateStatus(
            @Parameter(description = "ID of the order to update", required = true)
            @PathVariable Long id,
            @Parameter(description = "New status for the order", required = true,
                       schema = @Schema(implementation = Order.Status.class))
            @RequestParam Order.Status status) {
        return ResponseEntity.ok(service.updateStatus(id, status));
    }

    @Operation(summary = "Search orders with optional status and pagination")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Orders retrieved successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Page.class)))
    })
    @GetMapping
    public ResponseEntity<Page<Order>> searchOrders(
            @Parameter(description = "Filter orders by status", in = ParameterIn.QUERY)
            @RequestParam(required = false) Order.Status status,
            @Parameter(description = "Page number", in = ParameterIn.QUERY)
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Number of orders per page", in = ParameterIn.QUERY)
            @RequestParam(defaultValue = "10") int size
    ) {
        PageRequest pageable = PageRequest.of(page, size);
        Page<Order> ordersPage = service.searchOrders(status, pageable);
        return ResponseEntity.ok(ordersPage);
    }
}
