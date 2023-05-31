package com.efemistik.assigned2me.view

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.efemistik.assigned2me.databinding.Activity2Binding
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import java.util.*

class Activity2 : AppCompatActivity() {

    private lateinit var binding: Activity2Binding
    private lateinit var activityResultLauncher : ActivityResultLauncher<Intent>
    private lateinit var permissionLauncher : ActivityResultLauncher<String>
    var selectedPicture : Uri? = null
    private lateinit var auth : FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    private lateinit var storage : FirebaseStorage

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = Activity2Binding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        registerLauncher()

        auth = Firebase.auth
        firestore = Firebase.firestore
        storage = Firebase.storage
    }

    fun assign(view: View) {

        val uuid = UUID.randomUUID()
        val imageName = "$uuid.jpg"

        val reference = storage.reference
        val imageReference = reference.child("images").child(imageName)

        if(selectedPicture != null) {

            imageReference.putFile(selectedPicture!!).addOnSuccessListener {

                val uploadPictureReference = storage.reference.child("images").child(imageName)
                uploadPictureReference.downloadUrl.addOnSuccessListener {
                    val downloadUrl = it.toString()
                    if (auth.currentUser != null){
                        val postMap = hashMapOf<String,Any>()
                        postMap.put("downloadUrl",downloadUrl)
                        postMap.put("userEmail",auth.currentUser!!.email!!)
                        postMap.put("assignment",binding.subjectText.text.toString())
                        postMap.put("date", Timestamp.now())

                        firestore.collection("Assignments").add(postMap).addOnSuccessListener {
                            finish()
                        }.addOnFailureListener {
                            Toast.makeText(this@Activity2,it.localizedMessage,Toast.LENGTH_LONG).show()
                        }
                    }

                }

            }.addOnFailureListener {
                Toast.makeText(this, it.localizedMessage, Toast.LENGTH_LONG).show()
            }
        }

    }

    fun selectImage(view: View) {

        if(ContextCompat.checkSelfPermission(this,android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            if(ActivityCompat.shouldShowRequestPermissionRationale(this,android.Manifest.permission.READ_EXTERNAL_STORAGE)){
                Snackbar.make(view,"Your permission needed to reach gallery",Snackbar.LENGTH_INDEFINITE).setAction("Give Permission") {
                    permissionLauncher.launch(android.Manifest.permission.READ_EXTERNAL_STORAGE)
                }.show()
            } else {

                permissionLauncher.launch(android.Manifest.permission.READ_EXTERNAL_STORAGE)
            }
        } else {
            val intentToGallery = Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            activityResultLauncher.launch(intentToGallery)
        }
    }
    private fun registerLauncher(){

        activityResultLauncher =registerForActivityResult(ActivityResultContracts.StartActivityForResult()){ result ->
            if (result.resultCode == RESULT_OK) {
                val intentFromResult = result.data
                if (intentFromResult != null) {
                    selectedPicture = intentFromResult.data
                    selectedPicture?.let {
                        binding.imageView.setImageURI(it)
                    }
                }
            }
        }
        permissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { result ->
            if (result) {
                val intentToGallery = Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                activityResultLauncher.launch(intentToGallery)
            } else {
                Toast.makeText(this@Activity2,"Your permission needed to continue!",Toast.LENGTH_LONG).show()
            }
        }
    }
}
