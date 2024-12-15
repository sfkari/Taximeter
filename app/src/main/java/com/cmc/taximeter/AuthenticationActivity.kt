package com.cmc.taximeter

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class AuthenticationActivity : AppCompatActivity() {

    // Déclaration des vues
    private lateinit var edtEmail: EditText
    private lateinit var edtPassword: EditText
    private lateinit var edtName: EditText
    private lateinit var edtAge: EditText
    private lateinit var spinnerLicenseType: Spinner
    private lateinit var btnSignIn: Button
    private lateinit var btnSignUp: Button
    private lateinit var ibPassword: ImageButton
    private lateinit var txtSignUp: TextView
    private lateinit var txtSignIn: TextView

    var isPassword = true

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_authentication)

        // Initialisation des vues
        edtEmail = findViewById(R.id.edtEmail)
        edtPassword = findViewById(R.id.edtPassword)
        edtName = findViewById(R.id.edtName)
        edtAge = findViewById(R.id.edtAge)
        spinnerLicenseType = findViewById(R.id.spinnerLicenseType)
        btnSignIn = findViewById(R.id.btnSignIn)
        btnSignUp = findViewById(R.id.btnSignUp)
        txtSignUp = findViewById(R.id.txtSignUp)
        txtSignIn = findViewById(R.id.txtSignIn)
        ibPassword = findViewById(R.id.ibPassword)


        ibPassword.setOnClickListener {
            togglePasswordVisibility()
        }


        // Afficher la vue de connexion au démarrage
        switchToSignIn()

        // Action pour afficher l'inscription
        txtSignUp.setOnClickListener {
            switchToSignUp()
        }

        txtSignIn.setOnClickListener {
            switchToSignIn()
        }

        // Action pour se connecter
        btnSignIn.setOnClickListener {
            signIn()
        }

        // Action pour s'inscrire
        btnSignUp.setOnClickListener {
            signUp()
        }

        // Adapter pour le spinner
        val adapter = ArrayAdapter.createFromResource(
            this,
            R.array.license_types,
            android.R.layout.simple_spinner_item
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerLicenseType.adapter = adapter
    }

    private fun togglePasswordVisibility() {

        if (isPassword) {
            // Afficher le texte normal
            edtPassword.inputType = InputType.TYPE_CLASS_TEXT
            // Modifier l'icône du bouton si nécessaire (par exemple changer l'icône de l'œil)
            ibPassword.setImageResource(R.drawable.visibility)
        } else {
            // Afficher comme mot de passe
            edtPassword.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
            // Modifier l'icône du bouton pour l'œil ouvert
            ibPassword.setImageResource(R.drawable.visibility_off)
        }

        // Mettre à jour l'état pour l'alterner lors du prochain clic
        isPassword = !isPassword

        // Pour que le changement prenne effet immédiatement, demander à l'EditText de redonner le focus
        edtPassword.setSelection(edtPassword.text.length)

    }

    // Fonction pour basculer vers l'écran d'inscription
    private fun switchToSignUp() {
        edtName.visibility = View.VISIBLE
        edtAge.visibility = View.VISIBLE
        spinnerLicenseType.visibility = View.VISIBLE
        btnSignUp.visibility = View.VISIBLE
        btnSignIn.visibility = View.GONE
        txtSignUp.visibility = View.GONE
        txtSignIn.visibility = View.VISIBLE
    }

    // Fonction pour basculer vers l'écran de connexion
    private fun switchToSignIn() {
        edtName.visibility = View.GONE
        edtAge.visibility = View.GONE
        spinnerLicenseType.visibility = View.GONE
        btnSignUp.visibility = View.GONE
        btnSignIn.visibility = View.VISIBLE
        txtSignUp.visibility = View.VISIBLE
        txtSignIn.visibility = View.GONE
    }

    // Fonction de connexion
    private fun signIn() {
        // Récupérer les informations entrées par l'utilisateur
        val email = edtEmail.text.toString().lowercase()
        val password = edtPassword.text.toString()

        // Validation des champs
        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Veuillez remplir tous les champs", Toast.LENGTH_SHORT).show()
            return
        }

        // Vérifier les informations de connexion dans SharedPreferences
        val sharedPreferences = getSharedPreferences("UserPreferences", MODE_PRIVATE)
        val storedPassword = sharedPreferences.getString(email, null)

        if (storedPassword != null && storedPassword == password) {
            // Connexion réussie
            // Enregistrer l'email de l'utilisateur dans SharedPreferences comme utilisateur connecté
            val editor = sharedPreferences.edit()
            editor.putString("loggedInUser", email)  // Clé pour stocker l'email de l'utilisateur
            editor.apply()

            // Démarrer MainActivity après la connexion réussie
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        } else {
            // Informations incorrectes
            Toast.makeText(this, "Identifiants incorrects", Toast.LENGTH_SHORT).show()
        }
    }


    // Fonction d'inscription
    private fun signUp() {
        // Récupérer les informations entrées par l'utilisateur
        val email = edtEmail.text.toString().lowercase()
        val password = edtPassword.text.toString()
        val name = edtName.text.toString()
        val age = edtAge.text.toString()
        val licenseType = spinnerLicenseType.selectedItem.toString()

        // Validation des champs
        if (email.isEmpty() || password.isEmpty() || name.isEmpty() || age.isEmpty()) {
            Toast.makeText(this, "Veuillez remplir tous les champs", Toast.LENGTH_SHORT).show()
            return
        }

        // Vérifier si l'email existe déjà dans SharedPreferences
        val sharedPreferences = getSharedPreferences("UserPreferences", MODE_PRIVATE)
        val existingEmail = sharedPreferences.getString(email, null)

        if (existingEmail != null) {
            // Si l'email existe déjà
            Toast.makeText(this, "Cet email est déjà utilisé", Toast.LENGTH_SHORT).show()
            return
        }

        // Enregistrer l'utilisateur dans SharedPreferences
        val editor = sharedPreferences.edit()
        editor.putString(email, password)
        editor.putString(email + "_name", name)
        editor.putString(email + "_age", age)
        editor.putString(email + "_email", email)
        editor.putString(email + "_licenseType", licenseType)
        editor.apply()

        Toast.makeText(this, "Inscription réussie !", Toast.LENGTH_SHORT).show()
        switchToSignIn()
    }
}
