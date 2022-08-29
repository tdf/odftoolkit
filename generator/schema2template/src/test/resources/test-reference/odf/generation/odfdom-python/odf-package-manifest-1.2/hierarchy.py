
# /usr/bin/python

CHILD_ELEMENTS = {
	'*' : ( \
		'*', \
),
	'algorithm' : ( \
		'*', \
),
	'encryption-data' : ( \
		'algorithm', \
		'key-derivation', \
		'start-key-generation', \
),
	'file-entry' : ( \
		'encryption-data', \
),
	'key-derivation' : ( \
),
	'manifest' : ( \
		'file-entry', \
),
	'start-key-generation' : ( \
),
}

CHILD_ATTRIBUTES = {
	'*' : ( \
		'*', \
),
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
		'preferred-view-mode', \
		'size', \
		'version', \
),
	'key-derivation' : ( \
		'iteration-count', \
		'key-derivation-name', \
		'key-size', \
		'salt', \
),
	'manifest' : ( \
		'version', \
),
	'start-key-generation' : ( \
		'key-size', \
		'start-key-generation-name', \
),
}

