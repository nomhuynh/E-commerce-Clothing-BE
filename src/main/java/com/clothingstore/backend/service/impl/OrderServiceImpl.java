package com.clothingstore.backend.service.impl;

import com.clothingstore.backend.dto.order.OrderItemResponse;
import com.clothingstore.backend.dto.order.OrderRequest;
import com.clothingstore.backend.dto.order.OrderResponse;
import com.clothingstore.backend.entity.*;
import com.clothingstore.backend.entity.enums.DiscountType;
import com.clothingstore.backend.entity.enums.OrderStatus;
import com.clothingstore.backend.entity.enums.PaymentStatus;
import com.clothingstore.backend.repository.*;
import com.clothingstore.backend.service.LoyaltyService;
import com.clothingstore.backend.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
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
    private final LoyaltyService loyaltyService;
    private final CouponRepository couponRepository;
    private final CouponUsageRepository couponUsageRepository;

    @Override
    @Transactional
    public OrderResponse create(OrderRequest request) {
        User user = userRepository.findById(request.getUserId()).orElseThrow(() -> new RuntimeException("User not found"));
        Cart cart = cartRepository.findByUserId(request.getUserId()).orElseThrow(() -> new RuntimeException("Cart is empty"));
        if (cart.getItems() == null || cart.getItems().isEmpty()) {
            throw new RuntimeException("Cart is empty");
        }

        BigDecimal subtotal = cart.getItems().stream()
                .map(i -> unitPrice(i.getVariant()).multiply(BigDecimal.valueOf(i.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal shippingFee = BigDecimal.ZERO;
        Coupon appliedCoupon = null;
        BigDecimal discountAmount = BigDecimal.ZERO;
        if (cart.getCoupon() != null) {
            String couponId = cart.getCoupon().getId();
            Coupon coupon = couponRepository.findById(couponId)
                    .orElseThrow(() -> new RuntimeException("Coupon not found"));
            LocalDateTime now = LocalDateTime.now();
            if (!Boolean.TRUE.equals(coupon.getIsActive())
                    || coupon.getEndDate().isBefore(now)
                    || coupon.getStartDate().isAfter(now)) {
                throw new RuntimeException("Coupon is not valid");
            }
            discountAmount = computeDiscount(subtotal, coupon);
            if (discountAmount.compareTo(BigDecimal.ZERO) <= 0) {
                throw new RuntimeException("Order subtotal does not meet minimum for this coupon");
            }
            int limit = coupon.getUsageLimit() != null ? coupon.getUsageLimit() : 0;
            int used = coupon.getUsageCount() != null ? coupon.getUsageCount() : 0;
            if (limit > 0 && used >= limit) {
                throw new RuntimeException("Coupon usage limit reached");
            }
            Integer perUser = coupon.getPerUserLimit();
            if (perUser != null && perUser > 0) {
                long userUses = couponUsageRepository.countByCoupon_IdAndUser_Id(coupon.getId(), user.getId());
                if (userUses >= perUser) {
                    throw new RuntimeException("Coupon per-user limit reached");
                }
            }
            appliedCoupon = coupon;
        }

        BigDecimal totalAmount = subtotal.add(shippingFee).subtract(discountAmount);
        if (totalAmount.compareTo(BigDecimal.ZERO) < 0) {
            totalAmount = BigDecimal.ZERO;
        }

        Order order = Order.builder()
                .orderCode("ORD-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase())
                .user(user)
                .customerName(request.getCustomerName())
                .customerPhone(request.getCustomerPhone())
                .shippingAddress(request.getShippingAddress())
                .note(request.getNote())
                .subtotalAmount(subtotal)
                .shippingFee(shippingFee)
                .discountAmount(discountAmount)
                .totalAmount(totalAmount)
                .coupon(appliedCoupon)
                .orderStatus(OrderStatus.PENDING)
                .build();

        order = orderRepository.save(order);

        for (CartItem ci : cart.getItems()) {
            ProductVariant v = ci.getVariant();
            BigDecimal unit = unitPrice(v);
            OrderItem oi = OrderItem.builder()
                    .order(order)
                    .variant(v)
                    .productName(v.getProduct().getName())
                    .sku(v.getSku())
                    .quantity(ci.getQuantity())
                    .unitPrice(unit)
                    .totalPrice(unit.multiply(BigDecimal.valueOf(ci.getQuantity())))
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

        if (appliedCoupon != null) {
            appliedCoupon.setUsageCount((appliedCoupon.getUsageCount() != null ? appliedCoupon.getUsageCount() : 0) + 1);
            couponRepository.save(appliedCoupon);
            couponUsageRepository.save(CouponUsage.builder()
                    .coupon(appliedCoupon)
                    .user(user)
                    .orderId(order.getId())
                    .discountAmount(discountAmount)
                    .usedAt(LocalDateTime.now())
                    .build());
        }

        cart.getItems().clear();
        cart.setCoupon(null);
        cartRepository.save(cart);

        Order full = orderRepository.findByIdAndUserIdWithItems(order.getId(), request.getUserId())
                .orElse(order);
        return toResponse(full);
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderResponse> getByUser(String userId) {
        return orderRepository.findByUserIdOrderByCreatedAtDesc(userId).stream().map(this::toResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public OrderResponse getByIdForUser(String orderId, String userId) {
        Order order = orderRepository.findByIdAndUserIdWithItems(orderId, userId)
                .orElseThrow(() -> new RuntimeException("Order not found"));
        return toResponse(order);
    }

    @Override
    @Transactional
    public OrderResponse cancelOrder(String orderId, String userId) {
        Order order = orderRepository.findByIdAndUserId(orderId, userId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        if (!(order.getOrderStatus() == OrderStatus.PENDING || order.getOrderStatus() == OrderStatus.CONFIRMED)) {
            throw new RuntimeException("Order cannot be cancelled in current status");
        }

        order.setOrderStatus(OrderStatus.CANCELLED);
        orderRepository.save(order);
        reverseCouponUsageForOrder(order.getId());
        return toResponse(orderRepository.findByIdAndUserIdWithItems(orderId, userId)
                .orElseThrow(() -> new RuntimeException("Order not found")));
    }

    @Override
    @Transactional
    public OrderResponse requestReturn(String orderId, String userId, String reason) {
        Order order = orderRepository.findByIdAndUserId(orderId, userId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        if (order.getOrderStatus() != OrderStatus.DELIVERED) {
            throw new RuntimeException("Only delivered orders can request return");
        }

        order.setNote(reason != null && !reason.isBlank()
                ? (order.getNote() == null ? "RETURN_REQUEST: " + reason : order.getNote() + "\nRETURN_REQUEST: " + reason)
                : (order.getNote() == null ? "RETURN_REQUEST" : order.getNote() + "\nRETURN_REQUEST"));
        OrderStatus previousStatus = order.getOrderStatus();
        order.setOrderStatus(OrderStatus.RETURNED);
        orderRepository.save(order);
        loyaltyService.onOrderStatusChanged(order, previousStatus, OrderStatus.RETURNED);
        return toResponse(orderRepository.findByIdAndUserIdWithItems(orderId, userId)
                .orElseThrow(() -> new RuntimeException("Order not found")));
    }

    @Override
    @Transactional
    public OrderResponse updateStatus(String orderId, String status) {
        Order order = orderRepository.findById(orderId).orElseThrow(() -> new RuntimeException("Order not found"));
        OrderStatus previousStatus = order.getOrderStatus();
        OrderStatus newStatus = OrderStatus.valueOf(status);
        order.setOrderStatus(newStatus);
        orderRepository.save(order);
        if (newStatus == OrderStatus.CANCELLED && previousStatus != OrderStatus.CANCELLED) {
            reverseCouponUsageForOrder(orderId);
        }
        loyaltyService.onOrderStatusChanged(order, previousStatus, newStatus);
        return toResponse(orderRepository.findByIdWithItems(orderId).orElse(order));
    }

    private OrderResponse toResponse(Order order) {
        String paymentMethod = paymentRepository.findByOrderId(order.getId())
                .map(p -> p.getPaymentMethod().name())
                .orElse(null);

        List<OrderItemResponse> itemRows = new ArrayList<>();
        if (order.getItems() != null) {
            for (OrderItem oi : order.getItems()) {
                ProductVariant v = oi.getVariant();
                String imageUrl = v != null ? resolveImageUrl(v) : null;
                String colorName = v != null && v.getColor() != null ? v.getColor().getName() : "";
                String sizeName = v != null && v.getSize() != null ? v.getSize().getName() : "";
                String variantId = v != null ? v.getId() : null;
                itemRows.add(OrderItemResponse.builder()
                        .id(oi.getId())
                        .variantId(variantId)
                        .productName(oi.getProductName())
                        .sku(oi.getSku())
                        .quantity(oi.getQuantity())
                        .unitPrice(oi.getUnitPrice())
                        .totalPrice(oi.getTotalPrice())
                        .imageUrl(imageUrl)
                        .colorName(colorName)
                        .sizeName(sizeName)
                        .build());
            }
        }

        return OrderResponse.builder()
                .id(order.getId())
                .orderCode(order.getOrderCode())
                .userId(order.getUser() != null ? order.getUser().getId() : null)
                .customerName(order.getCustomerName())
                .customerPhone(order.getCustomerPhone())
                .shippingAddress(order.getShippingAddress())
                .note(order.getNote())
                .subtotalAmount(order.getSubtotalAmount())
                .shippingFee(order.getShippingFee())
                .discountAmount(order.getDiscountAmount())
                .totalAmount(order.getTotalAmount())
                .status(order.getOrderStatus())
                .paymentMethod(paymentMethod)
                .createdAt(order.getCreatedAt())
                .items(itemRows)
                .build();
    }

    private void reverseCouponUsageForOrder(String orderId) {
        couponUsageRepository.findByOrderId(orderId).ifPresent(usage -> {
            Coupon c = usage.getCoupon();
            int cnt = c.getUsageCount() != null ? c.getUsageCount() : 0;
            if (cnt > 0) {
                c.setUsageCount(cnt - 1);
                couponRepository.save(c);
            }
            couponUsageRepository.delete(usage);
        });
    }

    private static BigDecimal unitPrice(ProductVariant v) {
        if (v.getPrice() != null && v.getPrice().compareTo(BigDecimal.ZERO) > 0) {
            return v.getPrice();
        }
        if (v.getProduct() != null && v.getProduct().getBasePrice() != null) {
            return v.getProduct().getBasePrice();
        }
        return BigDecimal.ZERO;
    }

    private static BigDecimal computeDiscount(BigDecimal subtotal, Coupon c) {
        if (subtotal.compareTo(BigDecimal.ZERO) <= 0) {
            return BigDecimal.ZERO;
        }
        if (c.getMinOrderValue() != null && subtotal.compareTo(c.getMinOrderValue()) < 0) {
            return BigDecimal.ZERO;
        }
        BigDecimal d;
        if (c.getDiscountType() == DiscountType.percentage) {
            d = subtotal.multiply(c.getDiscountValue()).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
            if (c.getMaxDiscountAmount() != null && d.compareTo(c.getMaxDiscountAmount()) > 0) {
                d = c.getMaxDiscountAmount();
            }
        } else {
            d = c.getDiscountValue();
        }
        if (d.compareTo(subtotal) > 0) {
            d = subtotal;
        }
        return d;
    }

    private static String resolveImageUrl(ProductVariant v) {
        if (v.getImage() != null && !v.getImage().isBlank()) {
            return v.getImage();
        }
        Product p = v.getProduct();
        if (p == null || p.getImages() == null || p.getImages().isEmpty()) {
            return null;
        }
        List<ProductImage> imgs = p.getImages();
        return imgs.stream()
                .filter(img -> Boolean.TRUE.equals(img.getIsThumbnail()))
                .min(Comparator.comparing(img -> img.getSortOrder() != null ? img.getSortOrder() : 0))
                .map(ProductImage::getImageUrl)
                .orElseGet(() -> imgs.stream()
                        .min(Comparator.comparing(img -> img.getSortOrder() != null ? img.getSortOrder() : 0))
                        .map(ProductImage::getImageUrl)
                        .orElse(null));
    }
}
