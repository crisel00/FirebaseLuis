package com.cromero.firebaseluis

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_clicker.*


class clickerActivity : AppCompatActivity() {

    private lateinit var dbReference: DatabaseReference
    private lateinit var database: FirebaseDatabase

    var userID = ""
    var clicks = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_clicker)

        database = FirebaseDatabase.getInstance()
        dbReference = database!!.getReference()

        userID = intent.getStringExtra("EXTRA_USER_ID").toString()

        //el listener para añadirlo al nodo correspondiente (se llama una vez al ser creado)
         var eventListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                Log.e("onDataChange", dataSnapshot.value.toString())
                if(dataSnapshot.exists()){
                    clicks = Integer.parseInt(dataSnapshot.getValue().toString())
                } else {
                    clicks = 0
                }

                tv_Clicks.text = clicks.toString()
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.e("onDataChange", "Error!", databaseError.toException())
            }
        }

        dbReference.child(userID).addValueEventListener(eventListener)

        bt_tap.setOnClickListener { click() }
    }

    override fun onPause() {
        super.onPause()
        dbReference.child(userID).setValue(clicks)
    }

    private fun click(){
        clicks++
        tv_Clicks.text = clicks.toString()
    }
}