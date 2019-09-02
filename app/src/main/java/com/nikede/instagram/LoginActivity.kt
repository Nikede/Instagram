package com.nikede.instagram

import android.app.ProgressDialog
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {

    val TAG = "LoginActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        txt_signup.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }

        val pd = ProgressDialog(this)
        val context = this

        login.setOnClickListener {
            pd.setMessage(getString(R.string.please_wait))
            pd.show()
        }

        if (email.text.toString().isEmpty() || password.text.toString().isEmpty()) {
            Toast.makeText(this, getString(R.string.all_fields_are_required), Toast.LENGTH_SHORT).show()
        } else {
            val auth = FirebaseAuth.getInstance()
            auth.signInWithEmailAndPassword(email.text.toString(), password.text.toString()).addOnCompleteListener {
                if (it.isSuccessful) {
                    val uid = auth.currentUser?.uid
                    if (!uid.isNullOrEmpty()) {
                        val reference =
                            FirebaseDatabase.getInstance().getReference().child("Users").child(uid)

                        reference.addValueEventListener(object : ValueEventListener{
                            override fun onCancelled(p0: DatabaseError) {
                                pd.dismiss()
                            }

                            override fun onDataChange(p0: DataSnapshot) {
                                pd.dismiss()
                                val intent = Intent(context, MainActivity::class.java)
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                                startActivity(intent)
                                finish()
                            }

                        })
                    } else {
                        Log.e(TAG, "UserId is empty")
                    }
                } else {
                    pd.dismiss()
                    Toast.makeText(this, getString(R.string.authentication_failed), Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}
