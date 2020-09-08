package com.numerad.kwicmusic.ui.login

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
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
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.numerad.kwicmusic.R
import com.numerad.kwicmusic.SessionManager
import com.numerad.kwicmusic.auth.IdentityProvider
import com.numerad.kwicmusic.auth.IdentityProvider.Companion.GOOGLE
import com.numerad.kwicmusic.auth.TokenActivity
import com.numerad.kwicmusic.ui.main.MainActivity
import kotlinx.android.synthetic.main.activity_login.*
import net.openid.appauth.*
import net.openid.appauth.AuthorizationServiceConfiguration.RetrieveConfigurationCallback
import org.koin.core.KoinComponent
import org.koin.core.inject
import timber.log.Timber

class LoginActivity : BaseActivity(), View.OnClickListener, KoinComponent {

    private var googleSignInClient: GoogleSignInClient? = null
    private lateinit var loginViewModel: LoginViewModel
    private lateinit var auth: FirebaseAuth
    lateinit var authService: AuthorizationService
    private val sessionManager: SessionManager by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        loginViewModel = ViewModelProvider(this, LoginViewModelFactory())
            .get(LoginViewModel::class.java)

        loginViewModel.loginResult.observe(this, Observer {
            val loginResult = it ?: return@Observer

            loading.visibility = View.GONE
            if (loginResult.error != null) {
                Toast.makeText(applicationContext, loginResult.error, Toast.LENGTH_SHORT).show()
            }
            if (loginResult.success != null) {
                updateUiWithUser(loginResult.success)
            }
            setResult(Activity.RESULT_OK)
            finish()
        })

        sign_in_button.setOnClickListener(this)

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.google_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)
        auth = Firebase.auth
        authService = AuthorizationService(this)
    }

    override fun onStart() {
        super.onStart()

        val account = GoogleSignIn.getLastSignedInAccount(this)
        if (account != null)
            updateUiWithUser(account.toLoggedInUserView())
        else
            Timber.d("Already signed-in")
    }

    private fun updateUiWithUser(model: LoggedInUserView) {
        val welcome = getString(R.string.welcome)
        val displayName = model.displayName
        Toast.makeText(applicationContext, "$welcome $displayName", Toast.LENGTH_LONG).show()
        gotoMainActivity(model.displayName)
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.sign_in_button -> {
//                loginViewModel.login(username.text.toString(), password.text.toString())
                val signInIntent: Intent = googleSignInClient?.signInIntent ?: return
                startActivityForResult(signInIntent, RC_SIGN_IN)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                // Google Sign In was successful, authenticate with Firebase
                val account = task.getResult(ApiException::class.java)

                if (account == null) {
                    Timber.tag(TAG).e("account == null")
                    Snackbar.make(container, "account == null", Snackbar.LENGTH_SHORT).show()
                    return
                }

                firebaseAuthWithGoogle(account.idToken!!)
//                handleSignInResult(task)
            } catch (e: ApiException) {
                Timber.tag(TAG).e(e, e.localizedMessage)
                Snackbar.make(container, "Google Sign In failed", Snackbar.LENGTH_SHORT).show()
            }
        }
    }

    private fun gotoMainActivity(userName: String) {

        val bundle = Bundle().apply {
            putString(ARG_USER_NAME, userName)
        }

        val intent = Intent(this, MainActivity::class.java)
        intent.putExtras(bundle)
        startActivity(intent)
        finish()
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        showProgressBar()
        val credential = GoogleAuthProvider.getCredential(idToken, null)

        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    getConfig()
//                    sessionManager.saveAuthToken(token)
                    gotoMainActivity(user?.displayName ?: "")
                } else {
                    Timber.tag(TAG).e(task.exception, "FirebaseAuth.signInWithCredential failure")
                    Snackbar.make(container, "Authentication Failed", Snackbar.LENGTH_SHORT).show()
                }

                hideProgressBar()
            }
    }

    private fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            val account = completedTask.getResult(ApiException::class.java)
            if (account != null)
                updateUiWithUser(account.toLoggedInUserView())
            else
                Timber.e("Can't sign in")

        } catch (e: ApiException) {
            Timber.tag(TAG).e("signInResult:failed code=%s", e.statusCode)
            Snackbar.make(container, "Sign in Failed", Snackbar.LENGTH_SHORT).show()
        }
    }

    private fun getConfig() {
        GOOGLE.retrieveConfig(
            this,
            RetrieveConfigurationCallback { config: AuthorizationServiceConfiguration?, ex: AuthorizationException? ->
                when {
                    ex != null -> Timber.tag(TAG).w(ex, ex.localizedMessage)
                    config == null -> Timber.tag(TAG).e("AuthorizationServiceConfiguration is null")
                    else -> makeAuthRequest(config, GOOGLE)
                }
            })
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

        authService.performAuthorizationRequest(
            authRequest,
            TokenActivity.createPostAuthorizationIntent(
                this,
                authRequest,
                serviceConfig.discoveryDoc,
                idp.clientSecret
            ),
            authService.createCustomTabsIntentBuilder()
//                .setToolbarColor(getCustomTabColor())
                .build()
        )
    }

    private fun GoogleSignInAccount.toLoggedInUserView() = LoggedInUserView("unknown").also {
        it.displayName = displayName ?: "unknown"
    }

    companion object {
        private const val TAG = "LoginActivity"
        private const val RC_SIGN_IN = 9001
        const val ARG_USER_NAME = "ARG_USER_NAME"
    }
}