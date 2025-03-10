package com.ducanh.musicappdemo.ui

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.ducanh.musicappdemo.R
import com.ducanh.musicappdemo.data.entity.MenuItem
import com.ducanh.musicappdemo.databinding.ActivityMainBinding
import com.ducanh.musicappdemo.ui.adapter.MenuAdapter
import com.ducanh.musicappdemo.ui.adapter.OnMenuClickListener
import com.ducanh.musicappdemo.ui.fragment.discover.DiscoverFragment
import com.google.android.material.navigation.NavigationView

class MainActivity : AppCompatActivity(), OnMenuClickListener {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityMainBinding.inflate(layoutInflater)
        binding.lifecycleOwner = this
        binding.executePendingBindings()
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        val toggle = ActionBarDrawerToggle(
            this,
            binding.main,
            binding.toolbar,
            R.string.open_nav,
            R.string.close_nav
        )
        binding.main.addDrawerListener(toggle)
        toggle.syncState()
        val menuItems = listOf(
            MenuItem(R.drawable.ic_bulb_on, "Home"),
            MenuItem(R.drawable.ic_user, "Profile"),
            MenuItem(R.drawable.ic_heart, "Settings"),
            MenuItem(R.drawable.ic_globe_americas, "Logout")
        )

        // Setup RecyclerView
        binding.rvMenu.layoutManager = LinearLayoutManager(this)
        binding.rvMenu.adapter = MenuAdapter(menuItems,this)

        replaceFragment(DiscoverFragment())

        if (savedInstanceState == null) {
//            supportFragmentManager.beginTransaction()
//                .replace(R.id.fragment_container, HomeFragment()).commit()
//            navigationView.setCheckedItem(R.id.nav_home)
        }
    }

    override fun onItemClick(menuItem: MenuItem) {
        binding.main.closeDrawer(GravityCompat.START)
    }

    private fun replaceFragment(fragment: Fragment) {
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.fragment_container, fragment)
        fragmentTransaction.commit()
    }
}