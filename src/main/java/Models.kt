data class Topic(var text: String, var link: String)
data class Question(val title: String,
                    var answers: List<String>,
                    var correct: Int,
                    var explanation: String,
                    var images: String)

data class Questions(var title: String, var questions: List<Question>)

data class TypeSummary(var type: String, var count: Int)
data class StateSummary(var state: String, var summaries: List<TypeSummary>)
data class Summary(var summaries: List<StateSummary>)