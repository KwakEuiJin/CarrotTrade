package com.example.part3_chapter6.ChatList

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.part3_chapter6.ChatDetailed.ChatRoomActivity
import com.example.part3_chapter6.DBkey.Companion.CHILD_CHAT
import com.example.part3_chapter6.DBkey.Companion.DB_USERS
import com.example.part3_chapter6.R
import com.example.part3_chapter6.databinding.FragmentChatListBinding
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class ChatListFragment : Fragment(R.layout.fragment_chat_list) {

    private lateinit var chatDB: DatabaseReference
    private var binding:FragmentChatListBinding? = null
    private lateinit var chatListAdapter:ChatListAdapter
    private val auth by lazy {
        Firebase.auth
    }
    private val chatRoomList = mutableListOf<ChatListItem>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val fragmentChatListBinding = FragmentChatListBinding.bind(view)

        binding = fragmentChatListBinding
        chatRoomList.clear()

        chatListAdapter = ChatListAdapter(onItemClicked = {chatListItem ->
            //채팅방으로 이동하는 코드
            context?.let {
                val intent = Intent(it,ChatRoomActivity::class.java)
                intent.putExtra("chatKey",chatListItem.key)
                startActivity(intent)
            }

        })
        fragmentChatListBinding.chatListRecyclerView.adapter = chatListAdapter
        fragmentChatListBinding.chatListRecyclerView.layoutManager = LinearLayoutManager(context)

       if (auth.currentUser==null){
           //예외처리
           return
       }
        chatDB = Firebase.database.reference.child(DB_USERS).child(auth.currentUser?.uid.orEmpty()).child(CHILD_CHAT)
        chatDB.addListenerForSingleValueEvent(object : ValueEventListener{
            @SuppressLint("NotifyDataSetChanged")
            override fun onDataChange(snapshot: DataSnapshot) {
                snapshot.children.forEach{
                    val model = it.getValue(ChatListItem::class.java)
                    model ?: return
                    Log.d("chatlist1", model.itemTitle)
                    chatRoomList.add(model)
                }
                val chatList = mutableListOf<ChatListItem>()
                chatRoomList.distinctBy {
                    it.itemTitle
                }.forEach { chatList.add(it)

                }
                Log.d("chatlist2", chatList.toString())
                chatListAdapter.submitList(chatRoomList)
                chatListAdapter.notifyDataSetChanged()

            }

            override fun onCancelled(error: DatabaseError) {}

        })


    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onResume() {
        super.onResume()
        chatListAdapter.notifyDataSetChanged()
    }


}