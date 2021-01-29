package kr.co.mooreung.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.kakao.sdk.auth.LoginClient
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.user.UserApiClient
import kotlinx.android.synthetic.main.activity_login.*
import kr.co.mooreung.R
import kr.co.mooreung.ServerConnect.ResultTest
import kr.co.mooreung.ServerConnect.URLs
import kr.co.mooreung.ServerConnect.testAPI
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        //로그인 공통 콜백 구성
        val callback: ((OAuthToken?, Throwable?) -> Unit) = { token, error ->
            if (error != null) { //Login Fail
                Log.e(TAG, "Kakao Login Failed :", error)
            } else if (token != null) { //Login Success

                startMainActivity()
            }
        }
        findViewById<ImageView>(R.id.kakao_login_btn).setOnClickListener {
            // 카카오톡이 설치되어 있으면 카카오톡으로 로그인, 아니라면 카카오계정으로 로그인
            LoginClient.instance.run {
                if (isKakaoTalkLoginAvailable(this@LoginActivity)) {
                    loginWithKakaoTalk(this@LoginActivity, callback = callback)
                } else {
                    loginWithKakaoAccount(this@LoginActivity, callback = callback)
                }
            }
        }
        testButton.setOnClickListener {
            val api = testAPI.create()
            api.postTestData("이거 넘어가면 POST 성공").enqueue(object : Callback<ResultTest> {
                override fun onResponse(
                    call: Call<ResultTest>,
                    response: Response<ResultTest>
                ) {
                    Log.d("RESULT", "성공 : ${response.body()}")
                    Log.d("HTTP TEST", "http 프로토콜 성공")
                }
                override fun onFailure(call: Call<ResultTest>, t: Throwable) {
                    // 실패
                    Log.e("HTTP TEST", "FAIL")
                    Log.e("SEE", call.toString())
                }
            })
        }
    }


    private fun startMainActivity() {
        // 사용자 정보 요청 (기본)
        UserApiClient.instance.me { user, error ->
            if (error != null) {
                Log.e(TAG, "사용자 정보 요청 실패", error)
            }
            else if (user != null) {
                Log.e(TAG, URLs.URL_KAKAO_LOGIN)
                Log.i(TAG, "사용자 정보 요청 성공" +
                        "\n회원번호: ${user.id}" +
                        "\n이메일: ${user.kakaoAccount?.email}" +
                        "\n생일: ${user.kakaoAccount?.birthday}" +
                        "\n성별: ${user.kakaoAccount?.gender}")
            }
        }
        startActivity(Intent(this, MainActivity::class.java))
    }

    companion object {
        private const val TAG = "LoginActivity"
    }

}