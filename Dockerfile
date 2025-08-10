# Используем официальный образ OpenJDK
FROM amazoncorretto:17.0.0

# Устанавливаем рабочую директорию
WORKDIR /app

# Копируем файл с зависимостями и файл с приложением
# Имя толстого jar-ника будет сгенерировано такое, как мы указали в плагине <finalName>jdbc-app-fat</finalName>
COPY target/jdbc-app-fat.jar jdbc-app.jar

# Запускаем приложение
ENTRYPOINT ["java", "-jar", "jdbc-app.jar"]