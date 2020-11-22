package com.speacode.uploader

import com.intellij.notification.Notification
import com.intellij.notification.NotificationDisplayType
import com.intellij.notification.NotificationGroup
import com.intellij.notification.NotificationType
import com.intellij.openapi.fileChooser.FileChooser
import com.intellij.openapi.fileChooser.FileChooserDescriptor
import com.intellij.openapi.ui.DialogWrapper
import com.intellij.openapi.ui.Messages
import com.intellij.openapi.ui.ValidationInfo
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.ui.components.JBLabel
import com.intellij.uiDesigner.core.AbstractLayout
import com.intellij.util.ui.GridBag
import com.intellij.util.ui.JBUI
import com.intellij.util.ui.UIUtil
import java.awt.*
import java.awt.event.ActionEvent
import java.awt.event.ActionListener
import java.util.Timer
import javax.swing.*
import kotlin.concurrent.schedule

class UploaderGUI:DialogWrapper(true) {
    private val panelMain: JPanel = JPanel(GridBagLayout())
    private val txtPath: JTextField = JTextField()
    private val btnBrowse: JButton = JButton()
    private val txtTitle: JTextField = JTextField()
    private var validated:Boolean = false

    init{
        init()
        title="Speacode Video Uploader"
        setOKButtonText("Upload")
        setResizable(false)
        initValidation()
    }

    override fun doOKAction() {
        //upload file here
        super.doOKAction()
        Timer("Show Balloon", false).schedule(500){
            showBalloon("Video Uploader", "Video upload has been completed", NotificationType.INFORMATION)
        }
    }

    override fun doValidate(): ValidationInfo? {
        if(txtPath.text.isNullOrEmpty())
            return ValidationInfo("File path is required", txtPath)
        if(txtTitle.text.isNullOrEmpty())
            return ValidationInfo("Title is required", txtTitle)
        return null
    }

    private fun showBalloon(title:String, content:String, type: NotificationType)
    {
        val ng = NotificationGroup("com.speacode.uploader", NotificationDisplayType.STICKY_BALLOON, true)
        val notification = Notification(ng.displayId,title,content,type)
        notification.notify(null)
    }


    inner class BrowseFileButtonClickListener: ActionListener{
        override fun actionPerformed(e: ActionEvent?) {
            fileBrowser {t-> txtPath.text = t.path}
        }
    }

    override fun createCenterPanel(): JComponent? {
        val gridBag = GridBag()
            .setDefaultInsets(Insets(0,0,AbstractLayout.DEFAULT_VGAP, AbstractLayout.DEFAULT_HGAP))
            .setDefaultWeightX(1.0)
            .setDefaultFill(GridBagConstraints.HORIZONTAL)

        txtPath.isEditable = false
        panelMain.preferredSize = Dimension(300,200)
        panelMain.add(label("Title"), gridBag.nextLine().next().weightx(0.2))
        panelMain.add(txtTitle, gridBag.nextLine().next().weightx(0.8))
        panelMain.add(label("Video Path"), gridBag.nextLine().next().weightx(0.2))
        panelMain.add(txtPath, gridBag.nextLine().next().weightx(1.0))
        btnBrowse.addActionListener(BrowseFileButtonClickListener())
        btnBrowse.text = "BROWSE"
        panelMain.add(btnBrowse,gridBag.nextLine().next().weightx(0.1))

        return panelMain
    }

    private fun label(text:String): JComponent{
        val label = JBLabel(text)
        label.componentStyle = UIUtil.ComponentStyle.SMALL
        label.fontColor = UIUtil.FontColor.NORMAL
        label.border = JBUI.Borders.empty(0,5,2,0)
        return label
    }

    private fun showMessage(message: String)
    {
        Messages.showMessageDialog(null, message, "Speacode Video Uploader", Messages.getInformationIcon())
    }

    private fun fileBrowser(callback: (t: VirtualFile) -> Unit)
    {
        val fileChooserDescriptor = FileChooserDescriptor(
            true,
            false,
            false,
            false,
            false,
            false
        )
        fileChooserDescriptor.title = "Select the video you wish to upload"
        fileChooserDescriptor.description = "Your selected file will be uploaded"

        FileChooser.chooseFile(fileChooserDescriptor, null, null, callback)
    }
}