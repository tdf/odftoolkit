Please note that the file lib/ASF/View.pm makes assumptions about the format of mdtext files.
In particular, that the dependencies are all markdown files with subheadings of the form:

# foo ## {#bar} or
# foo ## [#bar]

It also now supports attr_list markup, e.g.

# foo ## {: #bar .class}

[See https://issues.apache.org/jira/browse/INFRA-9705?focusedCommentId=14610257]
