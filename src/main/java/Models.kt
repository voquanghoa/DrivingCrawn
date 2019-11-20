data class Topic(var text: String, var link: String)
data class Question(val title: String,
                    var answer: List<String>,
                    var correct: Int,
                    var explanation: String,
                    var images: String)

data class Questions(var title: String, var question: List<Question>)