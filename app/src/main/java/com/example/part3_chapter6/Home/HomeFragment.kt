package com.example.part3_chapter6.Home

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.part3_chapter6.ChatList.ChatListItem
import com.example.part3_chapter6.DBkey.Companion.CHILD_CHAT
import com.example.part3_chapter6.DBkey.Companion.DB_ARTICLE
import com.example.part3_chapter6.DBkey.Companion.DB_USERS
import com.example.part3_chapter6.R
import com.example.part3_chapter6.databinding.FragmentHomeBinding
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase


class HomeFragment : Fragment(R.layout.fragment_home) {

    private lateinit var articleDB :DatabaseReference
    private lateinit var userDB: DatabaseReference
    private val articleList = mutableListOf<ArticleModel>()
    private var isoverlap = false
    private val listener = object : ChildEventListener{
        override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
            val articleModel = snapshot.getValue(ArticleModel::class.java)
            Log.d("article model",articleModel.toString())
            articleModel ?: return

            articleList.add(articleModel)
            articleAdapter.submitList(articleList)
        }

        override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {}

        override fun onChildRemoved(snapshot: DataSnapshot) {}

        override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}

        override fun onCancelled(error: DatabaseError) {}

    }


    private lateinit var articleAdapter: ArticleAdapter
    private val auth by lazy {
        Firebase.auth
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val fragmentHomeBinding= FragmentHomeBinding.bind(view)
        articleList.clear()

        articleDB= Firebase.database.reference.child(DB_ARTICLE)
        userDB = Firebase.database.reference.child(DB_USERS)
        val chatDB = userDB.child(auth.currentUser?.uid.orEmpty()).child(CHILD_CHAT)
        articleAdapter = ArticleAdapter(onItemClicked = { articleModel ->
            val articleKey=auth.currentUser?.uid.orEmpty()+articleModel.sellerId+articleModel.title
            if (auth.currentUser!=null){
                //????????? ??? ??????
                if (auth.currentUser?.uid != articleModel.sellerId){
                    //???????????? ???????????? ?????????????????? ???????????? ????????????
                    chatDB.get().addOnSuccessListener {
                        it.children.forEach {
                            val model = it.getValue(ChatListItem::class.java)
                            if (articleKey==model?.key){
                                isoverlap=true
                                Log.d("????????????","1")
                                Snackbar.make(view,"?????? ???????????? ??????????????????.",Snackbar.LENGTH_LONG).show()
                            }
                        }
                        if (!isoverlap){
                            val chatRoom = ChatListItem(
                                buyerId = auth.currentUser?.uid.orEmpty(),
                                sellerId = articleModel.sellerId,
                                itemTitle = articleModel.title,
                                key =articleKey
                            )
                            userDB.child(auth.currentUser?.uid.orEmpty())
                                .child(CHILD_CHAT)
                                .push()
                                .setValue(chatRoom)
                            Log.d("????????????","3")
                            userDB.child(articleModel.sellerId)
                                .child(CHILD_CHAT)
                                .push()
                                .setValue(chatRoom)
                            Snackbar.make(view,"???????????? ????????????????????????.",Snackbar.LENGTH_LONG).show()
                        }
                    }


                } else{
                    articleDB.addChildEventListener(object :ChildEventListener{
                        override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                            val model = snapshot.getValue(ArticleModel::class.java)
                            if (model?.createAt==articleModel.createAt){
                                Log.d("????????????",model.toString())
                            } else{return}

                        }
                        override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {}
                        override fun onChildRemoved(snapshot: DataSnapshot) {}
                        override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}
                        override fun onCancelled(error: DatabaseError) {}
                    })
                    //???????????? ???????????? ?????????????????? ???????????? ????????????
                    Snackbar.make(view,"?????? ???????????????!!",Snackbar.LENGTH_LONG).show()
                }
            }else{
                //????????? ????????????
                Snackbar.make(view,"????????? ??? ??????????????????",Snackbar.LENGTH_LONG).show()
            }
        })

        fragmentHomeBinding.articleRecyclerView.layoutManager = LinearLayoutManager(context)
        fragmentHomeBinding.articleRecyclerView.adapter = articleAdapter

        fragmentHomeBinding.addFloatingButton.setOnClickListener {
            //todo ???????????????
            if(auth.currentUser !=null) {
                startActivity(Intent(context, AddArticleActivity::class.java))
            }
            else{

               Snackbar.make(view,"????????? ??? ??????????????????",Snackbar.LENGTH_LONG).show()
           }
            }


        articleDB.addChildEventListener(listener)


    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onResume() {
        super.onResume()
        articleAdapter.notifyDataSetChanged()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        articleDB.removeEventListener(listener)
    }

}

