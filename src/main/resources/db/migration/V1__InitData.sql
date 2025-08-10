-- Создание таблицы clients
CREATE TABLE clients
(
    id         BIGINT AUTO_INCREMENT PRIMARY KEY,
    first_name VARCHAR(255) NOT NULL,
    last_name  VARCHAR(255) NOT NULL,
    patronymic VARCHAR(255) NOT NULL,
    age        INT          NOT NULL,
    phone_number      VARCHAR(255) NOT NULL,
    city       VARCHAR(255) NOT NULL
);

-- Создание таблицы operators
CREATE TABLE operators
(
    id            BIGINT AUTO_INCREMENT PRIMARY KEY,
    first_name    VARCHAR(255) NOT NULL,
    last_name     VARCHAR(255) NOT NULL,
    date_of_birth DATE         NOT NULL,
    email         VARCHAR(255) NOT NULL,
    phone_number         VARCHAR(255) NOT NULL,
    address       VARCHAR(255) NOT NULL,
    status        BOOLEAN      NOT NULL
);

-- Создание таблицы orders
CREATE TABLE orders
(
    id                BIGINT AUTO_INCREMENT PRIMARY KEY,
    description       TEXT     NOT NULL,
    created           DATETIME NOT NULL,
    closed            DATETIME,
    status            ENUM ('REJECTED','EXECUTED','SECONDARY_REVIEW','POSTPONED'),
    clients_comment   VARCHAR(255),
    operators_comment VARCHAR(255),
    client_id         BIGINT   NOT NULL,
    operator_id       BIGINT   NOT NULL,
    FOREIGN KEY (client_id) REFERENCES clients (id),
    FOREIGN KEY (operator_id) REFERENCES operators (id)
);

-- Создание таблицы work_phones
CREATE TABLE work_phones
(
    id            BIGINT AUTO_INCREMENT PRIMARY KEY,
    phone_number  VARCHAR(255) NOT NULL,
    phone_type    VARCHAR(255) NOT NULL,
    serial_number VARCHAR(255) NOT NULL,
    operator_id   BIGINT DEFAULT NULL,
    FOREIGN KEY (operator_id) REFERENCES operators (id)
);

-- Создание таблицы phone_usage_history
CREATE TABLE phone_usage_history
(
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    usage_start DATETIME NOT NULL,
    usage_end   DATETIME,
    operator_id BIGINT   NOT NULL,
    phone_id    BIGINT   NOT NULL,
    FOREIGN KEY (operator_id) REFERENCES operators (id),
    FOREIGN KEY (phone_id) REFERENCES work_phones (id)
);

-- Создание таблицы tasks
CREATE TABLE tasks
(
    id           BIGINT AUTO_INCREMENT PRIMARY KEY,
    task_details VARCHAR(255)    NOT NULL,
    is_completed BOOLEAN NOT NULL,
    list_index INT NOT NULL,
    operator_id  BIGINT NOT NULL,
    FOREIGN KEY (operator_id) REFERENCES operators (id)
);

-- Вставка данных в таблицу clients
INSERT INTO clients (first_name, last_name, patronymic, age, phone_number, city)
VALUES ('Иван', 'Иванов', 'Иванович', 30, '+71234567890', 'Москва'),
       ('Мария', 'Петрова', 'Сергеевна', 28, '+70987654321', 'Санкт-Петербург'),
       ('Алексей', 'Сидоров', 'Александрович', 45, '+75551234567', 'Казань'),
       ('Ольга', 'Кузнецова', 'Владимировна', 35, '+79876543210', 'Новосибирск'),
       ('Дмитрий', 'Смирнов', 'Петрович', 29, '+71112223344', 'Екатеринбург');

-- Вставка данных в таблицу operators
INSERT INTO operators (first_name, last_name, date_of_birth, email, phone_number, address, status)
VALUES ('Анна', 'Смирнова', '1985-05-15', 'anna.smirnova@gmail.com', '+71112223344', 'Москва, ул. Ленина, 1', TRUE),
       ('Борис', 'Кузнецов', '1990-10-20', 'boris.kuznetsov@yandex.ru', '+74445556677','Санкт-Петербург, ул. Невская, 2', TRUE),
       ('Елена', 'Петрова', '1982-03-10', 'elena.petrova@gmail.com', '+77778889900', 'Казань, ул. Баумана, 3', FALSE),
       ('Игорь', 'Иванов', '1975-12-25', 'igor.ivanov@yandex.ru', '+73334445566', 'Новосибирск, ул. Красная, 4', TRUE),
       ('Наталья', 'Сидорова', '1992-07-18', 'natalia.sidorova@gmail.com', '+76667778899','Екатеринбург, ул. Маяковского, 5', FALSE);

-- Вставка данных в таблицу orders
INSERT INTO orders (description, created, closed, status, clients_comment, operators_comment, client_id, operator_id)
VALUES ('Заказ на установку интернета', '2023-10-01 10:00:00', NULL, 'SECONDARY_REVIEW', 'Нужен быстрый интернет',
        'Свяжемся с вами в течение часа', 1, 1),
       ('Заказ на ремонт телефона', '2023-10-02 11:00:00', '2023-10-02 14:00:00', 'REJECTED', 'Телефон не включается',
        'Замена аккумулятора', 2, 2),
       ('Заказ на установку антивируса', '2023-10-03 12:00:00', NULL, 'SECONDARY_REVIEW', 'Нужен антивирус для дома',
        'Установим антивирус', 3, 4),
       ('Заказ на ремонт компьютера', '2023-10-04 13:00:00', '2023-10-04 16:00:00', 'EXECUTED', 'Компьютер не работает',
        'Замена материнской платы', 4, 4),
       ('Заказ на установку программного обеспечения', '2023-10-05 14:00:00', NULL, 'SECONDARY_REVIEW',
        'Нужно установить программу для работы', 'Установим программу', 5, 1);

-- Вставка данных в таблицу work_phones
INSERT INTO work_phones (phone_number, phone_type, serial_number, operator_id)
VALUES ('+71112223344', 'Samsung', 'SN123456', 1),
       ('+74445556677', 'Apple', 'SN654321', 2),
       ('+77778889900', 'Nokia', 'SN987654', NULL),
       ('+73334445566', 'Samsung', 'SN321654', 4),
       ('+76667778899', 'Apple', 'SN654987', NULL);

-- Вставка данных в таблицу phone_usage_history
INSERT INTO phone_usage_history (usage_start, usage_end, operator_id, phone_id)
VALUES ('2023-10-01 09:00:00', NULL, 1, 1),
       ('2023-10-02 09:00:00', NULL, 2, 2),
       ('2023-10-03 09:00:00', '2023-10-03 17:00:00', 3, 3),
       ('2023-10-04 09:00:00', NULL, 4, 4),
       ('2023-10-05 09:00:00', '2023-10-05 17:00:00', 5, 5);

-- Вставка данных в таблицу tasks
INSERT INTO tasks (task_details, is_completed, list_index, operator_id)
VALUES ('Обзвон клиента Иванов', FALSE, 0, 1),
       ('Обзвон клиента Петрова', TRUE, 1, 2),
       ('Обзвон клиента Сидоров', FALSE, 2, 1),
       ('Обзвон клиента Кузнецова', TRUE, 3, 4),
       ('Обзвон клиента Смирнова', FALSE, 4, 1);