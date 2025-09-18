#!/bin/bash

# Скрипт для быстрого тестирования приложения на Linux/WSL

echo "=== Spark File Counter - Test Runner ==="
echo

# Проверяем наличие sbt
if ! command -v sbt &> /dev/null; then
    echo "Ошибка: sbt не установлен или не найден в PATH"
    echo "Установите sbt для продолжения"
    exit 1
fi

# Проверяем наличие Java
if ! command -v java &> /dev/null; then
    echo "Ошибка: Java не установлена или не найдена в PATH"
    echo "Установите Java 8+ для продолжения"
    exit 1
fi

echo "Java версия:"
java -version
echo

echo "sbt версия:"
sbt --version
echo

# Компилируем проект
echo "=== Компиляция проекта ==="
sbt clean compile

if [ $? -ne 0 ]; then
    echo "Ошибка компиляции!"
    exit 1
fi

echo
echo "=== Запуск тестирования на тестовых данных ==="
echo "Каталог для тестирования: $(pwd)/test-data"
echo

# Запускаем приложение на тестовых данных
sbt "run $(pwd)/test-data"

echo
echo "=== Тестирование завершено ==="
