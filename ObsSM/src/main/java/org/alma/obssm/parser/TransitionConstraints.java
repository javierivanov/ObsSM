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
 * 
 * @autor Javier Fuentes j.fuentes.m(at)icloud.com
 * @version 0.1
 * 
 *******************************************************************************/

package org.alma.obssm.parser;

import java.util.List;



/**
 * This class is used to describe a JSON element.
 * It has 3 Lists.
 * and_list, or_list, search_list.
 * and_list requires that all the elements on the list must be on the log.
 * or_list requires at least one element on the list must be on the log.
 * search_list is a list of regexpr that search for a pattern on the log.
 * @author Javier Fuentes
 * @version 0.1
 *
 */
public class TransitionConstraints {
    public List<String> and_list;
    public List<String> or_list;
    public List<String> search_list;
	@Override
	public String toString() {
		return "SubjectTransition [and_list=" + and_list + ", or_list=" + or_list + ", search_list=" + search_list + "]";
	}

    
    
}