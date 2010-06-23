/*
 *  Copyright (C) 2010 Romain Reuillon <romain.reuillon at openmole.org>
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.openmole.plugin.task.datasetdistribution

import java.io.BufferedOutputStream
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import java.util.Collection


import org.jfree.chart.JFreeChart
import org.jfree.chart.plot.PlotOrientation
import org.jfree.data.statistics.HistogramDataset
import org.openmole.core.implementation.task.Task
import org.openmole.core.implementation.tools.VariableExpansion
import org.openmole.core.model.data.IPrototype
import org.openmole.core.model.execution.IProgress
import org.openmole.core.model.job.IContext
import org.openmole.commons.exception.InternalProcessingError
import org.openmole.commons.exception.UserBadDataError
import scala.collection.mutable.ListBuffer

import org.jfree.chart.ChartUtilities._
import org.jfree.chart.ChartFactory._
import scala.collection.JavaConversions._

import org.openmole.core.implementation.tools.VariableExpansion._

class DatasetDistributionTask (name: String, outputDirectoryPath: String, nbCategories: String, chartTitle: String, xLegend: String, yLegend: String, imageWidth: Int, imageHeight: Int) extends Task(name) {

  object Extension { 
    def image = ".png"
  }

  private val charts = new ListBuffer[(IPrototype[Collection[Number]], String)]

  def addChart(prototype: IPrototype[Collection[Number]]) {
    addChart(prototype, prototype.getName() + Extension.image);
  }

  def addChart(prototype: IPrototype[Collection[Number]], fileName: String) {
    charts += ((prototype, fileName))
    addInput(prototype)
  }

  private def createChart(dataset: HistogramDataset, context: IContext): JFreeChart = {
    val chart = createHistogram(expandData(context, chartTitle),expandData(context, xLegend), expandData(context, yLegend), dataset, PlotOrientation.VERTICAL, false, false, false)
    chart setAntiAlias(true)
    chart
  }

  override def process(context: IContext, progress: IProgress) = {
    try {
      charts foreach ( chart => {
        val data = context getLocalValue(chart._1)
        val array = new Array[Double](data.size)
        var i = 0
        data foreach ( v => {
          array(i) = v.doubleValue
          i += 1
        } )

        val dataset = new HistogramDataset()
        dataset addSeries("", array, expandIntegerData(context, nbCategories))

        val jfchart = createChart(dataset, context)

        val os = new BufferedOutputStream(new FileOutputStream(expandData(context, outputDirectoryPath + "/" + chart._2)));
        try {
          writeChartAsPNG(os,jfchart,imageWidth,imageHeight)
        } finally {
          os.close()
        }
      } )

    } catch {
      case ex: FileNotFoundException => throw new UserBadDataError(ex)
      case ex: IOException => throw new InternalProcessingError(ex)
    }
  }
  
}
