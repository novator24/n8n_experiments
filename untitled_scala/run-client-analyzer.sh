#!/bin/bash

# Скрипт для запуска анализатора клиентов

echo "=== Запуск анализатора клиентов ==="

# Проверяем наличие Java
if ! command -v java &> /dev/null; then
    echo "Ошибка: Java не установлена"
    exit 1
fi

# Проверяем наличие sbt
if ! command -v sbt &> /dev/null; then
    echo "Ошибка: sbt не установлен"
    exit 1
fi

echo "Java версия:"
java -version

echo "sbt версия:"
sbt --version

echo "Компиляция проекта..."
sbt compile

if [ $? -ne 0 ]; then
    echo "Ошибка компиляции!"
    exit 1
fi

echo "Запуск анализатора клиентов..."
sbt "runMain com.example.spark.ClientAnalyzer"

echo "=== Завершено ==="
