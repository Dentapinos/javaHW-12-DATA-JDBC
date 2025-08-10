# Hi, I'm Dentapinos! 👋

[![Typing SVG](https://readme-typing-svg.herokuapp.com?color=%2336BCF7&lines=Computer+science+student)](https://git.io/typing-svg)

# Домашнее задание №12 - Реализация взаимодействия с базой данных через Spring Data Jdbc

Взять за основу [Проект на github](https://github.com/Kichmarevitmo/Spring_Data_Jdbc_Home_Work)

- Взять за основу ДЗ по ссылке с Github, но без страницы логина.
- Вместо Jetty использовать Spring Boot
- Работу с базой данных реализовать на Spring Data Jdbc
- В качестве движка шаблонов использовать Thymeleaf
- Если Thymeleaf не нравится, используйте чистый HTML и JavaScript
- Авторизацию и аутентификацию делать не надо.

## Что сделал я

Можно посмотреть презентацию [в картинках](DetailsWithImages.md)

Для выполнения домашнего задания я придумал создать web приложение "Система управления операторами и заявками".
Где я применил обращение к базе данных с помощью Spring Data Jdbc.
Я попробовал сделать запросы разными способами, которые предоставляет Spring Data Jdbc.
А именно:
- **CrudRepository**: предоставляет базовые CRUD операции (create, read, update, delete).
- **PagingAndSortingRepository**: расширяет CrudRepository и добавляет поддержку пагинации и сортировки.
  [Пример использования в проекте](org/javanamba/jhwjdbcdata/repository/ClientRepository.java)

- Именованные запросы (Named Queries): Позволяют определять SQL запросы в аннотациях репозитория.
  [Пример использования в проекте](org/javanamba/jhwjdbcdata/repository/OperatorRepository.java)

- Методы запросов (Query Methods): Spring Data JDBC автоматически генерирует SQL запросы на основе имен методов.
  [Пример использования в проекте](src/main/java/org/javanamba/jhwjdbcdata/repository/WorkPhoneRepository.java) - методы: findByPhoneNumberContainingAndActiveTrue и findBySerialNumberContainingAndActiveTrue

- JdbcTemplate: Позволяет выполнять произвольные SQL запросы.
  [Пример использования в проекте](org/javanamba/jhwjdbcdata/repository/CustomOrderRepository.java)

- NamedParameterJdbcTemplate: Аналогичен JdbcTemplate, но использует именованные параметры.
  [Пример использования в проекте](src/main/java/org/javanamba/jhwjdbcdata/repository/CustomOperatorRepository.java)

## Настройка для запуска

Для начала необходимо настроить базу данных 
[application.yaml](src/main/resources/application.yaml)

```yaml
  datasource:
    url: jdbc:mysql://localhost:3306/jdbc_db #(jdbc_db - имя вашей базы данных, указать свою)
    driver-class-name: com.mysql.cj.jdbc.Driver
    password: password #(указать свой пароль от базы данных)
    username: root #(указать свой логин от базы данных)
```

```yaml
app:
  table:
    size:
      all: 5 #(при необходимости изменить число отображаемых строк данных на странице в таблице)
```

## Дополнительные настройки для Docker

Настройки можно посмотреть в отдельном [файле](RunAppByDockerfile.md)
а так же более оптимизированный подход
[файл](RunAppByDockerfile-Compose.md)

## Ключевые компоненты

### Основные сущности:
- **Операторы (Operator)** - центральная сущность, сотрудники, работающие с заявками.
- **Заявки (Order)** - заявки, это создаваемые оператором заявки на выполнение необходимой работы с клиентом
- **Клиенты (Client)** - содержит информацию о клиентах
- **Рабочие телефоны (WorkPhone)** - средства связи операторов, для каждого можно назначить один такой телефон
- **История использования телефонов (PhoneUsageHistory)** - журнал использования телефонов, телефон назначается при поступлении оператора на работу
- **Задачи (Task)** - задачи, связанные с выполнением оператором необходимой работы

### Архитектура:
- MVC архитектура с четким разделением на слои
- Spring Data JDBC для работы с базой данных
- Flyway для управления миграциями БД
- Docker для контейнеризации
- Thymeleaf шаблонизатор для создания и обработки веб-страниц

### Современный стек технологий:
- Spring Boot 3.5.3
- Java 17+
- MySql 8.0

### Современные практики разработки
- Чистая архитектура:
    - Разделение на контроллеры, сервисы и репозитории
    - Использование DTO для передачи данных
    - Валидация входных данных

### Гибкость и масштабируемость:
- Гибкая настройка через application.yaml

### Безопасность:
- Обработка исключений через GlobalExceptionHandler
- Валидация входных данных

### Тестирование
- Базовые юнит-тесты сервисов

## Что узнал и чему научился
Я научился использовать возможности Spring Data Jdbc. Применил стандартные CRUD операции используя готовый набор методов CrudRepository.
Так же попрактиковался в написании собственных методов через именованные запросы, написание запросов по имени, и создал рандомные запросы используя JdbcTemplate и NamedParameterJdbcTemplate

## Authors

- [@Dentapinos](https://github.com/Dentapinos)
