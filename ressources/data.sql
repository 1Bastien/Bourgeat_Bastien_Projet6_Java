CREATE TABLE user (
    user_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    last_name VARCHAR(30) NOT NULL,
    first_name VARCHAR(30) NOT NULL,
    email VARCHAR(30) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(5) NOT NULL,
    balance DECIMAL(10,2) DEFAULT 0.00 NOT NULL,
    billing_address VARCHAR(150) NOT NULL,
    bankAccountNumber VARCHAR(30),
);

CREATE TABLE contact_list (
    user_id BIGINT NOT NULL,
    contact_email VARCHAR(30) NOT NULL,
    
    PRIMARY KEY (user_id, contact_email),
    
    CONSTRAINT fk_contact_list_user_id FOREIGN KEY (user_id) REFERENCES user(id),
    CONSTRAINT fk_contact_list_contact_email FOREIGN KEY (contact_email) REFERENCES user(email)
);

CREATE TABLE transaction (
    transaction_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    sender_id BIGINT NOT NULL,
    receiver_id BIGINT NOT NULL,
    amount DECIMAL(10, 2) NOT NULL,
    fee DECIMAL(10,2) NOT NULL,
    transaction_date DATETIME NOT NULL,
    
    CONSTRAINT fk_transaction_sender_id FOREIGN KEY (sender_id) REFERENCES user (user_id),
    CONSTRAINT fk_transaction_receiver_id FOREIGN KEY (receiver_id) REFERENCES user (user_id),
    
    CONSTRAINT ck_transaction_amount CHECK (amount > 0)
);

INSERT INTO user (last_name, first_name, email, password, role, balance, billing_address, bankAccountNumber) VALUES 
	('Doe', 'John', 'john.doe@example.com', 'hashed_password', 'USER', 1000.00, '123 Main St', '1234567890');
	
INSERT INTO contact_list (user_id, contact_email) VALUES (1, 'jane.smith@example.com');

INSERT INTO transaction (sender_id, receiver_id, amount, fee, transaction_date) VALUES (1, 2, 50.00, 1.00, '2024-01-12 08:30:00');