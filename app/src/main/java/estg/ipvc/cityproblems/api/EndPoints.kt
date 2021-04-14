package estg.ipvc.cityproblems.api

import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface EndPoints {
    @FormUrlEncoded
    @POST( "userverification")
    fun verificarUsers(@Field("username")username:String, @Field("password")password:String): Call<User>


}