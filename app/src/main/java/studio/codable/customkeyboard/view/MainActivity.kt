package studio.codable.customkeyboard.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import studio.codable.customkeyboard.R
import studio.codable.customkeyboard.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}