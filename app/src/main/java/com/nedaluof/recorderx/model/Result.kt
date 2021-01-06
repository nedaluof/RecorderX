package com.nedaluof.recorderx.model

/**
 * Created by nedaluof on 12/20/2020.
 */

data class Result<out T>(val status: Status, val data: T?, val message: String?) {
		companion object {
				fun <T> success(data: T): Result<T> =
						Result(status = Status.SUCCESS, data = data, message = null)

				fun <T> error(data: T?, message: String): Result<T> =
						Result(status = Status.ERROR, data = data, message = message)
		}
}

enum class Status { SUCCESS, ERROR; }