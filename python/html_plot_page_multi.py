import os
import sys
from bs4 import BeautifulSoup
import urllib


def main(experiment_name, scan_nr, pass_type):
    hpc_ip = os.environ['hpc_ip']
    server_port = os.environ['SERVER_PORT']

    fringe_plot_directory = os.environ['dirrectory'] + experiment_name + "/" + "/results/" + \
                            pass_type + "/fringe/" + scan_nr + "/" + "index.html"

    index_html = urllib.urlopen(fringe_plot_directory).read()
    soup = BeautifulSoup(index_html, "lxml")
    plots = soup.find_all("a")
    image = soup.find_all("img")
    plots[0]["href"] = "https://" + hpc_ip + ":" + server_port + "/vex/returnvexfile"

    if len(plots) > 0:
        for i in range(1, len(plots)):
            plots[i]["href"] = "https://" + hpc_ip + ":" + server_port + "/results/getplotmulti?name=" + plots[i]["href"] + \
                               "&passType=" + pass_type + "&scan=" + scan_nr
            plots[i]["class"] = "images"

    if len(image) > 0:
        image[0]["src"] = "https://" + hpc_ip + ":" + server_port + "/results/getplotmulti?name=" + image[0]["src"] + \
                          "&passType=" + pass_type + "&scan=" + scan_nr
        image[0]["class"] = "images"
        for i in range(1, len(image)):
            image[i]["src"] = "https://" + hpc_ip + ":" + server_port + "/results/getplotmulti?name=" + image[i]["src"] + \
                              "&passType=" + pass_type + "&scan=" + scan_nr
            image[i]["class"] = "images"

    [s.extract() for s in soup('head')]

    with open(os.environ['dirrectory'] + experiment_name + "/" + "/results/" + pass_type + "/fringe/" +
              scan_nr + "/" + "plots.html", "w") as file:
        file.write(str(soup))


if __name__ == "__main__":
    experiment_name = sys.argv[1]
    scan_nr = sys.argv[2]
    pass_type = sys.argv[3]
    main(experiment_name, scan_nr, pass_type)
    sys.exit(0)
