package com.insteem.ipfreely.steem

import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DefaultItemAnimator
import com.insteem.ipfreely.steem.Databases.NotificationsBusyDb
import com.insteem.ipfreely.steem.Enums.AdapterToUseFor
import com.insteem.ipfreely.steem.HelperClasses.NotificationsWebSocketListener
import com.insteem.ipfreely.steem.HelperClasses.swipecommonactionsclass
import com.insteem.ipfreely.steem.Interfaces.NotificationsInterface
import com.insteem.ipfreely.steem.jsonclasses.BusyNotificationJson
import kotlinx.android.synthetic.main.activity_notifications_busy_d.*
import kotlinx.android.synthetic.main.content_notifications_busy_d.*
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.WebSocket
import okio.ByteString
import org.json.JSONArray
import org.json.JSONObject
import java.util.*

class NotificationsBusyD : AppCompatActivity(),NotificationsInterface {
    override fun onOpen(webSocket: WebSocket?, response: Response?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onMessage(webSocket: WebSocket?, text: String?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onMessage(webSocket: WebSocket, bytes: ByteString) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onClosing(webSocket: WebSocket?, code: Int, reason: String?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onFailure(webSocket: WebSocket?, t: Throwable?, response: Response?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun dbLoaded() {
        //client = null
        //websocket is run on the background thread so run ui updates on the right thread
        runOnUiThread {
            adapter?.notificationsBusyHelperFunctions?.resetDate()
            val db = NotificationsBusyDb(this@NotificationsBusyD)
            //var ite = db.GetAllQuestions()
            //var sor = sortList(ite)
            //adapter?.notificationsBusyHelperFunctions?.add(sor)
            //adapter?.notificationsBusyHelperFunctions?.add(db.GetAllQuestions().reversed())
            adapter?.notificationsBusyHelperFunctions?.add(sortList(db.GetAllQuestions()))
            //adapter?.notifyDataSetChanged()
            sw?.makeswipestop()
        }


    }

    private var adapter: AllRecyclerViewAdapter? = null
    private var activity: Context? = null
    private var client: OkHttpClient? = null
    private var sw:swipecommonactionsclass? = null
    //internal var recyclerView: RecyclerView? = null
    var username : String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        MiscConstants.ApplyMyThemeArticle(this@NotificationsBusyD)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notifications_busy_d)
        setSupportActionBar(toolbar)

        sw  = swipecommonactionsclass(actvity_notifications_refresh)
        adapter = AllRecyclerViewAdapter(this, ArrayList(), list, dateanim, AdapterToUseFor.notifications)
        //var sw = swipecommonactionsclass(activity_feed_swipe_refresh_layout_favs)
        val db = NotificationsBusyDb(applicationContext)
        actvity_notifications_refresh.setOnRefreshListener {
            //var ad : List<BusyNotificationJson.Result> = db.GetAllQuestions().reversed()
            //adapter?.notificationsBusyHelperFunctions?.add(db.GetAllQuestions().reversed())
            client = OkHttpClient()
            start()
            //sw.makeswipestop()
        }
        //adapter?.setEmptyView(view?.findViewById(R.id.toDoEmptyView))

        list.itemAnimator = DefaultItemAnimator()
        list.adapter = adapter
        val sharedPreferences = getSharedPreferences(CentralConstants.sharedprefname, 0)
        username = sharedPreferences?.getString(CentralConstants.username, null)
        //key = sharedPreferences?.getString(CentralConstants.key, null)
        //display(CentralConstantsOfSteem.getInstance().jsonArray)


        //adapter?.notificationsBusyHelperFunctions?.add(db.GetAllQuestions().reversed())
        //var sor = db.GetAllQuestions().sortedWith(compareBy({it.timestamp}))
        adapter?.notificationsBusyHelperFunctions?.add(sortList(db.GetAllQuestions()))
    }

    /**
     * @param data list with notifications to sort
     * returns sorted according to timestamp
     */
    private fun sortList(data:List<BusyNotificationJson.Result>):List<BusyNotificationJson.Result>{
        return data.sortedWith(compareByDescending({it.timestamp}))
    }


    private fun start() {
        val request = Request.Builder().url("wss://api.busy.org/").build()
        val listener = NotificationsWebSocketListener(username,this@NotificationsBusyD)
        val ws = client?.newWebSocket(request, listener)


        var ar = JSONArray()
        ar.put(username)
        var ob = JSONObject()
        ob.put("id",2)
        ob.put("jsonrpc","2.0")
        ob.put("method","get_notifications")
        ob.put("params",ar)

        var obs = ob.toString()
        ws?.send(obs)
        adapter?.clear()
        adapter?.notifyDataSetChanged()
        //client?.dispatcher()?.executorService()?.shutdown()
    }

}
