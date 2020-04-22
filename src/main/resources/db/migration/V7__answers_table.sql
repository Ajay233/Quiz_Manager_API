CREATE TABLE answers (
    id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    question_id BIGINT NOT NULL,
    answer_number INT NOT NULL,
    description VARCHAR(250) NOT NULL,
    correct_answer BOOLEAN,
    PRIMARY KEY (id)
);