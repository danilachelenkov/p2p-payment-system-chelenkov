# 💳 P2P Payment System

Учебный проект — система peer-to-peer платежей на Spring Boot с Redis кэшированием.

## 🚀 Технологии

- **Backend:** Java 21, Spring Boot 3.2
- **Database:** PostgreSQL 15
- **Cache:** Redis 7
- **Build Tool:** Gradle
- **Containerization:** Docker, Docker Compose

## 📦 Запуск инфраструктуры

```bash
# Запуск PostgreSQL + Redis
docker compose up -d

# Проверка статуса
docker compose ps

# Остановка
docker compose down
```

## 📁 Структура проекта

```
p2p-payment-system-chelenkov/
├── payment-service/   # Spring Boot микросервис
├── docs/              # Документация
└── docker-compose.yml # PostgreSQL + Redis
```


