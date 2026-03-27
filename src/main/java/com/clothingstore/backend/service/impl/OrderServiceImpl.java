package com.clothingstore.backend.service.impl;

import com.clothingstore.backend.dto.order.OrderRequest;
import com.clothingstore.backend.dto.order.OrderResponse;
import com.clothingstore.backend.entity.*;
import com.clothingstore.backend.entity.enums.OrderStatus;
import com.clothingstore.backend.entity.enums.PaymentStatus;
import com.clothingstore.backend.repository.*;
import com.clothingstore.backend.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final CartRepository cartRepository;
    private final PaymentRepository paymentRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public OrderResponse create(OrderRequest request) {
        User user = userRepository.findById(request.getUserId()).orElseThrow(() -> new RuntimeException("User not found"));
        Cart cart = cartRepository.findByUserId(request.getUserId()).orElseThrow(() -> new RuntimeException("Cart is empty"));
        if (cart.getItems() == null || cart.getItems().isEmpty()) {
            throw new RuntimeException("Cart is empty");
        }

        BigDecimal subtotal = cart.getItems().stream()
                .map(i -> i.getVariant().getPrice().multiply(BigDecimal.valueOf(i.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        Order order = Order.builder()
                .orderCode("ORD-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase())
                .user(user)
                .customerName(request.getCustomerName())
                .customerPhone(request.getCustomerPhone())
                .shippingAddress(request.getShippingAddress())
                .note(request.getNote())
                .subtotalAmount(subtotal)
                .shippingFee(BigDecimal.ZERO)
                .discountAmount(BigDecimal.ZERO)
                .totalAmount(subtotal)
                .orderStatus(OrderStatus.PENDING)
                .build();

        order = orderRepository.save(order);

        for (CartItem ci : cart.getItems()) {
            OrderItem oi = OrderItem.builder()
                    .order(order)
                    .variant(ci.getVariant())
                    .productName(ci.getVariant().getProduct().getName())
                    .sku(ci.getVariant().getSku())
                    .quantity(ci.getQuantity())
                    .unitPrice(ci.getVariant().getPrice())
                    .totalPrice(ci.getVariant().getPrice().multiply(BigDecimal.valueOf(ci.getQuantity())))
                    .build();
            orderItemRepository.save(oi);
        }

        paymentRepository.save(Payment.builder()
                .order(order)
                .paymentMethod(request.getPaymentMethod())
                .paymentStatus(request.getPaymentMethod().name().equals("COD") ? PaymentStatus.SUCCESS : PaymentStatus.PENDING)
                .amount(order.getTotalAmount())
                .paymentTime(request.getPaymentMethod().name().equals("COD") ? LocalDateTime.now() : null)
                .build());

        cart.getItems().clear();
        cartRepository.save(cart);

        return toResponse(order);
    }

    @Override
    public List<OrderResponse> getByUser(String userId) {
        return orderRepository.findByUserIdOrderByCreatedAtDesc(userId).stream().map(this::toResponse).toList();
    }

    @Override
    public OrderResponse updateStatus(String orderId, String status) {
        Order order = orderRepository.findById(orderId).orElseThrow(() -> new RuntimeException("Order not found"));
        order.setOrderStatus(OrderStatus.valueOf(status));
        return toResponse(orderRepository.save(order));
    }

    private OrderResponse toResponse(Order order) {
        return OrderResponse.builder()
                .id(order.getId())
                .orderCode(order.getOrderCode())
                .userId(order.getUser() != null ? order.getUser().getId() : null)
                .totalAmount(order.getTotalAmount())
                .status(order.getOrderStatus())
                .build();
    }
}
