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

package org.openmole.commons.aspect.eventdispatcher.internal

import scala.collection.mutable.HashMap
import scala.collection.mutable.SynchronizedMap
import scala.collection.mutable.WeakHashMap

class ObjectListenerMap[L] {
    
  val listnerTypeMap = new WeakHashMap[AnyRef, HashMap[String, SortedListners[L]]]
  
  private def getOrCreateListners(obj: AnyRef, event: String): SortedListners[L] = {
    listnerTypeMap.getOrElseUpdate(obj, new HashMap[String, SortedListners[L]]).getOrElseUpdate(event, new SortedListners[L])
  }

  def get(obj: AnyRef, event: String): Iterable[L] = synchronized {
    listnerTypeMap.getOrElse(obj, HashMap.empty).getOrElse(event, Iterable.empty)
  }

  def register(obj: AnyRef, priority: Int, listner: L, event: String) = synchronized {
    getOrCreateListners(obj, event).register(priority, listner)
  }
  
}
