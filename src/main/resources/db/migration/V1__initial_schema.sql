-- The first migration establishes the normalized financial records used to derive balances.
CREATE TABLE app_user (
    id CHAR(36) PRIMARY KEY,
    email VARCHAR(320) NOT NULL UNIQUE,
    display_name VARCHAR(120) NOT NULL,
    created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    updated_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6)
);

CREATE TABLE expense_group (
    id CHAR(36) PRIMARY KEY,
    name VARCHAR(120) NOT NULL,
    default_currency CHAR(3) NOT NULL,
    created_by CHAR(36) NOT NULL,
    created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    archived_at DATETIME(6),
    CONSTRAINT expense_group_created_by_fk FOREIGN KEY (created_by) REFERENCES app_user(id)
);

CREATE TABLE membership (
    group_id CHAR(36) NOT NULL,
    user_id CHAR(36) NOT NULL,
    role VARCHAR(20) NOT NULL,
    status VARCHAR(20) NOT NULL,
    joined_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    removed_at DATETIME(6),
    PRIMARY KEY (group_id, user_id),
    CONSTRAINT membership_group_fk FOREIGN KEY (group_id) REFERENCES expense_group(id),
    CONSTRAINT membership_user_fk FOREIGN KEY (user_id) REFERENCES app_user(id),
    CONSTRAINT membership_role_check CHECK (role IN ('OWNER', 'MEMBER')),
    CONSTRAINT membership_status_check CHECK (status IN ('ACTIVE', 'REMOVED'))
);

CREATE TABLE expense (
    id CHAR(36) PRIMARY KEY,
    group_id CHAR(36) NOT NULL,
    description VARCHAR(240) NOT NULL,
    amount_minor BIGINT NOT NULL CHECK (amount_minor > 0),
    currency CHAR(3) NOT NULL,
    paid_at DATE NOT NULL,
    created_by CHAR(36) NOT NULL,
    split_method VARCHAR(20) NOT NULL,
    created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    updated_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    CONSTRAINT expense_group_fk FOREIGN KEY (group_id) REFERENCES expense_group(id),
    CONSTRAINT expense_created_by_fk FOREIGN KEY (created_by) REFERENCES app_user(id),
    CONSTRAINT expense_split_method_check CHECK (split_method IN ('EQUAL', 'EXACT', 'PERCENTAGE'))
);

CREATE TABLE expense_payment (
    expense_id CHAR(36) NOT NULL,
    user_id CHAR(36) NOT NULL,
    paid_minor BIGINT NOT NULL CHECK (paid_minor > 0),
    PRIMARY KEY (expense_id, user_id),
    CONSTRAINT payment_expense_fk FOREIGN KEY (expense_id) REFERENCES expense(id),
    CONSTRAINT payment_user_fk FOREIGN KEY (user_id) REFERENCES app_user(id)
);

CREATE TABLE expense_share (
    expense_id CHAR(36) NOT NULL,
    user_id CHAR(36) NOT NULL,
    share_minor BIGINT NOT NULL CHECK (share_minor >= 0),
    percentage_basis_points INTEGER,
    PRIMARY KEY (expense_id, user_id),
    CONSTRAINT share_expense_fk FOREIGN KEY (expense_id) REFERENCES expense(id),
    CONSTRAINT share_user_fk FOREIGN KEY (user_id) REFERENCES app_user(id)
);

CREATE TABLE settlement (
    id CHAR(36) PRIMARY KEY,
    group_id CHAR(36) NOT NULL,
    from_user_id CHAR(36) NOT NULL,
    to_user_id CHAR(36) NOT NULL,
    amount_minor BIGINT NOT NULL CHECK (amount_minor > 0),
    currency CHAR(3) NOT NULL,
    settled_at DATETIME(6) NOT NULL,
    created_by CHAR(36) NOT NULL,
    CONSTRAINT settlement_group_fk FOREIGN KEY (group_id) REFERENCES expense_group(id),
    CONSTRAINT settlement_from_user_fk FOREIGN KEY (from_user_id) REFERENCES app_user(id),
    CONSTRAINT settlement_to_user_fk FOREIGN KEY (to_user_id) REFERENCES app_user(id),
    CONSTRAINT settlement_created_by_fk FOREIGN KEY (created_by) REFERENCES app_user(id),
    CONSTRAINT settlement_different_users CHECK (from_user_id <> to_user_id)
);

CREATE INDEX expense_group_paid_at_idx ON expense (group_id, paid_at DESC);
CREATE INDEX settlement_group_settled_at_idx ON settlement (group_id, settled_at DESC);
