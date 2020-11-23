package com.example.parcial3

import android.bluetooth.BluetoothAssignedNumbers.GOOGLE
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.google.android.gms.auth.api.credentials.IdentityProviders.GOOGLE
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider


class MainActivity : AppCompatActivity() {
    private val GOOGLE_SIGN_IN = 100
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setup()
    }

    private fun setup() {
        title = "AUtenticación"

        btnSign.setOnClickListener{
            if (txtEmail.text.isNotEmpty() && txtPass.text.isNotEmpty()) {
                FirebaseAuth.getInstance().createUserWithEmailAndPassword(txtEmail.text.toString(), txtPass.text.toString()).addOnCompleteListener {
                    if (it.isSuccessful) {
                        showHome(it.result?.user?.email ?: "", ProviderType.BASIC)
                    } else {
                        showAlert()
                    }
                }

            }
        }
        btnLogin.setOnClickListener{
            if (txtEmail.text.isNotEmpty() && txtPass.text.isNotEmpty()) {
                FirebaseAuth.getInstance().signInWithEmailAndPassword(txtEmail.text.toString(), txtPass.text.toString()).addOnCompleteListener {
                    if (it.isSuccessful) {
                        showHome(it.result?.user?.email ?: "", ProviderType.BASIC)

                    } else {
                        showAlert()
                        Toast.makeText(this, "Ha ocurrido un error", Toast.LENGTH_LONG).show()
                    }
                }

            }
        }

        btnGoogle.setOnClickListener{
            // Configure Google Sign In
            Log.d("Botón", "Si entró al botón")
            Toast.makeText(this, "Mi alerta", Toast.LENGTH_LONG).show()
            val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestIdToken(getString(R.string.default_web_client_id))
                    .requestEmail()
                    .build()

            val googleClient = GoogleSignIn.getClient(this, gso)
            googleClient.signOut()

            startActivityForResult(googleClient.signInIntent, GOOGLE_SIGN_IN)
        }

    }

    private fun showAlert() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Error")
        builder.setMessage("Se ha producido un error autenticando al usuario")
        builder.setPositiveButton("Aceptar", null)
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }

    private fun showHome(email: String, provider: ProviderType) {
        val homeIntent = Intent(this, HomeActivity::class.java).apply {
            putExtra("email", email)
            putExtra("provider", provider.name)
        }
        startActivity(homeIntent)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == GOOGLE_SIGN_IN){
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)

            try {
                val account = task.getResult(ApiException::class.java)

                if(account != null) {
                    val credential = GoogleAuthProvider.getCredential(account.idToken, null)
                    FirebaseAuth.getInstance().signInWithCredential(credential).addOnCompleteListener {

                        if(it.isSuccessful) {
                            showHome(account.email ?: "", ProviderType.GOOGLE)
                        }else {
                            Log.d("Alerta", "Else")
                            showAlert()
                        }
                    }
                }
            } catch (e: ApiException) {
                Log.d("Alerta", e.message.toString())
                showAlert()
            }


        }
    }


}