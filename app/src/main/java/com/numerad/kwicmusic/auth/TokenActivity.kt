/*
 * Copyright 2015 The AppAuth Authors. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.numerad.kwicmusic.auth

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.AsyncTask
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.annotation.MainThread
import androidx.annotation.Nullable
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import com.google.android.material.snackbar.Snackbar
import com.numerad.kwicmusic.R
import com.squareup.picasso.Picasso
import com.squareup.picasso.Picasso.LoadedFrom
import com.squareup.picasso.Target
import net.openid.appauth.*
import net.openid.appauth.AuthState.AuthStateAction
import org.json.JSONException
import org.json.JSONObject
import timber.log.Timber
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.URL
import java.text.DateFormat
import java.util.*

/**
 * Handles token exchange after user authorization.
 */
class TokenActivity : AppCompatActivity() {
    private var mAuthState: AuthState? = null
    private var mAuthService: AuthorizationService? = null
    private var mUserInfoJson: JSONObject? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

//        setContentView(R.layout.activity_token)
        mAuthService = AuthorizationService(this)

        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey(KEY_AUTH_STATE)) {
                try {
                    mAuthState =
                        AuthState.jsonDeserialize(savedInstanceState.getString(KEY_AUTH_STATE)!!)
                } catch (ex: JSONException) {
                    Timber.tag(TAG).e("Malformed authorization JSON saved %s", ex)
                }
            }
            if (savedInstanceState.containsKey(KEY_USER_INFO)) {
                try {
                    mUserInfoJson = JSONObject(savedInstanceState.getString(KEY_USER_INFO))
                } catch (ex: JSONException) {
                    Timber.tag(TAG).e("Failed to parse saved user info JSON %s", ex)
                }
            }
        }

        if (mAuthState == null) {
            val response = AuthorizationResponse.fromIntent(intent)
            val ex = AuthorizationException.fromIntent(intent)
            mAuthState = AuthState(response, ex)

            if (response != null) {
                Timber.d("Received AuthorizationResponse.")
//                showSnackbar(R.string.exchange_notification)
                exchangeAuthorizationCode(response)
            } else {
                Timber.tag(TAG).i("Authorization failed: $ex")
//                showSnackbar(R.string.authorization_failed)
            }
        }
        refreshUi()
    }

    override fun onSaveInstanceState(state: Bundle) {
        super.onSaveInstanceState(state)

        if (mAuthState != null) {
            state.putString(
                KEY_AUTH_STATE,
                mAuthState!!.jsonSerializeString()
            )
        }
        if (mUserInfoJson != null) {
            state.putString(KEY_USER_INFO, mUserInfoJson.toString())
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mAuthService!!.dispose()
    }

    private fun receivedTokenResponse(
        @Nullable tokenResponse: TokenResponse?,
        @Nullable authException: AuthorizationException?
    ) {
        Log.d(TAG, "Token request complete")
        mAuthState!!.update(tokenResponse, authException)
//        showSnackbar(if (tokenResponse != null) R.string.exchange_complete else R.string.refresh_failed)
        refreshUi()
    }

    private fun refreshUi() {
        val refreshTokenInfoView = findViewById(R.id.refresh_token_info) as TextView
        val accessTokenInfoView = findViewById(R.id.access_token_info) as TextView
        val idTokenInfoView = findViewById(R.id.id_token_info) as TextView
        val refreshTokenButton = findViewById(R.id.refresh_token) as Button

        if (mAuthState!!.isAuthorized) {
            refreshTokenInfoView.setText(if (mAuthState!!.refreshToken == null) R.string.no_refresh_token_returned else R.string.refresh_token_returned)
            idTokenInfoView.setText(if (mAuthState!!.idToken == null) R.string.no_id_token_returned else R.string.id_token_returned)
            if (mAuthState!!.accessToken == null) {
                accessTokenInfoView.setText(R.string.no_access_token_returned)
            } else {
                val expiresAt = mAuthState!!.accessTokenExpirationTime
                val expiryStr: String
                expiryStr = if (expiresAt == null) {
                    getResources().getString(R.string.unknown_expiry)
                } else {
                    DateFormat.getDateTimeInstance(DateFormat.FULL, DateFormat.FULL)
                        .format(Date(expiresAt))
                }
                val tokenInfo: String = java.lang.String.format(
                    getResources().getString(R.string.access_token_expires_at),
                    expiryStr
                )
                accessTokenInfoView.text = tokenInfo
            }
        }

        refreshTokenButton.visibility =
            if (mAuthState!!.refreshToken != null) View.VISIBLE else View.GONE
        refreshTokenButton.setOnClickListener { refreshAccessToken() }
        val viewProfileButton = findViewById(R.id.view_profile) as Button
        val discoveryDoc = getDiscoveryDocFromIntent(getIntent())

        if (!mAuthState!!.isAuthorized
            || discoveryDoc == null || discoveryDoc.userinfoEndpoint == null
        ) {
            viewProfileButton.visibility = View.GONE
        } else {
            viewProfileButton.visibility = View.VISIBLE
            viewProfileButton.setOnClickListener {
                object : AsyncTask<Void?, Void?, Void?>() {
                    override fun doInBackground(vararg p0: Void?): Void? {
                        fetchUserInfo()
                        return null
                    }
                }.execute()
            }
        }
        val userInfoCard: View = findViewById(R.id.userinfo_card)
        if (mUserInfoJson == null) {
            userInfoCard.visibility = View.INVISIBLE
        } else {
            try {
                var name: String? = "???"
                if (mUserInfoJson!!.has("name")) {
                    name = mUserInfoJson!!.getString("name")
                }
                val userHeader = findViewById<TextView>(R.id.userinfo_name)
                userHeader.text = name
                if (mUserInfoJson!!.has("picture")) {
                    val profilePictureSize: Int =
                        getResources().getDimensionPixelSize(R.dimen.profile_pic_size)
                    Picasso.with(this@TokenActivity)
                        .load(Uri.parse(mUserInfoJson!!.getString("picture")))
                        .resize(profilePictureSize, profilePictureSize)
                        .into(UserProfilePictureTarget())
                }
                (findViewById<TextView>(R.id.userinfo_json)).text = mUserInfoJson!!.toString(2)
                userInfoCard.visibility = View.VISIBLE
            } catch (ex: JSONException) {
                Log.e(TAG, "Failed to read userinfo JSON", ex)
            }
        }
    }

    private fun refreshAccessToken() {
        val additionalParams =
            HashMap<String, String?>()
        if (getClientSecretFromIntent(getIntent()) != null) {
            additionalParams["client_secret"] = getClientSecretFromIntent(getIntent())
        }
        performTokenRequest(mAuthState!!.createTokenRefreshRequest(additionalParams))
    }

    private fun exchangeAuthorizationCode(authorizationResponse: AuthorizationResponse) {
        val additionalParams = HashMap<String, String?>()

        if (getClientSecretFromIntent(intent) != null) {
            additionalParams["client_secret"] = getClientSecretFromIntent(intent)
        }

        performTokenRequest(authorizationResponse.createTokenExchangeRequest(additionalParams))
    }

    private fun performTokenRequest(request: TokenRequest) {
        mAuthService!!.performTokenRequest(request) { tokenResponse, ex ->
            receivedTokenResponse(tokenResponse, ex)
        }
    }

    private fun fetchUserInfo() {
        if (mAuthState!!.authorizationServiceConfiguration == null) {
            Log.e(TAG, "Cannot make userInfo request without service configuration")
        }
        mAuthState!!.performActionWithFreshTokens(
            mAuthService!!,
            AuthStateAction { accessToken, idToken, ex ->
                if (ex != null) {
                    Log.e(TAG, "Token refresh failed when fetching user info")
                    return@AuthStateAction
                }
                val discoveryDoc =
                    getDiscoveryDocFromIntent(getIntent())
                        ?: throw IllegalStateException("no available discovery doc")
                val userInfoEndpoint: URL
                userInfoEndpoint = try {
                    URL(discoveryDoc.userinfoEndpoint.toString())
                } catch (urlEx: MalformedURLException) {
                    Log.e(TAG, "Failed to construct user info endpoint URL", urlEx)
                    return@AuthStateAction
                }
                var userInfoResponse: InputStream? = null
                try {
                    val conn =
                        userInfoEndpoint.openConnection() as HttpURLConnection
                    conn.setRequestProperty("Authorization", "Bearer $accessToken")
                    conn.instanceFollowRedirects = false
                    userInfoResponse = conn.inputStream
                    val response =
                        readStream(userInfoResponse)
                    updateUserInfo(JSONObject(response))
                } catch (ioEx: IOException) {
                    Log.e(TAG, "Network error when querying userinfo endpoint", ioEx)
                } catch (jsonEx: JSONException) {
                    Log.e(TAG, "Failed to parse userinfo response")
                } finally {
                    if (userInfoResponse != null) {
                        try {
                            userInfoResponse.close()
                        } catch (ioEx: IOException) {
                            Log.e(TAG, "Failed to close userinfo response stream", ioEx)
                        }
                    }
                }
            })
    }

    private fun updateUserInfo(jsonObject: JSONObject) {
        Handler(Looper.getMainLooper()).post {
            mUserInfoJson = jsonObject
            refreshUi()
        }
    }

    @MainThread
    private fun showSnackbar(@StringRes messageId: Int) {
        Snackbar.make(
            findViewById(R.id.coordinator),
            getResources().getString(messageId),
            Snackbar.LENGTH_SHORT
        )
            .show()
    }

    private inner class UserProfilePictureTarget : Target {
        override fun onBitmapLoaded(bitmap: Bitmap, from: LoadedFrom) {
            val image = BitmapDrawable(getResources(), bitmap)
            val userNameView = findViewById(R.id.userinfo_name) as TextView
            if (ViewCompat.getLayoutDirection(userNameView) === ViewCompat.LAYOUT_DIRECTION_LTR) {
                userNameView.setCompoundDrawablesWithIntrinsicBounds(image, null, null, null)
            } else {
                userNameView.setCompoundDrawablesWithIntrinsicBounds(null, null, image, null)
            }
        }

        override fun onBitmapFailed(errorDrawable: Drawable) {}
        override fun onPrepareLoad(placeHolderDrawable: Drawable) {}
    }

    companion object {
        private const val TAG = "TokenActivity"
        private const val KEY_AUTH_STATE = "authState"
        private const val KEY_USER_INFO = "userInfo"
        private const val EXTRA_AUTH_SERVICE_DISCOVERY = "authServiceDiscovery"
        private const val EXTRA_CLIENT_SECRET = "clientSecret"
        private const val BUFFER_SIZE = 1024

        @Throws(IOException::class)
        private fun readStream(stream: InputStream?): String {
            val br = BufferedReader(InputStreamReader(stream))
            val buffer = CharArray(BUFFER_SIZE)
            val sb = StringBuilder()
            var readCount: Int
            while (br.read(buffer).also { readCount = it } != -1) {
                sb.append(buffer, 0, readCount)
            }
            return sb.toString()
        }

        fun createPostAuthorizationIntent(
            context: Context?,
            request: AuthorizationRequest,
            discoveryDoc: AuthorizationServiceDiscovery?,
            clientSecret: String?
        ): PendingIntent {
            val intent = Intent(context, TokenActivity::class.java)
            if (discoveryDoc != null) {
                intent.putExtra(EXTRA_AUTH_SERVICE_DISCOVERY, discoveryDoc.docJson.toString())
            }
            if (clientSecret != null) {
                intent.putExtra(EXTRA_CLIENT_SECRET, clientSecret)
            }

            return PendingIntent.getActivity(context, request.hashCode(), intent, 0)
        }

        fun getDiscoveryDocFromIntent(intent: Intent): AuthorizationServiceDiscovery? {
            if (!intent.hasExtra(EXTRA_AUTH_SERVICE_DISCOVERY)) {
                return null
            }

            val discoveryJson = intent.getStringExtra(EXTRA_AUTH_SERVICE_DISCOVERY)

            return try {
                AuthorizationServiceDiscovery(JSONObject(discoveryJson))
            } catch (ex: JSONException) {
                throw IllegalStateException("Malformed JSON in discovery doc")
            } catch (ex: AuthorizationServiceDiscovery.MissingArgumentException) {
                throw IllegalStateException("Malformed JSON in discovery doc")
            }
        }

        fun getClientSecretFromIntent(intent: Intent): String? {
            return if (!intent.hasExtra(EXTRA_CLIENT_SECRET))
                null
            else
                intent.getStringExtra(EXTRA_CLIENT_SECRET)
        }
    }
}