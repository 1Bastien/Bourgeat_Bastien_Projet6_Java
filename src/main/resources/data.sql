CREATE TABLE user (
    user_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    last_name VARCHAR(30) NOT NULL,
    first_name VARCHAR(30) NOT NULL,
    email VARCHAR(30) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(20) NOT NULL DEFAULT 'ROLE_USER',
    balance DECIMAL NOT NULL,
    billing_address VARCHAR(150)
);

CREATE TABLE transaction (
    transaction_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    sender_id BIGINT NOT NULL,
    receiver_id BIGINT NOT NULL,
    amount DECIMAL(10, 2) NOT NULL,
    transaction_date DATE NOT NULL,
    
    CONSTRAINT fk_transaction_sender_id FOREIGN KEY (sender_id) REFERENCES user (user_id),
    CONSTRAINT fk_transaction_receiver_id FOREIGN KEY (receiver_id) REFERENCES user (user_id),
    
    CONSTRAINT ck_transaction_amount CHECK (amount > 0)
    CONSTRAINT ck_transaction_date CHECK (transaction_date <= NOW())
);

INSERT INTO user (last_name, first_name, email, password, role, balance, billing_address) VALUES
    ('Doe', 'John', 'john.doe@example.com', 'motdepasse1', 'ROLE_USER', 0.0, '123 Main St'),
    ('Smith', 'Jane', 'jane.smith@example.com', 'motdepasse2', 'ROLE_USER', 0.0, '456 Oak St');

INSERT INTO transaction (sender_id, receiver_id, amount, transaction_date) VALUES
    (1, 2, 50.00, '2022-01-01 12:00:00'),
    (2, 1, 30.00, '2022-01-02 15:30:00');
    