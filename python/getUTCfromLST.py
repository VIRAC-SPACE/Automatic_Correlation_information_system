import datetime
import math

# Constants
DEG_TO_RAD = math.pi / 180

# Clock class to handle time calculations
class Clock:
    def __init__(self):
        self.longitude = 0

    def juliandate(self, now):
        y = now.year
        m = now.month
        d = now.day
        if m == 1 or m == 2:
            y -= 1
            m += 12
        A = math.floor(y / 100)
        B = 2 - A + math.floor(A / 4)
        C = math.floor(365.25 * y)
        D = math.floor(30.6001 * (m + 1))
        return B + C + D + d + 1720994.5

    def gst(self, jd, dechours):
        S = jd - 2451545
        T = S / 36525
        T0 = 6.697374558 + (2400.051336 * T) + (0.000025862 * T ** 2)
        if T0 < 0:
            T0 = T0 + (24 * abs(math.floor(T0 / 24)))
        else:
            T0 = T0 - (24 * abs(math.floor(T0 / 24)))
        T0 = T0 + (dechours * 1.002737909)
        if T0 < 0:
            T0 = T0 + 24
        if T0 > 24:
            T0 = T0 - 24
        return T0

    def lst(self, gstime):
        utcdiff = abs(self.longitude) / 15
        lstime = gstime + utcdiff if self.longitude > 0 else gstime - utcdiff
        if lstime > 24:
            lstime = lstime - 24
        if lstime < 0:
            lstime = lstime + 24
        return lstime

    def get_now(self):
        now = datetime.datetime.utcnow()
        nowjd = self.juliandate(now)
        nowdechours = self.decimal_hours(now)
        gstime = self.gst(nowjd, nowdechours)
        lstime = self.lst(gstime)
        return now, lstime

    def decimal_hours(self, now):
        return (((now.second / 60) + now.minute) / 60) + now.hour

# Main loop
def main():
    clock = Clock()

    while True:
        now, lstime = clock.get_now()
        print(f"LST: {lstime}")
        # Šeit ievietot kodu, kas izmanto `lstime` pēc nepieciešamības
        # ...

if __name__ == "__main__":
    main()
