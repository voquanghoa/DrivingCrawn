import com.google.gson.Gson
import java.io.ByteArrayInputStream
import java.nio.file.Files
import java.nio.file.Paths
import java.util.*
import javax.imageio.ImageIO

fun main() {
    val imageDir = Paths.get("download", "images").toString()
    val ignoreFiles = listOf("summary.json")

    fun replaceImage(jsonPath: String){
        val json = jsonPath.readFile()
        val questions = Gson().fromJson(json, Questions::class.java)
        questions.questions.forEach {
            if(it.images.length > 40){
                try{
                    val binary = Base64.getDecoder().decode(it.images)
                    val stream = ByteArrayInputStream(binary)
                    val input = ImageIO.createImageInputStream(stream)
                    val imageReaders = ImageIO.getImageReaders(input)
                    val next = imageReaders.next()

                    it.images = "${newGuid()}.${next.formatName}"
                    println("Create image:: ${ it.images }")

                    Paths.get(imageDir, it.images).toFile().writeBytes(binary)
                }
                catch (ex: Exception){
                    println(ex)
                }
            }
        }

        questions.saveTo(jsonPath)
    }

    imageDir.createFolderIfNotExist()

    val files = Files.walk(Paths.get("download"))
            .map { it.toString() }
            .filter { it.endsWith(".json") }
            .filter {!ignoreFiles.any { f -> it.endsWith("/$f") }}

    files.forEach { replaceImage(it) }
}