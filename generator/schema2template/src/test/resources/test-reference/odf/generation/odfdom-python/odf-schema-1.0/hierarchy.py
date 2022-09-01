
# /usr/bin/python

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
	'chart:data-point' : ( \
),
	'chart:domain' : ( \
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
	'chart:legend' : ( \
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
),
	'chart:series' : ( \
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
	'dc:creator' : ( \
),
	'dc:date' : ( \
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
),
	'dr3d:sphere' : ( \
),
	'draw:a' : ( \
		'draw:frame', \
),
	'draw:applet' : ( \
		'draw:param', \
),
	'draw:area-circle' : ( \
		'office:event-listeners', \
		'svg:desc', \
),
	'draw:area-polygon' : ( \
		'office:event-listeners', \
		'svg:desc', \
),
	'draw:area-rectangle' : ( \
		'office:event-listeners', \
		'svg:desc', \
),
	'draw:caption' : ( \
		'draw:glue-point', \
		'office:event-listeners', \
		'text:list', \
		'text:p', \
),
	'draw:circle' : ( \
		'draw:glue-point', \
		'office:event-listeners', \
		'text:list', \
		'text:p', \
),
	'draw:connector' : ( \
		'draw:glue-point', \
		'office:event-listeners', \
		'text:list', \
		'text:p', \
),
	'draw:contour-path' : ( \
),
	'draw:contour-polygon' : ( \
),
	'draw:control' : ( \
		'draw:glue-point', \
),
	'draw:custom-shape' : ( \
		'draw:enhanced-geometry', \
		'draw:glue-point', \
		'office:event-listeners', \
		'text:list', \
		'text:p', \
),
	'draw:ellipse' : ( \
		'draw:glue-point', \
		'office:event-listeners', \
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
),
	'draw:g' : ( \
		'dr3d:scene', \
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
),
	'draw:layer-set' : ( \
		'draw:layer', \
),
	'draw:line' : ( \
		'draw:glue-point', \
		'office:event-listeners', \
		'text:list', \
		'text:p', \
),
	'draw:marker' : ( \
),
	'draw:measure' : ( \
		'draw:glue-point', \
		'office:event-listeners', \
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
		'presentation:animations', \
		'presentation:notes', \
),
	'draw:page-thumbnail' : ( \
),
	'draw:param' : ( \
),
	'draw:path' : ( \
		'draw:glue-point', \
		'office:event-listeners', \
		'text:list', \
		'text:p', \
),
	'draw:plugin' : ( \
		'draw:param', \
),
	'draw:polygon' : ( \
		'draw:glue-point', \
		'office:event-listeners', \
		'text:list', \
		'text:p', \
),
	'draw:polyline' : ( \
		'draw:glue-point', \
		'office:event-listeners', \
		'text:list', \
		'text:p', \
),
	'draw:rect' : ( \
		'draw:glue-point', \
		'office:event-listeners', \
		'text:list', \
		'text:p', \
),
	'draw:regular-polygon' : ( \
		'draw:glue-point', \
		'office:event-listeners', \
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
	'meta:date-string' : ( \
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
		'*', \
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
		'style:default-style', \
		'style:presentation-page-layout', \
		'style:style', \
		'svg:linearGradient', \
		'svg:radialGradient', \
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
	'style:chart-properties' : ( \
		'*', \
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
		'*', \
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
		'text:user-field-decls', \
		'text:user-index', \
		'text:variable-decls', \
),
	'style:footer-style' : ( \
		'style:header-footer-properties', \
),
	'style:graphic-properties' : ( \
		'*', \
),
	'style:handout-master' : ( \
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
		'text:user-field-decls', \
		'text:user-index', \
		'text:variable-decls', \
),
	'style:header-footer-properties' : ( \
		'*', \
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
		'text:user-field-decls', \
		'text:user-index', \
		'text:variable-decls', \
),
	'style:header-style' : ( \
		'style:header-footer-properties', \
),
	'style:list-level-properties' : ( \
		'*', \
),
	'style:map' : ( \
),
	'style:master-page' : ( \
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
		'office:forms', \
		'presentation:notes', \
		'style:footer', \
		'style:footer-left', \
		'style:header', \
		'style:header-left', \
		'style:style', \
),
	'style:page-layout' : ( \
		'style:footer-style', \
		'style:header-style', \
		'style:page-layout-properties', \
),
	'style:page-layout-properties' : ( \
		'*', \
),
	'style:paragraph-properties' : ( \
		'*', \
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
		'*', \
),
	'style:section-properties' : ( \
		'*', \
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
	'style:table-cell-properties' : ( \
		'*', \
),
	'style:table-column-properties' : ( \
		'*', \
),
	'style:table-properties' : ( \
		'*', \
),
	'style:table-row-properties' : ( \
		'*', \
),
	'style:text-properties' : ( \
		'*', \
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
	'table:detective' : ( \
		'table:highlighted-range', \
		'table:operation', \
),
	'table:error-macro' : ( \
),
	'table:error-message' : ( \
		'text:p', \
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
),
	'table:filter-or' : ( \
		'table:filter-and', \
		'table:filter-condition', \
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
	'table:operation' : ( \
),
	'table:previous' : ( \
		'table:change-track-table-cell', \
),
	'table:scenario' : ( \
),
	'table:shapes' : ( \
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
),
	'table:table-rows' : ( \
		'table:table-row', \
),
	'table:table-source' : ( \
),
	'table:target-range-address' : ( \
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
		'office:event-listeners', \
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
),
	'text:list-item' : ( \
		'text:h', \
		'text:list', \
		'text:number', \
		'text:p', \
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
		'smil:accumulate', \
		'smil:additive', \
		'smil:attributeName', \
		'smil:by', \
		'smil:calcMode', \
		'smil:fill', \
		'smil:from', \
		'smil:keySplines', \
		'smil:keyTimes', \
		'smil:repeatCount', \
		'smil:repeatDur', \
		'smil:targetElement', \
		'smil:to', \
		'smil:values', \
),
	'anim:animateColor' : ( \
		'anim:color-interpolation', \
		'anim:color-interpolation-direction', \
		'anim:formula', \
		'anim:sub-item', \
		'smil:accumulate', \
		'smil:additive', \
		'smil:attributeName', \
		'smil:by', \
		'smil:calcMode', \
		'smil:fill', \
		'smil:from', \
		'smil:keySplines', \
		'smil:keyTimes', \
		'smil:targetElement', \
		'smil:to', \
		'smil:values', \
),
	'anim:animateMotion' : ( \
		'anim:formula', \
		'anim:sub-item', \
		'smil:accumulate', \
		'smil:additive', \
		'smil:attributeName', \
		'smil:by', \
		'smil:calcMode', \
		'smil:fill', \
		'smil:from', \
		'smil:keySplines', \
		'smil:keyTimes', \
		'smil:targetElement', \
		'smil:to', \
		'smil:values', \
		'svg:origin', \
		'svg:path', \
),
	'anim:animateTransform' : ( \
		'anim:formula', \
		'anim:sub-item', \
		'smil:accumulate', \
		'smil:additive', \
		'smil:attributeName', \
		'smil:by', \
		'smil:fill', \
		'smil:from', \
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
		'smil:repeatCount', \
		'smil:repeatDur', \
		'xlink:href', \
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
),
	'anim:iterate' : ( \
		'anim:id', \
		'anim:iterate-interval', \
		'anim:iterate-type', \
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
),
	'anim:set' : ( \
		'anim:sub-item', \
		'smil:accumulate', \
		'smil:additive', \
		'smil:attributeName', \
		'smil:fill', \
		'smil:targetElement', \
		'smil:to', \
),
	'anim:transitionFilter' : ( \
		'anim:formula', \
		'anim:sub-item', \
		'smil:accumulate', \
		'smil:additive', \
		'smil:by', \
		'smil:calcMode', \
		'smil:direction', \
		'smil:fadeColor', \
		'smil:fill', \
		'smil:from', \
		'smil:mode', \
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
),
	'chart:data-point' : ( \
		'chart:repeated', \
		'chart:style-name', \
),
	'chart:domain' : ( \
		'table:cell-range-address', \
),
	'chart:error-indicator' : ( \
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
	'dc:creator' : ( \
),
	'dc:date' : ( \
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
),
	'draw:a' : ( \
		'office:name', \
		'office:server-map', \
		'office:target-frame-name', \
		'xlink:actuate', \
		'xlink:href', \
		'xlink:show', \
		'xlink:type', \
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
),
	'draw:circle' : ( \
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
),
	'draw:connector' : ( \
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
),
	'draw:custom-shape' : ( \
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
),
	'draw:ellipse' : ( \
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
),
	'draw:frame' : ( \
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
),
	'draw:g' : ( \
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
),
	'draw:glue-point' : ( \
		'draw:align', \
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
),
	'draw:marker' : ( \
		'draw:display-name', \
		'draw:name', \
		'svg:d', \
		'svg:viewBox', \
),
	'draw:measure' : ( \
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
),
	'draw:object' : ( \
		'draw:notify-on-update-of-ranges', \
		'xlink:actuate', \
		'xlink:href', \
		'xlink:show', \
		'xlink:type', \
),
	'draw:object-ole' : ( \
		'draw:class-id', \
		'xlink:actuate', \
		'xlink:href', \
		'xlink:show', \
		'xlink:type', \
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
		'draw:style-name', \
		'presentation:presentation-page-layout-name', \
		'presentation:use-date-time-name', \
		'presentation:use-footer-name', \
		'presentation:use-header-name', \
),
	'draw:page-thumbnail' : ( \
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
),
	'draw:param' : ( \
		'draw:name', \
		'draw:value', \
),
	'draw:path' : ( \
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
),
	'draw:plugin' : ( \
		'draw:mime-type', \
		'xlink:actuate', \
		'xlink:href', \
		'xlink:show', \
		'xlink:type', \
),
	'draw:polygon' : ( \
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
),
	'draw:polyline' : ( \
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
),
	'draw:rect' : ( \
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
),
	'draw:regular-polygon' : ( \
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
),
	'form:button' : ( \
		'form:button-type', \
		'form:control-implementation', \
		'form:default-button', \
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
		'form:tab-index', \
		'form:tab-stop', \
		'form:title', \
		'form:toggle', \
		'form:value', \
		'form:xforms-submission', \
		'office:target-frame', \
		'xforms:bind', \
		'xlink:href', \
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
		'form:name', \
		'form:printable', \
		'form:state', \
		'form:tab-index', \
		'form:tab-stop', \
		'form:title', \
		'form:value', \
		'form:visual-effect', \
		'xforms:bind', \
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
		'form:list-source', \
		'form:list-source-type', \
		'form:max-length', \
		'form:name', \
		'form:printable', \
		'form:readonly', \
		'form:size', \
		'form:tab-index', \
		'form:tab-stop', \
		'form:title', \
		'form:value', \
		'xforms:bind', \
),
	'form:connection-resource' : ( \
		'xlink:href', \
),
	'form:date' : ( \
		'form:control-implementation', \
		'form:convert-empty-to-null', \
		'form:current-value', \
		'form:data-field', \
		'form:disabled', \
		'form:id', \
		'form:max-length', \
		'form:max-value', \
		'form:min-value', \
		'form:name', \
		'form:printable', \
		'form:readonly', \
		'form:tab-index', \
		'form:tab-stop', \
		'form:title', \
		'form:value', \
		'xforms:bind', \
),
	'form:file' : ( \
		'form:control-implementation', \
		'form:current-value', \
		'form:disabled', \
		'form:id', \
		'form:max-length', \
		'form:name', \
		'form:printable', \
		'form:readonly', \
		'form:tab-index', \
		'form:tab-stop', \
		'form:title', \
		'form:value', \
		'xforms:bind', \
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
		'form:disabled', \
		'form:id', \
		'form:max-length', \
		'form:max-value', \
		'form:min-value', \
		'form:name', \
		'form:printable', \
		'form:readonly', \
		'form:tab-index', \
		'form:tab-stop', \
		'form:title', \
		'form:validation', \
		'form:value', \
		'xforms:bind', \
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
),
	'form:generic-control' : ( \
		'form:control-implementation', \
		'form:id', \
		'form:name', \
		'xforms:bind', \
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
),
	'form:hidden' : ( \
		'form:control-implementation', \
		'form:id', \
		'form:name', \
		'form:value', \
		'xforms:bind', \
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
		'office:currency', \
		'office:value', \
),
	'form:list-value' : ( \
		'office:boolean-value', \
),
	'form:list-value' : ( \
		'office:time-value', \
),
	'form:list-value' : ( \
		'office:date-value', \
),
	'form:list-value' : ( \
		'office:value', \
),
	'form:list-value' : ( \
		'office:value', \
),
	'form:list-value' : ( \
		'office:string-value', \
),
	'form:listbox' : ( \
		'form:bound-column', \
		'form:control-implementation', \
		'form:data-field', \
		'form:disabled', \
		'form:dropdown', \
		'form:id', \
		'form:list-source', \
		'form:list-source-type', \
		'form:multiple', \
		'form:name', \
		'form:printable', \
		'form:size', \
		'form:tab-index', \
		'form:tab-stop', \
		'form:title', \
		'form:xforms-list-source', \
		'xforms:bind', \
),
	'form:number' : ( \
		'form:control-implementation', \
		'form:convert-empty-to-null', \
		'form:current-value', \
		'form:data-field', \
		'form:disabled', \
		'form:id', \
		'form:max-length', \
		'form:max-value', \
		'form:min-value', \
		'form:name', \
		'form:printable', \
		'form:readonly', \
		'form:tab-index', \
		'form:tab-stop', \
		'form:title', \
		'form:value', \
		'xforms:bind', \
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
		'form:max-length', \
		'form:name', \
		'form:printable', \
		'form:tab-index', \
		'form:tab-stop', \
		'form:title', \
		'form:value', \
		'xforms:bind', \
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
		'form:name', \
		'form:printable', \
		'form:selected', \
		'form:tab-index', \
		'form:tab-stop', \
		'form:title', \
		'form:value', \
		'form:visual-effect', \
		'xforms:bind', \
),
	'form:text' : ( \
		'form:control-implementation', \
		'form:convert-empty-to-null', \
		'form:current-value', \
		'form:data-field', \
		'form:disabled', \
		'form:id', \
		'form:max-length', \
		'form:name', \
		'form:printable', \
		'form:readonly', \
		'form:tab-index', \
		'form:tab-stop', \
		'form:title', \
		'form:value', \
		'xforms:bind', \
),
	'form:textarea' : ( \
		'form:control-implementation', \
		'form:convert-empty-to-null', \
		'form:current-value', \
		'form:data-field', \
		'form:disabled', \
		'form:id', \
		'form:max-length', \
		'form:name', \
		'form:printable', \
		'form:readonly', \
		'form:tab-index', \
		'form:tab-stop', \
		'form:title', \
		'form:value', \
		'xforms:bind', \
),
	'form:time' : ( \
		'form:control-implementation', \
		'form:convert-empty-to-null', \
		'form:current-value', \
		'form:data-field', \
		'form:disabled', \
		'form:id', \
		'form:max-length', \
		'form:max-value', \
		'form:min-value', \
		'form:name', \
		'form:printable', \
		'form:readonly', \
		'form:tab-index', \
		'form:tab-stop', \
		'form:title', \
		'form:value', \
		'xforms:bind', \
),
	'form:value-range' : ( \
		'form:control-implementation', \
		'form:delay-for-repeat', \
		'form:disabled', \
		'form:id', \
		'form:max-value', \
		'form:min-value', \
		'form:name', \
		'form:orientation', \
		'form:page-step-size', \
		'form:printable', \
		'form:step-size', \
		'form:tab-index', \
		'form:tab-stop', \
		'form:title', \
		'form:value', \
		'xforms:bind', \
),
	'math:math' : ( \
		'*', \
),
	'meta:date-string' : ( \
),
	'number:am-pm' : ( \
),
	'number:boolean' : ( \
),
	'number:boolean-style' : ( \
		'number:country', \
		'number:language', \
		'number:title', \
		'number:transliteration-country', \
		'number:transliteration-format', \
		'number:transliteration-language', \
		'number:transliteration-style', \
		'style:name', \
		'style:volatile', \
),
	'number:currency-style' : ( \
		'number:automatic-order', \
		'number:country', \
		'number:language', \
		'number:title', \
		'number:transliteration-country', \
		'number:transliteration-format', \
		'number:transliteration-language', \
		'number:transliteration-style', \
		'style:name', \
		'style:volatile', \
),
	'number:currency-symbol' : ( \
		'number:country', \
		'number:language', \
),
	'number:date-style' : ( \
		'number:automatic-order', \
		'number:country', \
		'number:format-source', \
		'number:language', \
		'number:title', \
		'number:transliteration-country', \
		'number:transliteration-format', \
		'number:transliteration-language', \
		'number:transliteration-style', \
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
		'number:title', \
		'number:transliteration-country', \
		'number:transliteration-format', \
		'number:transliteration-language', \
		'number:transliteration-style', \
		'style:name', \
		'style:volatile', \
),
	'number:percentage-style' : ( \
		'number:country', \
		'number:language', \
		'number:title', \
		'number:transliteration-country', \
		'number:transliteration-format', \
		'number:transliteration-language', \
		'number:transliteration-style', \
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
		'number:title', \
		'number:transliteration-country', \
		'number:transliteration-format', \
		'number:transliteration-language', \
		'number:transliteration-style', \
		'style:name', \
		'style:volatile', \
),
	'number:time-style' : ( \
		'number:country', \
		'number:format-source', \
		'number:language', \
		'number:title', \
		'number:transliteration-country', \
		'number:transliteration-format', \
		'number:transliteration-language', \
		'number:transliteration-style', \
		'number:truncate-on-overflow', \
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
	'office:dde-source' : ( \
		'office:automatic-update', \
		'office:conversion-mode', \
		'office:dde-application', \
		'office:dde-item', \
		'office:dde-topic', \
		'office:name', \
),
	'office:document' : ( \
		'office:mimetype', \
		'office:version', \
),
	'office:document-content' : ( \
		'office:version', \
),
	'office:document-meta' : ( \
		'office:version', \
),
	'office:document-settings' : ( \
		'office:version', \
),
	'office:document-styles' : ( \
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
		'table:structure-protected', \
),
	'office:styles' : ( \
),
	'office:text' : ( \
		'text:global', \
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
),
	'script:event-listener' : ( \
		'script:event-name', \
		'script:language', \
		'script:macro-name', \
		'xlink:actuate', \
		'xlink:href', \
		'xlink:type', \
),
	'style:chart-properties' : ( \
		'*', \
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
		'*', \
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
	'style:graphic-properties' : ( \
		'*', \
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
		'*', \
),
	'style:header-left' : ( \
		'style:display', \
),
	'style:header-style' : ( \
),
	'style:list-level-properties' : ( \
		'*', \
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
		'*', \
),
	'style:paragraph-properties' : ( \
		'*', \
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
		'*', \
),
	'style:section-properties' : ( \
		'*', \
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
		'style:list-style-name', \
		'style:master-page-name', \
		'style:name', \
		'style:next-style-name', \
		'style:parent-style-name', \
),
	'style:table-cell-properties' : ( \
		'*', \
),
	'style:table-column-properties' : ( \
		'*', \
),
	'style:table-properties' : ( \
		'*', \
),
	'style:table-row-properties' : ( \
		'*', \
),
	'style:text-properties' : ( \
		'*', \
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
		'name', \
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
	'table:calculation-settings' : ( \
		'table:automatic-find-labels', \
		'table:case-sensitive', \
		'table:null-year', \
		'table:precision-as-shown', \
		'table:search-criteria-must-apply-to-whole-cell', \
		'table:use-regular-expressions', \
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
		'table:style-name', \
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
		'table:date-value-type', \
		'table:value-type', \
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
		'table:language', \
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
		'table:style-name', \
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
		'table:style-name', \
),
	'table:table-column' : ( \
		'table:default-cell-style-name', \
		'table:number-columns-repeated', \
		'table:style-name', \
		'table:visibility', \
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
	'table:tracked-changes' : ( \
		'table:track-changes', \
),
	'text:a' : ( \
		'office:name', \
		'office:target-frame-name', \
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
		'text:style-name', \
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
		'text:style-name', \
),
	'text:bibliography-configuration' : ( \
		'fo:country', \
		'fo:language', \
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
		'text:style-name', \
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
		'text:style-name', \
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
		'text:continue-numbering', \
		'text:style-name', \
),
	'text:list-header' : ( \
),
	'text:list-item' : ( \
		'text:start-value', \
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
		'text:start-value', \
		'text:style-name', \
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
		'text:style-name', \
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
),
	'text:p' : ( \
		'text:class-names', \
		'text:cond-style-name', \
		'text:id', \
		'text:style-name', \
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
		'text:style-name', \
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
		'text:style-name', \
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
		'text:style-name', \
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
		'text:style-name', \
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
		'text:outline-level', \
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

