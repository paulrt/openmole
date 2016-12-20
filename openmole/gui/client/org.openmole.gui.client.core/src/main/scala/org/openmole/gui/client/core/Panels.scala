package org.openmole.gui.client.core

import org.openmole.gui.client.core.files.TreeNodePanel
import scalatags.JsDom.{ tags ⇒ tags }

/*
 * Copyright (C) 24/07/15 // mathieu.leclaire@openmole.org
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

package object panels {
  val modelWizardPanel = new ModelWizardPanel
  val marketPanel = new MarketPanel
  val pluginPanel = new PluginPanel
  val executionPanel = new ExecutionPanel
  val docPanel = new GUIDocPanel
  val stackPanel = new TextPanel("Error stack")
  val treeNodePanel = TreeNodePanel()
}
