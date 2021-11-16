package com.crushtech.stateflowpractice

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.crushtech.stateflowpractice.databinding.ActivityMainBinding
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

class MainActivity : AppCompatActivity() {

    private var _binding: ActivityMainBinding? = null
    private val binding get() = _binding!!
    private val myViewModel: DemoViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.etUsername.doOnTextChanged { text, _, _, _ ->
            myViewModel.onUserNameChanged(text.toString())
        }
        binding.etPassword.doOnTextChanged { text, _, _, _ ->
            myViewModel.onPwdChanged(text.toString())
        }
        binding.btnLogin.setOnClickListener {
            myViewModel.onLoginPressed()
        }

        myViewModel.uiState.collectWhenStarted(this) {
            binding.progressBar.isVisible = it.isLoading
        }
        myViewModel.events.collectWhenStarted(this) {
            when (it) {
                is LoginScreenEvents.ShowSnackBar -> {
                    Snackbar.make(binding.root, it.message, Snackbar.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun LifecycleOwner.addRepeatingJob(
        state: Lifecycle.State,
        coroutineContext: CoroutineContext = EmptyCoroutineContext,
        block: suspend CoroutineScope.() -> Unit
    ): Job {
        return lifecycleScope.launch(coroutineContext) {
            lifecycle.repeatOnLifecycle(state, block)
        }
    }

    private inline fun <T> Flow<T>.collectWhenStarted(
        lifecycleOwner: LifecycleOwner,
        crossinline action: suspend (value: T) -> Unit
    ) {
        lifecycleOwner.addRepeatingJob(Lifecycle.State.STARTED) {
            collect(action)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}
