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

    @GET("anomalia/{tipo}")
    fun getAnomaliaByType(@Path("tipo") tipo: String?): retrofit2.Call<List<Anomalia>>

    @GET("eliminarUmaAnomalia/{id}")
    fun eliminarAnomalia(@Path("id") id: Int): Call<Anomalia>
}