package com.udacity.project4.locationreminders.data.local

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.data.dto.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.instanceOf
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
//Medium Test to test the repository
@MediumTest
class RemindersLocalRepositoryTest {

    //    done: Add testing implementation to the RemindersLocalRepository.kt
    private lateinit var remindersLocalRepository: RemindersLocalRepository

    private lateinit var database: RemindersDatabase

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @Before
    fun init() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            RemindersDatabase::class.java
        ).allowMainThreadQueries().build()
        remindersLocalRepository = RemindersLocalRepository(
            database.reminderDao(),
            Dispatchers.Main
        )
    }

    @After
    fun cleanUp() {
        database.close()
    }

    @Test
    fun getReminders_NoReminders_ReturnsEmptyList() = runBlocking {
        val result = remindersLocalRepository.getReminders()
        assertThat(result, instanceOf(Result.Success::class.java))
        assertThat((result as Result.Success).data.isEmpty(), `is`(true))
    }

    @Test
    fun saveReminder_oneReminderSaved_ReturnsOneReminder() = runBlocking {
        val reminder = ReminderDTO("title", "description", "location", 0.0, 0.0)
        remindersLocalRepository.saveReminder(reminder)
        val result = remindersLocalRepository.getReminders()
        assertThat(result, instanceOf(Result.Success::class.java))
        assertThat((result as Result.Success).data.size, `is`(1))
        assertThat((result as Result.Success).data[0].title, `is`(reminder.title))
        assertThat((result as Result.Success).data[0].description, `is`(reminder.description))
        assertThat((result as Result.Success).data[0].location, `is`(reminder.location))
        assertThat((result as Result.Success).data[0].latitude, `is`(reminder.latitude))
        assertThat((result as Result.Success).data[0].longitude, `is`(reminder.longitude))
    }

    @Test
    fun getReminderWithId_OneReminderAdded_ReturnsOneReminder() = runBlocking {
        val reminder = ReminderDTO("title", "description", "location", 0.0, 0.0)
        remindersLocalRepository.saveReminder(reminder)
        val result = remindersLocalRepository.getReminder(reminder.id)
        assertThat(result, instanceOf(Result.Success::class.java))
        assertThat((result as Result.Success).data.title, `is`(reminder.title))
        assertThat(result.data.description, `is`(reminder.description))
        assertThat(result.data.location, `is`(reminder.location))
        assertThat(result.data.latitude, `is`(reminder.latitude))
        assertThat(result.data.longitude, `is`(reminder.longitude))
    }

    @Test
    fun getReminderWithId_NoReminderAdded_ReturnsError() = runBlocking {
        val result = remindersLocalRepository.getReminder("1")
        assertThat(result is Result.Error, `is`(true))
        assertThat((result as Result.Error).message, `is`("Reminder not found!"))
    }

    @Test
    fun deleteAllReminders_OneReminderAdded_ReturnsEmptyList() = runBlocking {
        val reminder = ReminderDTO("title", "description", "location", 0.0, 0.0)
        remindersLocalRepository.saveReminder(reminder)
        remindersLocalRepository.deleteAllReminders()
        val result = remindersLocalRepository.getReminders()
        assertThat(result, instanceOf(Result.Success::class.java))
        assertThat((result as Result.Success).data.isEmpty(), `is`(true))
    }

}