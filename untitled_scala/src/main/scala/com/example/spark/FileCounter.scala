package com.example.spark

import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.functions._
import org.apache.spark.sql.types._
import java.sql.Date

object ClientAnalyzer {
  
  // Case classes для типизированных данных
  case class Client(client_id: String, fio: String, Region: String, account_num: Int)
  case class Bill(date_field: String, Summa_USD: Int, Account_num: Int)
  
  def main(args: Array[String]): Unit = {
    // Создаем SparkSession
    val spark = SparkSession.builder()
      .appName("Client Analyzer")
      .master("local[*]")
      .config("spark.sql.adaptive.enabled", "false")
      .config("spark.serializer", "org.apache.spark.serializer.KryoSerializer")
      .getOrCreate()
    
    import spark.implicits._
    
    try {
      val sc = spark.sparkContext
      sc.setLogLevel("WARN")
      
      println("=== АНАЛИЗ КЛИЕНТОВ И СЧЕТОВ ===")
      
      // Создаем данные для клиентов согласно исходному коду
      val clientsData = Seq(
        Client("A", "Иванов", "Москва", 111),
        Client("A", "Иванов", "Москва", 222),
        Client("B", "Петров", "Иваново", 333),
        Client("C", "Сидоров", "Москва", 444)
      )
      
      // Создаем данные для счетов согласно исходному коду
      val billsData = Seq(
        Bill("2012-01-01", 15000, 111),
        Bill("2012-02-01", 10000, 111),
        Bill("2012-02-01", 5000, 222),
        Bill("2012-03-01", 30000, 333),
        Bill("2012-04-01", 20000, 444)
      )
      
      // Создаем RDD и преобразуем в DataSet
      val clientsRDD = sc.parallelize(clientsData)
      val billsRDD = sc.parallelize(billsData)
      
      // Правильное создание DataSet через SparkSession
      val clientsDS = spark.createDataset(clientsData)
      val billsDS = spark.createDataset(billsData)
      
      // Преобразуем в DataFrame
      val clientsDF = clientsDS.toDF()
      val billsDF = billsDS.toDF()
      
      // Регистрируем временные представления
      clientsDF.createOrReplaceTempView("clients")
      billsDF.createOrReplaceTempView("bills")
      
      println("Данные о клиентах:")
      clientsDF.show()
      
      println("Данные о счетах:")
      billsDF.show()
      
      // Отобрать клиентов по г. Москва с суммарными остатками по клиенту от 20 000 на последнюю дату
      val moscowClientsWithHighBalance = spark.sql("""
        WITH client_totals AS (
          SELECT 
            c.client_id,
            c.fio,
            c.Region,
            c.account_num,
            SUM(b.Summa_USD) as total_balance
          FROM clients c
          JOIN bills b ON c.account_num = b.Account_num
          WHERE c.Region = 'Москва'
          GROUP BY c.client_id, c.fio, c.Region, c.account_num
          HAVING SUM(b.Summa_USD) >= 20000
        ),
        max_date AS (
          SELECT MAX(date_field) as latest_date
          FROM bills
        )
        SELECT 
          ct.client_id,
          ct.fio,
          ct.Region,
          ct.account_num,
          ct.total_balance,
          md.latest_date
        FROM client_totals ct
        CROSS JOIN max_date md
        ORDER BY ct.total_balance DESC
      """)
      
      println("\n=== РЕЗУЛЬТАТ: Клиенты из Москвы с балансом >= 20,000 ===")
      moscowClientsWithHighBalance.show()
      
      // Альтернативное решение с использованием DataFrame API
      println("\n=== АЛЬТЕРНАТИВНОЕ РЕШЕНИЕ ЧЕРЕЗ DataFrame API ===")
      
      val joinedDF = clientsDF
        .join(billsDF, clientsDF("account_num") === billsDF("Account_num"))
        .filter($"Region" === "Москва")
        .groupBy($"client_id", $"fio", $"Region", $"account_num")
        .agg(sum($"Summa_USD").alias("total_balance"))
        .filter($"total_balance" >= 20000)
        .orderBy($"total_balance".desc)
      
      joinedDF.show()
      
      // Дополнительная статистика
      val totalMoscowClients = clientsDF.filter($"Region" === "Москва").select($"client_id").distinct().count()
      val totalHighBalanceClients = joinedDF.count()
      
      println(s"\nОбщее количество клиентов из Москвы: $totalMoscowClients")
      println(s"Клиентов из Москвы с балансом >= 20,000: $totalHighBalanceClients")
      
      // Детальная статистика по всем клиентам
      println("\n=== ДЕТАЛЬНАЯ СТАТИСТИКА ===")
      
      val allClientsStats = spark.sql("""
        SELECT 
          c.Region,
          COUNT(DISTINCT c.client_id) as client_count,
          AVG(totals.balance) as avg_balance,
          MAX(totals.balance) as max_balance,
          MIN(totals.balance) as min_balance
        FROM clients c
        LEFT JOIN (
          SELECT account_num, SUM(Summa_USD) as balance
          FROM bills
          GROUP BY account_num
        ) totals ON c.account_num = totals.account_num
        GROUP BY c.Region
        ORDER BY avg_balance DESC
      """)
      
      println("Статистика по регионам:")
      allClientsStats.show()
      
    } catch {
      case e: Exception =>
        println(s"Ошибка при выполнении: ${e.getMessage}")
        e.printStackTrace()
    } finally {
      spark.stop()
    }
  }
}
