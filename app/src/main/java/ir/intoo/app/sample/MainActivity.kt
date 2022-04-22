package ir.intoo.app.sample

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import ir.intoo.api.tracker.Tracker

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
     //   Tracker(this)
        Tracker(context = this@MainActivity, startService = true)
//        Tracker.saveProfile(this, userAge = 1, userGender = Tracker.MALE)
    }
}