package br.edu.ifc.fraiburgo.nativeandroidresourcesbootcamp

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.CalendarContract
import android.provider.ContactsContract
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import br.edu.ifc.fraiburgo.nativeandroidresourcesbootcamp.databinding.ActivityMainBinding

private const val REQUEST_CONTACT_ID = 1

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupListeners()
        assertSelfPermissionToReadContacts()
    }

    override fun onResume() {
        super.onResume()
        setContacts()
    }

    private fun assertSelfPermissionToReadContacts() {
        if (!wasContactPermissionGranted()
        ) {
            askForContactPermission()
        }
    }

    private fun wasContactPermissionGranted(): Boolean {
        return ActivityCompat.checkSelfPermission(
            this,
            Manifest.permission.READ_CONTACTS
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun askForContactPermission() {
        ActivityCompat.requestPermissions(
            this, arrayOf(Manifest.permission.READ_CONTACTS),
            REQUEST_CONTACT_ID
        )
    }

    private fun setContacts() {
        val contactsList: MutableList<Contact> = mutableListOf()

        if (!wasContactPermissionGranted()) {
            contactsList.add(
                Contact(
                    name = "Access to contacts not granted!",
                    number = "Couldn't to import contacts"
                )
            )
        } else {
            val cursor = contentResolver.query(
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                null,
                null,
                null,
                null
            )

            if (cursor != null) {
                while (cursor.moveToNext()) {

                    val displayName = cursor.getColumnIndex(
                        ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME
                    )
                    val displayNumber = cursor.getColumnIndex(
                        ContactsContract.CommonDataKinds.Phone.NUMBER
                    )

                    contactsList.add(
                        Contact(cursor.getString(displayName), cursor.getString(displayNumber))
                    )
                }
                cursor.close()
            }
        }

        val adapter = ContactsAdapter(contactsList)

        binding.contactsRecyclerView.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        binding.contactsRecyclerView.adapter = adapter
    }

    private fun setupListeners() {
        binding.setEventOnCalendar.setOnClickListener {
            val intent = Intent(Intent.ACTION_INSERT)
                .setData(CalendarContract.Events.CONTENT_URI)
                .putExtra(CalendarContract.Events.TITLE, "Bootcamp everis")
                .putExtra(CalendarContract.Events.EVENT_LOCATION, "on line")
                .putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, System.currentTimeMillis())
                .putExtra(CalendarContract.EXTRA_EVENT_END_TIME, System.currentTimeMillis() + (60 * 60 * 1000))

            startActivity(intent)
        }
    }
}