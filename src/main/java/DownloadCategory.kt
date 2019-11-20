import com.google.gson.Gson
import org.jsoup.Jsoup
import org.jsoup.nodes.Element
import java.io.BufferedInputStream
import java.io.ByteArrayOutputStream
import java.io.File
import java.net.URL
import java.util.Base64.getEncoder

data class Topic(var text: String, var link: String)
data class Question(val title: String,
                    var answer: List<String>,
                    var correct: Int,
                    var explanation: String,
                    var images: String)

data class Questions(var title: String, var question: List<Question>)

fun main() {


    fun Element.getTopic(): Topic{
        val tds = this.select("td")

        val text = tds[0].text().replace("^\\d+\\.\\s".toRegex(), "")
        val href = tds[2].select("a")[1].attr("href")

        return Topic(text, href)
    }

    fun String.download(): String{
        val url = URL(this)
        val urlConnection = url.openConnection()

        val inStream = BufferedInputStream(urlConnection.getInputStream())
        val outStream = ByteArrayOutputStream()
        val BufferSize = 10 * 1024
        val buffer = ByteArray(BufferSize)
        var read = 0
        do{
            read = inStream.read(buffer, 0, BufferSize)
            if(read>0){
                outStream.write(buffer, 0, read)
            }
        }while (read>0)

        val encoded = getEncoder().encode(outStream.toByteArray())
        return String(encoded)
    }

    fun String.toSlug() = toLowerCase()
            .replace("\n", " ")
            .replace("[^a-z\\d\\s]".toRegex(), " ")
            .split(" ")
            .joinToString("-")
            .replace("-+".toRegex(), "-")

    fun Element.getQuestion(): Question{
        val title = this.select("b")[1].text().trim()
        val answers = this.select("span").map {
            it.text().replace("^[A-D]\\.".toRegex(), "").trim()
        }

        val correct = this.select("span").indexOfFirst {
            it.attr("style").length > 20
        }

        val explanation = this.select("p i").text().trim()
        var image = ""
        this.select("img").firstOrNull()?.also {
            val src = it.attr("src")
            image = "https://usdriving.net/$src".download()
        }

        return Question(title, answers, correct, explanation, image)
    }

    fun downloadTopics(): List<Topic>{
        val url = "https://usdriving.net/qcategory_list.php"
        val doc = Jsoup.connect(url).get().body()
        return doc.select("table tr").map { it.getTopic() }
    }

    fun Topic.download(): Questions{
        val url = "https://usdriving.net/${this.link}"
        val doc = Jsoup.connect(url).get().body()

        val questions = doc.select("div").filter {
            it.attr("style").equals("border-bottom:1px solid #bbbbbb; padding:15px 0px;line-height:25px;font-size:14px;")
        }.map {
            it.getQuestion()
        }

        return Questions(this.text, questions)
    }

    fun Any.saveTo(path: String){
        val json = Gson().toJson(this)
        File(path).writeText(json)
    }

    val trs = downloadTopics()

    trs.forEach {
        val questions = it.download()

        questions.saveTo("${it.text.toSlug()}.json")

        println("Download ${it.text} to ${it.text.toSlug() + ".json"}")
    }

    println(trs.joinToString("\n"))
}