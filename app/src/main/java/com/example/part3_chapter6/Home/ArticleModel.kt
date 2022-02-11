package com.example.part3_chapter6.Home


data class ArticleModel(
    val sellerId:String,
    val title:String,
    val createAt:Long,
    val price:String,
    val imageURL: String
){
    constructor():this("","",0,"","")
}
