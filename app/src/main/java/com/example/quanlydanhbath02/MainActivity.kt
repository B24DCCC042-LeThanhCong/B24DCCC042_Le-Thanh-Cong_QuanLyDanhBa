package com.example.quanlydanhbath02

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*

class MainActivity : Activity() {

    private lateinit var listView: ListView
    private lateinit var txtStats: TextView
    private lateinit var edtSearch: EditText
    private lateinit var contactAdapter: ContactAdapter

    private var displayedContacts: List<Contact> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        listView = findViewById(R.id.listContacts)
        txtStats = findViewById(R.id.txtStats)
        edtSearch = findViewById(R.id.edtSearch)

        contactAdapter = ContactAdapter()
        listView.adapter = contactAdapter

        findViewById<Button>(R.id.btnAdd).setOnClickListener {
            startActivity(Intent(this, AddContactActivity::class.java))
        }

        listView.setOnItemClickListener { _, _, position, _ ->
            val selectedContact = displayedContacts.getOrNull(position)
                ?: return@setOnItemClickListener

            val originalIndex =
                ContactRepository.contacts.indexOf(selectedContact)

            if (originalIndex >= 0) {
                val intent = Intent(this, ContactDetailActivity::class.java)
                intent.putExtra("index", originalIndex)
                startActivity(intent)
            }
        }

        edtSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(
                s: CharSequence?, start: Int, count: Int, after: Int
            ) = Unit

            override fun onTextChanged(
                s: CharSequence?, start: Int, before: Int, count: Int
            ) {
                showContacts()
            }

            override fun afterTextChanged(s: Editable?) = Unit
        })

        showContacts()
    }

    override fun onResume() {
        super.onResume()
        if (::contactAdapter.isInitialized) {
            showContacts()
        }
    }

    private fun showContacts() {
        val allContacts = ContactRepository.contacts
        val keyword = edtSearch.text.toString().trim()

        // Tìm theo tên hoặc số điện thoại.
        displayedContacts = allContacts
            .filter {
                it.name.contains(keyword, ignoreCase = true) ||
                        it.phone.contains(keyword, ignoreCase = true)
            }
            // Yêu thích lên đầu; giữ thứ tự tương đối của các liên hệ khác.
            .sortedByDescending { it.favorite }

        txtStats.text =
            "Tổng: ${allContacts.size} liên hệ  •  " +
                    "Yêu thích: ${allContacts.count { it.favorite }}"

        contactAdapter.notifyDataSetChanged()
    }

    private inner class ContactAdapter : BaseAdapter() {

        override fun getCount(): Int = displayedContacts.size

        override fun getItem(position: Int): Contact =
            displayedContacts[position]

        override fun getItemId(position: Int): Long = position.toLong()

        override fun getView(
            position: Int,
            convertView: View?,
            parent: ViewGroup
        ): View {
            val row = convertView ?: LayoutInflater.from(this@MainActivity)
                .inflate(R.layout.item_contact, parent, false)

            val contact = getItem(position)

            val avatar = row.findViewById<TextView>(R.id.txtAvatar)
            val name = row.findViewById<TextView>(R.id.txtContactName)
            val phone = row.findViewById<TextView>(R.id.txtContactPhone)
            val group = row.findViewById<TextView>(R.id.txtContactGroup)
            val favorite = row.findViewById<TextView>(R.id.txtFavorite)

            name.text = contact.name
            phone.text = contact.phone
            group.text = contact.group

            // Chữ cái đầu trong avatar.
            avatar.text = contact.name.trim()
                .firstOrNull()?.uppercaseChar()?.toString() ?: "?"

            // Màu avatar và nhãn phụ thuộc vào nhóm liên hệ.
            val colors = when (contact.group.trim().lowercase()) {
                "gia đình" -> intArrayOf(
                    Color.rgb(22, 163, 74),
                    Color.rgb(220, 252, 231)
                )
                "bạn bè" -> intArrayOf(
                    Color.rgb(37, 99, 235),
                    Color.rgb(219, 234, 254)
                )
                "công việc" -> intArrayOf(
                    Color.rgb(126, 34, 206),
                    Color.rgb(243, 232, 255)
                )
                else -> intArrayOf(
                    Color.rgb(194, 65, 12),
                    Color.rgb(255, 237, 213)
                )
            }

            val avatarBackground = GradientDrawable().apply {
                shape = GradientDrawable.OVAL
                setColor(colors[0])
            }
            avatar.background = avatarBackground
            avatar.setTextColor(Color.WHITE)

            val groupBackground = GradientDrawable().apply {
                shape = GradientDrawable.RECTANGLE
                cornerRadius = 12f
                setColor(colors[1])
            }
            group.background = groupBackground
            group.setTextColor(colors[0])

            if (contact.favorite) {
                favorite.visibility = View.VISIBLE
                favorite.text = "★"
            } else {
                favorite.visibility = View.GONE
            }

            return row
        }
    }
}
