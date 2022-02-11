package com.example.part3_chapter6.ChatDetailed

data class ChatItem(
    val sendId : String,
    val message: String
){
    constructor(): this("","")
}
