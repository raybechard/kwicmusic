package com.numerad.kwicmusic.ui.login

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.numerad.kwicmusic.R
import com.numerad.kwicmusic.SessionManager
import com.numerad.kwicmusic.auth.IdentityProvider
import com.numerad.kwicmusic.auth.TokenActivity
import com.numerad.kwicmusic.databinding.ActivityLoginBinding
import com.numerad.kwicmusic.domain.AuthenticationService
import com.numerad.kwicmusic.ui.main.MainActivity
import kotlinx.android.synthetic.main.activity_login.*
import net.openid.appauth.*
import net.openid.appauth.AuthorizationServiceConfiguration.RetrieveConfigurationCallback
import org.koin.core.KoinComponent
import org.koin.core.inject
import timber.log.Timber

class LoginActivity : BaseActivity(), View.OnClickListener, KoinComponent {

    lateinit var mAuthService: AuthorizationService
    private var googleSignInClient: GoogleSignInClient? = null
    private lateinit var loginViewModel: LoginViewModel
    private lateinit var auth: FirebaseAuth
    lateinit var binding: ActivityLoginBinding
    private val authenticationService: AuthenticationService by inject()
    private val sessionManager: SessionManager by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_login)

        val username = findViewById<EditText>(R.id.username)
        val password = findViewById<EditText>(R.id.password)
        val login = findViewById<Button>(R.id.login)
        val loading = findViewById<ProgressBar>(R.id.loading)

        loginViewModel = ViewModelProvider(this, LoginViewModelFactory())
            .get(LoginViewModel::class.java)

        loginViewModel.loginFormState.observe(this@LoginActivity, Observer {
            val loginState = it ?: return@Observer
            login.isEnabled = loginState.isDataValid

            if (loginState.usernameError != null) {
                username.error = getString(loginState.usernameError)
            }
            if (loginState.passwordError != null) {
                password.error = getString(loginState.passwordError)
            }
        })

        loginViewModel.loginResult.observe(this@LoginActivity, Observer {
            val loginResult = it ?: return@Observer

            loading.visibility = View.GONE
            if (loginResult.error != null) {
                showLoginFailed(loginResult.error)
            }
            if (loginResult.success != null) {
                updateUiWithUser(loginResult.success)
            }
            setResult(Activity.RESULT_OK)
            finish()
        })

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.google_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)

        sign_in_button.setOnClickListener(this)

        auth = Firebase.auth

        mAuthService = AuthorizationService(this)

//        binding = DataBindingUtil.setContentView(this, R.layout.activity_login)
//        binding.lifecycleOwner = this.viewLifecycleOwner
//        binding.viewmodel = loginViewModel
    }

    override fun onStart() {
        super.onStart()

        val account = GoogleSignIn.getLastSignedInAccount(this)
        if (account != null)
            updateUiWithUser(account.toLoggedInUserView())
        else
            Timber.d("User already signed-in")
    }

    private fun updateUiWithUser(model: LoggedInUserView) {
        val welcome = getString(R.string.welcome)
        val displayName = model.displayName

//        val intent = Intent(this, MainActivity::class.java)
//        startActivity(intent)
//        finish()

        Toast.makeText(applicationContext, "$welcome $displayName", Toast.LENGTH_LONG).show()
    }

    private fun showLoginFailed(@StringRes errorString: Int) {
        Toast.makeText(applicationContext, errorString, Toast.LENGTH_SHORT).show()
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.sign_in_button -> signIn()
        }
    }

    private fun signIn() {
        val signInIntent: Intent = googleSignInClient?.signInIntent ?: return
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                // Google Sign In was successful, authenticate with Firebase
                val account = task.getResult(ApiException::class.java)!!
                Timber.tag(TAG).d("firebaseAuthWithGoogle:%s", account.id)

                firebaseAuthWithGoogle(account.idToken!!)
//                handleSignInResult(task)
            } catch (e: ApiException) {
                // Google Sign In failed, update UI appropriately
                Timber.tag(TAG).e(e, e.localizedMessage)
                // ...
            }
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        showProgressBar()
        val credential = GoogleAuthProvider.getCredential(idToken, null)

        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Timber.d("signInWithCredential:success")
                    val user = auth.currentUser
//                    updateUI(user)

                    // todo ray how to get oauth2 access token here?
//                    val vv = authenticationService.getAuth(
//                        getString(R.string.server_client_id),
//                        "code",
//                        "openid email",
//                        "uri",
//                        generateNonce(15))
//                    getConfig()
//                    sessionManager.saveAuthToken(token)

                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    // If sign in fails, display a message to the user.
                    Timber.tag(TAG).w(task.exception, "signInWithCredential:failure")
//                    val view = binding.container
                    Snackbar.make(container, "Authentication Failed.", Snackbar.LENGTH_SHORT).show()
//                    updateUI(null)
                }

                hideProgressBar()
            }
    }

    private fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            val account = completedTask.getResult(ApiException::class.java)

            // Signed in successfully, show authenticated UI.
            if (account != null)
                updateUiWithUser(account.toLoggedInUserView())
            else
                Timber.e("Can't sign in")

        } catch (e: ApiException) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Timber.tag(TAG).w("signInResult:failed code=%s", e.statusCode)
//            updateUiWithUser(null)
        }
    }

    private fun updateUI(user: FirebaseUser?) {
        hideProgressBar()
        if (user != null) {
//            binding.status.text = getString(R.string.google_status_fmt, user.email)
//            binding.detail.text = getString(R.string.firebase_status_fmt, user.uid)

            binding.signInButton.visibility = View.GONE
//            binding.signOutAndDisconnect.visibility = View.VISIBLE
        } else {
//            binding.status.setText(R.string.signed_out)
//            binding.detail.text = null

            binding.signInButton.visibility = View.VISIBLE
//            binding.signOutAndDisconnect.visibility = View.GONE
        }
    }

    companion object {
        private const val TAG = "LoginActivity"
        private const val RC_SIGN_IN = 9001
    }

    fun generateNonce(length: Int): String {
        val allowedChars = ('A'..'Z') + ('a'..'z') + ('0'..'9')
        return (1..length)
            .map { allowedChars.random() }
            .joinToString("")
    }

    private fun getConfig() {
        val providers: List<IdentityProvider> =
            IdentityProvider.getEnabledProviders(this)

        for (idp in providers) {
            val retrieveCallback =
                RetrieveConfigurationCallback { serviceConfiguration: AuthorizationServiceConfiguration?, ex: AuthorizationException? ->
                    if (ex != null) {
                        Timber.tag(TAG).w(ex, ex.localizedMessage)
                    } else {
                        Timber.tag(TAG).d("configuration retrieved for %s", idp.name)

                        if (serviceConfiguration != null)
                            makeAuthRequest(serviceConfiguration, idp)
                        else
                            Timber.tag(TAG).e("serviceConfiguration is null")
                    }
                }

//            val idpButton = Button(this, null, R.style.Widget_AppCompat_Button_Borderless)
//            idpButton.setBackgroundResource(idp.buttonImageRes)
//            idpButton.contentDescription = resources.getString(idp.buttonContentDescriptionRes)
//            idpButton.width = resources.getDimensionPixelSize(R.dimen.idp_button_edge_size)
//            idpButton.height = resources.getDimensionPixelSize(R.dimen.idp_button_edge_size)
//            idpButton.setOnClickListener {
//                Log.d(MainActivity.TAG, "initiating auth for " + idp.name)
//                idp.retrieveConfig(this@MainActivity, retrieveCallback)
//            }
//            idpButtonContainer.addView(idpButton)

            idp.retrieveConfig(this, retrieveCallback)
        }
    }

    private fun makeAuthRequest(
        serviceConfig: AuthorizationServiceConfiguration,
        idp: IdentityProvider
    ) {
        val authRequest = AuthorizationRequest.Builder(
            serviceConfig,
            idp.clientId ?: "unknown",
            ResponseTypeValues.CODE,
            idp.redirectUri ?: Uri.EMPTY
        )
            .setScope(idp.scope)
            .build()

        Timber.tag(TAG).d("Making auth request to %s", idp.name)

        mAuthService.performAuthorizationRequest(
            authRequest,
            TokenActivity.createPostAuthorizationIntent(
                this,
                authRequest,
                serviceConfig.discoveryDoc,
                idp.clientSecret
            ),
            mAuthService.createCustomTabsIntentBuilder()
//                .setToolbarColor(getCustomTabColor())
                .build()
        )
    }
}

fun EditText.afterTextChanged(afterTextChanged: (String) -> Unit) {
    this.addTextChangedListener(object : TextWatcher {
        override fun afterTextChanged(editable: Editable?) {
            afterTextChanged.invoke(editable.toString())
        }

        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
    })
}

fun GoogleSignInAccount.toLoggedInUserView() = LoggedInUserView("unknown").also {
    it.displayName = displayName ?: "unknown"
}