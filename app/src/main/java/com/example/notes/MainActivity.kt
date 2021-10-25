package com.example.notes

import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.text.Html
import android.view.*
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.databinding.DataBindingUtil
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import com.example.notes.databinding.ActivityMainBinding
import com.example.notes.notesmain.ListType
import com.example.notes.notesmain.NotesMainFragmentDirections
import com.google.android.material.navigation.NavigationView


class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener{

    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        setTextForDrawerHeader()

        setSupportActionBar(binding.toolbar)

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.myNavHostFragment) as NavHostFragment
        navController = navHostFragment.navController

        NavigationUI.setupActionBarWithNavController(this, navController, binding.drawerLayout)
        NavigationUI.setupWithNavController(binding.navView, navController)

        binding.navView.setNavigationItemSelectedListener(this)

//        window.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
//                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
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

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            header.findViewById<TextView>(R.id.name).text = Html.fromHtml(builder.toString(), Html.FROM_HTML_MODE_LEGACY)
        } else {
            header.findViewById<TextView>(R.id.name).text = Html.fromHtml(builder.toString())
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        return NavigationUI.navigateUp(navController, binding.drawerLayout)
    }

    override fun onBackPressed() {
        if(binding.drawerLayout.isDrawerOpen(GravityCompat.START)){
            binding.drawerLayout.closeDrawers()
        }else{
            super.onBackPressed()
        }
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
        when(item.itemId){
            R.id.notes -> {
                navController.navigate(NotesMainFragmentDirections.actionNotesMainFragmentSelf())
                println("---------------notes")
            }
            R.id.reminders -> {
                val action = NotesMainFragmentDirections.actionNotesMainFragmentSelf()
                action.listType = ListType.REMINDERS
                navController.navigate(action)
                println("---------------reminders")
            }
            R.id.editLabels -> {
                NavigationUI.onNavDestinationSelected(item,navController)
            }
            R.id.settings -> {
                println("---------------Settings")
            }
            else -> {
                val action = NotesMainFragmentDirections.actionNotesMainFragmentSelf()
                val listType = ListType.LABEL
                listType.info = item.title.toString()
                action.listType = listType
                navController.navigate(action)
                println("---------------Label")
            }
        }
        binding.drawerLayout.closeDrawers()
        return true
    }

    private fun getColoredText(text: String, color: String): String{
        return StringBuilder("<font color=").append(color)
                .append(">").append(text).append("</font>").toString()
    }

}