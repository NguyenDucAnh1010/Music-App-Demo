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
import com.ducanh.musicappdemo.ui.fragment.favorite.FavoriteFragment
import com.ducanh.musicappdemo.ui.fragment.mymusic.MyMusicFragment
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

        binding.navHeader.ivClose.setOnClickListener {
            binding.main.closeDrawer(GravityCompat.START)
        }

        val menuItems = listOf(
            MenuItem(R.drawable.ic_bulb_on, R.string.kham_pha),
            MenuItem(R.drawable.ic_user, R.string.nhac_cua_toi),
            MenuItem(R.drawable.ic_heart, R.string.bai_hat_yeu_thich),
            MenuItem(R.drawable.ic_globe_americas, R.string.ngon_ngu)
        )

        binding.rvMenu.layoutManager = LinearLayoutManager(this)
        binding.rvMenu.adapter = MenuAdapter(menuItems,this)

        replaceFragment(DiscoverFragment())
    }

    override fun onItemClick(menuItem: MenuItem) {
        when(menuItem.icon){
            R.drawable.ic_bulb_on -> replaceFragment(DiscoverFragment())
            R.drawable.ic_user -> replaceFragment(MyMusicFragment())
            R.drawable.ic_heart -> replaceFragment(FavoriteFragment())
            else -> replaceFragment(DiscoverFragment())
        }
        binding.main.closeDrawer(GravityCompat.START)
    }

    private fun replaceFragment(fragment: Fragment) {
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.fragment_container, fragment)
        fragmentTransaction.commit()
    }
}