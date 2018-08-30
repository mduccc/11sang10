package com.indieteam.contacts

import android.content.ContentProviderOperation
import android.database.Cursor
import android.provider.ContactsContract
import android.util.Log
import android.widget.Toast
import es.dmoral.toasty.Toasty

class ProcessUpdateContacts(private val context: MainActivity){
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
        Log.d("number only", numberOnly)
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
        if (number[0].toString() == "+" && number[1].toString() == "8" && number[2].toString() == "4")
            firstOld = "0${number[3]}${number[4]}${number[5]}"
        else
            firstOld = "${number[0]}${number[1]}${number[2]}${number[3]}"

        when(firstOld){
            //Viettel
            "0162" -> firstNew = "032"
            "0163" -> firstNew = "033"
            "0164" -> firstNew = "034"
            "0165" -> firstNew = "035"
            "0166" -> firstNew = "036"
            "0167" -> firstNew = "037"
            "0168" -> firstNew = "038"
            "0169" -> firstNew = "039"

            //Mobifone
            "0120" -> firstNew = "070"
            "0121" -> firstNew = "079"
            "0122" -> firstNew = "077"
            "0126" -> firstNew = "076"
            "0128" -> firstNew  ="078"

            //Vinaphone
            "0123" -> firstNew = "083"
            "0124" -> firstNew = "084"
            "0125" -> firstNew = "085"
            "0127" -> firstNew = "081"
            "0129" -> firstNew = "082"

            //Vietnamobie
            "0186" -> firstNew = "056"
            "0188" -> firstNew = "058"

            //Gmobie
            "0199" -> firstNew = "059"
            else -> needConvert = 0
        }

        if(needConvert == 1){
            if(number[0].toString() == "+" && number[1].toString() == "8" && number[2].toString() == "4")
                converted = "+84${firstNew.substring(1)}${number.substring(6)}"
            else
                converted = "$firstNew${number.substring(4)}"
        }
        return converted
    }

    fun updateContacts(){
        if (contactsDetails.size >0) {
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