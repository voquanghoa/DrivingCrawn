import org.jsoup.Jsoup
import org.jsoup.nodes.Element
import java.nio.file.Paths

fun main() {

    fun Element.getTopic(): Topic{
        val tds = this.select("td")

        val text = tds[0].text().replace("^\\d+\\.\\s".toRegex(), "")
        val href = tds[2].select("a")[1].attr("href")

        return Topic(text, href)
    }

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
            image = "https://usdriving.net/$src".downloadAsBase64()
        }

        return Question(title, answers, correct, explanation, image)
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

    fun downloadTopics(): List<Topic>{
        val url = "https://usdriving.net/qcategory_list.php"
        val doc = Jsoup.connect(url).get().body()
        return doc.select("table tr").map { it.getTopic() }
    }

    Paths.get("download", "usdriving").toString().createFolderIfNotExist()

    val trs = downloadTopics()

    trs.forEach {
        val questions = it.download()

        val jsonPath = Paths.get("download", "usdriving", "${it.text.toSlug()}.json").toString()

        questions.saveTo(jsonPath)

        println("Download ${it.text} to $jsonPath")
    }

    println(trs.joinToString("\n"))

    println("DONE")
}