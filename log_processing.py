TS = []
TJ = []
with open("log.txt") as f:
    for line in f:
        TS.append( int(line.rstrip().split(";")[0]) )
        TJ.append( int(line.rstrip().split(";")[1]) )
average_TS = round( (sum(TS)/len(TS)) / 1000000, 2 )
average_TJ = round( (sum(TJ)/len(TJ)) / 1000000, 2 )
print(average_TS)
print(average_TJ)
