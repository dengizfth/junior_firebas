package com.fatihden.firebase.Adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.fatihden.firebase.Model.Post
import com.fatihden.firebase.databinding.RecyclerRowBinding

class PostAdapter(private val arrayListPost : ArrayList<Post>) :RecyclerView.Adapter<PostAdapter.PostHolder> () {

    class PostHolder(val binding : RecyclerRowBinding) : RecyclerView.ViewHolder(binding.root){

    }

    // İmplementation:
    // Kalıp Code
    // RecyclerRowBinding ile PostHolder'ı bağlama
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostHolder {
        val recyclerRowBinding = RecyclerRowBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return PostHolder(recyclerRowBinding)
    }

    // implementation :
    // Recycler View Ne kadar row yaratması gerektiğiyle ilgili ,RecyclerView'a veri kümesindeki öğe sayısını döndürür.
    // Kalıp Code
    override fun getItemCount(): Int {
        return arrayListPost.size
    }

    // implementation :
    // Adapter ile iligi yapılacaklar :
    override fun onBindViewHolder(holder: PostHolder, position: Int) {
        TODO("Not yet implemented")
    }
}