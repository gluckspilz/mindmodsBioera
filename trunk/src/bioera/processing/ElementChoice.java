/* ElementChoice.java v 1.0.9   11/6/04 7:15 PM
 *
 * BioEra - visual designer for biofeedback (http://www.bioera.net)
 *
 * Copyright (c) 2003-2004 Jarek Foltynski (http://www.foltynski.info)
 *
 * This program is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the Free
 * Software Foundation; either version 2 of the License, or (at your option)
 * any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for
 * more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */

package bioera.processing;

public class ElementChoice extends ElementSet {
	public int chosenElementID = -1;  // Contains ID of the chosen element
	protected int chosenElementIndex = -1;
/**
 * ElementChoice constructor comment.
 */
public ElementChoice() {
	super();
}
/**
 * ElementChoice constructor comment.
 */
public ElementChoice(Element e[]) {
	super(e);
}
/**
 * ElementChoice constructor comment.
 */
public ElementChoice(Element e[], int defaultIndex) {
	super(e);
	setChosenElementIndex(defaultIndex);
}
/**
 * ElementChoice constructor comment.
 */
public Element getChosenElement() {
	if (chosenElementID == -1)
		return null;
	
	for (int i = 0; i < elements.length; i++){
		if (elements[i].getId() == chosenElementID) {
			chosenElementIndex = i;
			return elements[i];
		}
	}

	return null;
}
/**
 * ElementChoice constructor comment.
 */
public final void process() throws Exception {
	elements[chosenElementIndex].process();
}
/**
 * ElementChoice constructor comment.
 */
public void setChosenElementIndex(int i) {
	chosenElementIndex = i;
	chosenElementID = elements[i].getId();
}
}
