import org.jsoup.Jsoup

fun main() {
    val url = "https://www.dmv-written-test.com/connecticut/practice-test-1.html"
    Jsoup.connect(url)
            .get()
            .body()
            .select(".stateblock a")
            .forEach {
                val slug = it.attr("href").findOne("https://www.dmv-written-test.com/([\\w_-]+)/.*")
                val text = it.text()

                println("\"$slug\" to \"$text\",")
            }
}