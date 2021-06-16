from collections import defaultdict
from pandas import Series, DataFrame
import itertools as it
import pandas as pd
import math
import csv
import sys
import argparse
import collections
import glob
import os
import re
import requests
import string
import sys

class Armin():
    
    def apriori(self, input_filename, output_filename, min_support_percentage, min_confidence):
        """
        Implement the Apriori algorithm, and write the result to an output file

        PARAMS
        ------
        input_filename: String, the name of the input file
        output_filename: String, the name of the output file
        min_support_percentage: float, minimum support percentage for an itemset
        min_confidence: float, minimum confidence for an association rule to be significant
        """

        CFI = set(())
        transaction_list = []
        VFI = []
        support_list = []

        # Read in CSV, parsing into all of the possible CFI's (a, b, c, ab, ac, bc, etc)
        with open(input_filename, 'r') as csvfile:
            csv_reader = csv.reader(csvfile, delimiter='\n', quotechar='|')
            for row in csv_reader:
                r = ','.join(row).split(',')[1:]
                transaction_list.append(r)
                CFI = CFI.union(r)
        CFI = list(CFI)
        CFI.sort()

        # Determine different unique combinations of letters using combinations from itertools
        # Determine the support for the various CFI's from the previous step (support = (# of occurences of item) / (# of transactions))
        for i in range(len(CFI) + 1):
            combos = it.combinations(CFI, i + 1)

            for c in combos:
                c = set(c)
                count = 0

                for trans in transaction_list:
                    trans = set(trans)
                    if c.issubset(trans):
                        count += 1
                support = count / len(transaction_list)

                if support >= min_support_percentage:
                    c = list(c)
                    c.sort()
                    VFI.append(c)
                    support_list.append(support)
                elif len(c) == 1:
                    c = list(c)
                    CFI.remove(c[0])

        with open(output_filename, "w", newline="") as f:

            for i in range(len(VFI)):
                row = csv.writer(f)
                verif_item = VFI[i]
                verif_item.insert(0, 'S')
                verif_item.insert(1, '%.4f' % support_list[i])
                for elem in range(len(verif_item))[2:]:
                    verif_item[elem] = str(verif_item[elem])
                row.writerow(verif_item)

            ss = VFI.copy()
            ss = [x[2:] for x in ss]

            unions = VFI.copy()
            unions = {(str(x[2:])): x[1] for x in unions}

            for pair in it.combinations(ss, 2):
                pair = list(pair)
                a = set(pair[0])
                b = set(pair[1])
                u = a.union(b)
                u = list(u)
                u.sort()

                if str(u) in unions:
                    union_support_percent = float(unions[str(u)])
                    first = list(a)
                    first.sort()
                    second = list(b)
                    second.sort()

                    if len(a.intersection(b)) == 0:
                        row = csv.writer(f, quoting=csv.QUOTE_NONE, quotechar=None, escapechar='\\')
                        first_support_percent = float(unions[str(first)])
                        flipped_support_percent = float(unions[str(second)])

                        conf = union_support_percent / first_support_percent
                        flipped_conf = union_support_percent / flipped_support_percent

                        if conf >= min_support_percentage:
                            row.writerow(['R'] + [str('%.4f' % union_support_percent)] + [str('%.4f' % conf)] + first + ['\'=>\''] + second)

                        if flipped_conf >= min_support_percentage:
                            row.writerow(['R'] + [str('%.4f' % union_support_percent)] + [str('%.4f' % flipped_conf)] + second + ['\'=>\''] + first)

        pass

if __name__ == "__main__":
    armin = Armin()
    armin.apriori('input.csv', 'output.sup=0.5,conf=0.7.csv', 0.5, 0.7)
    armin.apriori('input.csv', 'output.sup=0.5,conf=0.8.csv', 0.5, 0.8)
    armin.apriori('input.csv', 'output.sup=0.6,conf=0.8.csv', 0.6, 0.8)