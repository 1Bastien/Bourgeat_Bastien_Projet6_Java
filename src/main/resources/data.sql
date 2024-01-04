CREATE TABLE user (
    user_id INT(11) PRIMARY KEY AUTO_INCREMENT,
    last_name VARCHAR(30),
    first_name VARCHAR(30),
    email VARCHAR(30) UNIQUE,
    password VARCHAR(30),
    billing_address VARCHAR(255)
);

CREATE TABLE transaction (
    transaction_id INT(11) PRIMARY KEY AUTO_INCREMENT,
    sender_id INT(11),
    recipient_id INT(11),
    amount DECIMAL(10, 2),
    transaction_date DATETIME,
    
    CONSTRAINT fk_transaction_sender_id FOREIGN KEY (sender_id) REFERENCES user (user_id),
    CONSTRAINT fk_transaction_recipient_id FOREIGN KEY (recipient_id) REFERENCES user (user_id),
    
    CONSTRAINT ck_transaction_amount CHECK (amount > 0)
);

INSERT INTO user (last_name, first_name, email, password, billing_address) VALUES
    ('Doe', 'John', 'john.doe@example.com', 'motdepasse1', '123 Main St'),
    ('Smith', 'Jane', 'jane.smith@example.com', 'motdepasse2', '456 Oak St');

INSERT INTO transaction (sender_id, recipient_id, amount, transaction_date) VALUES
    (1, 2, 50.00, '2022-01-01 12:00:00'),
    (2, 1, 30.00, '2022-01-02 15:30:00');
