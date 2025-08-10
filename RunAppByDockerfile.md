# Создание контейнера и подключение проекта к базе данных на Docker

## Создание контейнера MySQL в Docker

### 1. Запуск контейнера MySQL

Для создания и запуска контейнера MySQL выполните следующую команду:

```shell
docker run --name mysql-container -e MYSQL_ROOT_PASSWORD=123456 -e MYSQL_DATABASE=jdbc-app -p 3307:3306 -d mysql:8.2.0
```

<b style="color:green">--name mysql-container</b>: Имя контейнера.

<b style="color:green">-e MYSQL_ROOT_PASSWORD=123456</b>: Устанавливает пароль для пользователя root.

<b style="color:green">-e MYSQL_DATABASE=jdbc_db</b>: Создает базу данных с именем jdbc_db.

<b style="color:green">-p 3307:3306</b>: Пробрасывает порт 3306 контейнера на порт 3307 хоста(внешний - тот к которому будет происходить подключение из приложения).

<b style="color:green">-d</b>: Запускает контейнер в фоновом режиме.

<b style="color:green">mysql:8.2.0</b>: Используемый образ MySQL.

### 2. Проверка контейнера

Для проверки работы контейнера и подключения к нему выполните следующую команду:

```shell
docker exec -it mysql-container bash
```

<b style="color:green">-it</b>: Опции для интерактивного режима и подключения к стандартному вводу/выводу.

<b style="color:green">mysql-container</b>: Имя контейнера.

<b style="color:green">bash</b>: Команда для запуска оболочки bash внутри контейнера.



## Настройка проекта

### 1. Добавление плагина в pom.xml

В файл pom.xml вашего проекта добавьте следующий плагин:

Для создания метаинформации, точки старта приложения
и создания толстого jar-ника(jar-файл со всеми зависимостями)
```xml
    <plugins>
        <plugin>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-maven-plugin</artifactId>
            <version>3.2.0</version>
            <executions>
                <execution>
                    <goals>
                        <goal>repackage</goal>
                    </goals>
                </execution>
            </executions>
            <configuration>
                <classifier>fat</classifier>
            </configuration>
        </plugin>
    .....
    </plugins>
```
Для указания имени приложения добавьте поле
```xml
<build>
  <finalName>jdbc-app</finalName>
    <plugins>
     ........   
    </plugins>
</build>
```

Этот плагин необходим для создания исполняемого JAR-файла с правильным манифестом.


## Создание Dockerfile

Создайте файл Dockerfile в корневой директории вашего проекта с следующим содержимым:

```dockerfile
# Используем официальный образ OpenJDK
FROM amazoncorretto:17.0.0

# Устанавливаем рабочую директорию
WORKDIR /app

# Копируем файл с зависимостями и файл с приложением
# Имя толстого jar-ника будет сгенерировано такое, как мы указали в плагине <finalName>jdbc-app-fat</finalName>
COPY target/jdbc-app-fat.jar jdbc-app.jar

# Запускаем приложение
ENTRYPOINT ["java", "-jar", "jdbc-app.jar"]
```

<b style="color:green">FROM amazoncorretto:17.0.0</b>: Используется образ Amazon Corretto версии 17.0.0.

<b style="color:green">WORKDIR /app</b>: Устанавливает рабочую директорию внутри контейнера.

<b style="color:green">COPY target/jdbc-app-fat.jar jdbc-app.jar</b>: Копирует JAR-файл вашего приложения в контейнер.

<b style="color:green">ENTRYPOINT ["java", "-jar", "jdbc-app.jar"]</b>: Устанавливает команду для запуска вашего приложения.


## Сборка и запуск Docker-образа


### 1. Собираем JAR-файл

Собираем Maven-проект

```shell
mvn clean package
```

Очистит старую сборку и создаст новый толстый и тонкий jar-ник

### 1. Сборка Docker-образа

Для сборки Docker-образа выполните следующую команду в корневой директории вашего проекта(там где находится Dockerfile):

```shell
docker build -t jdbc-application .
```
<b style="color:green">-t jdbc-app</b>:  Устанавливает тег для образа.

<b style="color:green">. (точка)</b>: Указывает на текущую директорию, где находится Dockerfile.

<i style="color:red">Важно</i> Если возникает такая ошибка:

```error
ERROR: error during connect: Head "http://%2F%2F.%2Fpipe%2FdockerDesktopLinuxEngine/_ping": open //./pipe/dockerDesktopLinuxEngine: The system cannot find the file specified.
```
Возможно не запущен Docker

### 2. Проверка созданного образа
Вы можете проверить, что образ успешно создан, с помощью команды:

```shell
docker images
```

В списке должен появится образ под названием jdbc-application

### 3. Создание и запуск Docker-контейнера на основе image приложения 

Для запуска контейнера на основе собранного образа выполните следующую команду:

```shell
docker run --name jdbc-app-container --link mysql-container:mysql -e SPRING_DATASOURCE_URL=jdbc:mysql://mysql:3306/jdbc-app -e SPRING_DATASOURCE_USERNAME=root -e SPRING_DATASOURCE_PASSWORD=123456 -p 8080:8080 -d jdbc-application
```
<b style="color:green">--name jdbc-app-container</b>: Имя контейнера.

<b style="color:green">--link mysql-container:mysql</b>: Связывает контейнер jdbc-app-container с контейнером mysql-container.
- mysql-container - имя контейнера MySQL.
- mysql - псевдоним, который будет использоваться для доступа к контейнеру MySQL из вашего приложения.

<b style="color:green">-e SPRING_DATASOURCE_URL=jdbc:mysql://mysql:3306/jdbc-app</b>: Задает URL для подключения к базе данных MySQL.
- mysql - псевдоним, заданный в --link.
- 3306 - порт MySQL внутри контейнера.
- jdbc-app - имя базы данных.

<b style="color:green">-e SPRING_DATASOURCE_USERNAME=root</b>: Задает имя пользователя для подключения к базе данных.

<b style="color:green">-e SPRING_DATASOURCE_PASSWORD=123456</b>:

<b style="color:green">-p 8080:8080</b>: Пробрасывает порт 8080 контейнера на порт 8080 хоста.

<b style="color:green">-d</b>: Запускает контейнер в фоновом режиме.

<b style="color:green">jdbc-application</b>: Используемый образ приложения.