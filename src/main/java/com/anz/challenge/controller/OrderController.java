package com.anz.challenge.controller;

import com.anz.challenge.model.Order;
import com.anz.challenge.service.OrderService;
import com.anz.challenge.dto.OrderSummary;
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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
	private static final Logger log = LoggerFactory.getLogger(OrderController.class);

	@Autowired
	private OrderService service;

	@Operation(summary = "Create a new order")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Order created successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Order.class))),
			@ApiResponse(responseCode = "400", description = "Invalid input") })
	@PostMapping
	public ResponseEntity<Order> createOrder(
			@Parameter(description = "Order object to be created", required = true) @Valid @RequestBody Order order) {
		log.info("Request received: Create new order with description='{}'", order.getDescription());
		Order created = service.createOrder(order);
		log.info("Order created successfully with ID={}", created.getId());
		return ResponseEntity.ok(created);

	}

	@Operation(summary = "Create multiple orders in bulk")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Orders created successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Order.class))),
			@ApiResponse(responseCode = "400", description = "Invalid input") })
	@PostMapping("/bulkOrders")
	public ResponseEntity<List<Order>> createOrders(
			@Parameter(description = "List of orders to be created", required = true) @Valid @RequestBody List<Order> orders) {
		log.info("Request received: Bulk create {} orders", orders.size());
		List<Order> created = service.createBulkOrders(orders);
		log.info("Bulk order creation completed. {} orders inserted.", created.size());
		return ResponseEntity.ok(created);
	}

	@Operation(summary = "Retrieve an order by ID")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Order retrieved successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Order.class))),
			@ApiResponse(responseCode = "404", description = "Order not found") })
	@GetMapping("/{id}")
	public ResponseEntity<Order> getOrder(
			@Parameter(description = "ID of the order to retrieve", required = true) @PathVariable Long id) {
		log.info("Request received: Retrieve order with ID={}", id);
		Order order = service.getOrder(id).orElseThrow(() -> {
			log.warn("Order not found for ID={}", id);
			return new OrderNotFoundException(id);
		});
		log.info("Order retrieved successfully: ID={}, status={}", order.getId(), order.getStatus());
		return ResponseEntity.ok(order);
	}

	@Operation(summary = "Update order status")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Order status updated successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Order.class))),
			@ApiResponse(responseCode = "404", description = "Order not found"),
			@ApiResponse(responseCode = "400", description = "Invalid status value") })
	@PutMapping("/{id}/status")
	public ResponseEntity<Order> updateStatus(
			@Parameter(description = "ID of the order to update", required = true) @PathVariable Long id,
			@Parameter(description = "New status for the order", required = true, schema = @Schema(implementation = Order.Status.class)) @RequestParam Order.Status status) {
		log.info("Request received: Update status of order ID={} to '{}'", id, status);
		Order updated = service.updateStatus(id, status);
		log.info("Order status updated: ID={}, newStatus={}", updated.getId(), updated.getStatus());
		return ResponseEntity.ok(updated);
	}

	@Operation(summary = "Search orders with optional status and pagination")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Orders retrieved successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Page.class))) })
	@GetMapping
	public ResponseEntity<Page<Order>> searchOrders(
			@Parameter(description = "Filter orders by status", in = ParameterIn.QUERY) @RequestParam(required = false) Order.Status status,
			@Parameter(description = "Page number", in = ParameterIn.QUERY) @RequestParam(defaultValue = "0") int page,
			@Parameter(description = "Number of orders per page", in = ParameterIn.QUERY) @RequestParam(defaultValue = "10") int size) {
		log.info("Request received: Search orders | status={} | page={} | size={}", status != null ? status : "ALL",
				page, size);

		PageRequest pageable = PageRequest.of(page, size);
		Page<Order> ordersPage = service.searchOrders(status, pageable);

		log.info("Search completed: {} orders returned on page {} of {}", ordersPage.getNumberOfElements(),
				ordersPage.getNumber() + 1, ordersPage.getTotalPages());

		return ResponseEntity.ok(ordersPage);
	}

	@GetMapping("/stream/status/{status}")
	public ResponseEntity<List<OrderSummary>> getOrdersByStatusStream(@PathVariable("status") Order.Status status) {
		log.info("Request received: Stream orders by status '{}'", status);
		List<OrderSummary> result = service.getOrdersByStatusStream(status);
		log.info("Streaming completed: {} orders returned for status '{}'", result.size(), status);
		return ResponseEntity.ok(result);
	}
}
