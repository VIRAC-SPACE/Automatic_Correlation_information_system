import os
import sys
import operator
from bs4 import BeautifulSoup
from os.path import expanduser
import urllib.request

if __name__ == "__main__":
    experimentName = sys.argv[1]
    home = expanduser("~")
    os.system("cp "+home+"/KANA_resources/kanaplots.html ~/KANA_resources/kanaplotsTMP.html");
    indexHTML = urllib.request.urlopen('file:///'+home+'/KANA_resources/kanaplotsTMP.html').read()
    soup = BeautifulSoup(indexHTML,  "lxml")
    files = os.listdir(home+"/experiments/KANA/"+ experimentName + '/'+ sys.argv[2] + "/results/m1-spectra/png")
    plotFile = []
    plotFiles = []
    #sort all files first by station then by number
    for filename in files:
        split = filename.split("_")
        plotFile = [split[1], int(split[3][:-4]), filename]
        plotFiles.append(plotFile)
    #add sorted files to html
    plotFiles = sorted(plotFiles, key=operator.itemgetter(0,1))
    for plotFile in plotFiles:
        new_text = soup.new_tag("p")
        new_text.insert(0, plotFile[2])
        soup.body.append(new_text)
        new_img = soup.new_tag("img", src="results/getkanaplot?name=" + plotFile[2])
        #new_img["style"] = ("margin-left: 100px;")	
        soup.body.append(new_img)
	
    [s.extract() for s in soup('head')]
    [s.extract() for s in soup('script')]

    os.system("touch "+home+"/experiments/KANA/" + experimentName + "/" + sys.argv[2] + "/plots/plots.html")
    with open(home+"/experiments/KANA/" + experimentName +"/" + sys.argv[2] + "/" + "plots/" + "plots.html", "w") as file:
        file.write(str(soup))
    os.system("rm "+home+"/KANA_resources/kanaplotsTMP.html")
    sys.exit(0)

