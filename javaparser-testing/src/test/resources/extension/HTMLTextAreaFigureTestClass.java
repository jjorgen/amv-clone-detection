/*
 *  @(#)TextAreaFigure.java
 *
 *  Project:		JHotdraw - a GUI framework for technical drawings
 *  http://www.jhotdraw.org
 *  http://jhotdraw.sourceforge.net
 *  Copyright:	 by the original author(s) and all contributors
 *  License:		Lesser GNU Public License (LGPL)
 *  http://www.opensource.org/licenses/lgpl-license.html
 */
package CH.ifa.draw.contrib.html;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.FlatteningPathIterator;
import java.awt.geom.PathIterator;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Iterator;
import java.util.Map;
import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

import CH.ifa.draw.contrib.TextAreaFigure;
import CH.ifa.draw.figures.RectangleFigure;
import CH.ifa.draw.framework.*;
import CH.ifa.draw.standard.*;
import CH.ifa.draw.util.ColorMap;
import CH.ifa.draw.util.Geom;
import CH.ifa.draw.util.Storable;
import CH.ifa.draw.util.StorableInput;
import CH.ifa.draw.util.StorableOutput;

/**
 * An HTMLTextAreaFigure contains HTML formatted text.<br>
 * Formatting is done internally by a JLabel component, so all display features
 * and constrains that apply for a JLabel apply also for an HTMLTextAreaFigure,
 * including text and images, as in any Web browser, even allowing for contents
 * residing on external Web sources. But don't forget that this is <b>NOT</b> a Web
 * browser, so HTML contents cannot be standard Web pages with headers, stylesheets,
 * javascript and who knows what else, just plain down to earth HTML code.
 * <p>
 * In order to automatically integrate "external" attributes like FillColor,
 * FontName, etc, HTMLTextAreaFigure encapsulates the text the user types in the
 * editor within a table with a single cell occupying the whole area.<br>
 * Here is what the HTML code passed to the JLabel looks like:<br>
 * <code>
 * &lt;html&gt;<br>
 * &lt;table border='0' width='area.width' height='area.height'
 * cellpadding='0' cellspacing='0' bgcolor='&FillColor;'&gt;<br>
 * &lt;tr&gt;<br>
 * &lt;td width='100%'&gt;<br>
 * &lt;font face='&FontName;' color='&TextColor;' size='&FontSize;'&gt;<br>
 * &lt;!-- add italic and bold attributes if required--&gt;<br>
 * &lt;i&gt;<br>
 * &lt;b&gt;<br>
 * ============&gt> User's contents go here &lt;============<br>
 * &lt;!-- close italic and bold attributes if required --&gt;<br>
 * &lt;/b&gt;<br>
 * &lt;/i&gt;<br>
 * &lt;/font&gt;<br>
 * &lt;/td&gt;<br>
 * &lt;/tr&gt;<br>
 * &lt;/table&gt;<br>
 * &lt;/html&gt;<br>
 * </code><br>
 * It is possible to write <i>raw</i> HTML code by calling
 * <code>setRawHTML(true)</code>. In that case no tags are issued.<br>
 * The user is then responsible for applying the figure attributes and in
 * general is responsible for the whole display.
 * This setting can be dynamically toggled as needed.<br>
 * Note that JLabel resets the font to its own default whenever it encounters
 * an HTML structure, like a table or a header tag. I couldn't find a workaround
 * for what can/should be called a bug. Normal browsers do not behave like this.<p>
 *
 * Internal attributes like FillColor or FontName are exposed as special SGML
 * entities using the standard SGML entity notation, ex: <code>&FillColor;</code>.<br>
 * Any attribute associated to the figure can be used and will be replaced with
 * an appropriate value from a ContentsProducer (see below) or its
 * toString() value if no specific ContentProducer is defined.<p>
 *
 * The HTML display and layouting can be time consuming, quite fast in most cases,
 * unless the HTML structure is complicated. This can become a serious penalty
 * when working with a large number of complicated figures.<br>
 * To help in this issue HTMLTextAreaFigure offers two display modes, DirectDraw,
 * where the HTML layout logic is executed every time the figure is displayed, and
 * BufferedDraw, where HTMLTextAreaFigure creates an in-memory image of the
 * resulting layout and uses the image for fast display until a change requires
 * to regenerate the image.<br>
 * The BufferedDraw mode is as fast as an image display can be, but it consumes
 * more memory than the DirectDraw mode, which in turn is slower.<br>
 * The setting is specific to each figure instance and it can be dynamically
 * toggled at any time, so it is possible to fine tune when and which figures
 * use either one of the drawing modes.<p>
 *
 * Remember the attributes based SGML entities?<br>
 * If you set the figure to be read only, so not allowing the user to directly
 * edit the HTML contens, then it is possible to use HTMLTextAreaFigures to
 * produce very elaborate and complex information layout.<br>
 * You create HTML templates for each figure layout you want to use and set them
 * as the text of the figure. Within the template text you place field names
 * wherever needed as you would for a Web page, then each figure using the template
 * associates the field values as attributes of the figure. The attribute exposure
 * feature will substitute the entity names with the current attribute's value.<br>
 * Please refer to the accompanying sample program to see in detail the multiple
 * ways this feature can enhance your drawings.<p>
 *
 * <b>ContentProducers</b><br>
 * As stated above, entities referenced in the HTML template code are replaced by
 * their current value in the drawn figure. The values themselves are provided
 * by ContentProducers.<br>
 * For a detailed description of ContentProducers please refer to their
 * own documentation, but to make it simple, a ContentProducer is an object that
 * implements the method <code>getContent</code> and is registered to produce
 * content for either specific entities, or entity classes.<br>
 * An entity class is the class of the attribute containing its value, ie: an
 * attribute containing a URL has class URL (<code>attribute.getClass()</code>),
 * and an URLContentProducer can be associated to it so that when the layout
 * needs the entity's value, the producer's getContent() method is called and the
 * returned value (ex: contents from a Web page, FTP file or disk file) is used
 * to replace the entity in the displayed figure.<br>
 * The ContentProducer can return either a String, in which case it is used
 * <b>as is</b>, or another Object. In the later case HTMLTextAreaFigure will
 * continue calling registered ContentProviders depending on the class of the
 * returned Object until it either gets a final String, or null. If null then
 * the entity is considered as unknown and left as is in the displayed text. To
 * make it dissapear alltogether the producer should return an empty String.<p>
 * HTMLTextAreaFigure registers default ContentProducers:
 * AttributeFigureContentProducer for the intrinsic attributes of the figure
 * (height, width, font name, etc.), URLContentProducer for URL attributes,
 * HTMLColorContentProducer for HTML color encoding and for embedded
 * TextAreaFigure and HTMLTextAreaFigure classes. That's right, you can embed
 * a TextAreaFigure or HTMLTextAreaFigure contents inside an HTMLTextAreaFigure
 * recursively for as many levels as your CPU and memory will allow.<br>
 * For instance, the main figure can consists of an HTML table where each
 * cell's contents come from a different HTMLTextAreaFigure.
 *
 * @author    Eduardo Francos - InContext
 * @created   7 May 2002
 * @version   <$CURRENT_VERSION$>
 */

public class HTMLTextAreaFigureTestClass extends TextAreaFigure
		 implements HTMLContentProducerContext, FigureChangeListener {

	/** Start marker for embedded attribute values */
	public final static char START_ENTITY_CHAR = '&';

	/** End marker for embedded attribute values */
	public final static char END_ENTITY_CHAR = ';';

	/** Marker escape character */
	public final static char ESCAPE_CHAR = '\\';

	/** holder for the image used for the display */
	protected transient DisposableResourceHolder fImageHolder;

	/** The label used for in-memory display */
	protected transient JLabel fDisplayDelegate;

	/** True if using direct drawing, false if using the memory image */
	protected boolean fUseDirectDraw = false;

	/** True if the memory image should be regenerated */
	protected boolean fIsImageDirty = true;

	/** Description of the Field */
	protected boolean fRawHTML = false;

	/** Supplier for intrinsic data */
	protected transient ContentProducer fIntrinsicContentProducer;

	/** Description of the Field */
	protected static ContentProducerRegistry fDefaultContentProducers = new ContentProducerRegistry();
	// initialize the default content producers for HTMLTextAreaFigure figures
	static {
		fDefaultContentProducers.registerContentProducer(TextAreaFigure.class, new TextHolderContentProducer());
		fDefaultContentProducers.registerContentProducer(Color.class, new HTMLColorContentProducer());
	}

	/**
	 * Returns a new String with the entity keywords replaced by their
	 * current attribute value.<br>
	 * The text is scanned for entity keywords delimited by the START_ENTITY_CHAR
	 * and END_ENTITY_CHAR characters as in<br>
	 * <code>&gt;font face='&amp;FontName;' color='&amp;FillColor;'&lt;</code><br>
	 * A keyword is replaced if and only if an attribute with the given name is
	 * found, otherwise the text is left as is.
	 *
	 * @param template  The template text
	 * @return          The resulting string with its attributes replaced
	 */
	protected String substituteEntityKeywords(String template) {
		int startPos;
		int chunkEnd;
		int endPos;
		StringBuffer finalText = new StringBuffer();

		startPos = 0;
		chunkEnd = startPos;
		try {
			while ((startPos = template.indexOf(START_ENTITY_CHAR, startPos)) != -1) {
				if (startPos != 0 && template.charAt(startPos - 1) == ESCAPE_CHAR) {
					// found an escaped parameter starter
					startPos++;
					continue;
				}

				// get the end of the parameter
				endPos = startPos + 1;
				while ((endPos = template.indexOf(END_ENTITY_CHAR, endPos)) != -1) {
					if (endPos == 0 || template.charAt(endPos - 1) != ESCAPE_CHAR) {
						// found a valid non escaped group stopper
						break;
					}
					// invalid entity, error? probably not, anyway we consider
					// this as not being an attribute replacement
					throw new InvalidAttributeMarker();
				}

				// OK, we now have an attribute
				String attrName = template.substring(startPos + 1, endPos);

				// replace it if present, otherwise leave as is
				String attrValue = getEntityHTMLRepresentation(attrName);
				if (attrValue != null) {
					finalText.append(template.substring(chunkEnd, startPos));
					// append the entity's value after performing
					// entity keyword substitution on its contents
					finalText.append(substituteEntityKeywords(attrValue));
					startPos = endPos + 1;
					chunkEnd = startPos;
				}
				else {
					startPos++;
				}
			}
		}
		catch (InvalidAttributeMarker ex) {
			// invalid marker, ignore
		}

		// append whatever is left
		finalText.append(template.substring(chunkEnd));

		// and return it
		return finalText.toString();
	}
}
