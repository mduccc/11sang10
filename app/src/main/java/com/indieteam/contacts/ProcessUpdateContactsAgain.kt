package com.indieteam.contacts

import android.app.Activity
import android.content.ContentProviderOperation
import android.database.Cursor
import android.provider.ContactsContract
import android.util.Log

class ProcessUpdateContactsAgain(val context: Activity){
    private lateinit var cursor: Cursor
    val contactsDetails = arrayListOf<ContactsDetails>()
    val uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI

    fun readContacts(){
        var id: Int
        var name: String
        var phone: String
        cursor = context.contentResolver.query(uri, null, null, null, null)
        while (cursor.moveToNext()) {
            id = cursor.getInt(cursor.getColumnIndex(ContactsContract.Data.RAW_CONTACT_ID))
            name = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME))
            phone = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))
            contactsDetails.add(ContactsDetails(id, name, phone))
        }
        cursor.close()
    }

    private fun trimNumber(rawNumber: String): String{
        var numberOnly = rawNumber.replace("[^0-9]".toRegex(), "")
        if(numberOnly.get(0).toString() == "8" && numberOnly.get(1).toString() == "4"){
            Log.d("84: ", "84")
            numberOnly = "+$numberOnly"
            if(numberOnly.get(3).toString() == "0"){
                numberOnly = "+84${numberOnly.substring(4)}"
            }
        }
        return numberOnly
    }

    private fun convert(number: String): String{
        var convered = number
        var needConvert = 1
        var firstOld = ""
        var firstNew = ""
        if(number.get(1).toString() == "8" && number.get(2).toString() == "4")
            firstOld = "0${number.get(3)}${number.get(4)}"
        else
            firstOld = "${number.get(0)}${number.get(1)}${number.get(2)}"

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
            if(number.get(1).toString() == "8" && number.get(2).toString() == "4")
                convered = "+84${firstNew.substring(1)}${number.substring(5)}"
            else
                convered = "$firstNew${number.substring(3)}"
        }
        return convered
    }

    fun updateContacts(){

        for(i in contactsDetails){
            val operation = arrayListOf<ContentProviderOperation>()
            val where = ContactsContract.CommonDataKinds.Phone.NUMBER + "=?"
            val param = arrayOf(i.phone)
            val trimNumber = trimNumber(i.phone)
            val convered = convert(trimNumber)
            Log.d("name: ", i.name)
            Log.d("phone trim: ", trimNumber)
            Log.d("phone converted: ", convered+"\n\n")
            operation.add(ContentProviderOperation.newUpdate(ContactsContract.Data.CONTENT_URI)
                    .withSelection(where, param)
                    .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, convered)
                    .build())
            context.contentResolver.applyBatch(ContactsContract.AUTHORITY, operation)
        }
        Log.d("Update", "Done")
        cursor.close()
    }
}