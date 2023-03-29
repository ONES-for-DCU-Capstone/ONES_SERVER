package com.example.ones_02.navigation.model

data class ContentDTO(var explain : String? = null, //설명 글
                      var title : String? = null, //제목
                      var imageUri : String? = null,
                      var price : String? = null,
                      var uid : String? = null,
                      var userId : String? = null,
                      var timestamp : Long? = null,
                      var favoriteCount : Int? = 0,
                      var favorites : MutableMap<String,Boolean> = HashMap()){  //중복 좋아요 방지
    data class Comment(var uid: String? = null,  //댓글
                       var userId : String? = null,
                       var comment : String? = null,
                       var timestamp : Long? = null,)
}