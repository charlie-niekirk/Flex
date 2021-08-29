package me.cniekirk.flex

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import me.cniekirk.flex.ui.main.SubmissionListFragment

class ContainerActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.container_activity)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, SubmissionListFragment.newInstance())
                .commitNow()
        }
    }
}