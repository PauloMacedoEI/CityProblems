package estg.ipvc.cityproblems.api

import retrofit2.Call
import retrofit2.http.*

interface EndPoints {
    @FormUrlEncoded
    @POST( "verificaruser")
    fun verificarUsers(@Field("username")username:String, @Field("password")password:String): Call<User>

    @GET("anomalia")
    fun getAllAnomalias(): retrofit2.Call<List<Anomalia>>

    @FormUrlEncoded
    @POST("inserirAnomalia")
    fun insertAnomalia(@Field("titulo") titulo: String?, @Field("descricao") descricao: String?, @Field("tipo") tipo: String?, @Field("foto") foto: String?, @Field("latitude") latitude: String?, @Field("longitude") longitude: String?, @Field("user_id") user_id: Int?): retrofit2.Call<Anomalia>

    //@GET("user/{id}/anomalia")
    //fun getAllUserAnomalias(@Path("id")id_username:Int): retrofit2.Call<List<Anomalia>>
}