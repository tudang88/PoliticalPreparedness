package com.example.android.politicalpreparedness

import android.app.Application
import com.example.android.politicalpreparedness.database.ElectionDatabase
import com.example.android.politicalpreparedness.election.ElectionsViewModel
import com.example.android.politicalpreparedness.election.VoterInfoViewModel
import com.example.android.politicalpreparedness.repository.ElectionsRepository
import com.example.android.politicalpreparedness.representative.RepresentativeViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.context.startKoin
import org.koin.dsl.module
import timber.log.Timber

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        /**
         * use Koin for Dependency Injection
         */
        val myModule = module {
            viewModel {
                ElectionsViewModel(get())
            }
            viewModel {
                VoterInfoViewModel(get())
            }
            viewModel {
                RepresentativeViewModel(get())
            }
            // repository instance
            single { ElectionsRepository(get()) }
            // database instance
            single {
                ElectionDatabase.getInstance(this@MyApplication)
            }
        }
        startKoin {
            androidContext(this@MyApplication)
            modules(listOf(myModule))
        }
        Timber.plant(Timber.DebugTree())
    }
}