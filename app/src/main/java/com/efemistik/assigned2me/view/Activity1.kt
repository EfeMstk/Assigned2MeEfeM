package com.efemistik.assigned2me.view

import android.content.AbstractThreadedSyncAdapter
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.LinearLayout
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.efemistik.assigned2me.R
import com.efemistik.assigned2me.adapter.FeedRecyclerAdapter
import com.efemistik.assigned2me.databinding.Activity1Binding
import com.efemistik.assigned2me.model.Assign
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class Activity1 : AppCompatActivity() {

    private lateinit var binding : Activity1Binding
    private lateinit var auth : FirebaseAuth
    private lateinit var db : FirebaseFirestore
    private lateinit var postArraylist : ArrayList<Assign>
    private lateinit var feedAdapter: FeedRecyclerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = Activity1Binding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        auth = Firebase.auth
        db = Firebase.firestore

        postArraylist = ArrayList<Assign>()

        getData()

        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        feedAdapter = FeedRecyclerAdapter(postArraylist)
        binding.recyclerView.adapter = feedAdapter
    }

    private fun getData() {
        db.collection("Assignments").orderBy("date",
            Query.Direction.DESCENDING).addSnapshotListener { value, error ->
            if (error != null) {
                Toast.makeText(this, error.localizedMessage, Toast.LENGTH_LONG).show()
            } else {
                if (value != null) if (!value.isEmpty) {
                    val documents = value.documents

                    postArraylist.clear()

                    for (document in documents) {
                        val assignment = document.get("assignment") as? String
                        val userEmail = document.get("userEmail") as? String
                        val downloadUrl = document.get("downloadUrl") as? String


                        if (assignment != null && userEmail != null && downloadUrl != null) {
                            val post = Assign(userEmail, assignment, downloadUrl)
                            postArraylist.add(post)
                        }
                        feedAdapter.notifyDataSetChanged()
                    }
                }
            }
        }
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {

        val menuInflater = menuInflater
        menuInflater.inflate(R.menu.assign_menu,menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        if(item.itemId == R.id.add_post) {
            val intent = Intent(this, Activity2::class.java)
            startActivity(intent)
        } else if (item.itemId == R.id.signout){
            auth.signOut()
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

        return super.onOptionsItemSelected(item)
    }
}
