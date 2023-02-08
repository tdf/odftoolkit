
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
	'*' : ( \
		'*', \
),
	'anim:animate' : ( \
),
	'anim:animateColor' : ( \
),
	'anim:animateMotion' : ( \
),
	'anim:animateTransform' : ( \
),
	'anim:audio' : ( \
),
	'anim:command' : ( \
		'anim:param', \
),
	'anim:iterate' : ( \
		'anim:animate', \
		'anim:animateColor', \
		'anim:animateMotion', \
		'anim:animateTransform', \
		'anim:audio', \
		'anim:command', \
		'anim:iterate', \
		'anim:par', \
		'anim:seq', \
		'anim:set', \
		'anim:transitionFilter', \
),
	'anim:par' : ( \
		'anim:animate', \
		'anim:animateColor', \
		'anim:animateMotion', \
		'anim:animateTransform', \
		'anim:audio', \
		'anim:command', \
		'anim:iterate', \
		'anim:par', \
		'anim:seq', \
		'anim:set', \
		'anim:transitionFilter', \
),
	'anim:param' : ( \
),
	'anim:seq' : ( \
		'anim:animate', \
		'anim:animateColor', \
		'anim:animateMotion', \
		'anim:animateTransform', \
		'anim:audio', \
		'anim:command', \
		'anim:iterate', \
		'anim:par', \
		'anim:seq', \
		'anim:set', \
		'anim:transitionFilter', \
),
	'anim:set' : ( \
),
	'anim:transitionFilter' : ( \
),
	'chart:axis' : ( \
		'chart:categories', \
		'chart:grid', \
		'chart:title', \
),
	'chart:categories' : ( \
),
	'chart:chart' : ( \
		'chart:footer', \
		'chart:legend', \
		'chart:plot-area', \
		'chart:subtitle', \
		'chart:title', \
		'table:table', \
),
	'chart:data-label' : ( \
		'text:p', \
),
	'chart:data-point' : ( \
		'chart:data-label', \
),
	'chart:domain' : ( \
),
	'chart:equation' : ( \
		'text:p', \
),
	'chart:error-indicator' : ( \
),
	'chart:floor' : ( \
),
	'chart:footer' : ( \
		'text:p', \
),
	'chart:grid' : ( \
),
	'chart:label-separator' : ( \
		'text:p', \
),
	'chart:legend' : ( \
		'text:p', \
),
	'chart:mean-value' : ( \
),
	'chart:plot-area' : ( \
		'chart:axis', \
		'chart:floor', \
		'chart:series', \
		'chart:stock-gain-marker', \
		'chart:stock-loss-marker', \
		'chart:stock-range-line', \
		'chart:wall', \
		'dr3d:light', \
),
	'chart:regression-curve' : ( \
		'chart:equation', \
),
	'chart:series' : ( \
		'chart:data-label', \
		'chart:data-point', \
		'chart:domain', \
		'chart:error-indicator', \
		'chart:mean-value', \
		'chart:regression-curve', \
),
	'chart:stock-gain-marker' : ( \
),
	'chart:stock-loss-marker' : ( \
),
	'chart:stock-range-line' : ( \
),
	'chart:subtitle' : ( \
		'text:p', \
),
	'chart:symbol-image' : ( \
),
	'chart:title' : ( \
		'text:p', \
),
	'chart:wall' : ( \
),
	'config:config-item' : ( \
),
	'config:config-item-map-entry' : ( \
		'config:config-item', \
		'config:config-item-map-indexed', \
		'config:config-item-map-named', \
		'config:config-item-set', \
),
	'config:config-item-map-indexed' : ( \
		'config:config-item-map-entry', \
),
	'config:config-item-map-named' : ( \
		'config:config-item-map-entry', \
),
	'config:config-item-set' : ( \
		'config:config-item', \
		'config:config-item-map-indexed', \
		'config:config-item-map-named', \
		'config:config-item-set', \
),
	'db:application-connection-settings' : ( \
		'db:data-source-settings', \
		'db:table-filter', \
		'db:table-type-filter', \
),
	'db:auto-increment' : ( \
),
	'db:character-set' : ( \
),
	'db:column' : ( \
),
	'db:column-definition' : ( \
),
	'db:column-definitions' : ( \
		'db:column-definition', \
),
	'db:columns' : ( \
		'db:column', \
),
	'db:component' : ( \
		'math:math', \
		'office:document', \
),
	'db:component-collection' : ( \
		'db:component', \
		'db:component-collection', \
),
	'db:connection-data' : ( \
		'db:connection-resource', \
		'db:database-description', \
		'db:login', \
),
	'db:connection-resource' : ( \
),
	'db:data-source' : ( \
		'db:application-connection-settings', \
		'db:connection-data', \
		'db:driver-settings', \
),
	'db:data-source-setting' : ( \
		'db:data-source-setting-value', \
),
	'db:data-source-setting-value' : ( \
),
	'db:data-source-settings' : ( \
		'db:data-source-setting', \
),
	'db:database-description' : ( \
		'db:file-based-database', \
		'db:server-database', \
),
	'db:delimiter' : ( \
),
	'db:driver-settings' : ( \
		'db:auto-increment', \
		'db:character-set', \
		'db:delimiter', \
		'db:table-settings', \
),
	'db:file-based-database' : ( \
),
	'db:filter-statement' : ( \
),
	'db:forms' : ( \
		'db:component', \
		'db:component-collection', \
),
	'db:index' : ( \
		'db:index-columns', \
),
	'db:index-column' : ( \
),
	'db:index-columns' : ( \
		'db:index-column', \
),
	'db:indices' : ( \
		'db:index', \
),
	'db:key' : ( \
		'db:key-columns', \
),
	'db:key-column' : ( \
),
	'db:key-columns' : ( \
		'db:key-column', \
),
	'db:keys' : ( \
		'db:key', \
),
	'db:login' : ( \
),
	'db:order-statement' : ( \
),
	'db:queries' : ( \
		'db:query', \
		'db:query-collection', \
),
	'db:query' : ( \
		'db:columns', \
		'db:filter-statement', \
		'db:order-statement', \
		'db:update-table', \
),
	'db:query-collection' : ( \
		'db:query', \
		'db:query-collection', \
),
	'db:reports' : ( \
		'db:component', \
		'db:component-collection', \
),
	'db:schema-definition' : ( \
		'db:table-definitions', \
),
	'db:server-database' : ( \
),
	'db:table-definition' : ( \
		'db:column-definitions', \
		'db:indices', \
		'db:keys', \
),
	'db:table-definitions' : ( \
		'db:table-definition', \
),
	'db:table-exclude-filter' : ( \
		'db:table-filter-pattern', \
),
	'db:table-filter' : ( \
		'db:table-exclude-filter', \
		'db:table-include-filter', \
),
	'db:table-filter-pattern' : ( \
),
	'db:table-include-filter' : ( \
		'db:table-filter-pattern', \
),
	'db:table-representation' : ( \
		'db:columns', \
		'db:filter-statement', \
		'db:order-statement', \
),
	'db:table-representations' : ( \
		'db:table-representation', \
),
	'db:table-setting' : ( \
		'db:character-set', \
		'db:delimiter', \
),
	'db:table-settings' : ( \
		'db:table-setting', \
),
	'db:table-type' : ( \
),
	'db:table-type-filter' : ( \
		'db:table-type', \
),
	'db:update-table' : ( \
),
	'dc:creator' : ( \
),
	'dc:date' : ( \
),
	'dc:description' : ( \
),
	'dc:language' : ( \
),
	'dc:subject' : ( \
),
	'dc:title' : ( \
),
	'dr3d:cube' : ( \
),
	'dr3d:extrude' : ( \
),
	'dr3d:light' : ( \
),
	'dr3d:rotate' : ( \
),
	'dr3d:scene' : ( \
		'dr3d:cube', \
		'dr3d:extrude', \
		'dr3d:light', \
		'dr3d:rotate', \
		'dr3d:scene', \
		'dr3d:sphere', \
		'draw:glue-point', \
		'svg:desc', \
		'svg:title', \
),
	'dr3d:sphere' : ( \
),
	'draw:a' : ( \
		'dr3d:scene', \
		'draw:caption', \
		'draw:circle', \
		'draw:connector', \
		'draw:control', \
		'draw:custom-shape', \
		'draw:ellipse', \
		'draw:frame', \
		'draw:g', \
		'draw:line', \
		'draw:measure', \
		'draw:page-thumbnail', \
		'draw:path', \
		'draw:polygon', \
		'draw:polyline', \
		'draw:rect', \
		'draw:regular-polygon', \
),
	'draw:applet' : ( \
		'draw:param', \
),
	'draw:area-circle' : ( \
		'office:event-listeners', \
		'svg:desc', \
		'svg:title', \
),
	'draw:area-polygon' : ( \
		'office:event-listeners', \
		'svg:desc', \
		'svg:title', \
),
	'draw:area-rectangle' : ( \
		'office:event-listeners', \
		'svg:desc', \
		'svg:title', \
),
	'draw:caption' : ( \
		'draw:glue-point', \
		'office:event-listeners', \
		'svg:desc', \
		'svg:title', \
		'text:list', \
		'text:p', \
),
	'draw:circle' : ( \
		'draw:glue-point', \
		'office:event-listeners', \
		'svg:desc', \
		'svg:title', \
		'text:list', \
		'text:p', \
),
	'draw:connector' : ( \
		'draw:glue-point', \
		'office:event-listeners', \
		'svg:desc', \
		'svg:title', \
		'text:list', \
		'text:p', \
),
	'draw:contour-path' : ( \
),
	'draw:contour-polygon' : ( \
),
	'draw:control' : ( \
		'draw:glue-point', \
		'svg:desc', \
		'svg:title', \
),
	'draw:custom-shape' : ( \
		'draw:enhanced-geometry', \
		'draw:glue-point', \
		'office:event-listeners', \
		'svg:desc', \
		'svg:title', \
		'text:list', \
		'text:p', \
),
	'draw:ellipse' : ( \
		'draw:glue-point', \
		'office:event-listeners', \
		'svg:desc', \
		'svg:title', \
		'text:list', \
		'text:p', \
),
	'draw:enhanced-geometry' : ( \
		'draw:equation', \
		'draw:handle', \
),
	'draw:equation' : ( \
),
	'draw:fill-image' : ( \
),
	'draw:floating-frame' : ( \
),
	'draw:frame' : ( \
		'draw:applet', \
		'draw:contour-path', \
		'draw:contour-polygon', \
		'draw:floating-frame', \
		'draw:glue-point', \
		'draw:image', \
		'draw:image-map', \
		'draw:object', \
		'draw:object-ole', \
		'draw:plugin', \
		'draw:text-box', \
		'office:event-listeners', \
		'svg:desc', \
		'svg:title', \
		'table:table', \
),
	'draw:g' : ( \
		'dr3d:scene', \
		'draw:a', \
		'draw:caption', \
		'draw:circle', \
		'draw:connector', \
		'draw:control', \
		'draw:custom-shape', \
		'draw:ellipse', \
		'draw:frame', \
		'draw:g', \
		'draw:glue-point', \
		'draw:line', \
		'draw:measure', \
		'draw:page-thumbnail', \
		'draw:path', \
		'draw:polygon', \
		'draw:polyline', \
		'draw:rect', \
		'draw:regular-polygon', \
		'office:event-listeners', \
		'svg:desc', \
		'svg:title', \
),
	'draw:glue-point' : ( \
),
	'draw:gradient' : ( \
),
	'draw:handle' : ( \
),
	'draw:hatch' : ( \
),
	'draw:image' : ( \
		'office:binary-data', \
		'text:list', \
		'text:p', \
),
	'draw:image-map' : ( \
		'draw:area-circle', \
		'draw:area-polygon', \
		'draw:area-rectangle', \
),
	'draw:layer' : ( \
		'svg:desc', \
		'svg:title', \
),
	'draw:layer-set' : ( \
		'draw:layer', \
),
	'draw:line' : ( \
		'draw:glue-point', \
		'office:event-listeners', \
		'svg:desc', \
		'svg:title', \
		'text:list', \
		'text:p', \
),
	'draw:marker' : ( \
),
	'draw:measure' : ( \
		'draw:glue-point', \
		'office:event-listeners', \
		'svg:desc', \
		'svg:title', \
		'text:list', \
		'text:p', \
),
	'draw:object' : ( \
		'math:math', \
		'office:document', \
),
	'draw:object-ole' : ( \
		'office:binary-data', \
),
	'draw:opacity' : ( \
),
	'draw:page' : ( \
		'anim:animate', \
		'anim:animateColor', \
		'anim:animateMotion', \
		'anim:animateTransform', \
		'anim:audio', \
		'anim:command', \
		'anim:iterate', \
		'anim:par', \
		'anim:seq', \
		'anim:set', \
		'anim:transitionFilter', \
		'dr3d:scene', \
		'draw:a', \
		'draw:caption', \
		'draw:circle', \
		'draw:connector', \
		'draw:control', \
		'draw:custom-shape', \
		'draw:ellipse', \
		'draw:frame', \
		'draw:g', \
		'draw:layer-set', \
		'draw:line', \
		'draw:measure', \
		'draw:page-thumbnail', \
		'draw:path', \
		'draw:polygon', \
		'draw:polyline', \
		'draw:rect', \
		'draw:regular-polygon', \
		'office:forms', \
		'presentation:animations', \
		'presentation:notes', \
		'svg:desc', \
		'svg:title', \
),
	'draw:page-thumbnail' : ( \
		'svg:desc', \
		'svg:title', \
),
	'draw:param' : ( \
),
	'draw:path' : ( \
		'draw:glue-point', \
		'office:event-listeners', \
		'svg:desc', \
		'svg:title', \
		'text:list', \
		'text:p', \
),
	'draw:plugin' : ( \
		'draw:param', \
),
	'draw:polygon' : ( \
		'draw:glue-point', \
		'office:event-listeners', \
		'svg:desc', \
		'svg:title', \
		'text:list', \
		'text:p', \
),
	'draw:polyline' : ( \
		'draw:glue-point', \
		'office:event-listeners', \
		'svg:desc', \
		'svg:title', \
		'text:list', \
		'text:p', \
),
	'draw:rect' : ( \
		'draw:glue-point', \
		'office:event-listeners', \
		'svg:desc', \
		'svg:title', \
		'text:list', \
		'text:p', \
),
	'draw:regular-polygon' : ( \
		'draw:glue-point', \
		'office:event-listeners', \
		'svg:desc', \
		'svg:title', \
		'text:list', \
		'text:p', \
),
	'draw:stroke-dash' : ( \
),
	'draw:text-box' : ( \
		'dr3d:scene', \
		'draw:a', \
		'draw:caption', \
		'draw:circle', \
		'draw:connector', \
		'draw:control', \
		'draw:custom-shape', \
		'draw:ellipse', \
		'draw:frame', \
		'draw:g', \
		'draw:line', \
		'draw:measure', \
		'draw:page-thumbnail', \
		'draw:path', \
		'draw:polygon', \
		'draw:polyline', \
		'draw:rect', \
		'draw:regular-polygon', \
		'table:table', \
		'text:alphabetical-index', \
		'text:bibliography', \
		'text:change', \
		'text:change-end', \
		'text:change-start', \
		'text:h', \
		'text:illustration-index', \
		'text:list', \
		'text:numbered-paragraph', \
		'text:object-index', \
		'text:p', \
		'text:section', \
		'text:soft-page-break', \
		'text:table-index', \
		'text:table-of-content', \
		'text:user-index', \
),
	'form:button' : ( \
		'form:properties', \
		'office:event-listeners', \
),
	'form:checkbox' : ( \
		'form:properties', \
		'office:event-listeners', \
),
	'form:column' : ( \
		'form:checkbox', \
		'form:combobox', \
		'form:date', \
		'form:formatted-text', \
		'form:listbox', \
		'form:number', \
		'form:text', \
		'form:textarea', \
		'form:time', \
),
	'form:combobox' : ( \
		'form:item', \
		'form:properties', \
		'office:event-listeners', \
),
	'form:connection-resource' : ( \
),
	'form:date' : ( \
		'form:properties', \
		'office:event-listeners', \
),
	'form:file' : ( \
		'form:properties', \
		'office:event-listeners', \
),
	'form:fixed-text' : ( \
		'form:properties', \
		'office:event-listeners', \
),
	'form:form' : ( \
		'form:button', \
		'form:checkbox', \
		'form:combobox', \
		'form:connection-resource', \
		'form:date', \
		'form:file', \
		'form:fixed-text', \
		'form:form', \
		'form:formatted-text', \
		'form:frame', \
		'form:generic-control', \
		'form:grid', \
		'form:hidden', \
		'form:image', \
		'form:image-frame', \
		'form:listbox', \
		'form:number', \
		'form:password', \
		'form:properties', \
		'form:radio', \
		'form:text', \
		'form:textarea', \
		'form:time', \
		'form:value-range', \
		'office:event-listeners', \
),
	'form:formatted-text' : ( \
		'form:properties', \
		'office:event-listeners', \
),
	'form:frame' : ( \
		'form:properties', \
		'office:event-listeners', \
),
	'form:generic-control' : ( \
		'form:properties', \
		'office:event-listeners', \
),
	'form:grid' : ( \
		'form:column', \
		'form:properties', \
		'office:event-listeners', \
),
	'form:hidden' : ( \
		'form:properties', \
		'office:event-listeners', \
),
	'form:image' : ( \
		'form:properties', \
		'office:event-listeners', \
),
	'form:image-frame' : ( \
		'form:properties', \
		'office:event-listeners', \
),
	'form:item' : ( \
),
	'form:list-property' : ( \
		'form:list-value', \
		'form:list-value', \
		'form:list-value', \
		'form:list-value', \
		'form:list-value', \
		'form:list-value', \
		'form:list-value', \
),
	'form:list-value' : ( \
),
	'form:list-value' : ( \
),
	'form:list-value' : ( \
),
	'form:list-value' : ( \
),
	'form:list-value' : ( \
),
	'form:list-value' : ( \
),
	'form:list-value' : ( \
),
	'form:listbox' : ( \
		'form:option', \
		'form:properties', \
		'office:event-listeners', \
),
	'form:number' : ( \
		'form:properties', \
		'office:event-listeners', \
),
	'form:option' : ( \
),
	'form:password' : ( \
		'form:properties', \
		'office:event-listeners', \
),
	'form:properties' : ( \
		'form:list-property', \
		'form:property', \
),
	'form:property' : ( \
),
	'form:radio' : ( \
		'form:properties', \
		'office:event-listeners', \
),
	'form:text' : ( \
		'form:properties', \
		'office:event-listeners', \
),
	'form:textarea' : ( \
		'form:properties', \
		'office:event-listeners', \
		'text:p', \
),
	'form:time' : ( \
		'form:properties', \
		'office:event-listeners', \
),
	'form:value-range' : ( \
		'form:properties', \
		'office:event-listeners', \
),
	'math:math' : ( \
		'*', \
),
	'meta:auto-reload' : ( \
),
	'meta:creation-date' : ( \
),
	'meta:date-string' : ( \
),
	'meta:document-statistic' : ( \
),
	'meta:editing-cycles' : ( \
),
	'meta:editing-duration' : ( \
),
	'meta:generator' : ( \
),
	'meta:hyperlink-behaviour' : ( \
),
	'meta:initial-creator' : ( \
),
	'meta:keyword' : ( \
),
	'meta:print-date' : ( \
),
	'meta:printed-by' : ( \
),
	'meta:template' : ( \
),
	'meta:user-defined' : ( \
),
	'number:am-pm' : ( \
),
	'number:boolean' : ( \
),
	'number:boolean-style' : ( \
		'number:boolean', \
		'number:text', \
		'style:map', \
		'style:text-properties', \
),
	'number:currency-style' : ( \
		'number:currency-symbol', \
		'number:number', \
		'number:text', \
		'style:map', \
		'style:text-properties', \
),
	'number:currency-symbol' : ( \
),
	'number:date-style' : ( \
		'number:am-pm', \
		'number:day', \
		'number:day-of-week', \
		'number:era', \
		'number:hours', \
		'number:minutes', \
		'number:month', \
		'number:quarter', \
		'number:seconds', \
		'number:text', \
		'number:week-of-year', \
		'number:year', \
		'style:map', \
		'style:text-properties', \
),
	'number:day' : ( \
),
	'number:day-of-week' : ( \
),
	'number:embedded-text' : ( \
),
	'number:era' : ( \
),
	'number:fraction' : ( \
),
	'number:hours' : ( \
),
	'number:minutes' : ( \
),
	'number:month' : ( \
),
	'number:number' : ( \
		'number:embedded-text', \
),
	'number:number-style' : ( \
		'number:fraction', \
		'number:number', \
		'number:scientific-number', \
		'number:text', \
		'style:map', \
		'style:text-properties', \
),
	'number:percentage-style' : ( \
		'number:number', \
		'number:text', \
		'style:map', \
		'style:text-properties', \
),
	'number:quarter' : ( \
),
	'number:scientific-number' : ( \
),
	'number:seconds' : ( \
),
	'number:text' : ( \
),
	'number:text-content' : ( \
),
	'number:text-style' : ( \
		'number:text', \
		'number:text-content', \
		'style:map', \
		'style:text-properties', \
),
	'number:time-style' : ( \
		'number:am-pm', \
		'number:hours', \
		'number:minutes', \
		'number:seconds', \
		'number:text', \
		'style:map', \
		'style:text-properties', \
),
	'number:week-of-year' : ( \
),
	'number:year' : ( \
),
	'office:annotation' : ( \
		'dc:creator', \
		'dc:date', \
		'meta:date-string', \
		'text:list', \
		'text:p', \
),
	'office:annotation-end' : ( \
),
	'office:automatic-styles' : ( \
		'number:boolean-style', \
		'number:currency-style', \
		'number:date-style', \
		'number:number-style', \
		'number:percentage-style', \
		'number:text-style', \
		'number:time-style', \
		'style:page-layout', \
		'style:style', \
		'text:list-style', \
),
	'office:binary-data' : ( \
),
	'office:body' : ( \
		'office:chart', \
		'office:database', \
		'office:drawing', \
		'office:image', \
		'office:presentation', \
		'office:spreadsheet', \
		'office:text', \
),
	'office:change-info' : ( \
		'dc:creator', \
		'dc:date', \
		'text:p', \
),
	'office:chart' : ( \
		'chart:chart', \
		'table:calculation-settings', \
		'table:consolidation', \
		'table:content-validations', \
		'table:data-pilot-tables', \
		'table:database-ranges', \
		'table:dde-links', \
		'table:label-ranges', \
		'table:named-expressions', \
		'text:alphabetical-index-auto-mark-file', \
		'text:dde-connection-decls', \
		'text:sequence-decls', \
		'text:user-field-decls', \
		'text:variable-decls', \
),
	'office:database' : ( \
		'db:data-source', \
		'db:forms', \
		'db:queries', \
		'db:reports', \
		'db:schema-definition', \
		'db:table-representations', \
),
	'office:dde-source' : ( \
),
	'office:document' : ( \
		'office:automatic-styles', \
		'office:body', \
		'office:font-face-decls', \
		'office:master-styles', \
		'office:meta', \
		'office:scripts', \
		'office:settings', \
		'office:styles', \
),
	'office:document-content' : ( \
		'office:automatic-styles', \
		'office:body', \
		'office:font-face-decls', \
		'office:scripts', \
),
	'office:document-meta' : ( \
		'office:meta', \
),
	'office:document-settings' : ( \
		'office:settings', \
),
	'office:document-styles' : ( \
		'office:automatic-styles', \
		'office:font-face-decls', \
		'office:master-styles', \
		'office:styles', \
),
	'office:drawing' : ( \
		'draw:page', \
		'table:calculation-settings', \
		'table:consolidation', \
		'table:content-validations', \
		'table:data-pilot-tables', \
		'table:database-ranges', \
		'table:dde-links', \
		'table:label-ranges', \
		'table:named-expressions', \
		'text:alphabetical-index-auto-mark-file', \
		'text:dde-connection-decls', \
		'text:sequence-decls', \
		'text:user-field-decls', \
		'text:variable-decls', \
),
	'office:event-listeners' : ( \
		'presentation:event-listener', \
		'script:event-listener', \
),
	'office:font-face-decls' : ( \
		'style:font-face', \
),
	'office:forms' : ( \
		'form:form', \
		'xforms:model', \
),
	'office:image' : ( \
		'draw:frame', \
),
	'office:master-styles' : ( \
		'draw:layer-set', \
		'style:handout-master', \
		'style:master-page', \
),
	'office:meta' : ( \
		'dc:creator', \
		'dc:date', \
		'dc:description', \
		'dc:language', \
		'dc:subject', \
		'dc:title', \
		'meta:auto-reload', \
		'meta:creation-date', \
		'meta:document-statistic', \
		'meta:editing-cycles', \
		'meta:editing-duration', \
		'meta:generator', \
		'meta:hyperlink-behaviour', \
		'meta:initial-creator', \
		'meta:keyword', \
		'meta:print-date', \
		'meta:printed-by', \
		'meta:template', \
		'meta:user-defined', \
),
	'office:presentation' : ( \
		'draw:page', \
		'presentation:date-time-decl', \
		'presentation:footer-decl', \
		'presentation:header-decl', \
		'presentation:settings', \
		'table:calculation-settings', \
		'table:consolidation', \
		'table:content-validations', \
		'table:data-pilot-tables', \
		'table:database-ranges', \
		'table:dde-links', \
		'table:label-ranges', \
		'table:named-expressions', \
		'text:alphabetical-index-auto-mark-file', \
		'text:dde-connection-decls', \
		'text:sequence-decls', \
		'text:user-field-decls', \
		'text:variable-decls', \
),
	'office:script' : ( \
		'*', \
),
	'office:scripts' : ( \
		'office:event-listeners', \
		'office:script', \
),
	'office:settings' : ( \
		'config:config-item-set', \
),
	'office:spreadsheet' : ( \
		'table:calculation-settings', \
		'table:consolidation', \
		'table:content-validations', \
		'table:data-pilot-tables', \
		'table:database-ranges', \
		'table:dde-links', \
		'table:label-ranges', \
		'table:named-expressions', \
		'table:table', \
		'table:tracked-changes', \
		'text:alphabetical-index-auto-mark-file', \
		'text:dde-connection-decls', \
		'text:sequence-decls', \
		'text:user-field-decls', \
		'text:variable-decls', \
),
	'office:styles' : ( \
		'draw:fill-image', \
		'draw:gradient', \
		'draw:hatch', \
		'draw:marker', \
		'draw:opacity', \
		'draw:stroke-dash', \
		'number:boolean-style', \
		'number:currency-style', \
		'number:date-style', \
		'number:number-style', \
		'number:percentage-style', \
		'number:text-style', \
		'number:time-style', \
		'style:default-page-layout', \
		'style:default-style', \
		'style:presentation-page-layout', \
		'style:style', \
		'svg:linearGradient', \
		'svg:radialGradient', \
		'table:table-template', \
		'text:bibliography-configuration', \
		'text:linenumbering-configuration', \
		'text:list-style', \
		'text:notes-configuration', \
		'text:outline-style', \
),
	'office:text' : ( \
		'dr3d:scene', \
		'draw:a', \
		'draw:caption', \
		'draw:circle', \
		'draw:connector', \
		'draw:control', \
		'draw:custom-shape', \
		'draw:ellipse', \
		'draw:frame', \
		'draw:g', \
		'draw:line', \
		'draw:measure', \
		'draw:page-thumbnail', \
		'draw:path', \
		'draw:polygon', \
		'draw:polyline', \
		'draw:rect', \
		'draw:regular-polygon', \
		'office:forms', \
		'table:calculation-settings', \
		'table:consolidation', \
		'table:content-validations', \
		'table:data-pilot-tables', \
		'table:database-ranges', \
		'table:dde-links', \
		'table:label-ranges', \
		'table:named-expressions', \
		'table:table', \
		'text:alphabetical-index', \
		'text:alphabetical-index-auto-mark-file', \
		'text:bibliography', \
		'text:change', \
		'text:change-end', \
		'text:change-start', \
		'text:dde-connection-decls', \
		'text:h', \
		'text:illustration-index', \
		'text:list', \
		'text:numbered-paragraph', \
		'text:object-index', \
		'text:p', \
		'text:page-sequence', \
		'text:section', \
		'text:sequence-decls', \
		'text:soft-page-break', \
		'text:table-index', \
		'text:table-of-content', \
		'text:tracked-changes', \
		'text:user-field-decls', \
		'text:user-index', \
		'text:variable-decls', \
),
	'presentation:animation-group' : ( \
		'presentation:dim', \
		'presentation:hide-shape', \
		'presentation:hide-text', \
		'presentation:play', \
		'presentation:show-shape', \
		'presentation:show-text', \
),
	'presentation:animations' : ( \
		'presentation:animation-group', \
		'presentation:dim', \
		'presentation:hide-shape', \
		'presentation:hide-text', \
		'presentation:play', \
		'presentation:show-shape', \
		'presentation:show-text', \
),
	'presentation:date-time' : ( \
),
	'presentation:date-time-decl' : ( \
),
	'presentation:dim' : ( \
		'presentation:sound', \
),
	'presentation:event-listener' : ( \
		'presentation:sound', \
),
	'presentation:footer' : ( \
),
	'presentation:footer-decl' : ( \
),
	'presentation:header' : ( \
),
	'presentation:header-decl' : ( \
),
	'presentation:hide-shape' : ( \
		'presentation:sound', \
),
	'presentation:hide-text' : ( \
		'presentation:sound', \
),
	'presentation:notes' : ( \
		'dr3d:scene', \
		'draw:a', \
		'draw:caption', \
		'draw:circle', \
		'draw:connector', \
		'draw:control', \
		'draw:custom-shape', \
		'draw:ellipse', \
		'draw:frame', \
		'draw:g', \
		'draw:line', \
		'draw:measure', \
		'draw:page-thumbnail', \
		'draw:path', \
		'draw:polygon', \
		'draw:polyline', \
		'draw:rect', \
		'draw:regular-polygon', \
		'office:forms', \
),
	'presentation:placeholder' : ( \
),
	'presentation:play' : ( \
),
	'presentation:settings' : ( \
		'presentation:show', \
),
	'presentation:show' : ( \
),
	'presentation:show-shape' : ( \
		'presentation:sound', \
),
	'presentation:show-text' : ( \
		'presentation:sound', \
),
	'presentation:sound' : ( \
),
	'script:event-listener' : ( \
),
	'style:background-image' : ( \
		'office:binary-data', \
),
	'style:chart-properties' : ( \
		'chart:label-separator', \
		'chart:symbol-image', \
),
	'style:column' : ( \
),
	'style:column-sep' : ( \
),
	'style:columns' : ( \
		'style:column', \
		'style:column-sep', \
),
	'style:default-page-layout' : ( \
		'style:footer-style', \
		'style:header-style', \
		'style:page-layout-properties', \
),
	'style:default-style' : ( \
		'style:chart-properties', \
		'style:drawing-page-properties', \
		'style:graphic-properties', \
		'style:paragraph-properties', \
		'style:ruby-properties', \
		'style:section-properties', \
		'style:table-cell-properties', \
		'style:table-column-properties', \
		'style:table-properties', \
		'style:table-row-properties', \
		'style:text-properties', \
),
	'style:drawing-page-properties' : ( \
		'presentation:sound', \
),
	'style:drop-cap' : ( \
),
	'style:font-face' : ( \
		'svg:definition-src', \
		'svg:font-face-src', \
),
	'style:footer' : ( \
		'style:region-center', \
		'style:region-left', \
		'style:region-right', \
		'table:table', \
		'text:alphabetical-index', \
		'text:alphabetical-index-auto-mark-file', \
		'text:bibliography', \
		'text:change', \
		'text:change-end', \
		'text:change-start', \
		'text:dde-connection-decls', \
		'text:h', \
		'text:illustration-index', \
		'text:index-title', \
		'text:list', \
		'text:object-index', \
		'text:p', \
		'text:section', \
		'text:sequence-decls', \
		'text:table-index', \
		'text:table-of-content', \
		'text:tracked-changes', \
		'text:user-field-decls', \
		'text:user-index', \
		'text:variable-decls', \
),
	'style:footer-left' : ( \
		'style:region-center', \
		'style:region-left', \
		'style:region-right', \
		'table:table', \
		'text:alphabetical-index', \
		'text:alphabetical-index-auto-mark-file', \
		'text:bibliography', \
		'text:change', \
		'text:change-end', \
		'text:change-start', \
		'text:dde-connection-decls', \
		'text:h', \
		'text:illustration-index', \
		'text:index-title', \
		'text:list', \
		'text:object-index', \
		'text:p', \
		'text:section', \
		'text:sequence-decls', \
		'text:table-index', \
		'text:table-of-content', \
		'text:tracked-changes', \
		'text:user-field-decls', \
		'text:user-index', \
		'text:variable-decls', \
),
	'style:footer-style' : ( \
		'style:header-footer-properties', \
),
	'style:footnote-sep' : ( \
),
	'style:graphic-properties' : ( \
		'style:background-image', \
		'style:columns', \
		'text:list-style', \
),
	'style:handout-master' : ( \
		'dr3d:scene', \
		'draw:a', \
		'draw:caption', \
		'draw:circle', \
		'draw:connector', \
		'draw:control', \
		'draw:custom-shape', \
		'draw:ellipse', \
		'draw:frame', \
		'draw:g', \
		'draw:line', \
		'draw:measure', \
		'draw:page-thumbnail', \
		'draw:path', \
		'draw:polygon', \
		'draw:polyline', \
		'draw:rect', \
		'draw:regular-polygon', \
),
	'style:header' : ( \
		'style:region-center', \
		'style:region-left', \
		'style:region-right', \
		'table:table', \
		'text:alphabetical-index', \
		'text:alphabetical-index-auto-mark-file', \
		'text:bibliography', \
		'text:change', \
		'text:change-end', \
		'text:change-start', \
		'text:dde-connection-decls', \
		'text:h', \
		'text:illustration-index', \
		'text:index-title', \
		'text:list', \
		'text:object-index', \
		'text:p', \
		'text:section', \
		'text:sequence-decls', \
		'text:table-index', \
		'text:table-of-content', \
		'text:tracked-changes', \
		'text:user-field-decls', \
		'text:user-index', \
		'text:variable-decls', \
),
	'style:header-footer-properties' : ( \
		'style:background-image', \
),
	'style:header-left' : ( \
		'style:region-center', \
		'style:region-left', \
		'style:region-right', \
		'table:table', \
		'text:alphabetical-index', \
		'text:alphabetical-index-auto-mark-file', \
		'text:bibliography', \
		'text:change', \
		'text:change-end', \
		'text:change-start', \
		'text:dde-connection-decls', \
		'text:h', \
		'text:illustration-index', \
		'text:index-title', \
		'text:list', \
		'text:object-index', \
		'text:p', \
		'text:section', \
		'text:sequence-decls', \
		'text:table-index', \
		'text:table-of-content', \
		'text:tracked-changes', \
		'text:user-field-decls', \
		'text:user-index', \
		'text:variable-decls', \
),
	'style:header-style' : ( \
		'style:header-footer-properties', \
),
	'style:list-level-label-alignment' : ( \
),
	'style:list-level-properties' : ( \
		'style:list-level-label-alignment', \
),
	'style:map' : ( \
),
	'style:master-page' : ( \
		'anim:animate', \
		'anim:animateColor', \
		'anim:animateMotion', \
		'anim:animateTransform', \
		'anim:audio', \
		'anim:command', \
		'anim:iterate', \
		'anim:par', \
		'anim:seq', \
		'anim:set', \
		'anim:transitionFilter', \
		'dr3d:scene', \
		'draw:a', \
		'draw:caption', \
		'draw:circle', \
		'draw:connector', \
		'draw:control', \
		'draw:custom-shape', \
		'draw:ellipse', \
		'draw:frame', \
		'draw:g', \
		'draw:layer-set', \
		'draw:line', \
		'draw:measure', \
		'draw:page-thumbnail', \
		'draw:path', \
		'draw:polygon', \
		'draw:polyline', \
		'draw:rect', \
		'draw:regular-polygon', \
		'office:forms', \
		'presentation:notes', \
		'style:footer', \
		'style:footer-left', \
		'style:header', \
		'style:header-left', \
),
	'style:page-layout' : ( \
		'style:footer-style', \
		'style:header-style', \
		'style:page-layout-properties', \
),
	'style:page-layout-properties' : ( \
		'style:background-image', \
		'style:columns', \
		'style:footnote-sep', \
),
	'style:paragraph-properties' : ( \
		'style:background-image', \
		'style:drop-cap', \
		'style:tab-stops', \
),
	'style:presentation-page-layout' : ( \
		'presentation:placeholder', \
),
	'style:region-center' : ( \
		'text:p', \
),
	'style:region-left' : ( \
		'text:p', \
),
	'style:region-right' : ( \
		'text:p', \
),
	'style:ruby-properties' : ( \
),
	'style:section-properties' : ( \
		'style:background-image', \
		'style:columns', \
		'text:notes-configuration', \
),
	'style:style' : ( \
		'style:chart-properties', \
		'style:drawing-page-properties', \
		'style:graphic-properties', \
		'style:map', \
		'style:paragraph-properties', \
		'style:ruby-properties', \
		'style:section-properties', \
		'style:table-cell-properties', \
		'style:table-column-properties', \
		'style:table-properties', \
		'style:table-row-properties', \
		'style:text-properties', \
),
	'style:tab-stop' : ( \
),
	'style:tab-stops' : ( \
		'style:tab-stop', \
),
	'style:table-cell-properties' : ( \
		'style:background-image', \
),
	'style:table-column-properties' : ( \
),
	'style:table-properties' : ( \
		'style:background-image', \
),
	'style:table-row-properties' : ( \
		'style:background-image', \
),
	'style:text-properties' : ( \
),
	'svg:definition-src' : ( \
),
	'svg:desc' : ( \
),
	'svg:font-face-format' : ( \
),
	'svg:font-face-name' : ( \
),
	'svg:font-face-src' : ( \
		'svg:font-face-name', \
		'svg:font-face-uri', \
),
	'svg:font-face-uri' : ( \
		'svg:font-face-format', \
),
	'svg:linearGradient' : ( \
		'svg:stop', \
),
	'svg:radialGradient' : ( \
		'svg:stop', \
),
	'svg:stop' : ( \
),
	'svg:title' : ( \
),
	'table:background' : ( \
),
	'table:body' : ( \
),
	'table:calculation-settings' : ( \
		'table:iteration', \
		'table:null-date', \
),
	'table:cell-address' : ( \
),
	'table:cell-content-change' : ( \
		'office:change-info', \
		'table:cell-address', \
		'table:deletions', \
		'table:dependencies', \
		'table:previous', \
),
	'table:cell-content-deletion' : ( \
		'table:cell-address', \
		'table:change-track-table-cell', \
),
	'table:cell-range-source' : ( \
),
	'table:change-deletion' : ( \
),
	'table:change-track-table-cell' : ( \
		'text:p', \
),
	'table:consolidation' : ( \
),
	'table:content-validation' : ( \
		'office:event-listeners', \
		'table:error-macro', \
		'table:error-message', \
		'table:help-message', \
),
	'table:content-validations' : ( \
		'table:content-validation', \
),
	'table:covered-table-cell' : ( \
		'dr3d:scene', \
		'draw:a', \
		'draw:caption', \
		'draw:circle', \
		'draw:connector', \
		'draw:control', \
		'draw:custom-shape', \
		'draw:ellipse', \
		'draw:frame', \
		'draw:g', \
		'draw:line', \
		'draw:measure', \
		'draw:page-thumbnail', \
		'draw:path', \
		'draw:polygon', \
		'draw:polyline', \
		'draw:rect', \
		'draw:regular-polygon', \
		'office:annotation', \
		'table:cell-range-source', \
		'table:detective', \
		'table:table', \
		'text:alphabetical-index', \
		'text:bibliography', \
		'text:change', \
		'text:change-end', \
		'text:change-start', \
		'text:h', \
		'text:illustration-index', \
		'text:list', \
		'text:numbered-paragraph', \
		'text:object-index', \
		'text:p', \
		'text:section', \
		'text:soft-page-break', \
		'text:table-index', \
		'text:table-of-content', \
		'text:user-index', \
),
	'table:cut-offs' : ( \
		'table:insertion-cut-off', \
		'table:movement-cut-off', \
),
	'table:data-pilot-display-info' : ( \
),
	'table:data-pilot-field' : ( \
		'table:data-pilot-field-reference', \
		'table:data-pilot-groups', \
		'table:data-pilot-level', \
),
	'table:data-pilot-field-reference' : ( \
),
	'table:data-pilot-group' : ( \
		'table:data-pilot-group-member', \
),
	'table:data-pilot-group-member' : ( \
),
	'table:data-pilot-groups' : ( \
		'table:data-pilot-group', \
),
	'table:data-pilot-layout-info' : ( \
),
	'table:data-pilot-level' : ( \
		'table:data-pilot-display-info', \
		'table:data-pilot-layout-info', \
		'table:data-pilot-members', \
		'table:data-pilot-sort-info', \
		'table:data-pilot-subtotals', \
),
	'table:data-pilot-member' : ( \
),
	'table:data-pilot-members' : ( \
		'table:data-pilot-member', \
),
	'table:data-pilot-sort-info' : ( \
),
	'table:data-pilot-subtotal' : ( \
),
	'table:data-pilot-subtotals' : ( \
		'table:data-pilot-subtotal', \
),
	'table:data-pilot-table' : ( \
		'table:data-pilot-field', \
		'table:database-source-query', \
		'table:database-source-sql', \
		'table:database-source-table', \
		'table:source-cell-range', \
		'table:source-service', \
),
	'table:data-pilot-tables' : ( \
		'table:data-pilot-table', \
),
	'table:database-range' : ( \
		'table:database-source-query', \
		'table:database-source-sql', \
		'table:database-source-table', \
		'table:filter', \
		'table:sort', \
		'table:subtotal-rules', \
),
	'table:database-ranges' : ( \
		'table:database-range', \
),
	'table:database-source-query' : ( \
),
	'table:database-source-sql' : ( \
),
	'table:database-source-table' : ( \
),
	'table:dde-link' : ( \
		'office:dde-source', \
		'table:table', \
),
	'table:dde-links' : ( \
		'table:dde-link', \
),
	'table:deletion' : ( \
		'office:change-info', \
		'table:cut-offs', \
		'table:deletions', \
		'table:dependencies', \
),
	'table:deletions' : ( \
		'table:cell-content-deletion', \
		'table:change-deletion', \
),
	'table:dependencies' : ( \
		'table:dependency', \
),
	'table:dependency' : ( \
),
	'table:desc' : ( \
),
	'table:detective' : ( \
		'table:highlighted-range', \
		'table:operation', \
),
	'table:error-macro' : ( \
),
	'table:error-message' : ( \
		'text:p', \
),
	'table:even-columns' : ( \
),
	'table:even-rows' : ( \
),
	'table:filter' : ( \
		'table:filter-and', \
		'table:filter-condition', \
		'table:filter-or', \
),
	'table:filter-and' : ( \
		'table:filter-condition', \
		'table:filter-or', \
),
	'table:filter-condition' : ( \
		'table:filter-set-item', \
),
	'table:filter-or' : ( \
		'table:filter-and', \
		'table:filter-condition', \
),
	'table:filter-set-item' : ( \
),
	'table:first-column' : ( \
),
	'table:first-row' : ( \
),
	'table:help-message' : ( \
		'text:p', \
),
	'table:highlighted-range' : ( \
),
	'table:insertion' : ( \
		'office:change-info', \
		'table:deletions', \
		'table:dependencies', \
),
	'table:insertion-cut-off' : ( \
),
	'table:iteration' : ( \
),
	'table:label-range' : ( \
),
	'table:label-ranges' : ( \
		'table:label-range', \
),
	'table:last-column' : ( \
),
	'table:last-row' : ( \
),
	'table:movement' : ( \
		'office:change-info', \
		'table:deletions', \
		'table:dependencies', \
		'table:source-range-address', \
		'table:target-range-address', \
),
	'table:movement-cut-off' : ( \
),
	'table:named-expression' : ( \
),
	'table:named-expressions' : ( \
		'table:named-expression', \
		'table:named-range', \
),
	'table:named-range' : ( \
),
	'table:null-date' : ( \
),
	'table:odd-columns' : ( \
),
	'table:odd-rows' : ( \
),
	'table:operation' : ( \
),
	'table:previous' : ( \
		'table:change-track-table-cell', \
),
	'table:scenario' : ( \
),
	'table:shapes' : ( \
		'dr3d:scene', \
		'draw:a', \
		'draw:caption', \
		'draw:circle', \
		'draw:connector', \
		'draw:control', \
		'draw:custom-shape', \
		'draw:ellipse', \
		'draw:frame', \
		'draw:g', \
		'draw:line', \
		'draw:measure', \
		'draw:page-thumbnail', \
		'draw:path', \
		'draw:polygon', \
		'draw:polyline', \
		'draw:rect', \
		'draw:regular-polygon', \
),
	'table:sort' : ( \
		'table:sort-by', \
),
	'table:sort-by' : ( \
),
	'table:sort-groups' : ( \
),
	'table:source-cell-range' : ( \
		'table:filter', \
),
	'table:source-range-address' : ( \
),
	'table:source-service' : ( \
),
	'table:subtotal-field' : ( \
),
	'table:subtotal-rule' : ( \
		'table:subtotal-field', \
),
	'table:subtotal-rules' : ( \
		'table:sort-groups', \
		'table:subtotal-rule', \
),
	'table:table' : ( \
		'office:dde-source', \
		'office:forms', \
		'table:desc', \
		'table:named-expressions', \
		'table:scenario', \
		'table:shapes', \
		'table:table-column', \
		'table:table-column-group', \
		'table:table-columns', \
		'table:table-header-columns', \
		'table:table-header-rows', \
		'table:table-row', \
		'table:table-row-group', \
		'table:table-rows', \
		'table:table-source', \
		'table:title', \
		'text:soft-page-break', \
),
	'table:table-cell' : ( \
		'dr3d:scene', \
		'draw:a', \
		'draw:caption', \
		'draw:circle', \
		'draw:connector', \
		'draw:control', \
		'draw:custom-shape', \
		'draw:ellipse', \
		'draw:frame', \
		'draw:g', \
		'draw:line', \
		'draw:measure', \
		'draw:page-thumbnail', \
		'draw:path', \
		'draw:polygon', \
		'draw:polyline', \
		'draw:rect', \
		'draw:regular-polygon', \
		'office:annotation', \
		'table:cell-range-source', \
		'table:detective', \
		'table:table', \
		'text:alphabetical-index', \
		'text:bibliography', \
		'text:change', \
		'text:change-end', \
		'text:change-start', \
		'text:h', \
		'text:illustration-index', \
		'text:list', \
		'text:numbered-paragraph', \
		'text:object-index', \
		'text:p', \
		'text:section', \
		'text:soft-page-break', \
		'text:table-index', \
		'text:table-of-content', \
		'text:user-index', \
),
	'table:table-column' : ( \
),
	'table:table-column-group' : ( \
		'table:table-column', \
		'table:table-column-group', \
		'table:table-columns', \
		'table:table-header-columns', \
),
	'table:table-columns' : ( \
		'table:table-column', \
),
	'table:table-header-columns' : ( \
		'table:table-column', \
),
	'table:table-header-rows' : ( \
		'table:table-row', \
		'text:soft-page-break', \
),
	'table:table-row' : ( \
		'table:covered-table-cell', \
		'table:table-cell', \
),
	'table:table-row-group' : ( \
		'table:table-header-rows', \
		'table:table-row', \
		'table:table-row-group', \
		'table:table-rows', \
		'text:soft-page-break', \
),
	'table:table-rows' : ( \
		'table:table-row', \
		'text:soft-page-break', \
),
	'table:table-source' : ( \
),
	'table:table-template' : ( \
		'table:background', \
		'table:body', \
		'table:even-columns', \
		'table:even-rows', \
		'table:first-column', \
		'table:first-row', \
		'table:last-column', \
		'table:last-row', \
		'table:odd-columns', \
		'table:odd-rows', \
),
	'table:target-range-address' : ( \
),
	'table:title' : ( \
),
	'table:tracked-changes' : ( \
		'table:cell-content-change', \
		'table:deletion', \
		'table:insertion', \
		'table:movement', \
),
	'text:a' : ( \
		'dr3d:scene', \
		'draw:a', \
		'draw:caption', \
		'draw:circle', \
		'draw:connector', \
		'draw:control', \
		'draw:custom-shape', \
		'draw:ellipse', \
		'draw:frame', \
		'draw:g', \
		'draw:line', \
		'draw:measure', \
		'draw:page-thumbnail', \
		'draw:path', \
		'draw:polygon', \
		'draw:polyline', \
		'draw:rect', \
		'draw:regular-polygon', \
		'office:annotation', \
		'office:annotation-end', \
		'office:event-listeners', \
		'presentation:date-time', \
		'presentation:footer', \
		'presentation:header', \
		'text:alphabetical-index-mark', \
		'text:alphabetical-index-mark-end', \
		'text:alphabetical-index-mark-start', \
		'text:author-initials', \
		'text:author-name', \
		'text:bibliography-mark', \
		'text:bookmark', \
		'text:bookmark-end', \
		'text:bookmark-ref', \
		'text:bookmark-start', \
		'text:change', \
		'text:change-end', \
		'text:change-start', \
		'text:chapter', \
		'text:character-count', \
		'text:conditional-text', \
		'text:creation-date', \
		'text:creation-time', \
		'text:creator', \
		'text:database-display', \
		'text:database-name', \
		'text:database-next', \
		'text:database-row-number', \
		'text:database-row-select', \
		'text:date', \
		'text:dde-connection', \
		'text:description', \
		'text:editing-cycles', \
		'text:editing-duration', \
		'text:execute-macro', \
		'text:expression', \
		'text:file-name', \
		'text:hidden-paragraph', \
		'text:hidden-text', \
		'text:image-count', \
		'text:initial-creator', \
		'text:keywords', \
		'text:line-break', \
		'text:measure', \
		'text:meta', \
		'text:meta-field', \
		'text:modification-date', \
		'text:modification-time', \
		'text:note', \
		'text:note-ref', \
		'text:object-count', \
		'text:page-continuation', \
		'text:page-count', \
		'text:page-number', \
		'text:page-variable-get', \
		'text:page-variable-set', \
		'text:paragraph-count', \
		'text:placeholder', \
		'text:print-date', \
		'text:print-time', \
		'text:printed-by', \
		'text:reference-mark', \
		'text:reference-mark-end', \
		'text:reference-mark-start', \
		'text:reference-ref', \
		'text:ruby', \
		'text:s', \
		'text:script', \
		'text:sender-city', \
		'text:sender-company', \
		'text:sender-country', \
		'text:sender-email', \
		'text:sender-fax', \
		'text:sender-firstname', \
		'text:sender-initials', \
		'text:sender-lastname', \
		'text:sender-phone-private', \
		'text:sender-phone-work', \
		'text:sender-position', \
		'text:sender-postal-code', \
		'text:sender-state-or-province', \
		'text:sender-street', \
		'text:sender-title', \
		'text:sequence', \
		'text:sequence-ref', \
		'text:sheet-name', \
		'text:soft-page-break', \
		'text:span', \
		'text:subject', \
		'text:tab', \
		'text:table-count', \
		'text:table-formula', \
		'text:template-name', \
		'text:text-input', \
		'text:time', \
		'text:title', \
		'text:toc-mark', \
		'text:toc-mark-end', \
		'text:toc-mark-start', \
		'text:user-defined', \
		'text:user-field-get', \
		'text:user-field-input', \
		'text:user-index-mark', \
		'text:user-index-mark-end', \
		'text:user-index-mark-start', \
		'text:variable-get', \
		'text:variable-input', \
		'text:variable-set', \
		'text:word-count', \
),
	'text:alphabetical-index' : ( \
		'text:alphabetical-index-source', \
		'text:index-body', \
),
	'text:alphabetical-index-auto-mark-file' : ( \
),
	'text:alphabetical-index-entry-template' : ( \
		'text:index-entry-chapter', \
		'text:index-entry-page-number', \
		'text:index-entry-span', \
		'text:index-entry-tab-stop', \
		'text:index-entry-text', \
),
	'text:alphabetical-index-mark' : ( \
),
	'text:alphabetical-index-mark-end' : ( \
),
	'text:alphabetical-index-mark-start' : ( \
),
	'text:alphabetical-index-source' : ( \
		'text:alphabetical-index-entry-template', \
		'text:index-title-template', \
),
	'text:author-initials' : ( \
),
	'text:author-name' : ( \
),
	'text:bibliography' : ( \
		'text:bibliography-source', \
		'text:index-body', \
),
	'text:bibliography-configuration' : ( \
		'text:sort-key', \
),
	'text:bibliography-entry-template' : ( \
		'text:index-entry-bibliography', \
		'text:index-entry-span', \
		'text:index-entry-tab-stop', \
),
	'text:bibliography-mark' : ( \
),
	'text:bibliography-source' : ( \
		'text:bibliography-entry-template', \
		'text:index-title-template', \
),
	'text:bookmark' : ( \
),
	'text:bookmark-end' : ( \
),
	'text:bookmark-ref' : ( \
),
	'text:bookmark-start' : ( \
),
	'text:change' : ( \
),
	'text:change-end' : ( \
),
	'text:change-start' : ( \
),
	'text:changed-region' : ( \
		'text:deletion', \
		'text:format-change', \
		'text:insertion', \
),
	'text:chapter' : ( \
),
	'text:character-count' : ( \
),
	'text:conditional-text' : ( \
),
	'text:creation-date' : ( \
),
	'text:creation-time' : ( \
),
	'text:creator' : ( \
),
	'text:database-display' : ( \
		'form:connection-resource', \
),
	'text:database-name' : ( \
		'form:connection-resource', \
),
	'text:database-next' : ( \
		'form:connection-resource', \
),
	'text:database-row-number' : ( \
		'form:connection-resource', \
),
	'text:database-row-select' : ( \
		'form:connection-resource', \
),
	'text:date' : ( \
),
	'text:dde-connection' : ( \
),
	'text:dde-connection-decl' : ( \
),
	'text:dde-connection-decls' : ( \
		'text:dde-connection-decl', \
),
	'text:deletion' : ( \
		'dr3d:scene', \
		'draw:a', \
		'draw:caption', \
		'draw:circle', \
		'draw:connector', \
		'draw:control', \
		'draw:custom-shape', \
		'draw:ellipse', \
		'draw:frame', \
		'draw:g', \
		'draw:line', \
		'draw:measure', \
		'draw:page-thumbnail', \
		'draw:path', \
		'draw:polygon', \
		'draw:polyline', \
		'draw:rect', \
		'draw:regular-polygon', \
		'office:change-info', \
		'table:table', \
		'text:alphabetical-index', \
		'text:bibliography', \
		'text:change', \
		'text:change-end', \
		'text:change-start', \
		'text:h', \
		'text:illustration-index', \
		'text:list', \
		'text:numbered-paragraph', \
		'text:object-index', \
		'text:p', \
		'text:section', \
		'text:soft-page-break', \
		'text:table-index', \
		'text:table-of-content', \
		'text:user-index', \
),
	'text:description' : ( \
),
	'text:editing-cycles' : ( \
),
	'text:editing-duration' : ( \
),
	'text:execute-macro' : ( \
		'office:event-listeners', \
),
	'text:expression' : ( \
),
	'text:file-name' : ( \
),
	'text:format-change' : ( \
		'office:change-info', \
),
	'text:h' : ( \
		'dr3d:scene', \
		'draw:a', \
		'draw:caption', \
		'draw:circle', \
		'draw:connector', \
		'draw:control', \
		'draw:custom-shape', \
		'draw:ellipse', \
		'draw:frame', \
		'draw:g', \
		'draw:line', \
		'draw:measure', \
		'draw:page-thumbnail', \
		'draw:path', \
		'draw:polygon', \
		'draw:polyline', \
		'draw:rect', \
		'draw:regular-polygon', \
		'office:annotation', \
		'office:annotation-end', \
		'presentation:date-time', \
		'presentation:footer', \
		'presentation:header', \
		'text:a', \
		'text:alphabetical-index-mark', \
		'text:alphabetical-index-mark-end', \
		'text:alphabetical-index-mark-start', \
		'text:author-initials', \
		'text:author-name', \
		'text:bibliography-mark', \
		'text:bookmark', \
		'text:bookmark-end', \
		'text:bookmark-ref', \
		'text:bookmark-start', \
		'text:change', \
		'text:change-end', \
		'text:change-start', \
		'text:chapter', \
		'text:character-count', \
		'text:conditional-text', \
		'text:creation-date', \
		'text:creation-time', \
		'text:creator', \
		'text:database-display', \
		'text:database-name', \
		'text:database-next', \
		'text:database-row-number', \
		'text:database-row-select', \
		'text:date', \
		'text:dde-connection', \
		'text:description', \
		'text:editing-cycles', \
		'text:editing-duration', \
		'text:execute-macro', \
		'text:expression', \
		'text:file-name', \
		'text:hidden-paragraph', \
		'text:hidden-text', \
		'text:image-count', \
		'text:initial-creator', \
		'text:keywords', \
		'text:line-break', \
		'text:measure', \
		'text:meta', \
		'text:meta-field', \
		'text:modification-date', \
		'text:modification-time', \
		'text:note', \
		'text:note-ref', \
		'text:number', \
		'text:object-count', \
		'text:page-continuation', \
		'text:page-count', \
		'text:page-number', \
		'text:page-variable-get', \
		'text:page-variable-set', \
		'text:paragraph-count', \
		'text:placeholder', \
		'text:print-date', \
		'text:print-time', \
		'text:printed-by', \
		'text:reference-mark', \
		'text:reference-mark-end', \
		'text:reference-mark-start', \
		'text:reference-ref', \
		'text:ruby', \
		'text:s', \
		'text:script', \
		'text:sender-city', \
		'text:sender-company', \
		'text:sender-country', \
		'text:sender-email', \
		'text:sender-fax', \
		'text:sender-firstname', \
		'text:sender-initials', \
		'text:sender-lastname', \
		'text:sender-phone-private', \
		'text:sender-phone-work', \
		'text:sender-position', \
		'text:sender-postal-code', \
		'text:sender-state-or-province', \
		'text:sender-street', \
		'text:sender-title', \
		'text:sequence', \
		'text:sequence-ref', \
		'text:sheet-name', \
		'text:soft-page-break', \
		'text:span', \
		'text:subject', \
		'text:tab', \
		'text:table-count', \
		'text:table-formula', \
		'text:template-name', \
		'text:text-input', \
		'text:time', \
		'text:title', \
		'text:toc-mark', \
		'text:toc-mark-end', \
		'text:toc-mark-start', \
		'text:user-defined', \
		'text:user-field-get', \
		'text:user-field-input', \
		'text:user-index-mark', \
		'text:user-index-mark-end', \
		'text:user-index-mark-start', \
		'text:variable-get', \
		'text:variable-input', \
		'text:variable-set', \
		'text:word-count', \
),
	'text:hidden-paragraph' : ( \
),
	'text:hidden-text' : ( \
),
	'text:illustration-index' : ( \
		'text:illustration-index-source', \
		'text:index-body', \
),
	'text:illustration-index-entry-template' : ( \
		'text:index-entry-chapter', \
		'text:index-entry-page-number', \
		'text:index-entry-span', \
		'text:index-entry-tab-stop', \
		'text:index-entry-text', \
),
	'text:illustration-index-source' : ( \
		'text:illustration-index-entry-template', \
		'text:index-title-template', \
),
	'text:image-count' : ( \
),
	'text:index-body' : ( \
		'dr3d:scene', \
		'draw:a', \
		'draw:caption', \
		'draw:circle', \
		'draw:connector', \
		'draw:control', \
		'draw:custom-shape', \
		'draw:ellipse', \
		'draw:frame', \
		'draw:g', \
		'draw:line', \
		'draw:measure', \
		'draw:page-thumbnail', \
		'draw:path', \
		'draw:polygon', \
		'draw:polyline', \
		'draw:rect', \
		'draw:regular-polygon', \
		'table:table', \
		'text:alphabetical-index', \
		'text:bibliography', \
		'text:change', \
		'text:change-end', \
		'text:change-start', \
		'text:h', \
		'text:illustration-index', \
		'text:index-title', \
		'text:list', \
		'text:numbered-paragraph', \
		'text:object-index', \
		'text:p', \
		'text:section', \
		'text:soft-page-break', \
		'text:table-index', \
		'text:table-of-content', \
		'text:user-index', \
),
	'text:index-entry-bibliography' : ( \
),
	'text:index-entry-chapter' : ( \
),
	'text:index-entry-link-end' : ( \
),
	'text:index-entry-link-start' : ( \
),
	'text:index-entry-page-number' : ( \
),
	'text:index-entry-span' : ( \
),
	'text:index-entry-tab-stop' : ( \
),
	'text:index-entry-text' : ( \
),
	'text:index-source-style' : ( \
),
	'text:index-source-styles' : ( \
		'text:index-source-style', \
),
	'text:index-title' : ( \
		'dr3d:scene', \
		'draw:a', \
		'draw:caption', \
		'draw:circle', \
		'draw:connector', \
		'draw:control', \
		'draw:custom-shape', \
		'draw:ellipse', \
		'draw:frame', \
		'draw:g', \
		'draw:line', \
		'draw:measure', \
		'draw:page-thumbnail', \
		'draw:path', \
		'draw:polygon', \
		'draw:polyline', \
		'draw:rect', \
		'draw:regular-polygon', \
		'table:table', \
		'text:alphabetical-index', \
		'text:bibliography', \
		'text:change', \
		'text:change-end', \
		'text:change-start', \
		'text:h', \
		'text:illustration-index', \
		'text:index-title', \
		'text:list', \
		'text:numbered-paragraph', \
		'text:object-index', \
		'text:p', \
		'text:section', \
		'text:soft-page-break', \
		'text:table-index', \
		'text:table-of-content', \
		'text:user-index', \
),
	'text:index-title-template' : ( \
),
	'text:initial-creator' : ( \
),
	'text:insertion' : ( \
		'office:change-info', \
),
	'text:keywords' : ( \
),
	'text:line-break' : ( \
),
	'text:linenumbering-configuration' : ( \
		'text:linenumbering-separator', \
),
	'text:linenumbering-separator' : ( \
),
	'text:list' : ( \
		'text:list-header', \
		'text:list-item', \
),
	'text:list-header' : ( \
		'text:h', \
		'text:list', \
		'text:number', \
		'text:p', \
		'text:soft-page-break', \
),
	'text:list-item' : ( \
		'text:h', \
		'text:list', \
		'text:number', \
		'text:p', \
		'text:soft-page-break', \
),
	'text:list-level-style-bullet' : ( \
		'style:list-level-properties', \
		'style:text-properties', \
),
	'text:list-level-style-image' : ( \
		'office:binary-data', \
		'style:list-level-properties', \
),
	'text:list-level-style-number' : ( \
		'style:list-level-properties', \
		'style:text-properties', \
),
	'text:list-style' : ( \
		'text:list-level-style-bullet', \
		'text:list-level-style-image', \
		'text:list-level-style-number', \
),
	'text:measure' : ( \
),
	'text:meta' : ( \
		'dr3d:scene', \
		'draw:a', \
		'draw:caption', \
		'draw:circle', \
		'draw:connector', \
		'draw:control', \
		'draw:custom-shape', \
		'draw:ellipse', \
		'draw:frame', \
		'draw:g', \
		'draw:line', \
		'draw:measure', \
		'draw:page-thumbnail', \
		'draw:path', \
		'draw:polygon', \
		'draw:polyline', \
		'draw:rect', \
		'draw:regular-polygon', \
		'office:annotation', \
		'office:annotation-end', \
		'presentation:date-time', \
		'presentation:footer', \
		'presentation:header', \
		'text:a', \
		'text:alphabetical-index-mark', \
		'text:alphabetical-index-mark-end', \
		'text:alphabetical-index-mark-start', \
		'text:author-initials', \
		'text:author-name', \
		'text:bibliography-mark', \
		'text:bookmark', \
		'text:bookmark-end', \
		'text:bookmark-ref', \
		'text:bookmark-start', \
		'text:change', \
		'text:change-end', \
		'text:change-start', \
		'text:chapter', \
		'text:character-count', \
		'text:conditional-text', \
		'text:creation-date', \
		'text:creation-time', \
		'text:creator', \
		'text:database-display', \
		'text:database-name', \
		'text:database-next', \
		'text:database-row-number', \
		'text:database-row-select', \
		'text:date', \
		'text:dde-connection', \
		'text:description', \
		'text:editing-cycles', \
		'text:editing-duration', \
		'text:execute-macro', \
		'text:expression', \
		'text:file-name', \
		'text:hidden-paragraph', \
		'text:hidden-text', \
		'text:image-count', \
		'text:initial-creator', \
		'text:keywords', \
		'text:line-break', \
		'text:measure', \
		'text:meta', \
		'text:meta-field', \
		'text:modification-date', \
		'text:modification-time', \
		'text:note', \
		'text:note-ref', \
		'text:object-count', \
		'text:page-continuation', \
		'text:page-count', \
		'text:page-number', \
		'text:page-variable-get', \
		'text:page-variable-set', \
		'text:paragraph-count', \
		'text:placeholder', \
		'text:print-date', \
		'text:print-time', \
		'text:printed-by', \
		'text:reference-mark', \
		'text:reference-mark-end', \
		'text:reference-mark-start', \
		'text:reference-ref', \
		'text:ruby', \
		'text:s', \
		'text:script', \
		'text:sender-city', \
		'text:sender-company', \
		'text:sender-country', \
		'text:sender-email', \
		'text:sender-fax', \
		'text:sender-firstname', \
		'text:sender-initials', \
		'text:sender-lastname', \
		'text:sender-phone-private', \
		'text:sender-phone-work', \
		'text:sender-position', \
		'text:sender-postal-code', \
		'text:sender-state-or-province', \
		'text:sender-street', \
		'text:sender-title', \
		'text:sequence', \
		'text:sequence-ref', \
		'text:sheet-name', \
		'text:soft-page-break', \
		'text:span', \
		'text:subject', \
		'text:tab', \
		'text:table-count', \
		'text:table-formula', \
		'text:template-name', \
		'text:text-input', \
		'text:time', \
		'text:title', \
		'text:toc-mark', \
		'text:toc-mark-end', \
		'text:toc-mark-start', \
		'text:user-defined', \
		'text:user-field-get', \
		'text:user-field-input', \
		'text:user-index-mark', \
		'text:user-index-mark-end', \
		'text:user-index-mark-start', \
		'text:variable-get', \
		'text:variable-input', \
		'text:variable-set', \
		'text:word-count', \
),
	'text:meta-field' : ( \
		'dr3d:scene', \
		'draw:a', \
		'draw:caption', \
		'draw:circle', \
		'draw:connector', \
		'draw:control', \
		'draw:custom-shape', \
		'draw:ellipse', \
		'draw:frame', \
		'draw:g', \
		'draw:line', \
		'draw:measure', \
		'draw:page-thumbnail', \
		'draw:path', \
		'draw:polygon', \
		'draw:polyline', \
		'draw:rect', \
		'draw:regular-polygon', \
		'office:annotation', \
		'office:annotation-end', \
		'presentation:date-time', \
		'presentation:footer', \
		'presentation:header', \
		'text:a', \
		'text:alphabetical-index-mark', \
		'text:alphabetical-index-mark-end', \
		'text:alphabetical-index-mark-start', \
		'text:author-initials', \
		'text:author-name', \
		'text:bibliography-mark', \
		'text:bookmark', \
		'text:bookmark-end', \
		'text:bookmark-ref', \
		'text:bookmark-start', \
		'text:change', \
		'text:change-end', \
		'text:change-start', \
		'text:chapter', \
		'text:character-count', \
		'text:conditional-text', \
		'text:creation-date', \
		'text:creation-time', \
		'text:creator', \
		'text:database-display', \
		'text:database-name', \
		'text:database-next', \
		'text:database-row-number', \
		'text:database-row-select', \
		'text:date', \
		'text:dde-connection', \
		'text:description', \
		'text:editing-cycles', \
		'text:editing-duration', \
		'text:execute-macro', \
		'text:expression', \
		'text:file-name', \
		'text:hidden-paragraph', \
		'text:hidden-text', \
		'text:image-count', \
		'text:initial-creator', \
		'text:keywords', \
		'text:line-break', \
		'text:measure', \
		'text:meta', \
		'text:meta-field', \
		'text:modification-date', \
		'text:modification-time', \
		'text:note', \
		'text:note-ref', \
		'text:object-count', \
		'text:page-continuation', \
		'text:page-count', \
		'text:page-number', \
		'text:page-variable-get', \
		'text:page-variable-set', \
		'text:paragraph-count', \
		'text:placeholder', \
		'text:print-date', \
		'text:print-time', \
		'text:printed-by', \
		'text:reference-mark', \
		'text:reference-mark-end', \
		'text:reference-mark-start', \
		'text:reference-ref', \
		'text:ruby', \
		'text:s', \
		'text:script', \
		'text:sender-city', \
		'text:sender-company', \
		'text:sender-country', \
		'text:sender-email', \
		'text:sender-fax', \
		'text:sender-firstname', \
		'text:sender-initials', \
		'text:sender-lastname', \
		'text:sender-phone-private', \
		'text:sender-phone-work', \
		'text:sender-position', \
		'text:sender-postal-code', \
		'text:sender-state-or-province', \
		'text:sender-street', \
		'text:sender-title', \
		'text:sequence', \
		'text:sequence-ref', \
		'text:sheet-name', \
		'text:soft-page-break', \
		'text:span', \
		'text:subject', \
		'text:tab', \
		'text:table-count', \
		'text:table-formula', \
		'text:template-name', \
		'text:text-input', \
		'text:time', \
		'text:title', \
		'text:toc-mark', \
		'text:toc-mark-end', \
		'text:toc-mark-start', \
		'text:user-defined', \
		'text:user-field-get', \
		'text:user-field-input', \
		'text:user-index-mark', \
		'text:user-index-mark-end', \
		'text:user-index-mark-start', \
		'text:variable-get', \
		'text:variable-input', \
		'text:variable-set', \
		'text:word-count', \
),
	'text:modification-date' : ( \
),
	'text:modification-time' : ( \
),
	'text:note' : ( \
		'text:note-body', \
		'text:note-citation', \
),
	'text:note-body' : ( \
		'dr3d:scene', \
		'draw:a', \
		'draw:caption', \
		'draw:circle', \
		'draw:connector', \
		'draw:control', \
		'draw:custom-shape', \
		'draw:ellipse', \
		'draw:frame', \
		'draw:g', \
		'draw:line', \
		'draw:measure', \
		'draw:page-thumbnail', \
		'draw:path', \
		'draw:polygon', \
		'draw:polyline', \
		'draw:rect', \
		'draw:regular-polygon', \
		'table:table', \
		'text:alphabetical-index', \
		'text:bibliography', \
		'text:change', \
		'text:change-end', \
		'text:change-start', \
		'text:h', \
		'text:illustration-index', \
		'text:list', \
		'text:numbered-paragraph', \
		'text:object-index', \
		'text:p', \
		'text:section', \
		'text:soft-page-break', \
		'text:table-index', \
		'text:table-of-content', \
		'text:user-index', \
),
	'text:note-citation' : ( \
),
	'text:note-continuation-notice-backward' : ( \
),
	'text:note-continuation-notice-forward' : ( \
),
	'text:note-ref' : ( \
),
	'text:notes-configuration' : ( \
		'text:note-continuation-notice-backward', \
		'text:note-continuation-notice-forward', \
),
	'text:number' : ( \
),
	'text:numbered-paragraph' : ( \
		'text:h', \
		'text:number', \
		'text:p', \
),
	'text:object-count' : ( \
),
	'text:object-index' : ( \
		'text:index-body', \
		'text:object-index-source', \
),
	'text:object-index-entry-template' : ( \
		'text:index-entry-chapter', \
		'text:index-entry-page-number', \
		'text:index-entry-span', \
		'text:index-entry-tab-stop', \
		'text:index-entry-text', \
),
	'text:object-index-source' : ( \
		'text:index-title-template', \
		'text:object-index-entry-template', \
),
	'text:outline-level-style' : ( \
		'style:list-level-properties', \
		'style:text-properties', \
),
	'text:outline-style' : ( \
		'text:outline-level-style', \
),
	'text:p' : ( \
		'dr3d:scene', \
		'draw:a', \
		'draw:caption', \
		'draw:circle', \
		'draw:connector', \
		'draw:control', \
		'draw:custom-shape', \
		'draw:ellipse', \
		'draw:frame', \
		'draw:g', \
		'draw:line', \
		'draw:measure', \
		'draw:page-thumbnail', \
		'draw:path', \
		'draw:polygon', \
		'draw:polyline', \
		'draw:rect', \
		'draw:regular-polygon', \
		'office:annotation', \
		'office:annotation-end', \
		'presentation:date-time', \
		'presentation:footer', \
		'presentation:header', \
		'text:a', \
		'text:alphabetical-index-mark', \
		'text:alphabetical-index-mark-end', \
		'text:alphabetical-index-mark-start', \
		'text:author-initials', \
		'text:author-name', \
		'text:bibliography-mark', \
		'text:bookmark', \
		'text:bookmark-end', \
		'text:bookmark-ref', \
		'text:bookmark-start', \
		'text:change', \
		'text:change-end', \
		'text:change-start', \
		'text:chapter', \
		'text:character-count', \
		'text:conditional-text', \
		'text:creation-date', \
		'text:creation-time', \
		'text:creator', \
		'text:database-display', \
		'text:database-name', \
		'text:database-next', \
		'text:database-row-number', \
		'text:database-row-select', \
		'text:date', \
		'text:dde-connection', \
		'text:description', \
		'text:editing-cycles', \
		'text:editing-duration', \
		'text:execute-macro', \
		'text:expression', \
		'text:file-name', \
		'text:hidden-paragraph', \
		'text:hidden-text', \
		'text:image-count', \
		'text:initial-creator', \
		'text:keywords', \
		'text:line-break', \
		'text:measure', \
		'text:meta', \
		'text:meta-field', \
		'text:modification-date', \
		'text:modification-time', \
		'text:note', \
		'text:note-ref', \
		'text:object-count', \
		'text:page-continuation', \
		'text:page-count', \
		'text:page-number', \
		'text:page-variable-get', \
		'text:page-variable-set', \
		'text:paragraph-count', \
		'text:placeholder', \
		'text:print-date', \
		'text:print-time', \
		'text:printed-by', \
		'text:reference-mark', \
		'text:reference-mark-end', \
		'text:reference-mark-start', \
		'text:reference-ref', \
		'text:ruby', \
		'text:s', \
		'text:script', \
		'text:sender-city', \
		'text:sender-company', \
		'text:sender-country', \
		'text:sender-email', \
		'text:sender-fax', \
		'text:sender-firstname', \
		'text:sender-initials', \
		'text:sender-lastname', \
		'text:sender-phone-private', \
		'text:sender-phone-work', \
		'text:sender-position', \
		'text:sender-postal-code', \
		'text:sender-state-or-province', \
		'text:sender-street', \
		'text:sender-title', \
		'text:sequence', \
		'text:sequence-ref', \
		'text:sheet-name', \
		'text:soft-page-break', \
		'text:span', \
		'text:subject', \
		'text:tab', \
		'text:table-count', \
		'text:table-formula', \
		'text:template-name', \
		'text:text-input', \
		'text:time', \
		'text:title', \
		'text:toc-mark', \
		'text:toc-mark-end', \
		'text:toc-mark-start', \
		'text:user-defined', \
		'text:user-field-get', \
		'text:user-field-input', \
		'text:user-index-mark', \
		'text:user-index-mark-end', \
		'text:user-index-mark-start', \
		'text:variable-get', \
		'text:variable-input', \
		'text:variable-set', \
		'text:word-count', \
),
	'text:page' : ( \
),
	'text:page-continuation' : ( \
),
	'text:page-count' : ( \
),
	'text:page-number' : ( \
),
	'text:page-sequence' : ( \
		'text:page', \
),
	'text:page-variable-get' : ( \
),
	'text:page-variable-set' : ( \
),
	'text:paragraph-count' : ( \
),
	'text:placeholder' : ( \
),
	'text:print-date' : ( \
),
	'text:print-time' : ( \
),
	'text:printed-by' : ( \
),
	'text:reference-mark' : ( \
),
	'text:reference-mark-end' : ( \
),
	'text:reference-mark-start' : ( \
),
	'text:reference-ref' : ( \
),
	'text:ruby' : ( \
		'text:ruby-base', \
		'text:ruby-text', \
),
	'text:ruby-base' : ( \
		'dr3d:scene', \
		'draw:a', \
		'draw:caption', \
		'draw:circle', \
		'draw:connector', \
		'draw:control', \
		'draw:custom-shape', \
		'draw:ellipse', \
		'draw:frame', \
		'draw:g', \
		'draw:line', \
		'draw:measure', \
		'draw:page-thumbnail', \
		'draw:path', \
		'draw:polygon', \
		'draw:polyline', \
		'draw:rect', \
		'draw:regular-polygon', \
		'office:annotation', \
		'office:annotation-end', \
		'presentation:date-time', \
		'presentation:footer', \
		'presentation:header', \
		'text:a', \
		'text:alphabetical-index-mark', \
		'text:alphabetical-index-mark-end', \
		'text:alphabetical-index-mark-start', \
		'text:author-initials', \
		'text:author-name', \
		'text:bibliography-mark', \
		'text:bookmark', \
		'text:bookmark-end', \
		'text:bookmark-ref', \
		'text:bookmark-start', \
		'text:change', \
		'text:change-end', \
		'text:change-start', \
		'text:chapter', \
		'text:character-count', \
		'text:conditional-text', \
		'text:creation-date', \
		'text:creation-time', \
		'text:creator', \
		'text:database-display', \
		'text:database-name', \
		'text:database-next', \
		'text:database-row-number', \
		'text:database-row-select', \
		'text:date', \
		'text:dde-connection', \
		'text:description', \
		'text:editing-cycles', \
		'text:editing-duration', \
		'text:execute-macro', \
		'text:expression', \
		'text:file-name', \
		'text:hidden-paragraph', \
		'text:hidden-text', \
		'text:image-count', \
		'text:initial-creator', \
		'text:keywords', \
		'text:line-break', \
		'text:measure', \
		'text:meta', \
		'text:meta-field', \
		'text:modification-date', \
		'text:modification-time', \
		'text:note', \
		'text:note-ref', \
		'text:object-count', \
		'text:page-continuation', \
		'text:page-count', \
		'text:page-number', \
		'text:page-variable-get', \
		'text:page-variable-set', \
		'text:paragraph-count', \
		'text:placeholder', \
		'text:print-date', \
		'text:print-time', \
		'text:printed-by', \
		'text:reference-mark', \
		'text:reference-mark-end', \
		'text:reference-mark-start', \
		'text:reference-ref', \
		'text:ruby', \
		'text:s', \
		'text:script', \
		'text:sender-city', \
		'text:sender-company', \
		'text:sender-country', \
		'text:sender-email', \
		'text:sender-fax', \
		'text:sender-firstname', \
		'text:sender-initials', \
		'text:sender-lastname', \
		'text:sender-phone-private', \
		'text:sender-phone-work', \
		'text:sender-position', \
		'text:sender-postal-code', \
		'text:sender-state-or-province', \
		'text:sender-street', \
		'text:sender-title', \
		'text:sequence', \
		'text:sequence-ref', \
		'text:sheet-name', \
		'text:soft-page-break', \
		'text:span', \
		'text:subject', \
		'text:tab', \
		'text:table-count', \
		'text:table-formula', \
		'text:template-name', \
		'text:text-input', \
		'text:time', \
		'text:title', \
		'text:toc-mark', \
		'text:toc-mark-end', \
		'text:toc-mark-start', \
		'text:user-defined', \
		'text:user-field-get', \
		'text:user-field-input', \
		'text:user-index-mark', \
		'text:user-index-mark-end', \
		'text:user-index-mark-start', \
		'text:variable-get', \
		'text:variable-input', \
		'text:variable-set', \
		'text:word-count', \
),
	'text:ruby-text' : ( \
),
	'text:s' : ( \
),
	'text:script' : ( \
),
	'text:section' : ( \
		'dr3d:scene', \
		'draw:a', \
		'draw:caption', \
		'draw:circle', \
		'draw:connector', \
		'draw:control', \
		'draw:custom-shape', \
		'draw:ellipse', \
		'draw:frame', \
		'draw:g', \
		'draw:line', \
		'draw:measure', \
		'draw:page-thumbnail', \
		'draw:path', \
		'draw:polygon', \
		'draw:polyline', \
		'draw:rect', \
		'draw:regular-polygon', \
		'office:dde-source', \
		'table:table', \
		'text:alphabetical-index', \
		'text:bibliography', \
		'text:change', \
		'text:change-end', \
		'text:change-start', \
		'text:h', \
		'text:illustration-index', \
		'text:list', \
		'text:numbered-paragraph', \
		'text:object-index', \
		'text:p', \
		'text:section', \
		'text:section-source', \
		'text:soft-page-break', \
		'text:table-index', \
		'text:table-of-content', \
		'text:user-index', \
),
	'text:section-source' : ( \
),
	'text:sender-city' : ( \
),
	'text:sender-company' : ( \
),
	'text:sender-country' : ( \
),
	'text:sender-email' : ( \
),
	'text:sender-fax' : ( \
),
	'text:sender-firstname' : ( \
),
	'text:sender-initials' : ( \
),
	'text:sender-lastname' : ( \
),
	'text:sender-phone-private' : ( \
),
	'text:sender-phone-work' : ( \
),
	'text:sender-position' : ( \
),
	'text:sender-postal-code' : ( \
),
	'text:sender-state-or-province' : ( \
),
	'text:sender-street' : ( \
),
	'text:sender-title' : ( \
),
	'text:sequence' : ( \
),
	'text:sequence-decl' : ( \
),
	'text:sequence-decls' : ( \
		'text:sequence-decl', \
),
	'text:sequence-ref' : ( \
),
	'text:sheet-name' : ( \
),
	'text:soft-page-break' : ( \
),
	'text:sort-key' : ( \
),
	'text:span' : ( \
		'dr3d:scene', \
		'draw:a', \
		'draw:caption', \
		'draw:circle', \
		'draw:connector', \
		'draw:control', \
		'draw:custom-shape', \
		'draw:ellipse', \
		'draw:frame', \
		'draw:g', \
		'draw:line', \
		'draw:measure', \
		'draw:page-thumbnail', \
		'draw:path', \
		'draw:polygon', \
		'draw:polyline', \
		'draw:rect', \
		'draw:regular-polygon', \
		'office:annotation', \
		'office:annotation-end', \
		'presentation:date-time', \
		'presentation:footer', \
		'presentation:header', \
		'text:a', \
		'text:alphabetical-index-mark', \
		'text:alphabetical-index-mark-end', \
		'text:alphabetical-index-mark-start', \
		'text:author-initials', \
		'text:author-name', \
		'text:bibliography-mark', \
		'text:bookmark', \
		'text:bookmark-end', \
		'text:bookmark-ref', \
		'text:bookmark-start', \
		'text:change', \
		'text:change-end', \
		'text:change-start', \
		'text:chapter', \
		'text:character-count', \
		'text:conditional-text', \
		'text:creation-date', \
		'text:creation-time', \
		'text:creator', \
		'text:database-display', \
		'text:database-name', \
		'text:database-next', \
		'text:database-row-number', \
		'text:database-row-select', \
		'text:date', \
		'text:dde-connection', \
		'text:description', \
		'text:editing-cycles', \
		'text:editing-duration', \
		'text:execute-macro', \
		'text:expression', \
		'text:file-name', \
		'text:hidden-paragraph', \
		'text:hidden-text', \
		'text:image-count', \
		'text:initial-creator', \
		'text:keywords', \
		'text:line-break', \
		'text:measure', \
		'text:meta', \
		'text:meta-field', \
		'text:modification-date', \
		'text:modification-time', \
		'text:note', \
		'text:note-ref', \
		'text:object-count', \
		'text:page-continuation', \
		'text:page-count', \
		'text:page-number', \
		'text:page-variable-get', \
		'text:page-variable-set', \
		'text:paragraph-count', \
		'text:placeholder', \
		'text:print-date', \
		'text:print-time', \
		'text:printed-by', \
		'text:reference-mark', \
		'text:reference-mark-end', \
		'text:reference-mark-start', \
		'text:reference-ref', \
		'text:ruby', \
		'text:s', \
		'text:script', \
		'text:sender-city', \
		'text:sender-company', \
		'text:sender-country', \
		'text:sender-email', \
		'text:sender-fax', \
		'text:sender-firstname', \
		'text:sender-initials', \
		'text:sender-lastname', \
		'text:sender-phone-private', \
		'text:sender-phone-work', \
		'text:sender-position', \
		'text:sender-postal-code', \
		'text:sender-state-or-province', \
		'text:sender-street', \
		'text:sender-title', \
		'text:sequence', \
		'text:sequence-ref', \
		'text:sheet-name', \
		'text:soft-page-break', \
		'text:span', \
		'text:subject', \
		'text:tab', \
		'text:table-count', \
		'text:table-formula', \
		'text:template-name', \
		'text:text-input', \
		'text:time', \
		'text:title', \
		'text:toc-mark', \
		'text:toc-mark-end', \
		'text:toc-mark-start', \
		'text:user-defined', \
		'text:user-field-get', \
		'text:user-field-input', \
		'text:user-index-mark', \
		'text:user-index-mark-end', \
		'text:user-index-mark-start', \
		'text:variable-get', \
		'text:variable-input', \
		'text:variable-set', \
		'text:word-count', \
),
	'text:subject' : ( \
),
	'text:tab' : ( \
),
	'text:table-count' : ( \
),
	'text:table-formula' : ( \
),
	'text:table-index' : ( \
		'text:index-body', \
		'text:table-index-source', \
),
	'text:table-index-entry-template' : ( \
		'text:index-entry-chapter', \
		'text:index-entry-page-number', \
		'text:index-entry-span', \
		'text:index-entry-tab-stop', \
		'text:index-entry-text', \
),
	'text:table-index-source' : ( \
		'text:index-title-template', \
		'text:table-index-entry-template', \
),
	'text:table-of-content' : ( \
		'text:index-body', \
		'text:table-of-content-source', \
),
	'text:table-of-content-entry-template' : ( \
		'text:index-entry-chapter', \
		'text:index-entry-link-end', \
		'text:index-entry-link-start', \
		'text:index-entry-page-number', \
		'text:index-entry-span', \
		'text:index-entry-tab-stop', \
		'text:index-entry-text', \
),
	'text:table-of-content-source' : ( \
		'text:index-source-styles', \
		'text:index-title-template', \
		'text:table-of-content-entry-template', \
),
	'text:template-name' : ( \
),
	'text:text-input' : ( \
),
	'text:time' : ( \
),
	'text:title' : ( \
),
	'text:toc-mark' : ( \
),
	'text:toc-mark-end' : ( \
),
	'text:toc-mark-start' : ( \
),
	'text:tracked-changes' : ( \
		'text:changed-region', \
),
	'text:user-defined' : ( \
),
	'text:user-field-decl' : ( \
),
	'text:user-field-decls' : ( \
		'text:user-field-decl', \
),
	'text:user-field-get' : ( \
),
	'text:user-field-input' : ( \
),
	'text:user-index' : ( \
		'text:index-body', \
		'text:user-index-source', \
),
	'text:user-index-entry-template' : ( \
		'text:index-entry-chapter', \
		'text:index-entry-page-number', \
		'text:index-entry-span', \
		'text:index-entry-tab-stop', \
		'text:index-entry-text', \
),
	'text:user-index-mark' : ( \
),
	'text:user-index-mark-end' : ( \
),
	'text:user-index-mark-start' : ( \
),
	'text:user-index-source' : ( \
		'text:index-source-styles', \
		'text:index-title-template', \
		'text:user-index-entry-template', \
),
	'text:variable-decl' : ( \
),
	'text:variable-decls' : ( \
		'text:variable-decl', \
),
	'text:variable-get' : ( \
),
	'text:variable-input' : ( \
),
	'text:variable-set' : ( \
),
	'text:word-count' : ( \
),
	'xforms:model' : ( \
		'*', \
),
}

CHILD_ATTRIBUTES = {
	'*' : ( \
		'*', \
),
	'*' : ( \
		'*', \
),
	'anim:animate' : ( \
		'anim:formula', \
		'anim:sub-item', \
		'smil:accelerate', \
		'smil:accumulate', \
		'smil:additive', \
		'smil:attributeName', \
		'smil:autoReverse', \
		'smil:begin', \
		'smil:by', \
		'smil:calcMode', \
		'smil:decelerate', \
		'smil:dur', \
		'smil:end', \
		'smil:fill', \
		'smil:fillDefault', \
		'smil:from', \
		'smil:keySplines', \
		'smil:keyTimes', \
		'smil:repeatCount', \
		'smil:repeatDur', \
		'smil:restart', \
		'smil:restartDefault', \
		'smil:targetElement', \
		'smil:to', \
		'smil:values', \
),
	'anim:animateColor' : ( \
		'anim:color-interpolation', \
		'anim:color-interpolation-direction', \
		'anim:formula', \
		'anim:sub-item', \
		'smil:accelerate', \
		'smil:accumulate', \
		'smil:additive', \
		'smil:attributeName', \
		'smil:autoReverse', \
		'smil:begin', \
		'smil:by', \
		'smil:calcMode', \
		'smil:decelerate', \
		'smil:dur', \
		'smil:end', \
		'smil:fill', \
		'smil:fillDefault', \
		'smil:from', \
		'smil:keySplines', \
		'smil:keyTimes', \
		'smil:repeatCount', \
		'smil:repeatDur', \
		'smil:restart', \
		'smil:restartDefault', \
		'smil:targetElement', \
		'smil:to', \
		'smil:values', \
),
	'anim:animateMotion' : ( \
		'anim:formula', \
		'anim:sub-item', \
		'smil:accelerate', \
		'smil:accumulate', \
		'smil:additive', \
		'smil:attributeName', \
		'smil:autoReverse', \
		'smil:begin', \
		'smil:by', \
		'smil:calcMode', \
		'smil:decelerate', \
		'smil:dur', \
		'smil:end', \
		'smil:fill', \
		'smil:fillDefault', \
		'smil:from', \
		'smil:keySplines', \
		'smil:keyTimes', \
		'smil:repeatCount', \
		'smil:repeatDur', \
		'smil:restart', \
		'smil:restartDefault', \
		'smil:targetElement', \
		'smil:to', \
		'smil:values', \
		'svg:origin', \
		'svg:path', \
),
	'anim:animateTransform' : ( \
		'anim:formula', \
		'anim:sub-item', \
		'smil:accelerate', \
		'smil:accumulate', \
		'smil:additive', \
		'smil:attributeName', \
		'smil:autoReverse', \
		'smil:begin', \
		'smil:by', \
		'smil:decelerate', \
		'smil:dur', \
		'smil:end', \
		'smil:fill', \
		'smil:fillDefault', \
		'smil:from', \
		'smil:repeatCount', \
		'smil:repeatDur', \
		'smil:restart', \
		'smil:restartDefault', \
		'smil:targetElement', \
		'smil:to', \
		'smil:values', \
		'svg:type', \
),
	'anim:audio' : ( \
		'anim:audio-level', \
		'anim:id', \
		'presentation:group-id', \
		'presentation:master-element', \
		'presentation:node-type', \
		'presentation:preset-class', \
		'presentation:preset-id', \
		'presentation:preset-sub-type', \
		'smil:begin', \
		'smil:dur', \
		'smil:end', \
		'smil:fill', \
		'smil:fillDefault', \
		'smil:repeatCount', \
		'smil:repeatDur', \
		'smil:restart', \
		'smil:restartDefault', \
		'xlink:href', \
		'xml:id', \
),
	'anim:command' : ( \
		'anim:command', \
		'anim:id', \
		'anim:sub-item', \
		'presentation:group-id', \
		'presentation:master-element', \
		'presentation:node-type', \
		'presentation:preset-class', \
		'presentation:preset-id', \
		'presentation:preset-sub-type', \
		'smil:begin', \
		'smil:end', \
		'smil:targetElement', \
		'xml:id', \
),
	'anim:iterate' : ( \
		'anim:id', \
		'anim:iterate-interval', \
		'anim:iterate-type', \
		'anim:sub-item', \
		'presentation:group-id', \
		'presentation:master-element', \
		'presentation:node-type', \
		'presentation:preset-class', \
		'presentation:preset-id', \
		'presentation:preset-sub-type', \
		'smil:accelerate', \
		'smil:autoReverse', \
		'smil:begin', \
		'smil:decelerate', \
		'smil:dur', \
		'smil:end', \
		'smil:endsync', \
		'smil:fill', \
		'smil:fillDefault', \
		'smil:repeatCount', \
		'smil:repeatDur', \
		'smil:restart', \
		'smil:restartDefault', \
		'smil:targetElement', \
		'xml:id', \
),
	'anim:par' : ( \
		'anim:id', \
		'presentation:group-id', \
		'presentation:master-element', \
		'presentation:node-type', \
		'presentation:preset-class', \
		'presentation:preset-id', \
		'presentation:preset-sub-type', \
		'smil:accelerate', \
		'smil:autoReverse', \
		'smil:begin', \
		'smil:decelerate', \
		'smil:dur', \
		'smil:end', \
		'smil:endsync', \
		'smil:fill', \
		'smil:fillDefault', \
		'smil:repeatCount', \
		'smil:repeatDur', \
		'smil:restart', \
		'smil:restartDefault', \
		'xml:id', \
),
	'anim:param' : ( \
		'anim:name', \
		'anim:value', \
),
	'anim:seq' : ( \
		'anim:id', \
		'presentation:group-id', \
		'presentation:master-element', \
		'presentation:node-type', \
		'presentation:preset-class', \
		'presentation:preset-id', \
		'presentation:preset-sub-type', \
		'smil:accelerate', \
		'smil:autoReverse', \
		'smil:begin', \
		'smil:decelerate', \
		'smil:dur', \
		'smil:end', \
		'smil:endsync', \
		'smil:fill', \
		'smil:fillDefault', \
		'smil:repeatCount', \
		'smil:repeatDur', \
		'smil:restart', \
		'smil:restartDefault', \
		'xml:id', \
),
	'anim:set' : ( \
		'anim:sub-item', \
		'smil:accelerate', \
		'smil:accumulate', \
		'smil:additive', \
		'smil:attributeName', \
		'smil:autoReverse', \
		'smil:begin', \
		'smil:decelerate', \
		'smil:dur', \
		'smil:end', \
		'smil:fill', \
		'smil:fillDefault', \
		'smil:repeatCount', \
		'smil:repeatDur', \
		'smil:restart', \
		'smil:restartDefault', \
		'smil:targetElement', \
		'smil:to', \
),
	'anim:transitionFilter' : ( \
		'anim:formula', \
		'anim:sub-item', \
		'smil:accelerate', \
		'smil:accumulate', \
		'smil:additive', \
		'smil:autoReverse', \
		'smil:begin', \
		'smil:by', \
		'smil:calcMode', \
		'smil:decelerate', \
		'smil:direction', \
		'smil:dur', \
		'smil:end', \
		'smil:fadeColor', \
		'smil:fill', \
		'smil:fillDefault', \
		'smil:from', \
		'smil:mode', \
		'smil:repeatCount', \
		'smil:repeatDur', \
		'smil:restart', \
		'smil:restartDefault', \
		'smil:subtype', \
		'smil:targetElement', \
		'smil:to', \
		'smil:type', \
		'smil:values', \
),
	'chart:axis' : ( \
		'chart:dimension', \
		'chart:name', \
		'chart:style-name', \
),
	'chart:categories' : ( \
		'table:cell-range-address', \
),
	'chart:chart' : ( \
		'chart:class', \
		'chart:column-mapping', \
		'chart:row-mapping', \
		'chart:style-name', \
		'svg:height', \
		'svg:width', \
		'xlink:href', \
		'xlink:type', \
		'xml:id', \
),
	'chart:data-label' : ( \
		'chart:style-name', \
		'svg:x', \
		'svg:y', \
),
	'chart:data-point' : ( \
		'chart:repeated', \
		'chart:style-name', \
		'xml:id', \
),
	'chart:domain' : ( \
		'table:cell-range-address', \
),
	'chart:equation' : ( \
		'chart:automatic-content', \
		'chart:display-equation', \
		'chart:display-r-square', \
		'chart:style-name', \
		'svg:x', \
		'svg:y', \
),
	'chart:error-indicator' : ( \
		'chart:dimension', \
		'chart:style-name', \
),
	'chart:floor' : ( \
		'chart:style-name', \
		'svg:width', \
),
	'chart:footer' : ( \
		'chart:style-name', \
		'svg:x', \
		'svg:y', \
		'table:cell-range', \
),
	'chart:grid' : ( \
		'chart:class', \
		'chart:style-name', \
),
	'chart:label-separator' : ( \
),
	'chart:legend' : ( \
		'chart:legend-align', \
		'chart:legend-position', \
		'chart:legend-position', \
		'chart:style-name', \
		'style:legend-expansion', \
		'style:legend-expansion', \
		'style:legend-expansion-aspect-ratio', \
		'svg:x', \
		'svg:y', \
),
	'chart:mean-value' : ( \
		'chart:style-name', \
),
	'chart:plot-area' : ( \
		'chart:data-source-has-labels', \
		'chart:style-name', \
		'dr3d:ambient-color', \
		'dr3d:distance', \
		'dr3d:focal-length', \
		'dr3d:lighting-mode', \
		'dr3d:projection', \
		'dr3d:shade-mode', \
		'dr3d:shadow-slant', \
		'dr3d:transform', \
		'dr3d:vpn', \
		'dr3d:vrp', \
		'dr3d:vup', \
		'svg:height', \
		'svg:width', \
		'svg:x', \
		'svg:y', \
		'table:cell-range-address', \
		'xml:id', \
),
	'chart:regression-curve' : ( \
		'chart:style-name', \
),
	'chart:series' : ( \
		'chart:attached-axis', \
		'chart:class', \
		'chart:label-cell-address', \
		'chart:style-name', \
		'chart:values-cell-range-address', \
		'xml:id', \
),
	'chart:stock-gain-marker' : ( \
		'chart:style-name', \
),
	'chart:stock-loss-marker' : ( \
		'chart:style-name', \
),
	'chart:stock-range-line' : ( \
		'chart:style-name', \
),
	'chart:subtitle' : ( \
		'chart:style-name', \
		'svg:x', \
		'svg:y', \
		'table:cell-range', \
),
	'chart:symbol-image' : ( \
		'xlink:href', \
),
	'chart:title' : ( \
		'chart:style-name', \
		'svg:x', \
		'svg:y', \
		'table:cell-range', \
),
	'chart:wall' : ( \
		'chart:style-name', \
		'svg:width', \
),
	'config:config-item' : ( \
		'config:name', \
		'config:type', \
),
	'config:config-item-map-entry' : ( \
		'config:name', \
),
	'config:config-item-map-indexed' : ( \
		'config:name', \
),
	'config:config-item-map-named' : ( \
		'config:name', \
),
	'config:config-item-set' : ( \
		'config:name', \
),
	'db:application-connection-settings' : ( \
		'db:append-table-alias-name', \
		'db:boolean-comparison-mode', \
		'db:enable-sql92-check', \
		'db:ignore-driver-privileges', \
		'db:is-table-name-length-limited', \
		'db:max-row-count', \
		'db:suppress-version-columns', \
		'db:use-catalog', \
),
	'db:auto-increment' : ( \
		'db:additional-column-statement', \
		'db:row-retrieving-statement', \
),
	'db:character-set' : ( \
		'db:encoding', \
),
	'db:column' : ( \
		'db:default-cell-style-name', \
		'db:description', \
		'db:name', \
		'db:style-name', \
		'db:title', \
		'db:visible', \
		'office:boolean-value', \
		'office:currency', \
		'office:date-value', \
		'office:string-value', \
		'office:time-value', \
		'office:value', \
		'office:value-type', \
		'office:value-type', \
		'office:value-type', \
		'office:value-type', \
		'office:value-type', \
		'office:value-type', \
		'office:value-type', \
),
	'db:column-definition' : ( \
		'db:data-type', \
		'db:is-autoincrement', \
		'db:is-empty-allowed', \
		'db:is-nullable', \
		'db:name', \
		'db:precision', \
		'db:scale', \
		'db:type-name', \
		'office:boolean-value', \
		'office:currency', \
		'office:date-value', \
		'office:string-value', \
		'office:time-value', \
		'office:value', \
		'office:value-type', \
		'office:value-type', \
		'office:value-type', \
		'office:value-type', \
		'office:value-type', \
		'office:value-type', \
		'office:value-type', \
),
	'db:column-definitions' : ( \
),
	'db:columns' : ( \
),
	'db:component' : ( \
		'db:as-template', \
		'db:description', \
		'db:name', \
		'db:title', \
		'xlink:actuate', \
		'xlink:href', \
		'xlink:show', \
		'xlink:type', \
),
	'db:component-collection' : ( \
		'db:description', \
		'db:name', \
		'db:title', \
),
	'db:connection-data' : ( \
),
	'db:connection-resource' : ( \
		'xlink:actuate', \
		'xlink:href', \
		'xlink:show', \
		'xlink:type', \
),
	'db:data-source' : ( \
),
	'db:data-source-setting' : ( \
		'db:data-source-setting-is-list', \
		'db:data-source-setting-name', \
		'db:data-source-setting-type', \
),
	'db:data-source-setting-value' : ( \
),
	'db:data-source-settings' : ( \
),
	'db:database-description' : ( \
),
	'db:delimiter' : ( \
		'db:decimal', \
		'db:field', \
		'db:string', \
		'db:thousand', \
),
	'db:driver-settings' : ( \
		'db:base-dn', \
		'db:is-first-row-header-line', \
		'db:parameter-name-substitution', \
		'db:show-deleted', \
		'db:system-driver-settings', \
),
	'db:file-based-database' : ( \
		'db:extension', \
		'db:media-type', \
		'xlink:href', \
		'xlink:type', \
),
	'db:filter-statement' : ( \
		'db:apply-command', \
		'db:command', \
),
	'db:forms' : ( \
),
	'db:index' : ( \
		'db:catalog-name', \
		'db:is-clustered', \
		'db:is-unique', \
		'db:name', \
),
	'db:index-column' : ( \
		'db:is-ascending', \
		'db:name', \
),
	'db:index-columns' : ( \
),
	'db:indices' : ( \
),
	'db:key' : ( \
		'db:delete-rule', \
		'db:name', \
		'db:referenced-table-name', \
		'db:type', \
		'db:update-rule', \
),
	'db:key-column' : ( \
		'db:name', \
		'db:related-column-name', \
),
	'db:key-columns' : ( \
),
	'db:keys' : ( \
),
	'db:login' : ( \
		'db:is-password-required', \
		'db:login-timeout', \
		'db:use-system-user', \
		'db:user-name', \
),
	'db:order-statement' : ( \
		'db:apply-command', \
		'db:command', \
),
	'db:queries' : ( \
),
	'db:query' : ( \
		'db:command', \
		'db:default-row-style-name', \
		'db:description', \
		'db:escape-processing', \
		'db:name', \
		'db:style-name', \
		'db:title', \
),
	'db:query-collection' : ( \
		'db:description', \
		'db:name', \
		'db:title', \
),
	'db:reports' : ( \
),
	'db:schema-definition' : ( \
),
	'db:server-database' : ( \
		'db:database-name', \
		'db:hostname', \
		'db:local-socket', \
		'db:port', \
		'db:type', \
),
	'db:table-definition' : ( \
		'db:catalog-name', \
		'db:name', \
		'db:schema-name', \
		'db:type', \
),
	'db:table-definitions' : ( \
),
	'db:table-exclude-filter' : ( \
),
	'db:table-filter' : ( \
),
	'db:table-filter-pattern' : ( \
),
	'db:table-include-filter' : ( \
),
	'db:table-representation' : ( \
		'db:catalog-name', \
		'db:default-row-style-name', \
		'db:description', \
		'db:name', \
		'db:schema-name', \
		'db:style-name', \
		'db:title', \
),
	'db:table-representations' : ( \
),
	'db:table-setting' : ( \
		'db:is-first-row-header-line', \
		'db:show-deleted', \
),
	'db:table-settings' : ( \
),
	'db:table-type' : ( \
),
	'db:table-type-filter' : ( \
),
	'db:update-table' : ( \
		'db:catalog-name', \
		'db:name', \
		'db:schema-name', \
),
	'dc:creator' : ( \
),
	'dc:date' : ( \
),
	'dc:description' : ( \
),
	'dc:language' : ( \
),
	'dc:subject' : ( \
),
	'dc:title' : ( \
),
	'dr3d:cube' : ( \
		'dr3d:max-edge', \
		'dr3d:min-edge', \
		'dr3d:transform', \
		'draw:class-names', \
		'draw:id', \
		'draw:layer', \
		'draw:style-name', \
		'draw:z-index', \
		'presentation:class-names', \
		'presentation:style-name', \
		'xml:id', \
),
	'dr3d:extrude' : ( \
		'dr3d:transform', \
		'draw:class-names', \
		'draw:id', \
		'draw:layer', \
		'draw:style-name', \
		'draw:z-index', \
		'presentation:class-names', \
		'presentation:style-name', \
		'svg:d', \
		'svg:viewBox', \
		'xml:id', \
),
	'dr3d:light' : ( \
		'dr3d:diffuse-color', \
		'dr3d:direction', \
		'dr3d:enabled', \
		'dr3d:specular', \
),
	'dr3d:rotate' : ( \
		'dr3d:transform', \
		'draw:class-names', \
		'draw:id', \
		'draw:layer', \
		'draw:style-name', \
		'draw:z-index', \
		'presentation:class-names', \
		'presentation:style-name', \
		'svg:d', \
		'svg:viewBox', \
		'xml:id', \
),
	'dr3d:scene' : ( \
		'dr3d:ambient-color', \
		'dr3d:distance', \
		'dr3d:focal-length', \
		'dr3d:lighting-mode', \
		'dr3d:projection', \
		'dr3d:shade-mode', \
		'dr3d:shadow-slant', \
		'dr3d:transform', \
		'dr3d:vpn', \
		'dr3d:vrp', \
		'dr3d:vup', \
		'draw:caption-id', \
		'draw:class-names', \
		'draw:id', \
		'draw:layer', \
		'draw:style-name', \
		'draw:z-index', \
		'presentation:class-names', \
		'presentation:style-name', \
		'svg:height', \
		'svg:width', \
		'svg:x', \
		'svg:y', \
		'table:end-cell-address', \
		'table:end-x', \
		'table:end-y', \
		'table:table-background', \
		'text:anchor-page-number', \
		'text:anchor-type', \
		'xml:id', \
),
	'dr3d:sphere' : ( \
		'dr3d:center', \
		'dr3d:size', \
		'dr3d:transform', \
		'draw:class-names', \
		'draw:id', \
		'draw:layer', \
		'draw:style-name', \
		'draw:z-index', \
		'presentation:class-names', \
		'presentation:style-name', \
		'xml:id', \
),
	'draw:a' : ( \
		'office:name', \
		'office:server-map', \
		'office:target-frame-name', \
		'office:title', \
		'xlink:actuate', \
		'xlink:href', \
		'xlink:show', \
		'xlink:type', \
		'xml:id', \
),
	'draw:applet' : ( \
		'draw:archive', \
		'draw:code', \
		'draw:may-script', \
		'draw:object', \
		'xlink:actuate', \
		'xlink:href', \
		'xlink:show', \
		'xlink:type', \
		'xml:id', \
),
	'draw:area-circle' : ( \
		'draw:nohref', \
		'office:name', \
		'office:target-frame-name', \
		'svg:cx', \
		'svg:cy', \
		'svg:r', \
		'xlink:href', \
		'xlink:show', \
		'xlink:type', \
),
	'draw:area-polygon' : ( \
		'draw:nohref', \
		'draw:points', \
		'office:name', \
		'office:target-frame-name', \
		'svg:height', \
		'svg:viewBox', \
		'svg:width', \
		'svg:x', \
		'svg:y', \
		'xlink:href', \
		'xlink:show', \
		'xlink:type', \
),
	'draw:area-rectangle' : ( \
		'draw:nohref', \
		'office:name', \
		'office:target-frame-name', \
		'svg:height', \
		'svg:width', \
		'svg:x', \
		'svg:y', \
		'xlink:href', \
		'xlink:show', \
		'xlink:type', \
),
	'draw:caption' : ( \
		'draw:caption-id', \
		'draw:caption-point-x', \
		'draw:caption-point-y', \
		'draw:class-names', \
		'draw:corner-radius', \
		'draw:id', \
		'draw:layer', \
		'draw:name', \
		'draw:style-name', \
		'draw:text-style-name', \
		'draw:transform', \
		'draw:z-index', \
		'presentation:class-names', \
		'presentation:style-name', \
		'svg:height', \
		'svg:width', \
		'svg:x', \
		'svg:y', \
		'table:end-cell-address', \
		'table:end-x', \
		'table:end-y', \
		'table:table-background', \
		'text:anchor-page-number', \
		'text:anchor-type', \
		'xml:id', \
),
	'draw:circle' : ( \
		'draw:caption-id', \
		'draw:class-names', \
		'draw:end-angle', \
		'draw:id', \
		'draw:kind', \
		'draw:layer', \
		'draw:name', \
		'draw:start-angle', \
		'draw:style-name', \
		'draw:text-style-name', \
		'draw:transform', \
		'draw:z-index', \
		'presentation:class-names', \
		'presentation:style-name', \
		'svg:cx', \
		'svg:cy', \
		'svg:height', \
		'svg:r', \
		'svg:width', \
		'svg:x', \
		'svg:y', \
		'table:end-cell-address', \
		'table:end-x', \
		'table:end-y', \
		'table:table-background', \
		'text:anchor-page-number', \
		'text:anchor-type', \
		'xml:id', \
),
	'draw:connector' : ( \
		'draw:caption-id', \
		'draw:class-names', \
		'draw:end-glue-point', \
		'draw:end-shape', \
		'draw:id', \
		'draw:layer', \
		'draw:line-skew', \
		'draw:name', \
		'draw:start-glue-point', \
		'draw:start-shape', \
		'draw:style-name', \
		'draw:text-style-name', \
		'draw:transform', \
		'draw:type', \
		'draw:z-index', \
		'presentation:class-names', \
		'presentation:style-name', \
		'svg:d', \
		'svg:viewBox', \
		'svg:x1', \
		'svg:x2', \
		'svg:y1', \
		'svg:y2', \
		'table:end-cell-address', \
		'table:end-x', \
		'table:end-y', \
		'table:table-background', \
		'text:anchor-page-number', \
		'text:anchor-type', \
		'xml:id', \
),
	'draw:contour-path' : ( \
		'draw:recreate-on-edit', \
		'svg:d', \
		'svg:height', \
		'svg:viewBox', \
		'svg:width', \
),
	'draw:contour-polygon' : ( \
		'draw:points', \
		'draw:recreate-on-edit', \
		'svg:height', \
		'svg:viewBox', \
		'svg:width', \
),
	'draw:control' : ( \
		'draw:caption-id', \
		'draw:class-names', \
		'draw:control', \
		'draw:id', \
		'draw:layer', \
		'draw:name', \
		'draw:style-name', \
		'draw:text-style-name', \
		'draw:transform', \
		'draw:z-index', \
		'presentation:class-names', \
		'presentation:style-name', \
		'svg:height', \
		'svg:width', \
		'svg:x', \
		'svg:y', \
		'table:end-cell-address', \
		'table:end-x', \
		'table:end-y', \
		'table:table-background', \
		'text:anchor-page-number', \
		'text:anchor-type', \
		'xml:id', \
),
	'draw:custom-shape' : ( \
		'draw:caption-id', \
		'draw:class-names', \
		'draw:data', \
		'draw:engine', \
		'draw:id', \
		'draw:layer', \
		'draw:name', \
		'draw:style-name', \
		'draw:text-style-name', \
		'draw:transform', \
		'draw:z-index', \
		'presentation:class-names', \
		'presentation:style-name', \
		'svg:height', \
		'svg:width', \
		'svg:x', \
		'svg:y', \
		'table:end-cell-address', \
		'table:end-x', \
		'table:end-y', \
		'table:table-background', \
		'text:anchor-page-number', \
		'text:anchor-type', \
		'xml:id', \
),
	'draw:ellipse' : ( \
		'draw:caption-id', \
		'draw:class-names', \
		'draw:end-angle', \
		'draw:id', \
		'draw:kind', \
		'draw:layer', \
		'draw:name', \
		'draw:start-angle', \
		'draw:style-name', \
		'draw:text-style-name', \
		'draw:transform', \
		'draw:z-index', \
		'presentation:class-names', \
		'presentation:style-name', \
		'svg:cx', \
		'svg:cy', \
		'svg:height', \
		'svg:rx', \
		'svg:ry', \
		'svg:width', \
		'svg:x', \
		'svg:y', \
		'table:end-cell-address', \
		'table:end-x', \
		'table:end-y', \
		'table:table-background', \
		'text:anchor-page-number', \
		'text:anchor-type', \
		'xml:id', \
),
	'draw:enhanced-geometry' : ( \
		'dr3d:projection', \
		'dr3d:shade-mode', \
		'draw:concentric-gradient-fill-allowed', \
		'draw:enhanced-path', \
		'draw:extrusion', \
		'draw:extrusion-allowed', \
		'draw:extrusion-brightness', \
		'draw:extrusion-color', \
		'draw:extrusion-depth', \
		'draw:extrusion-diffusion', \
		'draw:extrusion-first-light-direction', \
		'draw:extrusion-first-light-harsh', \
		'draw:extrusion-first-light-level', \
		'draw:extrusion-light-face', \
		'draw:extrusion-metal', \
		'draw:extrusion-number-of-line-segments', \
		'draw:extrusion-origin', \
		'draw:extrusion-rotation-angle', \
		'draw:extrusion-rotation-center', \
		'draw:extrusion-second-light-direction', \
		'draw:extrusion-second-light-harsh', \
		'draw:extrusion-second-light-level', \
		'draw:extrusion-shininess', \
		'draw:extrusion-skew', \
		'draw:extrusion-specularity', \
		'draw:extrusion-viewpoint', \
		'draw:glue-point-leaving-directions', \
		'draw:glue-point-type', \
		'draw:glue-points', \
		'draw:mirror-horizontal', \
		'draw:mirror-vertical', \
		'draw:modifiers', \
		'draw:path-stretchpoint-x', \
		'draw:path-stretchpoint-y', \
		'draw:text-areas', \
		'draw:text-path', \
		'draw:text-path-allowed', \
		'draw:text-path-mode', \
		'draw:text-path-same-letter-heights', \
		'draw:text-path-scale', \
		'draw:text-rotate-angle', \
		'draw:type', \
		'svg:viewBox', \
),
	'draw:equation' : ( \
		'draw:formula', \
		'draw:name', \
),
	'draw:fill-image' : ( \
		'draw:display-name', \
		'draw:name', \
		'svg:height', \
		'svg:width', \
		'xlink:actuate', \
		'xlink:href', \
		'xlink:show', \
		'xlink:type', \
),
	'draw:floating-frame' : ( \
		'draw:frame-name', \
		'xlink:actuate', \
		'xlink:href', \
		'xlink:show', \
		'xlink:type', \
		'xml:id', \
),
	'draw:frame' : ( \
		'draw:caption-id', \
		'draw:class-names', \
		'draw:copy-of', \
		'draw:id', \
		'draw:layer', \
		'draw:name', \
		'draw:style-name', \
		'draw:text-style-name', \
		'draw:transform', \
		'draw:z-index', \
		'presentation:class', \
		'presentation:class-names', \
		'presentation:placeholder', \
		'presentation:style-name', \
		'presentation:user-transformed', \
		'style:rel-height', \
		'style:rel-width', \
		'svg:height', \
		'svg:width', \
		'svg:x', \
		'svg:y', \
		'table:end-cell-address', \
		'table:end-x', \
		'table:end-y', \
		'table:table-background', \
		'text:anchor-page-number', \
		'text:anchor-type', \
		'xml:id', \
),
	'draw:g' : ( \
		'draw:caption-id', \
		'draw:class-names', \
		'draw:id', \
		'draw:name', \
		'draw:style-name', \
		'draw:z-index', \
		'presentation:class-names', \
		'presentation:style-name', \
		'svg:y', \
		'table:end-cell-address', \
		'table:end-x', \
		'table:end-y', \
		'table:table-background', \
		'text:anchor-page-number', \
		'text:anchor-type', \
		'xml:id', \
),
	'draw:glue-point' : ( \
		'draw:align', \
		'draw:escape-direction', \
		'draw:id', \
		'svg:x', \
		'svg:y', \
),
	'draw:gradient' : ( \
		'draw:angle', \
		'draw:border', \
		'draw:cx', \
		'draw:cy', \
		'draw:display-name', \
		'draw:end-color', \
		'draw:end-intensity', \
		'draw:name', \
		'draw:start-color', \
		'draw:start-intensity', \
		'draw:style', \
),
	'draw:handle' : ( \
		'draw:handle-mirror-horizontal', \
		'draw:handle-mirror-vertical', \
		'draw:handle-polar', \
		'draw:handle-position', \
		'draw:handle-radius-range-maximum', \
		'draw:handle-radius-range-minimum', \
		'draw:handle-range-x-maximum', \
		'draw:handle-range-x-minimum', \
		'draw:handle-range-y-maximum', \
		'draw:handle-range-y-minimum', \
		'draw:handle-switched', \
),
	'draw:hatch' : ( \
		'draw:color', \
		'draw:display-name', \
		'draw:distance', \
		'draw:name', \
		'draw:rotation', \
		'draw:style', \
),
	'draw:image' : ( \
		'draw:filter-name', \
		'xlink:actuate', \
		'xlink:href', \
		'xlink:show', \
		'xlink:type', \
		'xml:id', \
),
	'draw:image-map' : ( \
),
	'draw:layer' : ( \
		'draw:display', \
		'draw:name', \
		'draw:protected', \
),
	'draw:layer-set' : ( \
),
	'draw:line' : ( \
		'draw:caption-id', \
		'draw:class-names', \
		'draw:id', \
		'draw:layer', \
		'draw:name', \
		'draw:style-name', \
		'draw:text-style-name', \
		'draw:transform', \
		'draw:z-index', \
		'presentation:class-names', \
		'presentation:style-name', \
		'svg:x1', \
		'svg:x2', \
		'svg:y1', \
		'svg:y2', \
		'table:end-cell-address', \
		'table:end-x', \
		'table:end-y', \
		'table:table-background', \
		'text:anchor-page-number', \
		'text:anchor-type', \
		'xml:id', \
),
	'draw:marker' : ( \
		'draw:display-name', \
		'draw:name', \
		'svg:d', \
		'svg:viewBox', \
),
	'draw:measure' : ( \
		'draw:caption-id', \
		'draw:class-names', \
		'draw:id', \
		'draw:layer', \
		'draw:name', \
		'draw:style-name', \
		'draw:text-style-name', \
		'draw:transform', \
		'draw:z-index', \
		'presentation:class-names', \
		'presentation:style-name', \
		'svg:x1', \
		'svg:x2', \
		'svg:y1', \
		'svg:y2', \
		'table:end-cell-address', \
		'table:end-x', \
		'table:end-y', \
		'table:table-background', \
		'text:anchor-page-number', \
		'text:anchor-type', \
		'xml:id', \
),
	'draw:object' : ( \
		'draw:notify-on-update-of-ranges', \
		'xlink:actuate', \
		'xlink:href', \
		'xlink:show', \
		'xlink:type', \
		'xml:id', \
),
	'draw:object-ole' : ( \
		'draw:class-id', \
		'xlink:actuate', \
		'xlink:href', \
		'xlink:show', \
		'xlink:type', \
		'xml:id', \
),
	'draw:opacity' : ( \
		'draw:angle', \
		'draw:border', \
		'draw:cx', \
		'draw:cy', \
		'draw:display-name', \
		'draw:end', \
		'draw:name', \
		'draw:start', \
		'draw:style', \
),
	'draw:page' : ( \
		'draw:id', \
		'draw:master-page-name', \
		'draw:name', \
		'draw:nav-order', \
		'draw:style-name', \
		'presentation:presentation-page-layout-name', \
		'presentation:use-date-time-name', \
		'presentation:use-footer-name', \
		'presentation:use-header-name', \
		'xml:id', \
),
	'draw:page-thumbnail' : ( \
		'draw:caption-id', \
		'draw:class-names', \
		'draw:id', \
		'draw:layer', \
		'draw:name', \
		'draw:page-number', \
		'draw:style-name', \
		'draw:transform', \
		'draw:z-index', \
		'presentation:class', \
		'presentation:class-names', \
		'presentation:placeholder', \
		'presentation:style-name', \
		'presentation:user-transformed', \
		'svg:height', \
		'svg:width', \
		'svg:x', \
		'svg:y', \
		'table:end-cell-address', \
		'table:end-x', \
		'table:end-y', \
		'table:table-background', \
		'text:anchor-page-number', \
		'text:anchor-type', \
		'xml:id', \
),
	'draw:param' : ( \
		'draw:name', \
		'draw:value', \
),
	'draw:path' : ( \
		'draw:caption-id', \
		'draw:class-names', \
		'draw:id', \
		'draw:layer', \
		'draw:name', \
		'draw:style-name', \
		'draw:text-style-name', \
		'draw:transform', \
		'draw:z-index', \
		'presentation:class-names', \
		'presentation:style-name', \
		'svg:d', \
		'svg:height', \
		'svg:viewBox', \
		'svg:width', \
		'svg:x', \
		'svg:y', \
		'table:end-cell-address', \
		'table:end-x', \
		'table:end-y', \
		'table:table-background', \
		'text:anchor-page-number', \
		'text:anchor-type', \
		'xml:id', \
),
	'draw:plugin' : ( \
		'draw:mime-type', \
		'xlink:actuate', \
		'xlink:href', \
		'xlink:show', \
		'xlink:type', \
		'xml:id', \
),
	'draw:polygon' : ( \
		'draw:caption-id', \
		'draw:class-names', \
		'draw:id', \
		'draw:layer', \
		'draw:name', \
		'draw:points', \
		'draw:style-name', \
		'draw:text-style-name', \
		'draw:transform', \
		'draw:z-index', \
		'presentation:class-names', \
		'presentation:style-name', \
		'svg:height', \
		'svg:viewBox', \
		'svg:width', \
		'svg:x', \
		'svg:y', \
		'table:end-cell-address', \
		'table:end-x', \
		'table:end-y', \
		'table:table-background', \
		'text:anchor-page-number', \
		'text:anchor-type', \
		'xml:id', \
),
	'draw:polyline' : ( \
		'draw:caption-id', \
		'draw:class-names', \
		'draw:id', \
		'draw:layer', \
		'draw:name', \
		'draw:points', \
		'draw:style-name', \
		'draw:text-style-name', \
		'draw:transform', \
		'draw:z-index', \
		'presentation:class-names', \
		'presentation:style-name', \
		'svg:height', \
		'svg:viewBox', \
		'svg:width', \
		'svg:x', \
		'svg:y', \
		'table:end-cell-address', \
		'table:end-x', \
		'table:end-y', \
		'table:table-background', \
		'text:anchor-page-number', \
		'text:anchor-type', \
		'xml:id', \
),
	'draw:rect' : ( \
		'draw:caption-id', \
		'draw:class-names', \
		'draw:corner-radius', \
		'draw:id', \
		'draw:layer', \
		'draw:name', \
		'draw:style-name', \
		'draw:text-style-name', \
		'draw:transform', \
		'draw:z-index', \
		'presentation:class-names', \
		'presentation:style-name', \
		'svg:height', \
		'svg:rx', \
		'svg:ry', \
		'svg:width', \
		'svg:x', \
		'svg:y', \
		'table:end-cell-address', \
		'table:end-x', \
		'table:end-y', \
		'table:table-background', \
		'text:anchor-page-number', \
		'text:anchor-type', \
		'xml:id', \
),
	'draw:regular-polygon' : ( \
		'draw:caption-id', \
		'draw:class-names', \
		'draw:concave', \
		'draw:concave', \
		'draw:corners', \
		'draw:id', \
		'draw:layer', \
		'draw:name', \
		'draw:sharpness', \
		'draw:style-name', \
		'draw:text-style-name', \
		'draw:transform', \
		'draw:z-index', \
		'presentation:class-names', \
		'presentation:style-name', \
		'svg:height', \
		'svg:width', \
		'svg:x', \
		'svg:y', \
		'table:end-cell-address', \
		'table:end-x', \
		'table:end-y', \
		'table:table-background', \
		'text:anchor-page-number', \
		'text:anchor-type', \
		'xml:id', \
),
	'draw:stroke-dash' : ( \
		'draw:display-name', \
		'draw:distance', \
		'draw:dots1', \
		'draw:dots1-length', \
		'draw:dots2', \
		'draw:dots2-length', \
		'draw:name', \
		'draw:style', \
),
	'draw:text-box' : ( \
		'draw:chain-next-name', \
		'draw:corner-radius', \
		'fo:max-height', \
		'fo:max-width', \
		'fo:min-height', \
		'fo:min-width', \
		'text:id', \
		'xml:id', \
),
	'form:button' : ( \
		'form:button-type', \
		'form:control-implementation', \
		'form:default-button', \
		'form:delay-for-repeat', \
		'form:disabled', \
		'form:focus-on-click', \
		'form:id', \
		'form:image-align', \
		'form:image-data', \
		'form:image-position', \
		'form:image-position', \
		'form:label', \
		'form:name', \
		'form:printable', \
		'form:repeat', \
		'form:tab-index', \
		'form:tab-stop', \
		'form:title', \
		'form:toggle', \
		'form:value', \
		'form:xforms-submission', \
		'office:target-frame', \
		'xforms:bind', \
		'xlink:href', \
		'xml:id', \
),
	'form:checkbox' : ( \
		'form:control-implementation', \
		'form:current-state', \
		'form:data-field', \
		'form:disabled', \
		'form:id', \
		'form:image-align', \
		'form:image-position', \
		'form:image-position', \
		'form:is-tristate', \
		'form:label', \
		'form:linked-cell', \
		'form:name', \
		'form:printable', \
		'form:state', \
		'form:tab-index', \
		'form:tab-stop', \
		'form:title', \
		'form:value', \
		'form:visual-effect', \
		'xforms:bind', \
		'xml:id', \
),
	'form:column' : ( \
		'form:control-implementation', \
		'form:label', \
		'form:name', \
		'form:text-style-name', \
),
	'form:combobox' : ( \
		'form:auto-complete', \
		'form:control-implementation', \
		'form:convert-empty-to-null', \
		'form:current-value', \
		'form:data-field', \
		'form:disabled', \
		'form:dropdown', \
		'form:id', \
		'form:linked-cell', \
		'form:list-source', \
		'form:list-source-type', \
		'form:max-length', \
		'form:name', \
		'form:printable', \
		'form:readonly', \
		'form:size', \
		'form:source-cell-range', \
		'form:tab-index', \
		'form:tab-stop', \
		'form:title', \
		'form:value', \
		'xforms:bind', \
		'xml:id', \
),
	'form:connection-resource' : ( \
		'xlink:href', \
),
	'form:date' : ( \
		'form:control-implementation', \
		'form:convert-empty-to-null', \
		'form:current-value', \
		'form:data-field', \
		'form:delay-for-repeat', \
		'form:disabled', \
		'form:id', \
		'form:linked-cell', \
		'form:max-length', \
		'form:max-value', \
		'form:min-value', \
		'form:name', \
		'form:printable', \
		'form:readonly', \
		'form:repeat', \
		'form:spin-button', \
		'form:tab-index', \
		'form:tab-stop', \
		'form:title', \
		'form:value', \
		'xforms:bind', \
		'xml:id', \
),
	'form:file' : ( \
		'form:control-implementation', \
		'form:current-value', \
		'form:disabled', \
		'form:id', \
		'form:linked-cell', \
		'form:max-length', \
		'form:name', \
		'form:printable', \
		'form:readonly', \
		'form:tab-index', \
		'form:tab-stop', \
		'form:title', \
		'form:value', \
		'xforms:bind', \
		'xml:id', \
),
	'form:fixed-text' : ( \
		'form:control-implementation', \
		'form:disabled', \
		'form:for', \
		'form:id', \
		'form:label', \
		'form:multi-line', \
		'form:name', \
		'form:printable', \
		'form:title', \
		'xforms:bind', \
		'xml:id', \
),
	'form:form' : ( \
		'form:allow-deletes', \
		'form:allow-inserts', \
		'form:allow-updates', \
		'form:apply-filter', \
		'form:command', \
		'form:command-type', \
		'form:control-implementation', \
		'form:datasource', \
		'form:detail-fields', \
		'form:enctype', \
		'form:escape-processing', \
		'form:filter', \
		'form:ignore-result', \
		'form:master-fields', \
		'form:method', \
		'form:name', \
		'form:navigation-mode', \
		'form:order', \
		'form:tab-cycle', \
		'office:target-frame', \
		'xlink:actuate', \
		'xlink:href', \
		'xlink:type', \
),
	'form:formatted-text' : ( \
		'form:control-implementation', \
		'form:convert-empty-to-null', \
		'form:current-value', \
		'form:data-field', \
		'form:delay-for-repeat', \
		'form:disabled', \
		'form:id', \
		'form:linked-cell', \
		'form:max-length', \
		'form:max-value', \
		'form:min-value', \
		'form:name', \
		'form:printable', \
		'form:readonly', \
		'form:repeat', \
		'form:spin-button', \
		'form:tab-index', \
		'form:tab-stop', \
		'form:title', \
		'form:validation', \
		'form:value', \
		'xforms:bind', \
		'xml:id', \
),
	'form:frame' : ( \
		'form:control-implementation', \
		'form:disabled', \
		'form:for', \
		'form:id', \
		'form:label', \
		'form:name', \
		'form:printable', \
		'form:title', \
		'xforms:bind', \
		'xml:id', \
),
	'form:generic-control' : ( \
		'form:control-implementation', \
		'form:id', \
		'form:name', \
		'xforms:bind', \
		'xml:id', \
),
	'form:grid' : ( \
		'form:control-implementation', \
		'form:disabled', \
		'form:id', \
		'form:name', \
		'form:printable', \
		'form:tab-index', \
		'form:tab-stop', \
		'form:title', \
		'xforms:bind', \
		'xml:id', \
),
	'form:hidden' : ( \
		'form:control-implementation', \
		'form:id', \
		'form:name', \
		'form:value', \
		'xforms:bind', \
		'xml:id', \
),
	'form:image' : ( \
		'form:button-type', \
		'form:control-implementation', \
		'form:disabled', \
		'form:id', \
		'form:image-data', \
		'form:name', \
		'form:printable', \
		'form:tab-index', \
		'form:tab-stop', \
		'form:title', \
		'form:value', \
		'office:target-frame', \
		'xforms:bind', \
		'xlink:href', \
		'xml:id', \
),
	'form:image-frame' : ( \
		'form:control-implementation', \
		'form:data-field', \
		'form:disabled', \
		'form:id', \
		'form:image-data', \
		'form:name', \
		'form:printable', \
		'form:readonly', \
		'form:title', \
		'xforms:bind', \
		'xml:id', \
),
	'form:item' : ( \
		'form:label', \
),
	'form:list-property' : ( \
		'form:property-name', \
		'office:value-type', \
		'office:value-type', \
		'office:value-type', \
		'office:value-type', \
		'office:value-type', \
		'office:value-type', \
		'office:value-type', \
		'office:value-type', \
),
	'form:list-value' : ( \
		'office:boolean-value', \
),
	'form:list-value' : ( \
		'office:value', \
),
	'form:list-value' : ( \
		'office:currency', \
		'office:value', \
),
	'form:list-value' : ( \
		'office:date-value', \
),
	'form:list-value' : ( \
		'office:time-value', \
),
	'form:list-value' : ( \
		'office:string-value', \
),
	'form:list-value' : ( \
		'office:value', \
),
	'form:listbox' : ( \
		'form:bound-column', \
		'form:control-implementation', \
		'form:data-field', \
		'form:disabled', \
		'form:dropdown', \
		'form:id', \
		'form:linked-cell', \
		'form:list-linkage-type', \
		'form:list-source', \
		'form:list-source-type', \
		'form:multiple', \
		'form:name', \
		'form:printable', \
		'form:size', \
		'form:source-cell-range', \
		'form:tab-index', \
		'form:tab-stop', \
		'form:title', \
		'form:xforms-list-source', \
		'xforms:bind', \
		'xml:id', \
),
	'form:number' : ( \
		'form:control-implementation', \
		'form:convert-empty-to-null', \
		'form:current-value', \
		'form:data-field', \
		'form:delay-for-repeat', \
		'form:disabled', \
		'form:id', \
		'form:linked-cell', \
		'form:max-length', \
		'form:max-value', \
		'form:min-value', \
		'form:name', \
		'form:printable', \
		'form:readonly', \
		'form:repeat', \
		'form:spin-button', \
		'form:tab-index', \
		'form:tab-stop', \
		'form:title', \
		'form:value', \
		'xforms:bind', \
		'xml:id', \
),
	'form:option' : ( \
		'form:current-selected', \
		'form:label', \
		'form:selected', \
		'form:value', \
),
	'form:password' : ( \
		'form:control-implementation', \
		'form:convert-empty-to-null', \
		'form:disabled', \
		'form:echo-char', \
		'form:id', \
		'form:linked-cell', \
		'form:max-length', \
		'form:name', \
		'form:printable', \
		'form:tab-index', \
		'form:tab-stop', \
		'form:title', \
		'form:value', \
		'xforms:bind', \
		'xml:id', \
),
	'form:properties' : ( \
),
	'form:property' : ( \
		'form:property-name', \
		'office:boolean-value', \
		'office:currency', \
		'office:date-value', \
		'office:string-value', \
		'office:time-value', \
		'office:value', \
		'office:value-type', \
		'office:value-type', \
		'office:value-type', \
		'office:value-type', \
		'office:value-type', \
		'office:value-type', \
		'office:value-type', \
		'office:value-type', \
),
	'form:radio' : ( \
		'form:control-implementation', \
		'form:current-selected', \
		'form:data-field', \
		'form:disabled', \
		'form:id', \
		'form:image-align', \
		'form:image-position', \
		'form:image-position', \
		'form:label', \
		'form:linked-cell', \
		'form:name', \
		'form:printable', \
		'form:selected', \
		'form:tab-index', \
		'form:tab-stop', \
		'form:title', \
		'form:value', \
		'form:visual-effect', \
		'xforms:bind', \
		'xml:id', \
),
	'form:text' : ( \
		'form:control-implementation', \
		'form:convert-empty-to-null', \
		'form:current-value', \
		'form:data-field', \
		'form:disabled', \
		'form:id', \
		'form:linked-cell', \
		'form:max-length', \
		'form:name', \
		'form:printable', \
		'form:readonly', \
		'form:tab-index', \
		'form:tab-stop', \
		'form:title', \
		'form:value', \
		'xforms:bind', \
		'xml:id', \
),
	'form:textarea' : ( \
		'form:control-implementation', \
		'form:convert-empty-to-null', \
		'form:current-value', \
		'form:data-field', \
		'form:disabled', \
		'form:id', \
		'form:linked-cell', \
		'form:max-length', \
		'form:name', \
		'form:printable', \
		'form:readonly', \
		'form:tab-index', \
		'form:tab-stop', \
		'form:title', \
		'form:value', \
		'xforms:bind', \
		'xml:id', \
),
	'form:time' : ( \
		'form:control-implementation', \
		'form:convert-empty-to-null', \
		'form:current-value', \
		'form:data-field', \
		'form:delay-for-repeat', \
		'form:disabled', \
		'form:id', \
		'form:linked-cell', \
		'form:max-length', \
		'form:max-value', \
		'form:min-value', \
		'form:name', \
		'form:printable', \
		'form:readonly', \
		'form:repeat', \
		'form:spin-button', \
		'form:tab-index', \
		'form:tab-stop', \
		'form:title', \
		'form:value', \
		'xforms:bind', \
		'xml:id', \
),
	'form:value-range' : ( \
		'form:control-implementation', \
		'form:delay-for-repeat', \
		'form:disabled', \
		'form:id', \
		'form:linked-cell', \
		'form:max-value', \
		'form:min-value', \
		'form:name', \
		'form:orientation', \
		'form:page-step-size', \
		'form:printable', \
		'form:repeat', \
		'form:step-size', \
		'form:tab-index', \
		'form:tab-stop', \
		'form:title', \
		'form:value', \
		'xforms:bind', \
		'xml:id', \
),
	'math:math' : ( \
		'*', \
),
	'meta:auto-reload' : ( \
		'meta:delay', \
		'xlink:actuate', \
		'xlink:href', \
		'xlink:show', \
		'xlink:type', \
),
	'meta:creation-date' : ( \
),
	'meta:date-string' : ( \
),
	'meta:document-statistic' : ( \
		'meta:cell-count', \
		'meta:character-count', \
		'meta:draw-count', \
		'meta:frame-count', \
		'meta:image-count', \
		'meta:non-whitespace-character-count', \
		'meta:object-count', \
		'meta:ole-object-count', \
		'meta:page-count', \
		'meta:paragraph-count', \
		'meta:row-count', \
		'meta:sentence-count', \
		'meta:syllable-count', \
		'meta:table-count', \
		'meta:word-count', \
),
	'meta:editing-cycles' : ( \
),
	'meta:editing-duration' : ( \
),
	'meta:generator' : ( \
),
	'meta:hyperlink-behaviour' : ( \
		'office:target-frame-name', \
		'xlink:show', \
),
	'meta:initial-creator' : ( \
),
	'meta:keyword' : ( \
),
	'meta:print-date' : ( \
),
	'meta:printed-by' : ( \
),
	'meta:template' : ( \
		'meta:date', \
		'xlink:actuate', \
		'xlink:href', \
		'xlink:title', \
		'xlink:type', \
),
	'meta:user-defined' : ( \
		'meta:name', \
		'meta:value-type', \
		'meta:value-type', \
		'meta:value-type', \
		'meta:value-type', \
		'meta:value-type', \
),
	'number:am-pm' : ( \
),
	'number:boolean' : ( \
),
	'number:boolean-style' : ( \
		'number:country', \
		'number:language', \
		'number:rfc-language-tag', \
		'number:script', \
		'number:title', \
		'number:transliteration-country', \
		'number:transliteration-format', \
		'number:transliteration-language', \
		'number:transliteration-style', \
		'style:display-name', \
		'style:name', \
		'style:volatile', \
),
	'number:currency-style' : ( \
		'number:automatic-order', \
		'number:country', \
		'number:language', \
		'number:rfc-language-tag', \
		'number:script', \
		'number:title', \
		'number:transliteration-country', \
		'number:transliteration-format', \
		'number:transliteration-language', \
		'number:transliteration-style', \
		'style:display-name', \
		'style:name', \
		'style:volatile', \
),
	'number:currency-symbol' : ( \
		'number:country', \
		'number:language', \
		'number:rfc-language-tag', \
		'number:script', \
),
	'number:date-style' : ( \
		'number:automatic-order', \
		'number:country', \
		'number:format-source', \
		'number:language', \
		'number:rfc-language-tag', \
		'number:script', \
		'number:title', \
		'number:transliteration-country', \
		'number:transliteration-format', \
		'number:transliteration-language', \
		'number:transliteration-style', \
		'style:display-name', \
		'style:name', \
		'style:volatile', \
),
	'number:day' : ( \
		'number:calendar', \
		'number:style', \
),
	'number:day-of-week' : ( \
		'number:calendar', \
		'number:style', \
),
	'number:embedded-text' : ( \
		'number:position', \
),
	'number:era' : ( \
		'number:calendar', \
		'number:style', \
),
	'number:fraction' : ( \
		'number:denominator-value', \
		'number:grouping', \
		'number:min-denominator-digits', \
		'number:min-integer-digits', \
		'number:min-numerator-digits', \
),
	'number:hours' : ( \
		'number:style', \
),
	'number:minutes' : ( \
		'number:style', \
),
	'number:month' : ( \
		'number:calendar', \
		'number:possessive-form', \
		'number:style', \
		'number:textual', \
),
	'number:number' : ( \
		'number:decimal-places', \
		'number:decimal-replacement', \
		'number:display-factor', \
		'number:grouping', \
		'number:min-integer-digits', \
),
	'number:number-style' : ( \
		'number:country', \
		'number:language', \
		'number:rfc-language-tag', \
		'number:script', \
		'number:title', \
		'number:transliteration-country', \
		'number:transliteration-format', \
		'number:transliteration-language', \
		'number:transliteration-style', \
		'style:display-name', \
		'style:name', \
		'style:volatile', \
),
	'number:percentage-style' : ( \
		'number:country', \
		'number:language', \
		'number:rfc-language-tag', \
		'number:script', \
		'number:title', \
		'number:transliteration-country', \
		'number:transliteration-format', \
		'number:transliteration-language', \
		'number:transliteration-style', \
		'style:display-name', \
		'style:name', \
		'style:volatile', \
),
	'number:quarter' : ( \
		'number:calendar', \
		'number:style', \
),
	'number:scientific-number' : ( \
		'number:decimal-places', \
		'number:grouping', \
		'number:min-exponent-digits', \
		'number:min-integer-digits', \
),
	'number:seconds' : ( \
		'number:decimal-places', \
		'number:style', \
),
	'number:text' : ( \
),
	'number:text-content' : ( \
),
	'number:text-style' : ( \
		'number:country', \
		'number:language', \
		'number:rfc-language-tag', \
		'number:script', \
		'number:title', \
		'number:transliteration-country', \
		'number:transliteration-format', \
		'number:transliteration-language', \
		'number:transliteration-style', \
		'style:display-name', \
		'style:name', \
		'style:volatile', \
),
	'number:time-style' : ( \
		'number:country', \
		'number:format-source', \
		'number:language', \
		'number:rfc-language-tag', \
		'number:script', \
		'number:title', \
		'number:transliteration-country', \
		'number:transliteration-format', \
		'number:transliteration-language', \
		'number:transliteration-style', \
		'number:truncate-on-overflow', \
		'style:display-name', \
		'style:name', \
		'style:volatile', \
),
	'number:week-of-year' : ( \
		'number:calendar', \
),
	'number:year' : ( \
		'number:calendar', \
		'number:style', \
),
	'office:annotation' : ( \
		'draw:caption-point-x', \
		'draw:caption-point-y', \
		'draw:class-names', \
		'draw:corner-radius', \
		'draw:id', \
		'draw:layer', \
		'draw:name', \
		'draw:style-name', \
		'draw:text-style-name', \
		'draw:transform', \
		'draw:z-index', \
		'office:display', \
		'office:name', \
		'presentation:class-names', \
		'presentation:style-name', \
		'svg:height', \
		'svg:width', \
		'svg:x', \
		'svg:y', \
		'table:end-cell-address', \
		'table:end-x', \
		'table:end-y', \
		'table:table-background', \
		'text:anchor-page-number', \
		'text:anchor-type', \
		'xml:id', \
),
	'office:annotation-end' : ( \
		'office:name', \
),
	'office:automatic-styles' : ( \
),
	'office:binary-data' : ( \
),
	'office:body' : ( \
),
	'office:change-info' : ( \
),
	'office:chart' : ( \
),
	'office:database' : ( \
),
	'office:dde-source' : ( \
		'office:automatic-update', \
		'office:conversion-mode', \
		'office:dde-application', \
		'office:dde-item', \
		'office:dde-topic', \
		'office:name', \
),
	'office:document' : ( \
		'grddl:transformation', \
		'office:mimetype', \
		'office:version', \
),
	'office:document-content' : ( \
		'grddl:transformation', \
		'office:version', \
),
	'office:document-meta' : ( \
		'grddl:transformation', \
		'office:version', \
),
	'office:document-settings' : ( \
		'grddl:transformation', \
		'office:version', \
),
	'office:document-styles' : ( \
		'grddl:transformation', \
		'office:version', \
),
	'office:drawing' : ( \
),
	'office:event-listeners' : ( \
),
	'office:font-face-decls' : ( \
),
	'office:forms' : ( \
		'form:apply-design-mode', \
		'form:automatic-focus', \
),
	'office:image' : ( \
),
	'office:master-styles' : ( \
),
	'office:meta' : ( \
),
	'office:presentation' : ( \
),
	'office:script' : ( \
		'script:language', \
),
	'office:scripts' : ( \
),
	'office:settings' : ( \
),
	'office:spreadsheet' : ( \
		'table:protection-key', \
		'table:protection-key-digest-algorithm', \
		'table:structure-protected', \
),
	'office:styles' : ( \
),
	'office:text' : ( \
		'text:global', \
		'text:use-soft-page-breaks', \
),
	'presentation:animation-group' : ( \
),
	'presentation:animations' : ( \
),
	'presentation:date-time' : ( \
),
	'presentation:date-time-decl' : ( \
		'presentation:name', \
		'presentation:source', \
		'style:data-style-name', \
),
	'presentation:dim' : ( \
		'draw:color', \
		'draw:shape-id', \
),
	'presentation:event-listener' : ( \
		'presentation:action', \
		'presentation:direction', \
		'presentation:effect', \
		'presentation:speed', \
		'presentation:start-scale', \
		'presentation:verb', \
		'script:event-name', \
		'xlink:actuate', \
		'xlink:href', \
		'xlink:show', \
		'xlink:type', \
),
	'presentation:footer' : ( \
),
	'presentation:footer-decl' : ( \
		'presentation:name', \
),
	'presentation:header' : ( \
),
	'presentation:header-decl' : ( \
		'presentation:name', \
),
	'presentation:hide-shape' : ( \
		'draw:shape-id', \
		'presentation:delay', \
		'presentation:direction', \
		'presentation:effect', \
		'presentation:path-id', \
		'presentation:speed', \
		'presentation:start-scale', \
),
	'presentation:hide-text' : ( \
		'draw:shape-id', \
		'presentation:delay', \
		'presentation:direction', \
		'presentation:effect', \
		'presentation:path-id', \
		'presentation:speed', \
		'presentation:start-scale', \
),
	'presentation:notes' : ( \
		'draw:style-name', \
		'presentation:use-date-time-name', \
		'presentation:use-footer-name', \
		'presentation:use-header-name', \
		'style:page-layout-name', \
),
	'presentation:placeholder' : ( \
		'presentation:object', \
		'svg:height', \
		'svg:width', \
		'svg:x', \
		'svg:y', \
),
	'presentation:play' : ( \
		'draw:shape-id', \
		'presentation:speed', \
),
	'presentation:settings' : ( \
		'presentation:animations', \
		'presentation:endless', \
		'presentation:force-manual', \
		'presentation:full-screen', \
		'presentation:mouse-as-pen', \
		'presentation:mouse-visible', \
		'presentation:pause', \
		'presentation:show', \
		'presentation:show-end-of-presentation-slide', \
		'presentation:show-logo', \
		'presentation:start-page', \
		'presentation:start-with-navigator', \
		'presentation:stay-on-top', \
		'presentation:transition-on-click', \
),
	'presentation:show' : ( \
		'presentation:name', \
		'presentation:pages', \
),
	'presentation:show-shape' : ( \
		'draw:shape-id', \
		'presentation:delay', \
		'presentation:direction', \
		'presentation:effect', \
		'presentation:path-id', \
		'presentation:speed', \
		'presentation:start-scale', \
),
	'presentation:show-text' : ( \
		'draw:shape-id', \
		'presentation:delay', \
		'presentation:direction', \
		'presentation:effect', \
		'presentation:path-id', \
		'presentation:speed', \
		'presentation:start-scale', \
),
	'presentation:sound' : ( \
		'presentation:play-full', \
		'xlink:actuate', \
		'xlink:href', \
		'xlink:show', \
		'xlink:type', \
		'xml:id', \
),
	'script:event-listener' : ( \
		'script:event-name', \
		'script:language', \
		'script:macro-name', \
		'xlink:actuate', \
		'xlink:href', \
		'xlink:type', \
),
	'style:background-image' : ( \
		'draw:opacity', \
		'style:filter-name', \
		'style:position', \
		'style:repeat', \
		'xlink:actuate', \
		'xlink:href', \
		'xlink:show', \
		'xlink:type', \
),
	'style:chart-properties' : ( \
		'chart:angle-offset', \
		'chart:auto-position', \
		'chart:auto-size', \
		'chart:axis-label-position', \
		'chart:axis-position', \
		'chart:connect-bars', \
		'chart:data-label-number', \
		'chart:data-label-symbol', \
		'chart:data-label-text', \
		'chart:deep', \
		'chart:display-label', \
		'chart:error-category', \
		'chart:error-lower-indicator', \
		'chart:error-lower-limit', \
		'chart:error-lower-range', \
		'chart:error-margin', \
		'chart:error-percentage', \
		'chart:error-upper-indicator', \
		'chart:error-upper-limit', \
		'chart:error-upper-range', \
		'chart:gap-width', \
		'chart:group-bars-per-axis', \
		'chart:hole-size', \
		'chart:include-hidden-cells', \
		'chart:interpolation', \
		'chart:interval-major', \
		'chart:interval-minor-divisor', \
		'chart:japanese-candle-stick', \
		'chart:label-arrangement', \
		'chart:label-position', \
		'chart:label-position-negative', \
		'chart:lines', \
		'chart:link-data-style-to-source', \
		'chart:logarithmic', \
		'chart:maximum', \
		'chart:mean-value', \
		'chart:minimum', \
		'chart:origin', \
		'chart:overlap', \
		'chart:percentage', \
		'chart:pie-offset', \
		'chart:regression-type', \
		'chart:reverse-direction', \
		'chart:right-angled-axes', \
		'chart:scale-text', \
		'chart:series-source', \
		'chart:solid-type', \
		'chart:sort-by-x-values', \
		'chart:spline-order', \
		'chart:spline-resolution', \
		'chart:stacked', \
		'chart:symbol-height', \
		'chart:symbol-name', \
		'chart:symbol-type', \
		'chart:symbol-type', \
		'chart:symbol-type', \
		'chart:symbol-type', \
		'chart:symbol-width', \
		'chart:text-overlap', \
		'chart:three-dimensional', \
		'chart:tick-mark-position', \
		'chart:tick-marks-major-inner', \
		'chart:tick-marks-major-outer', \
		'chart:tick-marks-minor-inner', \
		'chart:tick-marks-minor-outer', \
		'chart:treat-empty-cells', \
		'chart:vertical', \
		'chart:visible', \
		'style:direction', \
		'style:rotation-angle', \
		'text:line-break', \
),
	'style:column' : ( \
		'fo:end-indent', \
		'fo:space-after', \
		'fo:space-before', \
		'fo:start-indent', \
		'style:rel-width', \
),
	'style:column-sep' : ( \
		'style:color', \
		'style:height', \
		'style:style', \
		'style:vertical-align', \
		'style:width', \
),
	'style:columns' : ( \
		'fo:column-count', \
		'fo:column-gap', \
),
	'style:default-page-layout' : ( \
),
	'style:default-style' : ( \
		'style:family', \
		'style:family', \
		'style:family', \
		'style:family', \
		'style:family', \
		'style:family', \
		'style:family', \
		'style:family', \
		'style:family', \
		'style:family', \
		'style:family', \
),
	'style:drawing-page-properties' : ( \
		'draw:background-size', \
		'draw:fill', \
		'draw:fill-color', \
		'draw:fill-gradient-name', \
		'draw:fill-hatch-name', \
		'draw:fill-hatch-solid', \
		'draw:fill-image-height', \
		'draw:fill-image-name', \
		'draw:fill-image-ref-point', \
		'draw:fill-image-ref-point-x', \
		'draw:fill-image-ref-point-y', \
		'draw:fill-image-width', \
		'draw:gradient-step-count', \
		'draw:opacity', \
		'draw:opacity-name', \
		'draw:secondary-fill-color', \
		'draw:tile-repeat-offset', \
		'presentation:background-objects-visible', \
		'presentation:background-visible', \
		'presentation:display-date-time', \
		'presentation:display-footer', \
		'presentation:display-header', \
		'presentation:display-page-number', \
		'presentation:duration', \
		'presentation:transition-speed', \
		'presentation:transition-style', \
		'presentation:transition-type', \
		'presentation:visibility', \
		'smil:direction', \
		'smil:fadeColor', \
		'smil:subtype', \
		'smil:type', \
		'style:repeat', \
		'svg:fill-rule', \
),
	'style:drop-cap' : ( \
		'style:distance', \
		'style:length', \
		'style:lines', \
		'style:style-name', \
),
	'style:font-face' : ( \
		'style:font-adornments', \
		'style:font-charset', \
		'style:font-family-generic', \
		'style:font-pitch', \
		'style:name', \
		'svg:accent-height', \
		'svg:alphabetic', \
		'svg:ascent', \
		'svg:bbox', \
		'svg:cap-height', \
		'svg:descent', \
		'svg:font-family', \
		'svg:font-size', \
		'svg:font-stretch', \
		'svg:font-style', \
		'svg:font-variant', \
		'svg:font-weight', \
		'svg:hanging', \
		'svg:ideographic', \
		'svg:mathematical', \
		'svg:overline-position', \
		'svg:overline-thickness', \
		'svg:panose-1', \
		'svg:slope', \
		'svg:stemh', \
		'svg:stemv', \
		'svg:strikethrough-position', \
		'svg:strikethrough-thickness', \
		'svg:underline-position', \
		'svg:underline-thickness', \
		'svg:unicode-range', \
		'svg:units-per-em', \
		'svg:v-alphabetic', \
		'svg:v-hanging', \
		'svg:v-ideographic', \
		'svg:v-mathematical', \
		'svg:widths', \
		'svg:x-height', \
),
	'style:footer' : ( \
		'style:display', \
),
	'style:footer-left' : ( \
		'style:display', \
),
	'style:footer-style' : ( \
),
	'style:footnote-sep' : ( \
		'style:adjustment', \
		'style:color', \
		'style:distance-after-sep', \
		'style:distance-before-sep', \
		'style:line-style', \
		'style:rel-width', \
		'style:width', \
),
	'style:graphic-properties' : ( \
		'dr3d:ambient-color', \
		'dr3d:back-scale', \
		'dr3d:backface-culling', \
		'dr3d:close-back', \
		'dr3d:close-front', \
		'dr3d:depth', \
		'dr3d:diffuse-color', \
		'dr3d:edge-rounding', \
		'dr3d:edge-rounding-mode', \
		'dr3d:emissive-color', \
		'dr3d:end-angle', \
		'dr3d:horizontal-segments', \
		'dr3d:lighting-mode', \
		'dr3d:normals-direction', \
		'dr3d:normals-kind', \
		'dr3d:shadow', \
		'dr3d:shininess', \
		'dr3d:specular-color', \
		'dr3d:texture-filter', \
		'dr3d:texture-generation-mode-x', \
		'dr3d:texture-generation-mode-y', \
		'dr3d:texture-kind', \
		'dr3d:texture-mode', \
		'dr3d:vertical-segments', \
		'draw:auto-grow-height', \
		'draw:auto-grow-width', \
		'draw:blue', \
		'draw:caption-angle', \
		'draw:caption-angle-type', \
		'draw:caption-escape', \
		'draw:caption-escape-direction', \
		'draw:caption-fit-line-length', \
		'draw:caption-gap', \
		'draw:caption-line-length', \
		'draw:caption-type', \
		'draw:color-inversion', \
		'draw:color-mode', \
		'draw:contrast', \
		'draw:decimal-places', \
		'draw:draw-aspect', \
		'draw:end-guide', \
		'draw:end-line-spacing-horizontal', \
		'draw:end-line-spacing-vertical', \
		'draw:fill', \
		'draw:fill-color', \
		'draw:fill-gradient-name', \
		'draw:fill-hatch-name', \
		'draw:fill-hatch-solid', \
		'draw:fill-image-height', \
		'draw:fill-image-name', \
		'draw:fill-image-ref-point', \
		'draw:fill-image-ref-point-x', \
		'draw:fill-image-ref-point-y', \
		'draw:fill-image-width', \
		'draw:fit-to-contour', \
		'draw:fit-to-size', \
		'draw:frame-display-border', \
		'draw:frame-display-scrollbar', \
		'draw:frame-margin-horizontal', \
		'draw:frame-margin-vertical', \
		'draw:gamma', \
		'draw:gradient-step-count', \
		'draw:green', \
		'draw:guide-distance', \
		'draw:guide-overhang', \
		'draw:image-opacity', \
		'draw:line-distance', \
		'draw:luminance', \
		'draw:marker-end', \
		'draw:marker-end-center', \
		'draw:marker-end-width', \
		'draw:marker-start', \
		'draw:marker-start-center', \
		'draw:marker-start-width', \
		'draw:measure-align', \
		'draw:measure-vertical-align', \
		'draw:ole-draw-aspect', \
		'draw:opacity', \
		'draw:opacity-name', \
		'draw:parallel', \
		'draw:placing', \
		'draw:red', \
		'draw:secondary-fill-color', \
		'draw:shadow', \
		'draw:shadow-color', \
		'draw:shadow-offset-x', \
		'draw:shadow-offset-y', \
		'draw:shadow-opacity', \
		'draw:show-unit', \
		'draw:start-guide', \
		'draw:start-line-spacing-horizontal', \
		'draw:start-line-spacing-vertical', \
		'draw:stroke', \
		'draw:stroke-dash', \
		'draw:stroke-dash-names', \
		'draw:stroke-linejoin', \
		'draw:symbol-color', \
		'draw:textarea-horizontal-align', \
		'draw:textarea-vertical-align', \
		'draw:tile-repeat-offset', \
		'draw:unit', \
		'draw:visible-area-height', \
		'draw:visible-area-left', \
		'draw:visible-area-top', \
		'draw:visible-area-width', \
		'draw:wrap-influence-on-position', \
		'fo:background-color', \
		'fo:border', \
		'fo:border-bottom', \
		'fo:border-left', \
		'fo:border-right', \
		'fo:border-top', \
		'fo:clip', \
		'fo:margin', \
		'fo:margin-bottom', \
		'fo:margin-left', \
		'fo:margin-right', \
		'fo:margin-top', \
		'fo:max-height', \
		'fo:max-width', \
		'fo:min-height', \
		'fo:min-width', \
		'fo:padding', \
		'fo:padding-bottom', \
		'fo:padding-left', \
		'fo:padding-right', \
		'fo:padding-top', \
		'fo:wrap-option', \
		'style:background-transparency', \
		'style:border-line-width', \
		'style:border-line-width-bottom', \
		'style:border-line-width-left', \
		'style:border-line-width-right', \
		'style:border-line-width-top', \
		'style:editable', \
		'style:flow-with-text', \
		'style:horizontal-pos', \
		'style:horizontal-rel', \
		'style:mirror', \
		'style:number-wrapped-paragraphs', \
		'style:overflow-behavior', \
		'style:print-content', \
		'style:protect', \
		'style:rel-height', \
		'style:rel-width', \
		'style:repeat', \
		'style:run-through', \
		'style:shadow', \
		'style:shrink-to-fit', \
		'style:vertical-pos', \
		'style:vertical-rel', \
		'style:wrap', \
		'style:wrap-contour', \
		'style:wrap-contour-mode', \
		'style:wrap-dynamic-threshold', \
		'style:writing-mode', \
		'svg:fill-rule', \
		'svg:height', \
		'svg:stroke-color', \
		'svg:stroke-linecap', \
		'svg:stroke-opacity', \
		'svg:stroke-width', \
		'svg:width', \
		'svg:x', \
		'svg:y', \
		'text:anchor-page-number', \
		'text:anchor-type', \
		'text:animation', \
		'text:animation-delay', \
		'text:animation-direction', \
		'text:animation-repeat', \
		'text:animation-start-inside', \
		'text:animation-steps', \
		'text:animation-stop-inside', \
),
	'style:handout-master' : ( \
		'draw:style-name', \
		'presentation:presentation-page-layout-name', \
		'presentation:use-date-time-name', \
		'presentation:use-footer-name', \
		'presentation:use-header-name', \
		'style:page-layout-name', \
),
	'style:header' : ( \
		'style:display', \
),
	'style:header-footer-properties' : ( \
		'fo:background-color', \
		'fo:border', \
		'fo:border-bottom', \
		'fo:border-left', \
		'fo:border-right', \
		'fo:border-top', \
		'fo:margin', \
		'fo:margin-bottom', \
		'fo:margin-left', \
		'fo:margin-right', \
		'fo:margin-top', \
		'fo:min-height', \
		'fo:padding', \
		'fo:padding-bottom', \
		'fo:padding-left', \
		'fo:padding-right', \
		'fo:padding-top', \
		'style:border-line-width', \
		'style:border-line-width-bottom', \
		'style:border-line-width-left', \
		'style:border-line-width-right', \
		'style:border-line-width-top', \
		'style:dynamic-spacing', \
		'style:shadow', \
		'svg:height', \
),
	'style:header-left' : ( \
		'style:display', \
),
	'style:header-style' : ( \
),
	'style:list-level-label-alignment' : ( \
		'fo:margin-left', \
		'fo:text-indent', \
		'text:label-followed-by', \
		'text:list-tab-stop-position', \
),
	'style:list-level-properties' : ( \
		'fo:height', \
		'fo:text-align', \
		'fo:width', \
		'style:font-name', \
		'style:vertical-pos', \
		'style:vertical-rel', \
		'svg:y', \
		'text:list-level-position-and-space-mode', \
		'text:min-label-distance', \
		'text:min-label-width', \
		'text:space-before', \
),
	'style:map' : ( \
		'style:apply-style-name', \
		'style:base-cell-address', \
		'style:condition', \
),
	'style:master-page' : ( \
		'draw:style-name', \
		'style:display-name', \
		'style:name', \
		'style:next-style-name', \
		'style:page-layout-name', \
),
	'style:page-layout' : ( \
		'style:name', \
		'style:page-usage', \
),
	'style:page-layout-properties' : ( \
		'fo:background-color', \
		'fo:border', \
		'fo:border-bottom', \
		'fo:border-left', \
		'fo:border-right', \
		'fo:border-top', \
		'fo:margin', \
		'fo:margin-bottom', \
		'fo:margin-left', \
		'fo:margin-right', \
		'fo:margin-top', \
		'fo:padding', \
		'fo:padding-bottom', \
		'fo:padding-left', \
		'fo:padding-right', \
		'fo:padding-top', \
		'fo:page-height', \
		'fo:page-width', \
		'style:border-line-width', \
		'style:border-line-width-bottom', \
		'style:border-line-width-left', \
		'style:border-line-width-right', \
		'style:border-line-width-top', \
		'style:first-page-number', \
		'style:footnote-max-height', \
		'style:layout-grid-base-height', \
		'style:layout-grid-base-width', \
		'style:layout-grid-color', \
		'style:layout-grid-display', \
		'style:layout-grid-lines', \
		'style:layout-grid-mode', \
		'style:layout-grid-print', \
		'style:layout-grid-ruby-below', \
		'style:layout-grid-ruby-height', \
		'style:layout-grid-snap-to', \
		'style:layout-grid-standard-mode', \
		'style:num-format', \
		'style:num-format', \
		'style:num-letter-sync', \
		'style:num-prefix', \
		'style:num-suffix', \
		'style:paper-tray-name', \
		'style:print', \
		'style:print-orientation', \
		'style:print-page-order', \
		'style:register-truth-ref-style-name', \
		'style:scale-to', \
		'style:scale-to-pages', \
		'style:shadow', \
		'style:table-centering', \
		'style:writing-mode', \
),
	'style:paragraph-properties' : ( \
		'fo:background-color', \
		'fo:border', \
		'fo:border-bottom', \
		'fo:border-left', \
		'fo:border-right', \
		'fo:border-top', \
		'fo:break-after', \
		'fo:break-before', \
		'fo:hyphenation-keep', \
		'fo:hyphenation-ladder-count', \
		'fo:keep-together', \
		'fo:keep-with-next', \
		'fo:line-height', \
		'fo:margin', \
		'fo:margin-bottom', \
		'fo:margin-left', \
		'fo:margin-right', \
		'fo:margin-top', \
		'fo:orphans', \
		'fo:padding', \
		'fo:padding-bottom', \
		'fo:padding-left', \
		'fo:padding-right', \
		'fo:padding-top', \
		'fo:text-align', \
		'fo:text-align-last', \
		'fo:text-indent', \
		'fo:widows', \
		'style:auto-text-indent', \
		'style:background-transparency', \
		'style:border-line-width', \
		'style:border-line-width-bottom', \
		'style:border-line-width-left', \
		'style:border-line-width-right', \
		'style:border-line-width-top', \
		'style:font-independent-line-spacing', \
		'style:join-border', \
		'style:justify-single-word', \
		'style:line-break', \
		'style:line-height-at-least', \
		'style:line-spacing', \
		'style:page-number', \
		'style:punctuation-wrap', \
		'style:register-true', \
		'style:shadow', \
		'style:snap-to-layout-grid', \
		'style:tab-stop-distance', \
		'style:text-autospace', \
		'style:vertical-align', \
		'style:writing-mode', \
		'style:writing-mode-automatic', \
		'text:line-number', \
		'text:number-lines', \
),
	'style:presentation-page-layout' : ( \
		'style:display-name', \
		'style:name', \
),
	'style:region-center' : ( \
),
	'style:region-left' : ( \
),
	'style:region-right' : ( \
),
	'style:ruby-properties' : ( \
		'style:ruby-align', \
		'style:ruby-position', \
),
	'style:section-properties' : ( \
		'fo:background-color', \
		'fo:margin-left', \
		'fo:margin-right', \
		'style:editable', \
		'style:protect', \
		'style:writing-mode', \
		'text:dont-balance-text-columns', \
),
	'style:style' : ( \
		'style:auto-update', \
		'style:class', \
		'style:data-style-name', \
		'style:default-outline-level', \
		'style:display-name', \
		'style:family', \
		'style:family', \
		'style:family', \
		'style:family', \
		'style:family', \
		'style:family', \
		'style:family', \
		'style:family', \
		'style:family', \
		'style:family', \
		'style:family', \
		'style:list-level', \
		'style:list-style-name', \
		'style:master-page-name', \
		'style:name', \
		'style:next-style-name', \
		'style:parent-style-name', \
		'style:percentage-data-style-name', \
),
	'style:tab-stop' : ( \
		'style:char', \
		'style:leader-color', \
		'style:leader-style', \
		'style:leader-text', \
		'style:leader-text-style', \
		'style:leader-type', \
		'style:leader-width', \
		'style:position', \
		'style:type', \
		'style:type', \
),
	'style:tab-stops' : ( \
),
	'style:table-cell-properties' : ( \
		'fo:background-color', \
		'fo:border', \
		'fo:border-bottom', \
		'fo:border-left', \
		'fo:border-right', \
		'fo:border-top', \
		'fo:padding', \
		'fo:padding-bottom', \
		'fo:padding-left', \
		'fo:padding-right', \
		'fo:padding-top', \
		'fo:wrap-option', \
		'style:border-line-width', \
		'style:border-line-width-bottom', \
		'style:border-line-width-left', \
		'style:border-line-width-right', \
		'style:border-line-width-top', \
		'style:cell-protect', \
		'style:decimal-places', \
		'style:diagonal-bl-tr', \
		'style:diagonal-bl-tr-widths', \
		'style:diagonal-tl-br', \
		'style:diagonal-tl-br-widths', \
		'style:direction', \
		'style:glyph-orientation-vertical', \
		'style:print-content', \
		'style:repeat-content', \
		'style:rotation-align', \
		'style:rotation-angle', \
		'style:shadow', \
		'style:shrink-to-fit', \
		'style:text-align-source', \
		'style:vertical-align', \
		'style:writing-mode', \
),
	'style:table-column-properties' : ( \
		'fo:break-after', \
		'fo:break-before', \
		'style:column-width', \
		'style:rel-column-width', \
		'style:use-optimal-column-width', \
),
	'style:table-properties' : ( \
		'fo:background-color', \
		'fo:break-after', \
		'fo:break-before', \
		'fo:keep-with-next', \
		'fo:margin', \
		'fo:margin-bottom', \
		'fo:margin-left', \
		'fo:margin-right', \
		'fo:margin-top', \
		'style:may-break-between-rows', \
		'style:page-number', \
		'style:rel-width', \
		'style:shadow', \
		'style:width', \
		'style:writing-mode', \
		'table:align', \
		'table:border-model', \
		'table:display', \
),
	'style:table-row-properties' : ( \
		'fo:background-color', \
		'fo:break-after', \
		'fo:break-before', \
		'fo:keep-together', \
		'style:min-row-height', \
		'style:row-height', \
		'style:use-optimal-row-height', \
),
	'style:text-properties' : ( \
		'fo:background-color', \
		'fo:color', \
		'fo:country', \
		'fo:font-family', \
		'fo:font-size', \
		'fo:font-style', \
		'fo:font-variant', \
		'fo:font-weight', \
		'fo:hyphenate', \
		'fo:hyphenation-push-char-count', \
		'fo:hyphenation-remain-char-count', \
		'fo:language', \
		'fo:letter-spacing', \
		'fo:script', \
		'fo:text-shadow', \
		'fo:text-transform', \
		'style:country-asian', \
		'style:country-complex', \
		'style:font-charset', \
		'style:font-charset-asian', \
		'style:font-charset-complex', \
		'style:font-family-asian', \
		'style:font-family-complex', \
		'style:font-family-generic', \
		'style:font-family-generic-asian', \
		'style:font-family-generic-complex', \
		'style:font-name', \
		'style:font-name-asian', \
		'style:font-name-complex', \
		'style:font-pitch', \
		'style:font-pitch-asian', \
		'style:font-pitch-complex', \
		'style:font-relief', \
		'style:font-size-asian', \
		'style:font-size-complex', \
		'style:font-size-rel', \
		'style:font-size-rel-asian', \
		'style:font-size-rel-complex', \
		'style:font-style-asian', \
		'style:font-style-complex', \
		'style:font-style-name', \
		'style:font-style-name-asian', \
		'style:font-style-name-complex', \
		'style:font-weight-asian', \
		'style:font-weight-complex', \
		'style:language-asian', \
		'style:language-complex', \
		'style:letter-kerning', \
		'style:rfc-language-tag', \
		'style:rfc-language-tag-asian', \
		'style:rfc-language-tag-complex', \
		'style:script-asian', \
		'style:script-complex', \
		'style:script-type', \
		'style:text-blinking', \
		'style:text-combine', \
		'style:text-combine-end-char', \
		'style:text-combine-start-char', \
		'style:text-emphasize', \
		'style:text-line-through-color', \
		'style:text-line-through-mode', \
		'style:text-line-through-style', \
		'style:text-line-through-text', \
		'style:text-line-through-text-style', \
		'style:text-line-through-type', \
		'style:text-line-through-width', \
		'style:text-outline', \
		'style:text-overline-color', \
		'style:text-overline-mode', \
		'style:text-overline-style', \
		'style:text-overline-type', \
		'style:text-overline-width', \
		'style:text-position', \
		'style:text-rotation-angle', \
		'style:text-rotation-scale', \
		'style:text-scale', \
		'style:text-underline-color', \
		'style:text-underline-mode', \
		'style:text-underline-style', \
		'style:text-underline-type', \
		'style:text-underline-width', \
		'style:use-window-font-color', \
		'text:condition', \
		'text:display', \
		'text:display', \
		'text:display', \
),
	'svg:definition-src' : ( \
		'xlink:actuate', \
		'xlink:href', \
		'xlink:type', \
),
	'svg:desc' : ( \
),
	'svg:font-face-format' : ( \
		'svg:string', \
),
	'svg:font-face-name' : ( \
		'svg:name', \
),
	'svg:font-face-src' : ( \
),
	'svg:font-face-uri' : ( \
		'xlink:actuate', \
		'xlink:href', \
		'xlink:type', \
),
	'svg:linearGradient' : ( \
		'draw:display-name', \
		'draw:name', \
		'svg:gradientTransform', \
		'svg:gradientUnits', \
		'svg:spreadMethod', \
		'svg:x1', \
		'svg:x2', \
		'svg:y1', \
		'svg:y2', \
),
	'svg:radialGradient' : ( \
		'draw:display-name', \
		'draw:name', \
		'svg:cx', \
		'svg:cy', \
		'svg:fx', \
		'svg:fy', \
		'svg:gradientTransform', \
		'svg:gradientUnits', \
		'svg:r', \
		'svg:spreadMethod', \
),
	'svg:stop' : ( \
		'svg:offset', \
		'svg:stop-color', \
		'svg:stop-opacity', \
),
	'svg:title' : ( \
),
	'table:background' : ( \
		'table:style-name', \
),
	'table:body' : ( \
		'table:paragraph-style-name', \
		'table:style-name', \
),
	'table:calculation-settings' : ( \
		'table:automatic-find-labels', \
		'table:case-sensitive', \
		'table:null-year', \
		'table:precision-as-shown', \
		'table:search-criteria-must-apply-to-whole-cell', \
		'table:use-regular-expressions', \
		'table:use-wildcards', \
),
	'table:cell-address' : ( \
		'table:column', \
		'table:row', \
		'table:table', \
),
	'table:cell-content-change' : ( \
		'table:acceptance-state', \
		'table:id', \
		'table:rejecting-change-id', \
),
	'table:cell-content-deletion' : ( \
		'table:id', \
),
	'table:cell-range-source' : ( \
		'table:filter-name', \
		'table:filter-options', \
		'table:last-column-spanned', \
		'table:last-row-spanned', \
		'table:name', \
		'table:refresh-delay', \
		'xlink:actuate', \
		'xlink:href', \
		'xlink:type', \
),
	'table:change-deletion' : ( \
		'table:id', \
),
	'table:change-track-table-cell' : ( \
		'office:boolean-value', \
		'office:currency', \
		'office:date-value', \
		'office:string-value', \
		'office:time-value', \
		'office:value', \
		'office:value-type', \
		'office:value-type', \
		'office:value-type', \
		'office:value-type', \
		'office:value-type', \
		'office:value-type', \
		'office:value-type', \
		'table:cell-address', \
		'table:formula', \
		'table:matrix-covered', \
		'table:number-matrix-columns-spanned', \
		'table:number-matrix-rows-spanned', \
),
	'table:consolidation' : ( \
		'table:function', \
		'table:link-to-source-data', \
		'table:source-cell-range-addresses', \
		'table:target-cell-address', \
		'table:use-labels', \
),
	'table:content-validation' : ( \
		'table:allow-empty-cell', \
		'table:base-cell-address', \
		'table:condition', \
		'table:display-list', \
		'table:name', \
),
	'table:content-validations' : ( \
),
	'table:covered-table-cell' : ( \
		'office:boolean-value', \
		'office:currency', \
		'office:date-value', \
		'office:string-value', \
		'office:time-value', \
		'office:value', \
		'office:value-type', \
		'office:value-type', \
		'office:value-type', \
		'office:value-type', \
		'office:value-type', \
		'office:value-type', \
		'office:value-type', \
		'table:content-validation-name', \
		'table:formula', \
		'table:number-columns-repeated', \
		'table:protect', \
		'table:protected', \
		'table:style-name', \
		'xhtml:about', \
		'xhtml:content', \
		'xhtml:datatype', \
		'xhtml:property', \
		'xml:id', \
),
	'table:cut-offs' : ( \
),
	'table:data-pilot-display-info' : ( \
		'table:data-field', \
		'table:display-member-mode', \
		'table:enabled', \
		'table:member-count', \
),
	'table:data-pilot-field' : ( \
		'table:function', \
		'table:is-data-layout-field', \
		'table:orientation', \
		'table:orientation', \
		'table:selected-page', \
		'table:source-field-name', \
		'table:used-hierarchy', \
),
	'table:data-pilot-field-reference' : ( \
		'table:field-name', \
		'table:member-name', \
		'table:member-type', \
		'table:member-type', \
		'table:type', \
),
	'table:data-pilot-group' : ( \
		'table:name', \
),
	'table:data-pilot-group-member' : ( \
		'table:name', \
),
	'table:data-pilot-groups' : ( \
		'table:date-end', \
		'table:date-start', \
		'table:end', \
		'table:grouped-by', \
		'table:source-field-name', \
		'table:start', \
		'table:step', \
),
	'table:data-pilot-layout-info' : ( \
		'table:add-empty-lines', \
		'table:layout-mode', \
),
	'table:data-pilot-level' : ( \
		'table:show-empty', \
),
	'table:data-pilot-member' : ( \
		'table:display', \
		'table:name', \
		'table:show-details', \
),
	'table:data-pilot-members' : ( \
),
	'table:data-pilot-sort-info' : ( \
		'table:data-field', \
		'table:order', \
		'table:sort-mode', \
		'table:sort-mode', \
),
	'table:data-pilot-subtotal' : ( \
		'table:function', \
),
	'table:data-pilot-subtotals' : ( \
),
	'table:data-pilot-table' : ( \
		'table:application-data', \
		'table:buttons', \
		'table:drill-down-on-double-click', \
		'table:grand-total', \
		'table:identify-categories', \
		'table:ignore-empty-rows', \
		'table:name', \
		'table:show-filter-button', \
		'table:target-range-address', \
),
	'table:data-pilot-tables' : ( \
),
	'table:database-range' : ( \
		'table:contains-header', \
		'table:display-filter-buttons', \
		'table:has-persistent-data', \
		'table:is-selection', \
		'table:name', \
		'table:on-update-keep-size', \
		'table:on-update-keep-styles', \
		'table:orientation', \
		'table:refresh-delay', \
		'table:target-range-address', \
),
	'table:database-ranges' : ( \
),
	'table:database-source-query' : ( \
		'table:database-name', \
		'table:query-name', \
),
	'table:database-source-sql' : ( \
		'table:database-name', \
		'table:parse-sql-statement', \
		'table:sql-statement', \
),
	'table:database-source-table' : ( \
		'table:database-name', \
		'table:database-table-name', \
),
	'table:dde-link' : ( \
),
	'table:dde-links' : ( \
),
	'table:deletion' : ( \
		'table:acceptance-state', \
		'table:id', \
		'table:multi-deletion-spanned', \
		'table:position', \
		'table:rejecting-change-id', \
		'table:table', \
		'table:type', \
),
	'table:deletions' : ( \
),
	'table:dependencies' : ( \
),
	'table:dependency' : ( \
		'table:id', \
),
	'table:desc' : ( \
),
	'table:detective' : ( \
),
	'table:error-macro' : ( \
		'table:execute', \
),
	'table:error-message' : ( \
		'table:display', \
		'table:message-type', \
		'table:title', \
),
	'table:even-columns' : ( \
		'table:paragraph-style-name', \
		'table:style-name', \
),
	'table:even-rows' : ( \
		'table:paragraph-style-name', \
		'table:style-name', \
),
	'table:filter' : ( \
		'table:condition-source', \
		'table:condition-source-range-address', \
		'table:display-duplicates', \
		'table:target-range-address', \
),
	'table:filter-and' : ( \
),
	'table:filter-condition' : ( \
		'table:case-sensitive', \
		'table:data-type', \
		'table:field-number', \
		'table:operator', \
		'table:value', \
),
	'table:filter-or' : ( \
),
	'table:filter-set-item' : ( \
		'table:value', \
),
	'table:first-column' : ( \
		'table:paragraph-style-name', \
		'table:style-name', \
),
	'table:first-row' : ( \
		'table:paragraph-style-name', \
		'table:style-name', \
),
	'table:help-message' : ( \
		'table:display', \
		'table:title', \
),
	'table:highlighted-range' : ( \
		'table:cell-range-address', \
		'table:contains-error', \
		'table:direction', \
		'table:marked-invalid', \
),
	'table:insertion' : ( \
		'table:acceptance-state', \
		'table:count', \
		'table:id', \
		'table:position', \
		'table:rejecting-change-id', \
		'table:table', \
		'table:type', \
),
	'table:insertion-cut-off' : ( \
		'table:id', \
		'table:position', \
),
	'table:iteration' : ( \
		'table:maximum-difference', \
		'table:status', \
		'table:steps', \
),
	'table:label-range' : ( \
		'table:data-cell-range-address', \
		'table:label-cell-range-address', \
		'table:orientation', \
),
	'table:label-ranges' : ( \
),
	'table:last-column' : ( \
		'table:paragraph-style-name', \
		'table:style-name', \
),
	'table:last-row' : ( \
		'table:paragraph-style-name', \
		'table:style-name', \
),
	'table:movement' : ( \
		'table:acceptance-state', \
		'table:id', \
		'table:rejecting-change-id', \
),
	'table:movement-cut-off' : ( \
		'table:end-position', \
		'table:position', \
		'table:start-position', \
),
	'table:named-expression' : ( \
		'table:base-cell-address', \
		'table:expression', \
		'table:name', \
),
	'table:named-expressions' : ( \
),
	'table:named-range' : ( \
		'table:base-cell-address', \
		'table:cell-range-address', \
		'table:name', \
		'table:range-usable-as', \
),
	'table:null-date' : ( \
		'table:date-value', \
		'table:value-type', \
),
	'table:odd-columns' : ( \
		'table:paragraph-style-name', \
		'table:style-name', \
),
	'table:odd-rows' : ( \
		'table:paragraph-style-name', \
		'table:style-name', \
),
	'table:operation' : ( \
		'table:index', \
		'table:name', \
),
	'table:previous' : ( \
		'table:id', \
),
	'table:scenario' : ( \
		'table:border-color', \
		'table:comment', \
		'table:copy-back', \
		'table:copy-formulas', \
		'table:copy-styles', \
		'table:display-border', \
		'table:is-active', \
		'table:protected', \
		'table:scenario-ranges', \
),
	'table:shapes' : ( \
),
	'table:sort' : ( \
		'table:algorithm', \
		'table:bind-styles-to-content', \
		'table:case-sensitive', \
		'table:country', \
		'table:embedded-number-behavior', \
		'table:language', \
		'table:rfc-language-tag', \
		'table:script', \
		'table:target-range-address', \
),
	'table:sort-by' : ( \
		'table:data-type', \
		'table:field-number', \
		'table:order', \
),
	'table:sort-groups' : ( \
		'table:data-type', \
		'table:order', \
),
	'table:source-cell-range' : ( \
		'table:cell-range-address', \
),
	'table:source-range-address' : ( \
		'table:column', \
		'table:end-column', \
		'table:end-row', \
		'table:end-table', \
		'table:row', \
		'table:start-column', \
		'table:start-row', \
		'table:start-table', \
		'table:table', \
),
	'table:source-service' : ( \
		'table:name', \
		'table:object-name', \
		'table:password', \
		'table:source-name', \
		'table:user-name', \
),
	'table:subtotal-field' : ( \
		'table:field-number', \
		'table:function', \
),
	'table:subtotal-rule' : ( \
		'table:group-by-field-number', \
),
	'table:subtotal-rules' : ( \
		'table:bind-styles-to-content', \
		'table:case-sensitive', \
		'table:page-breaks-on-group-change', \
),
	'table:table' : ( \
		'table:is-sub-table', \
		'table:name', \
		'table:print', \
		'table:print-ranges', \
		'table:protected', \
		'table:protection-key', \
		'table:protection-key-digest-algorithm', \
		'table:style-name', \
		'table:template-name', \
		'table:use-banding-columns-styles', \
		'table:use-banding-rows-styles', \
		'table:use-first-column-styles', \
		'table:use-first-row-styles', \
		'table:use-last-column-styles', \
		'table:use-last-row-styles', \
		'xml:id', \
),
	'table:table-cell' : ( \
		'office:boolean-value', \
		'office:currency', \
		'office:date-value', \
		'office:string-value', \
		'office:time-value', \
		'office:value', \
		'office:value-type', \
		'office:value-type', \
		'office:value-type', \
		'office:value-type', \
		'office:value-type', \
		'office:value-type', \
		'office:value-type', \
		'table:content-validation-name', \
		'table:formula', \
		'table:number-columns-repeated', \
		'table:number-columns-spanned', \
		'table:number-matrix-columns-spanned', \
		'table:number-matrix-rows-spanned', \
		'table:number-rows-spanned', \
		'table:protect', \
		'table:protected', \
		'table:style-name', \
		'xhtml:about', \
		'xhtml:content', \
		'xhtml:datatype', \
		'xhtml:property', \
		'xml:id', \
),
	'table:table-column' : ( \
		'table:default-cell-style-name', \
		'table:number-columns-repeated', \
		'table:style-name', \
		'table:visibility', \
		'xml:id', \
),
	'table:table-column-group' : ( \
		'table:display', \
),
	'table:table-columns' : ( \
),
	'table:table-header-columns' : ( \
),
	'table:table-header-rows' : ( \
),
	'table:table-row' : ( \
		'table:default-cell-style-name', \
		'table:number-rows-repeated', \
		'table:style-name', \
		'table:visibility', \
		'xml:id', \
),
	'table:table-row-group' : ( \
		'table:display', \
),
	'table:table-rows' : ( \
),
	'table:table-source' : ( \
		'table:filter-name', \
		'table:filter-options', \
		'table:mode', \
		'table:refresh-delay', \
		'table:table-name', \
		'xlink:actuate', \
		'xlink:href', \
		'xlink:type', \
),
	'table:table-template' : ( \
		'table:first-row-end-column', \
		'table:first-row-start-column', \
		'table:last-row-end-column', \
		'table:last-row-start-column', \
		'table:name', \
),
	'table:target-range-address' : ( \
		'table:column', \
		'table:end-column', \
		'table:end-row', \
		'table:end-table', \
		'table:row', \
		'table:start-column', \
		'table:start-row', \
		'table:start-table', \
		'table:table', \
),
	'table:title' : ( \
),
	'table:tracked-changes' : ( \
		'table:track-changes', \
),
	'text:a' : ( \
		'office:name', \
		'office:target-frame-name', \
		'office:title', \
		'text:style-name', \
		'text:visited-style-name', \
		'xlink:actuate', \
		'xlink:href', \
		'xlink:show', \
		'xlink:type', \
),
	'text:alphabetical-index' : ( \
		'text:name', \
		'text:protected', \
		'text:protection-key', \
		'text:protection-key-digest-algorithm', \
		'text:style-name', \
		'xml:id', \
),
	'text:alphabetical-index-auto-mark-file' : ( \
		'xlink:href', \
		'xlink:type', \
),
	'text:alphabetical-index-entry-template' : ( \
		'text:outline-level', \
		'text:style-name', \
),
	'text:alphabetical-index-mark' : ( \
		'text:key1', \
		'text:key1-phonetic', \
		'text:key2', \
		'text:key2-phonetic', \
		'text:main-entry', \
		'text:string-value', \
		'text:string-value-phonetic', \
),
	'text:alphabetical-index-mark-end' : ( \
		'text:id', \
),
	'text:alphabetical-index-mark-start' : ( \
		'text:id', \
		'text:key1', \
		'text:key1-phonetic', \
		'text:key2', \
		'text:key2-phonetic', \
		'text:main-entry', \
		'text:string-value-phonetic', \
),
	'text:alphabetical-index-source' : ( \
		'fo:country', \
		'fo:language', \
		'fo:script', \
		'style:rfc-language-tag', \
		'text:alphabetical-separators', \
		'text:capitalize-entries', \
		'text:combine-entries', \
		'text:combine-entries-with-dash', \
		'text:combine-entries-with-pp', \
		'text:comma-separated', \
		'text:ignore-case', \
		'text:index-scope', \
		'text:main-entry-style-name', \
		'text:relative-tab-stop-position', \
		'text:sort-algorithm', \
		'text:use-keys-as-entries', \
),
	'text:author-initials' : ( \
		'text:fixed', \
),
	'text:author-name' : ( \
		'text:fixed', \
),
	'text:bibliography' : ( \
		'text:name', \
		'text:protected', \
		'text:protection-key', \
		'text:protection-key-digest-algorithm', \
		'text:style-name', \
		'xml:id', \
),
	'text:bibliography-configuration' : ( \
		'fo:country', \
		'fo:language', \
		'fo:script', \
		'style:rfc-language-tag', \
		'text:numbered-entries', \
		'text:prefix', \
		'text:sort-algorithm', \
		'text:sort-by-position', \
		'text:suffix', \
),
	'text:bibliography-entry-template' : ( \
		'text:bibliography-type', \
		'text:style-name', \
),
	'text:bibliography-mark' : ( \
		'text:address', \
		'text:annote', \
		'text:author', \
		'text:bibliography-type', \
		'text:booktitle', \
		'text:chapter', \
		'text:custom1', \
		'text:custom2', \
		'text:custom3', \
		'text:custom4', \
		'text:custom5', \
		'text:edition', \
		'text:editor', \
		'text:howpublished', \
		'text:identifier', \
		'text:institution', \
		'text:isbn', \
		'text:issn', \
		'text:journal', \
		'text:month', \
		'text:note', \
		'text:number', \
		'text:organizations', \
		'text:pages', \
		'text:publisher', \
		'text:report-type', \
		'text:school', \
		'text:series', \
		'text:title', \
		'text:url', \
		'text:volume', \
		'text:year', \
),
	'text:bibliography-source' : ( \
),
	'text:bookmark' : ( \
		'text:name', \
		'xml:id', \
),
	'text:bookmark-end' : ( \
		'text:name', \
),
	'text:bookmark-ref' : ( \
		'text:ref-name', \
		'text:reference-format', \
),
	'text:bookmark-start' : ( \
		'text:name', \
		'xhtml:about', \
		'xhtml:content', \
		'xhtml:datatype', \
		'xhtml:property', \
		'xml:id', \
),
	'text:change' : ( \
		'text:change-id', \
),
	'text:change-end' : ( \
		'text:change-id', \
),
	'text:change-start' : ( \
		'text:change-id', \
),
	'text:changed-region' : ( \
		'text:id', \
		'xml:id', \
),
	'text:chapter' : ( \
		'text:display', \
		'text:outline-level', \
),
	'text:character-count' : ( \
		'style:num-format', \
		'style:num-format', \
		'style:num-letter-sync', \
),
	'text:conditional-text' : ( \
		'text:condition', \
		'text:current-value', \
		'text:string-value-if-false', \
		'text:string-value-if-true', \
),
	'text:creation-date' : ( \
		'style:data-style-name', \
		'text:date-value', \
		'text:fixed', \
),
	'text:creation-time' : ( \
		'style:data-style-name', \
		'text:fixed', \
		'text:time-value', \
),
	'text:creator' : ( \
		'text:fixed', \
),
	'text:database-display' : ( \
		'style:data-style-name', \
		'text:column-name', \
		'text:database-name', \
		'text:table-name', \
		'text:table-type', \
),
	'text:database-name' : ( \
		'text:database-name', \
		'text:table-name', \
		'text:table-type', \
),
	'text:database-next' : ( \
		'text:condition', \
		'text:database-name', \
		'text:table-name', \
		'text:table-type', \
),
	'text:database-row-number' : ( \
		'style:num-format', \
		'style:num-format', \
		'style:num-letter-sync', \
		'text:database-name', \
		'text:table-name', \
		'text:table-type', \
		'text:value', \
),
	'text:database-row-select' : ( \
		'text:condition', \
		'text:database-name', \
		'text:row-number', \
		'text:table-name', \
		'text:table-type', \
),
	'text:date' : ( \
		'style:data-style-name', \
		'text:date-adjust', \
		'text:date-value', \
		'text:fixed', \
),
	'text:dde-connection' : ( \
		'text:connection-name', \
),
	'text:dde-connection-decl' : ( \
		'office:automatic-update', \
		'office:dde-application', \
		'office:dde-item', \
		'office:dde-topic', \
		'office:name', \
),
	'text:dde-connection-decls' : ( \
),
	'text:deletion' : ( \
),
	'text:description' : ( \
		'text:fixed', \
),
	'text:editing-cycles' : ( \
		'text:fixed', \
),
	'text:editing-duration' : ( \
		'style:data-style-name', \
		'text:duration', \
		'text:fixed', \
),
	'text:execute-macro' : ( \
		'text:name', \
),
	'text:expression' : ( \
		'office:boolean-value', \
		'office:currency', \
		'office:date-value', \
		'office:string-value', \
		'office:time-value', \
		'office:value', \
		'office:value-type', \
		'office:value-type', \
		'office:value-type', \
		'office:value-type', \
		'office:value-type', \
		'office:value-type', \
		'office:value-type', \
		'style:data-style-name', \
		'text:display', \
		'text:formula', \
),
	'text:file-name' : ( \
		'text:display', \
		'text:fixed', \
),
	'text:format-change' : ( \
),
	'text:h' : ( \
		'text:class-names', \
		'text:cond-style-name', \
		'text:id', \
		'text:is-list-header', \
		'text:outline-level', \
		'text:restart-numbering', \
		'text:start-value', \
		'text:style-name', \
		'xhtml:about', \
		'xhtml:content', \
		'xhtml:datatype', \
		'xhtml:property', \
		'xml:id', \
),
	'text:hidden-paragraph' : ( \
		'text:condition', \
		'text:is-hidden', \
),
	'text:hidden-text' : ( \
		'text:condition', \
		'text:is-hidden', \
		'text:string-value', \
),
	'text:illustration-index' : ( \
		'text:name', \
		'text:protected', \
		'text:protection-key', \
		'text:protection-key-digest-algorithm', \
		'text:style-name', \
		'xml:id', \
),
	'text:illustration-index-entry-template' : ( \
		'text:style-name', \
),
	'text:illustration-index-source' : ( \
		'text:caption-sequence-format', \
		'text:caption-sequence-name', \
		'text:index-scope', \
		'text:relative-tab-stop-position', \
		'text:use-caption', \
),
	'text:image-count' : ( \
		'style:num-format', \
		'style:num-format', \
		'style:num-letter-sync', \
),
	'text:index-body' : ( \
),
	'text:index-entry-bibliography' : ( \
		'text:bibliography-data-field', \
		'text:style-name', \
),
	'text:index-entry-chapter' : ( \
		'text:display', \
		'text:outline-level', \
		'text:style-name', \
),
	'text:index-entry-link-end' : ( \
		'text:style-name', \
),
	'text:index-entry-link-start' : ( \
		'text:style-name', \
),
	'text:index-entry-page-number' : ( \
		'text:style-name', \
),
	'text:index-entry-span' : ( \
		'text:style-name', \
),
	'text:index-entry-tab-stop' : ( \
		'style:leader-char', \
		'style:position', \
		'style:type', \
		'style:type', \
		'text:style-name', \
),
	'text:index-entry-text' : ( \
		'text:style-name', \
),
	'text:index-source-style' : ( \
		'text:style-name', \
),
	'text:index-source-styles' : ( \
		'text:outline-level', \
),
	'text:index-title' : ( \
		'text:name', \
		'text:protected', \
		'text:protection-key', \
		'text:protection-key-digest-algorithm', \
		'text:style-name', \
		'xml:id', \
),
	'text:index-title-template' : ( \
		'text:style-name', \
),
	'text:initial-creator' : ( \
		'text:fixed', \
),
	'text:insertion' : ( \
),
	'text:keywords' : ( \
		'text:fixed', \
),
	'text:line-break' : ( \
),
	'text:linenumbering-configuration' : ( \
		'style:num-format', \
		'style:num-format', \
		'style:num-letter-sync', \
		'text:count-empty-lines', \
		'text:count-in-text-boxes', \
		'text:increment', \
		'text:number-lines', \
		'text:number-position', \
		'text:offset', \
		'text:restart-on-page', \
		'text:style-name', \
),
	'text:linenumbering-separator' : ( \
		'text:increment', \
),
	'text:list' : ( \
		'text:continue-list', \
		'text:continue-numbering', \
		'text:style-name', \
		'xml:id', \
),
	'text:list-header' : ( \
		'xml:id', \
),
	'text:list-item' : ( \
		'text:start-value', \
		'text:style-override', \
		'xml:id', \
),
	'text:list-level-style-bullet' : ( \
		'style:num-prefix', \
		'style:num-suffix', \
		'text:bullet-char', \
		'text:bullet-relative-size', \
		'text:level', \
		'text:style-name', \
),
	'text:list-level-style-image' : ( \
		'text:level', \
		'xlink:actuate', \
		'xlink:href', \
		'xlink:show', \
		'xlink:type', \
),
	'text:list-level-style-number' : ( \
		'style:num-format', \
		'style:num-format', \
		'style:num-letter-sync', \
		'style:num-prefix', \
		'style:num-suffix', \
		'text:display-levels', \
		'text:level', \
		'text:start-value', \
		'text:style-name', \
),
	'text:list-style' : ( \
		'style:display-name', \
		'style:name', \
		'text:consecutive-numbering', \
),
	'text:measure' : ( \
		'text:kind', \
),
	'text:meta' : ( \
		'xhtml:about', \
		'xhtml:content', \
		'xhtml:datatype', \
		'xhtml:property', \
		'xml:id', \
),
	'text:meta-field' : ( \
		'style:data-style-name', \
		'xml:id', \
),
	'text:modification-date' : ( \
		'style:data-style-name', \
		'text:date-value', \
		'text:fixed', \
),
	'text:modification-time' : ( \
		'style:data-style-name', \
		'text:fixed', \
		'text:time-value', \
),
	'text:note' : ( \
		'text:id', \
		'text:note-class', \
),
	'text:note-body' : ( \
),
	'text:note-citation' : ( \
		'text:label', \
),
	'text:note-continuation-notice-backward' : ( \
),
	'text:note-continuation-notice-forward' : ( \
),
	'text:note-ref' : ( \
		'text:note-class', \
		'text:ref-name', \
		'text:reference-format', \
),
	'text:notes-configuration' : ( \
		'style:num-format', \
		'style:num-format', \
		'style:num-letter-sync', \
		'style:num-prefix', \
		'style:num-suffix', \
		'text:citation-body-style-name', \
		'text:citation-style-name', \
		'text:default-style-name', \
		'text:footnotes-position', \
		'text:master-page-name', \
		'text:note-class', \
		'text:start-numbering-at', \
		'text:start-value', \
),
	'text:number' : ( \
),
	'text:numbered-paragraph' : ( \
		'text:continue-numbering', \
		'text:level', \
		'text:list-id', \
		'text:start-value', \
		'text:style-name', \
		'xml:id', \
),
	'text:object-count' : ( \
		'style:num-format', \
		'style:num-format', \
		'style:num-letter-sync', \
),
	'text:object-index' : ( \
		'text:name', \
		'text:protected', \
		'text:protection-key', \
		'text:protection-key-digest-algorithm', \
		'text:style-name', \
		'xml:id', \
),
	'text:object-index-entry-template' : ( \
		'text:style-name', \
),
	'text:object-index-source' : ( \
		'text:index-scope', \
		'text:relative-tab-stop-position', \
		'text:use-chart-objects', \
		'text:use-draw-objects', \
		'text:use-math-objects', \
		'text:use-other-objects', \
		'text:use-spreadsheet-objects', \
),
	'text:outline-level-style' : ( \
		'style:num-format', \
		'style:num-format', \
		'style:num-letter-sync', \
		'style:num-prefix', \
		'style:num-suffix', \
		'text:display-levels', \
		'text:level', \
		'text:start-value', \
		'text:style-name', \
),
	'text:outline-style' : ( \
		'style:name', \
),
	'text:p' : ( \
		'text:class-names', \
		'text:cond-style-name', \
		'text:id', \
		'text:style-name', \
		'xhtml:about', \
		'xhtml:content', \
		'xhtml:datatype', \
		'xhtml:property', \
		'xml:id', \
),
	'text:page' : ( \
		'text:master-page-name', \
),
	'text:page-continuation' : ( \
		'text:select-page', \
		'text:string-value', \
),
	'text:page-count' : ( \
		'style:num-format', \
		'style:num-format', \
		'style:num-letter-sync', \
),
	'text:page-number' : ( \
		'style:num-format', \
		'style:num-format', \
		'style:num-letter-sync', \
		'text:fixed', \
		'text:page-adjust', \
		'text:select-page', \
),
	'text:page-sequence' : ( \
),
	'text:page-variable-get' : ( \
		'style:num-format', \
		'style:num-format', \
		'style:num-letter-sync', \
),
	'text:page-variable-set' : ( \
		'text:active', \
		'text:page-adjust', \
),
	'text:paragraph-count' : ( \
		'style:num-format', \
		'style:num-format', \
		'style:num-letter-sync', \
),
	'text:placeholder' : ( \
		'text:description', \
		'text:placeholder-type', \
),
	'text:print-date' : ( \
		'style:data-style-name', \
		'text:date-value', \
		'text:fixed', \
),
	'text:print-time' : ( \
		'style:data-style-name', \
		'text:fixed', \
		'text:time-value', \
),
	'text:printed-by' : ( \
		'text:fixed', \
),
	'text:reference-mark' : ( \
		'text:name', \
),
	'text:reference-mark-end' : ( \
		'text:name', \
),
	'text:reference-mark-start' : ( \
		'text:name', \
),
	'text:reference-ref' : ( \
		'text:ref-name', \
		'text:reference-format', \
),
	'text:ruby' : ( \
		'text:style-name', \
),
	'text:ruby-base' : ( \
),
	'text:ruby-text' : ( \
		'text:style-name', \
),
	'text:s' : ( \
		'text:c', \
),
	'text:script' : ( \
		'script:language', \
		'xlink:href', \
		'xlink:type', \
),
	'text:section' : ( \
		'text:condition', \
		'text:display', \
		'text:display', \
		'text:name', \
		'text:protected', \
		'text:protection-key', \
		'text:protection-key-digest-algorithm', \
		'text:style-name', \
		'xml:id', \
),
	'text:section-source' : ( \
		'text:filter-name', \
		'text:section-name', \
		'xlink:href', \
		'xlink:show', \
		'xlink:type', \
),
	'text:sender-city' : ( \
		'text:fixed', \
),
	'text:sender-company' : ( \
		'text:fixed', \
),
	'text:sender-country' : ( \
		'text:fixed', \
),
	'text:sender-email' : ( \
		'text:fixed', \
),
	'text:sender-fax' : ( \
		'text:fixed', \
),
	'text:sender-firstname' : ( \
		'text:fixed', \
),
	'text:sender-initials' : ( \
		'text:fixed', \
),
	'text:sender-lastname' : ( \
		'text:fixed', \
),
	'text:sender-phone-private' : ( \
		'text:fixed', \
),
	'text:sender-phone-work' : ( \
		'text:fixed', \
),
	'text:sender-position' : ( \
		'text:fixed', \
),
	'text:sender-postal-code' : ( \
		'text:fixed', \
),
	'text:sender-state-or-province' : ( \
		'text:fixed', \
),
	'text:sender-street' : ( \
		'text:fixed', \
),
	'text:sender-title' : ( \
		'text:fixed', \
),
	'text:sequence' : ( \
		'style:num-format', \
		'style:num-format', \
		'style:num-letter-sync', \
		'text:formula', \
		'text:name', \
		'text:ref-name', \
),
	'text:sequence-decl' : ( \
		'text:display-outline-level', \
		'text:name', \
		'text:separation-character', \
),
	'text:sequence-decls' : ( \
),
	'text:sequence-ref' : ( \
		'text:ref-name', \
		'text:reference-format', \
),
	'text:sheet-name' : ( \
),
	'text:soft-page-break' : ( \
),
	'text:sort-key' : ( \
		'text:key', \
		'text:sort-ascending', \
),
	'text:span' : ( \
		'text:class-names', \
		'text:style-name', \
),
	'text:subject' : ( \
		'text:fixed', \
),
	'text:tab' : ( \
		'text:tab-ref', \
),
	'text:table-count' : ( \
		'style:num-format', \
		'style:num-format', \
		'style:num-letter-sync', \
),
	'text:table-formula' : ( \
		'style:data-style-name', \
		'text:display', \
		'text:formula', \
),
	'text:table-index' : ( \
		'text:name', \
		'text:protected', \
		'text:protection-key', \
		'text:protection-key-digest-algorithm', \
		'text:style-name', \
		'xml:id', \
),
	'text:table-index-entry-template' : ( \
		'text:style-name', \
),
	'text:table-index-source' : ( \
		'text:caption-sequence-format', \
		'text:caption-sequence-name', \
		'text:index-scope', \
		'text:relative-tab-stop-position', \
		'text:use-caption', \
),
	'text:table-of-content' : ( \
		'text:name', \
		'text:protected', \
		'text:protection-key', \
		'text:protection-key-digest-algorithm', \
		'text:style-name', \
		'xml:id', \
),
	'text:table-of-content-entry-template' : ( \
		'text:outline-level', \
		'text:style-name', \
),
	'text:table-of-content-source' : ( \
		'text:index-scope', \
		'text:outline-level', \
		'text:relative-tab-stop-position', \
		'text:use-index-marks', \
		'text:use-index-source-styles', \
		'text:use-outline-level', \
),
	'text:template-name' : ( \
		'text:display', \
),
	'text:text-input' : ( \
		'text:description', \
),
	'text:time' : ( \
		'style:data-style-name', \
		'text:fixed', \
		'text:time-adjust', \
		'text:time-value', \
),
	'text:title' : ( \
		'text:fixed', \
),
	'text:toc-mark' : ( \
		'text:outline-level', \
		'text:string-value', \
),
	'text:toc-mark-end' : ( \
		'text:id', \
),
	'text:toc-mark-start' : ( \
		'text:id', \
		'text:outline-level', \
),
	'text:tracked-changes' : ( \
		'text:track-changes', \
),
	'text:user-defined' : ( \
		'office:boolean-value', \
		'office:date-value', \
		'office:string-value', \
		'office:time-value', \
		'office:value', \
		'style:data-style-name', \
		'text:fixed', \
		'text:name', \
),
	'text:user-field-decl' : ( \
		'office:boolean-value', \
		'office:currency', \
		'office:date-value', \
		'office:string-value', \
		'office:time-value', \
		'office:value', \
		'office:value-type', \
		'office:value-type', \
		'office:value-type', \
		'office:value-type', \
		'office:value-type', \
		'office:value-type', \
		'office:value-type', \
		'text:formula', \
		'text:name', \
),
	'text:user-field-decls' : ( \
),
	'text:user-field-get' : ( \
		'style:data-style-name', \
		'text:display', \
		'text:name', \
),
	'text:user-field-input' : ( \
		'style:data-style-name', \
		'text:description', \
		'text:name', \
),
	'text:user-index' : ( \
		'text:name', \
		'text:protected', \
		'text:protection-key', \
		'text:protection-key-digest-algorithm', \
		'text:style-name', \
		'xml:id', \
),
	'text:user-index-entry-template' : ( \
		'text:outline-level', \
		'text:style-name', \
),
	'text:user-index-mark' : ( \
		'text:index-name', \
		'text:outline-level', \
		'text:string-value', \
),
	'text:user-index-mark-end' : ( \
		'text:id', \
),
	'text:user-index-mark-start' : ( \
		'text:id', \
		'text:index-name', \
		'text:outline-level', \
),
	'text:user-index-source' : ( \
		'text:copy-outline-levels', \
		'text:index-name', \
		'text:index-scope', \
		'text:relative-tab-stop-position', \
		'text:use-floating-frames', \
		'text:use-graphics', \
		'text:use-index-marks', \
		'text:use-index-source-styles', \
		'text:use-objects', \
		'text:use-tables', \
),
	'text:variable-decl' : ( \
		'office:value-type', \
		'text:name', \
),
	'text:variable-decls' : ( \
),
	'text:variable-get' : ( \
		'style:data-style-name', \
		'text:display', \
		'text:name', \
),
	'text:variable-input' : ( \
		'office:value-type', \
		'style:data-style-name', \
		'text:description', \
		'text:display', \
		'text:name', \
),
	'text:variable-set' : ( \
		'office:boolean-value', \
		'office:currency', \
		'office:date-value', \
		'office:string-value', \
		'office:time-value', \
		'office:value', \
		'office:value-type', \
		'office:value-type', \
		'office:value-type', \
		'office:value-type', \
		'office:value-type', \
		'office:value-type', \
		'office:value-type', \
		'style:data-style-name', \
		'text:display', \
		'text:formula', \
		'text:name', \
),
	'text:word-count' : ( \
		'style:num-format', \
		'style:num-format', \
		'style:num-letter-sync', \
),
	'xforms:model' : ( \
		'*', \
),
}

