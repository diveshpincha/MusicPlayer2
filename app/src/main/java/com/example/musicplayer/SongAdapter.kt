package com.example.musicplayer

import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.musicplayer.database.SongData
import com.example.musicplayer.databinding.SongItemBinding
import org.jetbrains.anko.imageURI

class SongAdapter(private val onClickListener: OnClickListener): ListAdapter<SongData, SongAdapter.SongViewHolder>(DiffCallBack){ //ListAdapter<SongInfo,SongAdapter.SongViewHolder>(DiffCallBack) {

    class SongViewHolder(var binding : SongItemBinding):RecyclerView.ViewHolder(binding.root){
        fun bind(song:SongData){
            binding.property = song
            binding.song.isSelected=true
            binding.executePendingBindings()
            binding.imageView2.imageURI = Uri.parse(song.imageUri)
            if (binding.imageView2.drawable == null) binding.imageView2.setImageResource(R.mipmap.music_icon)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SongViewHolder {
        return SongViewHolder(SongItemBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun onBindViewHolder(holder: SongViewHolder, position: Int) {
        val song = getItem(position)
        holder.itemView.setOnClickListener{
            onClickListener.onClick(song)
        }
        holder.bind(song)
    }

    companion object DiffCallBack:DiffUtil.ItemCallback<SongData>(){
        override fun areItemsTheSame(oldItem: SongData, newItem: SongData): Boolean {
            return oldItem==newItem
        }

        override fun areContentsTheSame(oldItem: SongData, newItem: SongData): Boolean {
            return oldItem==newItem
        }
    }
    class OnClickListener(val clickListener: (song: SongData) -> Unit) {
        fun onClick(song: SongData) = clickListener(song)
    }
}