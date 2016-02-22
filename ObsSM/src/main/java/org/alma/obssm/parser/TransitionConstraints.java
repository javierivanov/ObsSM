/*******************************************************************************
 * ALMA - Atacama Large Millimeter Array
 * Copyright (c) AUI - Associated Universities Inc., 2016
 * (in the framework of the ALMA collaboration).
 * All rights reserved.
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307  USA
 *******************************************************************************/

package org.alma.obssm.parser;

import java.util.List;



/**
 * This class is used to describe a JSON element.
 * It has 3 Lists and 1 String
 * and_list, or_list, search_list.
 * and_list requires that all the elements on the list must be on the log.
 * or_list requires at least one element on the list must be on the log.
 * search_list is a list of regexpr that search for a pattern on the log.
 * keyname is the identifier of the SM
 * @author Javier Fuentes
 * @version 0.2
 *
 */
public class TransitionConstraints {
    public List<String> and_list;
    public List<String> or_list;
    public List<String> search_list;
    public String keyName;
    
    
	@Override
	public String toString() {
		return "TransitionConstraints [and_list=" + and_list + ", or_list=" + or_list + ", search_list=" + search_list
				+ ", keyName=" + keyName + "]";
	}


	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((and_list == null) ? 0 : and_list.hashCode());
		result = prime * result + ((keyName == null) ? 0 : keyName.hashCode());
		result = prime * result + ((or_list == null) ? 0 : or_list.hashCode());
		result = prime * result + ((search_list == null) ? 0 : search_list.hashCode());
		return result;
	}


	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		TransitionConstraints other = (TransitionConstraints) obj;
		if (and_list == null) {
			if (other.and_list != null)
				return false;
		} else if (!and_list.equals(other.and_list))
			return false;
		if (keyName == null) {
			if (other.keyName != null)
				return false;
		} else if (!keyName.equals(other.keyName))
			return false;
		if (or_list == null) {
			if (other.or_list != null)
				return false;
		} else if (!or_list.equals(other.or_list))
			return false;
		if (search_list == null) {
			if (other.search_list != null)
				return false;
		} else if (!search_list.equals(other.search_list))
			return false;
		return true;
	}
	
	
	
}
