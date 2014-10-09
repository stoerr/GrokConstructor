package net.stoerr.grokconstructor.matcher

import javax.servlet.http.HttpServletRequest

import net.stoerr.grokconstructor.forms.MultilineFormPart
import org.junit.runner.RunWith
import org.mockito.Mockito._
import org.scalatest.FlatSpec
import org.scalatest.junit.JUnitRunner
import org.scalatest.matchers.ShouldMatchers
import org.scalatest.mock.MockitoSugar

/**
 * Tests the functionality of MultilineFormPart
 * @author <a href="http://www.stoerr.net/">Hans-Peter Stoerr</a>
 * @since 26.02.13
 */
@RunWith(classOf[JUnitRunner])
class MultilineFormTest extends FlatSpec with ShouldMatchers with MockitoSugar {

  private class MockForm(val request: HttpServletRequest) extends MultilineFormPart

  "MultilineFormPart.multilineFilter" should "correctly parse multilines" in {
    val mockreq = mock[HttpServletRequest]
    when(mockreq.getParameter("multiline")).thenReturn("^-")
    when(mockreq.getParameterValues("multilinenegate")).thenReturn(Array[String]())
    val form = new MockForm(mockreq)

    form.multilineFilter(List("-a", "b", "-b", "-bb")) should equal(List("-a", "b\n-b\n-bb"))

    form.multilineFilter(List()) should equal(List())
    form.multilineFilter(List("a")) should equal(List("a"))
    form.multilineFilter(List("-a")) should equal(List("-a"))
    form.multilineFilter(List("a", "b")) should equal(List("a", "b"))
    form.multilineFilter(List("-a", "b")) should equal(List("-a", "b"))
    form.multilineFilter(List("a", "-a")) should equal(List("a\n-a"))
    form.multilineFilter(List("a", "-a", "b")) should equal(List("a\n-a", "b"))
    form.multilineFilter(List("-a", "b", "-b", "-bb")) should equal(List("-a", "b\n-b\n-bb"))
  }

  it should "observe negate" in {
    val mockreq = mock[HttpServletRequest]
    when(mockreq.getParameter("multiline")).thenReturn("^-")
    when(mockreq.getParameterValues("multilinenegate")).thenReturn(Array("negate"))
    val form = new MockForm(mockreq)
    form.multilineFilter(List("-a", "b", "-b", "-bb")) should equal(List("-a\nb", "-b", "-bb"))
  }

}
