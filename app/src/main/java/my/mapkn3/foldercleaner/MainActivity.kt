package my.mapkn3.foldercleaner

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.view.View
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

class MainActivity : AppCompatActivity() {
    companion object {
        val FOLDERS_KEY = "folders"
        val IGNORE_KEY = "ignore"
        val SELECT_FOLDER = 1
        val SELECT_IGNORE = 2
        var IGNORE_FILES = listOf(".nomedia")
    }

    private lateinit var foldersFragment: FoldersFragment
    private lateinit var ignoreFragment: IgnoreFragment

    val PERMISSIONS = arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE)
    val REQUEST_CODE = 1337

    private fun canAccessExternalSd(): Boolean {
        return hasPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
    }

    private fun hasPermission(permission: String): Boolean {
        return PackageManager.PERMISSION_GRANTED == ContextCompat.checkSelfPermission(this, permission)
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

        viewPager2.adapter = ViewPagerFragmentStateAdapter(listOf(ignoreFragment, foldersFragment), supportFragmentManager, lifecycle)
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
                        if (foldersFragment.folders.contains(path)) {
                            toastShort("Folder '$path' already selected")
                        } else {
                            foldersFragment.folders.add(path)
                            foldersFragment.adapter.notifyDataSetChanged()
                        }
                    }
                }
            SELECT_IGNORE ->
                if (resultCode == Activity.RESULT_OK && data != null) {
                    val path = data.getStringExtra(ChooserActivity.RESULT_STRING)
                    if (ignoreFragment.ignore.contains(path)) {
                        toastShort("Ignore '$path' already selected")
                    } else {
                        ignoreFragment.ignore.add(path)
                        ignoreFragment.adapter.notifyDataSetChanged()
                    }
                }
        }
    }

    fun startChooseFolder(view: View) {
        val chooserActivity = Intent(this, ChooserActivity::class.java)
        chooserActivity.putExtra(ChooserActivity.CHOOSE, FileSystemModel.MODE.FOLDER)
        chooserActivity.putExtra(ChooserActivity.GET, FileSystemModel.TYPE.PATH)
        startActivityForResult(chooserActivity, SELECT_FOLDER)
    }

    fun startChooseIgnore(view: View) {
        val chooserActivity = Intent(this, ChooserActivity::class.java)
        chooserActivity.putExtra(ChooserActivity.CHOOSE, FileSystemModel.MODE.FILE)
        chooserActivity.putExtra(ChooserActivity.GET, FileSystemModel.TYPE.NAME)
        startActivityForResult(chooserActivity, SELECT_IGNORE)
    }

    fun startClearFolders(view: View) {
        if (foldersFragment.folders.isNotEmpty()) {
            foldersFragment.folders.forEach { path ->
                val files: Array<File>? = File(path).listFiles { dir, name -> !ignoreFragment.ignore.contains(name) }
                if (files == null || files.isEmpty()) {
                    toastShort("Folder '$path' is empty")
                } else {
                    files.forEach { file ->
                        val filePath = file.absolutePath
                        val isDelete = file.delete()
                        if (!isDelete) {
                            toastLong("File '$filePath' is not deleted")
                        }
                    }
                    toastLong("Folder '$path' is cleared")
                }
            }
            toastLong("Complete!")
        } else {
            toastLong("Nothing is selected...")
        }
    }

    fun saveData(key: String, data: ArrayList<String>) {
        getPreferences(Context.MODE_PRIVATE).edit()
            .putStringSet(key, HashSet(data))
            .apply()
    }

    fun loadData(key: String): ArrayList<String> {
        val savedFolders = getPreferences(Context.MODE_PRIVATE).getStringSet(key, emptySet())
        return ArrayList(savedFolders)
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
}
