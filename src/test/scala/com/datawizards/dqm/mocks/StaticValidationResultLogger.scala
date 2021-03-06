package com.datawizards.dqm.mocks

import com.datawizards.dqm.logger.ValidationResultLogger
import com.datawizards.dqm.result._

import scala.collection.mutable.ListBuffer

class StaticValidationResultLogger extends ValidationResultLogger {
  val results = new ListBuffer[ValidationResult]

  override def log(result: ValidationResult): Unit =
    results += result

  override protected def logInvalidRecords(invalidRecords: Seq[InvalidRecord]): Unit = { /* do nothing */ }

  override protected def logTableStatistics(tableStatistics: TableStatistics): Unit = { /* do nothing */ }

  override protected def logColumnStatistics(columnsStatistics: Seq[ColumnStatistics]): Unit = { /* do nothing */ }

  override protected def logGroupByStatistics(groupByStatisticsList: Seq[GroupByStatistics]): Unit = { /* do nothing */ }

  override protected def logInvalidGroups(invalidGroups: Seq[InvalidGroup]): Unit = { /* do nothing */ }

  override protected def logInvalidTableTrends(invalidTableTrends: Seq[InvalidTableTrend]): Unit = { /* do nothing */ }
}
