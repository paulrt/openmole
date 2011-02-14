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

import scala.collection.immutable.SortedMap
import scala.collection.immutable.TreeMap
import scala.collection.mutable.ListBuffer

class SortedListners[T] extends Iterable[T] {
  
  var listners: SortedMap[Int, ListBuffer[T]] = TreeMap.empty[Int, ListBuffer[T]](new Ordering[Int] {
      def compare(a: Int, b: Int) = (a - b)
    })
 
  def register(priority: Int, listner: T) = synchronized {
    listners.get(priority) match {
      case Some(listnersBuf) =>  listnersBuf += listner
      case None => listners += priority -> ListBuffer(listner)
    }
  }

  override def iterator: Iterator[T] = synchronized {  
    new Iterator[T] {
      val it = listners.valuesIterator
      var curSetIt = if(it.hasNext) it.next.iterator else Iterator.empty
      
      override def hasNext = {it.hasNext || curSetIt.hasNext}
        
      override def next = {
        if(!curSetIt.hasNext) curSetIt = it.next.iterator
        curSetIt.next
      }
    }
  }
  
}
