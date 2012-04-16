/*
 * Copyright (C) 2011 reuillon
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

package org.openmole.plugin.grouping.batch

import org.openmole.core.model.data.IContext
import org.openmole.core.model.mole.IGrouping
import org.openmole.core.implementation.mole.MoleJobGroup

/**
 * Group mole jobs given a fixed number of batch.
 * 
 * @param numberOfBatch total number of batch
 */
class NumberOfBatchGrouping(numberOfBatch: Int) extends IGrouping {

  var currentBatchNumber = 0

  override def apply(context: IContext) = {
    val jobCategory = new MoleJobGroup(currentBatchNumber)
    currentBatchNumber = (currentBatchNumber + 1) % numberOfBatch
    jobCategory
  }

}
