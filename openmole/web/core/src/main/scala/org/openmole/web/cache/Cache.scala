package org.openmole.web.cache

import org.openmole.core.model.mole.IMoleExecution
import java.io.File
import akka.actor.ActorSystem
import org.openmole.web.db.SlickDB
import org.openmole.web.db.tables.{ MoleStats, MoleData }
import slick.driver.H2Driver.simple._
import Database.threadLocalSession
import slick.jdbc.meta.MTable

import javax.sql.rowset.serial.SerialBlob

/**
 * Created by mhammons on 4/23/14.
 */
class Cache(system: ActorSystem, database: SlickDB) {
  private val cachedMoles = new DataHandler[String, IMoleExecution](system)
  private val capsules = new DataHandler[String, File](system)
  private val moleStats = new DataHandler[String, Stats](system)
  private val mole2CacheId = new DataHandler[IMoleExecution, String](system)

  val logger = org.openmole.web.Log.log

  val dataB = database.db

  dataB withSession {
    if (MTable.getTables("MoleData").list().isEmpty)
      MoleData.ddl.create // check that table exists somehow
  }

  dataB withSession {
    if (MTable.getTables("MoleStats").list().isEmpty)
      MoleStats.ddl.create
  }

  def cacheMoleExecution(moleExecution: IMoleExecution, path: Option[File], cacheId: String) = {
    path foreach (capsules.add(cacheId, _))
    cachedMoles add (cacheId, moleExecution)
    mole2CacheId add (moleExecution, cacheId)
    moleExecution
  }

  def getMole(key: String) = cachedMoles get key

  def getCacheId(mole: IMoleExecution) = mole2CacheId get mole getOrElse (throw new Exception("Cache ID requested for uncached mole."))

  def getMoleStats(key: String): Stats = {
    lazy val stats = getStatus(key) flatMap (s ⇒ dataB withSession {
      val status = Status.statuses.find(_.toString == s).getOrElse(throw new Exception("Unknown status on DB"))
      (for (s ← MoleStats if s.id === key) yield s.*).list().map {
        case (_, a, b, c, d, e) ⇒
          println(c)
          Stats(a, b, c, d, e, status)
      }.headOption
    })

    println(stats)
    moleStats get key orElse stats getOrElse EmptyStats
  }

  def getUnfinishedMoleKeys = dataB withSession {
    (for (m ← MoleData if m.state === "Running") yield m.id.asColumnOf[String]).list
  }

  def getMoleKeys = dataB withSession {
    (for {
      m ← MoleData
    } yield m.id.asColumnOf[String]).list
  }

  def getStatus(moleId: String): Option[String] =
    dataB withSession {
      (for (m ← MoleData if m.id === moleId) yield m.state).list().headOption
    }

  def decacheMole(mole: IMoleExecution) = {
    val mKey = mole2CacheId get mole
    mKey foreach (id ⇒ println(s"decaching mole id: $id"))
    mKey foreach (cachedMoles get _ foreach (_.cancel))
    mKey flatMap (moleStats get _) foreach {
      stats ⇒
        println(stats)
        dataB withSession {
          MoleStats.filter(_.id === mKey.get)
            .map(x ⇒ x.*)
            .update((mKey.get, stats.ready, stats.running, stats.completed, stats.failed, stats.cancelled))
        }
    }
    mKey foreach (k ⇒ List(cachedMoles, moleStats, capsules) map (_ remove k))
    mole2CacheId remove mole
  }

  def deleteMole(key: String) = {
    getMole(key) map decacheMole

    dataB withSession {
      MoleData.filter(_.id === key).delete
      MoleStats.filter(_.id === key).delete
    }

  }

  def setStatus(mole: IMoleExecution, status: Status) {
    val moleId = getCacheId(mole)
    dataB withSession {
      val x = for { m ← MoleData if m.id === moleId } yield m.state
      x update status.toString

    }
    logger.info(s"updated mole: ${moleId} to ${status}")
  }

  def getMoleResult(key: String) = dataB withSession {
    val blob = (for (m ← MoleData if m.id === key) yield m.result).list.head
    blob.getBytes(1, blob.length.toInt)
  }

  def storeResultBlob(exec: IMoleExecution, blob: SerialBlob) = dataB withSession {
    val moleId = getCacheId(exec)
    val r = for (m ← MoleData if m.id === moleId) yield m.result
    r.update(blob)
    logger.info(s"Blob stored for mole: $moleId")
    println("test")
  }

  def getCapsule(exec: IMoleExecution) = mole2CacheId get exec flatMap (capsules get _)

  def updateStats(exec: IMoleExecution, stats: Stats) {
    val moleId = getCacheId(exec)
    moleStats add (moleId, stats)
    logger.info(s"Updated the stats of mole $moleId to $stats")
  }

}
