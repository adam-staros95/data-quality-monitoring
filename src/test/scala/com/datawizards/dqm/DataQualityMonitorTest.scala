package com.datawizards.dqm

import com.datawizards.dqm.alert.DevNullAlertSender
import com.datawizards.dqm.configuration.location.StaticTableLocation
import com.datawizards.dqm.configuration.{DataQualityMonitoringConfiguration, StaticConfigurationLoader, TableConfiguration}
import com.datawizards.dqm.logger.StaticValidationResultLogger
import com.datawizards.dqm.result.{InvalidRecord, ValidationResult}
import com.datawizards.dqm.rules.{FieldRules, NotNullRule, TableRules}
import org.apache.spark.sql.{Row, SparkSession}
import org.apache.spark.sql.types.{StringType, StructField, StructType}
import org.scalatest.{FunSuite, Matchers}

class DataQualityMonitorTest extends FunSuite with Matchers {
  lazy val spark: SparkSession = SparkSession.builder().master("local").getOrCreate()

  test("DataQualityMonitorTest - simple") {
    val schema = StructType(Seq(
      StructField("f1", StringType),
      StructField("f2", StringType),
      StructField("f3", StringType)
    ))
    val input = spark.createDataFrame(spark.sparkContext.parallelize(Seq(
      Row("r1.f1", "r1.f2", null),
      Row("r2.f1", null, "r2.f3"),
      Row(null, "r3.f2", "r3.f3")
    )), schema)

    val configurationLoader = new StaticConfigurationLoader(DataQualityMonitoringConfiguration(
      tablesConfiguration = Seq(
        TableConfiguration(
          location = StaticTableLocation(input),
          rules = TableRules(Seq(
            FieldRules(
              field = "f2",
              rules = Seq(
                new NotNullRule()
              )
            )
          )))
        )
      )
    )
    val logger = new StaticValidationResultLogger()
    val alertSender = DevNullAlertSender
    DataQualityMonitor.run(configurationLoader, logger, alertSender)

    logger.results.head should equal(ValidationResult(
      invalidRecords = Seq(
        InvalidRecord(
          row = """{"f1" : "r2.f1", "f2" : "null", "f3" : "r2.f3"}""",
          value = "null",
          rule = "NOT NULL"
        )
      )
    ))
  }

}
