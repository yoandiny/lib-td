CREATE EXTENSION IF NOT EXISTS pgcrypto;

-- 1. Dummy tables
CREATE TABLE IF NOT EXISTS dummy
(
    id VARCHAR
        CONSTRAINT dummy_pk PRIMARY KEY
);

INSERT INTO dummy (id)
VALUES ('dummy-table-id-1')
ON CONFLICT (id) DO NOTHING;

CREATE TABLE IF NOT EXISTS dummy_uuid
(
    id VARCHAR
        CONSTRAINT dummy_uuid_pk PRIMARY KEY
);

INSERT INTO dummy_uuid (id)
VALUES ('dummy-uuid-id-1')
ON CONFLICT (id) DO NOTHING;

-- 2. Cleanups
DROP TABLE IF EXISTS book_copy;
DROP TYPE IF EXISTS copy_status;
DROP TYPE IF EXISTS copy_condition;

-- 3. Core tables (Author & Book)
CREATE TABLE IF NOT EXISTS author
(
    id         UUID DEFAULT gen_random_uuid()
        CONSTRAINT author_pk PRIMARY KEY,
    last_name  VARCHAR NOT NULL,
    first_name VARCHAR NOT NULL
);

CREATE TABLE IF NOT EXISTS book
(
    id               UUID DEFAULT gen_random_uuid()
        CONSTRAINT book_pk PRIMARY KEY,
    title            VARCHAR NOT NULL,
    author_id        UUID    NOT NULL
        CONSTRAINT book_author_fk REFERENCES author (id),
    isbn             VARCHAR UNIQUE,
    publication_year INTEGER,
    genre            VARCHAR
);

-- 4. Formats & Book Editions
CREATE TABLE IF NOT EXISTS format
(
    id           UUID DEFAULT gen_random_uuid()
        CONSTRAINT format_pk PRIMARY KEY,
    format_label VARCHAR(50) NOT NULL
);

CREATE TABLE IF NOT EXISTS book_edition
(
    id        UUID DEFAULT gen_random_uuid()
        CONSTRAINT book_edition_pk PRIMARY KEY,
    isbn      VARCHAR UNIQUE,
    book_id   UUID NOT NULL
        CONSTRAINT book_edition_book_fk REFERENCES book (id),
    format_id UUID
        CONSTRAINT book_edition_format_fk REFERENCES format (id),
    price     DOUBLE PRECISION
);

-- 5. Arrivals
CREATE TABLE IF NOT EXISTS arrival
(
    id         UUID DEFAULT gen_random_uuid()
        CONSTRAINT arrival_pk PRIMARY KEY,
    arrived_at TIMESTAMP NOT NULL
);

CREATE TABLE IF NOT EXISTS arrival_line
(
    id              UUID DEFAULT gen_random_uuid()
        CONSTRAINT arrival_line_pk PRIMARY KEY,
    arrival_id      UUID NOT NULL
        CONSTRAINT arrival_line_arrival_fk REFERENCES arrival (id) ON DELETE CASCADE,
    book_edition_id UUID NOT NULL
        CONSTRAINT arrival_line_book_edition_fk REFERENCES book_edition (id),
    quantity        INTEGER NOT NULL
);

CREATE INDEX IF NOT EXISTS arrival_line_book_edition_idx
    ON arrival_line (book_edition_id);

-- 6. Customers
CREATE TABLE IF NOT EXISTS customer
(
    id          UUID DEFAULT gen_random_uuid()
        CONSTRAINT customer_pk PRIMARY KEY,
    full_name   VARCHAR NOT NULL,
    email       VARCHAR NOT NULL UNIQUE,
    phone       VARCHAR,
    register_at TIMESTAMP
);

-- 7. Sales & Payments
CREATE TABLE IF NOT EXISTS sale
(
    id           UUID DEFAULT gen_random_uuid()
        CONSTRAINT sale_pk PRIMARY KEY,
    id_customer  UUID           NOT NULL
        CONSTRAINT sale_customer_fk REFERENCES customer (id),
    sale_date    TIMESTAMP      NOT NULL,
    total_amount NUMERIC(15, 2) NOT NULL DEFAULT 0,
    status       VARCHAR(20)    NOT NULL
        CONSTRAINT sale_status_check CHECK (status IN ('IN_PROGRESS', 'VALIDATED', 'CANCELLED'))
);

CREATE TABLE IF NOT EXISTS sale_line
(
    id              UUID DEFAULT gen_random_uuid()
        CONSTRAINT sale_line_pk PRIMARY KEY,
    id_sale         UUID           NOT NULL
        CONSTRAINT sale_line_sale_fk REFERENCES sale (id),
    id_book_edition UUID           NOT NULL
        CONSTRAINT sale_line_book_edition_fk REFERENCES book_edition (id),
    unit_price      NUMERIC(15, 2) NOT NULL,
    quantity        INTEGER        NOT NULL
        CONSTRAINT sale_line_quantity_positive CHECK (quantity > 0)
);

CREATE TABLE IF NOT EXISTS payment
(
    id      UUID DEFAULT gen_random_uuid()
        CONSTRAINT payment_pk PRIMARY KEY,
    id_sale UUID           NOT NULL UNIQUE
        CONSTRAINT payment_sale_fk REFERENCES sale (id),
    amount  NUMERIC(15, 2) NOT NULL,
    method  VARCHAR(20)    NOT NULL
        CONSTRAINT payment_method_check CHECK (method IN ('CASH', 'CARD', 'MOBILE')),
    status  VARCHAR(20)    NOT NULL
        CONSTRAINT payment_status_check CHECK (status IN ('PENDING', 'PAID', 'FAILED')),
    paid_at TIMESTAMP
);
