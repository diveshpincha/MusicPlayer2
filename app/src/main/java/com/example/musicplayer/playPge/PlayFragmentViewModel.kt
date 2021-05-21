package com.example.musicplayer.playPge

import android.app.Application
import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import androidx.core.content.ContextCompat.startActivity
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.musicplayer.database.SongDao
import com.example.musicplayer.database.SongData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch


class PlayFragmentViewModel(private val dataSource: SongDao, private val app: Application):ViewModel() {

    val viewModelJob = Job()
    val viewModelScope = CoroutineScope(viewModelJob + Dispatchers.Main)

    var mediaPlayer:MediaPlayer? = null

    var current = 0

    var songs = MutableLiveData<List<SongData>>()

    var songName = MutableLiveData<String>()

    var imageUri = MutableLiveData<String>()

    var isFavourite = MutableLiveData<Boolean>()

    var maxTime = MutableLiveData<Int>(0)

    lateinit var forUpdate : SongData

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
        mediaPlayer?.stop()
    }

    init{
        initialize()
    }

    fun share(uri: Uri, context: Context){
        val share  = Intent(Intent.ACTION_SEND)
        share.type = "audio/*";
        share.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION;
        share.putExtra(Intent.EXTRA_STREAM, uri);
        context.startActivity(Intent.createChooser(share, "Share Sound File"));
    }

    private fun initialize() {
        viewModelScope.launch {
            songs.value = dataSource.display()
        }
    }

    fun getPlaylistFav(){
        viewModelScope.launch {
            songs.value=dataSource.getFavourites()
        }
    }

    fun getPlaylistAll(){
        viewModelScope.launch {
            songs.value=dataSource.display()
        }
    }

    fun startMedia(ind: Int) {
        current = ind
        var pos = ind
        viewModelScope.launch {

            if(songs.value.isNullOrEmpty()) {
                songs.value = dataSource.display()
            }

            if(pos>songs.value!!.size-1){
                pos = ind%(songs.value!!.size)
            }
            if(pos<0){
                if(pos%songs.value!!.size==0){
                    pos = 0
                }else{
                    pos = songs.value!!.size + pos%songs.value!!.size
                }
            }
            mediaPlayer?.reset()
            mediaPlayer = MediaPlayer.create(app, Uri.parse(songs.value!!.get(pos).songUri))
            mediaPlayer?.start()
            isFavourite.value = songs.value!![pos].fav
            songName.value = songs.value!![pos].title
            imageUri.value = songs.value!!.get(pos).imageUri
            maxTime.value= mediaPlayer?.duration
            mediaPlayer?.setOnCompletionListener{
                startMedia(pos + 1)
            }
        }
    }

    fun playingCheck() : Boolean{
        if(mediaPlayer!!.isPlaying){
            mediaPlayer!!.pause()
            return true
        }
        mediaPlayer!!.start()
        return false
    }


    fun setFav(ind: Int){
        isFavourite.value = !(isFavourite.value)!!

        viewModelScope.launch {
            var pos = ind

            if (pos > songs.value!!.size - 1) {
                pos = ind % (songs.value!!.size)
            }
            if (pos < 0) {
                if (pos % songs.value!!.size == 0) {
                    pos = 0
                } else {
                    pos = songs.value!!.size + pos % songs.value!!.size
                }
            }
            forUpdate = songs.value!!.get(pos)
            forUpdate.fav = isFavourite.value!!
            dataSource.update(forUpdate)
        }
    }
}