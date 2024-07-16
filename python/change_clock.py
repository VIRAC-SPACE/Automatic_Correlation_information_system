#! /usr/bin/python

import sys


def change_clock_line(line, r, o):
    l = line.split(":")
    l[1] = r + " usec"
    l[3] = o + ";\n"
    
    l = " : ".join(l)
    return l


def main():
    vex_file = sys.argv[1]
    input_ = sys.argv[2].split(",")
    print("\n")
    print(input_)
    print(sys.argv[2])
    vex_lines = list()

    with open(vex_file, 'r') as contents:
        lines = contents.readlines()
        for line in lines:
            vex_lines.append(line)
    i = 0
    j = 1        
    
    while len(input_) > 0:
        if len(input_) == 0:
            break
        if j > len(input_) - 1:
            break
    
        for l in range(0, len(vex_lines)):
            if j > len(input_) - 1:
                break
            if "clock_early" in vex_lines[l]:
                vex_lines[l] = change_clock_line(vex_lines[l], input_[i], input_[j])
                i = i + 2
                j = j + 2
            
    vex_file = open(vex_file, 'w')
    
    for v in vex_lines:
        vex_file.write(v)


if __name__ == "__main__":
    main()
    sys.exit(0)

