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

import android.content.Context
import android.content.res.Resources
import android.net.Uri
import androidx.annotation.*
import com.numerad.kwicmusic.R
import net.openid.appauth.AuthorizationServiceConfiguration
import net.openid.appauth.AuthorizationServiceConfiguration.RetrieveConfigurationCallback
import java.util.*

/**
 * An abstraction of identity providers, containing all necessary info for the demo app.
 */
internal class IdentityProvider(
    @NonNull name: String,
    @BoolRes enabledRes: Int,
    @StringRes discoveryEndpointRes: Int,
    @StringRes authEndpointRes: Int,
    @StringRes tokenEndpointRes: Int,
    @StringRes clientIdRes: Int,
    @StringRes clientSecretRes: Int,
    @StringRes redirectUriRes: Int,
    @StringRes scopeRes: Int,
    @DrawableRes buttonImageRes: Int,
    @StringRes buttonContentDescriptionRes: Int
) {
    @NonNull
    val name: String

    @DrawableRes
    val buttonImageRes: Int

    @StringRes
    val buttonContentDescriptionRes: Int

    @BoolRes
    private val mEnabledRes: Int

    @StringRes
    private val mDiscoveryEndpointRes: Int

    @StringRes
    private val mAuthEndpointRes: Int

    @StringRes
    private val mTokenEndpointRes: Int

    @StringRes
    private val mClientIdRes: Int

    @StringRes
    private val mClientSecretRes: Int

    @StringRes
    private val mRedirectUriRes: Int

    @StringRes
    private val mScopeRes: Int
    private var mConfigurationRead = false
    private var mEnabled = false
    private var mDiscoveryEndpoint: Uri? = null
    private var mAuthEndpoint: Uri? = null
    private var mTokenEndpoint: Uri? = null
    private var mClientId: String? = null
    private var mClientSecret: String? = null
    private var mRedirectUri: Uri? = null
    private var mScope: String? = null

    /**
     * This must be called before any of the getters will function.
     */
    fun readConfiguration(context: Context) {
        if (mConfigurationRead) {
            return
        }
        val res = context.resources
        mEnabled = res.getBoolean(mEnabledRes)
        mDiscoveryEndpoint =
            if (isSpecified(mDiscoveryEndpointRes)) getUriResource(
                res,
                mDiscoveryEndpointRes,
                "discoveryEndpointRes"
            ) else null
        mAuthEndpoint =
            if (isSpecified(mAuthEndpointRes)) getUriResource(
                res,
                mAuthEndpointRes,
                "authEndpointRes"
            ) else null
        mTokenEndpoint =
            if (isSpecified(mTokenEndpointRes)) getUriResource(
                res,
                mTokenEndpointRes,
                "tokenEndpointRes"
            ) else null
        mClientId = res.getString(mClientIdRes)
        mClientSecret = if (isSpecified(mClientSecretRes)) res.getString(
            mClientSecretRes
        ) else null
        mRedirectUri =
            getUriResource(res, mRedirectUriRes, "mRedirectUriRes")
        mScope = res.getString(mScopeRes)
        mConfigurationRead = true
    }

    private fun checkConfigurationRead() {
        check(mConfigurationRead) { "Configuration not read" }
    }

    val isEnabled: Boolean
        get() {
            checkConfigurationRead()
            return mEnabled
        }

    @get:Nullable
    val discoveryEndpoint: Uri?
        get() {
            checkConfigurationRead()
            return mDiscoveryEndpoint
        }

    @get:Nullable
    val authEndpoint: Uri?
        get() {
            checkConfigurationRead()
            return mAuthEndpoint
        }

    @get:Nullable
    val tokenEndpoint: Uri?
        get() {
            checkConfigurationRead()
            return mTokenEndpoint
        }

    @get:NonNull
    val clientId: String?
        get() {
            checkConfigurationRead()
            return mClientId
        }

    @get:Nullable
    val clientSecret: String?
        get() {
            checkConfigurationRead()
            return mClientSecret
        }

    @get:NonNull
    val redirectUri: Uri?
        get() {
            checkConfigurationRead()
            return mRedirectUri
        }

    @get:NonNull
    val scope: String?
        get() {
            checkConfigurationRead()
            return mScope
        }

    fun retrieveConfig(context: Context, callback: RetrieveConfigurationCallback) {
        readConfiguration(context)
        if (discoveryEndpoint != null) {
            AuthorizationServiceConfiguration.fetchFromUrl(mDiscoveryEndpoint!!, callback)
        } else {
            val config =
                AuthorizationServiceConfiguration(mAuthEndpoint!!, mTokenEndpoint!!, null)
            callback.onFetchConfigurationCompleted(config, null)
        }
    }

    companion object {
        /**
         * Value used to indicate that a configured property is not specified or required.
         */
        const val NOT_SPECIFIED = -1
        val GOOGLE = IdentityProvider(
            "Google",
            R.bool.google_enabled,
            R.string.google_discovery_uri,
            NOT_SPECIFIED,  // auth endpoint is discovered
            NOT_SPECIFIED,  // token endpoint is discovered
            R.string.google_client_id_oauth,
            NOT_SPECIFIED,  // client secret is not required for Google
            R.string.google_auth_redirect_uri,
            R.string.google_scope_string,
            R.drawable.common_google_signin_btn_icon_dark,
            R.string.google_name
        )
        val PROVIDERS = Arrays.asList(
            GOOGLE
        )

        fun getEnabledProviders(context: Context): List<IdentityProvider> {
            val providers =
                ArrayList<IdentityProvider>()
            for (provider in PROVIDERS) {
                provider.readConfiguration(context)
                if (provider.isEnabled) {
                    providers.add(provider)
                }
            }
            return providers
        }

        private fun isSpecified(value: Int): Boolean {
            return value != NOT_SPECIFIED
        }

        private fun checkSpecified(value: Int, valueName: String): Int {
            require(value != NOT_SPECIFIED) { "$valueName must be specified" }
            return value
        }

        private fun getUriResource(
            res: Resources,
            @StringRes resId: Int,
            resName: String
        ): Uri {
            return Uri.parse(res.getString(resId))
        }
    }

    init {
        require(
            !(!isSpecified(discoveryEndpointRes)
                    && !isSpecified(authEndpointRes)
                    && !isSpecified(tokenEndpointRes))
        ) { "the discovery endpoint or the auth and token endpoints must be specified" }
        this.name = name
        mEnabledRes = checkSpecified(enabledRes, "enabledRes")
        mDiscoveryEndpointRes = discoveryEndpointRes
        mAuthEndpointRes = authEndpointRes
        mTokenEndpointRes = tokenEndpointRes
        mClientIdRes = checkSpecified(clientIdRes, "clientIdRes")
        mClientSecretRes = clientSecretRes
        mRedirectUriRes =
            checkSpecified(redirectUriRes, "redirectUriRes")
        mScopeRes = checkSpecified(scopeRes, "scopeRes")
        this.buttonImageRes =
            checkSpecified(buttonImageRes, "buttonImageRes")
        this.buttonContentDescriptionRes = checkSpecified(
            buttonContentDescriptionRes,
            "buttonContentDescriptionRes"
        )
    }
}