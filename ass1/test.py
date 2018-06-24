from __future__ import absolute_import
from lmcut import main, Timer

problems = [
    u"data/depot/pfile1.strips",
    u"data/depot/pfile2.strips",
    u"data/driverlog/pfile1.strips",
    u"data/elevators/p01.strips",
    u"data/hiking/ptesting-1-2-3.strips",
    u"data/openstacks/p03.strips",
    u"data/parcprinter/p03.strips",
    u"data/test/pfile.strips",
    u"data/transport/p01.strips",
    u"data/visitall/p-05-5.strips",
    u"data/woodworking/p01.strips",
    u"data/sokoban/p04.strips",
]

timer = Timer(u"Run all test")

for problem in problems:
    print u"\n" + u"*" * 20 + u" Running %s " % problem + u"*" * 20
    main(problem)
    timer.checkpoint(u"Finished %s" % problem)

timer.finish()
