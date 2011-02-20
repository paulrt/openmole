/*
 * Copyright (C) 2010 reuillon
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.openmole.plugin.environment.glite.internal

import java.util.logging.Level
import java.util.logging.Logger

import org.openmole.core.batch.environment.BatchExecutionJob
import org.openmole.core.model.mole.IMoleExecution
import org.openmole.core.implementation.execution.JobRegistry
import org.openmole.core.implementation.execution.StatisticKey
import org.openmole.core.implementation.execution.StatisticSample
import org.openmole.core.model.execution.ExecutionState._
import org.openmole.core.model.execution.IStatisticKey
import org.openmole.core.model.job.IJob
import org.openmole.commons.tools.cache.AssociativeCache
import org.openmole.misc.updater.IUpdatableWithVariableDelay
import org.openmole.plugin.environment.glite.GliteEnvironment._
import org.openmole.misc.workspace.Workspace
import org.openmole.plugin.environment.glite.GliteEnvironment
import scala.collection.mutable.HashMap
import scala.collection.immutable.TreeSet
import scala.collection.mutable.HashSet
import scala.collection.mutable.Set
import scala.collection.mutable.MultiMap
import scala.ref.WeakReference
import scala.math._

class OverSubmissionAgent(environment: WeakReference[GliteEnvironment]) extends IUpdatableWithVariableDelay {

  def this (environment: GliteEnvironment) = this(new WeakReference(environment))
  
  override def delay = Workspace.preferenceAsDurationInMs(OverSubmissionInterval)

  override def update: Boolean = {
    Logger.getLogger(classOf[OverSubmissionAgent].getName).log(Level.FINE,"oversubmission started")
 
    val env = environment.get match {
      case None => return false
      case Some(env) => env
    }

    val registry = env.jobRegistry
    registry.synchronized {

      val toProceed = registry.allExecutionJobs.groupBy( ejob => (JobRegistry(ejob.job).orNull, new StatisticKey(ejob.job))).filter( elt => {elt._1 != null && elt._2.size > 0})
      //Logger.getLogger(classOf[OverSubmissionAgent].getName).log(Level.FINE,"size " + toProceed.size + " all " + registry.allExecutionJobs)
    
      toProceed.foreach {
        case(k, jobs) => {
            val now = System.currentTimeMillis
            val stillRunning = jobs.filter(_.state == RUNNING)
            
            Logger.getLogger(classOf[OverSubmissionAgent].getName).log(Level.FINE,"still running " + stillRunning.size )
  
            val stillRunningSamples = jobs.view.map{_.batchJob}.filter(bj => bj != null && bj.state == RUNNING).map{j => new StatisticSample(j.timeStemp(SUBMITTED), j.timeStemp(RUNNING), now)}
            val samples = (env.statistic(k._1, k._2) ++ stillRunningSamples).toArray
 
            Logger.getLogger(classOf[OverSubmissionAgent].getName).log(Level.FINE,"still running samples " + stillRunningSamples.size  + " samples size " + samples.size)

            var nbRessub = if(!samples.isEmpty) {
              val windowSize = (jobs.size * Workspace.preferenceAsDouble(OverSubmissionSamplingWindowFactor)).toInt
              val windowStart = if(samples.size > windowSize) samples.size - windowSize else 0
            
              val nbSamples = Workspace.preferenceAsInt(OverSubmissionNbSampling)
              val interval = (samples.last.done - samples(windowStart).submitted) / (nbSamples) 
            
              Logger.getLogger(classOf[OverSubmissionAgent].getName).log(Level.FINE,"interval " + interval)
            
              val maxNbRunning = (for(date <- (samples(windowStart).submitted) until(samples.last.done, interval)) yield samples.count( s => s.running <= date && s.done >= date)).max 
            
              val minOversub = Workspace.preferenceAsInt(OverSubmissionMinNumberOfJob)
              if(maxNbRunning < minOversub) minOversub - jobs.size else maxNbRunning - stillRunning.size
            } else Workspace.preferenceAsInt(OverSubmissionMinNumberOfJob) - jobs.size
            
            Logger.getLogger(classOf[OverSubmissionAgent].getName).log(Level.FINE,"NbRessub " + nbRessub)
            val numberOfSimultaneousExecutionForAJobWhenUnderMinJob = Workspace.preferenceAsInt(OverSubmissionNumberOfJobUnderMin)
            if (nbRessub > 0) {
              // Resubmit nbRessub jobs in a fair manner
              val order = new HashMap[Int, Set[IJob]] with MultiMap[Int, IJob]
              var keys = new TreeSet[Int]

              for (job <- jobs.map{_.job}) {
                val nb = registry.nbExecutionJobs(job)
                if (nb < numberOfSimultaneousExecutionForAJobWhenUnderMinJob) {
                  val set = order.getOrElseUpdate(nb, new HashSet[IJob])
                  set += job
                  keys += nb
                }
              }

              if (!keys.isEmpty) {
                while (nbRessub > 0 && keys.head < numberOfSimultaneousExecutionForAJobWhenUnderMinJob) {
                  var key = keys.head
                  val jobs = order(keys.head)
                  val it = jobs.iterator
                  val job = it.next

                  //Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).log(Level.FINE, "Resubmit : running " + key + " nbRessub " + nbRessub);

                  try {
                    env.submit(job)
                  } catch {
                    case e => Logger.getLogger(classOf[OverSubmissionAgent].getName).log(Level.WARNING, "Submission of job failed, oversubmission failed.", e);
                  }

                  jobs -= job
                  if (jobs.isEmpty) {
                    order -= key
                    keys -= key
                  }

                  key += 1
                  order.getOrElseUpdate(key, new HashSet[IJob]) += job
                  keys += key
                  nbRessub -= 1
                }
              }
            }
          }
      }
      
    }
 
    true
  }
   
   

}
