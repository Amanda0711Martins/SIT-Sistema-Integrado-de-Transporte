-- schema.sql
-- This file contains database schema definition
CREATE TABLE IF NOT EXISTS expenses (
                                        id SERIAL PRIMARY KEY,
                                        description VARCHAR(255) NOT NULL,
    amount DECIMAL(12, 2) NOT NULL,
    type VARCHAR(20) NOT NULL,
    category VARCHAR(50),
    expense_date TIMESTAMP NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP
    );

CREATE TABLE IF NOT EXISTS invoices (
                                        id SERIAL PRIMARY KEY,
                                        invoice_number VARCHAR(50) NOT NULL UNIQUE,
    client_id BIGINT NOT NULL,
    amount DECIMAL(12, 2) NOT NULL,
    issue_date TIMESTAMP NOT NULL,
    due_date TIMESTAMP,
    status VARCHAR(20) NOT NULL,
    fiscal_key VARCHAR(255),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP
    );

CREATE INDEX IF NOT EXISTS idx_invoice_client_id ON invoices(client_id);
CREATE INDEX IF NOT EXISTS idx_invoice_status ON invoices(status);
CREATE INDEX IF NOT EXISTS idx_invoice_issue_date ON invoices(issue_date);

CREATE TABLE IF NOT EXISTS billings (
                                        id SERIAL PRIMARY KEY,
                                        client_id BIGINT NOT NULL,
                                        billing_number VARCHAR(50) NOT NULL UNIQUE,
    amount DECIMAL(12, 2) NOT NULL,
    issue_date TIMESTAMP NOT NULL,
    due_date TIMESTAMP NOT NULL,
    status VARCHAR(20) NOT NULL,
    bar_code VARCHAR(255),
    payment_method VARCHAR(50),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP
    );

CREATE INDEX IF NOT EXISTS idx_billing_client_id ON billings(client_id);
CREATE INDEX IF NOT EXISTS idx_billing_status ON billings(status);
CREATE INDEX IF NOT EXISTS idx_billing_due_date ON billings(due_date);