CREATE TABLE verification_token (
    id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    token VARCHAR(250) NOT NULL,
    expiry_date TIMESTAMP NOT NULL,
    PRIMARY KEY (id)
);