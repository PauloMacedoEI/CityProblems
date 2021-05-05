package estg.ipvc.cityproblems

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat.startActivity
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.Marker
import estg.ipvc.cityproblems.api.Anomalia
import retrofit2.Callback


class MarkerInfo(context: Context) : GoogleMap.InfoWindowAdapter{

    var mContext = context
    var mapaWindow = (context as Activity).layoutInflater.inflate(R.layout.activity_marker_info, null)


    private fun janela(marker: Marker, view: View){
        val titulo = view.findViewById<TextView>(R.id.title_anomalia)
//        val descricao = view.findViewById<TextView>(R.id.descMarker)
//        val imagem = view.findViewById<ImageView>(R.id.foto)
        val user = view.findViewById<TextView>(R.id.user_anomalia)
        val layout = view.findViewById<ConstraintLayout>(R.id.layoutAnomalia)
        val data = marker.snippet.split("+").toTypedArray()


        titulo.text = marker.title

        //descricao.text = data[0]
//        Picasso.get().load(data[5]).into(imagem);
//        imagem.getLayoutParams().height = 200;
//        imagem.getLayoutParams().width = 100;
//        imagem.requestLayout();
//
//        if(data[2].toInt() == data[3].toInt()){
//            user.text = data[4]
//            user.visibility = (View.VISIBLE)
//            layout.visibility = (View.VISIBLE)
//        }
//        else{
//                user.visibility = (View.GONE)
//                layout.visibility = (View.GONE) }
    }
    override fun getInfoWindow(marker: Marker): View {
        janela(marker, mapaWindow)
        return mapaWindow
    }
    override fun getInfoContents(marker: Marker): View? {
        janela(marker, mapaWindow)
        return mapaWindow
    }
}