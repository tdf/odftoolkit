#!/usr/local/bin/python
#           Licensed to the Apache Software Foundation (ASF) under one
#           or more contributor license agreements.  See the NOTICE file
#           distributed with this work for additional information
#           regarding copyright ownership.  The ASF licenses this file
#           to you under the Apache License, Version 2.0 (the
#           "License"); you may not use this file except in compliance
#           with the License.  You may obtain a copy of the License at
#
#             http://www.apache.org/licenses/LICENSE-2.0
#
#           Unless required by applicable law or agreed to in writing,
#           software distributed under the License is distributed on an
#           "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
#           KIND, either express or implied.  See the License for the
#           specific language governing permissions and limitations
#           under the License.

import socket,sys,os,markdown,select,signal
from markdown.extensions.toc import TocExtension

if "MARKDOWN_SOCKET" not in os.environ:
    print >>sys.stderr, "missing MARKDOWN_SOCKET environment variable"
    sys.exit(1)
path = os.environ["MARKDOWN_SOCKET"]

try: 
    pid = os.fork() 
    if pid > 0:
        sys.exit(0) 
except OSError, e: 
    print >>sys.stderr, "fork #1 failed: %d (%s)" % (e.errno, e.strerror) 
    sys.exit(1)

os.chdir("/") 
os.setsid() 
os.umask(0) 

EXTENSIONS = ['tables', 'addtableclass', 'def_list', TocExtension(permalink=True), 'attr_list',
             'codehilite', 'elementid' , 'footnotes', 'abbr']

# check markdown prereqs outside trapped exceptions
dummy = markdown.markdown(unicode("", "utf-8"), EXTENSIONS)

sys.stdin = open('/dev/null', 'r')
sys.stdout = open('/dev/null', 'w')
sys.stderr = open('/dev/null', 'w')

s = socket.socket(socket.AF_UNIX, socket.SOCK_STREAM)
try:
    os.remove(path)
except OSError:
    pass

s.bind(path)
s.listen(128)
readable = [ s ]
writable = []
data = {}

def reap(signum, frame):
    cpid, cstatus = os.wait()
    return
    
signal.signal(signal.SIGCHLD, reap)
signal.siginterrupt(signal.SIGCHLD, False)

while 1:
    try:
        can_read = []
        can_write = []
        has_error = []
        while 1:
            try:
                can_read, can_write, has_error = select.select(readable, writable, [])
                break
            except:
                pass
                
        for r in can_read:
            if r == s:
                conn, addr = s.accept()
                readable.append(conn)
            else:
                newdata = ''
                while 1:
                    try:
                        newdata = r.recv(8192)
                        break
                    except:
                        pass
                if not newdata:
                    readable.remove(r)
                    writable.append(r)
                elif r in data:
                    data[r] += newdata
                else:
                    data[r]  = newdata

        for w in can_write:
            if w in data:

                # try to fork if there's too much data to process in sequence
                forked = 0
                if len(data[w]) > 100000:
                    try:
                        pid = os.fork()
                        if pid > 0:
                            # do nothing
                            forked = 1
                        else:
                            # in child
                            forked = 2
                    except:
                        pass
                    
                if forked != 1:
                    try:
                        data[w] = markdown.markdown(unicode(data[w], "utf-8"),
                           EXTENSIONS)
                        w.sendall(data[w].encode("utf-8"))
                    except:
                        pass
                        
                if forked == 2:
                    os._exit(0)

                del data[w]
            writable.remove(w)
            w.close()
    except:
        pass
