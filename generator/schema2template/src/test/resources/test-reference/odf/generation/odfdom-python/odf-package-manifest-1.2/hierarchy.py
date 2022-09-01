
# /usr/bin/python

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

