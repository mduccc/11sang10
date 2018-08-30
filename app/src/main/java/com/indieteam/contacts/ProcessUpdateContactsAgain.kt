package com.indieteam.contacts

import android.app.Activity
import android.content.ContentProviderOperation
import android.database.Cursor
import android.provider.ContactsContract
import android.util.Log
import es.dmoral.toasty.Toasty

class ProcessUpdateContactsAgain(private val context: Activity){
    private lateinit var cursor: Cursor
    private val contactsDetails = arrayListOf<ContactsDetails>()
    private val uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI

    fun readContacts(){
        var id: Int
        var name: String
        var phone: String
        try {
            cursor = context.contentResolver.query(uri, null, null, null, null)
            while (cursor.moveToNext()) {
                id = cursor.getInt(cursor.getColumnIndex(ContactsContract.Data.RAW_CONTACT_ID))
                name = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME))
                phone = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))
                contactsDetails.add(ContactsDetails(id, name, phone))
            }
            cursor.close()
        }catch (e: Exception){
            Toasty.error(context, "Your device is not support")

        }
    }

    private fun trimNumber(rawNumber: String): String{
        // remove all if it's not number
        var numberOnly = rawNumber.replace("[^0-9]".toRegex(), "")
        if(numberOnly[0].toString() == "8" && numberOnly[1].toString() == "4"){
            numberOnly = "+$numberOnly"
            if(numberOnly[3].toString() == "0"){
                numberOnly = "+84${numberOnly.substring(4)}"
            }
        }
        return numberOnly
    }

    private fun convert(number: String): String{
        var converted = number
        var needConvert = 1
        val firstOld: String
        var firstNew = ""
        if(number[0].toString() == "+" && number[1].toString() == "8" && number[2].toString() == "4")
            firstOld = "0${number[3]}${number[4]}"
        else
            firstOld = "${number[0]}${number[1]}${number[2]}"

        when(firstOld){
        //Viettel
            "032" -> firstNew = "0162"
            "033" -> firstNew = "0163"
            "034" -> firstNew = "0164"
            "035" -> firstNew = "0165"
            "036" -> firstNew = "0166"
            "037" -> firstNew = "0167"
            "038" -> firstNew = "0168"
            "039" -> firstNew = "0169"

        //Mobifone
            "070" -> firstNew = "0120"
            "079" -> firstNew = "0121"
            "077" -> firstNew = "0122"
            "076" -> firstNew = "0126"
            "078" -> firstNew  ="0128"

        //Vinaphone
            "083" -> firstNew = "0123"
            "084" -> firstNew = "0124"
            "085" -> firstNew = "0125"
            "081" -> firstNew = "0127"
            "082" -> firstNew = "0129"

        //Vietnamobie
            "056" -> firstNew = "0186"
            "058" -> firstNew = "0188"

        //Gmobie
            "059" -> firstNew = "0199"
            else -> needConvert = 0
        }

        if(needConvert == 1){
            if(number[0].toString() == "+" && number[1].toString() == "8" && number[2].toString() == "4")
                converted = "+84${firstNew.substring(1)}${number.substring(5)}"
            else
                converted = "$firstNew${number.substring(3)}"
        }
        return converted
    }

    fun updateContacts(){
        if(contactsDetails.size > 0) {
            for (i in contactsDetails) {
                val operation = arrayListOf<ContentProviderOperation>()
                val where = ContactsContract.CommonDataKinds.Phone.NUMBER + "=?"
                val param = arrayOf(i.phone)
                val trimNumber = trimNumber(i.phone)
                val converted = convert(trimNumber)
                Log.d("name: ", i.name)
                Log.d("phone origin: ", i.phone)
                Log.d("phone trim: ", trimNumber)
                Log.d("phone converted: ", converted + "\n\n")
                operation.add(ContentProviderOperation.newUpdate(ContactsContract.Data.CONTENT_URI)
                        .withSelection(where, param)
                        .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, converted)
                        .build())
                context.contentResolver.applyBatch(ContactsContract.AUTHORITY, operation)
            }
            Log.d("Update", "Done")
        }
    }
}