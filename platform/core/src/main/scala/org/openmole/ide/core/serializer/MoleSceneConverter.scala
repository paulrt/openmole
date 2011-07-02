/*
 * Copyright (C) 2011 Mathieu leclaire <mathieu.leclaire at openmole.org>
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

package org.openmole.ide.core.serializer

import com.thoughtworks.xstream.converters.Converter
import com.thoughtworks.xstream.converters.MarshallingContext
import com.thoughtworks.xstream.converters.UnmarshallingContext
import com.thoughtworks.xstream.io.HierarchicalStreamReader
import com.thoughtworks.xstream.io.HierarchicalStreamWriter
import scala.collection.JavaConversions
import scala.collection.JavaConversions._
import java.awt.Point
import org.openmole.ide.core.properties.TaskDataUI
import org.openmole.ide.core.workflow.implementation.MoleScene
import org.openmole.ide.core.control.MoleScenesManager
import org.openmole.ide.core.commons.Constants
import org.openmole.ide.core.dataproxy.Proxys
import org.openmole.ide.core.workflow.implementation.TransitionUI
import org.openmole.ide.core.workflow.implementation.paint.ISlotWidget
import org.openmole.ide.core.commons.CapsuleType._
import org.openmole.ide.core.commons.TransitionType
import org.openmole.ide.core.exception.MoleExceptionManagement
import org.openmole.ide.core.workflow.model.ICapsuleUI
import scala.collection.mutable.HashMap
import scala.collection.mutable.HashSet

class MoleSceneConverter extends Converter{
  override def marshal(o: Object,writer: HierarchicalStreamWriter,mc: MarshallingContext) = {
    
    var firstSlotID = new HashMap[ICapsuleUI, Int]
    var iSlotMapping = new HashMap[ISlotWidget, Int]
    var taskUIs = new HashSet[TaskDataUI]
    
    val molescene= o.asInstanceOf[MoleScene]
    var slotcount = 0
    
    writer.addAttribute("name", molescene.manager.name.get)
    molescene.manager.capsules.values.foreach(view=> {
        writer.startNode("capsule")
        writer.addAttribute("start", view.startingCapsule.toString)
        writer.addAttribute("x", String.valueOf(view.connectableWidget.convertLocalToScene(view.connectableWidget.getLocation).getX))
        writer.addAttribute("y", String.valueOf(view.connectableWidget.convertLocalToScene(view.connectableWidget.getLocation).getY))

        //Input slot
        slotcount+= 1
        firstSlotID.put(view, slotcount)
        view.connectableWidget.islots.foreach(is=>{ 
            iSlotMapping += is-> slotcount
            writer.startNode("islot")
            writer.addAttribute("id",slotcount.toString)
            writer.endNode
            slotcount+= 1
          })
        
        //Output slot
        writer.startNode("oslot")
        writer.addAttribute("id", slotcount.toString)
        writer.endNode
        
        //Task
        if (view.capsuleType != CAPSULE) {
          taskUIs.add(view.dataProxy.get.dataUI.asInstanceOf[TaskDataUI])
          writer.startNode("task");
          writer.addAttribute("name", view.dataProxy.get.dataUI.name)
          writer.endNode
        }
        writer.endNode
      })
    //Transitions
    molescene.manager.getTransitions.foreach(trans=> {
        writer.startNode("transition");
        writer.addAttribute("source",(firstSlotID(trans.source) + trans.source.nbInputSlots).toString)
        writer.addAttribute("target", iSlotMapping(trans.target).toString)
        writer.addAttribute("type", TransitionType.toString(trans.transitionType))
        
        writer.addAttribute("condition", trans.condition.getOrElse(""))
        writer.endNode
      })
  }
  
  override def unmarshal(reader: HierarchicalStreamReader,uc: UnmarshallingContext) =  {
    var oslots = new HashMap[String, ICapsuleUI]
    var islots = new HashMap[String, ISlotWidget]
    
    val scene = new MoleScene
    scene.manager.name = Some(reader.getAttribute("name"))
        
    
    //Capsules
    while (reader.hasMoreChildren) {
      reader.moveDown
      val n0 = reader.getNodeName
      n0 match {
        case "capsule"=> {
            val p= new Point
            p.setLocation(reader.getAttribute("x").toDouble, reader.getAttribute("y").toDouble)
            val caps = MoleScenesManager.createCapsule(scene, p)
            val start = reader.getAttribute("start").toBoolean
            if (start) scene.manager.startingCapsule = Some(caps)
            while (reader.hasMoreChildren) {
              reader.moveDown
              val n1= reader.getNodeName
              n1 match{
                case "islot"=> islots.put(reader.getAttribute("id"), caps.addInputSlot(start))
                case "oslot"=> oslots.put(reader.getAttribute("id"), caps)
                case "task"=> caps.encapsule(Proxys.getTaskDataProxyUI(reader.getAttribute("name")))  
                case _=> MoleExceptionManagement.showException("Unknown balise "+ n1)
              }
              reader.moveUp
            }     
          }
        case "transition"=> {
            val source = oslots(reader.getAttribute("source"))
            val target = islots(reader.getAttribute("target"))             
            if (scene.manager.registerTransition(source, target, TransitionType.fromString(reader.getAttribute("type")),Some(reader.getAttribute("condition"))))
              scene.createEdge(scene.manager.capsuleID(source), scene.manager.capsuleID(target.capsule))           
          }
        case _=> MoleExceptionManagement.showException("Unknown balise "+ n0)        
      }
      reader.moveUp
    }
        
    MoleScenesManager.display(scene)
    scene
  }
  
  override def canConvert(t: Class[_]) = t.equals(classOf[MoleScene])
}