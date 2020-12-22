package by.zenkevich_churun.findcell.prisoner.ui.root.activity

import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import by.zenkevich_churun.findcell.core.util.android.AndroidUtil
import by.zenkevich_churun.findcell.core.util.android.NavigationUtil
import by.zenkevich_churun.findcell.prisoner.R
import com.google.android.material.navigation.NavigationView


internal class PrisonerNavigationManager(
    private val toolbar: Toolbar,
    private val drawer: NavigationView,
    private val controller: NavController ) {

    private var lastDest = 0
    private var actionFrom = 0
    private var action: (() -> Unit)? = null


    fun setup(authorized: Boolean) {
        inflateGraph( if(authorized) R.id.fragmProfile else R.id.fragmAuth )

        drawer.setNavigationItemSelectedListener { item ->
            navigate(item.itemId)
            (drawer.parent as DrawerLayout).closeDrawers()
            true  // Yes, display this item as the selected one.
        }

        controller.addOnDestinationChangedListener { _, dest, _ ->
            select(dest.id)
            setTitle(dest.label)
            setDrawerEnabled()

            lastDest = dest.id
            optionallyNavigateBack()
        }
    }

    /** Authomatically navigate back when destination reaches the given one. **/
    fun doOnce(destId: Int, what: () -> Unit) {
        actionFrom = destId
        action = what
        optionallyNavigateBack()
    }


    private val appName: String
        get() = AndroidUtil.stringByResourceName(toolbar.context, "app_name") ?: ""

    private fun inflateGraph(startDest: Int) {
        val prisonerGraph = controller.navInflater.inflate(R.navigation.prisoner)
        prisonerGraph.startDestination = startDest
        controller.graph = prisonerGraph
    }

    private fun navigate(itemId: Int) {
        when(itemId) {
            R.id.miProfile -> {
                NavigationUtil.navigateIfNotYet(
                    controller,
                    R.id.fragmProfile,
                    R.id.actSelectProfileMenu
                ) { null }
            }

            R.id.miArests  -> {
                NavigationUtil.navigateIfNotYet(
                    controller,
                    R.id.fragmArests,
                    R.id.actSelectArestsMenu
                ) { null }
            }

            R.id.miAuth -> {
                NavigationUtil.navigateIfNotYet(
                    controller,
                    R.id.fragmAuth,
                    R.id.actSelectAuthMenu
                ) { null }
            }

            else -> {
                throw NotImplementedError("Unknown menu item $itemId")
            }
        }
    }

    private fun select(destId: Int) {
        val itemId = when(destId) {
            R.id.fragmProfile -> R.id.miProfile
            R.id.fragmArests  -> R.id.miArests
            R.id.fragmAuth    -> R.id.miAuth
            else -> return
        }

        if(drawer.checkedItem?.itemId != itemId) {
            drawer.setCheckedItem(itemId)
        }
    }

    private fun optionallyNavigateBack() {
        val act = action ?: return
        if(lastDest == actionFrom) {
            action = null
            act()
        }
    }

    private fun setDrawerEnabled() {
        val enabled = (controller.currentDestination?.id != R.id.fragmAuth)
        val lockMode =
            if(enabled) DrawerLayout.LOCK_MODE_UNLOCKED
            else DrawerLayout.LOCK_MODE_LOCKED_CLOSED

        (drawer.parent as DrawerLayout).setDrawerLockMode(lockMode)
    }

    private fun setTitle(title: CharSequence?) {
        toolbar.title = title ?: appName
    }
}