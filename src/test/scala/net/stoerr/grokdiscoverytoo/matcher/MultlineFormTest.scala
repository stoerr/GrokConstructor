package net.stoerr.grokdiscoverytoo.matcher

import org.scalatest.junit.JUnitRunner
import org.junit.runner.RunWith
import org.scalatest.matchers.ShouldMatchers
import org.scalatest.FlatSpec
import javax.servlet.http.HttpServletRequest
import org.scalatest.mock.MockitoSugar
import org.mockito.Mockito._

/**
 * Tests the functionality of MultlineForm
 * @author <a href="http://www.stoerr.net/">Hans-Peter Stoerr</a>
 * @since 26.02.13
 */
@RunWith(classOf[JUnitRunner])
class MultlineFormTest extends FlatSpec with ShouldMatchers with MockitoSugar {

  private class MockForm(val request: HttpServletRequest) extends MultlineForm

  "MultlineForm.multlineFilter" should "correctly parse multlines" in {
    val mockreq = mock[HttpServletRequest]
    when(mockreq.getParameter("multline")).thenReturn("^-")
    val form = new MockForm(mockreq)
    form.multlineFilter(List()) should equal(List())
    form.multlineFilter(List("a")) should equal(List("a"))
    form.multlineFilter(List("-a")) should equal(List("-a"))
    form.multlineFilter(List("a", "b")) should equal(List("a", "b"))
    form.multlineFilter(List("-a", "b")) should equal(List("-a", "b"))
    form.multlineFilter(List("a", "-a")) should equal(List("a\n-a"))
    form.multlineFilter(List("a", "-a", "b")) should equal(List("a\n-a", "b"))
    form.multlineFilter(List("-a", "b", "-b", "-bb")) should equal(List("-a", "b\n-b\n-bb"))
  }

  it should "observe negate" in {
    val mockreq = mock[HttpServletRequest]
    when(mockreq.getParameter("multline")).thenReturn("^-")
    when(mockreq.getParameterValues("multlinenegate")).thenReturn(Array("negate"))
    val form = new MockForm(mockreq)
    form.multlineFilter(List("-a", "b", "-b", "-bb")) should equal(List("-a\nb", "-b", "-bb"))
  }

}
