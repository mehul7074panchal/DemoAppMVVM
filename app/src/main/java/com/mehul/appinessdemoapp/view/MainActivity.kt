package com.mehul.appinessdemoapp.view

import CommonUtility.CommonMethod
import CommonUtility.HttpManager
import CommonUtility.RequestPackage
import android.app.ProgressDialog
import android.app.SearchManager
import android.content.Context
import android.content.DialogInterface
import android.os.AsyncTask
import android.os.Bundle
import android.view.Menu
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.view.MenuItemCompat
import androidx.databinding.DataBindingUtil
import com.mehul.appinessdemoapp.R
import com.mehul.appinessdemoapp.databinding.ActivityMainBinding
import com.mehul.appinessdemoapp.model.Item
import org.json.JSONArray

import java.text.ParseException
import java.util.*


class MainActivity : AppCompatActivity() {
    private var binding: ActivityMainBinding? = null

    var listItem = ArrayList<Item>()

    lateinit var adapter: ItemAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        if (CommonMethod.isOnline(this)) {
            Task().execute()
        }else{
            val alertDialog: AlertDialog = AlertDialog.Builder(this@MainActivity).create()
            alertDialog.setTitle("No Internet")
            alertDialog.setMessage("Turn on your internet!")
            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK"
            ) { _, _ -> finish() }
            alertDialog.show()
        }

    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean { // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_search, menu)
        val searchItem = menu.findItem(R.id.action_search)
        val searchManager =
            getSystemService(Context.SEARCH_SERVICE) as SearchManager
        val searchView: SearchView = MenuItemCompat.getActionView(searchItem) as SearchView
        searchView.setSearchableInfo(
            searchManager.getSearchableInfo(componentName)
        )
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                val text = query.toLowerCase(Locale.getDefault())
                try {
                    adapter.filter(text)
                } catch (e: ParseException) {
                    e.printStackTrace()
                }
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                val text = newText.toLowerCase(Locale.getDefault())
                try {
                    adapter.filter(text)
                } catch (e: ParseException) {
                    e.printStackTrace()
                }
                return false
            }
        })
        return true
    }

    internal inner class Task : AsyncTask<String, Void, ArrayList<Item>>() {

        internal var pd: ProgressDialog? = null

        override fun onPreExecute() {
            super.onPreExecute()
            listItem.clear()

            pd = ProgressDialog(this@MainActivity)
            pd!!.setTitle("Processing...")
            pd!!.setMessage("Please wait.")
            pd!!.setCancelable(false)
            pd!!.isIndeterminate = true
            pd!!.show()


        }

        override fun doInBackground(vararg obj: String): ArrayList<Item> {

            val rp = RequestPackage()
            rp.uri = CommonMethod.Main_URL
            rp.method = "GET"

            val result = HttpManager.getData(rp)


            val jsonArray = JSONArray(result)

            for (i in 0 until jsonArray.length()) {
                val obj = jsonArray.getJSONObject(i)
                val item = Item()
                item.backers = obj.optString("num.backers")
                item.title = obj.optString("title")
                item.by = obj.optString("by")
                listItem.add(item)

            }

            listItem.sortBy { item -> item.title?.toLowerCase() }
            return listItem

        }

        override fun onPostExecute(result: ArrayList<Item>) {
            super.onPostExecute(result)
            if (pd != null)
                pd!!.dismiss()


            adapter = ItemAdapter(result, this@MainActivity)
            binding?.itemAdapter = adapter

        }
    }


}
