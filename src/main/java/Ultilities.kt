import com.google.gson.Gson
import java.io.BufferedInputStream
import java.io.ByteArrayOutputStream
import java.io.File
import java.lang.Exception
import java.net.URL
import java.nio.file.Path
import java.util.*

const val BufferSize = 10 * 1024

fun String.downloadRaw(to: String){
    File(to).writeBytes(downloadAsBytes())
}

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
    return String(Base64.getEncoder().encode(downloadAsBytes()))
}

fun String.renameTo(newName: String){
    File(this).renameTo(File(newName))
}

fun String.readFileAsBytes(): ByteArray{
    return File(this).readBytes()
}

fun String.readFile(): String{
    return File(this).readText()
}

fun String.createFolderIfNotExist() : String{
    val file = File(this)
    if (!file.exists()) {
        file.parent?.createFolderIfNotExist()
        file.mkdir()
    }
    return this
}

fun Path.createFolderIfNotExist() : String{
    return this.toString().createFolderIfNotExist()
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

fun String.fileNameFromUrl() = this.substring(this.indexOfLast { it == '/' } + 1)

fun Any.saveTo(path: String){
    val json = Gson().toJson(this)
    File(path).writeText(json)
}

fun String.writeTo(path: String){
    File(path).writeText(this)
}

fun String.copyChildrenTo(destination: String, extension: String){
    val list = File(this).listFiles()

    for(x in list){
        if(x.name.endsWith(extension)){
            x.copyTo(Path.of(destination, x.name).toFile())
        }
    }
}

fun newGuid() = UUID.randomUUID().toString().replace("-", "")