package com.numerad.kwicmusic.domain

import com.numerad.kwicmusic.data.models.dtos.PlaylistItemListResponse
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Query

interface AuthenticationService {
//    @GET("https://accounts.google.com/o/oauth2/v2/auth") // old
    @GET("https://www.googleapis.com/oauth2/v4/token") // new; see https://stackoverflow.com/questions/48912655/gmail-api-oauth-error-parameter-not-allowed-for-this-message-type-redirect-uri
    fun getAuth(
        @Query("client_id") clientId: String,
        @Query("response_type") responseType: String,
        @Query("scope") scope: String,
        @Query("redirect_uri") redirectUri: String,
//        @Query("state") state: String,
        @Query("nonce") nonce: String
//        @Query("login_hint") loginHint: String,
//        @Query("hd") hd: Stringw
    ): Single<PlaylistItemListResponse>
}
