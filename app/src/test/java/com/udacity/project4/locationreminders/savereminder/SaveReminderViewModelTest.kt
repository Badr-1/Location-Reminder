package com.udacity.project4.locationreminders.savereminder

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.udacity.project4.locationreminders.data.FakeDataSource
import com.udacity.project4.locationreminders.reminderslist.ReminderDataItem
import com.google.common.truth.Truth.assertThat
import com.udacity.project4.utils.MainCoroutineRule
import com.udacity.project4.utils.getOrAwaitValue
import com.udacity.project4.R

import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.context.stopKoin

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
class SaveReminderViewModelTest {


    //done: provide testing to the SaveReminderView and its live data objects
    private lateinit var viewModel: SaveReminderViewModel

    private lateinit var remindersRepository: FakeDataSource

    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @Before
    fun setupViewModel() {
        remindersRepository = FakeDataSource()
        viewModel =
            SaveReminderViewModel(ApplicationProvider.getApplicationContext(), remindersRepository)
    }

    @After
    fun tearDown() {
        stopKoin()
    }


    // validateEnteredData
    @Test
    fun validateEnteredData_reminderTitleIsEmpty_returnFalse() {
        val reminder = ReminderDataItem(
            "",
            "description",
            "location",
            0.0,
            0.0
        )
        val result = viewModel.validateEnteredData(reminder)
        assertThat(result).isFalse()
        assertThat(viewModel.showSnackBarInt.getOrAwaitValue()).isEqualTo(R.string.err_enter_title)
    }

    @Test
    fun validateEnteredData_reminderLocationIsEmpty_returnFalse() {
        val reminder = ReminderDataItem(
            "title",
            "description",
            "",
            0.0,
            0.0
        )
        val result = viewModel.validateEnteredData(reminder)
        assertThat(result).isFalse()
        assertThat(viewModel.showSnackBarInt.getOrAwaitValue()).isEqualTo(R.string.err_select_location)
    }

    @Test
    fun validateEnteredData_reminderDataIsValid_returnTrue() {
        val reminder = ReminderDataItem(
            "title",
            "description",
            "location",
            0.0,
            0.0
        )
        val result = viewModel.validateEnteredData(reminder)
        assertThat(result).isTrue()
    }

    // save Reminder
    @Test
    fun saveReminder_ToastMessage_ReminderSaved() {
        val reminder = ReminderDataItem(
            "title",
            "description",
            "location",
            0.0,
            0.0
        )

        mainCoroutineRule.dispatcher.pauseDispatcher()

        viewModel.saveReminder(reminder)

        assertThat(viewModel.showLoading.getOrAwaitValue()).isTrue()

        mainCoroutineRule.dispatcher.resumeDispatcher()

        assertThat(viewModel.showToast.getOrAwaitValue()).isEqualTo("Reminder Saved !")
        assertThat(viewModel.showLoading.getOrAwaitValue()).isFalse()
        assertThat(viewModel.navigationCommand.getOrAwaitValue()).isNotNull()
    }

}