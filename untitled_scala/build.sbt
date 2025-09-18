name := "SparkFileCounter"

version := "1.0"

scalaVersion := "2.12.18"

libraryDependencies ++= Seq(
  "org.apache.spark" %% "spark-core" % "3.5.0",
  "org.apache.spark" %% "spark-sql" % "3.5.0",
  "org.apache.hadoop" % "hadoop-client" % "3.3.6"
)

// Исключаем конфликтующие зависимости
libraryDependencies := libraryDependencies.value.map(_.exclude("org.slf4j", "slf4j-log4j12"))

// Настройки для сборки JAR файла
assembly / assemblyMergeStrategy := {
  case PathList("META-INF", xs @ _*) => MergeStrategy.discard
  case x => MergeStrategy.first
}
