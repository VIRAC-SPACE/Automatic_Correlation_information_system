#! /usr/bin/python

import sys
import os
import collections
import itertools
import re

old_node_re = re.compile("^node-[1-30]")
def is_old_node(node_name):
    return bool(old_node_re.match(node_name))

def generate_ranks_file(basename, run_target, nodes, max_correlator_processes_old=sys.maxint, max_correlator_processes_new=sys.maxint, evlbi_nodes=None, input_on_sfxc=True, evlbi=False):
    print (max_correlator_processes_old)
    print (max_correlator_processes_new)
    f = open(basename + ".ranks", 'w')
    
    print >>f, "rank 0={t} slot=0".format(t = run_target)
    print >>f, "rank 1={t} slot=1".format(t = run_target)
    print >>f, "rank 2={t} slot=2,3".format(t = run_target)
    
    rank = 2
    stations = ["Tr", "Ir", "Wb", "wx", "qw", "kk", "ll"]
    node_slot = collections.defaultdict(int)
    
    if evlbi:
        nodes = nodes[:] # make a copy of the node, so that we can delete evlbi nodes used for data reception
        for station in stations:
            station_node = evlbi_nodes[station]
            node_id =  station_node.node.node_id
            rank += 1
            
            jive5ab_cores = bin(station_node.mask).count("1")
            rank_line = "rank %d=%s slot=%d,%d,%d" % (rank, node_id, node_slot[node_id], node_slot[node_id] + 1, node_slot[node_id] + 2)
            if jive5ab_cores > 1:
                # more than one core for jive5ab means we can only host one receive process per node, so we can use 4 cores for SFXC
                rank_line += (",%d" % (node_slot[node_id] + 3))
            print >>f, rank_line
            node_slot[node_id] += 4
            nodes = filter(lambda x: x.node_id != node_id, nodes)
            
    else:
        file_machine_slot = collections.defaultdict(int) # { identifier : next unused slot index }
        if input_on_sfxc:
            # do input on sfxc, on capable nodes, 2 slots per input node
            input_nodes = nodes# bit hackish: we can do 10Gbps to evlbi_nodes and the k-nodes, so they qualify as input_nodes
            input_slots_list = [(node, [slot, slot  + 1]) for node, slot in itertools.product(input_nodes, [0, 2])]
            input_slots_index = 0
                                    
        for station in stations:
            rank += 1
            input_type = "file"
            
            if input_type == "vsn":
                if input_on_sfxc:
                    if input_slots_index >= len(input_slots_list):
                        raise RuntimeError("Not enough SFXC nodes to run input nodes on cluster")
                    node, slots = input_slots_list[input_slots_index]
                    input_slots_index += 1
                    print >>f, "rank %d=%s slot=%s" % (rank, "node28", ",".join(map(str,slots)))
                    node_slot[0] = max(node_slot[0], max(slots) + 1) # mark the slots used
                else:
                    print >>f, "rank %d=%s slot=0,1,2" % (rank, "master2")
            elif input_type in ["flexbuff", "file"]:
                
                print >>f, "rank {r}={ip} slot={s1},{s2}".format(r = rank, ip = "localhost", s1 = 1 % 16, s2 = (1+1) % 16)
            else:
                raise RuntimeError("Unknown input type {t} of data source {d} for station {s}".format(t = input_type, d = 1, s = station))
            
    for slot in xrange(16):
        for node in nodes:
            index = nodes.index(node)
            max_correlator_processes = max_correlator_processes_old if is_old_node(node) else max_correlator_processes_new
            # skip slot if it is already used for evlbi or the slot numer is not actually available on this machine
            if slot < node_slot[index] or slot >= min(16, max_correlator_processes):
                continue
            rank = rank + 1
            print >>f, "rank %d=%s slot=%d" % (rank, node, slot)        
    f.close()
    return (os.path.split(f.name)[1], rank) # lose the path



generate_ranks_file("Ir-Tr", "localhost", ["localhost", "localhost"])



