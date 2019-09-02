package com.nikede.instagram

import android.app.ProgressDialog
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_register.*
import java.util.*
import kotlin.collections.HashMap

class RegisterActivity : AppCompatActivity() {

    val TAG = "RegisterActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        txt_login.setOnClickListener{
            startActivity(Intent(this, LoginActivity::class.java))
        }

        register.setOnClickListener{
            val pd = ProgressDialog(this)
            pd.setMessage(getString(R.string.please_wait))
            pd.show()

            if (username.text.isEmpty() || fullname.text.isEmpty()
                || email.text.isEmpty() || password.text.isEmpty())
                Toast.makeText(this, getString(R.string.all_fields_are_required), Toast.LENGTH_SHORT).show()
            else if (password.text.length < 6)
                Toast.makeText(this, getString(R.string.password_must_have_at_least_6_characters), Toast.LENGTH_SHORT).show()
            else {
                register(username.text.toString(), fullname.text.toString(), email.text.toString(), password.text.toString(), pd)
            }
        }
    }

    private fun register(username: String, fullname: String, email: String, password: String, pd: ProgressDialog) {
        val auth = FirebaseAuth.getInstance()
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener {
                if (it.isSuccessful()) {
                    val firebaseUser = auth.currentUser
                    val userId = firebaseUser?.uid

                    if (!userId.isNullOrEmpty()) {
                        val reference = FirebaseDatabase.getInstance().reference.child("Users").child(userId)
                        var hashMap = HashMap<String, Any>()
                        hashMap.put("userId", userId)
                        hashMap.put("username", username)
                        hashMap.put("fullname", fullname)
                        hashMap.put("bio", "")
                        hashMap.put("imageUrl", "https://firebasestorage.googleapis.com/v0/b/instagram-b8a5e.appspot.com/o/placeholder.png?alt=media&token=f7c40229-46cb-40bb-b154-4f0badf1ec32")

                        reference.setValue(hashMap).addOnCompleteListener {
                            if (it.isSuccessful) {
                                pd.dismiss()
                                val intent = Intent(this, MainActivity::class.java)
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                                startActivity(intent)
                            }
                        }
                    } else {
                        Log.e(TAG, "UserId is empty")
                    }
                } else {
                    pd.dismiss()
                    Toast.makeText(this, getString(R.string.cant_register), Toast.LENGTH_SHORT).show()
                }
            }
    }
}
