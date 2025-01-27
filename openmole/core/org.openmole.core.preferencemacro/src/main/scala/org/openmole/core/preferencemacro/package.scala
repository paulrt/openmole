package org.openmole.core

import scala.reflect.macros.whitebox._

/*
 * Copyright (C) 2019 Romain Reuillon
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
package object preferencemacro {
  import scala.language.experimental.macros

  def list[T](t: T): Seq[ConfigurationLocation[_]] = macro list_impl[T]

  def list_impl[T: c.WeakTypeTag](c: Context)(t: c.Expr[T]): c.Expr[Seq[ConfigurationLocation[_]]] = {
    import c.universe._

    val tType = weakTypeOf[T]
    val configurationLocationType = weakTypeOf[ConfigurationLocation[_]]

    val configurations =
      tType.members.collect {
        case m: MethodSymbol if m.returnType <:< configurationLocationType && m.paramLists.isEmpty && m.isPublic ⇒ m
        case m: TermSymbol if m.typeSignature <:< configurationLocationType && m.isPublic                        ⇒ m
      }

    val configurationValues = configurations.map { c ⇒ q"$t.$c" }
    val result = q"""Seq(..$configurationValues)"""

    c.Expr[Seq[ConfigurationLocation[_]]](result)
  }
}
