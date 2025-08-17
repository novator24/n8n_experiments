package org.example;
//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Main {

    public static void main(String... args) {
        SpringApplication.run(MainApplication.class, args);
    }
    /*
    Задание реализовать Rest Api для управления списком задач
Необходимо разработать RESTfull сервис для управления списком задач.
Задача - id, title, description, status

Операции - Создание задачи,
 Получение всех задач, Получение задачи по id, Обновление задач, Удаление задач
     */
}