package studio.codable.customkeyboard

import android.graphics.Rect
import android.inputmethodservice.InputMethodService
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import studio.codable.customkeyboard.adapter.CustomKeyboardAdapter
import studio.codable.customkeyboard.data.DataManager
import studio.codable.customkeyboard.data.SampleData
import studio.codable.customkeyboard.databinding.LayoutCustomKeyboardBinding
import studio.codable.customkeyboard.model.Animal
import studio.codable.customkeyboard.model.DownloadStatus

class CustomKeyboardInputService : InputMethodService() {

    companion object {
        private const val TAG = "CUSTOM_KEYBOARD"
    }

    private var _binding: LayoutCustomKeyboardBinding? = null
    private val binding: LayoutCustomKeyboardBinding
        get() = _binding!!

    private val dataManager: DataManager = DataManager()

    override fun onCreateInputView(): View {
        _binding = LayoutCustomKeyboardBinding.inflate(layoutInflater, null, false)
        return binding.root
    }

    override fun onStartInputView(info: EditorInfo?, restarting: Boolean) {
        super.onStartInputView(info, restarting)
        setOnClickListener()
        initRecyclerView()
    }

    private fun setOnClickListener() {
        binding.icKeyboard.setOnClickListener {
            val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            imm.showInputMethodPicker()
        }
    }

    private fun hideProgressBar() {
        Handler(Looper.getMainLooper()).postDelayed(
            {
                try {
                    binding.apply {
                        pbLoading.visibility = View.INVISIBLE
                        rvAnimals.visibility = View.VISIBLE
                    }
                } catch (t: Throwable) {
                    Log.d(TAG, "Progress bar problem")
                    Toast.makeText(
                        applicationContext,
                        "Problem with hiding progress bar",
                        Toast.LENGTH_SHORT
                    ).show()
                }

            }, 1000
        )
    }

    private var dropsItemDecoration = object : RecyclerView.ItemDecoration() {
        override fun getItemOffsets(
            outRect: Rect,
            view: View,
            parent: RecyclerView,
            state: RecyclerView.State
        ) {
            outRect.bottom = resources.getDimensionPixelSize(R.dimen.rv_margin_bottom)
            outRect.left = resources.getDimensionPixelSize(R.dimen.rv_margin_lr)
            outRect.right = resources.getDimensionPixelSize(R.dimen.rv_margin_lr)
        }
    }

    private fun initRecyclerView() {
        binding.rvAnimals.apply {
            layoutManager = LinearLayoutManager(applicationContext)
            adapter = CustomKeyboardAdapter(animals = SampleData.animals) { animal ->
                startShareActivity(
                    animal
                )
            }
            addItemDecoration(dropsItemDecoration)
        }
        hideProgressBar()
    }

    private fun startShareActivity(animal: Animal) {
        Toast.makeText(applicationContext, "You clicked: ${animal.name}", Toast.LENGTH_SHORT).show()

        val editorInfo = currentInputEditorInfo

        dataManager.shareAnimal(
            applicationContext,
            imageUrl = animal.imageUrl,
            fileName = animal.name,
        ) {
            when (it) {
                is DownloadStatus.Success -> {
                    applicationContext.startActivity(
                        dataManager.getKeyboardSharingIntent(
                            applicationContext,
                            editorInfo.packageName,
                            it.result
                        )
                    )
                }
                is DownloadStatus.Fail.NetworkError -> {
                    Log.d(TAG, "DownloadStatus.Fail.NetworkError ${animal.name}")
                    Toast.makeText(
                        applicationContext,
                        R.string.network_problem,
                        Toast.LENGTH_SHORT
                    ).show()
                }
                is DownloadStatus.Fail.IOError -> {
                    Log.d(TAG, "DownloadStatus.Fail.IOError ${animal.name}")
                    Toast.makeText(
                        applicationContext,
                        R.string.download_problem,
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            switchInputMethod()
        }
    }

    private fun switchInputMethod() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            switchToPreviousInputMethod()
        } else {
            val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            imm.switchToLastInputMethod(window.window?.attributes?.token)
        }
    }
}