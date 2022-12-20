package com.udacity.project4.locationreminders.reminderslist

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.udacity.project4.locationreminders.data.FakeDataSource
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.utils.MainCoroutineRule
import com.udacity.project4.utils.getOrAwaitValue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runBlockingTest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.context.stopKoin

@RunWith(AndroidJUnit4::class)
@ExperimentalCoroutinesApi
class RemindersListViewModelTest {

    //done: provide testing to the RemindersListViewModel and its live data objects
    private lateinit var viewModel: RemindersListViewModel

    private lateinit var remindersRepository: FakeDataSource


    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @Before
    fun setupViewModel() {
        remindersRepository = FakeDataSource()
        viewModel =
            RemindersListViewModel(ApplicationProvider.getApplicationContext(), remindersRepository)
    }

    @After
    fun cleanUp() {
        stopKoin()
    }


    @Test
    fun loadReminders_showLoadingStatus() {
        mainCoroutineRule.pauseDispatcher()
        viewModel.loadReminders()
        assert(viewModel.showLoading.getOrAwaitValue())
        mainCoroutineRule.resumeDispatcher()
        assert(!viewModel.showLoading.getOrAwaitValue())
    }

    @Test
    fun loadReminder_AddingAReminder_isNotEmpty() = runBlockingTest {
        val reminder = ReminderDTO("title", "description", "location", 0.0, 0.0)
        remindersRepository.saveReminder(reminder)
        viewModel.loadReminders()
        assert(viewModel.remindersList.getOrAwaitValue().isNotEmpty())
    }

    @Test
    fun loadReminder_NoReminders_isEmpty() = runBlockingTest {
        viewModel.loadReminders()
        assert(viewModel.remindersList.getOrAwaitValue().isEmpty())
    }

    @Test
    fun loadReminder_ErrorLoadingReminders_showError() {
        mainCoroutineRule.pauseDispatcher()
        remindersRepository.setReturnError(true)
        viewModel.loadReminders()
        mainCoroutineRule.resumeDispatcher()
        assert(viewModel.showSnackBar.getOrAwaitValue() == "Error retrieving reminders")
    }
}