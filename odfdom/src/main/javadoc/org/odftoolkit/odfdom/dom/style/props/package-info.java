 /*
  DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER

  Copyright 2018-2019 The Document Foundation. All rights reserved.
  Copyright 2008, 2010 Oracle and/or its affiliates. All rights reserved.

  Use is subject to license terms.

  Licensed under the Apache License, Version 2.0 (the "License"); you may not
  use this file except in compliance with the License. You may obtain a copy
  of the License at http://www.apache.org/licenses/LICENSE-2.0. You can also
  obtain a copy of the License at http://odftoolkit.org/docs/license.txt

  Unless required by applicable law or agreed to in writing, software
  distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
  WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.

  See the License for the specific language governing permissions and
  limitations under the License.

 ************************************************************************/
    /**
        Classes representing the style properties.
        (DEPRECATED: functionality will be moved to element.style for version 0.8 see <a href="https://odftoolkit.org/bugzilla/show_bug.cgi?id=72">
        <b>Bug&nbsp;72</b></a>)

        Each style-family is associated with a set of properties, e.g. style:family="text"
        is associated with a style:text-properties child element. Some style families contain
        multiple style properties.The associations which properties go with which style
        families were generated from the RelaxNG schema. Then again these properties have
        various style property attributes.
        <p align="center">
        <img src="../../../../../resources/simple_odf_fam_prop.jpg"> */
package org.odftoolkit.odfdom.dom.style.props;
