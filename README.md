# Pay My Buddy

Pay My Buddy est une application de transfert bancaire entre particulier. (Projet étudiant)


# Diagramme de classe UML 

![Diagramme de classe UML](/ressources/Diagramme-de-classe-UML.png)


# Modèle physique de données

```
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
```