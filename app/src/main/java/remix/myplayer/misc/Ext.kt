package remix.myplayer.misc

import android.content.Context
import android.content.res.Configuration
import android.provider.MediaStore
import remix.myplayer.bean.mp3.Album
import remix.myplayer.bean.mp3.Artist
import remix.myplayer.bean.mp3.Folder
import remix.myplayer.util.MediaStoreUtil

fun Album.getSongIds(): List<Int> {
  return MediaStoreUtil.getSongIds(MediaStore.Audio.Media.ALBUM_ID + "=?", arrayOf((albumID.toString())))
}

fun Artist.getSongIds(): List<Int> {
  return MediaStoreUtil.getSongIds(MediaStore.Audio.Media.ARTIST_ID + "=?", arrayOf(artistID.toString()))
}

fun Folder.getSongIds(): List<Int> {
  return MediaStoreUtil.getSongIdsByParentId(parentId)
}

fun Context.isPortraitOrientation(): Boolean {
  val configuration = this.resources.configuration //获取设置的配置信息
  val orientation = configuration.orientation //获取屏幕方向
  return orientation == Configuration.ORIENTATION_PORTRAIT
}

