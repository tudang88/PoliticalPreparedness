package com.example.android.politicalpreparedness

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.android.politicalpreparedness.representative.RepresentativeViewModel
import org.koin.androidx.viewmodel.ext.android.getStateViewModel

class MainActivity : AppCompatActivity() {
    private lateinit var representativeViewModel: RepresentativeViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        representativeViewModel = getStateViewModel()
        setContentView(R.layout.activity_main)
    }
}
