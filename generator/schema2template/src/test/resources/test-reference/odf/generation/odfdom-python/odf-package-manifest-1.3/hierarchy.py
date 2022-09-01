
# /usr/bin/python

CHILD_ELEMENTS = {
	'*' : ( \
		'*', \
),
	'manifest:CipherData' : ( \
		'manifest:CipherValue', \
),
	'manifest:CipherValue' : ( \
),
	'manifest:PGPData' : ( \
		'manifest:PGPKeyID', \
		'manifest:PGPKeyPacket', \
),
	'manifest:PGPKeyID' : ( \
),
	'manifest:PGPKeyPacket' : ( \
),
	'manifest:algorithm' : ( \
		'*', \
),
	'manifest:encrypted-key' : ( \
		'manifest:CipherData', \
		'manifest:encryption-method', \
		'manifest:keyinfo', \
),
	'manifest:encryption-data' : ( \
		'manifest:algorithm', \
		'manifest:key-derivation', \
		'manifest:start-key-generation', \
),
	'manifest:encryption-method' : ( \
),
	'manifest:file-entry' : ( \
		'manifest:encryption-data', \
),
	'manifest:key-derivation' : ( \
),
	'manifest:keyinfo' : ( \
		'manifest:PGPData', \
),
	'manifest:manifest' : ( \
		'manifest:encrypted-key', \
		'manifest:file-entry', \
),
	'manifest:start-key-generation' : ( \
),
}

CHILD_ATTRIBUTES = {
	'*' : ( \
		'*', \
),
	'manifest:CipherData' : ( \
),
	'manifest:CipherValue' : ( \
),
	'manifest:PGPData' : ( \
),
	'manifest:PGPKeyID' : ( \
),
	'manifest:PGPKeyPacket' : ( \
),
	'manifest:algorithm' : ( \
		'manifest:algorithm-name', \
		'manifest:initialisation-vector', \
),
	'manifest:encrypted-key' : ( \
),
	'manifest:encryption-data' : ( \
		'manifest:checksum', \
		'manifest:checksum-type', \
),
	'manifest:encryption-method' : ( \
		'manifest:PGPAlgorithm', \
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
		'manifest:key-derivation-name', \
		'manifest:key-size', \
		'manifest:salt', \
),
	'manifest:keyinfo' : ( \
),
	'manifest:manifest' : ( \
		'manifest:version', \
),
	'manifest:start-key-generation' : ( \
		'manifest:key-size', \
		'manifest:start-key-generation-name', \
),
}

