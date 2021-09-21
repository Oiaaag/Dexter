package ma.dexter.ui.activity.code

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import io.github.rosemoe.sora.langs.java.JavaLanguage
import ma.dexter.managers.DexProjectManager
import ma.dexter.databinding.ActivityJavaViewerBinding
import ma.dexter.editor.scheme.smali.SchemeLightSmali
import ma.dexter.ui.util.setDefaults

class JavaViewerActivity : AppCompatActivity() {
    private lateinit var binding: ActivityJavaViewerBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityJavaViewerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        DexProjectManager.currentJavaCode.use { code ->

            with(binding.codeEditor) {
                colorScheme = SchemeLightSmali()
                isEditable = false

                setText(code.value)
                setDefaults()
                setEditorLanguage(JavaLanguage())
            }

        }

    }
}
