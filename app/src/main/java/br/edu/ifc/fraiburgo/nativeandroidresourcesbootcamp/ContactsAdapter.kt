package br.edu.ifc.fraiburgo.nativeandroidresourcesbootcamp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ContactsAdapter(
    private val contactsList: List<Contact>
    ): RecyclerView.Adapter<ContactsAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactsAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.contact_view_item, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ContactsAdapter.ViewHolder, position: Int) {
        holder.bindItem(contactsList[position])
    }

    override fun getItemCount(): Int {
        return contactsList.size
    }

    class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        fun bindItem(contact: Contact) {
            val textName = itemView.findViewById<TextView>(R.id.contact_name)
            val textNumber = itemView.findViewById<TextView>(R.id.contact_number)

            textName.text = contact.name
            textNumber.text = contact.number
        }
    }
}