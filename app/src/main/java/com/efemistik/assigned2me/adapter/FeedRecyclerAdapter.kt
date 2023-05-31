package com.efemistik.assigned2me.adapter

import android.R
import android.content.ContentValues.TAG
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.recyclerview.widget.RecyclerView
import com.efemistik.assigned2me.databinding.RecyclerRowBinding
import com.efemistik.assigned2me.model.Assign
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.core.View
import com.squareup.picasso.Picasso
import android.widget.Spinner

class FeedRecyclerAdapter(private val postList: ArrayList<Assign>) : RecyclerView.Adapter<FeedRecyclerAdapter.PostHolder>() {
    private val db = FirebaseFirestore.getInstance()

    class PostHolder(val binding: RecyclerRowBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostHolder {
        val binding = RecyclerRowBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PostHolder(binding)
    }

    override fun getItemCount(): Int {
        return postList.size
    }

    override fun onBindViewHolder(holder: PostHolder, position: Int) {
        holder.binding.recyclerEmailText.text = postList[position].email
        holder.binding.recyclerAssignmentText.text = postList[position].assignment
        Picasso.get().load(postList[position].downloadUrl).into(holder.binding.recyclerImageView)

    }


}