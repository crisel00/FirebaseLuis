package com.cromero.firebaseluis

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity(){

    //variables
    var mGoogleSignInClient: GoogleSignInClient? = null;
    val RC_GOOGLE_SIGNIN = 1

    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val bundle=Bundle()
        bundle.putString("message", "Entrada")

        auth = Firebase.auth

        //Configurar login de google
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .build()

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);



        val account = GoogleSignIn.getLastSignedInAccount(this)
        if(account != null){
            showOk();
        }



        setup()
    }

    fun googleLogin(){
        var signInIntent = mGoogleSignInClient?.signInIntent

        startActivityForResult(signInIntent, RC_GOOGLE_SIGNIN)
    }

    private fun setup(){
        botonRegistrar.setOnClickListener {
            if (etEmail.text.isNotEmpty() && etContrasena.text.isNotEmpty()) {
                FirebaseAuth.getInstance().createUserWithEmailAndPassword(
                    etEmail.text.toString(),
                    etContrasena.text.toString()
                ).addOnCompleteListener {
                    if (it.isSuccessful) {
                        showOk()
                        persistencia()
                        sesion()
                        startClicker()

                    } else {
                        showError()
                    }
                }
            }
        }

        botonIniciar.setOnClickListener {
            if(etEmail.text.isNotEmpty() && etContrasena.text.isNotEmpty()){
                FirebaseAuth.getInstance().signInWithEmailAndPassword(
                    etEmail.text.toString(),
                    etContrasena.text.toString()
                ).addOnCompleteListener{
                    if(it.isSuccessful){
                        showOk()
                        persistencia()
                        sesion()
                        startClicker()

                    } else{
                        showError()
                    }
                }
            }
        }

        googleSignIn.setOnClickListener{
            googleLogin()
        }

        b_anonymousSignIn.setOnClickListener {
            auth.signInAnonymously()
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        // Sign in success, update UI with the signed-in user's information
                        //Toast.makeText(this, "ANONIMOOOOOOOOO", Toast.LENGTH_LONG).show()

                        val intent = Intent(baseContext, clickerActivity::class.java)
                        intent.putExtra("EXTRA_USER_ID", "ANONIMO")
                        startActivity(intent)

                    } else {
                        // If sign in fails, display a message to the user.
                        Toast.makeText(this, "Anonimon´t", Toast.LENGTH_LONG).show()
                        Toast.makeText(
                            baseContext, "Authentication failed.",
                            Toast.LENGTH_SHORT
                        ).show()

                    }
                }

        }
    }



    private fun showOk(){
        Toast.makeText(this, "OK", Toast.LENGTH_SHORT).show()
    }

    private fun showError(){
        Toast.makeText(this, "ERROR", Toast.LENGTH_SHORT).show()
    }

    private fun sesion(){
        val prefs:SharedPreferences=getSharedPreferences(
            (getString(R.string.datosInicio)),
            Context.MODE_PRIVATE
        )
        val email = prefs.getString("email", null)
        if(email==null){
            botonRegistrar.setEnabled(true)
            botonIniciar.setEnabled(true)
        }else{

        }
    }

    private fun persistencia(){
        val prefs:SharedPreferences.Editor = getSharedPreferences(
            getString(R.string.datosInicio),
            Context.MODE_PRIVATE
        ).edit()
        prefs.putString("email", etEmail.text.toString())
        prefs.apply()

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == RC_GOOGLE_SIGNIN){
            var task:Task<GoogleSignInAccount> = GoogleSignIn.getSignedInAccountFromIntent(data)
            handleGoogleSignInResult(task)
        }
    }

    private fun handleGoogleSignInResult(task: Task<GoogleSignInAccount>) {
        try{
            Toast.makeText(this, "LOGEADO EN GOOGLE YEAH BOI", Toast.LENGTH_LONG).show()
            startClicker()
        }catch (e: ApiException){
            Toast.makeText(this, "ERROR AL INCIAR SESION D:", Toast.LENGTH_LONG).show()
        }
    }

    private fun startClicker(){
        val intent = Intent(baseContext, clickerActivity::class.java)
        intent.putExtra("EXTRA_USER_ID", auth.currentUser?.uid.toString())
        startActivity(intent)
    }
}

//todo
// 1.- Login con email/contraseña                       --Hecho
// 2.- Si es correcto redirigir                         --
// 3.- Añadir login Google                              --Hecho
// 4.- Añadir login anonimo                             --Hecho
// 5.- pensar en que agregar a la app tras el login     --Hecho