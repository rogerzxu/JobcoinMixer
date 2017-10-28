package rxu.jobcoin.mixer.dao

import rxu.jobcoin.mixer.config.Config
import rxu.jobcoin.mixer.model.MixerRecord

import com.google.inject.{Inject, Singleton}

import scala.collection.mutable.HashMap
import play.api.Logger

import scala.collection.mutable

/*
 * This isn't a real Dao; a real solution would use some sort of persistent
 * data store.
 */
@Singleton
class MixerDao @Inject()(
  config: Config
) {

  //depositAddress is a random UUID, so it serves as a convenient key
  private val store = mutable.HashMap[String,MixerRecord]()

  def put(depositAddress: String, mixerRecord: MixerRecord): Unit = {
    Logger.info(s"Storing record $mixerRecord")
    store.put(depositAddress, mixerRecord)
  }

  def get(depositAddress: String): MixerRecord = {
    store(depositAddress)
  }

  def list: Seq[MixerRecord] = store.values.toSeq

  def remove(depositAddress: String): Unit = {
    val removed = store.remove(depositAddress)
    Logger.info(s"Removed record $removed")
  }

  def update(depositAddress: String, mixerRecord: MixerRecord): Option[MixerRecord] = {
    Logger.info(s"Storing record $mixerRecord")
    store.put(depositAddress, mixerRecord)
  }

}
