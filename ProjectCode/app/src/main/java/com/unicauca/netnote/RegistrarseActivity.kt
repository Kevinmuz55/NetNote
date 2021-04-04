package com.unicauca.netnote

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.auth.ktx.userProfileChangeRequest


class RegistrarseActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var registerButton: Button
    private lateinit var emailEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var nameEditText: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registrarse)

        auth = FirebaseAuth.getInstance()

        registerButton = findViewById(R.id.registrarse_registrarse_button)
        emailEditText = findViewById(R.id.registrarse_correo_editText)
        passwordEditText = findViewById(R.id.registrarse_contrasena_editText)
        nameEditText = findViewById(R.id.registrarse_nombre_editText)

        registerButton.setOnClickListener {
            registerUser()
        }


    }
    private fun registerUser() {
        if (emailEditText.text.toString().isEmpty()) {
            emailEditText.error = resources.getString(R.string.error_ingresarCorreo)
            emailEditText.requestFocus()
            return
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(emailEditText.text.toString()).matches()) {
            emailEditText.error = resources.getString(R.string.error_ingresarCorreoInvalido)
            emailEditText.requestFocus()
            return
        }
        if (passwordEditText.text.toString().isEmpty()) {
            passwordEditText.error = resources.getString(R.string.error_ingresarContrasena)
            passwordEditText.requestFocus()
            return
        }
        if (passwordEditText.text.length <= 5) {
            passwordEditText.error = resources.getString(R.string.error_ingresarContrasenaInvalida)
            passwordEditText.requestFocus()
            return
        }

        auth.createUserWithEmailAndPassword(
            emailEditText.text.toString(),
            passwordEditText.text.toString()
        )
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    user?.sendEmailVerification()
                        ?.addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                Toast.makeText(baseContext, resources.getString(R.string.acierto_registrarse), Toast.LENGTH_SHORT).show()
                                //Agregar nombre y foto
                                val profileUpdates = UserProfileChangeRequest.Builder()
                                    .setDisplayName(nameEditText.toString())
                                    .setPhotoUri(Uri.parse("https://www.flaticon.com/svg/vstatic/svg/1077/1077012.svg?token=exp=1617572628~hmac=7e65ad7926dbda0c0e9141eb3ac6331a"))
                                    .build()
                                user.updateProfile(profileUpdates)
                                auth.signOut()
                                startActivity(Intent(this, IniciarSesionActivity::class.java))
                                finish()
                            }
                        }

                } else {

                    Toast.makeText(
                        baseContext, resources.getString(R.string.error_registrarse),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
    }
}