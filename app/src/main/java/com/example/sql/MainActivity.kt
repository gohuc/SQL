package com.example.sql

import android.os.Bundle
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private lateinit var dbHelper: SQLHelper
    private lateinit var personRepository: PersonRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.test)

        initDatabase()
        addTestData()
        showDataOnScreen()
    }

    private fun initDatabase() {
        dbHelper = SQLHelper(this)
        personRepository = PersonRepository(dbHelper)

        dbHelper.writableDatabase
    }

    private fun addTestData() {
        try {
            personRepository.addTestData()
            Toast.makeText(this, "Тестовые данные добавлены", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Toast.makeText(this, "Ошибка добавления данных: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    private fun showDataOnScreen() {
        try {
            val persons = personRepository.getAllPersons()
            val container = findViewById<LinearLayout>(R.id.text)
            if (container == null) {
                showSimpleDisplay(persons)
            } else {
                showInContainer(container, persons)
            }

        } catch (e: Exception) {
            Toast.makeText(this, "Ошибка отображения: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    private fun showInContainer(container: LinearLayout, persons: List<Person>) {
        container.removeAllViews()

        if (persons.isEmpty()) {
            val emptyText = TextView(this).apply {
                text = "Нет данных в базе"
                textSize = 18f
                setPadding(16, 16, 16, 16)
            }
            container.addView(emptyText)
            return
        }
        val title = TextView(this).apply {
            text = "Люди в базе данных:"
            textSize = 20f
            setPadding(16, 16, 16, 8)
        }
        container.addView(title)
        persons.forEach { person ->
            val personView = TextView(this).apply {
                text = "${person.id}. ${person.name}, возраст: ${person.age}"
                textSize = 16f
                setPadding(32, 8, 16, 8)
            }
            container.addView(personView)
        }
        val summary = TextView(this).apply {
            text = "Всего записей: ${persons.size}"
            textSize = 16f
            setPadding(16, 16, 16, 16)
        }
        container.addView(summary)
    }

    private fun showSimpleDisplay(persons: List<Person>) {
        val personList = persons.joinToString("\n") {
            "${it.id}. ${it.name}, возраст: ${it.age}"
        }

        val message = if (persons.isNotEmpty()) {
            "Найдено ${persons.size} записей:\n$personList"
        } else {
            "База данных пуста"
        }
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
        persons.forEach { person ->
            println("Person: ${person.id}, ${person.name}, ${person.age}")
        }
    }

    override fun onDestroy() {
        dbHelper.close()
        super.onDestroy()
    }
}