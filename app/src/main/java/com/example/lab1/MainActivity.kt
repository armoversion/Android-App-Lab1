package com.example.lab1

import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

data class Question(
    val textResId: Int,
    val answer: Boolean,
    var isAnswered: Boolean = false
)

class MainActivity : AppCompatActivity() {

    private lateinit var scoreTextView: TextView
    private lateinit var questionTextView: TextView
    private lateinit var trueButton: Button
    private lateinit var falseButton: Button
    private lateinit var nextButton: ImageButton

    private var defaultTextColor: ColorStateList? = null

    private val fullQuestionPool = listOf(
        Question(R.string.question_1, true),
        Question(R.string.question_2, false),
        Question(R.string.question_3, true),
        Question(R.string.question_4, false),
        Question(R.string.question_5, true),
        Question(R.string.question_6, false),
        Question(R.string.question_7, true),
        Question(R.string.question_8, false)
    )

    private val QUIZ_LIMIT = 4

    private lateinit var questionBank: List<Question>

    private var currentIndex = 0
    private var score = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        scoreTextView = findViewById(R.id.score_text_view)
        questionTextView = findViewById(R.id.question_text_view)
        trueButton = findViewById(R.id.true_button)
        falseButton = findViewById(R.id.false_button)
        nextButton = findViewById(R.id.next_button)

        defaultTextColor = questionTextView.textColors

        initQuizSession()

        trueButton.setOnClickListener {
            checkAnswer(true)
        }

        falseButton.setOnClickListener {
            checkAnswer(false)
        }

        nextButton.setOnClickListener {
            moveToNextQuestion()
        }
    }

    private fun initQuizSession() {
        questionBank = fullQuestionPool.shuffled().take(QUIZ_LIMIT)
        currentIndex = 0
        score = 0
        updateScore()
        updateQuestion()
    }

    private fun updateScore() {
        scoreTextView.text = "Бали: $score / ${questionBank.size}"
    }

    private fun updateQuestion() {
        val questionTextResId = questionBank[currentIndex].textResId
        questionTextView.setText(questionTextResId)

        defaultTextColor?.let { questionTextView.setTextColor(it) }
        trueButton.isEnabled = true
        falseButton.isEnabled = true
    }

    private fun moveToNextQuestion() {
        if (questionBank.all { it.isAnswered }) {
            showFinalResults()
            return
        }

        do {
            currentIndex = (currentIndex + 1) % questionBank.size
        } while (questionBank[currentIndex].isAnswered)

        updateQuestion()
    }

    private fun checkAnswer(userPressedTrue: Boolean) {
        val currentQuestion = questionBank[currentIndex]
        val isCorrect = (userPressedTrue == currentQuestion.answer)

        currentQuestion.isAnswered = true

        if (isCorrect) {
            score++
            updateScore()
        }

        val messageResId = if (isCorrect) R.string.correct_toast else R.string.incorrect_toast
        val toast = Toast.makeText(this, messageResId, Toast.LENGTH_SHORT)
        toast.setGravity(Gravity.TOP, 0, 200)
        toast.show()

        val answerText = if (currentQuestion.answer) "Відповідь: Правда" else "Відповідь: Неправда"
        questionTextView.text = answerText

        if (isCorrect) {
            questionTextView.setTextColor(Color.GREEN)
        } else {
            questionTextView.setTextColor(Color.RED)
        }

        trueButton.isEnabled = false
        falseButton.isEnabled = false
    }

    private fun showFinalResults() {
        val percent = (score.toDouble() / questionBank.size) * 100
        val resultMessage = "Тестування завершено!\nВаш результат: $score із ${questionBank.size} (${percent.toInt()}%)"

        questionTextView.text = resultMessage
        defaultTextColor?.let { questionTextView.setTextColor(it) }

        trueButton.isEnabled = false
        falseButton.isEnabled = false
        nextButton.isEnabled = false

        Toast.makeText(this, "Тест пройдено!", Toast.LENGTH_LONG).show()
    }
}