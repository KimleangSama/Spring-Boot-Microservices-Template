CREATE TABLE orders
(
    id           BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    order_number VARCHAR(255)   NOT NULL,
    sku_code     VARCHAR(255)   NOT NULL,
    price        DECIMAL(10, 2) NOT NULL,
    quantity     INT            NOT NULL
);