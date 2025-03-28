1. Название: Task management system
2. Описание: приложение для создания задач и отслеживания их готовности с возможностью оставлять комментарии.
3. Запуск: база данных реализована в Postgresql. Чтобы запустить, в терминале пишем команду "docker-compose up". Добавить ключ -d для запуска в фоне. В файле миграции добавляется пользователь по умолчанию "admin". В main классе реализован интерфейс CommandLineRunner чтобы вызвать функцию в которой у пользователя "admin" вставляется хэшированный пароль. После успешного запуска можно перейти на страницу http://localhost:8080/swagger-ui.html, пройти авторизацию на стандартной странице spring security (логин "admin@example.com", пароль "admin"). Стандартный токен spring security отключен. Используется jwt токен, который сохраняется в cookies. Токен действует 1 час. Изменить время действия токена можно в файле application.yaml в переменной jwtExpirationInMs.
4. Тестирование: протестировать можно в интерфейсе swagger-ui. Методы контроллера имеют интуитивно понятные названия для понимания. Также добавлены юнит тесты для классов TaskController.java и TaskService.java.

5. Начиная с ветки 16 добавлено развертывание в kubernetes. Должен быть установлен kind, kubectl. 
6. Пересобираем проект "./gradlew clean build -x test". Создаем кластер "kind create cluster". 
7. Собираем докер образ "docker build -t my-app:v1 ."  . 
8. Применяем конфиг файлы к8с командой "kubectl apply -f k8s/app-deployment.yaml" "kubectl apply -f k8s/ingress.yaml" "kubectl apply -f k8s/postgres-deployment.yaml". 
9. Пишем команду "kind load docker-image my-app:v1". 
10. Дальше вводим команду "kubectl port-forward svc/my-app-service 8080:80". С помощью этого мы можем тестировать дальше на localhost:8080.
11. Для обновления данных проекта, при добавлении новых эндпоинтов и т.п. пишем следующее:
   "./gradlew clean build -x test" //пересобрали без теста.
   "docker build -t my-app:v2 ." //новая версия должна быть.
   "kind load docker-image my-app:v2" // также с новой версией.
   изменяем версию image в app-deployment.yaml (image: my-app:v2)
   при необходимости снова делаем проброс портов
   "kubectl port-forward svc/my-app-service 8080:80"