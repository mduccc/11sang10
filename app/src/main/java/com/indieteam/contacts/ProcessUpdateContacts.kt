package contacts.indieteam.contacts

import android.app.Activity
import android.content.ContentProviderOperation
import android.database.Cursor
import android.os.Build
import android.provider.ContactsContract
import android.util.Log

class ProcessUpdateContacts(val context: Activity){
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
            firstOld = "0${number.get(3)}${number.get(4)}${number.get(5)}"
        else
            firstOld = "${number.get(0)}${number.get(1)}${number.get(2)}${number.get(3)}"

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
            if(number.get(1).toString() == "8" && number.get(2).toString() == "4")
                convered = "+84${firstNew.substring(1)}${number.substring(6)}"
            else
                convered = "$firstNew${number.substring(4)}"
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