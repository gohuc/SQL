package com.example.sql

import android.content.ContentValues

class PersonRepository(private val dbHelper: SQLHelper) {

    fun addTestData() {
        val db = dbHelper.writableDatabase

        val testPersons = arrayOf(
            arrayOf("Иван Коробцов", "19"),
            arrayOf("Илья Акимов", "18"),
            arrayOf("Анастасия Сереченко", "18"),
            arrayOf("Иван Быков", "5"),
            arrayOf("Гай Юлий Цезрь", "2124")
        )

        testPersons.forEach { personData ->
            val values = ContentValues().apply {
                put("name", personData[0])
                put("age", personData[1])
            }
            db.insert("Person", null, values)
        }
    }


    fun getAllPersons(): List<Person> {
        val persons = mutableListOf<Person>()
        val db = dbHelper.readableDatabase
        val query = "SELECT * FROM Person"
        val cursor = db.rawQuery(query, null)

        cursor.use {
            while (it.moveToNext()) {
                val id = it.getLong(it.getColumnIndexOrThrow("_id"))
                val name = it.getString(it.getColumnIndexOrThrow("name"))
                val age = it.getInt(it.getColumnIndexOrThrow("age"))

                persons.add(Person(id, name, age))
            }
        }
        return persons
    }

    fun deletePerson(): Int {
        val db = dbHelper.writableDatabase
        return db.delete("Person", null, null)
    }

    fun deletePersonByID(id: Long): Int {
        val db = dbHelper.writableDatabase
        return db.delete("Person", "_id = ?", arrayOf(id.toString()))
    }

    fun updatePersonByID(id: Long, name: String, age: Int): Int {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put("name", name)
            put("age", age)
        }
        return db.update("Person", values, "_id = ?", arrayOf(id.toString()))
    }

    fun getPersonById(id: Long): Person? {
        val db = dbHelper.writableDatabase
        val cursor = db.rawQuery(
            "SELECT * FROM Person WHERE _id = ?",
            arrayOf(id.toString())
        )
        cursor.use {
            if (it.moveToFirst()) {
                return Person(
                    id = it.getLong(it.getColumnIndexOrThrow("_id")),
                    name = it.getString(it.getColumnIndexOrThrow("name")),
                    age = it.getInt(it.getColumnIndexOrThrow("age"))
                )
            }
        }
        return null
    }
}