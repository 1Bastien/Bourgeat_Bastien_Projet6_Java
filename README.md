# Pay My Buddy

Pay My Buddy est une application de transfert bancaire entre particulier. (Projet étudiant)


# Diagramme de classe UML 

![Diagramme de classe UML](/ressources/UML.png)


# Modèle physique de données

```
CREATE TABLE user (
    user_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    last_name VARCHAR(30) NOT NULL,
    first_name VARCHAR(30) NOT NULL,
    email VARCHAR(30) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(5) NOT NULL,
    balance DECIMAL(10,2) DEFAULT 0.00 NOT NULL,
    billing_address VARCHAR(150) NOT NULL,
    bankAccountNumber VARCHAR(30)
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
```