
# /usr/bin/python

CHILD_ELEMENTS = {
	'*' : ( \
		'*', \
),
	'ds:Signature' : ( \
		'*', \
),
	'dsig:document-signatures' : ( \
		'ds:Signature', \
),
}

CHILD_ATTRIBUTES = {
	'*' : ( \
		'*', \
),
	'ds:Signature' : ( \
		'*', \
),
	'dsig:document-signatures' : ( \
		'dsig:version', \
),
}

