
package com.example.quanlydanhbath02

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView

class LoginActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val user = findViewById<EditText>(R.id.edtUsername)
        val pass = findViewById<EditText>(R.id.edtPassword)
        val error = findViewById<TextView>(R.id.txtError)

        findViewById<Button>(R.id.btnLogin).setOnClickListener {
            val u = user.text.toString().trim()
            val p = pass.text.toString()

            if (u.isEmpty() || p.isEmpty()) {
                error.text = "Vui lòng nhập đầy đủ thông tin"
                error.visibility = View.VISIBLE
            } else if (u == "admin" && p == "123456") {
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            } else {
                error.text = "Sai tên đăng nhập hoặc mật khẩu"
                error.visibility = View.VISIBLE
            }
        }
    }
}
