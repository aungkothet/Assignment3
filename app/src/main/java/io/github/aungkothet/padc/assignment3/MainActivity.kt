package io.github.aungkothet.padc.assignment3

import android.app.SearchManager
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.AlarmClock
import android.provider.CalendarContract.Events
import android.provider.ContactsContract
import android.provider.MediaStore
import android.support.v7.app.AppCompatActivity
import android.view.View
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    private val REQUEST_VIDEO_CAPTURE = 1

    private val REQUEST_SELECT_PHONE_NUMBER = 11

    fun selectContact(view: View) {
        val intent = Intent(Intent.ACTION_PICK).apply {
            type = ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE
        }
        if (intent.resolveActivity(packageManager) != null) {
            startActivityForResult(intent, REQUEST_SELECT_PHONE_NUMBER)
        }
    }

    fun dispatchTakeVideoIntent(view: View) {
        Intent(MediaStore.ACTION_VIDEO_CAPTURE).also { takeVideoIntent ->
            takeVideoIntent.resolveActivity(packageManager)?.also {
                startActivityForResult(takeVideoIntent, REQUEST_VIDEO_CAPTURE)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    fun startTimer(view: View) {
        val seconds = et_timer_seconds.text.toString()
        if (!seconds.isBlank()) {
            val alarmIntent = Intent(AlarmClock.ACTION_SET_TIMER).apply {
                putExtra(AlarmClock.EXTRA_LENGTH, seconds.toInt())
                putExtra(AlarmClock.EXTRA_SKIP_UI, false)
            }
            if (alarmIntent.resolveActivity(packageManager) != null) {
                startActivity(alarmIntent)
            }
        } else {
            et_timer_seconds.error = "Enter seconds for timer!"
        }

    }

    fun setCalendar(view: View) {
        val intent = Intent(Intent.ACTION_INSERT).apply {
            data = Events.CONTENT_URI
            putExtra(Events.TITLE, "Assignment 3, Week 3")
            putExtra(Events.EVENT_LOCATION, "PADC Myanmar")
            putExtra(Events.DESCRIPTION, "Set by the Implicit intent for showing the result of assignment 3 week 3")
        }
        if (intent.resolveActivity(packageManager) != null) {
            startActivity(intent)
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_VIDEO_CAPTURE && resultCode == RESULT_OK) {
            val videoUri: Uri? = data?.data
            videoView.visibility = View.VISIBLE
            videoView.setVideoURI(videoUri)
            videoView.start()
        } else if (requestCode == REQUEST_SELECT_PHONE_NUMBER && resultCode == RESULT_OK) {
            val contactUri: Uri? = data?.data
            val projection: Array<String> = arrayOf(
                ContactsContract.CommonDataKinds.Phone.NUMBER,
                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME_ALTERNATIVE,
                ContactsContract.CommonDataKinds.Phone.LAST_TIME_CONTACTED,
                ContactsContract.CommonDataKinds.Phone.LAST_TIME_USED
            )
            contentResolver.query(contactUri!!, projection, null, null, null).use { cursor ->
                if (cursor.moveToFirst()) {
                    val numberIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)
                    val displayNameIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)
                    val displayNameAltIndex =
                        cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME_ALTERNATIVE)

                    tv_contact_detail.text = "Number :: ${cursor.getString(numberIndex)}\n" +
                            "Display Name :: ${cursor.getString(displayNameIndex)}\n" +
                            "Display Name Alternate :: ${cursor.getString(displayNameAltIndex)}\n"
                }
            }
        }
    }


    fun performWebSearch(view: View) {
        val searchText = et_web_search.text.toString()
        val intent = Intent(Intent.ACTION_WEB_SEARCH)
        intent.putExtra(SearchManager.QUERY, searchText)
        startActivity(intent)
    }
}
