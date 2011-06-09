/*
 * Copyright (C) 2011 <mathieu.leclaire at openmole.org>
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
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.openmole.ide.core.commons

import java.awt.Color
import java.awt.Toolkit
import java.awt.datatransfer.DataFlavor
import org.openmole.ide.core.properties._
import org.openmole.ide.core.palette.DataProxyUI

object Constants{
  
  val TASK_HEADER_BACKGROUND_COLOR= new Color(68, 120, 33)
  val TASK_SELECTION_COLOR= new Color(255, 100, 0)
  val CONDITION_LABEL_BACKGROUND_COLOR= new Color(255, 238, 170)
  val CONDITION_LABEL_BORDER_COLOR= new Color(230, 180, 0)
  val DEFAULT_BACKGROUND_TASK_COLOR= new Color(255,255,255)
  val DEFAULT_BORDER_TASK_COLOR= new Color(0,0,0)
  
  val SCREEN_WIDTH= Toolkit.getDefaultToolkit.getScreenSize.width
  val SCREEN_HEIGHT= Toolkit.getDefaultToolkit.getScreenSize.height
  val PANEL_WIDTH= SCREEN_WIDTH* 0.8
  val PANEL_HEIGHT= SCREEN_HEIGHT* 0.8
  val EXPANDED_TASK_CONTAINER_WIDTH= 200
  val TASK_CONTAINER_WIDTH= 80
  val TASK_CONTAINER_HEIGHT= 100
  val TASK_TITLE_WIDTH = TASK_CONTAINER_WIDTH
  val TASK_TITLE_HEIGHT= 20
  val TASK_IMAGE_HEIGHT = TASK_CONTAINER_HEIGHT - TASK_TITLE_HEIGHT - 20
  val TASK_IMAGE_WIDTH= 70
  val TASK_IMAGE_HEIGHT_OFFSET= TASK_TITLE_HEIGHT + 10
  val TASK_IMAGE_WIDTH_OFFSET= (TASK_CONTAINER_WIDTH - TASK_IMAGE_WIDTH) / 2
  val EXPANDED_TASK_IMAGE_WIDTH_OFFSET= (EXPANDED_TASK_CONTAINER_WIDTH - TASK_IMAGE_WIDTH) / 2
  val NB_MAX_SLOTS= 5
    
  val TASK = "TASK"
  val PROTOTYPE = "PROTOTYPE"
  val SAMPLING = "SAMPLING"
  val ENVIRONMENT = "ENVIRONMENT"
  
  val ENTITY_DATA_FLAVOR= new DataFlavor(classOf[DataProxyUI[_]], "task")
//  val PROTOTYPE_DATA_FLAVOR= new DataFlavor(classOf[DataProxyUI[IPrototypeDataUI]], "prototype")
//  val SAMPLING_DATA_FLAVOR= new DataFlavor(classOf[DataProxyUI[ISamplingDataUI]], "sampling")
//  val ENVIRONMENT_DATA_FLAVOR= new DataFlavor(classOf[DataProxyUI[IEnvironmentDataUI]], "environment")

 // def simpleEntityName(entityName: String) = entityName.split('_')(0)
}