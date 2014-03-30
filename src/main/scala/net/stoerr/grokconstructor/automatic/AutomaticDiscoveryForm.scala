package net.stoerr.grokconstructor.automatic

import net.stoerr.grokconstructor.webframework.WebForm
import net.stoerr.grokconstructor.forms.{MultlineFormPart, GrokPatternFormPart, LoglinesFormPart}
import javax.servlet.http.HttpServletRequest

/**
 * Form for the automatic discovery algorithm
 * @author <a href="http://www.stoerr.net/">Hans-Peter Stoerr</a>
 * @since 08.03.13
 */
case class AutomaticDiscoveryForm(request: HttpServletRequest) extends WebForm with LoglinesFormPart with MultlineFormPart with GrokPatternFormPart
