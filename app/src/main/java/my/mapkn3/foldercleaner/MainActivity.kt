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
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import my.mapkn3.chooser.ChooserActivity
import my.mapkn3.chooser.model.FileSystemModel
import java.io.File

class MainActivity : AppCompatActivity() {
    companion object {
        private const val FOLDERS_KEY = "folders"
        val SELECT_FOLDER = 1
        var IGNORE_FILES = listOf(".nomedia")
    }

    private lateinit var folders: ArrayList<String>
    private lateinit var adapter: ArrayAdapter<String>


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

        loadData()

        val folderListView = findViewById<ListView>(R.id.folders)
        adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, folders)
        folderListView.adapter = adapter

        folderListView.setOnItemLongClickListener { parent, view, position, id ->
            val selectedItem = (view as TextView).text.toString()
            val removedItem = folders.removeAt(position)
            adapter.notifyDataSetChanged()
            toastShort("Folder '$removedItem' unselected")
            removedItem == selectedItem
        }
    }

    override fun onStop() {
        saveData()
        super.onStop()
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
                        if (folders.contains(path)) {
                            toastShort("Folder '$path' already selected")
                        } else {
                            folders.add(path)
                            adapter.notifyDataSetChanged()
                        }
                    }
                }
        }
    }

    fun startChooseFolder(view: View) {
        val chooserActivity = Intent(this, ChooserActivity::class.java)
        chooserActivity.putExtra(ChooserActivity.CHOOSE, FileSystemModel.MODE.FOLDER)
        startActivityForResult(chooserActivity, SELECT_FOLDER)
    }

    fun startClearFolders(view: View) {
        if (folders.isNotEmpty()) {
            folders.forEach { path ->
                val files: Array<File>? = File(path).listFiles { dir, name -> !IGNORE_FILES.contains(name) }
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

    private fun saveData() {
        getPreferences(Context.MODE_PRIVATE).edit()
            .putStringSet(FOLDERS_KEY, HashSet(folders))
            .apply()
    }

    private fun loadData() {
        val savedFolders = getPreferences(Context.MODE_PRIVATE).getStringSet(FOLDERS_KEY, emptySet())
        folders = ArrayList(savedFolders)
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
}
