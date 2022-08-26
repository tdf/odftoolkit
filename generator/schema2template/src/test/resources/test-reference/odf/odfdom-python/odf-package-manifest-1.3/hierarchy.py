
# /usr/bin/python

CHILD_ELEMENTS = {
	'*' : ( \
		'*', \
),
	'CipherData' : ( \
		'CipherValue', \
),
	'CipherValue' : ( \
),
	'PGPData' : ( \
		'PGPKeyID', \
		'PGPKeyPacket', \
),
	'PGPKeyID' : ( \
),
	'PGPKeyPacket' : ( \
),
	'algorithm' : ( \
		'*', \
),
	'encrypted-key' : ( \
		'CipherData', \
		'encryption-method', \
		'keyinfo', \
),
	'encryption-data' : ( \
		'algorithm', \
		'key-derivation', \
		'start-key-generation', \
),
	'encryption-method' : ( \
),
	'file-entry' : ( \
		'encryption-data', \
),
	'key-derivation' : ( \
),
	'keyinfo' : ( \
		'PGPData', \
),
	'manifest' : ( \
		'encrypted-key', \
		'file-entry', \
),
	'start-key-generation' : ( \
),
}

CHILD_ATTRIBUTES = {
	'*' : ( \
		'*', \
),
	'CipherData' : ( \
),
	'CipherValue' : ( \
),
	'PGPData' : ( \
),
	'PGPKeyID' : ( \
),
	'PGPKeyPacket' : ( \
),
	'algorithm' : ( \
		'algorithm-name', \
		'initialisation-vector', \
),
	'encrypted-key' : ( \
),
	'encryption-data' : ( \
		'checksum', \
		'checksum-type', \
),
	'encryption-method' : ( \
		'PGPAlgorithm', \
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
		'key-derivation-name', \
		'key-size', \
		'salt', \
),
	'keyinfo' : ( \
),
	'manifest' : ( \
		'version', \
),
	'start-key-generation' : ( \
		'key-size', \
		'start-key-generation-name', \
),
}

