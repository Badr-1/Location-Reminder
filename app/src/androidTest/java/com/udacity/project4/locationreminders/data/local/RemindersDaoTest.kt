package com.udacity.project4.locationreminders.data.local

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.SmallTest;
import com.udacity.project4.locationreminders.data.dto.ReminderDTO

import org.junit.Before;
import org.junit.Rule;
import org.junit.runner.RunWith;

import kotlinx.coroutines.ExperimentalCoroutinesApi;
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers
import org.hamcrest.MatcherAssert
import org.hamcrest.Matchers.nullValue
import org.junit.After
import org.junit.Test

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
//Unit test the DAO
@SmallTest
class RemindersDaoTest {

    //    done: Add testing implementation to the RemindersDao.kt
    private lateinit var database: RemindersDatabase

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @Before
    fun initializeDatabase() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            RemindersDatabase::class.java
        ).build()
    }

    @After
    fun closeDb() = database.close()


    // getReminders
    @Test
    fun getReminders_NoReminders_ReturnsEmptyList() = runBlockingTest {
        val reminders = database.reminderDao().getReminders()
        MatcherAssert.assertThat(reminders, CoreMatchers.`is`(emptyList()))
    }

    @Test
    fun getReminders_OneReminderAdded_ReturnsNotEmpty() = runBlockingTest {
        val reminder = ReminderDTO("title", "description", "location", 0.0, 0.0)
        database.reminderDao().saveReminder(reminder)
        val reminders = database.reminderDao().getReminders()
        MatcherAssert.assertThat(reminders, CoreMatchers.`is`(CoreMatchers.not(emptyList())))
        MatcherAssert.assertThat(reminders[0].title, CoreMatchers.`is`(reminder.title))
        MatcherAssert.assertThat(reminders[0].description, CoreMatchers.`is`(reminder.description))
        MatcherAssert.assertThat(reminders[0].location, CoreMatchers.`is`(reminder.location))
        MatcherAssert.assertThat(reminders[0].latitude, CoreMatchers.`is`(reminder.latitude))
        MatcherAssert.assertThat(reminders[0].longitude, CoreMatchers.`is`(reminder.longitude))
        MatcherAssert.assertThat(reminders.size, CoreMatchers.`is`(1))
    }

    // getReminderById and saveReminder
    @Test
    fun getReminderById_NoReminders_ReturnsNull() = runBlockingTest {
        val reminder = database.reminderDao().getReminderById("1")
        MatcherAssert.assertThat(reminder, CoreMatchers.`is`(nullValue()))
    }

    @Test
    fun getReminderById_OneReminderAdded_ReturnsReminder() = runBlockingTest {
        val reminder = ReminderDTO("title", "description", "location", 0.0, 0.0)
        database.reminderDao().saveReminder(reminder)
        val reminderById = database.reminderDao().getReminderById(reminder.id)
        MatcherAssert.assertThat(reminderById, CoreMatchers.`is`(CoreMatchers.notNullValue()))
        MatcherAssert.assertThat(reminderById?.title, CoreMatchers.`is`(reminder.title))
        MatcherAssert.assertThat(reminderById?.description, CoreMatchers.`is`(reminder.description))
        MatcherAssert.assertThat(reminderById?.location, CoreMatchers.`is`(reminder.location))
        MatcherAssert.assertThat(reminderById?.latitude, CoreMatchers.`is`(reminder.latitude))
        MatcherAssert.assertThat(reminderById?.longitude, CoreMatchers.`is`(reminder.longitude))
    }

    // deleteAllReminders
    @Test
    fun deleteAllReminders_OneReminderAdded_ReturnsEmptyList() = runBlockingTest {
        val reminder = ReminderDTO("title", "description", "location", 0.0, 0.0)
        database.reminderDao().saveReminder(reminder)
        MatcherAssert.assertThat(database.reminderDao().getReminders().size, CoreMatchers.`is`(1))
        database.reminderDao().deleteAllReminders()
        val reminders = database.reminderDao().getReminders()
        MatcherAssert.assertThat(reminders, CoreMatchers.`is`(emptyList()))
    }

}