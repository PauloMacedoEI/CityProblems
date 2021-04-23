package estg.ipvc.cityproblems.api

import retrofit2.Call
import retrofit2.http.*

interface EndPoints {
    @FormUrlEncoded
    @POST( "verificaruser")
    fun verificarUsers(@Field("username")username:String, @Field("password")password:String): Call<User>

    @GET("anomalia")
    fun getAllAnomalias(): retrofit2.Call<List<Anomalia>>

    //@GET("user/{id}/anomalia")
    //fun getAllUserAnomalias(@Path("id")id_username:Int): retrofit2.Call<List<Anomalia>>
}