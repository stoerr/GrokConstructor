package net.stoerr.grokconstructor

import com.google.apphosting.api.DeadlineExceededException

/** Wraps a DeadlineExceededException for better error message. */
class TimeoutException(msg: String, e: Throwable) extends Exception(msg, e) {

  // empty

}
