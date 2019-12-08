import org.jsoup.Jsoup
import java.lang.StringBuilder
import java.nio.file.Path

fun main() {
    fun download(url: String, folder: String){

        val fileName = Path.of(folder, url.fileNameFromUrl()).toString()

        if(fileName.fileExist())return

        if(url.endsWith(".html")){
            folder.createFolderIfNotExist()

            val html = Jsoup.connect(url)
                    .get()
                    .body()
                    .selectFirst(".docwrapper")
                    .html()

            "<html><meta http-equiv='Content-Type' content='text/html; charset=utf-8' /><body>$html</body></html>".writeTo(fileName)

        }else{
            url.downloadRaw(fileName)
        }
    }

    fun downloadScript(){
        val url = "http://www.oceangirl.org/archive/html/00.html"
        val doc = Jsoup.connect(url)
                .get()
                .body()
        val lis = doc.select("#scripts li")

        val sb = StringBuilder()

        sb.append("<html><meta http-equiv='Content-Type' content='text/html; charset=utf-8' /><body>")

        sb.append("<strong>Script</strong><br/>")
        sb.append("<ul>")

        for (li in lis){
            val listTitle = li.text().findOne("([^:]*):")
            val links = li.select("a")
            sb.append("<li>$listTitle :")
            for (link in links){
                val href =  link.attr("href")
                val text = link.text()

                download("http://www.oceangirl.org/archive/html/$href", "thuy/${listTitle.toSlug()}")

                sb.append("<a href='${listTitle.toSlug()}/$href'>$text</a>&nbsp;")
            }

            sb.append("</li>")
        }

        sb.append("</ul>")

        sb.append("<br/><br/><br/>")
        sb.append("<strong>Books</strong>")

        val bookSection = doc.select("#book")

        for(i in bookSection.select("a")){
            val href = i.attr("href")
            i.attr("href", "books/$href")
            download("http://www.oceangirl.org/archive/html/$href", "thuy/books")
        }

        sb.append(bookSection.html())

        sb.append("</body></html>")

        sb.toString().writeTo("thuy/index.html")
    }


    downloadScript()
}