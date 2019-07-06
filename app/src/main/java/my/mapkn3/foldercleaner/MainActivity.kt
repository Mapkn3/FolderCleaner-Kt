package my.mapkn3.foldercleaner

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_main.*
import my.mapkn3.chooser.ChooserActivity
import my.mapkn3.chooser.model.FileSystemModel
import my.mapkn3.foldercleaner.fragment.FoldersFragment
import my.mapkn3.foldercleaner.fragment.IgnoreFragment
import java.io.File

class MainActivity : AppCompatActivity(), FoldersFragment.FolderFragmentListener,
    IgnoreFragment.IgnoreFragmentListener {
    companion object {
        val SELECT_FOLDER = 1
        val SELECT_IGNORE = 2
    }

    private lateinit var foldersFragment: FoldersFragment
    private lateinit var ignoreFragment: IgnoreFragment

    val PERMISSIONS = arrayOf(
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
        Manifest.permission.READ_EXTERNAL_STORAGE
    )
    val REQUEST_CODE = 1337

    private fun canAccessExternalSd(): Boolean {
        return hasPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
    }

    private fun hasPermission(permission: String): Boolean {
        return PackageManager.PERMISSION_GRANTED == ContextCompat.checkSelfPermission(
            this,
            permission
        )
    }

    private fun requestForPermission(): Boolean {
        var isPermissionOn = true
        val version = Build.VERSION.SDK_INT
        if (version >= 23) {
            if (!canAccessExternalSd()) {
                isPermissionOn = false
                ActivityCompat.requestPermissions(this, PERMISSIONS, REQUEST_CODE)
            }
        }
        return isPermissionOn
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        requestForPermission()

        if (savedInstanceState == null) {
            foldersFragment = FoldersFragment.newInstance()
            ignoreFragment = IgnoreFragment.newInstance()
        }

        viewPager2.adapter = ViewPagerFragmentStateAdapter(
            listOf(ignoreFragment, foldersFragment),
            supportFragmentManager,
            lifecycle
        )
        viewPager2.currentItem = 1
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            SELECT_FOLDER ->
                if (resultCode == Activity.RESULT_OK && data != null) {
                    val path = data.getStringExtra(ChooserActivity.RESULT_STRING)
                    if (path == Environment.getExternalStorageDirectory().absolutePath) {
                        toastShort("You can't select root folder!")
                    } else {
                        if (foldersFragment.foldersList.contains(path)) {
                            toastShort("Folder '$path' is already selected")
                        } else {
                            foldersFragment.addFolder(path)
                        }
                    }
                }
            SELECT_IGNORE ->
                if (resultCode == Activity.RESULT_OK && data != null) {
                    val path = data.getStringExtra(ChooserActivity.RESULT_STRING)
                    if (ignoreFragment.ignoreList.contains(path)) {
                        toastShort("Ignore '$path' is already selected")
                    } else {
                        ignoreFragment.addIgnore(path)
                    }
                }
        }
    }

    override fun saveData(key: String, data: ArrayList<String>) {
        getPreferences(Context.MODE_PRIVATE).edit()
            .putStringSet(key, HashSet(data))
            .apply()
    }

    override fun loadData(key: String): ArrayList<String> {
        val savedFolders = getPreferences(Context.MODE_PRIVATE).getStringSet(key, emptySet())
        return ArrayList(savedFolders)
    }

    override fun onChooseIgnoreClick() {
        val chooserActivity = Intent(this, ChooserActivity::class.java).apply {
            putExtra(ChooserActivity.CHOOSE, FileSystemModel.MODE.FILE)
            putExtra(ChooserActivity.GET, FileSystemModel.TYPE.NAME)
        }
        startActivityForResult(chooserActivity, SELECT_IGNORE)
    }

    override fun onChooseFolderClick() {
        val chooserActivity = Intent(this, ChooserActivity::class.java).apply {
            putExtra(ChooserActivity.CHOOSE, FileSystemModel.MODE.FOLDER)
            putExtra(ChooserActivity.GET, FileSystemModel.TYPE.PATH)
        }
        startActivityForResult(chooserActivity, SELECT_FOLDER)
    }

    override fun onClearFolderClick() {
        if (foldersFragment.foldersList.isNotEmpty()) {
            /*foldersFragment.foldersList.forEach { path ->
                val item = File(path)
                if (ignoreFragment.ignoreList.contains(item.name)) {
                    toastShort("Folder '$path' is in ignoreList list")
                } else {
                    val files: Array<File>? = item.listFiles()
                    if (files == null || files.isEmpty()) {
                        toastShort("Folder '$path' is empty")
                    } else {
                        files.forEach {
                            if (!deepRemoveItem(it)) {
                                toastLong("File '${it.absolutePath}' is not deleted")
                            }
                        }
                        toastLong("Folder '$path' is cleared")
                    }
                }
            }*/
            foldersFragment.foldersList.map { File(it) }.forEach { deepRemoveItem(it) }
            toastLong("Complete!")
        } else {
            toastLong("Nothing is selected...")
        }
    }

    private fun deepRemoveItem(item: File): Boolean {
        var result = false
        if (!ignoreFragment.ignoreList.contains(item.name)) {
            if (item.isDirectory) {
                item.listFiles().forEach { deepRemoveItem(it) }
            }
            result = item.delete()
        }
        return result
    }

    fun toastShort(message: String) {
        toast(message, Toast.LENGTH_SHORT)
    }

    fun toastLong(message: String) {
        toast(message, Toast.LENGTH_LONG)
    }

    private fun toast(message: String, duration: Int) {
        Toast.makeText(this, message, duration).show()
    }

    override fun notify(message: String) {
        toastShort(message)
    }
}
