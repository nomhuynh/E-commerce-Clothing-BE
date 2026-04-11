-- One default variant per product that has none (MySQL 8+).
-- Prerequisites: at least one row in Colors and Sizes with deleted_at IS NULL.
-- Run: mysql -u root -p stylex_spring < scripts/seed-default-product-variants.sql

INSERT INTO ProductVariants (
    variant_id,
    product_id,
    color_id,
    size_id,
    sku,
    price,
    stock_quantity,
    is_active,
    created_at,
    updated_at,
    deleted_at
)
SELECT
    UUID(),
    p.product_id,
    (SELECT c.color_id FROM Colors c WHERE c.deleted_at IS NULL ORDER BY c.color_id LIMIT 1),
    (SELECT s.size_id FROM Sizes s WHERE s.deleted_at IS NULL ORDER BY s.size_id LIMIT 1),
    CONCAT('AUTO-', REPLACE(UUID(), '-', '')),
    p.base_price,
    100,
    1,
    CURRENT_TIMESTAMP(6),
    CURRENT_TIMESTAMP(6),
    NULL
FROM Products p
WHERE p.deleted_at IS NULL
  AND NOT EXISTS (
      SELECT 1
      FROM ProductVariants v
      WHERE v.product_id = p.product_id
        AND v.deleted_at IS NULL
  );
