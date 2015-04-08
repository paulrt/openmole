/*
 * Copyright (C) 2012 Romain Reuillon
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

package org.openmole.core.workflow

import org.openmole.core.workflow.builder.TaskBuilder
import org.openmole.core.workflow.mole._
import org.openmole.core.workflow.puzzle._
import org.openmole.core.workflow.tools._
import org.openmole.core.workflow.data._
import org.openmole.core.workflow.mole._
import org.openmole.core.workflow.task._
import org.openmole.core.workflow.tools._
import org.openmole.core.workflow.transition._

import org.openmole.core.workflow.puzzle._
import task._
import data._

package transition {

  case class TransitionParameter(
      puzzleParameter: Puzzle,
      conditionParameter: Condition = Condition.True,
      filterParameter: Filter[String] = Filter.empty) {
    def when(condition: Condition) = copy(conditionParameter = condition)
    def filter(filter: Filter[String]) = copy(filterParameter = filter)
  }

  trait TransitionPackage {

    implicit def transitionsPuzzlePieceDecorator(from: PuzzlePiece) = new TransitionDecorator(from)
    implicit def transitionsPuzzleDecorator(from: Puzzle) = new TransitionDecorator(from)
    implicit def transitionsCapsuleDecorator(from: Capsule) = new TransitionDecorator(from.toPuzzle)
    implicit def transitionsTaskDecorator(from: Task) = new TransitionDecorator(from.toCapsule.toPuzzle)
    implicit def transitionsTaskBuilderDecorator(from: TaskBuilder) = new TransitionDecorator(from.toTask.toCapsule.toPuzzle)
    implicit def transitionsSlotDecorator(slot: Slot) = new TransitionDecorator(slot.toPuzzle)
    implicit def taskToSlotConverter(task: Task) = Slot(Capsule(task))
    implicit def transitionToSlotConverter(transition: ITransition) = transition.end
    implicit def conditionStringConverter(condition: String) = Condition(condition)

    class TransitionDecorator(from: Puzzle) {

      def when(condition: Condition) = TransitionParameter(from, condition)
      def filter(filter: Filter[String]) = TransitionParameter(from, filterParameter = filter)

      def -<(
        to: Puzzle,
        condition: Condition = Condition.True,
        filter: Filter[String] = Filter.empty,
        size: Option[String] = None) = {

        val transitions = from.lasts.map {
          c ⇒
            size match {
              case None    ⇒ new ExplorationTransition(c, to.firstSlot, condition, filter)
              case Some(s) ⇒ new EmptyExplorationTransition(c, to.firstSlot, s, condition, filter)
            }
        }

        Puzzle.merge(from.firstSlot, to.lasts, from :: to :: Nil, transitions)
      }

      def -<(toHead: Puzzle, toTail: Puzzle*): Puzzle = -<(toHead :: toTail.toList)

      def -<(toPuzzles: Seq[Puzzle]) = {
        val transitions = for (f ← from.lasts; l ← toPuzzles) yield new ExplorationTransition(f, l.firstSlot)
        Puzzle.merge(from.firstSlot, toPuzzles.flatMap {
          _.lasts
        }, from :: toPuzzles.toList ::: Nil, transitions)
      }

      def -<-(
        to: Puzzle,
        condition: Condition = Condition.True,
        filter: Filter[String] = Filter.empty) = {

        val transitions = from.lasts.map {
          c ⇒ new SlaveTransition(c, to.firstSlot, condition, filter)
        }

        Puzzle.merge(from.firstSlot, to.lasts, from :: to :: Nil, transitions)
      }

      def -<-(toHead: Puzzle, toTail: Puzzle*): Puzzle = -<-(toHead :: toTail.toList)

      def -<-(toPuzzles: Seq[Puzzle]) = {
        val transitions = for (f ← from.lasts; l ← toPuzzles) yield new SlaveTransition(f, l.firstSlot)
        Puzzle.merge(from.firstSlot, toPuzzles.flatMap {
          _.lasts
        }, from :: toPuzzles.toList ::: Nil, transitions)
      }

      def >-(
        to: Puzzle,
        condition: Condition = Condition.True,
        filter: Filter[String] = Filter.empty,
        trigger: Condition = Condition.False) = {
        val transitions = from.lasts.map { c ⇒ new AggregationTransition(c, to.firstSlot, condition, filter, trigger) }
        Puzzle.merge(from.firstSlot, to.lasts, from :: to :: Nil, transitions)
      }

      def >-(toHead: Puzzle, toTail: Puzzle*): Puzzle = >-(toHead :: toTail.toList)

      def >-(toPuzzles: Seq[Puzzle]) = {
        val transitions = for (f ← from.lasts; l ← toPuzzles) yield new AggregationTransition(f, l.firstSlot)
        Puzzle.merge(from.firstSlot, toPuzzles.flatMap {
          _.lasts
        }, from :: toPuzzles.toList ::: Nil, transitions)
      }

      def >|(
        to: Puzzle,
        trigger: Condition,
        filter: Filter[String] = Filter.empty) = {
        val transitions = from.lasts.map { c ⇒ new EndExplorationTransition(c, to.firstSlot, trigger, filter) }
        Puzzle.merge(from.firstSlot, to.lasts, from :: to :: Nil, transitions)
      }

      def --(to: Puzzle, condition: Condition = Condition.True, filter: Filter[String] = Filter.empty): Puzzle = {
        val transitions =
          from.lasts.map {
            c ⇒ new Transition(c, to.firstSlot, condition, filter)
          }
        Puzzle.merge(from.firstSlot, to.lasts, from :: to :: Nil, transitions)
      }

      def --(head: Puzzle, tail: Puzzle*): Puzzle = this.--((Seq(head) ++ tail).map(TransitionParameter(_)): _*)

      def --(parameters: TransitionParameter*): Puzzle = {
        val puzzles = parameters.map { case TransitionParameter(t, condition, filter) ⇒ this.--(t, condition, filter) }
        Puzzle.merge(from.firstSlot, puzzles.flatMap(_.lasts), puzzles)
      }

      def --=(to: Puzzle, condition: Condition = Condition.True, filter: Filter[String] = Filter.empty): Puzzle = {
        val transitions =
          from.lasts.map {
            c ⇒ new Transition(c, Slot(to.first), condition, filter)
          }
        Puzzle.merge(from.firstSlot, to.lasts, from :: to :: Nil, transitions)
      }

      def --=(head: Puzzle, tail: Puzzle*): Puzzle = this.--=((Seq(head) ++ tail).map(TransitionParameter(_)): _*)

      def --=(parameters: TransitionParameter*): Puzzle = {
        val puzzles = parameters.map { case TransitionParameter(t, condition, filter) ⇒ this.--=(t, condition, filter) }
        Puzzle.merge(from.firstSlot, puzzles.flatMap(_.lasts), puzzles)
      }

      def oo(to: Puzzle, filter: Filter[String] = Filter.empty) = {
        val channels = from.lasts.map {
          c ⇒ new DataChannel(c, to.firstSlot, filter)
        }
        Puzzle.merge(from.firstSlot, to.lasts, from :: to :: Nil, dataChannels = channels)
      }

    }

  }
}

package object transition extends TransitionPackage