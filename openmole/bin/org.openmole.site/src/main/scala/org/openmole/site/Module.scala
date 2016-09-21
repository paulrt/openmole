/**
 * Created by Romain Reuillon on 02/09/16.
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
 *
 */
package org.openmole.site

import org.openmole.core.module._
import org.openmole.plugin.environment.condor.CondorEnvironment
import org.openmole.plugin.environment.desktopgrid.DesktopGridEnvironment
import org.openmole.plugin.environment.egi._
import org.openmole.plugin.environment.oar.OAREnvironment
import org.openmole.plugin.environment.pbs.PBSEnvironment
import org.openmole.plugin.environment.sge.SGEEnvironment
import org.openmole.plugin.environment.slurm.SLURMEnvironment
import org.openmole.plugin.environment.ssh.SSHEnvironment
import org.openmole.plugin.task.care.CARETask
import org.openmole.plugin.task.netlogo5.NetLogo5Task
import org.openmole.plugin.task.scala.ScalaTask
import org.openmole.plugin.task.systemexec.SystemExecTask
import org.openmole.plugin.task.template.TemplateTask
import org.openmole.tool.file._
import org.openmole.tool.hash._

object module {

  case class ModuleEntry(name: String, description: String, components: Seq[File])

  def allModules =
    Seq[ModuleEntry](
      ModuleEntry("Condor", "Delegate workload to a Condor cluster", components[CondorEnvironment]),
      ModuleEntry("DesktopGrid", "Delegate workload to an adhoc desktop grid", components[DesktopGridEnvironment]),
      ModuleEntry("EGI", "Delegate workload to EGI", components[DIRACEnvironment]),
      ModuleEntry("OAR", "Delegate workload to an OAR cluster", components[OAREnvironment]),
      ModuleEntry("PBS", "Delegate workload to a PBS cluster", components[PBSEnvironment]),
      ModuleEntry("SGE", "Delegate workload to an SGE cluster", components[SGEEnvironment]),
      ModuleEntry("SLURM", "Delegate workload to a SLURM cluster", components[SLURMEnvironment]),
      ModuleEntry("SSH", "Delegate workload to a server via SSH", components[SSHEnvironment]),
      ModuleEntry("CARE", "Execute CARE archive", components[CARETask]),
      ModuleEntry("NetLogo5", "Execute NetLogo 5 simulation models", components[NetLogo5Task]),
      ModuleEntry("Scala", "Run scala code", components[ScalaTask]),
      ModuleEntry("SystemExec", "Execute system command", components[SystemExecTask]),
      ModuleEntry("Template", "Generate files", components[TemplateTask])
    )

  def generate(modules: Seq[ModuleEntry], baseDirectory: File, location: File ⇒ String) = {
    def allFiles = modules.flatMap(_.components)

    for {
      f ← allFiles.distinct
    } yield {
      val dest = baseDirectory / location(f)
      f copy dest
    }

    modules.map { m ⇒
      Module(
        m.name,
        m.description,
        m.components.map(f ⇒ Component(location(f), f.hash.toString))
      )
    }
  }

}

