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

/**      The "Document Layer" exist for usability reasons, it gives a feature based view on the document model.
		One feature consists of one or more ODF elements (e.g. table).</br>
		<b>NOTE:</b>Between ODFDOM 0.8.8 and 0.9.0 the "Document Layer" was marked as "Deprecated" in favor of the Simple API. Simple API is marked as deprecated with Version 0.9.0 of the ODF Toolkit.
		Instead of copying many classes from ODFDOM as Simple API did, new user functionality should rely on ODFDOM but should still avoid implementation details of the ODF XML.
*/
package org.odftoolkit.odfdom.doc;
