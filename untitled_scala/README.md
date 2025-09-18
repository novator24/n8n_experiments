# Spark File Counter

Минимальное Scala приложение для Apache Spark, которое выполняет Map-Reduce подсчет количества файлов в указанном каталоге с группировкой по расширениям файлов.

## Возможности

- Рекурсивный обход каталогов
- Подсчет файлов по расширениям с использованием Map-Reduce
- Вывод статистики через Spark SQL
- Локальный запуск без необходимости кластера Spark

## Требования

- Java 8 или выше
- Scala 2.12.x
- sbt (Scala Build Tool)
- Apache Hadoop (для Windows)

## Установка и настройка на Windows

### 1. Установка Java

1. Скачайте и установите Java 8 или выше с [Oracle](https://www.oracle.com/java/technologies/downloads/) или [OpenJDK](https://openjdk.org/)
2. Добавьте `JAVA_HOME` в переменные среды:
   ```
   JAVA_HOME=C:\Program Files\Java\jdk-11.0.x
   ```
3. Добавьте `%JAVA_HOME%\bin` в переменную `PATH`

### 2. Установка Scala

1. Скачайте Scala 2.12.18 с [официального сайта](https://www.scala-lang.org/download/)
2. Распакуйте в `C:\scala`
3. Добавьте в переменные среды:
   ```
   SCALA_HOME=C:\scala
   PATH=%PATH%;%SCALA_HOME%\bin
   ```

### 3. Установка sbt

1. Скачайте sbt с [официального сайта](https://www.scala-sbt.org/download.html)
2. Установите используя Windows Installer
3. Проверьте установку:
   ```cmd
   sbt --version
   ```

### 4. Установка Hadoop для Windows

#### Скачивание Hadoop

1. Перейдите на [Apache Hadoop Releases](https://archive.apache.org/dist/hadoop/common/)
2. Скачайте `hadoop-3.3.6.tar.gz` (или последнюю стабильную версию 3.x)
3. Распакуйте архив в `C:\hadoop` (используйте 7-Zip для .tar.gz файлов)

#### Скачивание winutils

1. Перейдите на [GitHub winutils](https://github.com/steveloughran/winutils)
2. Скачайте `winutils.exe` для вашей версии Hadoop (например, hadoop-3.3.1/bin/winutils.exe)
3. Поместите `winutils.exe` в `C:\hadoop\bin\`

#### Настройка переменных среды для Hadoop

Добавьте следующие переменные среды:

```cmd
HADOOP_HOME=C:\hadoop
HADOOP_CONF_DIR=%HADOOP_HOME%\etc\hadoop
PATH=%PATH%;%HADOOP_HOME%\bin;%HADOOP_HOME%\sbin
```

#### Создание каталога для данных Hadoop

```cmd
mkdir C:\hadoop\data
mkdir C:\hadoop\data\dfs
mkdir C:\hadoop\data\dfs\namenode
mkdir C:\hadoop\data\dfs\datanode
mkdir C:\tmp\hive
```

#### Настройка прав доступа

Откройте командную строку от имени администратора и выполните:

```cmd
cd C:\hadoop\bin
winutils.exe chmod -R 777 C:\tmp\hive
winutils.exe chmod -R 777 C:\hadoop\data
```

### 5. Проверка установки

Откройте новую командную строку и проверьте:

```cmd
java -version
scala -version
sbt --version
hadoop version
winutils.exe
```

## Сборка проекта

1. Клонируйте или скачайте проект
2. Откройте командную строку в корневой папке проекта
3. Выполните сборку:

```cmd
sbt clean compile
```

4. Создайте JAR файл с зависимостями:

```cmd
sbt assembly
```

Результирующий JAR файл будет создан в `target\scala-2.12\SparkFileCounter-assembly-1.0.jar`

## Запуск приложения

### Способ 1: Через sbt

```cmd
sbt "run C:\path\to\your\directory"
```

### Способ 2: Через JAR файл

```cmd
java -cp target\scala-2.12\SparkFileCounter-assembly-1.0.jar com.example.spark.FileCounter C:\path\to\your\directory
```

### Способ 3: Через spark-submit (если установлен Spark)

```cmd
spark-submit --class com.example.spark.FileCounter --master local[*] target\scala-2.12\SparkFileCounter-assembly-1.0.jar C:\path\to\your\directory
```

## Примеры использования

### Подсчет файлов в папке Windows

```cmd
sbt "run C:\Windows\System32"
```

### Подсчет файлов в папке пользователя

```cmd
sbt "run C:\Users\%USERNAME%\Documents"
```

### Подсчет файлов в корне диска C:

```cmd
sbt "run C:\"
```

## Пример вывода

```
Начинаем подсчет файлов в каталоге: C:\Users\User\Documents
Найдено файлов для обработки: 1247

=== РЕЗУЛЬТАТЫ ПОДСЧЕТА ФАЙЛОВ ===
Общее количество файлов: 1247

Распределение по расширениям:
.txt: 423 файл(ов)
.docx: 156 файл(ов)
.pdf: 98 файл(ов)
.jpg: 87 файл(ов)
без_расширения: 45 файл(ов)
.png: 32 файл(ов)

=== ДОПОЛНИТЕЛЬНАЯ СТАТИСТИКА ===
Топ-5 расширений файлов:
+-----------+-----+
|  extension|count|
+-----------+-----+
|       .txt|  423|
|      .docx|  156|
|       .pdf|   98|
|       .jpg|   87|
|без_расширения|   45|
+-----------+-----+
```

## Возможные проблемы и решения

### Ошибка "HADOOP_HOME not set"
Убедитесь, что переменная `HADOOP_HOME` установлена правильно и перезапустите командную строку.

### Ошибка "winutils.exe not found"
1. Убедитесь, что `winutils.exe` находится в `%HADOOP_HOME%\bin\`
2. Проверьте, что путь `%HADOOP_HOME%\bin` добавлен в `PATH`

### Ошибка прав доступа
Запустите от имени администратора:
```cmd
winutils.exe chmod -R 777 C:\tmp
```

### OutOfMemoryError
Увеличьте память для JVM:
```cmd
set JAVA_OPTS=-Xmx4g -Xms2g
sbt "run C:\path\to\directory"
```

## Структура проекта

```
SparkFileCounter/
├── build.sbt                          # Конфигурация сборки
├── project/
│   └── plugins.sbt                    # Плагины sbt
├── src/
│   └── main/
│       └── scala/
│           └── com/
│               └── example/
│                   └── spark/
│                       └── FileCounter.scala  # Основной код
├── test-data/                         # Тестовые данные
│   ├── docs/
│   ├── images/
│   └── scripts/
├── run-test.sh                        # Скрипт для тестирования (Linux/WSL)
└── README.md                          # Документация
```

## Быстрое тестирование

Для быстрого тестирования на Linux/WSL выполните:

```bash
./run-test.sh
```

Этот скрипт:
1. Проверит наличие Java и sbt
2. Скомпилирует проект
3. Запустит приложение на тестовых данных в каталоге `test-data/`