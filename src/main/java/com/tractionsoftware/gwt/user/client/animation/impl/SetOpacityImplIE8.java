/*
 * Copyright 2010 Traction Software, Inc.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.tractionsoftware.gwt.user.client.animation.impl;

import com.google.gwt.dom.client.Element;

/**
 * IE doesn't support standard CSS opacity and instead requires
 * -ms-filter or filter to be specified. Oh, and it wants a value
 * between 0-100.
 */
public class SetOpacityImplIE8 implements SetOpacityImpl {

    @Override
    public void setOpacity(Element e, float opacity) {
        e.getStyle().setProperty("MsFilter", "\"progid:DXImageTransform.Microsoft.Alpha(Opacity="+(opacity * 100)+")\"");
	e.getStyle().setProperty("filter", "alpha(opacity="+((int)(opacity * 100))+")");	
    }

}