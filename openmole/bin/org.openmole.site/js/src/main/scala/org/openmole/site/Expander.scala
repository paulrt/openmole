package org.openmole.site

import scaladget.api.{ BootstrapTags ⇒ bs }
import scalatags.{ JsDom ⇒ tags }

import tags._
import bs._

/*
 * Copyright (C) 26/06/17 // mathieu.leclaire@openmole.org
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
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

object Expander {
  def apply() = {
    val collapseNode = org.scalajs.dom.window.document.getElementById(shared.moreCollapse)

    if (collapseNode != null) {
      val parent = collapseNode.parentNode
      val content = collapseNode.textContent
      val button = bs.button("More")

      parent.removeChild(collapseNode)
      parent.appendChild(button.expandOnclick(tags.all.div(RawFrag(content))))
    }
  }
}