
# /usr/bin/python

CHILD_ELEMENTS = {
	'manifest:algorithm' : ( \
),
	'manifest:encryption-data' : ( \
		'manifest:algorithm', \
		'manifest:key-derivation', \
),
	'manifest:file-entry' : ( \
		'manifest:encryption-data', \
),
	'manifest:key-derivation' : ( \
),
	'manifest:manifest' : ( \
		'manifest:file-entry', \
),
}

CHILD_ATTRIBUTES = {
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
		'manifest:size', \
),
	'manifest:key-derivation' : ( \
		'manifest:iteration-count', \
		'manifest:key-derivation-name', \
		'manifest:salt', \
),
	'manifest:manifest' : ( \
),
}

