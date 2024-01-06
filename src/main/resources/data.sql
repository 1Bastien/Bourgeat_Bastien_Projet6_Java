CREATE TABLE user (
    user_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    last_name VARCHAR(30) NOT NULL,
    first_name VARCHAR(30) NOT NULL,
    email VARCHAR(30) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(5) NOT NULL,
    balance DECIMAL(10,2) DEFAULT 0.00 NOT NULL,
    billing_address VARCHAR(150) NOT NULL,
    
    CONSTRAINT ck_user_balance CHECK (balance > 0)
);

CREATE TABLE user_contacts (
    user_id BIGINT NOT NULL,
    contact_id BIGINT NOT NULL,
    
    PRIMARY KEY (user_id, contact_id),
    
    CONSTRAINT fk_user_contacts_user_id FOREIGN KEY (user_id) REFERENCES user (id),
    CONSTRAINT fk_user_contacts_contact_id FOREIGN KEY (contact_id) REFERENCES user (id)
);


CREATE TABLE transaction (
    transaction_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    sender_id BIGINT NOT NULL,
    receiver_id BIGINT NOT NULL,
    amount DECIMAL(10, 2) NOT NULL,
    transaction_date DATETIME NOT NULL,
    
    CONSTRAINT fk_transaction_sender_id FOREIGN KEY (sender_id) REFERENCES user (user_id),
    CONSTRAINT fk_transaction_receiver_id FOREIGN KEY (receiver_id) REFERENCES user (user_id),
    
    CONSTRAINT ck_transaction_amount CHECK (amount > 0)
);

INSERT INTO user (last_name, first_name, email, password, role, balance, billing_address) VALUES 
       ('Doe', 'John', 'john.doe@example.com', 'hashed_password_1', 'USER', 100.00, '123 Main St'),
       ('Smith', 'Alice', 'alice.smith@example.com', 'hashed_password_2', 'ADMIN', 150.00, '456 Oak Ave');

INSERT INTO user_contacts (user_id, contact_id) VALUES (1, 2);

INSERT INTO transaction (sender_id, receiver_id, amount, transaction_date) VALUES (1, 2, 50.00, '2022-01-01 12:00:00');
    