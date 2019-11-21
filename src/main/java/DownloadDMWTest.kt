import org.jsoup.Jsoup
import org.jsoup.nodes.Element
import java.lang.Exception
import java.nio.file.Paths

fun main() {
    val states = mapOf(
            "illinois" to "Illinois",
            "indiana" to "Indiana",
            "iowa" to "Iowa",
            "kansas" to "Kansas",
            "kentucky" to "Kentucky",
            "louisiana" to "Louisiana",
            "maine" to "Maine",
            "maryland" to "Maryland",
            "massachusetts" to "Massachusetts",
            "michigan" to "Michigan",
            "minnesota" to "Minnesota",
            "mississippi" to "Mississippi",
            "missouri" to "Missouri",
            "montana" to "Montana",
            "nebraska" to "Nebraska",
            "nevada" to "Nevada",
            "new-hampshire" to "New Hampshire",
            "new-jersey" to "New Jersey",
            "new-mexico" to "New Mexico",
            "new-york" to "New York",
            "north-carolina" to "North Carolina",
            "north-dakota" to "North Dakota",
            "ohio" to "Ohio",
            "oklahoma" to "Oklahoma",
            "oregon" to "Oregon",
            "pennsylvania" to "Pennsylvania",
            "rhode-island" to "Rhode Island",
            "south-carolina" to "South Carolina",
            "south-dakota" to "South Dakota",
            "tennessee" to "Tennessee",
            "texas" to "Texas",
            "utah" to "Utah",
            "vermont" to "Vermont",
            "virginia" to "Virginia",
            "washington" to "Washington",
            "west-virginia" to "West Virginia",
            "wisconsin" to "Wisconsin",
            "wyoming" to "Wyoming"
    )
    val types = mapOf(
            "" to "Car",
            "motorcycle/" to "Motorcycle",
            "cdl/" to "CDL"
    )

    fun Element.getQuestion(correct: Int): Question{
        val title = this.select(".question2").text()
        val answers = this.select(".answer .text .answers").map { it.text() }
        val explanation = this.select(".explanation").text().findOne("(.*)more$").trim()

        var image = ""

        this.select(".answer .images img").firstOrNull()?.also {
            try{
                image = it.attr("src").downloadAsBase64()
            }catch (ex: Exception){
                println("The image ${it.attr("src")} in question $title from ${this.ownerDocument().location()} could not be downloaded due to the error ${ex.message}")
            }
        }

        return Question(title, answers, correct, explanation, image)
    }

    fun downloadQuestions(state: String, type: String, testId: Int, page: Int): Questions{
        val url = "https://www.dmv-written-test.com/$state/${type}practice-test-${testId}.html?page=$page"
        val html = Jsoup.connect(url).get()
        val body = html.body()

        val correct = html.html().findOne("var correctanswers =\\[(.*)\\];")
                .replace("\"","")
                .split(",").map { it[0] - 'A' }
        val title = body.select(".title-container .title").text()

        val answers = body.select("#contentwrapper .questions .questiondiv")
                .mapIndexed { index, element -> element.getQuestion(correct[index]) }
                .toMutableList()

        val maxQuestionIndex = body.select(".questiondiv .question1").map {
            it.text().findOne("(\\d+)").toInt()
        }.max()!!

        val questionCount = body.select("#title-container .stats tr")[1].select("td")[1].text().toInt()

        if(maxQuestionIndex < questionCount){
            answers.addAll(downloadQuestions(state, type, testId, page + 1).questions)
        }

        return Questions(title, answers)
    }

    fun downloadQuestions(state: String, type: String): Int{
        val stateFolder = Paths.get("download", "dmw", states[state]).toString()
        stateFolder.createFolderIfNotExist()

        val outputFolder = Paths.get(stateFolder, types[type]).toString()
        outputFolder.createFolderIfNotExist()

        println("Downloading ${states[state]} ${types[type]}")

        var test = 0

        while (true){
            try{
                test ++
                val jsonPath = Paths.get(outputFolder, "test_$test.json").toString()
                if(jsonPath.fileExist()) continue

                println("Downloading ${states[state]} ${types[type]} test_$test.json")

                val questions = downloadQuestions(state, type, test, 1)
                questions.saveTo(jsonPath)
            }catch (ex: org.jsoup.HttpStatusException){
                break
            }
        }

        return test - 1
    }

    "download".createFolderIfNotExist()
    Paths.get("download", "dmw").toString().createFolderIfNotExist()

    val summaries = mutableListOf<StateSummary>()
    states.keys.forEach {
        state ->
        run {
            val sums = mutableListOf<TypeSummary>()
            types.keys.forEach {
                sums.add(TypeSummary(types[it]!!, downloadQuestions(state, it)))
            }
            summaries.add(StateSummary(states[state]!!, sums))
        }
    }

    Summary(summaries).saveTo(Paths.get("download", "dmw", "summary.json").toString())
    println("DONE")
}