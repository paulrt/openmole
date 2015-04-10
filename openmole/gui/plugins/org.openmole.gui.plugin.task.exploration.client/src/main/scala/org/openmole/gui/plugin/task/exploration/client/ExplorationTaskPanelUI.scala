package org.openmole.gui.plugin.task.exploration.client

/*
 * Copyright (C) 31/03/2015 // mathieu.leclaire@openmole.org
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

import org.openmole.gui.ext.dataui.PanelUI
import scalatags.JsDom.all._
import org.openmole.gui.misc.js.{Forms => bs, OMEditor}
import scalatags.JsDom.{tags ⇒ tags}
import scala.scalajs.js.annotation.JSExport
import scalatags.JsDom.all._
import scalatags.JsDom.tags

@JSExport("org.openmole.gui.plugin.task.exploration.client.ExplorationTaskPanelUI")
class ExplorationTaskPanelUI(dataUI: ExplorationTaskDataUI) extends PanelUI {

  @JSExport
  val view = {
    bs.div()(
      OMEditor.tag
    )
  }

  def save = {
    dataUI.code() = editor.code
  }

  lazy val editor = OMEditor(Seq(
    ("Compile", "Enter", () ⇒ println("Compile  !"))
  )
  )

  def getEditor = {
    editor
  }

  override def jQueryCalls = {
    Seq(() => getEditor)
  }


}