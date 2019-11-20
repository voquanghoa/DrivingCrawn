import com.google.gson.Gson
import java.io.BufferedInputStream
import java.io.ByteArrayOutputStream
import java.io.File
import java.lang.Exception
import java.net.URL
import java.util.*

const val BufferSize = 10 * 1024

fun String.downloadAsBytes(): ByteArray{
    val url = URL(this)
    val urlConnection = url.openConnection()

    val inStream = BufferedInputStream(urlConnection.getInputStream())
    val outStream = ByteArrayOutputStream()
    val buffer = ByteArray(BufferSize)
    var read = 0
    do{
        read = inStream.read(buffer, 0, BufferSize)
        if(read>0){
            outStream.write(buffer, 0, read)
        }
    }while (read>0)

    return outStream.toByteArray()
}

fun String.downloadAsBase64(): String{
    try {
        return String(Base64.getEncoder().encode(downloadAsBytes()))
    }catch (ex: Exception){
        throw ex
    }
}

fun String.createFolderIfNotExist() {
    val file = File(this)
    if (!file.exists()) {
        file.mkdir()
    }
}

fun String.fileExist(): Boolean{
    return File(this).exists()
}

fun deleteIfExist(name: String) {
    val file = File(name)
    if (file.exists()) {
        file.deleteRecursively()
    }
}

fun String.downloadAsString(): String{
    return String(downloadAsBytes())
}

fun String.findOne(regex: String): String{
    return regex.toRegex().find(this)!!.groupValues[1]
}

fun String.toSlug() = toLowerCase()
        .replace("\n", " ")
        .replace("[^a-z\\d\\s]".toRegex(), " ")
        .split(" ")
        .joinToString("-")
        .replace("-+".toRegex(), "-")

fun Any.saveTo(path: String){
    val json = Gson().toJson(this)
    File(path).writeText(json)
}