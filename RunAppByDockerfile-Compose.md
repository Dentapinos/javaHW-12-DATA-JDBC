# Запуск проекта в контейнерах Docker

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

### 3. Создание контейнеров с Docker Compose

Создайте docker-compose.yml в корне проекта

```yaml
services:
  mysql:
    image: mysql:8.2.0
    container_name: mysql-container
    environment:
      MYSQL_DATABASE: jdbc-app
      MYSQL_ROOT_PASSWORD: 123456
    ports:
      - "3308:3306"
    volumes:
      - mysql-data:/var/lib/mysql

  jdbc-app:
    image: jdbc-application
    container_name: jdbc-app-container
    environment:
      SPRING_DATASOURCE_URL: jdbc:mysql://mysql:3308/jdbc-app
      SPRING_DATASOURCE_USERNAME: root
      SPRING_DATASOURCE_PASSWORD: 123456
    ports:
      - "8080:8080"
    depends_on:
      - mysql

volumes:
  mysql-data:
```

Запустите контейнеры с помощью команды:

```shell
docker-compose up -d
```

### Все готово! Проект запущен