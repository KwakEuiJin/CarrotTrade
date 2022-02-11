package com.example.part3_chapter6.ChatList

data class ChatListItem(
    val buyerId: String,
    val sellerId:String,
    val itemTitle:String,
    val key: String
){
    constructor(): this("","","","")
}
