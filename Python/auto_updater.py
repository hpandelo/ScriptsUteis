"""
Helcio Macedo
Checksum Verifier v1.0 + Auto Updater v1.0
https://github.com/neomacedo/ScriptsUteis
-----------------------------------------------------------
Script used to compare if local file its the same as remote
If not the same.. Download and write the new file on local directory
"""

import hashlib
import urllib2
import optparse

# Remote address to file
remote_url = 'https://raw.githubusercontent.com/neomacedo/Area51/master/arquivo_teste.txt'

# Local address to the file
local_url = '../../GitHub/Area51/arquivo_teste.txt'


# Method who will return md5 Checksum [Local]
def get_local_md5_sum(url):
    try:
        return hashlib.md5(open(local_url, 'a+b').read()).hexdigest()

    except Exception as ex:
        print 'Failed to get remote file checksum! \n Exception: ' + str(ex.message)


# Method who will return md5 Checksum [Remote]
def get_remote_md5_sum(url):
    try:
        # Parse Options
        opt = optparse.OptionParser()
        opt.add_option('--url', '-u', default=remote_url)
        options, args = opt.parse_args()

        remote = urllib2.urlopen(options.url)
        md5hash = hashlib.md5()

        data = remote.read()
        md5hash.update(data)

        return md5hash.hexdigest()

    except Exception as ex:
        print 'Failed to get remote file checksum! \n Exception: ' + str(ex.message)


# Method who will update file to the same version of remote URL
def update_local_file():
    try:
        # Parse Options
        opt = optparse.OptionParser()
        opt.add_option('--url', '-u', default=remote_url)
        options, args = opt.parse_args()

        remote = urllib2.urlopen(options.url)

        data = remote.read()

        open(local_url, 'w+').write(data)

        print 'File updated'
        print 'MD5 Local: ' + get_local_md5_sum(local_url)

    except Exception as ex:
        print 'Failed to update file! \n Exception: ' + str(ex.message)


# Main Method
if __name__ == '__main__':
    print 'MD5 Local: ' + get_local_md5_sum(local_url)
    print 'MD5 Remote: ' + get_remote_md5_sum(remote_url)

    if get_local_md5_sum(local_url) == get_remote_md5_sum(remote_url):
        print 'File up-to-date'
    else:
        print 'Updating File....'
        update_local_file()

    # If you want to open your new file (e.g: gateway.py) .. uncomment these lines:
    # import os
    # os.system("gateway.py 1")
    #
    # Or just import it (if located on the same folder as this)
    # import gateway

# EOF
