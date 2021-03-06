package estg.ipvc.cityproblems

import android.graphics.Color
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatDelegate
import com.mikhaellopez.circularprogressbar.CircularProgressBar

class Sensores : AppCompatActivity(), SensorEventListener {

    private lateinit var sensorManager: SensorManager

    //sensor Luminosidade
    private var brightness: Sensor? = null
    private lateinit var text: TextView
    private lateinit var pb: CircularProgressBar

    //sensor Acelerometro
    private lateinit var square: TextView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sensores)
        supportActionBar?.hide()

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        //sensor Luminosidade
        text = findViewById(R.id.tv_text)
        pb = findViewById(R.id.circularProgressBar)

        //sensor Acelerometro
        square = findViewById(R.id.tv_square)
        setUpSensorStuff()
    }

    private fun setUpSensorStuff(){
        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager

        //sensorLuminosidade
        brightness = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT)

        //sensor Acelerometro
        sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)?.also {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_FASTEST)
        }
    }

    override fun onSensorChanged(event: SensorEvent?) {
        //sensor Luminosidade
        if (event?.sensor?.type == Sensor.TYPE_LIGHT) {
            val light = event.values[0]

            text.text = "Sensor: $light\n${brightness(light)}"
            pb.setProgressWithAnimation(light)
        }

        //sensor Acelerometro
        if (event?.sensor?.type == Sensor.TYPE_ACCELEROMETER) {
            //Log.d("Main", "onSensorChanged: sides ${event.values[0]} front/back ${event.values[1]} ")

            // Sides = Tilting phone left(10) and right(-10)
            val sides = event.values[0]

            // Up/Down = Tilting phone up(10), flat (0), upside-down(-10)
            val upDown = event.values[1]

            square.apply {
                rotationX = upDown * 3f
                rotationY = sides * 3f
                rotation = -sides
                translationX = sides * -10
                translationY = upDown * 10
            }

            // Changes the colour of the square if it's completely flat
            val color = if (upDown.toInt() == 0 && sides.toInt() == 0) Color.GREEN else Color.RED
            square.setBackgroundColor(color)

            square.text = getString(R.string.up_down) +": " +"${upDown.toInt()}\n" + getString(R.string.left_right) +": "+ "${sides.toInt()}"
        }

    }

    private fun brightness(brightness: Float): String {
        return when (brightness.toInt()){
            0 -> getString(R.string.you_cant_see)
            in 1..10 -> getString(R.string.very_dark)
            in 11..50 -> getString(R.string.dark)
            in 51..5000 -> getString(R.string.normal)
            in 5001..25000 -> getString(R.string.light)
            else -> getString(R.string.very_light)
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        return
    }

    override fun onResume() {
        super.onResume()
        sensorManager.registerListener(this, brightness, SensorManager.SENSOR_DELAY_NORMAL)
    }

    override fun onPause(){
        super.onPause()
        sensorManager.unregisterListener(this)
    }
}