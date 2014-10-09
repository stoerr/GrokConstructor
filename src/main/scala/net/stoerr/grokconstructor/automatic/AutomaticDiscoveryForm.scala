package net.stoerr.grokconstructor.automatic

import javax.servlet.http.HttpServletRequest

import net.stoerr.grokconstructor.forms.{GrokPatternFormPart, LoglinesFormPart, MultilineFormPart}
import net.stoerr.grokconstructor.webframework.WebForm

/**
 * Form for the automatic discovery algorithm
 * @author <a href="http://www.stoerr.net/">Hans-Peter Stoerr</a>
 * @since 08.03.13
 */
case class AutomaticDiscoveryForm(request: HttpServletRequest) extends WebForm with LoglinesFormPart with MultilineFormPart with GrokPatternFormPart
