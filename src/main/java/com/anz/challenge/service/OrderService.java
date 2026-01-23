package com.anz.challenge.service;

import com.anz.challenge.model.Order;
import com.anz.challenge.repository.OrderRepository;
import com.anz.challenge.exception.OrderNotFoundException;
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
        if(order.getStatus() == null) order.setStatus(Order.Status.CREATED);
        Order saved = repository.save(order);
        notificationService.notifyStatusChange(saved.getId(), saved.getStatus().name());
        return saved;
    }

    @Transactional
    public List<Order> createBulkOrders(List<Order> orders) {
        // Ensure status is set
        for (Order o : orders) {
            if (o.getStatus() == null) o.setStatus(Order.Status.CREATED);
        }

        // Save all orders in a single transaction
        List<Order> savedOrders = repository.saveAll(orders);

        // Send notifications outside the transactional rollback effect
        for (Order o : savedOrders) {
            try {
                // Notify status change (Spring Retry + idempotency inside service)
                notificationService.notifyStatusChange(o.getId(), o.getStatus().name());
            } catch (Exception e) {
                // Log failure but do not affect order transaction
                log.error("Notification failed for order {} with status {}: {}", 
                          o.getId(), o.getStatus().name(), e.getMessage());
            }
        }

        return savedOrders;
    }

    public Optional<Order> getOrder(Long id) {
        return repository.findById(id);
    }

    @Transactional
    public Order updateStatus(Long id, Order.Status status) {
        Order order = repository.findById(id)
                .orElseThrow(() -> new OrderNotFoundException(id));
        // Validate allowed transitions
        validateStatusChange(order, status);
        order.setStatus(status);
        Order updated = repository.save(order);
        notificationService.notifyStatusChange(updated.getId(), updated.getStatus().name());
        return updated;
    }

    // Search with optional status and pagination
    public Page<Order> searchOrders(Order.Status status, Pageable pageable) {
        if(status != null) {
            return repository.findByStatus(status, pageable);
        } else {
            return repository.findAll(pageable);
        }
    }
    
    private void validateStatusChange(Order order, Order.Status newStatus) {
        if (order.getStatus() == Order.Status.COMPLETED ||
            order.getStatus() == Order.Status.CANCELLED) {
            throw new IllegalArgumentException(
                "Cannot change status from " + order.getStatus()
            );
        }
    }
}
