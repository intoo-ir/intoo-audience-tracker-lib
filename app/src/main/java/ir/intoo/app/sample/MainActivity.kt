package ir.intoo.app.sample

import android.os.Bundle
import android.widget.RadioButton
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import ir.intoo.api.tracker.Tracker

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val startTrackerWithService = findViewById<MaterialButton>(R.id.startTrackerWithService)
        val startTrackerWithoutService =
            findViewById<MaterialButton>(R.id.startTrackerWithoutService)
        val saveProfile =
            findViewById<MaterialButton>(R.id.saveProfile)
        val inputAge =
            findViewById<TextInputEditText>(R.id.inputAge)
        val radioFemale =
            findViewById<RadioButton>(R.id.radioFemale)

        //init
        val tracker = Tracker(this@MainActivity)

        //check running service tracker
        if (tracker.isRunningService()) {
            startTrackerWithService.apply {
                text = getString(R.string.stop_service)
            }
        }

        startTrackerWithService.setOnClickListener {
            if (tracker.isRunningService()) {
                tracker.stopService()
                startTrackerWithService.apply {
                    text = getString(R.string.start_with_service)
                }
            } else {
                startTrackerWithService.apply {
                    text = getString(R.string.stop_service)
                }
                tracker.start(startService = true)
            }

        }
        startTrackerWithoutService.setOnClickListener {
            if (tracker.isRunningTracker()) {
                startTrackerWithoutService.apply {
                    text = getString(R.string.start_without_service)
                }
                tracker.stop()
            } else {
                startTrackerWithoutService.apply {
                    text = getString(R.string.stop_tracker)
                }
                tracker.start(startService = false)
            }

        }

        saveProfile.setOnClickListener {
            val age = inputAge.text.toString().toIntOrNull()
            var gender = Tracker.Gender.male.name
            if (radioFemale.isChecked) {
                gender = Tracker.Gender.female.name
            }
            //save profile
            Tracker.saveProfile(this, userAge = age, userGender = gender)
        }
    }
}