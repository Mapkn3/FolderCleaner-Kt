package my.mapkn3.chooser

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import my.mapkn3.chooser.model.FileSystemModel
import my.mapkn3.chooser.ui.main.ChooserFragment

class ChooserActivity : AppCompatActivity(), ChooserFragment.ChooserFragmentListener {
    companion object {
        val CHOOSE = "choose"
        val GET = "get"
        val RESULT_STRING = "RESULT_STRING"
    }

    val PERMISSIONS = arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE)
    val REQUEST_CODE = 1337

    private lateinit var chooserFragment: ChooserFragment

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
        requestForPermission()

        setContentView(R.layout.chooser_activity)
        if (savedInstanceState == null) {
            chooserFragment = ChooserFragment.newInstance(
                intent.getSerializableExtra(CHOOSE) as FileSystemModel.MODE,
                intent.getSerializableExtra(GET) as FileSystemModel.TYPE
            )
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, chooserFragment)
                .commitNow()
        }
    }

    override fun onSelectClick(result: String) {
        val intent = Intent()
        intent.putExtra(RESULT_STRING, result)
        setResult(Activity.RESULT_OK, intent)
        finish()
    }

    override fun onBackPressed() {
        if (!chooserFragment.selectPrev()) {
            super.onBackPressed()
        }
    }
}