package com.example.lastommg.Login

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Base64
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.lastommg.MainActivity
import com.example.lastommg.databinding.ActivityLoginBinding
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.TaskCompletionSource
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.kakao.auth.AuthType
import com.kakao.auth.Session
import com.kakao.sdk.common.util.Utility
import org.json.JSONObject


open class LoginActivity1 : AppCompatActivity() {
    private var _binding: ActivityLoginBinding? = null
    private val binding get() = _binding!!
    private val TAG: String = "로그"
    private lateinit var callback : SessionCallback
    private lateinit var fbAuth : FirebaseAuth
    var kakao_name: String? = null
    var kakao_uid: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        _binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        Log.d (TAG, "LoginActivity - onCreate() called")
        fbAuth = Firebase.auth
        callback = SessionCallback(this)
        kakaoLoginStart()
        Log.d(TAG, "LoginActivity - u1 called")


    }

    public override fun onStart() {
        super.onStart()
        Log . d (TAG, "LoginActivity - onStart() called")
    }

    private fun kakaoLoginStart() {
        Log.d(TAG, "LoginActivity - kakaoLoginStart() called")
        val keyHash = Utility.getKeyHash(this)
        Log . d (TAG, "KEY_HASH : $keyHash")
        Session.getCurrentSession().addCallback(callback)
        Session.getCurrentSession().open(AuthType.KAKAO_LOGIN_ALL, this)
    }
    open fun getFirebaseJwt(kakaoAccessToken: String): Task<String> {
        Log.d(TAG, "LoginActivity - getFirebaseJwt() called")
        val source = TaskCompletionSource<String>()
        val queue = Volley.newRequestQueue(this)
        val url = "http://10.0.2.2:8000/verifyToken"
        val validationObject: HashMap<String?, String?> = HashMap()
        validationObject ["token"] = kakaoAccessToken

        val request: JsonObjectRequest = object : JsonObjectRequest(
            Request.Method.POST, url,
            JSONObject(validationObject as Map<*, *>),
            Response.Listener { response ->
                try {
                    val firebaseToken = response.getString("firebase_token")
                    source . setResult (firebaseToken)
                } catch (e: Exception) {
                    source.setException(e)
                }
            },
            Response.ErrorListener { error ->
                Log.e(TAG, error.toString())
                source . setException (error)
            }) {
            override fun getParams(): Map<String, String> {
                val params: MutableMap<String, String> = HashMap()
                params ["Authorization"] = String.format("Basic %s", Base64.encodeToString(
                    String.format("%s:%s", "token", kakaoAccessToken)
                        .toByteArray(), Base64.DEFAULT
                )
                )
                return params
            }
        }
        queue . add (request)
        return source.task
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        Log.d(TAG, "LoginActivity - onActivityResult() called")
        if (Session.getCurrentSession()
                .handleActivityResult(requestCode, resultCode, data)
        ) {
            Log.i(TAG, "Session get current session")
            return
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    fun startMainActivity() {
        Log.d(TAG, "LoginActivity - startMainActivity() called")
        val intent = Intent(this, KakaoUserInfo::class.java)
        startActivity (intent)
        finish ()
    }
    @SuppressLint("ResourceType")
    fun setProfile(){
        val user = Firebase.auth.currentUser

        user?.let {

            kakao_name = user.displayName
            kakao_uid = user.uid
            Log.d("카톡유아이디", kakao_uid!!)

        }

    }
    override fun onDestroy() {
        super.onDestroy()
        Session . getCurrentSession ().removeCallback(callback)
    }
}

