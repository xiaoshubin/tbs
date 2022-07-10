package com.smallcake.tbsdocrenderview

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.hjq.permissions.Permission
import com.hjq.permissions.XXPermissions

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

            XXPermissions.with(this)
                // 申请单个权限
                .permission(Permission.MANAGE_EXTERNAL_STORAGE)
                .request { _, all ->
                    if (all) {
                    }
                }
        findViewById<Button>(R.id.btn).setOnClickListener{
            startActivity(Intent(this@MainActivity,DocActivity::class.java))
        }



    }


}