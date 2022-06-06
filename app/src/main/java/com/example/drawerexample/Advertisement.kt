package com.example.drawerexample

class Advertisement (
    var id : String = "",
    var title :String = "",
    var description :String = "",
    var date :String = "",
    var duration :String = "",
    var location :String = "",
    var skill :String = "",
    var creatorMail : String = "",
    var creatorUID : String = "",
    var status: String = "",
    var buyerUID: String= "",
    var requests: List<String> = listOf(),

    var rateForBuyer: Float? = null,
    var rateForSeller: Float? = null,
    var commentForBuyer: String? = null,
    var commentForSeller: String? = null
)
