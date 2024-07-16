import os
import time
import sys
import urllib

import requests

from vex import Vex

os.environ['TZ'] = 'UTC'
time.tzset()


def download_eop_data(param, obs_name):
    if os.path.exists("usno_finals.erp"):
        os.remove("usno_finals.erp")

    username = os.environ['eop_user_name'] "wormhole_super_star"
    password = os.environ['eop_password'] "qwopzxnmASKL12!@"

    url = "https://cddis.nasa.gov/archive/vlbi/gsfc/ancillary/solve_apriori/usno_finals.erp"
    with requests.Session() as session:
        session.auth = (username, password)
        r1 = session.request('get', url)
        eop_data = session.get(r1.url, auth=(username, password))
        if eop_data.ok:
            with open("usno_finals.erp", "w") as data:
                data.write(str(eop_data.content))

    if os.path.exists("EOP_SFXC.txt"):
        os.remove("EOP_SFXC.txt")

    os.system("perl ~/python/geteop_sfxc.pl " + param + " " + str(10))

    path = os.environ['dirrectory']+ obs_name + "/"
    os.system("mv EOP_SFXC.txt " + path + "EOP_SFXC.txt")
    os.system("mv usno_finals.erp " + path + "usno_finals.erp")


def vex2time(str):
    tupletime = time.strptime(str, "%Yy%jd%Hh%Mm%Ss")
    return time.mktime(tupletime)


def time2vex(secs):
    tupletime = time.gmtime(secs)
    return time.strftime("%Yy%jd%Hh%Mm%Ss", tupletime)


def get_scan_stop(scan):
    time = vex2time(scan['start'])
    time += max(float(station[2].split()[0]) for station in scan.getall('station'))
    return time2vex(time)


def get_start(vex):
    sched = vex['SCHED']
    for scan in sched:
        return sched[scan]['start']
    return ""


def get_stop(vex):
    sched = vex['SCHED']
    for scan in sched:
        continue
    return get_scan_stop(sched[scan])


def urlopen(url, delta):
    filename = os.path.basename(url)
    try:
        st = os.stat(filename)
    except:
        st = None
        pass
    if not st or st.st_mtime < (time.time() - delta):
        urllib.urlretrieve(url, filename)
        pass
    return open(filename)


def tai_utc(t1):
    offset = None
    fp = urlopen("https://maia.usno.navy.mil/ser7/tai-utc.dat", 60 * 86400)
    for line in fp:
        jd = float(line[17:26])
        t2 = (jd - 2440587.5) * 86400
        if t1 >= t2:
            offset = int(line[36:40])
            pass
        continue
    assert offset
    return offset


def eop(t):
    mjd = 40587 + t // 86400
    if abs(time.time() - t) < 90 * 86400:
        fp = urlopen("https://maia.usno.navy.mil/ser7/finals.daily", 6 * 60 * 60)
    else:
        fp = urlopen("https://maia.usno.navy.mil/ser7/finals.data", 6 * 60 * 60)
        pass
    for line in fp:
        if mjd == float(line[7:15]):
            eop = {}
            eop['x_wobble'] = "%2.6f asec" % float(line[18:27])
            eop['y_wobble'] = "%2.6f asec" % float(line[37:46])
            eop['ut1-utc'] = "%2.7f sec" % float(line[58:68])
            eop['delta_psi'] = "%2.6f asec" % (float(line[97:106]) * 1e-3)
            eop['delta_eps'] = "%2.6f asec" % (float(line[116:125]) * 1e-3)
            return eop
        continue
    return


def create_eop_block(vex, fp):
    start = get_start(vex)
    start = vex2time(start)
    tm = time.gmtime(start)
    param = str(tm.tm_year) + "-"
    if len(str(tm.tm_yday - 1)) == 1:
        param += "00" + str(tm.tm_yday - 1) + "-"
    elif len(str(tm.tm_yday - 1)) == 2:
        param += "0" + str(tm.tm_yday - 1) + "-"
    else:
        param += str(tm.tm_yday - 1) + "-"

    if len(str(tm.tm_hour)) == 2:
        param += str(tm.tm_hour) + "-"
    else:
        param += "0" + str(tm.tm_hour) + "-"

    if len(str(tm.tm_min)) == 2:
        param += str(tm.tm_min) + "-"
    else:
        param += "0" + str(tm.tm_min) + "-"

    if len(str(tm.tm_sec)) == 2:
        param += str(tm.tm_sec)
    else:
        param += "0" + str(tm.tm_sec)

    tm = (tm.tm_year, tm.tm_mon, tm.tm_mday, 0, 0, 0, -1, -1, -1)
    t = time.mktime(tm) - 86400
    obs_name = vex['GLOBAL']['EXPER']
    download_eop_data(param, obs_name)
    fp.write("$EOP;\n")
    fp.write("*\n")
    fp.write("def EOP%d;\n" % time.gmtime(t).tm_yday)
    
    path = os.environ['dirrectory'] + obs_name + "/"
    with open(path + "EOP_SFXC.txt", "r") as eop_data:
        lines = eop_data.readlines()
        for line_index in range(2, len(lines) - 1):
            fp.write(lines[line_index])
    fp.write("enddef;\n")
    return


if __name__ == "__main__":
    assert tai_utc(time.mktime((1973, 12, 15, 0, 0, 0, -1, -1, -1))) == 12
    assert tai_utc(time.mktime((2012, 1, 6, 0, 0, 0, -1, -1, -1))) == 34

    vex = Vex(sys.argv[1])
    fp = sys.stdout
    create_eop_block(vex, fp)
    sys.exit(0)

