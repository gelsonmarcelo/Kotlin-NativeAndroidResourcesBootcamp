package br.edu.ifc.fraiburgo.nativeandroidresourcesbootcamp

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.CalendarContract
import android.provider.ContactsContract
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import br.edu.ifc.fraiburgo.nativeandroidresourcesbootcamp.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var register: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        registerForActivityResult()
        assertSelfPermissionToReadContacts()
        setupListeners()
    }

    private fun registerForActivityResult() {
        register =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                onActivityResult(IMAGE_PICK_ID, result)
            }
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
            READ_CONTACT_ID
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

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when (requestCode) {
            READ_EXTERNAL_STORAGE_ID -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    pickImageFromGallery()
                } else {
                    Toast.makeText(this, "Permission not granted", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun pickImageFromGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*" //Everything that's an image

        register.launch(intent)
    }

    private fun onActivityResult(requestCode: Int, result: ActivityResult) {
        if (result.resultCode == Activity.RESULT_OK) {
            val data = result.data
            when (requestCode) {
                IMAGE_PICK_ID -> {
                    binding.imageView.setImageURI(data?.data)
                }
            }
        }
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

        binding.pickGalleryButton.setOnClickListener {
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                pickImageFromGallery()
            } else {
                val permissions = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
                requestPermissions(permissions, READ_EXTERNAL_STORAGE_ID)
            }
        }
    }

    companion object {
        private const val READ_CONTACT_ID = 1
        private const val READ_EXTERNAL_STORAGE_ID = 2
        private const val IMAGE_PICK_ID = 3
    }
}
