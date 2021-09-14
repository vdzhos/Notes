package com.example.notes

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Html
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import com.example.notes.databinding.ActivityMainBinding
import com.google.android.material.navigation.NavigationView

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener{

    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController
    private lateinit var drawerLayout: DrawerLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        drawerLayout = binding.drawerLayout

        setTextForDrawerHeader()

        setSupportActionBar(binding.toolbar)

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.myNavHostFragment) as NavHostFragment
        navController = navHostFragment.navController

        NavigationUI.setupActionBarWithNavController(this,navController,drawerLayout)
        NavigationUI.setupWithNavController(binding.navView, navController)
    }

    private fun setTextForDrawerHeader(){
        binding.navView.inflateHeaderView(R.layout.nav_header)
        val header = binding.navView.getHeaderView(0)
        val builder = StringBuilder()
        builder.append(getColoredText("V", "#4885ed"))
                .append(getColoredText("d", "#db3236"))
                .append(getColoredText("z", "#f4c20d"))
                .append(getColoredText("h", "#4885ed"))
                .append(getColoredText("o", "#3cba54"))
                .append(getColoredText("s", "#db3236"))

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            header.findViewById<TextView>(R.id.name).text = Html.fromHtml(builder.toString(),Html.FROM_HTML_MODE_LEGACY)
        } else {
            header.findViewById<TextView>(R.id.name).text = Html.fromHtml(builder.toString())
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        return NavigationUI.navigateUp(navController, drawerLayout)
    }

    fun setLoadingPanelVisibility(visible: Boolean){
        binding.loadingPanel.visibility = if(visible){
            window.setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
            View.VISIBLE
        } else {
            window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
            View.GONE
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        TODO("Not yet implemented")
    }

    private fun getColoredText(text: String, color: String): String{
        return StringBuilder("<font color=").append(color)
                .append(">").append(text).append("</font>").toString()
    }

}