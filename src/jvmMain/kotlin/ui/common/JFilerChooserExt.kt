package ui.common

import java.awt.Component
import java.io.File
import javax.swing.JFileChooser

class JFilerChooserExt(defaultFilename: String) : JFileChooser() {

    init {
        selectedFile = File(defaultFilename)
    }

}