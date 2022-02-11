package com.example.part3_chapter6.ChatDetailed

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.part3_chapter6.DBkey.Companion.DB_CHAT
import com.example.part3_chapter6.R
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class ChatRoomActivity : AppCompatActivity() {

    private val auth by lazy {
        Firebase.auth
    }
    private val chatList = mutableListOf<ChatItem>()
    private val adapter = ChatItemAdapter()
    private var chatDB : DatabaseReference? = null

    @SuppressLint("CutPasteId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_room)

        val chatKey = intent.getStringExtra("chatKey")

        chatDB = Firebase.database.reference.child(DB_CHAT).child(chatKey.toString())
        chatDB?.addChildEventListener(object : ChildEventListener{
            @SuppressLint("NotifyDataSetChanged")
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val chatItem =snapshot.getValue(ChatItem::class.java)
                chatItem ?: return
                chatList.add(chatItem)
                adapter.submitList(chatList)
                adapter.notifyDataSetChanged()

            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {}

            override fun onChildRemoved(snapshot: DataSnapshot) {}

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}

            override fun onCancelled(error: DatabaseError) {}

        })

        findViewById<RecyclerView>(R.id.chatRecyclerView).adapter = adapter
        findViewById<RecyclerView>(R.id.chatRecyclerView).layoutManager=LinearLayoutManager(this)
        findViewById<Button>(R.id.sendButton).setOnClickListener {
            val chatItem = ChatItem(
                sendId = auth.currentUser?.uid.orEmpty(),
                message = findViewById<EditText>(R.id.messageEditText).text.toString()
            )
            chatDB?.push()?.setValue(chatItem)
        }


    }
}