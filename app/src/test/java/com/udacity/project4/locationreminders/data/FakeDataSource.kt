package com.udacity.project4.locationreminders.data

import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.data.dto.Result
import java.sql.ResultSet

//Use FakeDataSource that acts as a test double to the LocalDataSource
class FakeDataSource(private var reminders: MutableList<ReminderDTO> = mutableListOf()) :
    ReminderDataSource {

    private var shouldReturnError = false

    //    done: Create a fake data source to act as a double to the real data source
    fun setReturnError(value: Boolean) {
        shouldReturnError = value
    }

    override suspend fun getReminders(): Result<List<ReminderDTO>> {
//        done("Return the reminders")
        try {
            if (shouldReturnError) {
                return Result.Error("Error retrieving reminders")
            }
            return Result.Success(reminders)
        } catch (exception: Exception) {
            return Result.Error(exception.localizedMessage)
        }
    }

    override suspend fun saveReminder(reminder: ReminderDTO) {
//        done("save the reminder")
        reminders.add(reminder)
    }

    override suspend fun getReminder(id: String): Result<ReminderDTO> {
//        done("return the reminder with the id")
        try {
            if (shouldReturnError) {
                return Result.Error("Error retrieving reminder with Id $id")
            }
            val foundReminderDTO = reminders.find { it.id == id }

            return if (foundReminderDTO != null) {
                Result.Success(foundReminderDTO)
            } else {
                Result.Error("Reminder with Id $id not found")
            }
        } catch (exception: Exception) {
            return Result.Error(exception.localizedMessage)
        }
    }

    override suspend fun deleteAllReminders() {
//        done("delete all the reminders")
        reminders.clear()
    }


}