# Календарь задач

Учебное Android-приложение: задачи на конкретные дни, список, детали, редактирование и отметка о выполнении.
Все данные хранятся локально на устройстве (Room), без сети и внешних API.

## Технологии

- Kotlin, Android Studio, `minSdk 26` (Android 8.0)
- XML-разметка и Material Design
- Room (SQLite), Kotlin Coroutines и Flow
- Архитектура MVVM: Activity → ViewModel → Repository → Room (ViewModel, LiveData)

## Состояние проекта

| Этап | Тема | Статус | Материалы |
|---|---|---|---|
| 1 | Окружение и основы | готово | [отчёт](docs/report_stage1.md) |
| 2 | Список задач (RecyclerView), переходы | готово | [схема экранов](Diagrams.md) |
| 3 | Локальная база данных (Room) | готово | [отчёт и тесты](docs/report_stage3.md), [скриншоты](Screenshots.md) |
| 4 | Архитектура MVVM | готово | [схема и пояснения](docs/architecture_stage4.md) |
| 5 | Форма добавления и редактирования с выбором даты и времени | в планах | |
| 6 | Календарное представление | в планах | |
| 7 | Уведомления и напоминания | в планах | |
| 8 | Настройки, тёмная тема, поиск и сортировка | в планах | |

## Структура кода

```
app/src/main/java/ru/university/taskcalendar/
├── ui/          Activity и адаптер списка (только отображение)
├── viewmodel/   TaskViewModel (состояние экрана, LiveData)
├── data/        TaskRepository, TaskDao, TaskEntity, AppDatabase
└── model/       Task
```

## Сборка и запуск

1. Открыть проект в актуальной стабильной версии Android Studio и дождаться синхронизации Gradle.
2. Создать эмулятор (API 26 и выше) или подключить устройство.
3. Запустить конфигурацию `app` (кнопка Run).

## Тесты

- Unit-тест: `![img_1.png](img_1.png)`
- Инструментальный тест Room : `![img.png](img.png)`

