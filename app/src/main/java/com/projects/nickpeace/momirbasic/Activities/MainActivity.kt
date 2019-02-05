package com.projects.nickpeace.momirbasic.Activities

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.support.v4.app.LoaderManager
import android.support.v4.content.Loader
import android.util.Log
import android.widget.*
import com.projects.nickpeace.momirbasic.CardLoader
import com.projects.nickpeace.momirbasic.GlideApp
import com.projects.nickpeace.momirbasic.R
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), LoaderManager.LoaderCallbacks<String> {

    private val LOG_TAG = "MainActivity"

    // Member variables
    private var cmcValue = 0
    private var isCardDisplayed = false;

    // Declare our views
    private lateinit var cmcTextView:TextView

    private lateinit var layoutCmcWheel:LinearLayout
    private lateinit var btnCmcDecrease:Button
    private lateinit var btnCmcIncrease:Button
    private lateinit var btnCastCreature:Button

    private lateinit var btnMomirVig:Button
    private lateinit var btnHistory:Button
    private lateinit var btnSettings:Button

    private lateinit var imgViewCard:ImageView
    private lateinit var layoutCardHolder:LinearLayout
    private lateinit var btnCloseCardView:Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Find our views
        // CMC wheel views
        layoutCmcWheel = findViewById(R.id.layout_cmcwheel)
        cmcTextView = findViewById(R.id.text_cmc)
        btnCmcDecrease = findViewById(R.id.btn_cmc_decrease)
        btnCmcIncrease = findViewById(R.id.btn_cmc_increase)
        btnCastCreature = findViewById(R.id.btn_cast)

        // Navigation views
        btnMomirVig = findViewById(R.id.btn_momirvig)
        btnHistory = findViewById(R.id.btn_history)
        btnSettings = findViewById(R.id.btn_settings)

        //Cardholder views
        layoutCardHolder = findViewById(R.id.layout_cardholder)
        imgViewCard = findViewById(R.id.imageview_card)
        btnCloseCardView = findViewById(R.id.btn_closecardview)

        // Add our listeners to our buttons in the CMC wheel
        btnCmcDecrease.setOnClickListener { changeCmc(it) }
        btnCmcIncrease.setOnClickListener { changeCmc(it) }
        btnCastCreature.setOnClickListener { getCreatureCard(it) }

        // Setup our bottom navigation listeners
        btnMomirVig.setOnClickListener { onClickNavButton(it) }
        btnHistory.setOnClickListener { onClickNavButton(it) }
        btnSettings.setOnClickListener { onClickNavButton(it) }

        btnCloseCardView.setOnClickListener { hideCardView() }

        // Set up loader
        if(supportLoaderManager.getLoader<Any>(0) != null)run {
            supportLoaderManager.initLoader(0, null, this)
        }
    }

    //
    // This function handles whenever one of our bottom navigation buttons is clicked
    //
    private fun onClickNavButton(it: View?) {

        when(it?.id){
            R.id.btn_momirvig -> displayCardView(getString(R.string.momir_vig_avatar_uri))
            R.id.btn_history -> makeToast("TODO: History activity")
            R.id.btn_settings -> startActivity(Intent(this,SettingsActivity::class.java))
        }
        // TODO - Idea for History; what if it was a horizontal scrolling image view?
    }

    //
    // Takes in an image Uri and displays it in a card ImageView
    //
    private fun displayCardView(imageURI: String) {

        // Load the image into the imageview
        GlideApp
            .with(this)
            .load(imageURI)
            .placeholder(R.drawable.loading_timewalk)
            .into(imageview_card)

        // Disable all the other buttons so they can't be clicked while looking at the cardview
        disableMainButtons()
        toggleCloseButton()

        layoutCardHolder.visibility = View.VISIBLE
        isCardDisplayed = true

    }

    //
    // Hides the card Fragment
    //
    private fun hideCardView(){

        // Re-enable all the buttons
        enableMainButtons()
        toggleCloseButton()

        layoutCardHolder.visibility = View.GONE
        isCardDisplayed = false
    }

    //
    // Makes our coroutine call to go get a creature card
    //
    private fun getCreatureCard(it: View?) {

        // Check to see if we are connected to the network. If we are, then we can make the API call
        val connMgr = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo: NetworkInfo? = connMgr.activeNetworkInfo

        // If we're able to connect to the internet, we go fetch a creature of the given cmc and
        // disable the button while its happening, so the user can't spam calls
        if(networkInfo != null && networkInfo.isConnected){

            val queryBundle = Bundle()
            queryBundle.putString("cmcToGet",cmcTextView.text.toString())
            supportLoaderManager.restartLoader(0, queryBundle, this)
            btnCastCreature.isEnabled = false
            btnCastCreature.text = getString(R.string.btntext_fetching)

        } else{
            makeToast("Please get an internet connection and try again!")
        }
    }

    //
    // Function that fires whenever you want to increase or decrease the cmc you want to cast
    //
    private fun changeCmc(button: View?) {

        // Checks the ID of the button that is passed in to determine how to change the CMC
        when(button?.id){
            R.id.btn_cmc_decrease ->{
                if (cmcValue > 0) {
                    cmcValue--
                    cmcTextView.text = cmcValue.toString()
                }
            }
            R.id.btn_cmc_increase ->{
                if (cmcValue < 16) {
                    cmcValue++
                    cmcTextView.text = cmcValue.toString()
                }
            }
        }
    }

    //
    // Overridden loader functions
    //
    override fun onCreateLoader(p0: Int, p1: Bundle?): CardLoader {
        var cmcToGet = ""

        if(p1 != null){
            cmcToGet = p1.getString("cmcToGet")
        }

        return CardLoader(this, cmcToGet)
    }

    override fun onLoadFinished(p0: Loader<String>, p1: String?) {
        Log.i(LOG_TAG,p1)
        btnCastCreature.isEnabled = true
        btnCastCreature.text = getString(R.string.string_cast_creature)
    }

    override fun onLoaderReset(p0: Loader<String>) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    //
    // Utility functions
    //

    // Make summoning toast messages easier
    fun makeToast(message:String){
        Toast.makeText(this,message,Toast.LENGTH_SHORT).show()
    }

    // Disables all the buttons not specific to the card fragment
    private fun disableMainButtons() {
        btnSettings.visibility = View.GONE
        btnMomirVig.visibility = View.GONE
        btnHistory.visibility = View.GONE
        layoutCmcWheel.visibility = View.GONE
        btnCastCreature.visibility = View.GONE
    }

    // Enables main buttons
    private fun enableMainButtons() {
        btnSettings.visibility = View.VISIBLE
        btnMomirVig.visibility = View.VISIBLE
        btnHistory.visibility = View.VISIBLE
        layoutCmcWheel.visibility = View.VISIBLE
        btnCastCreature.visibility = View.VISIBLE
    }

    // Toggles the visibility of the close button for the card fragment
    private fun toggleCloseButton() {
        btnCloseCardView.visibility = when(isCardDisplayed) {
            true -> View.GONE
            false -> View.VISIBLE
        }
    }
}
