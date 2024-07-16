import sys
import time
import os

os.environ['TZ'] = 'UTC'
time.tzset()


def vex2time(str):
    tupletime = time.strptime(str, "%Yy%jd%Hh%Mm%Ss")
    return time.mktime(tupletime)


def time2vex(secs):
    tupletime = time.gmtime(secs)
    return time.strftime("%Yy%jd%Hh%Mm%Ss", tupletime)


def get_mid(start, stop):
    start = vex2time (start)
    stop = vex2time (stop)
    mid = start + (stop - start) / 2
    return time2vex(mid)


if __name__ == "__main__":
    start = sys.argv[1]
    stop = sys.argv[2]
    
    print(get_mid(start, stop))
    sys.exit(0)

