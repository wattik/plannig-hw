import sys
from abc import ABC
from pprint import pprint

from problem import *


class A(ABC):
    def __init__(self, pepa) -> None:
        self.pepa = pepa
        print("A")


class B(A):
    def __init__(self, pepa) -> None:
        super().__init__(pepa)
        print("B")


def main():
    a = A(1)
    b = B(1)

if __name__ == '__main__':
    main()
