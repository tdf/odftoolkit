
# /usr/bin/python

CHILD_ELEMENTS = {
	'algorithm' : ( \
),
	'encryption-data' : ( \
		'algorithm', \
		'key-derivation', \
),
	'file-entry' : ( \
		'encryption-data', \
),
	'key-derivation' : ( \
),
	'manifest' : ( \
		'file-entry', \
),
}

CHILD_ATTRIBUTES = {
	'algorithm' : ( \
		'algorithm-name', \
		'initialisation-vector', \
),
	'encryption-data' : ( \
		'checksum', \
		'checksum-type', \
),
	'file-entry' : ( \
		'full-path', \
		'media-type', \
		'size', \
),
	'key-derivation' : ( \
		'iteration-count', \
		'key-derivation-name', \
		'salt', \
),
	'manifest' : ( \
),
}

