from lmcut import main, Timer

problems = [
    "data/depot/pfile1.strips",
    "data/depot/pfile2.strips",
    "data/driverlog/pfile1.strips",
    "data/elevators/p01.strips",
    "data/hiking/ptesting-1-2-3.strips",
    "data/openstacks/p03.strips",
    "data/parcprinter/p03.strips",
    "data/test/pfile.strips",
    "data/transport/p01.strips",
    "data/visitall/p-05-5.strips",
    "data/woodworking/p01.strips",
    "data/sokoban/p04.strips",
]

timer = Timer("Run all test")

for problem in problems:
    print("\n" + "*" * 20 + " Running %s " % problem + "*" * 20)
    main(problem)
    timer.checkpoint("Finished %s" % problem)

timer.finish()
