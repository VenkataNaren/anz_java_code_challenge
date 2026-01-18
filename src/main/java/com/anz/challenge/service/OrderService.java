package com.anz.challenge.service;

import com.anz.challenge.model.Order;
import com.anz.challenge.repository.OrderRepository;
import com.anz.challenge.exception.OrderNotFoundException;
import lombok.RequiredArgsConstructor;
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
        for (Order o : orders) {
            if(o.getStatus() == null) o.setStatus(Order.Status.CREATED);
        }
        List<Order> savedOrders = repository.saveAll(orders);

        // send notifications for each order
        for(Order o : savedOrders) {
            notificationService.notifyStatusChange(o.getId(), o.getStatus().name());
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
}
