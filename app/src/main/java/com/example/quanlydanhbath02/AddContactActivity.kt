package com.example.quanlydanhbath02

import android.app.Activity
import android.os.Bundle
import android.view.View
import android.widget.*

class AddContactActivity : Activity() {

    private var editIndex = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_contact)

        editIndex = intent.getIntExtra("editIndex", -1)

        val edtName = findViewById<EditText>(R.id.edtName)
        val edtPhone = findViewById<EditText>(R.id.edtPhone)
        val spGroup = findViewById<Spinner>(R.id.spGroup)
        val rgGender = findViewById<RadioGroup>(R.id.rgGender)
        val chkFavorite = findViewById<CheckBox>(R.id.chkFavorite)
        val edtNote = findViewById<EditText>(R.id.edtNote)
        val txtError = findViewById<TextView>(R.id.txtError)
        val txtFormTitle = findViewById<TextView>(R.id.txtFormTitle)
        val btnSave = findViewById<Button>(R.id.btnSave)
        val btnCancel = findViewById<Button>(R.id.btnCancel)

        val groups = listOf("Bạn bè", "Gia đình", "Công việc", "Khác")

        spGroup.adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_dropdown_item,
            groups
        )

        // Nếu đang sửa, hiển thị dữ liệu của liên hệ hiện tại.
        if (editIndex in ContactRepository.contacts.indices) {
            val contact = ContactRepository.contacts[editIndex]

            txtFormTitle.text = "Chỉnh sửa liên hệ"
            btnSave.text = "Lưu thay đổi"

            edtName.setText(contact.name)
            edtPhone.setText(contact.phone)
            edtNote.setText(contact.note)
            chkFavorite.isChecked = contact.favorite

            spGroup.setSelection(
                groups.indexOf(contact.group).coerceAtLeast(0)
            )

            when (contact.gender) {
                "Nam" -> rgGender.check(R.id.rbMale)
                "Nữ" -> rgGender.check(R.id.rbFemale)
                else -> rgGender.clearCheck()
            }
        } else {
            txtFormTitle.text = "Thêm liên hệ mới"
            btnSave.text = "Lưu liên hệ"
        }

        btnSave.setOnClickListener {
            val name = edtName.text.toString().trim()
            val phone = edtPhone.text.toString().trim()

            val gender = when (rgGender.checkedRadioButtonId) {
                R.id.rbMale -> "Nam"
                R.id.rbFemale -> "Nữ"
                else -> ""
            }

            // Kiểm tra dữ liệu nhập.
            when {
                name.isEmpty() || phone.isEmpty() -> {
                    txtError.text =
                        "Vui lòng nhập họ tên và số điện thoại"
                    txtError.visibility = View.VISIBLE
                }

                !phone.matches(Regex("^0[0-9]{9}$")) -> {
                    txtError.text =
                        "Số điện thoại phải có 10 chữ số và bắt đầu bằng 0"
                    txtError.visibility = View.VISIBLE
                }

                ContactRepository.contacts.withIndex().any {
                    it.index != editIndex && it.value.phone == phone
                } -> {
                    txtError.text = "Số điện thoại đã tồn tại"
                    txtError.visibility = View.VISIBLE
                }

                else -> {
                    val selectedGroup = spGroup.selectedItem.toString()
                    val note = edtNote.text.toString().trim()

                    if (editIndex in ContactRepository.contacts.indices) {
                        // Cập nhật liên hệ đã có.
                        val contact = ContactRepository.contacts[editIndex]

                        contact.name = name
                        contact.phone = phone
                        contact.group = selectedGroup
                        contact.gender = gender
                        contact.favorite = chkFavorite.isChecked
                        contact.note = note

                        Toast.makeText(
                            this,
                            "Đã cập nhật liên hệ",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        // Thêm liên hệ mới.
                        ContactRepository.contacts.add(
                            Contact(
                                name = name,
                                phone = phone,
                                group = selectedGroup,
                                gender = gender,
                                favorite = chkFavorite.isChecked,
                                note = note
                            )
                        )

                        Toast.makeText(
                            this,
                            "Đã thêm liên hệ",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                    finish()
                }
            }
        }

        btnCancel.setOnClickListener {
            finish()
        }
    }
}
