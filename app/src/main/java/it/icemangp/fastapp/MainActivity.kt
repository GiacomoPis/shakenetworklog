package it.icemangp.fastapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import it.icemangp.fastapp.ui.main.MainFragment
import it.icemangp.shakenetworklog.data.NetworkLogManager

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, MainFragment.newInstance())
                .commitNow()
        }

        NetworkLogManager.start(lifecycle)
    }
}