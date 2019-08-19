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
import my.mapkn3.foldercleaner.fragment.ListWithControlFragment
import java.io.File

class MainActivity : AppCompatActivity(), ListWithControlFragment.ListWithControlFragmentListener {

    companion object {
        const val SELECT_FOLDER = 1
        const val SELECT_IGNORE = 2

        const val FOLDERS_KEY = "foldersList"
        const val IGNORE_KEY = "ignoreList"
    }

    private lateinit var foldersFragment: ListWithControlFragment
    private lateinit var ignoreFragment: ListWithControlFragment

    private val permissions = arrayOf(
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
        Manifest.permission.READ_EXTERNAL_STORAGE
    )
    private val requestCode = 1337

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
                ActivityCompat.requestPermissions(this, permissions, requestCode)
            }
        }
        return isPermissionOn
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        requestForPermission()

        if (savedInstanceState == null) {
            foldersFragment = ListWithControlFragment.newInstance(
                FOLDERS_KEY,
                ListWithControlFragment.ControlPosition.UP,
                arrayOf(
                    "Select folder" to {
                        val chooserActivity = Intent(this, ChooserActivity::class.java).apply {
                            putExtra(ChooserActivity.CHOOSE, FileSystemModel.MODE.FOLDER)
                            putExtra(ChooserActivity.GET, FileSystemModel.TYPE.PATH)
                        }
                        startActivityForResult(chooserActivity, SELECT_FOLDER)
                    },
                    "Clear folders" to {
                        if (foldersFragment.itemList.isNotEmpty()) {
                            foldersFragment.itemList.forEach { path ->
                                val item = File(path)
                                if (ignoreFragment.itemList.contains(item.name)) {
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
                            }
                            toastLong("Complete!")
                        } else {
                            toastLong("Nothing is selected...")
                        }
                    }
                )
            )
            ignoreFragment = ListWithControlFragment.newInstance(
                IGNORE_KEY,
                ListWithControlFragment.ControlPosition.DOWN,
                arrayOf("Select ignore" to {
                    val chooserActivity = Intent(this, ChooserActivity::class.java).apply {
                        putExtra(ChooserActivity.CHOOSE, FileSystemModel.MODE.FILE)
                        putExtra(ChooserActivity.GET, FileSystemModel.TYPE.NAME)
                    }
                    startActivityForResult(chooserActivity, SELECT_IGNORE)
                })
            )
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
                        if (foldersFragment.itemList.contains(path)) {
                            toastShort("Folder '$path' is already selected")
                        } else {
                            foldersFragment.addItem(path)
                        }
                    }
                }
            SELECT_IGNORE ->
                if (resultCode == Activity.RESULT_OK && data != null) {
                    val path = data.getStringExtra(ChooserActivity.RESULT_STRING)
                    if (ignoreFragment.itemList.contains(path)) {
                        toastShort("Ignore '$path' is already selected")
                    } else {
                        ignoreFragment.addItem(path)
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

    private fun deepRemoveItem(item: File): Boolean {
        var result = false
        if (!ignoreFragment.itemList.contains(item.name)) {
            if (item.isDirectory) {
                item.listFiles().forEach { deepRemoveItem(it) }
            }
            result = item.delete()
        }
        return result
    }

    private fun toastShort(message: String) {
        toast(message, Toast.LENGTH_SHORT)
    }

    private fun toastLong(message: String) {
        toast(message, Toast.LENGTH_LONG)
    }

    private fun toast(message: String, duration: Int) {
        Toast.makeText(this, message, duration).show()
    }

    override fun notify(message: String) {
        toastShort(message)
    }
}
