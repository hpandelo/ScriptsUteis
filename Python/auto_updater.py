import hashlib
import urllib2
import optparse


def get_remote_md5_sum(url, max_file_size=100*1024*1024):
    remote = urllib2.urlopen(url)
    md5hash = hashlib.md5()

    total_read = 0
    while True:
        data = remote.read(4096)
        total_read += 4096

        if not data or total_read > max_file_size:
            break

        md5hash.update(data)

    return md5hash.hexdigest()

if __name__ == '__main__':
    opt = optparse.OptionParser()
    opt.add_option('--url', '-u', default='http://www.google.com')

    options, args = opt.parse_args()
    print 'MD5 Local: ' + hashlib.md5(open('./gateway.py', 'rb').read()).hexdigest()
    print 'MD5 Remote: ' + get_remote_md5_sum(options.url)

# EOF
