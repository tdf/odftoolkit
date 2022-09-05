
# /usr/bin/python
########################################################################
#
# DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER
#
# Use is subject to license terms.
#
# Licensed under the Apache License, Version 2.0 (the "License"); you may not
# use this file except in compliance with the License. You may obtain a copy
# of the License at http://www.apache.org/licenses/LICENSE-2.0. You can also
# obtain a copy of the License at http://odftoolkit.org/docs/license.txt
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
# WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
#
# See the License for the specific language governing permissions and
# limitations under the License.
#
########################################################################/
CHILD_ELEMENTS = {
	'*' : ( \
		'*', \
),
	'manifest:algorithm' : ( \
		'*', \
),
	'manifest:encryption-data' : ( \
		'manifest:algorithm', \
		'manifest:key-derivation', \
		'manifest:start-key-generation', \
),
	'manifest:file-entry' : ( \
		'manifest:encryption-data', \
),
	'manifest:key-derivation' : ( \
),
	'manifest:manifest' : ( \
		'manifest:file-entry', \
),
	'manifest:start-key-generation' : ( \
),
}

CHILD_ATTRIBUTES = {
	'*' : ( \
		'*', \
),
	'manifest:algorithm' : ( \
		'manifest:algorithm-name', \
		'manifest:initialisation-vector', \
),
	'manifest:encryption-data' : ( \
		'manifest:checksum', \
		'manifest:checksum-type', \
),
	'manifest:file-entry' : ( \
		'manifest:full-path', \
		'manifest:media-type', \
		'manifest:preferred-view-mode', \
		'manifest:size', \
		'manifest:version', \
),
	'manifest:key-derivation' : ( \
		'manifest:iteration-count', \
		'manifest:key-derivation-name', \
		'manifest:key-size', \
		'manifest:salt', \
),
	'manifest:manifest' : ( \
		'manifest:version', \
),
	'manifest:start-key-generation' : ( \
		'manifest:key-size', \
		'manifest:start-key-generation-name', \
),
}

