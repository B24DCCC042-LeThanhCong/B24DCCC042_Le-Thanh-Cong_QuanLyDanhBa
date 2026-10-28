package com.example.quanlydanhbath02

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.CheckBox
import android.widget.TextView
import android.widget.Toast

class ContactDetailActivity : Activity() {

    private var contactIndex = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_contact_detail)

        contactIndex = intent.getIntExtra("index", -1)

        // Kiểm tra liên hệ còn tồn tại không
        if (contactIndex !in ContactRepository.contacts.indices) {
            finish()
            return
        }

        val contact = ContactRepository.contacts[contactIndex]

        // Hiển thị chữ cái đầu làm avatar
        findViewById<TextView>(R.id.txtAvatarDetail).text =
            contact.name.trim()
                .firstOrNull()
                ?.uppercaseChar()
                ?.toString() ?: "?"

        // Hiển thị thông tin liên hệ
        findViewById<TextView>(R.id.txtName).text = contact.name
        findViewById<TextView>(R.id.txtPhone).text = contact.phone
        findViewById<TextView>(R.id.txtGroup).text =
            "Nhóm: ${contact.group}"
        findViewById<TextView>(R.id.txtGender).text =
            "Giới tính: ${contact.gender}"
        findViewById<TextView>(R.id.txtNote).text =
            "Ghi chú: ${contact.note.ifBlank { "Không có" }}"

        // Đánh dấu hoặc bỏ đánh dấu yêu thích
        val chkFavorite =
            findViewById<CheckBox>(R.id.chkFavoriteDetail)

        chkFavorite.isChecked = contact.favorite

        chkFavorite.setOnCheckedChangeListener { _, checked ->
            ContactRepository.contacts
                .getOrNull(contactIndex)
                ?.favorite = checked
        }

        // Sửa liên hệ
        findViewById<Button>(R.id.btnEdit).setOnClickListener {
            val editIntent =
                Intent(this, AddContactActivity::class.java)

            editIntent.putExtra("editIndex", contactIndex)
            startActivity(editIntent)
            finish()
        }

        // Gọi điện: mở trình quay số
        findViewById<Button>(R.id.btnCall).setOnClickListener {
            val dialIntent = Intent(
                Intent.ACTION_DIAL,
                Uri.parse("tel:${contact.phone}")
            )

            startActivity(dialIntent)
        }

        // Xóa liên hệ
        findViewById<Button>(R.id.btnDelete).setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle("Xóa liên hệ")
                .setMessage(
                    "Bạn có chắc muốn xóa ${contact.name}?"
                )
                .setNegativeButton("Hủy", null)
                .setPositiveButton("Xóa") { _, _ ->
                    ContactRepository.contacts.removeAt(contactIndex)

                    Toast.makeText(
                        this,
                        "Đã xóa liên hệ",
                        Toast.LENGTH_SHORT
                    ).show()

                    finish()
                }
                .show()
        }

        // Quay lại danh sách
        // XML mới dùng TextView cho nút quay lại
        findViewById<TextView>(R.id.btnBack).setOnClickListener {
            finish()
        }
    }
}
