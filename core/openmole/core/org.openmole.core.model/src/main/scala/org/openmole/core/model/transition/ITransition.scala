/*
 * Copyright (C) 2010 Romain Reuillon
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.openmole.core.model.transition

import org.openmole.core.model.mole._
import org.openmole.core.model.data._
import org.openmole.core.model.tools._

trait ITransition {

  /**
   *
   * Get the starting capsule of this transition.
   *
   * @return the starting capsule of this transition
   */
  def start: ICapsule

  /**
   *
   * Get the ending capsule of this transition.
   *
   * @return the ending capsule of this transition
   */
  def end: Slot

  /**
   *
   * Get the condition under which this transition is performed.
   *
   * @return the condition under which this transition is performed
   */
  def condition: ICondition

  /**
   *
   * Get the value of the condition under which this transition is performed.
   * @param context the context in which this condition is evaluated
   *
   * @return the value of the condition under which this transition is performed
   */
  def isConditionTrue(context: Context): Boolean

  /**
   *
   * Get the filter of the variables which are filtred by this transition.
   *
   * @return filter on the names of the variables which are filtred by this transition
   */
  def filter: Filter[String]

  /**
   * Get the unfiltred user output data of the starting capsule going through
   * this transition
   *
   * @return the unfiltred output data of the staring capsule
   */
  def data(mole: IMole, sources: Sources, hooks: Hooks): Iterable[Data[_]]

  /**
   *
   * Perform the transition and submit the jobs for the following capsules in the mole.
   *
   * @param from      context generated by the previous job
   * @param ticket    ticket of the previous job
   * @param subMole   current submole
   */
  def perform(from: Context, ticket: ITicket, subMole: ISubMoleExecution)

}
