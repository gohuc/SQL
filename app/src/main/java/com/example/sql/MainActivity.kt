package com.example.sql

import android.R.attr.hint
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.sql.databinding.TestBinding

class MainActivity : AppCompatActivity() {

    private lateinit var dbHelper: SQLHelper
    private lateinit var personRepository: PersonRepository
    private lateinit var  binding: TestBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = TestBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initDatabase()
        addTestData()
        showDataOnScreen()
        enableDeleteAll()
        enableDeleteById()
        enableEditById()
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
            val container = binding.data
            showInContainer(container, persons)

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

    private fun enableDeleteAll(){
        binding.deleteall.setOnClickListener {
            binding.data.visibility = View.GONE
            binding.buttons.visibility = View.GONE
            binding.confirm.visibility = View.VISIBLE
            binding.yesbtn.setOnClickListener {
                val deleted = personRepository.deletePerson()
                showDataOnScreen()
                binding.data.visibility = View.VISIBLE
                binding.buttons.visibility = View.VISIBLE
                binding.confirm.visibility = View.GONE
            }
            binding.cancelbtn.setOnClickListener {
                showDataOnScreen()
                binding.buttons.visibility = View.VISIBLE
                binding.data.visibility = View.VISIBLE
            }
        }
    }
    private fun enableDeleteById(){
        binding.deletebyid.setOnClickListener {
            binding.data.visibility = View.GONE
            binding.buttons.visibility = View.GONE
            binding.deleteconfirmid.visibility = View.VISIBLE
            binding.deletebtn.setOnClickListener {
                val idText = binding.deletetext.text.toString()
                if (idText.isBlank()){
                    Toast.makeText(this, "Введите id", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
                val deleted = personRepository.deletePersonByID(idText.toLong())
                if (deleted > 0) {
                    Toast.makeText(this, "Запись удалена", Toast.LENGTH_SHORT).show()
                } else{
                    Toast.makeText(this, "Запись с таким id не найдена", Toast.LENGTH_SHORT).show()
                }
                showDataOnScreen()
                binding.deleteconfirmid.visibility = View.GONE
                binding.data.visibility= View.VISIBLE
                binding.buttons.visibility = View.VISIBLE
            }
            binding.nobtn.setOnClickListener {
                showDataOnScreen()
                binding.buttons.visibility = View.VISIBLE
                binding.data.visibility = View.VISIBLE
                binding.deleteconfirmid.visibility = View.GONE
            }
        }
    }
    private fun enableEditById() {
        var currentEditId: Long? = null

        binding.editbyid.setOnClickListener {
            binding.data.visibility = View.GONE
            binding.buttons.visibility = View.GONE
            binding.edit.visibility = View.VISIBLE
            binding.editor.visibility = View.GONE
            binding.backbtn.setOnClickListener {
                binding.data.visibility = View.VISIBLE
                binding.buttons.visibility = View.VISIBLE
                binding.edit.visibility = View.GONE
                binding.edittext.text.clear()
            }
            currentEditId = null
        }
        binding.editbtn.setOnClickListener {
            val idText = binding.edittext.text.toString()
            if (idText.isBlank()) {
                Toast.makeText(this, "Введите id", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val person = personRepository.getPersonById(idText.toLong())
            if (person == null) {
                Toast.makeText(this, "Пользователь с таким id не найден", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            } else {
                currentEditId = person.id

                binding.edit.visibility = View.GONE
                binding.editor.visibility = View.VISIBLE
                binding.nametext.setText(person.name)
                binding.agetext.setText(person.age.toString())
            }
        }

        binding.applybtn?.setOnClickListener {
            val id = currentEditId
            if (id == null) {
                Toast.makeText(this, "Ошибка: ID не найден", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val nameText = binding.nametext.text.toString()
            val ageText = binding.agetext.text.toString()

            if (nameText.isBlank()) {
                Toast.makeText(this, "Введите имя", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (ageText.isBlank()) {
                Toast.makeText(this, "Введите возраст", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            try {
                val updated = personRepository.updatePersonByID(
                    id,
                    nameText,
                    ageText.toInt()
                )

                if (updated > 0) {
                    Toast.makeText(this, "Запись отредактирована", Toast.LENGTH_SHORT).show()

                    showDataOnScreen()
                    binding.editor.visibility = View.GONE
                    binding.data.visibility = View.VISIBLE
                    binding.buttons.visibility = View.VISIBLE
                    binding.edittext.text.clear()
                    binding.nametext.text.clear()
                    binding.agetext.text.clear()
                    currentEditId = null
                } else {
                    Toast.makeText(this, "Ошибка при обновлении записи", Toast.LENGTH_SHORT).show()
                }
            } catch (e: NumberFormatException) {
                Toast.makeText(this, "Возраст должен быть числом", Toast.LENGTH_SHORT).show()
            }
        }
        binding.otmenabtn.setOnClickListener {
            showDataOnScreen()
            binding.buttons.visibility = View.VISIBLE
            binding.data.visibility = View.VISIBLE
            binding.editor.visibility = View.GONE
            binding.edit.visibility = View.GONE

            binding.edittext.text.clear()
            binding.nametext.text.clear()
            binding.agetext.text.clear()
            currentEditId = null
        }
    }

    override fun onDestroy() {
        dbHelper.close()
        super.onDestroy()
    }
}