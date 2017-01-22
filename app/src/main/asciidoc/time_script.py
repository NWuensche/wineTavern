reading = False
skip = False
i = 0
section = 0
names = ["Severin", "Louis", "Niklas", "Katharina", "Michel"]
res = [0, 0, 0, 0, 0]

def timeDiff(first, second):
    first = first.split(':')
    second = second.split(':')
    return (int(second[0]) - int(first[0])) * 60 + int(second[1]) - int(first[1])

file = open("time_recording.adoc", 'r')

for line in file:
    i += 1
    if (i < 17): continue
    if (skip):
        skip = False
        continue

    if (line[:4] == "|==="):
        if (not reading):
            section += 1
            skip = True
        reading = not reading
        continue
    
    if (reading):
        line = line.replace(' ', '')
        args = line.split('|')
        diff = timeDiff(args[2], args[3])

        if (section == 1):
            for index in range(len(res)):
                res[index] += diff
        else:
            res[section - 2] += diff
        print "Zeile", str(i) + ":", diff, "min"

print "--------------------------------------"
for index in range(len(names)):
    print names[index] + ": " + str(round(res[index] / 60.0, 1)) + "h (" + str(res[index]) + "min)"
print "--------------------------------------"
file.close()
