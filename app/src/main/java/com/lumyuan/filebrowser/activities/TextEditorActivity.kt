package com.lumyuan.filebrowser.activities

import android.R
import android.annotation.SuppressLint
import android.app.UiModeManager
import android.content.Context
import android.graphics.Typeface
import android.os.Bundle
import android.view.KeyEvent
import android.view.MenuItem
import android.view.MotionEvent
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.lumyuan.androidfilemanager.FileUtils
import com.lumyuan.androidfilemanager.file.File
import com.lumyuan.filebrowser.base.BaseActivity
import com.lumyuan.filebrowser.config.EditorConfigManager
import com.lumyuan.filebrowser.databinding.ActivityTextEditBinding
import com.lumyuan.filebrowser.model.TextEditorViewModel
import com.lumyuan.filebrowser.pojo.EditorConfigBean
import com.lxj.xpopup.XPopup
import com.lxj.xpopup.impl.LoadingPopupView
import io.github.rosemoe.sora.event.ContentChangeEvent
import io.github.rosemoe.sora.event.EditorKeyEvent
import io.github.rosemoe.sora.event.KeyBindingEvent
import io.github.rosemoe.sora.event.SelectionChangeEvent
import io.github.rosemoe.sora.lang.EmptyLanguage
import io.github.rosemoe.sora.lang.Language
import io.github.rosemoe.sora.langs.textmate.TextMateColorScheme
import io.github.rosemoe.sora.langs.textmate.TextMateLanguage
import io.github.rosemoe.sorakt.subscribeEvent
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Observer
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.schedulers.Schedulers
import org.eclipse.tm4e.core.internal.theme.reader.ThemeReader
import java.io.InputStreamReader

class TextEditorActivity : BaseActivity() {

    private val binding by binding(ActivityTextEditBinding::inflate)
    private lateinit var file: File
    private lateinit var loadingPopupView: LoadingPopupView
    private lateinit var viewModel: TextEditorViewModel
    private lateinit var editorConfigManager: EditorConfigManager
    private lateinit var content: String
    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this)[TextEditorViewModel::class.java]
        setSupportActionBar(binding.titleBar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        editorConfigManager = EditorConfigManager(this)

        binding.editor.textSizePx = editorConfigManager.loadConfig().textSize
        binding.editor.setOnTouchListener { v, event ->
            when (event.action) {
                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                    editorConfigManager.saveConfig(EditorConfigBean(binding.editor.textSizePx))
                }
            }
            false
        }

        loadingPopupView = XPopup.Builder(this)
            .dismissOnBackPressed(false)
            .isLightNavigationBar(true)
            .isViewMode(true)
            .asLoading("读取中...")
        loadingPopupView.show()

        try {
            file = File(intent.getStringExtra("file")!!)
        }catch (e: Exception){
            e.printStackTrace()
        }
        Observable.create {
            try {
                it.onNext(FileUtils.readBytes(file.getPath()))
            } catch (e: Exception) {
                it.onNext("错误信息: \n\t\t$e".toByteArray(viewModel.charset.value!!))
            }
            it.onComplete()
        }.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe{
                if (!isFinishing){
                    loadingPopupView.dismiss()
                    viewModel.textContent.value = it
                }
            }
        try {
            supportActionBar!!.title = file.getName()
            supportActionBar!!.subtitle = viewModel.charset.value!!.name()
        }catch (e: Exception){
            e.printStackTrace()
        }

        initSymbolInput()
        initEditor()
        setLanguage(try {
            file.getName()
        }catch (e: Exception){
            ".text"
        })

        viewModel.textContent.observe(this){
            content = String(it, viewModel.charset.value!!)
            binding.editor.setText(content)
        }
    }


    private fun initSymbolInput() {
        val inputView = binding.symbolInput
        inputView.bindEditor(binding.editor)
        inputView.addSymbols(
            arrayOf("->", "{", "}", "(", ")", ",", ".", ";", "\"", "?", "+", "-", "*", "/"),
            arrayOf("\t", "{}", "}", "(", ")", ",", ".", ";", "\"", "?", "+", "-", "*", "/")
        )
    }

    @SuppressLint("ResourceType")
    private fun initEditor() {
        val uiModeManager: UiModeManager = getSystemService(Context.UI_MODE_SERVICE) as UiModeManager
        if (uiModeManager.nightMode ==UiModeManager.MODE_NIGHT_YES){
            try {
                val iRawTheme = ThemeReader.readThemeSync(
                    "darcula.json",
                    assets.open("textmate/darcula.json")
                )
                val colorScheme = TextMateColorScheme.create(iRawTheme)
                binding.editor.colorScheme = colorScheme
                val language = binding.editor.editorLanguage
                if (language is TextMateLanguage) {
                    language.updateTheme(iRawTheme)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }else {
            try {
                val iRawTheme = ThemeReader.readThemeSync(
                    "QuietLight.tmTheme",
                    assets.open("textmate/QuietLight.tmTheme")
                )
                val colorScheme = TextMateColorScheme.create(iRawTheme)
                binding.editor.colorScheme = colorScheme
                val language = binding.editor.editorLanguage
                if (language is TextMateLanguage) {
                    language.updateTheme(iRawTheme)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        binding.editor.apply {
            typefaceText = Typeface.createFromAsset(assets, "JetBrainsMono-Regular.ttf")
            setLineSpacing(2f, 1.1f)
//            nonPrintablePaintingFlags =
//                CodeEditor.FLAG_DRAW_WHITESPACE_LEADING or CodeEditor.FLAG_DRAW_LINE_SEPARATOR or CodeEditor.FLAG_DRAW_WHITESPACE_IN_SELECTION
            // Update display dynamically
            subscribeEvent<SelectionChangeEvent> { _, _ ->

            }
            subscribeEvent<ContentChangeEvent> { _, _ ->
                postDelayed(
                    {

                    },
                    50
                )
            }

            subscribeEvent<KeyBindingEvent> { event, _ ->
                if (event.eventType != EditorKeyEvent.Type.DOWN) {
                    return@subscribeEvent
                }

                Toast.makeText(
                    context,
                    "Keybinding event: " + generateKeybindingString(event),
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    private fun setLanguage(language: String) {
        binding.editor.setEditorLanguage(getFileType(language))
    }

    private fun getFileType(name: String): Language?{
        return when (try {
            name.substring(name.lastIndexOf(".")).lowercase()
        }catch (e: Exception){
            "text"
        }) {
            ".java", ".iyu", ".myu", ".ijava", ".ilua", ".ijs", ".sh" -> {
                TextMateLanguage.create(
                    "java.tmLanguage.json",
                    assets.open("textmate/java/syntaxes/java.tmLanguage.json"),
                    InputStreamReader(assets.open("textmate/java/language-configuration.json")),
                    (binding.editor.colorScheme as TextMateColorScheme).rawTheme
                )
            }
            ".kt" ->{
                TextMateLanguage.create(
                    "kotlin.tmLanguage",
                    assets.open("textmate/kotlin/syntaxes/kotlin.tmLanguage"),
                    InputStreamReader(assets.open("textmate/kotlin/language-configuration.json")),
                    (binding.editor.colorScheme as TextMateColorScheme).rawTheme
                )
            }
            ".json" ->{
                TextMateLanguage.create(
                    "json.tmLanguage.json",
                    assets.open("textmate/json/syntaxes/json.tmLanguage.json"),
                    InputStreamReader(assets.open("textmate/json/language-configuration.json")),
                    (binding.editor.colorScheme as TextMateColorScheme).rawTheme
                )
            }
            ".xml", ".html" ->{
                TextMateLanguage.create(
                    "xml.tmLanguage.json",
                    assets.open("textmate/xml/syntaxes/xml.tmLanguage.json"),
                    InputStreamReader(assets.open("textmate/xml/language-configuration.json")),
                    (binding.editor.colorScheme as TextMateColorScheme).rawTheme
                )
            }
            ".gradle", ".css" ->{
                TextMateLanguage.create(
                    "java.tmLanguage.json",
                    assets.open("textmate/java/syntaxes/java.tmLanguage.json"),
                    InputStreamReader(assets.open("textmate/java/language-configuration.json")),
                    (binding.editor.colorScheme as TextMateColorScheme).rawTheme
                )
            }
            else -> {
                EmptyLanguage()
            }
        }
    }

    private fun generateKeybindingString(event: KeyBindingEvent): String {
        val sb = StringBuilder()
        if (event.isCtrlPressed) {
            sb.append("Ctrl + ")
        }

        if (event.isAltPressed) {
            sb.append("Alt + ")
        }

        if (event.isShiftPressed) {
            sb.append("Shift + ")
        }

        sb.append(KeyEvent.keyCodeToString(event.keyCode))
        return sb.toString()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.home -> {
                finish()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onDestroy() {
        super.onDestroy()
        binding.editor.release()
    }

}