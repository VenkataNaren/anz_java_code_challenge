package com.anz.challenge.service;

import com.anz.challenge.dto.NotificationEvent;
import com.anz.challenge.dto.OrderSummary;
import com.anz.challenge.exception.OrderNotFoundException;
import com.anz.challenge.model.Order;
import com.anz.challenge.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {

	private static final Logger log = LoggerFactory.getLogger(OrderService.class);

	@Autowired
	private OrderRepository repository;

	@Autowired
	private NotificationService notificationService;

	@Transactional
	public Order createOrder(Order order) {
		if (order.getStatus() == null)
			order.setStatus(Order.Status.CREATED);
		Order saved = repository.save(order);
		sendNotificationsAsync(saved);
		return saved;
	}

	public List<Order> createBulkOrders(List<Order> orders) {
		List<Order> saved = saveOrdersTransactional(orders);
		sendNotificationsAsync(saved);
		return saved;
	}

	@Transactional
	public List<Order> saveOrdersTransactional(List<Order> orders) {
		return repository.saveAll(orders);
	}

	public void sendNotificationsAsync(Order order) {
		sendNotificationsAsync(List.of(order));
	}

	public void sendNotificationsAsync(List<Order> orders) {
		if (orders == null || orders.isEmpty()) {
			log.info("No orders provided for notification publishing.");
			return;
		}
		log.info("Publishing notification events for {} order(s).", orders.size());
		for (Order o : orders) {
			try {
				log.info("Publishing notification event: orderId={}, status={}", o.getId(), o.getStatus().name());
				notificationService.notifyStatusChange(o.getId(), o.getStatus().name());
				log.info("Published notification event: orderId={}, status={}, description={}", o.getId(), o.getStatus().name(), o.getDescription());
			} catch (Exception e) {
				log.error("Notification failed for order {}: {}", o.getId(), e.getMessage());
			}
		}
	}

	public Optional<Order> getOrder(Long id) {
		return repository.findById(id);
	}

	@Transactional
	public Order updateStatus(Long id, Order.Status status) {
		Order order = repository.findById(id).orElseThrow(() -> new OrderNotFoundException(id));
		// Validate allowed transitions
		validateStatusChange(order, status);
		order.setStatus(status);
		Order updated = repository.save(order);
		sendNotificationsAsync(updated);
		return updated;
	}

	// Search with optional status and pagination
	public Page<Order> searchOrders(Order.Status status, Pageable pageable) {
		if (status != null) {
			return repository.findByStatus(status, pageable);
		} else {
			return repository.findAll(pageable);
		}
	}

	private void validateStatusChange(Order order, Order.Status newStatus) {
		if (order.getStatus() == Order.Status.COMPLETED || order.getStatus() == Order.Status.CANCELLED) {
			throw new IllegalArgumentException("Cannot change status from " + order.getStatus());
		}
	}

	public List<OrderSummary> getOrdersByStatusStream(Order.Status status) {
		return repository.findAll() // fetch all orders
				.stream() // create a stream
				.filter(order -> order.getStatus() == status) // filter by status
				.map(order -> {
					OrderSummary summary = new OrderSummary(order.getId(), order.getDescription(),
							order.getStatus().name());
					summary.logDetails(); // log here
					return summary; // return for collect
				}).collect(Collectors.toList()); // collect as list
	}
}
