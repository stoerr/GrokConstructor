package net.stoerr.grokdiscoverytoo.webframe

import scala.xml.{Text, NodeSeq}

/**
 * Some helper functions to create tables
 * @author <a href="http://www.stoerr.net/">Hans-Peter Stoerr</a>
 * @since 28.02.13
 */
trait TableMaker {

  implicit def stringToNode(str: String): NodeSeq = new Text(str)

  def warn(content: NodeSeq) = <span style="color:red">{content}</span>

  def row(content: NodeSeq) = <tr>
    <td>
      {content}
    </td>
  </tr>

  def rowheader(content: NodeSeq) = <tr>
    <th>
      {content}
    </th>
  </tr>

  def row2(content1: NodeSeq, content2: NodeSeq) = <tr>
    <td>
      {content1}
    </td> <td>
      {content2}
    </td>
  </tr>

  def row2(content: NodeSeq) = <tr>
    <td colspan="2">
      {content}
    </td>
  </tr>

  def rowheader2(content1: NodeSeq, content2: NodeSeq) = <tr>
    <th>
      {content1}
    </th> <th>
      {content2}
    </th>
  </tr>

  def rowheader2(content: NodeSeq) = <tr>
    <th colspan="2">
      {content}
    </th>
  </tr>

}
